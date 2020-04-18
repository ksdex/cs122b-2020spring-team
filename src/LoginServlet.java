import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /* This example only allows email/password to be test/test
        /  in the real project, you should talk to the database to verify email/password
        */
        JsonObject responseJsonObject = new JsonObject();
        Connection dbcon = null;
        int sameuser = 0;
        try {
            dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            String query = "SELECT password from users where email = '"+email+"'";
            ResultSet rs = statement.executeQuery(query);
            if(!rs.next()){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            }
            else{
                String truepwd = rs.getString("password");
                if(truepwd.equals(password)){
                    request.getSession().setAttribute("user", new User(email));
                    request.getSession().setAttribute("accessBoolean", true);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.getWriter().write(responseJsonObject.toString());
    }
}
