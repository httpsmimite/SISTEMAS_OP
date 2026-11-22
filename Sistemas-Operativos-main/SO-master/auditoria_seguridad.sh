#!/bin/bash
# Capitulo 15 - Auditoria final de seguridad de la plataforma
# Uso: sudo bash auditoria_seguridad.sh
# Solo consulta: no modifica cuentas, permisos ni archivos.
R=/srv/clinica
USUARIOS="carlos.admin sofia.ti dra.mendoza qf.gomez ana.caja luis.admision"

echo "=============================================================="
echo " AUDITORIA DE SEGURIDAD - CLINICA SAN JOSE DE LA SALUD"
echo " Host: $(hostname) | Fecha: $(date '+%Y-%m-%d %H:%M')"
echo "=============================================================="

echo; echo "[1] GRUPOS DE TRABAJO Y MIEMBROS"
getent group g_admin g_asistencial g_operativo

echo; echo "[2] PERTENENCIA DE CADA USUARIO"
for u in $USUARIOS; do id "$u"; done

echo; echo "[3] CUENTAS CON PRIVILEGIOS SUDO"
getent group sudo

echo; echo "[4] ESTADO DE LAS CONTRASENAS (L = bloqueada, P = con contrasena)"
for u in $USUARIOS; do passwd -S "$u"; done

echo; echo "[5] PROPIETARIOS Y PERMISOS DE LA ESTRUCTURA"
ls -ld "$R" "$R"/*

echo; echo "[6] ELEMENTOS ACCESIBLES PARA 'OTROS' DENTRO DE LAS AREAS (esperado: ninguno)"
OTROS=$(find "$R" -mindepth 1 -perm /o+rwx 2>/dev/null)
if [ -z "$OTROS" ]; then echo "Ninguno: ningun archivo ni carpeta concede permisos a otros."; else echo "$OTROS"; fi

echo; echo "[7] ACCESO REMOTO (SSH)"
grep -hE '^[[:space:]]*(PasswordAuthentication|PermitRootLogin)' /etc/ssh/sshd_config /etc/ssh/sshd_config.d/*.conf 2>/dev/null \
  || echo "Sin directivas explicitas: se aplican los valores por defecto de OpenSSH."

echo; echo "[8] VOLUMEN DE RESPALDOS"
if mountpoint -q "$R/respaldos"; then df -h "$R/respaldos"; else echo "AVISO: el volumen de respaldos NO esta montado."; fi
ls -lh "$R/respaldos" 2>/dev/null

echo "=============================================================="