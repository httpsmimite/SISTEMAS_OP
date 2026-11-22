#!/bin/bash
# Capitulo 14 - Respaldo de la Clinica San Jose de la Salud
# Uso: sudo bash backup_clinica.sh
# Empaqueta /srv/clinica (sin la carpeta de respaldos) en el disco adicional
# y genera su suma SHA-256 para comprobar la integridad mas adelante.
set -euo pipefail

DESTINO="${DESTINO:-/srv/clinica/respaldos}"
MONTAJE_OBLIGATORIO="${MONTAJE_OBLIGATORIO:-1}"   # 0 solo para pruebas fuera de la VM

if [ "$MONTAJE_OBLIGATORIO" = "1" ] && ! mountpoint -q "$DESTINO"; then
  echo "ERROR: el volumen de respaldos no esta montado en $DESTINO" >&2
  echo "Monte el disco adicional (mount /dev/sdb $DESTINO) y vuelva a ejecutar." >&2
  exit 1
fi

FECHA=$(date +%Y-%m-%d_%H%M)
NOMBRE="backup_clinica_${FECHA}.tar.gz"
ARCHIVO="$DESTINO/$NOMBRE"

# --exclude va antes de los objetos a empaquetar; -p conserva permisos
tar --exclude=srv/clinica/respaldos -czpf "$ARCHIVO" -C / srv/clinica

( cd "$DESTINO" && sha256sum "$NOMBRE" > "$NOMBRE.sha256" )

# El respaldo contiene datos de TODAS las areas: solo el propietario (carlos.admin)
# puede leerlo; el grupo g_admin solo puede entrar a la carpeta y ver la suma.
if getent passwd carlos.admin >/dev/null && getent group g_admin >/dev/null; then
  chown carlos.admin:g_admin "$ARCHIVO" "$ARCHIVO.sha256"
fi
chmod 600 "$ARCHIVO"
chmod 640 "$ARCHIVO.sha256"

echo "Respaldo creado: $ARCHIVO"
ls -lh "$ARCHIVO" "$ARCHIVO.sha256"
echo "Suma SHA-256:"; cat "$ARCHIVO.sha256"