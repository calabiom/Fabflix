/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
	console.log(resultDataString);

	/*INTERESTING THING HAPPENED HERE...slight diff between this and the example*/

    resultDataJson = JSON.parse(JSON.stringify(resultDataString));

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("dashboard.html");
    } else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        if (resultDataJson["status"] === "fail") {
	        console.log("show error message");
	        console.log(resultDataJson["message"]);
	        $("#login_error_message").text(resultDataJson["message"]);
	        grecaptcha.reset();
        }
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.post(
        "api/employee-login",
        // Serialize the login form to the data sent by POST request
        $("#login_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString)
    );
}

// Bind the submit action of the form to a handler function
$("#login_form").submit((event) => submitLoginForm(event));

