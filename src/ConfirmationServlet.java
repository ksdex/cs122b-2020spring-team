import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession();
        Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");

        if(cartItems != null) {
            // Output stream to STDOUT
            PrintWriter out = response.getWriter();

            try {
                // Get a connection from dataSource
                Connection dbcon = dataSource.getConnection();

                JsonArray jsonArray = new JsonArray();
                Random r = new Random();
                for (Map.Entry<String, float[]> entry : cartItems.entrySet()) {
                    // Declare our statement
                    String movieId = entry.getKey();
                    float[] itemInfo = entry.getValue();
                    Statement statement = dbcon.createStatement();
                    String query = "SELECT m.title from movies as m where id = '" + movieId + "'";

                    // Perform the query
                    ResultSet rs = statement.executeQuery(query);
                    float totalPrice = 0;
                    // Iterate through each row of rs
                    while (rs.next()) {
                        String movie_title = rs.getString("title");
                        // Create a JsonObject based on the data we retrieve from rs
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id", movieId);
                        jsonObject.addProperty("title", movie_title);
                        jsonObject.addProperty("num", itemInfo[0]);
                        jsonObject.addProperty("price", itemInfo[1]);
                        totalPrice += itemInfo[0] * itemInfo[1];
                        jsonArray.add(jsonObject);
                    }
                    JsonObject price = new JsonObject();
                    price.addProperty("totalPrice", totalPrice);
                    jsonArray.add(price);
                    session.setAttribute("cartItems", new HashMap<String, float[]>());
                    rs.close();
                    statement.close();
                }
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);
                dbcon.close();
                System.out.println("SingleMovieServletReturn");
                out.close();

            } catch (Exception e) {
                // write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());
                // set response status to 500 (Internal Server Error)
                response.setStatus(500);
            }
        }
    }
}
