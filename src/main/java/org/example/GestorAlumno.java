package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
Clase que nos servira para gestionar los objetos de clase AlumnoCalificado
 */
public class GestorAlumno {
    private Conexion miConexion = new Conexion();



    public void agregarAlumno(AlumnoCalificado alm) throws SQLException {
        String sql = "INSERT INTO Alumnos (codigo, nombre, apellido, carrera, edad, faltas, horarioAprobado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = miConexion.conectar()) {
            if (conn == null) {
                throw new SQLException("No se pudo conectar a la base de datos. Verifique su servidor.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, alm.getCodigo());
                pstmt.setString(2, alm.getNombre());
                pstmt.setString(3, alm.getApellido());
                pstmt.setString(4, alm.getCarrera());
                pstmt.setInt(5, alm.getEdad());
                pstmt.setInt(6, alm.getFaltas());
                pstmt.setBoolean(7, alm.isHorarioAprobado());

                pstmt.executeUpdate();
                System.out.println("Alumno guardado exitosamente en la base de datos.");
            }
        }
    }

    /*
    Enlistamos los alumnos que estén calificados, identificados con la variable horarioAprobado.
     */
    public List<Alumno> obtenerAlumnos() throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT * FROM Alumnos WHERE horarioAprobado = 1";

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
                            rs.getString("carrera"),
                            rs.getLong("codigo"),
                            rs.getInt("edad"),
                            rs.getInt("faltas"),
                            rs.getBoolean("horarioAprobado")
                    );
                    lista.add(alm);
                }
            }
        }
        return lista;
    }

    /*
    Método que sera usado para buscar alumnos con su codigo universitario
     */
    public AlumnoCalificado buscarAlumnos(long codigo) {
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
                                rs.getString("carrera"),
                                rs.getLong("codigo"),
                                rs.getInt("edad"),
                                rs.getInt("faltas"),
                                rs.getBoolean("horarioAprobado")
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
    Metodo que servira para colocar faltas a los alumnos que no asistan
    si tienen más de 3 faltas se los retira de la lista de alumnos calificados
     */
    public AlumnoCalificado ponerFalta(long codigo) {
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
    Si al quitar faltas el total es menor o igual a 3, el alumno vuelve a estar calificado.
     */
    public AlumnoCalificado revocarFalta(long codigo, int cantidadQuitar) {
        if (cantidadQuitar <= 0) {
            throw new IllegalArgumentException("La cantidad de faltas a quitar debe ser mayor a 0.");
        }

        AlumnoCalificado alm = buscarAlumnos(codigo);
        int nuevasFaltas = Math.max(0, alm.getFaltas() - cantidadQuitar);
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
            System.out.println("Error al revocar faltas: " + e.getMessage());
            throw new RuntimeException("Error en la base de datos al revocar falta: " + e.getMessage());
        }
        return buscarAlumnos(codigo);
    }
}
