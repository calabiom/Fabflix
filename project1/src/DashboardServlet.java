
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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


@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
//	@Resource(name = "jdbc/moviedb")
//  private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get parameters from URL
		String movie_title = request.getParameter("movieTitle");
		String movie_year = request.getParameter("movieYear");
		String movie_director = request.getParameter("movieDirector");
		String star_name = request.getParameter("starName");
		String star_dob = request.getParameter("starDOB");
		String genre = request.getParameter("genreName");
		String is_movie = request.getParameter("addMovie");
		String is_star = request.getParameter("addStar");
		// booleans for testing
		boolean addMovie = false;
		boolean addStar = false;
		boolean existsDOB = true;
		boolean existsSN = true;
		boolean validParameters = true;
		// check if it is a movie addition
		if(is_movie.equals("true")) {
			addMovie = true;
		}
		// check is a star's birthyear exists in a movie addition
		if(addMovie&&star_dob.equals("")) {
			existsDOB = false;
		}
		// check if it is a star addition 
		if(is_star.equals("true")) {
			addStar = true;
		}
		// check if a star's birthyear exists
		if(addStar&&star_dob.equals("")) {
			existsDOB = false;
		}
		// check for valid parameters
		if(addMovie) {
			if(movie_title.equals("")||movie_year.equals("")||movie_director.equals("")||star_name.equals("")||genre.equals("")) {
				validParameters = false;
			}
		}
		if(addStar&&star_name.equals("")) {
			existsSN = false;
		}
		response.setContentType("application/json"); // Response mime type
		PrintWriter out = response.getWriter();
		
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
			// Construct a query with parameter represented by "?"
			String query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'moviedb'";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			//statement.setString(1, id);

			// Perform the query
			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {

				String table_name = rs.getString("TABLE_NAME");
				String column_name = rs.getString("COLUMN_NAME");
                String data_type = rs.getString("DATA_TYPE");
                
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("table_name", table_name);
				jsonObject.addProperty("column_name", column_name);
                jsonObject.addProperty("data_type", data_type);
                
                jsonArray.add(jsonObject);
			}
			
			// get id and add the last part by one then add it back
			String temp_starId = "";
			if(addStar&&!existsSN) {
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "failparameters");
	            responseJsonObject.addProperty("message", "ERROR: For star addition, you must have: star name, star birthyear (optional)");
	            jsonArray.add(responseJsonObject);
			}
			if(addStar) {
				String dob_query = "SELECT MAX(id) as topId FROM stars";
				Statement starIdStatement = dbcon.createStatement();
				ResultSet rs_dob = starIdStatement.executeQuery(dob_query);
				while(rs_dob.next()) {
					temp_starId = rs_dob.getString("topId");
				}
				int numbersId = Integer.parseInt(temp_starId.substring(2));
				numbersId++;
				temp_starId = "nm" + numbersId;
			}
			// star addition with no birthyear
			if(addStar&&existsSN&&!existsDOB) {
				String queryUpdate ="INSERT INTO stars (id, name) VALUES(?, ?);";
				PreparedStatement update = dbcon.prepareStatement(queryUpdate);
				update.setString(1, temp_starId);
				update.setString(2, star_name);
				update.executeUpdate();
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "successaddstar");
	            responseJsonObject.addProperty("message", "SUCCESFUL: You added new star with name!");
	            jsonArray.add(responseJsonObject);
			}
			// star addition with birthyear
			if(addStar&&existsSN&&existsDOB) {
				String queryUpdate ="INSERT INTO stars (id, name, birthYear) VALUES(?, ?, ?);";
				PreparedStatement update = dbcon.prepareStatement(queryUpdate);
				update.setString(1, temp_starId);
				update.setString(2, star_name);
				update.setInt(3, Integer.parseInt(star_dob));
				update.executeUpdate();
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "successaddstar");
	            responseJsonObject.addProperty("message", "SUCCESFUL: You added new star with name and birthyear!");
	            jsonArray.add(responseJsonObject);
			}
			// invalid parameters for movie addition
			if(addMovie&&!validParameters) {
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "failparameters");
	            responseJsonObject.addProperty("message", "ERROR: For movie addition, you must have: movie title, movie year, movie director, single star name, and genre!");
	            jsonArray.add(responseJsonObject);
			}
			// movie addition 
			if(addMovie&&validParameters) {
				// query to check how many movies exist with input provided
				int count = 0;
				String movieExists = "SELECT COUNT(*) as amount FROM movies WHERE title = ? and year = ? and director = ?";
				PreparedStatement movieExists_statement = dbcon.prepareStatement(movieExists);
				movieExists_statement.setString(1, movie_title);
				movieExists_statement.setInt(2, Integer.parseInt(movie_year));
				movieExists_statement.setString(3, movie_director);
				ResultSet movieExists_rs = movieExists_statement.executeQuery();
				while(movieExists_rs.next()) {
					count = movieExists_rs.getInt("amount");
				}
				// if statement to check if it does exist
				if(count == 0) {
//					// generate movie id 
					String query_movie_id = "{call make_movieID(?)}";
					CallableStatement get_movie_id = dbcon.prepareCall(query_movie_id);
					get_movie_id.registerOutParameter(1, Types.VARCHAR);
					get_movie_id.execute();
					// call stored procedure
					String add_movie_message = "";
					String add_movie_procedure = "{call add_movie(?, ?, ?, ?, ?, ?, ?, ?)}";
					CallableStatement stored_procedure = dbcon.prepareCall(add_movie_procedure);
					stored_procedure.registerOutParameter(1, Types.VARCHAR);
					stored_procedure.setString(2, get_movie_id.getString(1));
					stored_procedure.setString(3, movie_title);
					stored_procedure.setInt(4, Integer.parseInt(movie_year));
					stored_procedure.setString(5, movie_director);
					stored_procedure.setString(6, star_name);
					// the 7th parameter depends on user input
					if(addMovie&&existsDOB) {
						stored_procedure.setInt(7, Integer.parseInt(star_dob));
					}
					else {
						stored_procedure.setString(7,  null);
					}
					stored_procedure.setString(8, genre);
					stored_procedure.execute();
					add_movie_message = stored_procedure.getString(1);
					
					// movie was succesfully added, show the stored procedure to the user
					JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "success");
		            responseJsonObject.addProperty("message", stored_procedure.getString(1));
		            jsonArray.add(responseJsonObject);
				}
				else {
					// Movie exists show a message to the employee
					JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "Whoops! Movie already exists! No changes were made!");
		            jsonArray.add(responseJsonObject);
		            
				}
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
