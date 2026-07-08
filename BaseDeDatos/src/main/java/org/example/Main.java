package org.example;
import java.sql.*;


public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        String url = "jdbc:sqlserver://localhost;"
                + "databaseName=master;"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        String usuario = "sa";
        String password = "Contrasena123!";

        try {
            Connection cn = DriverManager.getConnection(url, usuario, password);
            String sql = "select * from MSreplication_options";
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString("optname"));
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


