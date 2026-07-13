package org.example;

import javafx.application.Platform;

/*
Mantiene sincronizada la lista de alumnos consultando la base de datos en intervalos.
Útil cuando varios equipos registran faltas o alumnos al mismo tiempo.
 */
public class HiloSincronizacion extends HiloComedor {

    private final Runnable tareaSincronizacion;
    private final int intervaloSegundos;

    public HiloSincronizacion(int intervaloSegundos, Runnable tareaSincronizacion) {
        super("HiloSincronizacionComedor");
        this.intervaloSegundos = intervaloSegundos;
        this.tareaSincronizacion = tareaSincronizacion;
    }

    @Override
    public void run() {
        while (isActivo() && !isInterrupted()) {
            try {
                Thread.sleep(intervaloSegundos * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (isActivo() && !isInterrupted()) {
                Platform.runLater(tareaSincronizacion);
            }
        }
    }
}
