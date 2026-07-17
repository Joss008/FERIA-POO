package org.example;

/*
Clase que hereda atributos de la clase padre Alumno.
Se le agrega las variables faltas, horarioAprobado y diasSolicitados.
diasSolicitados: días de la semana (LUNES-VIERNES) en que el alumno solicita el comedor,
                 almacenados separados por coma (ej: "LUNES,MIERCOLES,VIERNES")
 */
public class AlumnoCalificado extends Alumno {
    private int faltas;
    private boolean horarioAprobado;
    private String diasSolicitados; // Días en que el alumno asiste al comedor

/*
Constructor de esta clase
 */
    public AlumnoCalificado(String nombre, String apellido, String ciclo,
                            long codigo, int edad, int faltas,
                            boolean horarioAprobado, String diasSolicitados) {
        super(nombre, apellido, ciclo, codigo, edad);
        this.faltas = faltas;
        this.horarioAprobado = horarioAprobado;
        this.diasSolicitados = diasSolicitados;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }

    public boolean isHorarioAprobado() {
        return horarioAprobado;
    }

    public void setHorarioAprobado(boolean horarioAprobado) {
        this.horarioAprobado = horarioAprobado;
    }

    public String getDiasSolicitados() {
        return diasSolicitados;
    }

    public void setDiasSolicitados(String diasSolicitados) {
        this.diasSolicitados = diasSolicitados;
    }

    @Override
    public String toString() {
        return "\nNombre Completo: " + getNombre() + " " + getApellido()
                + " | Ciclo: " + getCiclo()
                + " | Codigo: " + getCodigo()
                + " | Edad: " + getEdad()
                + " | Número de faltas: " + getFaltas()
                + " | Días solicitados: " + (diasSolicitados != null ? diasSolicitados : "No registrado")
                + " | ";
    }
}
