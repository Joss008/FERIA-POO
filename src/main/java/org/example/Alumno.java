package org.example;

/*
Clase que servira como clase padre, que simula un alumno de la universidad
con 5 atributos caracteristicos de los alumnos (NOMBRE, APELLIDO, CARRERA, CODIGO y EDAD)
 */
public class Alumno {
    private String nombre;
    private String apellido;
    private String carrera;
    private long codigo;
    private int edad;

/*
Constructor de la clase padre Alumno
 */
    public Alumno(String nombre, String apellido, String carrera, long codigo, int edad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.carrera = carrera;
        this.codigo = codigo;
        this.edad = edad;
    }

/*
Setters y Getters de los atributos encapsulados
 */
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

/*
Modificamos con @Override para ver un mensaje más amigable y entendible
 */
    @Override
    public String toString() {
        return "\nNombre: " + nombre + " " + apellido + " | Carrera: " + carrera + " | Codigo: " + codigo + " | Edad: " + edad + " | ";
    }
}
