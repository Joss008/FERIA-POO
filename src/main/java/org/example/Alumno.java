package org.example;

/*
Clase que servira como clase padre, que simula un alumno de la universidad
con 5 atributos caracteristicos de los alumnos (NOMBRE, APELLIDO, CICLO, CODIGO y EDAD)
 */
public class Alumno {
    private String nombre;
    private String apellido;
    private String ciclo;   // Cambiado de 'carrera' a 'ciclo' (números romanos I al X)
    private long codigo;
    private int edad;

/*
Constructor de la clase padre Alumno
 */
    public Alumno(String nombre, String apellido, String ciclo, long codigo, int edad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.ciclo = ciclo;
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

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
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
        return "\nNombre: " + nombre + " " + apellido + " | Ciclo: " + ciclo + " | Codigo: " + codigo + " | Edad: " + edad + " | ";
    }
}
