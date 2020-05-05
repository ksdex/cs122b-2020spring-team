import javax.servlet.http.HttpServletRequest;

public class SessionParamList {
    String search;
    String genre;
    String startwith;

    String back;
    String firstSort;
    String firstSortOrder;
    String secondSort;
    String secondSortOrder;
    String offset;
    String itemNum;
    String starname;
    String title;
    String year;
    String director;

    public SessionParamList(HttpServletRequest request){
        search = request.getParameter("search");
        genre =  request.getParameter("genreid");
        startwith = request.getParameter("startwith");

        back = request.getParameter("back");
        firstSort = request.getParameter("firstSort");
        firstSortOrder = request.getParameter("firstSortOrder");
        HelperFunc.printToConsole(firstSort);
        HelperFunc.printToConsole(firstSortOrder);
        secondSort = request.getParameter("secondSort");
        secondSortOrder = request.getParameter("secondSortOrder");
        offset = request.getParameter("offset");
        itemNum = request.getParameter("itemNum");

        starname = request.getParameter("starname");
        title = request.getParameter("title");
        year = request.getParameter("year");
        director = request.getParameter("director");
    }
}
