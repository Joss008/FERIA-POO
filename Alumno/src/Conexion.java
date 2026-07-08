package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion{
    String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=ComedorEPIS;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    String usuario = "sa";
    String password = "ComedorEpis2026!";

    public Connection conectar (){
        Connection con = null;
        try{
            con = DriverManager.getConnection(url,usuario,password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return con;
    }
}