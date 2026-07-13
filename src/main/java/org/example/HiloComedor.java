package org.example;

/*
Clase base para hilos del sistema. Centraliza el ciclo de vida del hilo
y permite detener tareas en segundo plano al cerrar la aplicación.
 */
public abstract class HiloComedor extends Thread {

    private volatile boolean activo = true;

    public HiloComedor(String nombreHilo) {
        super(nombreHilo);
        setDaemon(true);
    }

    public boolean isActivo() {
        return activo;
    }

    public void detener() {
        activo = false;
        interrupt();
    }

    @Override
    public abstract void run();
}
