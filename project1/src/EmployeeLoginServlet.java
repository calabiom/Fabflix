import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


/**
 * Servlet implementation class EmployeeLoginServlet
 */
@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
    	
    	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();

            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Whoops! Recaptcha Verification Error :(");
            out.write(responseJsonObject.toString());
            
            return;
        }
    	
    	String username = request.getParameter("username");
        String password = request.getParameter("password");

        
		response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");     
	        String query = "SELECT * FROM employees as e WHERE e.email = ?;"; // and c.password = ?;";

			PreparedStatement statement = dbcon.prepareStatement(query);

			statement.setString(1, username);
			//statement.setString(2, password);
			
	        // Perform the query
	        ResultSet rs = statement.executeQuery();
	
	        System.out.println("after exec");

            JsonObject responseJsonObject = new JsonObject();
            
	        if (!rs.isBeforeFirst() ) {    
	            System.out.println("No email"); 

	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "Whoops! Incorrect Username or Password");
	                   
	        } else {
	           // System.out.println("right email, but wb the password?");
	            
	        	System.out.println("right email, for now?");
	        	
//	            request.getSession().setAttribute("employee", new Employee(username));
//	            request.getSession().setAttribute("user", new User(username));  ///// important!!!

	            
	    		boolean success = false;
	            rs.next();
    			String encryptedPassword = rs.getString("password");
	            System.out.println("Here's the encrypted password: " + encryptedPassword);
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	            System.out.println("Just checked the password, and it's " + success);
	            

	            if (success) {
	            	request.getSession().setAttribute("employee", new Employee(username));
		            request.getSession().setAttribute("user", new User(username));
	
		            responseJsonObject.addProperty("status", "success");
		            responseJsonObject.addProperty("message", "success");
	            } else {
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "Whoops! Incorrect Username or Password");	            	
	            }

	        }
	        	        
            out.write(responseJsonObject.toString());
            System.out.println(responseJsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            } // end while
        } 
          catch (Exception e) {
    	
            System.out.println("500 ERROR");

	//    	write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
	
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        }
    }

}
