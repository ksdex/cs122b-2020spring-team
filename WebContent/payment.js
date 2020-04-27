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


function handlePriceResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");
    console.log(resultData);
    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let priceLabelElement = document.getElementById("totalPrice");
    priceLabelElement.innerText = "$" + resultData[0]["price"].toFixed(2);
}

let payment_form = $("#payment_form");
payment_form.submit(submitPaymentForm);
function submitPaymentForm(formSubmitEvent){
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: resultData => handlePaymentResult(resultData)
        }
    );
}


function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        alert("Successfully checkout :D");
        console.log("confirmation?");
        window.location.href = "confirmation.html";
    } else {
        // If payment fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/payment", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handlePriceResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

