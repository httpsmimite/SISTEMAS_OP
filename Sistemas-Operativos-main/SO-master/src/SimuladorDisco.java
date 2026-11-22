import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimuladorDisco {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Posicion inicial del cabezal (0-199, -1 = datos de la clinica): ");
        int inicio = sc.nextInt();
        sc.nextLine();

        int[] solicitudes;
        if (inicio == -1) {
            inicio = 53;
            solicitudes = new int[]{98, 183, 37, 122, 14, 124, 65, 67};
            System.out.println("Usando escenario Clinica San Jose -> Cabezal inicial: 53");
            System.out.println("Cola de cilindros: 98 183 37 122 14 124 65 67");
        } else {
            System.out.print("Ingrese solicitudes de cilindros separadas por espacios: ");
            String[] partes = sc.nextLine().trim().split("\\s+");
            solicitudes = new int[partes.length];
            for (int i = 0; i < partes.length; i++) {
                solicitudes[i] = Integer.parseInt(partes[i]);
            }
        }
        ejecutarFCFS(inicio, solicitudes);
        ejecutarSSTF(inicio, solicitudes);
        sc.close();
    }

    private static void ejecutarFCFS(int inicio, int[] req) {
        System.out.println("\n== FCFS (First-Come, First-Served) ==");
        int actual = inicio;
        int desplazamientoTotal = 0;
        StringBuilder orden = new StringBuilder();
        orden.append(actual);

        for (int cilindro : req) {
            int movimiento = Math.abs(cilindro - actual);
            desplazamientoTotal += movimiento;
            actual = cilindro;
            orden.append(" -> ").append(actual);
        }

        System.out.println("Orden de atencion: " + orden);
        System.out.println("Desplazamiento total del cabezal: " + desplazamientoTotal + " cilindros");
        System.out.printf("Desplazamiento promedio: %.2f cilindros%n", (double) desplazamientoTotal / req.length);
    }

    private static void ejecutarSSTF(int inicio, int[] req) {
        System.out.println("\n== SSTF (Shortest Seek Time First) ==");
        int actual = inicio;
        int desplazamientoTotal = 0;
        boolean[] atendido = new boolean[req.length];
        List<Integer> secuencia = new ArrayList<>();
        secuencia.add(actual);

        for (int i = 0; i < req.length; i++) {
            int idxMasCercano = -1;
            int minDistancia = Integer.MAX_VALUE;

            for (int j = 0; j < req.length; j++) {
                if (!atendido[j]) {
                    int dist = Math.abs(req[j] - actual);
                    if (dist < minDistancia) {
                        minDistancia = dist;
                        idxMasCercano = j;
                    }
                }
            }

            atendido[idxMasCercano] = true;
            desplazamientoTotal += minDistancia;
            actual = req[idxMasCercano];
            secuencia.add(actual);
        }

        StringBuilder orden = new StringBuilder();
        for (int i = 0; i < secuencia.size(); i++) {
            orden.append(secuencia.get(i));
            if (i < secuencia.size() - 1) {
                orden.append(" -> ");
            }
        }

        System.out.println("Orden de atencion: " + orden);
        System.out.println("Desplazamiento total del cabezal: " + desplazamientoTotal + " cilindros");
        System.out.printf("Desplazamiento promedio: %.2f cilindros%n", (double) desplazamientoTotal / req.length);

    }

}