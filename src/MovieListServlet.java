import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movieList")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        String genre =  request.getParameter("genreid");
        String startwith = request.getParameter("startwith");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "SELECT id from movies as m, ratings as r where m.id = r.movieId order by rating desc limit 0, 20";
            if(genre!=null){
                    query = "SELECT movieid from genres_in_movies where genreId="+genre;
            }
            else if(startwith!=null){
                if(startwith.equals("none")){
                    query = "select id as movieid from movies where title not REGEXP '^[0-9a-zA-Z]' ";
                }
                else{
                    query = "SELECT id as movieid from movies where title like '" + startwith + "%' or title like '"+startwith.toUpperCase()+"%'";
                }
            }
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("movieid");
                Statement statement2 = dbcon.createStatement();
                ResultSet rs2 = statement2.executeQuery(
                        "SELECT * from movies as m, ratings as r where m.id = r.movieId and id = '" + id + "'");

                while(rs2.next()){
                    JsonObject jsonObject = new JsonObject();
                    String movie_id = rs2.getString("id");
                    String movie_title = rs2.getString("title");
                    String movie_year = rs2.getString("year");
                    String movie_director = rs2.getString("director");
                    String movie_rating = rs2.getString("rating");
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    Statement statement3 = dbcon.createStatement();
                    ResultSet rs3 = statement3.executeQuery(
                            "select * from movies,stars,stars_in_movies" +
                                    " where stars.id=stars_in_movies.starId and stars_in_movies.movieId=movies.id" +
                                    " and movies.id= '" + movie_id + "'");
                    JsonObject starsJsonObject = new JsonObject();
                    int count = 1;
                    while(rs3.next()){
                        JsonObject singleStarJsonObject = new JsonObject();
                        singleStarJsonObject.addProperty("id", rs3.getString("starId"));
                        singleStarJsonObject.addProperty("name", rs3.getString("name"));
                        starsJsonObject.add(Integer.toString(count), singleStarJsonObject);
                        count += 1;
                    }
                    jsonObject.add("movie_stars", starsJsonObject);
                    rs3.close();
                    statement3.close();

                    Statement statement4 = dbcon.createStatement();
                    ResultSet rs4 = statement4.executeQuery(
                            "select * from movies,genres,genres_in_movies" +
                                    " where genres.id=genres_in_movies.genreId and genres_in_movies.movieId=movies.id" +
                                    " and movies.id='" + movie_id + "'");
                    JsonObject genresJsonObject = new JsonObject();
                    count = 1;
                    while(rs4.next()){
                        genresJsonObject.addProperty(Integer.toString(count), rs4.getString("name"));
                        count += 1;
                    }
                    jsonObject.add("movie_genres", genresJsonObject);
                    rs4.close();
                    statement4.close();
                    jsonArray.add(jsonObject);
                }
                rs2.close();
                statement2.close();
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

        out.close();

    }
}
