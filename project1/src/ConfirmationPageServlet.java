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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Map;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

/**
 * Servlet implementation class ConfirmationPageServlet
 */
@WebServlet(name = "ConfirmationPageServlet", urlPatterns = "/api/confirmation")
public class ConfirmationPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json"); // Response mime type
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
    	User customer = (User) session.getAttribute("user");
    	Map<String, Integer> customerCart = customer.createCartCopy();

    	JsonArray jsonArray = new JsonArray();
    	try {
            // Get a connection from dataSource
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
            
            // Find Customer ID
            String queryCID = "SELECT c.id as customerId FROM customers c " + 
				" WHERE c.email = ?";
            PreparedStatement statement = dbcon.prepareStatement(queryCID);
            statement.setString(1, customer.getUsername());
            ResultSet rs = statement.executeQuery();
            String customer_id = "";
            while(rs.next()) {
            	customer_id = rs.getString("customerId");                
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        	LocalDateTime now = LocalDateTime.now();
            /// update the table with information about sales  
            for (Map.Entry<String, Integer> entry : customerCart.entrySet()) {
            	String id = entry.getKey();
            	int copies = entry.getValue();
            	for(int i = 0; i < copies; i++) { // once for each copy of the movie 
            		String query ="INSERT INTO sales(customerId, movieId, saleDate) "
					+ "VALUES(?, ?, ?)";
            		PreparedStatement update = dbcon.prepareStatement(query);
            		
            		update.setInt(1,  Integer.parseInt(customer_id));
            		update.setString(2,  id);
            		update.setString(3, dtf.format(now));
					int retID = update.executeUpdate();
					System.out.println("Inserted into DB");
            	}
            }
            /// put sales information onto the front end 
            
            /// get sales id 
            for (Map.Entry<String, Integer> pair : customerCart.entrySet()) {
        	    String id = pair.getKey();
        	    int copies = pair.getValue();
                String querySID = "SELECT s.id as salesId FROM sales s " + 
    				" WHERE s.customerId LIKE ? and s.movieId LIKE ? and s.saleDate LIKE ?"
    						+ "ORDER BY s.id DESC LIMIT ?";
                PreparedStatement queryTwo = dbcon.prepareStatement(querySID);
                queryTwo.setInt(1, Integer.parseInt(customer_id));
                queryTwo.setString(2,  id);
                queryTwo.setString(3, dtf.format(now));
                queryTwo.setInt(4, copies);
                ResultSet resultSet = queryTwo.executeQuery();
                String sID = "";
                while(resultSet.next()) {
                	sID += resultSet.getString("salesId") + ", ";   
                }
                sID = sID.substring(0, sID.length() - 2);
        		JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("sales_id", sID);
    			jsonObject.addProperty("movie_title", customer.getMovieTitle(id));
    			jsonObject.addProperty("copies", Integer.toString(copies));

    			jsonArray.add(jsonObject);
    		}
            customer.clearCart();
    		
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
			response.setStatus(500);
    	}
		//////////////////////////////Information displayed

        response.setStatus(200);

        // write all the data into the jsonObject
        response.getWriter().write(jsonArray.toString());
	} 

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

}
