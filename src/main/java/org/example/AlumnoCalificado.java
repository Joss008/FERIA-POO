package org.example;

/*
Clase que hereda atributos de la clase padre Alumno
se le agrega las variables faltas y horarioAprobado
 */
public class AlumnoCalificado extends Alumno {
    private int faltas;
    private boolean horarioAprobado;

/*
Constructor de esta clase
 */
    public AlumnoCalificado(String nombre, String apellido, String carrera, long codigo, int edad, int faltas, boolean horarioAprobado) {
        super(nombre, apellido, carrera, codigo, edad);
        this.faltas = faltas;
        this.horarioAprobado = horarioAprobado;
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

    @Override
    public String toString() {
        return "\nNombre Completo: " + getNombre() + " " + getApellido() + " | Carrera: " + getCarrera() + " | Codigo: " + getCodigo() + " | Edad: " + getEdad() + " | " + "Número de faltas: " + getFaltas() + " | ";
    }
}
