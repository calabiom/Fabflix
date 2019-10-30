
var allPastQueries = {};

///*
// * This function is called by the library when it needs to lookup a query.
// * 
// * The parameter query is the query string.
// * The doneCallback is a callback function provided by the library, after you get the
// *   suggestion list from AJAX, you need to call this function to let the library know.
// */
function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	
	// TODO: if you want to check past query results first, you can do it here
	if (allPastQueries[query] != null){
		
		console.log("Query is familiar, retrieving suggestions from cache");
		
		var pastJson = allPastQueries[query];
		
		count = 0;
		for (var key in pastJson){
			count++;
		}
		
		for (i = 0; i < count; i++){
			console.log("Movie " + i + ": " + pastJson[i]["value"] + " with id: " + pastJson[i]["data"]["id"]);
		}
		
		doneCallback( { suggestions: pastJson } );
		
	} else {
	// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
	// with the query data
	
		console.log("sending AJAX request to backend Java Servlet")
		
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "autocomplete-search?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		}) 
	}
}


///*
// * This function is used to handle the ajax success callback function.
// * It is called by our own code upon the success of the AJAX request
// * 
// * data is the JSON data string you get from your Java Servlet
// * 
// */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	allPastQueries[query] = jsonData;
	
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"])
	
	location.href = "single-movie.html?id=" + suggestion["data"]["id"];
}


// * This statement binds the autocomplete library with the input box element and 
// *   sets necessary parameters of the library.
// */
//// $('#autocomplete') is to find element by the ID "autocomplete"
$('#advanced-search-bar').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    minChars: 3,
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
	
	 location.href = "index.html?title=" + query + "&year=&director=&star=&listing=10&sortingOption=2&page=1&fts=t";
	

}

$('#advanced-search-bar').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		
		handleNormalSearch($('#advanced-search-bar').val())
		
		console.log("here");
	}
})