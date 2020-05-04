/* helper.js
 * import && export: https://www.cnblogs.com/jixiaohua/p/10704622.html
 *                   https://www.it1352.com/1025522.html
 *
 * export: import {consolePrint, initUpper, handleReturnUrl, getAllParameter, getParameterByName, reloadPage} from './helper.js';
 */

export function initUpper(s){
    return s[0].toUpperCase() + s.substring(1, s.length);
}


// debug
let consolePrint = 1;

export function consolePrint(tar){
    if(consolePrint == 1) {
        console.log(tar.toString())
    }
}


// Shopping cart && Confirmation
export function outputCartInfo(){

}




// Single movie && Single star
export function handleReturnUrl(lastParam){
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


export function getAllParameter(){
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

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
export function getParameterByName(target) {
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



export function reloadPage() {
    if(location.href.indexOf('#reloaded')==-1){
        location.href=location.href+"#reloaded";
        location.reload();
    }
}