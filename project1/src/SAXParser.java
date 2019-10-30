import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.sql.SQLException;


public class SAXParser {

	
	private MovieHandler movieHandler;
	private StarHandler starHandler;
	private CastHandler castHandler;
	
	ArrayList<Movie> movies; 					// ArrayList of Movie objects
	HashSet<String> genres; 					// HashSet of genre names
	ArrayList<Star> stars;						// ArrayList of Star objects
	Map<String, ArrayList<String>> mIdToStar;	// HashMap of movieId : Star name
	Map<String, Movie> mIdToTitle;
	
	ArrayList<String> addedMovies; 				// for ratings table
	Map<String, String> addedStars; 			// starId : name
	Map<String, Integer> addedGenres;


	public void parseXML() {
		System.out.println("Parsing mains243.xml");
		movieHandler = new MovieHandler();
	    movieHandler.runParser();
	    
		System.out.println("Parsing actors63.xml");
		starHandler = new StarHandler();
	    starHandler.runParser();
	    
		System.out.println("Parsing casts124.xml");
		castHandler = new CastHandler();
	    castHandler.runParser();
	    
	    movies = movieHandler.myMovies;
	    genres = movieHandler.allGenres;
	    mIdToTitle = movieHandler.idToMovie;
	    stars = starHandler.myStars;
	    mIdToStar = castHandler.movieIdToStar;
	    
	    
	}
	
	public void buildHashMap(){
		
		Map<String, String> movieIdToMovie = new HashMap<String, String>();
		Map<String, String> starIdToName = new HashMap<String, String>();
		Map<String, String> genreIdToName = new HashMap<String, String>();
		Map<String, String> genreNameToId = new HashMap<String, String>();
		Map<Integer, String> genreIdToMovieId = new HashMap<Integer, String>();

		
		addedMovies = new ArrayList<String>();
		addedStars = new HashMap<String, String>();
		addedGenres = new HashMap<String, Integer>();

		String maxStarId = "";
		int maxGenreId = 200;
		
        Connection conn = null;

        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");

            String movieQuery = "select * from movies";
            Statement movieStatement = conn.createStatement();
            ResultSet rsMovie = movieStatement.executeQuery(movieQuery);
            
            while (rsMovie.next()) {
            	movieIdToMovie.put(rsMovie.getString("id"), rsMovie.getString("title") + ":" + rsMovie.getString("year") + ":" + rsMovie.getString("director"));
            }
            
            String genreQuery = "select * from genres";
            Statement genreStatement = conn.createStatement();
            ResultSet rsGenre = genreStatement.executeQuery(genreQuery);
            
            while (rsGenre.next()) {
            	genreIdToName.put(rsGenre.getString("id"), rsGenre.getString("name"));
  //          	genreNameToId.put(rsGenre.getString("name"), rsGenre.getString("id"));

            }
            
            String gimQuery = "select * from genres_in_movies";
            Statement gimStatement = conn.createStatement();
            ResultSet rsGIM = gimStatement.executeQuery(gimQuery);
            
            int countGIM = 0;
            while (rsGIM.next()) {
            	genreIdToMovieId.put(countGIM, String.valueOf(rsGIM.getInt("genreId")) + ":" + rsGIM.getString("movieId"));
            	countGIM++;
            }
            
            String starQuery = "select * from stars";
            Statement starStatement = conn.createStatement();
            ResultSet rsStar = starStatement.executeQuery(starQuery);
            
            while (rsStar.next()) {
            	starIdToName.put(rsStar.getString("id"), rsStar.getString("name") + ":" + rsStar.getString("birthyear"));
            }
            
            String maxStarIdQuery = "select max(id) as s from stars";
            Statement maxStarIdStatement = conn.createStatement();
            ResultSet rsMaxStarId = maxStarIdStatement.executeQuery(maxStarIdQuery);
            
            while (rsMaxStarId.next()) {
            	maxStarId = rsMaxStarId.getString("s");
            }
            
            String maxGenreIdQuery = "select max(id) as g from genres";
            Statement maxGenreIdStatement = conn.createStatement();
            ResultSet rsMaxGenreId = maxGenreIdStatement.executeQuery(maxGenreIdQuery);
            
            while (rsMaxGenreId.next()) {
            	maxGenreId = rsMaxGenreId.getInt("g");
            }
            
            
    	    System.out.println("No of movies in db: " + movieIdToMovie.size());
    	    System.out.println("No of genres in db: " + genreIdToName.size());
    	    System.out.println("No of stars in db: " + starIdToName.size());
    	        	    
    	    conn.setAutoCommit(false);
            
            // inserting genres
    	    String sqlInsertGenre = "insert into genres (name) values (?);";
    	    PreparedStatement psInsertGenre = conn.prepareStatement(sqlInsertGenre);
    	    
    	    Iterator<String> genre_iter = genres.iterator();
    	    System.out.println("Adding to batch for INSERT GENRE");

    	    while (genre_iter.hasNext()) {
    	        String genreName = genre_iter.next();

    	    	if (!(genreIdToName.containsValue(genreName))) {
    	    		maxGenreId++;
    	    		addedGenres.put(genreName, maxGenreId);
    	    		psInsertGenre.setString(1, genreName);
    	    		psInsertGenre.addBatch();
    	    	} else {
    	    		// System.out.println("Trying to add '" + genreName + "', but it's already in the GENRES table. This will not be added.");
    	    	}
    	    }
    	    psInsertGenre.executeBatch();
    	    conn.commit();
    	    System.out.println("Executed batch for INSERT GENRE");
            
            // inserting movies
    	    
    	    String sqlInsertMovie = "insert into movies (id, title, year, director) values (?, ?, ?, ?);";
    	    PreparedStatement psInsertMovie = conn.prepareStatement(sqlInsertMovie);
    	    
    	    System.out.println("Adding to batch for INSERT MOVIE");
    	    
    	    for (Map.Entry<String, Movie> entry : mIdToTitle.entrySet()) {
    	    	String k = entry.getKey();
    	    	Movie m = entry.getValue();
    	    	String movieString = m.getTitle() + ":" + m.getYear() + ":" + m.getDirector();

    	    	if (!(movieIdToMovie.containsValue(movieString)) &&  !(movieIdToMovie.containsKey(m.getId()))) {

    	    		addedMovies.add(m.getId());
    	    		
    	    		psInsertMovie.setString(1, m.getId());
    	    		psInsertMovie.setString(2, m.getTitle());
    	    		psInsertMovie.setInt(3, m.getYear());
    	    		psInsertMovie.setString(4, m.getDirector());
    	    		psInsertMovie.addBatch();
    	    	} else {
    	   // 		System.out.println("Trying to add '" + m.getTitle() + "', but it's already in the MOVIES table. This will not be added.");
    	    	}
    	    }
    	    

    	    psInsertMovie.executeBatch();
    	    conn.commit();
    	    System.out.println("Executed batch for INSERT MOVIE");
    	    	

    	    // inserting ratings
    	    if (addedMovies.size() > 0) {
	      	    String sqlInsertRating = "insert into ratings (movieId, rating, numVotes) values (?, -1, 0);";
	    	    PreparedStatement psInsertRating = conn.prepareStatement(sqlInsertRating);
	    	    
	    	    System.out.println("Adding to batch for INSERT RATINGS");
	    	    
	    	    for (int i=0; i < addedMovies.size(); i++) {
	    	    	String movieId = addedMovies.get(i);
	
	    	    	psInsertRating.setString(1, movieId);
	    	    	psInsertRating.addBatch();
	    	    	
	    	    }
	    	    psInsertRating.executeBatch();
	    	    conn.commit();
	    	    System.out.println("Executed batch for INSERT STAR");
	    	    psInsertRating.close();

    	    }
            // inserting stars
    	    
      	    String sqlInsertStar = "insert into stars (id, name, birthyear) values (?, ?, ?);";
    	    PreparedStatement psInsertStar =  conn.prepareStatement(sqlInsertStar);
    	    
    	    System.out.println("Adding to batch for INSERT STAR");
    	    
    	    int newStarId = Integer.parseInt(maxStarId.substring(2)) + 1;
    	    
    	 //   boolean starAdded = false;
    	    
    	    for (int i=0; i < stars.size(); i++) {
    	    	Star s = stars.get(i);
    	    	
    	    	String starString = s.getName() + ":" + s.getBirthYear();
    	    	
    	    	if (!(starIdToName.containsValue(starString))) {
    	    		
    	   // 		starAdded = true;
    	    		
	    	    	String newStarIdString = "nm" + Integer.toString(newStarId);
	    	    	psInsertStar.setString(1, newStarIdString);
	    	    	psInsertStar.setString(2, s.getName());
	    	    	psInsertStar.setInt(3, s.getBirthYear());
	    	    	
	    	    	addedStars.put(newStarIdString, s.getName());
	    	    	
	    	    	psInsertStar.addBatch();
	    	    	
	    	    	newStarId++;
    	    	}
    	    }
    	    psInsertStar.executeBatch();
    	    conn.commit();
    	    System.out.println("Executed batch for INSERT STAR");
    	        
    	    
            // inserting stars in movies
    	    
    	    String sqlInsertSIM = "insert into stars_in_movies (starId, movieId) values (?, ?);";
    	    PreparedStatement psInsertSIM = conn.prepareStatement(sqlInsertSIM);
    	    
    	    System.out.println("Adding to batch for INSERT SIM");
    	    
      	    for (int i=0; i < movies.size(); i++) {								// loop through all movies
      	    	if (mIdToStar.containsKey(movies.get(i).getId())) {							// narrow movies to only movies that are in casts.xml
	      	    	
	      	    	ArrayList<String> listOfStars = mIdToStar.get(movies.get(i).getId());		// get arraylist of star names
	      	    	
	      	    	for (int j = 0; j < listOfStars.size(); j++) {							// loop through array list of star names
	      	    	    String starId = "";
	      	    	    
	      	    		for (Map.Entry<String, String> entry : addedStars.entrySet()) {							
	      	    	        if (entry.getValue().equals(listOfStars.get(j))) {
	      	    	            starId = entry.getKey();
	      	    	            break;
	      	    	        }
	      	    	    }
	      	    		
	      	    		if (!(starId.equals(""))) {
	      	    			//System.out.println("Adding to batch for INSERT SIM");

	      	    			psInsertSIM.setString(1, starId);
	      	    			psInsertSIM.setString(2, movies.get(i).getId());
	      	    			psInsertSIM.addBatch();
	      	    			
	      	    		}
	      	    		
	      	    	}
      	    	}
      	    }
    	    
      	    
      	    psInsertSIM.executeBatch();
      	    conn.commit();
    	    System.out.println("Executed batch for INSERT SIM");
   	    
    	   
    		// movies = ArrayList of Movie objects
    		// genres = HashSet of all genre names that are parsed
    		// stars = ArrayList of Star objects
    		// mIdToStar = HashMap of FID : ArrayList of Star names
    		// mIdToTitle = HashMap of FID: Movie object   
    	    // addedGenres = gName : gId
    	    // genreIdToName = "gId" : gName from DB
    	    
    	    
            String newGenreQuery = "select * from genres";
            Statement newGenreStatement = conn.createStatement();
            ResultSet rsNewGenre = newGenreStatement.executeQuery(newGenreQuery);
            
            while (rsNewGenre.next()) {
            //	genreIdToName.put(rsNewGenre.getString("id"), rsGenre.getString("name"));
            	genreNameToId.put(rsNewGenre.getString("name"), rsNewGenre.getString("id"));

            }
    	    
            // inserting genres in movies
    	   
  			System.out.println("Adding to batch for INSERT GIM");
    	    String sqlInsertGIM = "insert into genres_in_movies (genreId, movieId) values (?, ?);";
    	    PreparedStatement psInsertGIM = conn.prepareStatement(sqlInsertGIM);
    	    Iterator<Movie> it = movies.iterator();
    	    while (it.hasNext()) {
    	    	Movie m = it.next();
    	    	boolean add = true;
    	    	for (int i = 0; i < m.genres.size(); i++) {

      	    		for (Map.Entry<Integer, String> entry : genreIdToMovieId.entrySet()) {
      	    			String toFind = genreNameToId.get(m.genres.get(i)) + ":" + m.getId();
      	    			 if (entry.getValue().equals(toFind)) {
       	    	        	add = false;
       	    	        	break;
       	    	        } 
      	    	    }
      	    		
      	    		if (add) {
   	    	        	int genreId = Integer.parseInt(genreNameToId.get(m.genres.get(i)));
   	    	            
         	    		psInsertGIM.setInt(1, genreId);
         	    		psInsertGIM.setString(2, m.getId());
         	    		psInsertGIM.addBatch();
   	    	            break;
      	    		}

    	    	}
       	    }

      	    psInsertGIM.executeBatch();
      	    conn.commit();
    	    System.out.println("Executed batch for INSERT GIM");
            
            
 //   	    System.out.println("closing psGenre");

    	    psInsertGenre.close();
    	    
 //   	    System.out.println("closing psMovie");

    	    psInsertMovie.close();
    	    
 //   	    System.out.println("closing psStar");

    	    psInsertStar.close();
    	    
//    	    System.out.println("closing psSIM");

    	    psInsertSIM.close();
    	    
  //  	    System.out.println("closing psGIM");

    	    psInsertGIM.close();
            
//    	    System.out.println("closing rsMovie");

        	rsMovie.close();
        	
//    	    System.out.println("closing movieStatement");

			movieStatement.close();
			
//    	    System.out.println("closing rsGenre");

        	rsGenre.close();
        	
//    	    System.out.println("closing genreStatement");

			genreStatement.close();
			
//    	    System.out.println("closing rsStar");

        	rsStar.close();
        	
  //  	    System.out.println("closing starStatement");

			starStatement.close();

			System.out.println("closing connection");

			conn.close();
			
			System.out.println("CLOSED connection");

     
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
      
	}
	
	
	
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	    SAXParser sp = new SAXParser();
	    sp.parseXML();
	    
	    System.out.println("No of movies parsed in mains243.xml: " + sp.movies.size());
	    System.out.println("No of id:title pairs parsed in mains243.xml: " + sp.mIdToTitle.size());
	    System.out.println("No of genres parsed in mains243.xml: " + sp.genres.size());
	    System.out.println("No of stars parsed in actors63.xml: " + sp.stars.size());
	    System.out.println("No of casts parsed in casts124.xml: " + sp.mIdToStar.size());
	    
	    sp.buildHashMap();
	    
	    System.out.println("DONE WITH EVERYTHING!");
	    System.exit(0);
	       
	}	
	
	
	
}
