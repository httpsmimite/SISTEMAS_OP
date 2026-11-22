#!/bin/bash
# Capitulo 11 - Clinica San Jose de la Salud. Ejecutar con: sudo bash preparar_clinica.sh
set -e
R=/srv/clinica

for g in g_admin g_asistencial g_operativo; do groupadd -f $g; done

mk() { id "$1" &>/dev/null || useradd -m -s /bin/bash -G "$2" "$1"; }
mk carlos.admin g_admin,sudo
mk sofia.ti g_admin
mk dra.mendoza g_asistencial
mk qf.gomez g_asistencial
mk ana.caja g_operativo
mk luis.admision g_operativo
# Asignar contrasenas luego: for u in carlos.admin sofia.ti dra.mendoza qf.gomez ana.caja luis.admision; do passwd $u; done

mkdir -p $R/{admision_caja/{citas,pagos},ti/{configuracion,bitacoras},farmacia/{inventario,entregas},laboratorio/{muestras,resultados}}

printf 'codigo_cita,codigo_paciente,fecha,hora,especialidad\nC-0001,P-1001,2026-10-05,09:00,Medicina General\nC-0002,P-1002,2026-10-05,09:30,Pediatria\n' > $R/admision_caja/citas/citas_2026-10.csv
printf 'comprobante,monto,medio_pago\nB-0001,80.00,efectivo\nB-0002,120.00,tarjeta\n' > $R/admision_caja/pagos/pagos_diario.csv
printf 'Servidor: srv-clinica-sanjose\nSO: Ubuntu Server LTS\n' > $R/ti/configuracion/inventario_servidor.txt
printf '#!/bin/bash\nvmstat 1 3\nps -eo pid,stat,ni,%%cpu,%%mem,comm --sort=-%%cpu | head -n 6\n' > $R/ti/configuracion/monitoreo.sh
printf 'codigo,medicamento,lote,stock\nM-001,Paracetamol 500mg,L2301,120\nM-002,Amoxicilina 500mg,L2305,60\n' > $R/farmacia/inventario/stock_medicamentos.csv
printf 'entrega,codigo_paciente,codigo_medicamento,cantidad\nE-0001,P-1001,M-001,2\n' > $R/farmacia/entregas/entregas_2026-10.csv
printf 'codigo_muestra,codigo_paciente,tipo\nS-0001,P-1001,Sangre\n' > $R/laboratorio/muestras/muestras_2026-10.csv
printf 'Paciente: P-1001\nExamen: Hemograma\nHemoglobina: 14.2 g/dL\n' > $R/laboratorio/resultados/hemograma_P-1001.txt

chown -R carlos.admin:g_operativo $R/admision_caja
chown -R carlos.admin:g_asistencial $R/farmacia $R/laboratorio
chown -R carlos.admin:g_admin $R/ti

chmod 751 $R
find $R -mindepth 1 -type d -exec chmod 2770 {} +
find $R -type f -exec chmod 660 {} +
chmod 640 $R/ti/configuracion/inventario_servidor.txt
chmod 750 $R/ti/configuracion/monitoreo.sh

echo "Estructura lista:"; ls -lR $R
