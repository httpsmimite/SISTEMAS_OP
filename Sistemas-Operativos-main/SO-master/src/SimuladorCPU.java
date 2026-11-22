import java.util.*;

public class SimuladorCPU {
    static class P {
        int pid, ll, rf, pr, rem, ini = -1, fin;
        String nom;
        P(int pid, String nom, int ll, int rf, int pr) { this.pid = pid; this.nom = nom; this.ll = ll; this.rf = rf; this.pr = pr; rem = rf; }
        int ret() { return fin - ll; }
        int esp() { return ret() - rf; }
    }

    static List<P> copia(List<P> l) {
        List<P> c = new ArrayList<>();
        for (P p : l) c.add(new P(p.pid, p.nom, p.ll, p.rf, p.pr));
        return c;
    }

    static double[] tabla(String t, List<P> l, StringBuilder g) {
        System.out.println("\n== " + t + " ==\nGantt: " + g);
        System.out.printf("%-4s %-26s %-7s %-7s %-7s %-8s %-8s%n", "PID", "Proceso", "Llegada", "Rafaga", "Fin", "Espera", "Retorno");
        double se = 0, sr = 0;
        for (P p : l) {
            System.out.printf("%-4d %-26s %-7d %-7d %-7d %-8d %-8d%n", p.pid, p.nom, p.ll, p.rf, p.fin, p.esp(), p.ret());
            se += p.esp(); sr += p.ret();
        }
        double[] r = {se / l.size(), sr / l.size()};
        System.out.printf("Promedio espera = %.2f | Promedio retorno = %.2f%n", r[0], r[1]);
        return r;
    }

    static double[] fcfs(List<P> in) {
        List<P> l = copia(in);
        l.sort(Comparator.comparingInt((P p) -> p.ll).thenComparingInt(p -> p.pid));
        StringBuilder g = new StringBuilder();
        int t = 0;
        for (P p : l) {
            if (t < p.ll) { g.append("[").append(t).append("-").append(p.ll).append(" ocioso]"); t = p.ll; }
            g.append("[").append(t).append(" P").append(p.pid).append(" ").append(t + p.rf).append("]");
            t += p.rf; p.fin = t;
        }
        return tabla("FCFS", l, g);
    }

    static double[] rr(List<P> in, int q) {
        List<P> l = copia(in);
        l.sort(Comparator.comparingInt((P p) -> p.ll).thenComparingInt(p -> p.pid));
        Deque<P> cola = new ArrayDeque<>();
        StringBuilder g = new StringBuilder();
        int t = 0, i = 0, hechos = 0;
        while (hechos < l.size()) {
            while (i < l.size() && l.get(i).ll <= t) cola.add(l.get(i++));
            if (cola.isEmpty()) { t = l.get(i).ll; continue; }
            P p = cola.poll();
            int u = Math.min(q, p.rem);
            g.append("[").append(t).append(" P").append(p.pid).append(" ").append(t + u).append("]");
            t += u; p.rem -= u;
            while (i < l.size() && l.get(i).ll <= t) cola.add(l.get(i++));
            if (p.rem > 0) cola.add(p); else { p.fin = t; hechos++; }
        }
        return tabla("Round Robin (quantum = " + q + ")", l, g);
    }

    public static void main(String[] a) {
        Scanner s = new Scanner(System.in);
        List<P> ps = new ArrayList<>();
        System.out.print("Cantidad de procesos (minimo 5, 0 = datos de la clinica): ");
        int n = s.nextInt();
        if (n == 0) {
            ps.add(new P(1, "BD historias clinicas", 0, 8, 1));
            ps.add(new P(2, "Sync reportes caja/farmacia", 1, 4, 3));
            ps.add(new P(3, "Monitoreo memoria/procesos", 2, 9, 2));
            ps.add(new P(4, "Respaldo de archivos", 3, 5, 4));
            ps.add(new P(5, "Emision resultados lab", 4, 2, 1));
        } else {
            for (int k = 1; k <= n; k++) {
                System.out.print("P" + k + " -> nombre llegada rafaga prioridad: ");
                ps.add(new P(k, s.next(), s.nextInt(), s.nextInt(), s.nextInt()));
            }
        }
        System.out.print("Quantum Round Robin: ");
        int q = s.nextInt();
        double[] f = fcfs(ps), r = rr(ps, q);
        System.out.printf("%n== Comparacion ==%nFCFS: espera %.2f, retorno %.2f%nRR(q=%d): espera %.2f, retorno %.2f%n", f[0], f[1], q, r[0], r[1]);
        System.out.println("Menor espera promedio: " + (f[0] <= r[0] ? "FCFS" : "Round Robin"));
    }
}
