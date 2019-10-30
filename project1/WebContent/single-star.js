/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    let starElement = jQuery("#star_header1");
    starElement.append("Movie Star: " + resultData[0]["star_name"]);
    
    console.log("handleResult: populating movie table from resultData");
    
    let movieROW = "";
	let data = "";
	data += "" + resultData[0]["star_movies"] + "";
	let arrMovies = data.split(", ");
	
	let j = 0;
	for(; j < arrMovies.length - 1; j++){
		let movieInfo = arrMovies[j].split(";");
		movieROW += '<a href="single-movie.html?id=' + movieInfo[0] +  '"title = "Click for Movie information">'
		+ movieInfo[1] + ", " + '</a>';
	}
	let movieInfo = arrMovies[j].split(";");
	movieROW += '<a href="single-movie.html?id=' + movieInfo[0] + '"title = "Click for Movie information">' + movieInfo[1] + '</a>';
    
    let movieTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[0]["star_name"] + "</th>";
        rowHTML += "<th>" + resultData[0]["star_dob"] + "</th>";
        rowHTML += "<th>" + movieROW + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});