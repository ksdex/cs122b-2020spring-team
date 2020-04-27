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

// #######################################
// Helper function
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


function getAllParameter(){
    let url = window.location.href;
    if(url.indexOf("?") == -1){
        return null;
    }
    url = url.split("?")[1];
    url = url.split("&");
    let result = {};
    for(let i = 0; i < url.length; i++){
        let temp = url[i].split("=");
        result[temp[0]] = temp[1];
    }
    return result;
}


function sendCurAndMoreParam(dataSet){
    let parameterSet = getAllParameter();
    console.log(parameterSet);
    dataSet = Object.assign(dataSet, parameterSet);

    console.log(dataSet);

    // update page
    if (dataSet != {}) {
        $.ajax(
            "api/movieList", {
                method: "POST",
                data: dataSet,
                success: (resultData) => handleSortResult(resultData)
            }
        );
    }
}

// ################################################
// Global variable

let listSortOrder = ["noSort", -1, "noSort", -1];
let pageViewItem = 20;
let reachEnd = 0; // 0: false; 1: true

// ################################################
// Task 3: Previous/Next
function changePageViewItem(numItem){
    pageViewItem = numItem;
    let currentPageNum = Number(document.getElementById("curPage").innerText);
    let offset = (currentPageNum-1) * numItem;
    let dataSet = {"offset": offset, "itemNum": numItem};
    console.log(dataSet);
    sendCurAndMoreParam(dataSet);
}


function jumpPrevPage(){
    let currentPageNum = Number(document.getElementById("curPage").innerText);

    console.log(reachEnd);
    if(reachEnd == 1){
        reachEnd = 0;
        document.getElementById("nextPage").disabled = false;
    }


    if(currentPageNum > 1){
        document.getElementById("prevPage").disabled = false;
        $("#curPage").text(currentPageNum-1);
        if(currentPageNum-1 <= 1){
            document.getElementById("prevPage").disabled = true;
        }
        changePageViewItem(pageViewItem);
    }
    else{
        document.getElementById("prevPage").disabled = true;
    }
}


function jumpNextPage(){
    document.getElementById("prevPage").disabled = false;
    let currentPageNum = Number(document.getElementById("curPage").innerText);
    $("#curPage").text(currentPageNum+1);
    changePageViewItem(pageViewItem);
}



// ################################################
// Task 3: Sort

function theOtherSort(item){
    if(item == "Rating"){
        return "Title";
    }
    else{
        return "Rating";
    }
}

function updateList(item, order){
    if(order == 0){
        $("#" + item).text(item + " ↓");
    }
    else{
        $("#" + item).text(item + " ↑");
    }
    if(listSortOrder[2] == "noSort"){
        $("#" + theOtherSort(item)).text(theOtherSort(item));
    }
}


function sortByItem(item, sortOrder, order){
    listSortOrder[sortOrder*2] = item;
    listSortOrder[sortOrder*2 + 1] = handleSortOrder(order);
    console.log(listSortOrder);
    updateList(item, order);
    if(listSortOrder[0] != "noSort") {
        // If the current page has parameters
        let dataSet = {};
        let temp;
        if (listSortOrder[0] != "noSort") {
            temp = listSortOrder[0];
            dataSet["firstSort"] = temp.replace(temp[0], temp[0].toLowerCase());
            dataSet["firSortOrder"] = listSortOrder[1];
        }
        if (listSortOrder[2] != "noSort") {
            temp = listSortOrder[2];
            dataSet["secondSort"] = temp.replace(temp[0], temp[0].toLowerCase());
            dataSet["secSortOrder"] = listSortOrder[3];
        }
        sendCurAndMoreParam(dataSet);
    }
}


function handleSortOrder(i){
    if(i == 0){
        return "desc";
    }
    else{
        return "asc";
    }
}


function handleSortResult(resultData){
    let starTableBodyElement = jQuery("#star_table_body");
    starTableBodyElement.text("");
    console.log("Clear");
    handleResult(resultData);
}


// ################################################
// Handle page

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
        let genreHTML = "";
        for(let j = 1; j<4; j++){
            if(resultData[i]['movie_genres'][j] != undefined) {
                genreHTML += '<li><a href="index.html?genreid=' + resultData[i]['movie_genres'][j]['genreId'] + '">' +
                            resultData[i]['movie_genres'][j]['name'] + "</a>";
            }
        }
        rowHTML += genreHTML
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

    console.log(resultData.length);
    console.log(pageViewItem);
    console.log(resultData.length <= pageViewItem.valueOf());

    if(resultData.length < pageViewItem.valueOf()){
        reachEnd = 1;
        document.getElementById("nextPage").disabled = true;
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