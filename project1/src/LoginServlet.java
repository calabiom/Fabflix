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
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userAgent = request.getHeader("User-Agent");
        System.out.println("recieved login request");
        System.out.println("userAgent: " + userAgent);
        
        // this makes sure it only recaptcha for browser 
/*        if (userAgent != null && !userAgent.contains("Android")) {
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
    	}*/
        
    	String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("Username: " + username);
        System.out.println("Password: "+ password);
        
		response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            if (ds == null)
                out.println("ds is null.");
            
	        Connection dbcon = ds.getConnection();
	        String query = "SELECT * FROM customers as c WHERE c.email = ?;"; 
	         if (dbcon == null)
	                out.println("dbcon is null.");

			PreparedStatement statement = dbcon.prepareStatement(query);

			statement.setString(1, username);
			
	        // Perform the query
	        ResultSet rs = statement.executeQuery();
            JsonObject responseJsonObject = new JsonObject();
            System.out.println("Made the statement: " + statement.toString());
	        if (!rs.isBeforeFirst() ) {    
	            System.out.println("No email"); 

	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "Whoops! Incorrect Username or Password");
	                   
	        } else {
	            
	    		boolean success = false;          
	            rs.next();
    			String encryptedPassword = rs.getString("password");
    			System.out.println("E-Password: " + encryptedPassword);
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	            
    			///////////////////// unedit line on top and delete this below (this is because I need to encrypt passwords)
//	    			if(encryptedPassword.equals(password)) {
//		            	success = true;
//		            }
//		            else {
//		            	success = false;
//		            }
//		            System.out.println("Just checked the password, and it's " + success);
	            ///////////////////// delete above

	            if (success) {
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
        	System.out.println("Error: " + e.getMessage());
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
	        
        /**
         * This example only allows username/password to be anteater/123456
         * In real world projects, you should talk to the database to verify username/password
         */
	        
/*	        
        if (username.equals("anteater") && password.equals("123456")) {
            // Login succeeds
            // Set this user into current session
            String sessionId = ((HttpServletRequest) request).getSession().getId();
            Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
            request.getSession().setAttribute("user", new User(username));

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

            response.getWriter().write(responseJsonObject.toString());
        } else {
            // Login fails
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            if (!username.equals("anteater")) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }
            response.getWriter().write(responseJsonObject.toString());
        }
    }
}
*/