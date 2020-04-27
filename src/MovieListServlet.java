import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movieList")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("action: ");
        System.out.println(action);
        if(action != null) {
            System.out.println("Post: action not null");
            System.out.println(action.equals("addToCart"));
            if(action.equals("addToCart")){
                String movieId = request.getParameter("movieId");
                HttpSession session = request.getSession();
                Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
                if (cartItems == null){
                    Map<String, float[]> result = new HashMap<String, float[]>();
                    float[] temp = new float[2];
                    temp[0] = 1;
                    temp[1] = 0;
                    result.put(movieId, temp);
                    session.setAttribute("cartItems", result);
                }
                else{
                    float[] temp = new float[2];
                    if (cartItems.get(movieId) == null){
                        temp[0] = 1;
                        temp[1] = 0;
                        cartItems.put(movieId, temp);
                    }
                    else{
                        temp[0] = cartItems.get(movieId)[0] + 1;
                        temp[1] = cartItems.get(movieId)[1];
                        cartItems.put(movieId, temp);
                    }
                    session.setAttribute("cartItems", cartItems);
                }
                System.out.println(movieId);
                System.out.println(cartItems);
            }
        }
        else {
            doGet(request, response);
        }
    }


    private String getOrder(String firstSort, String firstSortOrder, String secondSort, String secondSortOrder){
        String order = "";
        if (firstSort == null) {
            order += " order by rating desc";
        } else {
            System.out.println("Get here");
            order += " order by " + firstSort + " " + firstSortOrder;
            if (secondSort != null) {
                order += ", " + secondSort + " " + secondSortOrder;
            }
        }
        return order;
    }


    private String getLimit(String offset, String itemNum){
        String limit = "";
        if (offset == null) {
            limit += " limit 20 offset 0";
        } else {
            limit += " limit " + itemNum + " offset " + offset;
        }
        return limit;
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession();

        String search = request.getParameter("search");
        String genre =  request.getParameter("genreid");
        String startwith = request.getParameter("startwith");

        String back = request.getParameter("back");
        String firstSort = request.getParameter("firstSort");
        String firstSortOrder = request.getParameter("firstSortOrder");
        System.out.println(firstSort);
        System.out.println(firstSortOrder);
        String secondSort = request.getParameter("secondSort");
        String secondSortOrder = request.getParameter("secondSortOrder");
        String offset = request.getParameter("offset");
        String itemNum = request.getParameter("itemNum");
        JsonObject paramList = new JsonObject();
        if(search != null) {
            paramList.addProperty("search", search);
        }
        if(genre != null) {
            paramList.addProperty("genre", genre);
        }
        if(startwith != null) {
            paramList.addProperty("startwith", startwith);
        }
        if(firstSort != null) {
            paramList.addProperty("firstSort", firstSort);
            paramList.addProperty("firstSortOrder", firstSortOrder);
        }
        if(secondSort != null) {
            paramList.addProperty("secondSort", secondSort);
            paramList.addProperty("secondSortOrder", secondSortOrder);
        }
        if(offset != null) {
            paramList.addProperty("offset", offset);
        }
        if(itemNum != null) {
            paramList.addProperty("itemNum", itemNum);
        }

        System.out.println(firstSort +" " + firstSortOrder +" " +  secondSort +" " +  secondSortOrder);
        System.out.println(offset +" " +  itemNum);
        String order = getOrder(firstSort, firstSortOrder, secondSort, secondSortOrder);
        String limit = getLimit(offset, itemNum);

        System.out.println(order);
        System.out.println(limit);

        System.out.println("code is here");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "SELECT id as movieid from movies as m, ratings as r where m.id = r.movieId" + order + limit;
            if(search!=null){
                System.out.println("search is here");

                String starname = request.getParameter("starname");
                String title = request.getParameter("title");
                String year = request.getParameter("year");
                String director = request.getParameter("director");
                if (starname != null) {
                    paramList.addProperty("starname", starname);
                }
                if (title != null) {
                    paramList.addProperty("title", title);
                }
                if (year != null) {
                    paramList.addProperty("year", year);
                }
                if (director != null) {
                    paramList.addProperty("director", director);
                }

                if(starname != null && starname.indexOf("%20") != -1){
                    starname = starname.replace("%20", " ");
                }

                String[] strArray={title, director};
                String[] name={"title","director"};
                System.out.println("Line 81");
                if(starname!=null&&starname.length()!=0){
                    JsonArray jsonArray = new JsonArray();
                    int onlyStarFlag = 0;
                    if(title!=null||year!=null||director!=null){
                        query = "SELECT id as movieid from (select m.id, m.title, m.year, m.director  from movies as m, ratings as r " +
                                "where m.id = ratings.Id " + order + ") as m where ";
                        int num = 1;
                        System.out.println("Line 88");
                        for (int i = 0; i< strArray.length; i++){
                            if(strArray[i]!=null&&strArray[i].length()!=0){
                                if(num==1){
                                    query += name[i] + " like " + "'%" + strArray[i] + "%'";
                                    num++;
                                }
                                else{
                                    query += " and " + name[i] + " like " + "'%" + strArray[i] + "%'";
                                }
                            }
                        }
                        if(year!=null&&year.length()!=0){
                            if(num==1){
                                query += "year = " + year;
                            }
                            else{
                                query += " and year = " + year;
                            }
                        }
                        query += limit;
                    }
                    else{
                        onlyStarFlag = 1;
                    }
                    System.out.println("line 111");

                    Statement statement0 = dbcon.createStatement();
                    ResultSet rs0 = statement0.executeQuery(
                            "SELECT m.movieid, r.rating, mo.title as Title from (SELECT movieId as movieid from stars_in_movies, stars" +
                                                       " where stars_in_movies.starId = stars.id" +
                                                       " and stars.name like '%"+starname+"%') as m, ratings as r, movies as mo" +
                                    " where m.movieid = r.movieId" +
                                    " and mo.id = r.movieId " + order + limit
                            );
                    System.out.println("line 120");
                    while(rs0.next()){
                        String mid = rs0.getString("movieid");
                        String query2="";
                        if(onlyStarFlag==0){
                            query2 = query+" and id ='"+mid+"'";
                        }
                        else{
                            query2 = "SELECT id as movieid from movies where id='" + mid + "'";
                        }
                        Statement starStatement = dbcon.createStatement();
                        ResultSet starrs = starStatement.executeQuery(query2);
                        while(starrs.next()){
                            String id = starrs.getString("movieid");
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
                                        "select sim.starId, siom.name, count(sim.starId) from stars_in_movies as sim, (" +
                                                "select starId, name from movies,stars,stars_in_movies where stars.id=stars_in_movies.starId " +
                                                "and stars_in_movies.movieId=movies.id and movies.id='" + movie_id + "' " +
                                                ")as siom " + // siom: stars_in_one_movie
                                                "where sim.starId = siom.starId " +
                                                "group by sim.starId " +
                                                "order by count(sim.starId) desc, siom.name");
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
                                                " and movies.id='" + movie_id + "' order by genres.name");
                                JsonObject genresJsonObject = new JsonObject();
                                count = 1;
                                while(rs4.next()){
                                    JsonObject oneGenresJsonObject = new JsonObject();
                                    oneGenresJsonObject.addProperty("name", rs4.getString("name"));
                                    oneGenresJsonObject.addProperty("genreId", rs4.getString("genreId"));
                                    genresJsonObject.add(Integer.toString(count), oneGenresJsonObject);
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
                        starrs.close();
                        starStatement.close();
                    }
                    dbcon.close();
                    out.write(jsonArray.toString());
                    // set response status to 200 (OK)
                    session.setAttribute("lastParamList", paramList);

                    response.setStatus(200);
                    out.close();
                    return;
                }
                else{
                    query = "SELECT id as movieid from (select m.id, m.title, m.year, m.director from movies as m, ratings as r " +
                            "where m.id = r.movieId " + order + ") as m where ";
                    int num = 1;
                    System.out.println("Line 211");
                    for (int i = 0; i< strArray.length; i++){
                        if(strArray[i]!=null&&strArray[i].length()!=0){
                            if(num==1){
                                query += name[i] + " like " + "'%" + strArray[i] + "%'";
                                num++;
                            }
                            else{
                                query += " and " + name[i] + " like " + "'%" + strArray[i] + "%'";
                            }
                        }
                    }
                    System.out.println("Line 223");
                    if(year!=null&&year.length()!=0){
                        if(num==1){
                            query += "year = " + year;
                        }
                        else{
                            query += " and year = " + year;
                        }
                    }
                    System.out.println("Line 232");
                    query += limit;
                }
            }
            else{
                System.out.println("no search");
                if(genre!=null){
                    query = "select g.movieid from (SELECT movieid from genres_in_movies where genreId="+genre + ") as g, movies as m, ratings as r " +
                            "where m.id = g.movieid and m.id = r.movieId " + order + limit;
                }
                else if(startwith!=null){
                    if(startwith.equals("none")){
                        query = "select m.id from (select id as movieid, title from movies where title not REGEXP '^[0-9a-zA-Z]') as m, ratings as r " +
                                "where m.id = r.movieId " + order + limit;
                    }
                    else{
                        query = "select m.movieid from (SELECT id as movieid, title from movies where title like '" + startwith + "%' or " +
                                "title like '"+startwith.toUpperCase()+"%') as m, ratings as r where m.movieid = r.movieId " + order + limit;
                    }
                }
            }

            // Perform the query
            System.out.println(query);
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("movieid");
                Statement statement2 = dbcon.createStatement();
                ResultSet rs2 = statement2.executeQuery(
                        "SELECT * from movies as m, ratings as r where m.id = r.movieId and id = '" + id + "'" );

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
                            "select sim.starId, siom.name, count(sim.starId) from stars_in_movies as sim, (" +
                                    "select starId, name from movies,stars,stars_in_movies where stars.id=stars_in_movies.starId " +
                                    "and stars_in_movies.movieId=movies.id and movies.id='" + movie_id + "' " +
                                    ")as siom " + // siom: stars_in_one_movie
                                    "where sim.starId = siom.starId " +
                                    "group by sim.starId " +
                                    "order by count(sim.starId) desc, siom.name");
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
                                    " and movies.id='" + movie_id + "' order by genres.name" );
                    JsonObject genresJsonObject = new JsonObject();
                    count = 1;
                    while(rs4.next()){
                        JsonObject oneGenresJsonObject = new JsonObject();
                        oneGenresJsonObject.addProperty("name", rs4.getString("name"));
                        oneGenresJsonObject.addProperty("genreId", rs4.getString("genreId"));
                        genresJsonObject.add(Integer.toString(count), oneGenresJsonObject);
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
            rs.close();
            statement.close();
            dbcon.close();
            session.setAttribute("lastParamList", paramList);
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);


        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println("Error: " + e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }

        out.close();

    }
}
