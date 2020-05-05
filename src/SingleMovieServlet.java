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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HelperFunc.printToConsole("action: ");
        HelperFunc.printToConsole(action);
        PrintWriter out = response.getWriter();
        if(action != null) {
            try{
                HelperFunc.addToCartButton(out, action, request);
            } catch (Exception e) {
                // write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "fail");
                HelperFunc.printToConsole("Error: " + e.getMessage());
                out.write(jsonObject.toString());
                response.setStatus(200);
            }
            out.close();
        }
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HelperFunc.printToConsole("do??Get_SingleMovieServlet?really?");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT * from movies as m, ratings as r where m.id = r.movieId and id = '" + id + "'";
            JsonArray jsonArray = HelperFunc.movieListTable(query, dbcon);
            // last item: lastParamJson
            HttpSession session = request.getSession();
            SessionParamList lastParam = (SessionParamList) session.getAttribute("lastParamList");
            if(lastParam != null){
                jsonArray.add(HelperFunc.sessionParamToJsonObject(lastParam));
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            dbcon.close();

        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        HelperFunc.printToConsole("SingleMovieServletReturn?");
        out.close();
    }
}
