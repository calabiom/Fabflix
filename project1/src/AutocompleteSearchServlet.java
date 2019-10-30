

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class AutocompleteSearchServlet
 */
@WebServlet(name = "AutocompleteSearchServlet", urlPatterns = "/autocomplete-search")
public class AutocompleteSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AutocompleteSearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

		
		try {
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			// get the query string from parameter
			String query = request.getParameter("query");
			
			// return the empty json array if query is null or empty
			if (query == null || query.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			
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
            
            String stringToQuery = "SELECT id, title from movies WHERE MATCH(title) AGAINST(? in BOOLEAN MODE) LIMIT 10;";
            
    		PreparedStatement statement = dbcon.prepareStatement(stringToQuery);
    		
			String[] titleWordsFromSearch = query.split(" ");
			
			String wordsForFullTextSearch = "";
			for (int i = 0; i < titleWordsFromSearch.length; i++) {
				wordsForFullTextSearch += "+" + titleWordsFromSearch[i] + "* ";
			}
			
			statement.setString(1, wordsForFullTextSearch);

			// search on superheroes and add the results to JSON Array
			// this example only does a substring match
			// TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
            	String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");

                // Create a JsonObject based on the data we retrieve from rs
                jsonArray.add(generateJsonObject(movie_id, movie_title));
            }
            
//            // write JSON string to output
            out.write(jsonArray.toString());
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
        } catch (Exception e) {
    //    	write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
 
	}
	
	/*
	 * Generate the JSON Object from hero to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "heroID": 11 }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String id, String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", name);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("id", id);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}

}
