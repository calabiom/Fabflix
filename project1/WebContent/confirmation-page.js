function handleResult(resultData) {

    console.log("handleResult: populating shopping cart from resultData");

    let movieTableBodyElement = jQuery("#single_movie_table_body");
   
    for (let i = 0; i <  resultData.length; i++) {
    	let rowHTML = "";
    	rowHTML += "<tr>";
    	rowHTML += "<th>" + resultData[i]["sales_id"] + "</th>";
    	rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
    	rowHTML += "<th>" + resultData[i]["copies"] + "</th>";
    	rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    	
    }
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/confirmation", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});