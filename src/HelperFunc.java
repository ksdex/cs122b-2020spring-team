import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class HelperFunc {
    static boolean debugMode = true;

    static public void printToConsole(Object tar){
        if(debugMode){
            System.out.println(tar.toString());
        }
    }


    static public void addToCartButton(PrintWriter out, String action, HttpServletRequest request) throws IOException {
        HelperFunc.printToConsole("Post: action not null");
        HelperFunc.printToConsole(action.equals("addToCart"));
        if(action.equals("addToCart")) {
            String movieId = request.getParameter("movieId");
            HttpSession session = request.getSession();
            Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
            if (cartItems == null) {
                Map<String, float[]> result = new HashMap<String, float[]>();
                float[] temp = new float[2];
                temp[0] = 1;
                temp[1] = 0;
                result.put(movieId, temp);
                session.setAttribute("cartItems", result);
            } else {
                float[] temp = new float[2];
                if (cartItems.get(movieId) == null) {
                    temp[0] = 1;
                    temp[1] = 0;
                    cartItems.put(movieId, temp);
                } else {
                    temp[0] = cartItems.get(movieId)[0] + 1;
                    temp[1] = cartItems.get(movieId)[1];
                    cartItems.put(movieId, temp);
                }
                session.setAttribute("cartItems", cartItems);
            }
            HelperFunc.printToConsole(movieId);
            HelperFunc.printToConsole(cartItems);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            out.write(jsonObject.toString());
        }
    }


    static public JsonObject sessionParamToJsonObject(SessionParamList paramList){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("search", paramList.search);
        jsonObject.addProperty("genreid", paramList.genre);
        jsonObject.addProperty("startwith", paramList.startwith);
        jsonObject.addProperty("firstSort", paramList.firstSort);
        jsonObject.addProperty("firstSortOrder", paramList.firstSortOrder);
        jsonObject.addProperty("secondSort", paramList.secondSort);
        jsonObject.addProperty("secondSortOrder", paramList.secondSortOrder);
        jsonObject.addProperty("offset", paramList.offset);
        jsonObject.addProperty("itemNum", paramList.itemNum);
        jsonObject.addProperty("starname", paramList.starname);
        jsonObject.addProperty("title", paramList.title);
        jsonObject.addProperty("year", paramList.year);
        jsonObject.addProperty("director", paramList.director);
        return jsonObject;
    }


    static public JsonArray movieListTable(String query, Connection dbcon) throws SQLException {
        Statement statement = dbcon.createStatement();
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
        rs.close();
        statement.close();
        return jsonArray;
    }

}
