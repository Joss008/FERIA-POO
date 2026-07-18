package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
Clase que nos servira para gestionar los objetos de clase AlumnoCalificado.
Los métodos sincronizados permiten acceso seguro cuando varios hilos consultan o modifican datos.
 */
public class GestorAlumno {
    private Conexion miConexion = new Conexion();

    /*
    Agrega un alumno a la base de datos, incluyendo su ciclo y días solicitados.
     */
    public synchronized void agregarAlumno(AlumnoCalificado alm) throws SQLException {
        String sql = "INSERT INTO Alumnos (codigo, nombre, apellido, ciclo, edad, faltas, horarioAprobado, diasSolicitados) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new SQLException("No se pudo conectar a la base de datos. Verifique su servidor.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, alm.getCodigo());
                pstmt.setString(2, alm.getNombre());
                pstmt.setString(3, alm.getApellido());
                pstmt.setString(4, alm.getCiclo());
                pstmt.setInt(5, alm.getEdad());
                pstmt.setInt(6, alm.getFaltas());
                pstmt.setBoolean(7, alm.isHorarioAprobado());
                pstmt.setString(8, alm.getDiasSolicitados());

                pstmt.executeUpdate();
                System.out.println("Alumno guardado exitosamente en la base de datos.");
            }
        }
    }

    /*
    Enlistamos todos los alumnos calificados, filtrados por día de la semana.
    Solo muestra alumnos cuyo campo diasSolicitados contenga el día indicado.
    Máximo 30 alumnos por día.
     */
    public synchronized List<AlumnoCalificado> obtenerAlumnosPorDia(String dia) throws SQLException {
        List<AlumnoCalificado> alumnosCalificados = new ArrayList<>();
        String sql = "SELECT TOP 30 * FROM Alumnos WHERE horarioAprobado = 1 AND diasSolicitados LIKE ?";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer la conexión con SQL Server (conexión nula).");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + dia.toUpperCase() + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        AlumnoCalificado alm = new AlumnoCalificado(
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("ciclo"),
                                rs.getLong("codigo"),
                                rs.getInt("edad"),
                                rs.getInt("faltas"),
                                rs.getBoolean("horarioAprobado"),
                                rs.getString("diasSolicitados")
                        );
                        alumnosCalificados.add(alm);
                    }
                }
            }
        }
        return alumnosCalificados;
    }

    /*
    Enlistamos todos los alumnos (sin filtro de día).
     */
    public synchronized List<AlumnoCalificado> obtenerAlumnos() throws SQLException {
        List<AlumnoCalificado> alumnosCalificados = new ArrayList<>();
        String sql = "SELECT * FROM Alumnos";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer la conexión con SQL Server (conexión nula).");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    AlumnoCalificado alm = new AlumnoCalificado(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("ciclo"),
                            rs.getLong("codigo"),
                            rs.getInt("edad"),
                            rs.getInt("faltas"),
                            rs.getBoolean("horarioAprobado"),
                            rs.getString("diasSolicitados")
                    );
                    alumnosCalificados.add(alm);
                }
            }
        }
        return alumnosCalificados;
    }

    /*
    Método que sera usado para buscar alumnos con su codigo universitario
     */
    public synchronized AlumnoCalificado buscarAlumnos(long codigo) {
        String sql = "SELECT * FROM Alumnos WHERE codigo = ?";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new IllegalStateException("No hay conexión con la base de datos.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, codigo);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new AlumnoCalificado(
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("ciclo"),
                                rs.getLong("codigo"),
                                rs.getInt("edad"),
                                rs.getInt("faltas"),
                                rs.getBoolean("horarioAprobado"),
                                rs.getString("diasSolicitados")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar: " + e.getMessage());
            throw new RuntimeException("Error en la base de datos al buscar alumno: " + e.getMessage());
        }
        throw new IllegalArgumentException("Alumno con codigo: " + codigo + " no encontrado");
    }

    /*
    Metodo que servira para colocar faltas a los alumnos que no asistan.
    Si tienen más de 3 faltas se marcarán como inactivos en la interfaz.
     */
    public synchronized AlumnoCalificado ponerFalta(long codigo) {
        AlumnoCalificado alm = buscarAlumnos(codigo);

        int nuevasFaltas = alm.getFaltas() + 1;
        boolean sigueCalificado = nuevasFaltas <= 3;

        String sql = "UPDATE Alumnos SET faltas = ?, horarioAprobado = ? WHERE codigo = ?";
        try (Connection con = miConexion.conectar()) {
            if (con == null) {
                throw new IllegalStateException("No hay conexión con la base de datos.");
            }
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, nuevasFaltas);
                pstmt.setBoolean(2, sigueCalificado);
                pstmt.setLong(3, codigo);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar la falta: " + e.getMessage());
            throw new RuntimeException("Error en la base de datos al registrar falta: " + e.getMessage());
        }
        return buscarAlumnos(codigo);
    }

    /*
    Método para revocar (quitar) faltas a los alumnos.
    Requiere una justificación que se guarda en la tabla ReportesFalta.
    Si al quitar faltas el total es menor o igual a 3, el alumno vuelve a estar calificado.
     */
    public synchronized AlumnoCalificado revocarFalta(long codigo, int cantidadQuitar, String justificacion) {
        if (cantidadQuitar <= 0) {
            throw new IllegalArgumentException("La cantidad de faltas a quitar debe ser mayor a 0.");
        }
        if (justificacion == null || justificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar una justificación para revocar la falta.");
        }

        AlumnoCalificado alm = buscarAlumnos(codigo);
        int nuevasFaltas = Math.max(0, alm.getFaltas() - cantidadQuitar);
        boolean sigueCalificado = nuevasFaltas <= 3;

        String sqlUpdate = "UPDATE Alumnos SET faltas = ?, horarioAprobado = ? WHERE codigo = ?";
        String sqlReporte = "INSERT INTO ReportesFalta (codigo_alumno, justificacion, fecha) VALUES (?, ?, GETDATE())";

        try (Connection con = miConexion.conectar()) {
            if (con == null) {
                throw new IllegalStateException("No hay conexión con la base de datos.");
            }
            // Actualizar faltas del alumno
            try (PreparedStatement pstmt = con.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, nuevasFaltas);
                pstmt.setBoolean(2, sigueCalificado);
                pstmt.setLong(3, codigo);
                pstmt.executeUpdate();
            }
            // Guardar reporte de justificación
            try (PreparedStatement pstmtR = con.prepareStatement(sqlReporte)) {
                pstmtR.setLong(1, codigo);
                pstmtR.setString(2, justificacion.trim());
                pstmtR.executeUpdate();
                System.out.println("Justificación guardada en el reporte exitosamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al revocar faltas: " + e.getMessage());
            throw new RuntimeException("Error en la base de datos al revocar falta: " + e.getMessage());
        }
        return buscarAlumnos(codigo);
    }

    /*
    Método para ver el historial de justificaciones de faltas revocadas.
    Muestra código, nombre del alumno, justificación y fecha.
     */
    public synchronized void mostrarReportesJustificaciones() throws SQLException {
        String sql = "SELECT r.id, r.codigo, a.nombre, a.apellido, r.justificacion, r.fecha " +
                "FROM ReportesFalta r " +
                "LEFT JOIN Alumnos a ON r.codigo = a.codigo " +
                "ORDER BY r.fecha DESC";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer la conexión.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                System.out.println("\n=============================================");
                System.out.println("  REPORTES DE JUSTIFICACIONES DE FALTAS");
                System.out.println("=============================================");

                boolean hayReportes = false;
                while (rs.next()) {
                    hayReportes = true;
                    System.out.println("ID Reporte  : " + rs.getInt("id"));
                    System.out.println("Código      : " + rs.getLong("codigo"));
                    System.out.println("Alumno      : " + rs.getString("nombre") + " " + rs.getString("apellido"));
                    System.out.println("Justificación: " + rs.getString("justificacion"));
                    System.out.println("Fecha       : " + rs.getTimestamp("fecha"));
                    System.out.println("---------------------------------------------");
                }
                if (!hayReportes) {
                    System.out.println("No hay reportes de justificaciones registrados.");
                }
            }
        }
    }

    /*
    Método para mostrar el historial de inasistencias.
    Muestra N° de orden, apellidos, nombres y ciclo de todos los alumnos.
     */
    public synchronized void mostrarHistorialInasistencias() throws SQLException {

        // 1. Agregamos WHERE faltas > 0 para omitir a los que tienen 0 faltas
        // 2. Ordenamos primero por faltas DESC (de mayor a menor)
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY faltas DESC, apellido, nombre) AS nro, " +
                "apellido, nombre, ciclo, faltas FROM Alumnos WHERE faltas > 0";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new SQLException("No se pudo establecer la conexión.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                System.out.println("\n==========================================================");
                System.out.println("              HISTORIAL DE INASISTENCIAS");
                System.out.println("==========================================================");

                // Quitamos la columna extra "Apellido" para que el texto tenga espacio y se vea limpio
                System.out.printf("%-5s %-35s %-8s %-6s%n",
                        "N°", "Apellidos y Nombres", "Ciclo", "Faltas");
                System.out.println("----------------------------------------------------------");

                boolean hayRegistros = false;
                while (rs.next()) {
                    hayRegistros = true;
                    int nro = rs.getInt("nro");
                    String ape = rs.getString("apellido");
                    String nom = rs.getString("nombre");
                    String ciclo = rs.getString("ciclo");
                    int faltas = rs.getInt("faltas");

                    // Imprimimos solo las 4 columnas necesarias
                    System.out.printf("%-5d %-35s %-8s %-6d%n",
                            nro, ape + ", " + nom, ciclo, faltas);
                }

                if (!hayRegistros) {
                    System.out.println("No hay alumnos con inasistencias registradas actualmente.");
                }
                System.out.println("==========================================================");
            }
        }
    }
}