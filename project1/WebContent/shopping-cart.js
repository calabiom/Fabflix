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

    console.log("handleResult: populating shopping cart from resultData");

    let movieTableBodyElement = jQuery("#single_movie_table_body");
    
    var checkoutBtnElement = document.getElementById("checkout-btn");
    if (resultData.length > 0){
    	checkoutBtnElement.setAttribute("style", "visibility: visible;")
    }

    // Concatenate the html tags with resultData jsonObject to create table rows
//    for (let i = 0; i < Math.min(10, resultData.length); i++) {
    for (let i = 0; i <  resultData.length; i++) {

	    let rowHTML = "";
	        rowHTML += "<tr>";
	        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
	        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
	        rowHTML += "<th><form class=\"form-inline\" >";
	        
	        rowHTML +=  "<input type=\"number\" name=\"quantity\" id='"+ resultData[i]["movie_id"]  + "-quantity' class=\"form-control input-number\" value='" + resultData[i]["copies"] + "' min=\"0\"></form></th>";
//	        rowHTML +=  "<input type=\"hidden\" name=\"id\" value='" + resultData[i]["movie_id"]  + "'>";
//	        rowHTML +=  "<input type=\"hidden\" name=\"title\" value='" + resultData[i]["movie_title"] + "'>";
//
//	        rowHTML += "<button class=\"btn btn-success\" onclick=\"handleCartRequest('" + resultData[i]["movie_id"] + "', '"+ resultData[i]["movie_title"] + "');return false;\">Update Quantity</button></form></th>";

	        rowHTML += "<th><button class=\"btn btn-success\" onclick=\"handleCartRequest('" + resultData[i]["movie_id"] + "', '"+ resultData[i]["movie_title"] + "', 'modify')\">Yes</button></th>";

	        rowHTML += "<th><button class=\"btn btn-danger\" onclick=\"handleCartRequest('" + resultData[i]["movie_id"] + "', '"+ resultData[i]["movie_title"] + "', 'delete')\">Yes</button></th>";

		//    rowHTML += "<th><button class=\"btn btn-success\" onclick=\"handleCartRequest('" + resultData[i]["copies"] + "')\">" + resultData[i]["copies"] + "</button></th>";
	        rowHTML += "</tr>";
	
	        // Append the row created to the table body, which will refresh the page
	        movieTableBodyElement.append(rowHTML);

    }
}

function handleCartRequest(movieId, movieTitle, option){
	let fullId= movieId + "-quantity";
	
	let amount = document.getElementById(fullId).value;
	
	console.log(amount)
    
	$.post(
            "api/shopping-cart?id=" + movieId + "&title=" + movieTitle + "&option=" + option + "&amount=" + amount.toString(),
            // Serialize the cart form to the data sent by POST request
            (resultDataString) => handleCartInfo(resultDataString)
        );
	
}

function handleCartInfo(resultDataString){
    resultDataJson = JSON.parse(resultDataString);

    
    console.log(resultDataJson["status"]);
    console.log("Previous items were: " + resultDataJson["previousItems"]);
    console.log("Current user's cart is: " + resultDataJson["userCart"]);
    
    alert("Updated movie quantity!");
    window.location.reload();
	
}



// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/shopping-cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
