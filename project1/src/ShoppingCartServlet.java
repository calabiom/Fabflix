import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession();
           
    	User customer = (User) session.getAttribute("user");
    	
    	Map<String, Integer> customerCart = customer.createCartCopy();

    	JsonArray jsonArray = new JsonArray();

    	for (Map.Entry<String, Integer> entry : customerCart.entrySet()) {
    	    String id = entry.getKey();
    	    int copies = entry.getValue();
    	    
    	    System.out.println(id + " " + Integer.toString(copies));
    	    
    		JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("movie_id", id);
			jsonObject.addProperty("movie_title", customer.getMovieTitle(id));
			jsonObject.addProperty("copies", Integer.toString(copies));

			jsonArray.add(jsonObject);
		}
		
        response.setStatus(200);

        // write all the data into the jsonObject
        response.getWriter().write(jsonArray.toString());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
 
		String movie_item_id= request.getParameter("id");
		String movie_title = request.getParameter("title");
		String modify_option = request.getParameter("option");
		String amount = request.getParameter("amount");
		
		

        System.out.println(movie_item_id + " " + movie_title + " " + amount);

        HttpSession session = request.getSession();
        
        User customer = (User) session.getAttribute("user");

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        if (modify_option.equals("add")) {
	        // get the previous items in a ArrayList
	        if (previousItems == null) {
	            previousItems = new ArrayList<>();
	            previousItems.add(movie_item_id);
	            session.setAttribute("previousItems", previousItems); 
	            
	            customer.addToCart(movie_item_id);
	            customer.updateMovieList(movie_item_id, movie_title);
	
	        } else {
	            // prevent corrupted states through sharing under multi-threads
	            // will only be executed by one thread at a time
	            synchronized (previousItems) {
	                previousItems.add(movie_item_id);
	                customer.addToCart(movie_item_id);
	                customer.updateMovieList(movie_item_id, movie_title);
	
	            }
	        }
        }
        
        if (modify_option.equals("modify")) {
            customer.modifyQuantity(movie_item_id, Integer.parseInt(amount));        	        	
        }
        
        if (modify_option.equals("delete")) {
            int result = customer.removeCartItem(movie_item_id);
            System.out.println("removal of item successful?: " + Integer.toString(result));
        }
        
        session.setAttribute("user", customer);
        
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("status", "success");
        responseJsonObject.addProperty("previousItems", String.join(",", previousItems));
        responseJsonObject.addProperty("userCart", customer.printCart());

        
        response.getWriter().write(responseJsonObject.toString());
        
	}

}
