package org.example;
import java.sql.*;

public class Conexion {
    String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=ComedorEPIS;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    String usuario = "sa";
    String password = "ComedorEpis2026!";

    public Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, usuario, password);
            // Agrega esta línea para confirmar visualmente
            System.out.println("[OK] Conectado a SQL Server");
        } catch (SQLException e) {
            System.out.println("[ERROR] No se pudo conectar: " + e.getMessage());
        }
        return con;
    }
}

