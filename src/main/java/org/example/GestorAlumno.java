    package org.example;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class GestorAlumno {
        private Conexion miConexion = new Conexion();

        public void agregarAlumno(AlumnoCalificado alm) {
            String sql = "INSERT INTO Alumnos (codigo, nombre, carrera, edad, faltas, horarioAprobado) VALUES (?, ?, ?, ?, ?, ?)";

            // Usamos miConexion.conectar()
            try (Connection conn = miConexion.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, alm.getCodigo());
                pstmt.setString(2, alm.getNombre());
                pstmt.setString(3, alm.getCarrera());
                pstmt.setInt(4, alm.getEdad());
                pstmt.setInt(5, alm.getFaltas());
                pstmt.setBoolean(6, alm.isHorarioAprobado());

                pstmt.executeUpdate();
                System.out.println("Alumno guardado exitosamente en la base de datos.");

            } catch (SQLException e) {
                System.out.println("Error al guardar el alumno: " + e.getMessage());
            }
        }

        public List<Alumno> obtenerAlumnos() {
            List<Alumno> lista = new ArrayList<>();
            String sql = "SELECT * FROM Alumnos WHERE horarioAprobado = 1";

            try (Connection conn = miConexion.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    AlumnoCalificado alm = new AlumnoCalificado(
                            rs.getString("nombre"),
                            rs.getString("carrera"),
                            rs.getLong("codigo"),
                            rs.getInt("edad"),
                            rs.getInt("faltas"),
                            rs.getBoolean("horarioAprobado")
                    );
                    lista.add(alm);
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener alumnos: " + e.getMessage());
            }
            return lista;
        }

        public Alumno buscarAlumnos(long codigo) {
            String sql = "SELECT * FROM Alumnos WHERE codigo = ?";

            try (Connection conn = miConexion.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, codigo);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return new AlumnoCalificado(
                            rs.getString("nombre"),
                            rs.getString("carrera"),
                            rs.getLong("codigo"),
                            rs.getInt("edad"),
                            rs.getInt("faltas"),
                            rs.getBoolean("horarioAprobado")
                    );
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar: " + e.getMessage());
            }
            throw new IllegalArgumentException("Alumno con codigo: " + codigo + " no encontrado");
        }
    }

