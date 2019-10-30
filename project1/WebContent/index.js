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

function refreshBrowsing(option){
	console.log("Time to refresh the page with the same results...");

    let url = window.location.href;

    let title = getParameterByName("title");
    let year = getParameterByName("year");
    let director = getParameterByName("director");
    let star = getParameterByName("star");
	
    var sortingOption, listingNum;
	
	if (option.toString() === "sorting"){
		console.log("...SORTED");	    
	    
		listingNum = getParameterByName('listing');
	    
	    sortingOption = document.getElementById('sorting-options').value;
	}
	
	if (option.toString() === "listing"){
		console.log("...with different LISTINGS per page");
		    
	    sortingOption = getParameterByName('sort');
	    listingNum = document.getElementById('listing-options').value;
	}
	    
	url = "index.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
		+ "&listing=" + listingNum + "&sort=" + sortingOption + "&page=1";

	window.location = url;
	
}

function createPrevNext(resultData){
	console.log("here in cPN");
	
	let title = getParameterByName("title");
    let year = getParameterByName("year");
    let director = getParameterByName("director");
    let star = getParameterByName("star");
	let listingNum = getParameterByName("listing");
	let sortingOption = getParameterByName("sort");
	let pageNum = getParameterByName("page");

	let pagerElement = jQuery('#btn-display');
//    let previousBtnElement = jQuery('#previous-btn');
//   	let nextBtnElement = jQuery('#next-btn');

    if (pageNum > 1){ // prev
    	console.log("here in PREV");

    	let newPageNum = parseInt(pageNum) - 1;
    	let newPageNumString = newPageNum.toString();
    	
    	let hrefHTML = "<li class=\"previous\"><a href=\"index.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
    		"&listing=" + listingNum + "&sort=" + sortingOption + "&page=" + newPageNumString + "\">Previous</a></li>";
    	pagerElement.append(hrefHTML);
    }

    if (resultData.length == listingNum){ // next
    	console.log("here in NEXT");

    	let newPageNum = parseInt(pageNum) + 1;
    	let newPageNumString = newPageNum.toString();
    	let hrefHTML = "<li class=\"next\"><a href=\"index.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
    		"&listing=" + listingNum + "&sort=" + sortingOption + "&page=" + newPageNumString + "\">Next</a></li>";
    	pagerElement.append(hrefHTML);
	}
}


function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");
    console.log("ResultData size: " + resultData.length.toString());

    
    let title = getParameterByName("title");
    let year = getParameterByName("year");
    let director = getParameterByName("director");
    let star = getParameterByName("star");
    
    let searchHeader = jQuery("#Searched");
    
    if(title == null && year == null && director == null && star == null){
    	searchHeader.append("Search results will be displayed below...");
    } else if (title == "" && year == "" && director == "" && star == ""){
    	searchHeader.append("Displaying top rated movies...");
    } else {
    	searchHeader.append("Results for Movie Title: < " + title + 
    		" >   Movie Year: < " + year +
    		" >   Movie Director: < " + director +
    		" >	  Star: < " + star + " > ");
    }
    
    let movieTableBodyElement = jQuery("#movie_table_body");
    
    for (let i = 0; i < Math.min(getParameterByName("listing"), resultData.length); i++) {

    	let starROW = "";
    	let data = "";
    	data += "" + resultData[i]['movie_stars'] + "";
    	let arrStars = data.split(", ");
    	
    	let j = 0;
    	for(; j < arrStars.length - 1; j++){
    		let starInfo = arrStars[j].split(";");
    		starROW += '<a href="single-star.html?id=' + starInfo[0] +  '"title = "Click for Movie Star information">'
    		+ starInfo[1] + ", " + '</a>';
    	}
    	let starInfo = arrStars[j].split(";");
		starROW += 	'<a href="single-star.html?id=' + starInfo[0] + '"title = "Click for Movie Star information">' + starInfo[1] + '</a>';
    	
///////////////////////////////// Genres Hyperlink 
		let genreROW = "";
    	let temp = "";
    	temp += "" + resultData[i]['movie_genres'] + "";
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
		
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '"title = "Click for Movie information">'
            + resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + genreROW + "</th>";
        rowHTML += "<th>" + starROW + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
	    rowHTML += "<th><button class=\"btn btn-success\" onclick=\"handleCartRequest('" + resultData[i]["movie_id"] + "', '"+ resultData[i]["movie_title"] + "')\">Add Movie</button></th>";        
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
    createPrevNext(resultData);
}


function handleCartRequest(movieId, movieTitle){
    console.log("pressed Add to Cart");

    $.post(
        "api/shopping-cart?id=" + movieId + "&title=" + movieTitle + "&option=" + "add",
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


jQuery.ajax({
    dataType: "json", 
    method: "GET", 
    url: "api/movies?title=" + encodeURIComponent(getParameterByName("title")) + "&year=" + getParameterByName("year") + "&director=" + encodeURIComponent(getParameterByName("director"))
    	+ "&star=" + encodeURIComponent(getParameterByName("star")) + "&listing=" + getParameterByName('listing') + "&sort=" + getParameterByName('sort') 
    	+ "&page=" + getParameterByName('page') + "&fts=" + getParameterByName("fts"),
    success: (resultData) => handleMovieResult(resultData) 
});