#!/bin/bash
# Capitulo 14 - Verificacion de un respaldo sin tocar los datos originales
# Uso: sudo bash verificar_backup.sh [archivo.tar.gz]
# Sin argumento usa el respaldo mas reciente. Extrae en un directorio temporal.
set -euo pipefail

DESTINO="${DESTINO:-/srv/clinica/respaldos}"
ARCHIVO="${1:-$(ls -t "$DESTINO"/backup_clinica_*.tar.gz 2>/dev/null | head -n 1 || true)}"
PRUEBA="srv/clinica/laboratorio/resultados/hemograma_P-1001.txt"

if [ -z "$ARCHIVO" ] || [ ! -f "$ARCHIVO" ]; then
  echo "ERROR: no se encontro ningun respaldo en $DESTINO" >&2
  exit 1
fi

echo "Respaldo a verificar: $ARCHIVO"

echo "[1] Integridad (SHA-256)"
if [ -f "$ARCHIVO.sha256" ]; then
  ( cd "$(dirname "$ARCHIVO")" && sha256sum -c "$(basename "$ARCHIVO").sha256" )
else
  echo "AVISO: no existe $ARCHIVO.sha256; se omite la comparacion de suma."
fi

echo "[2] Lectura del contenido del archivo comprimido"
tar -tzf "$ARCHIVO" > /dev/null && echo "El archivo se lee sin errores."

TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

echo "[3] Restauracion de prueba en un directorio temporal ($TMP)"
tar -xzpf "$ARCHIVO" -C "$TMP"
ls -l "$TMP/$PRUEBA"
cat "$TMP/$PRUEBA"

echo "[4] Comparacion con el archivo original"
if [ -f "/$PRUEBA" ]; then
  if diff -q "$TMP/$PRUEBA" "/$PRUEBA" >/dev/null; then
    echo "OK: el archivo restaurado es identico al original."
  else
    echo "AVISO: el archivo restaurado difiere del original (puede haber cambiado despues del respaldo)."
  fi
else
  echo "El original no existe en /$PRUEBA: el contenido restaurado es la unica copia."
fi

echo "RESULTADO: verificacion terminada. Los datos originales no fueron modificados."