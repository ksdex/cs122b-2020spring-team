/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</th>";

        // add genres
        rowHTML += "<th>" + resultData[i]['movie_year'] + "</th>";
        rowHTML += "<th>" + resultData[i]['movie_director'] + "</th>";
        rowHTML += "<th><ul>";
        for(let j = 1; j<4; j++){
            if(resultData[i]['movie_genres'][j] != undefined) {
                rowHTML += '<li>' + resultData[i]['movie_genres'][j];
            }
        }
        rowHTML += "</ul></th>";

        // add stars
        rowHTML += "<th><ul>" ;
        for(let j = 1; j < 4; j++){
            if(resultData[i]['movie_stars'][j] != undefined){
                rowHTML += '<li><a href="single-star.html?id=' + resultData[i]['movie_stars'][j]["id"] + '">'
                    + resultData[i]['movie_stars'][j]["name"] + '</a>';
            }
        }

        rowHTML += "</ul></th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
console.log("javascript is here")
let genreid = getParameterByName('genreid');
let startwith = getParameterByName('startwith');
let search = getParameterByName('search');
if(search!=null){
    let url = "api/movieList?search=true"
    let title = getParameterByName('title');
    if(title!=null){
        url += "&title=" + title;
    }
    let year = getParameterByName('year');
    if(year!=null){
        url += "&year=" + year;
    }
    let director = getParameterByName('director');
    if(director!=null){
        url += "&director=" + director;
    }
    let starname = getParameterByName('starname');
    if(starname!=null){
        url += "&starname=" + starname;
    }
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: url, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
else if(genreid!=null){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movieList?genreid="+genreid, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
else if(startwith!=null){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movieList?startwith="+startwith, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
else{
    console.log("everything is null");
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movieList", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}