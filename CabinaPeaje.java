import java.util.Random;

public class CabinaPeaje implements Runnable {

    // Variable global compartida (intencionalmente SIN sincronizacion)
    public static int totalRecaudadoGlobal = 0;

    private String nombre;
    private int vehiculosAtendidos;
    private int vehiculosAsignados;
    private Random random = new Random();

    public CabinaPeaje(String nombre, int vehiculosAsignados) {
        this.nombre = nombre;
        this.vehiculosAsignados = vehiculosAsignados;
        this.vehiculosAtendidos = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVehiculosAtendidos() {
        return vehiculosAtendidos;
    }

    @Override
    public void run() {
        for (int i = 1; i <= vehiculosAsignados; i++) {
            try {
                // Simula el tiempo de cobro: entre 500 y 1500 ms
                int tiempo = 500 + random.nextInt(1001);
                Thread.sleep(tiempo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            vehiculosAtendidos++;
            totalRecaudadoGlobal += 50; // Seccion critica expuesta (condicion de carrera)

            System.out.println(nombre + " atendio el vehiculo #" + vehiculosAtendidos
                    + " | Total global: " + totalRecaudadoGlobal);
        }
    }

    public static void main(String[] args) {
        final int TOTAL_VEHICULOS = 30;
        final int NUM_CABINAS = 3;

        CabinaPeaje[] cabinas = new CabinaPeaje[NUM_CABINAS];
        Thread[] hilos = new Thread[NUM_CABINAS];

        // Distribuir los 30 vehiculos entre las 3 cabinas (10 cada una)
        for (int i = 0; i < NUM_CABINAS; i++) {
            String nombre = (i == 0) ? "Cabina Flores" : "Cabina " + (i + 1);
            int asignados = TOTAL_VEHICULOS / NUM_CABINAS
                    + (i < TOTAL_VEHICULOS % NUM_CABINAS ? 1 : 0);
            cabinas[i] = new CabinaPeaje(nombre, asignados);
            hilos[i] = new Thread(cabinas[i], nombre);
        }

        System.out.println("=== Abriendo estacion de peaje ===");
        for (Thread hilo : hilos) {
            hilo.start();
        }

        // Esperar a que todas las cabinas terminen
        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println();
        System.out.println("========== REPORTE FINAL ==========");
        int sumaVehiculos = 0;
        for (CabinaPeaje cabina : cabinas) {
            System.out.println(cabina.getNombre() + ": " + cabina.getVehiculosAtendidos() + " vehiculos");
            sumaVehiculos += cabina.getVehiculosAtendidos();
        }
        System.out.println("Total de vehiculos atendidos: " + sumaVehiculos);
        System.out.println("Recaudacion esperada: $" + (sumaVehiculos * 50));
        System.out.println("totalRecaudadoGlobal: $" + totalRecaudadoGlobal);
        System.out.println("===================================");
    }
}
