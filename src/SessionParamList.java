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


    private String returnNullString(Object tar){
        if(tar == null){
            return "null(value)";
        }
        else{
            return tar.toString();
        }
    }


    public String toString(){
        return "search=" + returnNullString(search) + " | genre=" + returnNullString(genre) +
                " \n| startwith=" + returnNullString(startwith) + " | back=" + returnNullString(back) +
                " \n| firstSort=" + returnNullString(firstSort) + " | firstSortOrder=" + returnNullString(firstSortOrder) +
                " \n| back=" + returnNullString(back) + " | back=" + returnNullString(back) +
                " \n| secondSort=" + returnNullString(secondSort) + " | secondSortOrder=" + returnNullString(secondSortOrder) +
                " \n| secondSortOrder=" + returnNullString(secondSortOrder) + " | secondSortOrder=" + returnNullString(secondSortOrder) +
                " \n| offset=" + returnNullString(offset) + " | itemNum=" + returnNullString(itemNum) +
                " \n| starname=" + returnNullString(starname) + " | title=" + returnNullString(title) +
                " \n| year=" + returnNullString(year) + " | director=" + returnNullString(director);
    }
}
