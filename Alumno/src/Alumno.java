public class Alumno {
    private String nombre;
    private String carrera;
    private long codigo;
    private int edad;
    private int faltas;

    public Alumno(String nombre, String carrera, long codigo,int edad) {
        this.nombre = nombre;
        this.carrera = carrera;
        this.codigo = codigo;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return "\nNombre: " + nombre + " | Carrera: " + carrera + " | Codigo: " + codigo + " | Edad: " + edad + " | ";
    }
}