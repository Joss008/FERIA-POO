package org.example;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/*
Clase principal del programa.
 */
public class Main {

    // Días válidos de la semana (lunes a viernes)
    private static final List<String> DIAS_VALIDOS = Arrays.asList(
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"
    );

    // Ciclos válidos en números romanos (I al X)
    private static final List<String> CICLOS_VALIDOS = Arrays.asList(
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    );

    public static void main(String[] args) throws SQLException {
        Scanner teclado = new Scanner(System.in);

        /*
        Indexamos un objeto
         */
        GestorAlumno admin1 = new GestorAlumno();

        /*
        Pedimos las credenciales UNA SOLA VEZ al inicio del programa
         */
        boolean accesoPermitido = false;
        while (!accesoPermitido) {
            System.out.println("Ingrese usuario administrador");
            String usuario = teclado.nextLine();
            System.out.println("Ingrese contraseña del administrador");
            String contraseña = teclado.nextLine();

            if (usuario.equals("admin0") && contraseña.equals("Sistemas")) {
                accesoPermitido = true;
                System.out.println("Acceso concedido. Bienvenido al sistema.");
            } else {
                System.out.println("Usuario o contraseña incorrecta. Intente nuevamente.");
            }
        }

        /*
        Variable que usaremos para la toma de decisiones en el switch
         */
        int opcion = 0;

        /*
        El bucle do-while mantiene el menú activo hasta que el usuario elija Salir
         */
        do {
                System.out.println("""
                    =====================================================
                    |   Bienvenido al Sistema Control del Comedor EPIS  |
                    =====================================================
                    1.- Lista de alumnos calificados (por día)
                    2.- Buscar alumno por código
                    3.- Agregar a un alumno a la lista
                    4.- Poner una falta al alumno
                    5.- Quitar una falta a un alumno (con justificación)
                    6.- Ver justificaciones de faltas
                    7.- Historial de inasistencias
                    8.- Salir""");

                opcion = teclado.nextInt();
                teclado.nextLine();

                /**
                 * Switch con 8 acciones, incluida la de finalizar el programa.
                 */
                switch (opcion) {

                    // ─────────────────────────────────────────────────────────
                    // CASE 1: Lista de alumnos calificados filtrada por día
                    // ─────────────────────────────────────────────────────────
                    case 1:
                        System.out.println("\nSeleccione el día de la semana para ver la lista:");
                        System.out.println("  1. LUNES");
                        System.out.println("  2. MARTES");
                        System.out.println("  3. MIERCOLES");
                        System.out.println("  4. JUEVES");
                        System.out.println("  5. VIERNES");
                        System.out.print("Ingrese el número del día: ");

                        int numeroDia = teclado.nextInt();
                        teclado.nextLine();

                        if (numeroDia < 1 || numeroDia > 5) {
                            System.out.println("Opción de día inválida.");
                            break;
                        }

                        String diaSeleccionado = DIAS_VALIDOS.get(numeroDia - 1);
                        List<AlumnoCalificado> alumnosDia = admin1.obtenerAlumnosPorDia(diaSeleccionado);

                        System.out.println("\n========================================");
                        System.out.println("  Alumnos calificados — " + diaSeleccionado);
                        System.out.println("  (Máximo 30 alumnos por día)");
                        System.out.println("========================================");

                        if (alumnosDia.isEmpty()) {
                            System.out.println("No hay alumnos registrados para el día " + diaSeleccionado + ".");
                        } else {
                            for (AlumnoCalificado a : alumnosDia) {
                                System.out.println(a);
                            }
                            System.out.println("\nTotal: " + alumnosDia.size() + " alumno(s).");
                        }
                        break;

                    // ─────────────────────────────────────────────────────────
                    // CASE 2: Buscar alumno por código
                    // ─────────────────────────────────────────────────────────
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

                    // ─────────────────────────────────────────────────────────
                    // CASE 3: Agregar alumno (con ciclo romano + diasSolicitados)
                    // ─────────────────────────────────────────────────────────
                    case 3:
                        System.out.println("Ingrese el nombre del estudiante");
                        String nombre = teclado.nextLine();

                        System.out.println("Ingrese el apellido del estudiante");
                        String apellido = teclado.nextLine();

                        // Pedir ciclo en números romanos con validación
                        String ciclo;
                        while (true) {
                            System.out.println("Ingrese el ciclo del estudiante (I, II, III, IV, V, VI, VII, VIII, IX, X)");
                            ciclo = teclado.nextLine().trim().toUpperCase();
                            if (CICLOS_VALIDOS.contains(ciclo)) {
                                break;
                            }
                            System.out.println("Ciclo inválido. Use números romanos del I al X.");
                        }

                        System.out.println("Ingrese el código del estudiante");
                        long codigoAgregar = teclado.nextLong();
                        System.out.println("Ingrese la edad del alumno");
                        int edad = teclado.nextInt();
                        teclado.nextLine();

                        // Pedir días solicitados (pueden ser varios)
                        Set<String> diasSet = new LinkedHashSet<>();
                        System.out.println("Ingrese los días solicitados en el comedor (LUNES, MARTES, MIERCOLES, JUEVES, VIERNES).");
                        System.out.println("Ingrese un día a la vez. Escriba 'FIN' cuando termine.");
                        while (true) {
                            System.out.print("Día: ");
                            String diaIngresado = teclado.nextLine().trim().toUpperCase();
                            if (diaIngresado.equals("FIN")) {
                                if (diasSet.isEmpty()) {
                                    System.out.println("Debe ingresar al menos un día.");
                                    continue;
                                }
                                break;
                            }
                            if (DIAS_VALIDOS.contains(diaIngresado)) {
                                diasSet.add(diaIngresado);
                                System.out.println("Día agregado: " + diaIngresado);
                            } else {
                                System.out.println("Día inválido. Use: LUNES, MARTES, MIERCOLES, JUEVES o VIERNES.");
                            }
                        }
                        String diasSolicitados = String.join(",", diasSet);

                        admin1.agregarAlumno(new AlumnoCalificado(nombre, apellido, ciclo, codigoAgregar, edad, 0, true, diasSolicitados));
                        System.out.println("Alumno registrado con días: " + diasSolicitados);
                        break;

                    // ─────────────────────────────────────────────────────────
                    // CASE 4: Poner falta
                    // ─────────────────────────────────────────────────────────
                    case 4:
                        System.out.println("Ingrese el codigo del alumno");
                        long codigoBuscar = teclado.nextLong();
                        teclado.nextLine();
                        admin1.ponerFalta(codigoBuscar);
                        break;

                    // ─────────────────────────────────────────────────────────
                    // CASE 5: Quitar falta con justificación
                    // ─────────────────────────────────────────────────────────
                    case 5:
                        System.out.println("Ingrese el codigo del alumno");
                        long codigoBuscarRevocar = teclado.nextLong();
                        teclado.nextLine();
                        try {
                            System.out.println("\nAlumno encontrado: " + admin1.buscarAlumnos(codigoBuscarRevocar));
                            System.out.println("Ingrese la cantidad de faltas que quiere revocar");
                            int cantidadFaltas = teclado.nextInt();
                            teclado.nextLine();

                            // Solicitar justificación obligatoria
                            String justificacion;
                            while (true) {
                                System.out.println("Ingrese la justificación de por qué se quita la falta:");
                                justificacion = teclado.nextLine().trim();
                                if (!justificacion.isEmpty()) {
                                    break;
                                }
                                System.out.println("La justificación no puede estar vacía.");
                            }

                            AlumnoCalificado almActualizado = admin1.revocarFalta(codigoBuscarRevocar, cantidadFaltas, justificacion);
                            System.out.println("\nAlumno actualizado: " + almActualizado);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    // ─────────────────────────────────────────────────────────
                    // CASE 6: Ver justificaciones de faltas (reportes)
                    // ─────────────────────────────────────────────────────────
                    case 6:
                        admin1.mostrarReportesJustificaciones();
                        break;

                    // ─────────────────────────────────────────────────────────
                    // CASE 7: Historial de inasistencias
                    // ─────────────────────────────────────────────────────────
                    case 7:
                        admin1.mostrarHistorialInasistencias();
                        break;

                    // ─────────────────────────────────────────────────────────
                    // CASE 8: Salir
                    // ─────────────────────────────────────────────────────────
                    case 8:
                        System.out.println("Finalizando el programa....");
                        break;

                    default:
                        System.out.println("Numero o carácter invalido");
                }

        } while (opcion != 8);

        teclado.close();
    }
}