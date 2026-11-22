import java.util.*;

public class SimuladorPaginas {
    static int simular(String alg, int[] ref, int m) {
        List<Integer> marcos = new ArrayList<>(); // FIFO: orden de llegada; LRU: menos reciente al inicio
        int fallos = 0;
        System.out.println("\n== " + alg + " (marcos = " + m + ") ==");
        System.out.printf("%-4s %-8s %-" + (m * 3 + 2) + "s %s%n", "Paso", "Ref", "Marcos", "Resultado");
        for (int i = 0; i < ref.length; i++) {
            int p = ref[i];
            boolean hit = marcos.contains(p);
            if (hit) {
                if (alg.equals("LRU")) { marcos.remove((Integer) p); marcos.add(p); }
            } else {
                fallos++;
                if (marcos.size() == m) marcos.remove(0);
                marcos.add(p);
            }
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < m; k++) sb.append(k < marcos.size() ? String.format("%-3d", marcos.get(k)) : "-  ");
            System.out.printf("%-4d %-8d %-" + (m * 3 + 2) + "s %s%n", i + 1, p, sb, hit ? "acierto" : "FALLO");
        }
        System.out.printf("Total de fallos %s = %d de %d referencias (%.1f%%)%n", alg, fallos, ref.length, 100.0 * fallos / ref.length);
        return fallos;
    }

    public static void main(String[] a) {
        Scanner s = new Scanner(System.in);
        System.out.print("Cadena de referencias (separada por espacios): ");
        String[] t = s.nextLine().trim().split("\\s+");
        int[] ref = new int[t.length];
        for (int i = 0; i < t.length; i++) ref[i] = Integer.parseInt(t[i]);
        System.out.print("Numero de marcos: ");
        int m = s.nextInt();
        int f = simular("FIFO", ref, m), l = simular("LRU", ref, m);
        System.out.printf("%n== Comparacion ==%nFIFO: %d fallos | LRU: %d fallos | Mejor: %s%n", f, l, f == l ? "empate" : f < l ? "FIFO" : "LRU");
    }
}
