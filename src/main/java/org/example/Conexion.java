package org.example;
import java.sql.*;

/**
 *  Clase que conectara nuestro programa con la base de datos
 */
public class Conexion {
    String url = "jdbc:sqlserver://localhost:1435;"
            + "databaseName=ComedorEPIS;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    String usuario = "sa";
    String password = "ComedorEpis2026!";

    /*
    En este metodo agregaremos un try - catch en caso de errores al conectar nuestro programa
     */

    public Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            System.out.println("[ERROR] No se pudo conectar: " + e.getMessage());
        }
        return con;
    }
}

