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


function handleReturnUrl(lastParam){
    let url = "index.html"
    console.log(lastParam);
    console.log(Object.keys(lastParam).length);
    if(Object.keys(lastParam).length <= 0){
        return url;
    }
    else{
        url += "?"
        console.log(lastParam);
        for(let item in lastParam){
            url += item + "=" + lastParam[item] + "&";
        }
        url += "back=1";
        return url;
    }
}


function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    let lastParam = resultData[resultData.length-1];
    console.log(lastParam);
    let url = handleReturnUrl(lastParam);
    document.getElementById("returnPrevMain").href = url;
    console.log("Set url: " + url);

    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    for (let i = 0; i < resultData.length-1; i++) {
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Not add link <- Add a link to single-movie.html with id passed with GET url parameter
            '<a>'
            + resultData[i]["movie_title"] +     // display movie_name for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + resultData[i]['movie_year'] + "</th>";
        rowHTML += "<th>" + resultData[i]['movie_director'] + "</th>";
        rowHTML += "<th><ul>";
        let genreHTML = "";
        let j = 1;
        while(resultData[i]['movie_genres'][j] != undefined) {
            genreHTML += '<li><a href="index.html?genreid=' + resultData[i]['movie_genres'][j]['genreId'] + '">' +
                resultData[i]['movie_genres'][j]['name'] + "</a>";
            j++;
        }
        rowHTML += genreHTML
        rowHTML += "</ul></th>";
        rowHTML += "</ul></th>";

        rowHTML += "<th><ul>";
        j = 1;
        while(resultData[i]['movie_stars'][j] != undefined){
            rowHTML += '<li><a href="single-star.html?id=' + resultData[i]['movie_stars'][j]["id"] + '">'
                        + resultData[i]['movie_stars'][j]["name"] + '</a>';
            j++;
        }

        rowHTML += "</ul></th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});