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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "BrowseResultsServlet", urlPatterns = "/api/browse-results")
public class BrowseResultsServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		String isGenre = request.getParameter("genre");
		String listingNum = request.getParameter("listing");
		String sortOption = request.getParameter("sort");
		String pageNum = request.getParameter("page");
		
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
			// Construct a query with parameter represented by "?"	
			
			
			String query = "SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.id, ';', g.name SEPARATOR ', ') AS genre, GROUP_CONCAT(DISTINCT s.id, ';', s.name SEPARATOR ', ') AS stars, r.rating "
					+ " FROM movies m, ratings r, genres g, genres_in_movies gm, stars s, stars_in_movies sm  "
					+ " WHERE m.id = r.movieId and g.id = gm.genreId and m.id = gm.movieId and s.id = sm.starId and m.id = sm.movieId ";
			
			if (isGenre.equals("true")) {
				query += " AND m.id IN (SELECT DISTINCT m.id FROM movies m, genres g, genres_in_movies gm WHERE g.id = gm.genreId and m.id = gm.movieId and g.id = ? ) GROUP BY m.id ";
			} else {
				query += " AND m.title LIKE CONCAT(?, '%') group by m.id ";
			}
			

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

			int offset_multiplier = Integer.parseInt(pageNum) - 1;
			int offset = offset_multiplier * Integer.parseInt(listingNum);
			
			query += " LIMIT ? OFFSET ?";
			
			
			System.out.println(query);
			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, id);
			statement.setInt(2, Integer.parseInt(listingNum));
			statement.setInt(3, offset);

			// Perform the query
			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
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
			
            // write JSON string to output
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
        } 
          catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}