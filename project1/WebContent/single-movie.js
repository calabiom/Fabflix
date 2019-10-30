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

    console.log("handleResult: populating single movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieElement = jQuery("#movie_name_header");

    // append two html <p> created to the h3 body, which will refresh the page
    movieElement.append("Movie: " + resultData[0]["movie_title"] + " (" +resultData[0]["movie_year"] +")");
//    let starInfoElement = jQuery("#single_movie_table_info");

    // append two html <p> created to the h3 body, which will refresh the page
//    starInfoElement.append("<p>Movie id: " + resultData[0]["movie_id"] + "</p>");

    console.log("handleResult: populating single movie table from resultData");

    let starROW = "";
	let data = "";
	data += "" + resultData[0]['movie_stars'] + "";
	let arrStars = data.split(", ");
	let j = 0;
	for(; j < arrStars.length - 1; j++){
		let starInfo = arrStars[j].split(";");
		starROW += '<a href="single-star.html?id=' + starInfo[0] + '"title = "Click for Movie Star information">' 
		+ starInfo[1] + ", " + '</a>';
	}
	let starInfo = arrStars[j].split(";");
	starROW += '<a href="single-star.html?id=' + starInfo[0] + '"title = "Click for Movie Star information">' + starInfo[1] + '</a>';
	
///////////////////////////////// Genres Hyperlink 
	let genreROW = "";
	let temp = "";
	temp += "" + resultData[0]['movie_genres'] + "";
	let arrGenres = temp.split(", ");
	
	let k = 0;
	for(; k < arrGenres.length - 1; k++){
		let genreInfo = arrGenres[k].split(";");
		genreROW += '<a href="browse-results.html?id=' + genreInfo[0] +  '&genre=true&listing=10&sort=0&page=1" title = "Click to browse genre">'
		+ genreInfo[1] + ", " + '</a>';
	}
	let genreInfo = arrGenres[k].split(";");
	genreROW +='<a href="browse-results.html?id=' + genreInfo[0] +  '&genre=true&listing=10&sort=0&page=1" title = "Click to browse genre">'
		+ genreInfo[1] + '</a>';
	/////////////////////////////////
	
    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
//    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[0]["movie_id"] + "</th>";
        rowHTML += "<th>" + resultData[0]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";
        rowHTML += "<th>" + genreROW + "</th>";
        rowHTML += "<th>" + starROW + "</th>";
        rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
	    rowHTML += "<th><button class=\"btn btn-success\" onclick=\"handleCartRequest('" + resultData[0]["movie_id"] + "', '"+ resultData[0]["movie_title"] + "')\">Add Movie</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
//    }
}

function handleCartRequest(movieId, movieTitle){
    console.log("pressed Add to Cart");

    $.post(
        "api/shopping-cart?id=" + movieId  + "&title=" + movieTitle + "&option=" + "add",
        // Serialize the cart form to the data sent by POST request
        (resultDataString) => printCartResults(resultDataString)
    );
}


function printCartResults(resultDataString){
    resultDataJson = JSON.parse(resultDataString);
    
    console.log(resultDataJson["status"]);
    console.log("Previous items were: " + resultDataJson["previousItems"]);
    console.log("Current user's cart is: " + resultDataJson["userCart"]);

    alert("Movie has been added to your shopping cart!");

}

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});