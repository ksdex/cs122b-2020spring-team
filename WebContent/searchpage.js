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
function searchText() {
    // Get request URL
    let title = document.getElementById("title").value.toString();
    let year = document.getElementById("year").value.toString();
    let director = document.getElementById("director").value.toString();
    let starname = document.getElementById("starname").value.toString();
    let name = new Array('title','year','director','starname');
    let array = new Array(title,year,director,starname);
    let url = "index.html";
    let anyElement = 0;
    for(let i = 0; i < name.length; i++){
        if(array[i]!=null&&array[i].length!=0){
            if(anyElement==0){
                anyElement++;
                url += "?search=true";
            }
            url += '&' + name[i] + '=' + array[i];
        }
    }
    window.location.href=url;
}