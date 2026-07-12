package org.example;
import java.util.Scanner;

/*
Clase principal del programa.
 */
public class Main {
    static void main() {
        Scanner teclado = new Scanner(System.in);

        /*
        Indexamos un objeto
         */

        GestorAlumno admin1 = new GestorAlumno();
        //admin1.agregarAlumno(new Alumno("Jeff","Ing Sistemas",2511130064L,true,18,1));
        //admin1.agregarAlumno(new Alumno("Josue","Ing Sistemas",2511130062L,false,19,0));
        //admin1.agregarAlumno(new Alumno("Martin","Ing Sistemas",2511130065L,true,16,2));

        /*
        Variable que usaremos para la toma de decisiones en el switch
         */
        int opcion;

        /*
        Usamos el bucle do while para que el programa siga funcionando hasta que el usuario decida salir del programa
         */
        do {
            System.out.println("""
                    =====================================================
                    | Bienvenido al Sistema Control del Comedor EPIS |
                    =====================================================
                    1.- Lista de alumnos calificados
                    2.- Buscar alumno por código
                    3.- Agregar a un alumno a la lista
                    4.- Poner una falta al alumno
                    5.- Salir""");

            opcion = teclado.nextInt();
            teclado.nextLine();
            /**
             * Switch que se usara con 5 acciones incluído finaliza programa.ñ
             */
            switch (opcion) {
                case 1:
                    System.out.println(admin1.obtenerAlumnos());
                    break;
                case 2:
                    System.out.println("Ingrese el código del alumno");
                    long codigo = teclado.nextLong();
                    teclado.nextLine();
                    try {
                        System.out.println("\nAlumno encontrado: " + admin1.buscarAlumnos(codigo));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Ingrese el nombre del estudiante");
                    String nombre = teclado.nextLine();
                    System.out.println("Ingrese la carrera del estudiante ");
                    String carrera = teclado.nextLine();
                    System.out.println("Ingrese el código del estudiante");
                    long codigoAgregar = teclado.nextLong();
                    System.out.println("Ingrese la edad del alumno");
                    int edad = teclado.nextInt();
                    teclado.nextLine();
                    admin1.agregarAlumno(new AlumnoCalificado(nombre, carrera, codigoAgregar, edad, 0, true));
                    break;
                case 4:
                    System.out.println("Ingrese el codigo del alumno");
                    long codigoBuscar = teclado.nextLong();
                    admin1.ponerFalta(codigoBuscar);
                    break;
                case 5:
                    System.out.println("Finalizando el programa....");
                    break;
                default:
                    System.out.println("Numero o carácter invalido");
            }
        } while (opcion != 5);

        teclado.close();
    }
}

