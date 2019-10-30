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

function handleResult(resultData) {

    console.log("handleResult: populating db's metadata from resultData");
	
    resultDataJson = JSON.parse(JSON.stringify(resultData));
    if (resultDataJson[resultData.length - 1]["status"] == "fail") {
    	alert("ERROR: Movie you tried adding already exists! No changes were made...");
    }
    if (resultDataJson[resultData.length - 1]["status"] == "success") {
        $("#login_error_message").text(resultDataJson[[resultData.length - 1]]["message"]);
    	alert("SUCCESFUL: You added a new movie to the database! Procedure will be shown below");
    }
    if (resultDataJson[resultData.length - 1]["status"] == "successaddstar") {
    	alert(resultDataJson[[resultData.length - 1]]["message"]);
    }
    if (resultDataJson[resultData.length - 1]["status"] == "failparameters") {
    	alert(resultDataJson[[resultData.length - 1]]["message"]);
    }
    let movie_title = getParameterByName("movieTitle");
    let movie_year = getParameterByName("movieYear");
    let movie_director = getParameterByName("movieDirector");
    let star_name = getParameterByName("starName");
    let star_dob = getParameterByName("starDOB");
    let genre_name = getParameterByName("genreName");
    
    let searchHeader = jQuery("#Searched");
    searchHeader.append("Previous Values Used: Movie Title: < " + movie_title + " > Movie Year: < " + movie_year + " > Movie Director: < " + 
    		movie_director + " > Star Name: < " + star_name + " > Star Birthyear: < " + star_dob + " > Genre: < " + genre_name + " >");
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length - 1; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["data_type"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}




//function printCartResults(resultDataString){
//    resultDataJson = JSON.parse(resultDataString);
//    alert("New information has been added to Fablix's Database!");
//
//}

// Get id from URL
//let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/dashboard?movieTitle=" + getParameterByName("movieTitle") + "&movieYear="+ getParameterByName("movieYear") +
    	"&movieDirector=" + getParameterByName("movieDirector") + "&starName=" + getParameterByName("starName") + 
    	"&starDOB=" + getParameterByName("starDOB") + "&genreName=" + getParameterByName("genreName") + 
    	"&addMovie=" + getParameterByName("addMovie") + "&addStar=" + getParameterByName("addStar"),
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});