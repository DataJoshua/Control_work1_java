package servlets.org;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.swing.plaf.nimbus.State;
import java.awt.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/form")
public class Form extends HttpServlet {

    private Connection conn;

    public void init(ServletConfig var1) throws ServletException{
        MakeConn makeConn = new MakeConn("java", "postgres", "123");
        this.conn = makeConn.getConn();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("form.jsp").forward(req,resp);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        Date birthdate = Date.valueOf(req.getParameter("birthdate"));
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");

        //hashing
        String password_digest = BCrypt.hashpw(password, BCrypt.gensalt(12));

        try {

            Statement stmId = conn.createStatement();
            ResultSet resultSet = stmId.executeQuery("SELECT nextval('users_id_seq')");
            resultSet.next();
            Integer user_id = resultSet.getInt("nextval");

            PreparedStatement stm = conn.prepareStatement("INSERT INTO users (id, login, password) VALUES (?, ?, ?);");
            stm.setInt(1, user_id);
            stm.setString(2, login);
            stm.setString(3, password_digest);


            PreparedStatement stm2 = conn.prepareStatement("INSERT INTO users_info (user_id, name, phone, birthday) VALUES (?, ?, ?, ?);");
            stm2.setInt(1, user_id);
            stm2.setString(2, name);
            stm2.setString(3, phone);
            stm2.setDate(4, birthdate);

            int row_stm = stm.executeUpdate();

            if(row_stm > 0){
                int row_stm2 = stm2.executeUpdate();
                if(row_stm2 > 0 ){
                    req.setAttribute("name",name );
                    req.setAttribute("birthdate",birthdate );
                    req.setAttribute("login",login );
                    req.setAttribute("phone",phone );
                    req.getRequestDispatcher("show.jsp").forward(req, resp);
                }
                else{
                    // Erase the previous inserted row if the second statement does not work
                    stm.executeUpdate("DELETE FROM users WHERE user_id = " + user_id);
                    resp.getWriter().println("<h1>Somethong went wrong!</h1>");
                }
            }
            else{
                resp.sendRedirect("<h1>Somethong went wrong!</h1>");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
