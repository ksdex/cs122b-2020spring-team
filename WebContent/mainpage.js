// #######################################
// Helper functions

// debug
let allowConsolePrint = 1;
function consolePrint(tar){
    if(allowConsolePrint == 1) {
        console.log(tar);
    }
}

// #######################################

function handleGenreResult(resultData) {
    consolePrint("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let genreTable = jQuery("#browserGenre");
    let titleTable = jQuery("#browserTitle")
    let none = "none";
    for (let j = 0; j < 10; j++){
        let rowHTML2 = "";
        rowHTML2 += '<li class="titleLinkWrapper"><a class="link" href="index.html?startwith='+j+'">';
        rowHTML2 += "<span>"+j+"</span>"+ '</a></li>';
        titleTable.append(rowHTML2);
    }
    for (let j = 0; j < 26; j++){
        let rowHTML2 = "";
        rowHTML2 += '<li class="titleLinkWrapper"><a class="link" href="index.html?startwith='+ String.fromCharCode(97+j)+'">';
        rowHTML2 += "<span>"+String.fromCharCode(65+j)+"</span>"+ '</a></li>';
        titleTable.append(rowHTML2);
    }
    let rowHTML2 = "";
    rowHTML2 += '<li class="titleLinkWrapper"><a class="link" href="index.html?startwith='+ none +'">';
    rowHTML2 += "<span>*</span>"+ '</a></li>';
    titleTable.append(rowHTML2);
    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        if(resultData[i]['genre_id'] != undefined){
            rowHTML += '<li class="genreLinkWrapper"><a class="link" href="index.html?genreid='+resultData[i]['genre_id']+'">';
            rowHTML += "<span>"+resultData[i]['genre_name']+"</span>"+ '</a></li>';
        }
        // Append the row created to the table body, which will refresh the page
        genreTable.append(rowHTML);
    }
}


// #####################################################

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/mainpage", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});