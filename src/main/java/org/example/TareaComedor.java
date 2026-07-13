package org.example;

import javafx.application.Platform;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/*
Ejecuta una operación del comedor en un hilo secundario.
Se usa para consultas y escrituras a la base de datos sin congelar la interfaz.
 */
public class TareaComedor<T> extends HiloComedor {

    private final Callable<T> trabajo;
    private final Consumer<T> alCompletar;
    private final Consumer<Exception> alFallar;

    public TareaComedor(String nombreHilo, Callable<T> trabajo, Consumer<T> alCompletar, Consumer<Exception> alFallar) {
        super(nombreHilo);
        this.trabajo = trabajo;
        this.alCompletar = alCompletar;
        this.alFallar = alFallar;
    }

    @Override
    public void run() {
        try {
            T resultado = trabajo.call();
            Platform.runLater(() -> alCompletar.accept(resultado));
        } catch (Exception e) {
            Platform.runLater(() -> alFallar.accept(e));
        }
    }
}
