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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        System.out.println("doGet_SingleMovieServlet");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "SELECT * from movies as m, ratings as r where m.id = r.movieId and id = '" + id + "'";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                Statement statement2 = dbcon.createStatement();
                ResultSet rs2 = statement2.executeQuery(
                        "select sim.starId, siom.name, count(sim.starId) from stars_in_movies as sim, (" +
                                "select starId, name from movies,stars,stars_in_movies where stars.id=stars_in_movies.starId " +
                                "and stars_in_movies.movieId=movies.id and movies.id='" + movie_id + "' " +
                                ")as siom " + // siom: stars_in_one_movie
                                "where sim.starId = siom.starId " +
                                "group by sim.starId " +
                                "order by count(sim.starId) desc, siom.name");
                JsonObject starsJsonObject = new JsonObject();
                int count = 1;
                while(rs2.next()){
                    JsonObject singleStarJsonObject = new JsonObject();
                    singleStarJsonObject.addProperty("id", rs2.getString("starId"));
                    singleStarJsonObject.addProperty("name", rs2.getString("name"));
                    starsJsonObject.add(Integer.toString(count), singleStarJsonObject);
                    count += 1;
                }
                jsonObject.add("movie_stars", starsJsonObject);
                rs2.close();
                statement2.close();

                Statement statement3 = dbcon.createStatement();
                ResultSet rs3 = statement3.executeQuery(
                        "select * from movies,genres,genres_in_movies" +
                                " where genres.id=genres_in_movies.genreId and genres_in_movies.movieId=movies.id" +
                                " and movies.id='" + movie_id + "' order by genres.name");
                JsonObject genresJsonObject = new JsonObject();
                count = 1;
                while(rs3.next()){
                    JsonObject oneGenresJsonObject = new JsonObject();
                    oneGenresJsonObject.addProperty("name", rs3.getString("name"));
                    oneGenresJsonObject.addProperty("genreId", rs3.getString("genreId"));
                    genresJsonObject.add(Integer.toString(count), oneGenresJsonObject);
                    count += 1;
                }
                jsonObject.add("movie_genres", genresJsonObject);
                rs3.close();
                statement3.close();
                jsonArray.add(jsonObject);
            }

            // last item: lastParamJson
            HttpSession session = request.getSession();
            JsonObject lastParam = (JsonObject) session.getAttribute("lastParamList");
            if(lastParam != null){
                jsonArray.add(lastParam);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        System.out.println("SingleMovieServletReturn");
        out.close();

    }
}
