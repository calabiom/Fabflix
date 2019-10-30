import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.*; 
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
 * Servlet implementation class MovieServlet
 */
@WebServlet(name = "MovieServlet", urlPatterns = "/api/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		String listingNum = request.getParameter("listing");
		String sortOption = request.getParameter("sort");
		String pageNum = request.getParameter("page");
		String fullTextStatus = request.getParameter("fts");
		System.out.println("Title: " + title);
		System.out.println("year: " + year);
		System.out.println("director: " + director);
		System.out.println("Listing: " + listingNum);
		System.out.println("sortoption: " + sortOption);
		boolean titleQuery = true;
		boolean yearQuery = true;
		boolean directorQuery = true;
		boolean starQuery = true;
		boolean fullTextSearch = false;
		
		ArrayList<String> arrQuery = new ArrayList<String>(); 
		
//		// ******** Note: If parameters are empty then eliminate extra queries #saveTime 
		if(title.equals("")) {
			titleQuery = false;
		}
		if(year.equals("")) {
			yearQuery = false;	
		}
		if(director.equals("")) {
			directorQuery = false;	
		}
		if(star.equals("")) {
			starQuery = false;	
		}
		if(!fullTextStatus.equals("null")) {
			fullTextSearch = true;
			System.out.println("make FTS true");

		}

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource

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
        	System.out.println("Connected to data source");
        	
            String query = "SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.id, ';', g.name SEPARATOR ', ') AS genre, GROUP_CONCAT(DISTINCT s.id, ';', s.name SEPARATOR ', ') AS stars, r.rating " +
            		" FROM movies m, ratings r, genres g, genres_in_movies gm, stars s, stars_in_movies sm " +
            		" WHERE m.id = r.movieId and g.id = gm.genreId and m.id = gm.movieId and s.id = sm.starId and m.id = sm.movieId ";
            
            //titleQuery
            int arguments = 0;
            if (titleQuery) {
            	arrQuery.add("title");
            	if(title.contains("%") || title.contains("_")) {
        			query += " and m.title LIKE ?";
        		} else {
        			
        			if (fullTextSearch) { // (yearQuery == false && directorQuery == false && starQuery == false) { // Movie title is the only thing being searched
        			//	fullTextSearch = true;
        				query += " and MATCH(m.title) AGAINST (? IN BOOLEAN MODE)";
        			} else {
            			query += " and m.title LIKE CONCAT('%', ?, '%')";
        			}
        			
        		}
            	arguments++;
            }
            
            //yearQuery
            if(yearQuery) {
            	arrQuery.add("year");
            	query += " and m.year = ?";
            	arguments++;
            }
            System.out.println("yearQuery...");
            //directorQuery
            if(directorQuery) {
            	arrQuery.add("director");
            	if(director.contains("%") || director.contains("_")) {
        			query += " and m.director LIKE ?";
        		} else {
        			query += " and m.director LIKE CONCAT('%', ?, '%')";}
            	arguments++;
            }
            System.out.println("directorQuery...");
            //starQuery
            if(starQuery) {
            	arrQuery.add("star");
            	if(star.contains("%") || star.contains("_")) {
        			query += " and s.name LIKE ?";
        		} else {
        			query += " and s.name LIKE CONCAT('%', ?, '%')";} 
                arguments++;
            }
            query += " GROUP BY m.title, m.id ";
            System.out.println("starQuery...");
            //+ orderQuery + amountQuery;
            //orderQuery
            if (sortOption.equals("0")) {				
    			query += "ORDER BY m.title ASC ";
    		} 
    		if (sortOption.equals("1")) {
    			query += "ORDER BY m.title DESC ";
    		} 
    		if (sortOption.equals("2")) {
    			query += "ORDER BY r.rating DESC ";
    		} 
    		if (sortOption.equals("3")) {
    			query += "ORDER BY r.rating ASC ";
    		}
    		System.out.println("sortOption...");
    		//amountQuery
    		int offsetMultiplier = Integer.parseInt(pageNum) - 1;
    		int offset = offsetMultiplier * Integer.parseInt(listingNum);
    		query += " LIMIT ? OFFSET ?";
    		System.out.println("Before prepare statement");
    		PreparedStatement statement = dbcon.prepareStatement(query);
    		System.out.println("After prepare statement");
    		if(titleQuery) {
    			
    			if (fullTextSearch) {
    				String[] titleWordsFromSearch = title.split(" ");
    				
    				String wordsForFullTextSearch = "";
    				for (int i = 0; i < titleWordsFromSearch.length; i++) {
    					wordsForFullTextSearch += "+" + titleWordsFromSearch[i] + "* ";
    				}
    				
    				statement.setString(1, wordsForFullTextSearch);
    				
    			} else {
    				statement.setString(arrQuery.indexOf("title") + 1, title);
    			}
    		}
    		if(yearQuery) {
    			statement.setInt(arrQuery.indexOf("year") + 1, Integer.parseInt(year));
    		}
    		if(directorQuery) {
    			statement.setString(arrQuery.indexOf("director") + 1, director);
    		}
    		if(starQuery) {
    			statement.setString(arrQuery.indexOf("star") + 1, star);
    		}
    		statement.setInt(arguments + 1, Integer.parseInt(listingNum));
    		statement.setInt(arguments + 2, offset);
            // Perform the query
    		System.out.println("Before execution");
    		ResultSet rs = statement.executeQuery();
    		System.out.println("After execution");

            JsonArray jsonArray = new JsonArray();
//
//            // Iterate through each row of rs
            while (rs.next()) {
            	String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genre");
                String movie_stars = rs.getString("stars");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
//            
//            // write JSON string to output
            out.write(jsonArray.toString());
            System.out.println(jsonArray.toString());
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
			System.out.println("500 Error");
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();
	}
}

