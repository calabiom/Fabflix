function handleBrowseMenuResult(resultData) {
    console.log("handleGenreResult: populating genre table from resultData");

    let genreListElement = jQuery("#genre_display");
    let titleListElement = jQuery("#title_display");
    
    var titleArray = ["0","1","2","3","4","5","6","7","8", "9", "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"]
    
    for (let i = 0; i <  resultData.length; i++) {
    	
        let rowHTML = "";
        
        // <a href="#" class="list-group-item list-group-item-action">Cras justo odio</a>
        rowHTML += '<a href="browse-results.html?id=' + resultData[i]['genre_id'] + '&genre=true&listing=10&sort=0&page=1" class="list-group-item list-group-item-action">'
            + resultData[i]["genre_name"] +     // display genre_name for the link text
            '</a>';

        genreListElement.append(rowHTML);
    }
    
    for(let i = 0; i< titleArray.length; i++){
        let rowHTML = "";
        
        rowHTML += '<a href="browse-results.html?id=' + titleArray[i] + '&genre=false&listing=10&sort=0&page=1" class="list-group-item list-group-item-action">'
            + titleArray[i] +     // display genre_name for the link text
            '</a>';

        titleListElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browse-menu", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleBrowseMenuResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});