    package org.example;
    import java.lang.invoke.StringConcatFactory;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class GestorAlumno {
        private Conexion miConexion = new Conexion();

        public void agregarAlumno(AlumnoCalificado alm) {
            String sql = "INSERT INTO Alumnos (codigo, nombre, carrera, edad, faltas, horarioAprobado) VALUES (?, ?, ?, ?, ?, ?)";

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

        public AlumnoCalificado buscarAlumnos(long codigo) {
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


        public AlumnoCalificado ponerFalta(long codigo) {
            AlumnoCalificado alm = buscarAlumnos(codigo);

            int nuevasFaltas = alm.getFaltas() + 1;
            boolean sigueCalificado = nuevasFaltas <= 3;

            String sql = "UPDATE Alumnos SET FALTAS = ?, horarioAprobado = ? WHERE codigo = ?";
            try (Connection con = miConexion.conectar();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setInt(1, nuevasFaltas);
                pstmt.setBoolean(2, sigueCalificado);
                pstmt.setLong(3, codigo);

                pstmt.executeUpdate();
                if (!sigueCalificado) {
                    System.out.println("El alumno superó las 3 faltas (Total: " + nuevasFaltas + ") y fue retirado de los calificados.");
                } else {
                    System.out.println("Falta registrada correctamente. Total de faltas: " + nuevasFaltas);
                }

            } catch (SQLException e) {
                System.out.println("Error al registrar la falta: " + e.getMessage());
            }

            // 4. Retornamos el objeto alumno actualizado desde la base de datos
            return buscarAlumnos(codigo);
        }
    }


