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


function handleShoppingCartResult(resultData) {
    console.log("handleMovieResult: populating handleShoppingCartResult from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let shoppingCartTableBodyElement = jQuery("#shopping_cart_table");

    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";

        /*
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
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
         */

        // Append the row created to the table body, which will refresh the page
        shoppingCartTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shopping-cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleShoppingCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});