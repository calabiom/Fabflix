import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
/**
 * Servlet implementation class CheckoutInformationServlet
 */
@WebServlet(name = "CheckoutInformationServlet", urlPatterns = "/api/checkout")
public class CheckoutInformationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ccId = request.getParameter("ccId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String expDate = request.getParameter("expDate");
		
		response.setContentType("application/json"); // Response mime type
		PrintWriter out = response.getWriter();
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
            if (dbcon == null)
                out.println("dbcon is null.");		     
	        String query = "SELECT * FROM creditcards as c WHERE "
	        		+ " c.id = ? and c.firstName = ? and c.lastName = ? and c.expiration = ?";

			PreparedStatement statement = dbcon.prepareStatement(query);

			statement.setString(1, ccId);
			statement.setString(2, firstName);
			statement.setString(3, lastName);
			statement.setString(4, expDate);
			
	        ResultSet rs = statement.executeQuery();
	
	        System.out.println("after exec");

            JsonObject responseJsonObject = new JsonObject();
            
            if (!rs.isBeforeFirst() ) {    
	            System.out.println("No data"); 

	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "Whoops! Incorrect information...");
	                   
	        } else {
	            System.out.println("succ login"); 
	            
	            // request.getSession().setAttribute("user", new User(username));
	            //going to need to add the new statement to sales
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");

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
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}

}
