package servlets.org;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MakeConn {
    private Connection conn;

    public MakeConn(String dbName, String user, String pwd){
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/" + dbName;
            conn = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Connection getConn(){
        return this.conn;
    }
}
