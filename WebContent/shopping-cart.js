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


let isCartEmpty = true;

function handleShoppingCartResult(resultData) {
    console.log("handleMovieResult: populating handleShoppingCartResult from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let shoppingCartTableBodyElement = jQuery("#shopping_cart_body");

    console.log(resultData.length);

    if(resultData.length == 0){
        isCartEmpty = true;
    }
    else{
        isCartEmpty = false;
    }


    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["num"] +
            "<button onclick=\"changeItem('addItem', '" + resultData[i]["id"] + "')\"> + </button>" +
            "<button onclick=\"changeItem('decreaseItem', '" + resultData[i]["id"] + "')\"> - </button>" +
            "<button onclick=\"changeItem('deleteItem', '" + resultData[i]["id"] + "')\"> × </button>" +
            "</th>";
        rowHTML += "<th>$" + resultData[i]["price"].toFixed(2) + "</th>";
        rowHTML += "<th>$" + (resultData[i]["price"] * resultData[i]["num"]).toFixed(2) + "</th>";
        rowHTML += "</tr>";

        console.log(rowHTML);
        // Append the row created to the table body, which will refresh the page
        shoppingCartTableBodyElement.append(rowHTML);
    }
}


function changeItem(action, movieId){
    let shoppingCartTableBodyElement = document.getElementById("shopping_cart_body");
    shoppingCartTableBodyElement.innerText = "";
    console.log("in");
    console.log(shoppingCartTableBodyElement.innerText);
    $.ajax("api/shopping-cart", {
        method: "POST",
        data: {"action": action, "movieId": movieId},
        success: resultData => handleShoppingCartResult(resultData)
    });
}


function checkOut(){
    console.log(isCartEmpty);
    if(isCartEmpty == false) {
        window.location.href = "payment.html";
    }
    else{
        alert("The cart is empty.");
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