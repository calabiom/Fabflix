import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieHandler extends DefaultHandler{
	/* Movie class: id, title, director, year */
	
	ArrayList<Movie> myMovies;
	HashSet<String> allGenres;
	
	Map<String, Movie> idToMovie;
	
	private Movie tempMovie;
	
	private String tempVal;
	
	private String tempDirector;
	
   public MovieHandler() {
        myMovies = new ArrayList<Movie>();
        allGenres = new HashSet<String>();
        idToMovie = new HashMap<String, Movie>();
    }

   
   public static void main(String[] args) {
       MovieHandler mh = new MovieHandler();
       mh.runParser();
   }


   public void runParser() {
       parseDocument();
//       printData();
   }

   private void parseDocument() {

       //get a factory
       SAXParserFactory spf = SAXParserFactory.newInstance();
       try {

           //get a new instance of parser
           SAXParser sp = spf.newSAXParser();

           //parse the file and also register this class for call backs
        //   System.out.println("About to parse....");
           sp.parse("mains243.xml", this);
           System.out.println("Done parsing movie-test.xml");


       } catch (SAXException se) {
           se.printStackTrace();
       } catch (ParserConfigurationException pce) {
           pce.printStackTrace();
       } catch (IOException ie) {
           ie.printStackTrace();
       }
   }
   
   
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
            
           // System.out.println("Created new Movie object");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	String result = cleanString(tempVal);
    	
        if (qName.equalsIgnoreCase("film")) {
            
            if (!(tempMovie.getId().isEmpty())) {
                //add it to the list
            	tempMovie.setDirector(tempDirector);
            	
                myMovies.add(tempMovie);
                
//                System.out.println(tempMovie.toString());
                if (idToMovie.containsKey(tempMovie.getId())){
                	System.out.println("Fid already exists: " + tempMovie.getId() + " - " + tempMovie.getTitle() + ". Reported as inconsistent data.");
                } else {
                	idToMovie.put(tempMovie.getId(), tempMovie);
                }
            	
            } else {
            	
            	System.out.println("Fid is missing for: " + tempMovie.getTitle() + ". Will skip this movie. Reported as inconsistent data");
            }
            


        } else if (qName.equalsIgnoreCase("t")) {
        	
        	if (!(tempVal.isEmpty())) {
        		tempMovie.setTitle(tempVal);
        	} else {
        		tempMovie.setTitle("Untitled Movie");
        	}
        	
        } else if (qName.equalsIgnoreCase("fid")) { // MAY HAVE TO ALTER THIS WHEN WE EXTEND THIS TO PARSING CASTS AND ACTORS
        	
        	if (!(result.isEmpty())) { // have to check if the id is an empty string later on when populating database.
        		tempMovie.setId(result);
        	} else {
        		//System.out.println("fid is EMPTY");
        	}
        	
        } else if (qName.equalsIgnoreCase("year")) { // HANDLE EXCEPTIONS (EX: 19XX)
            
        	try{
            	if (!(result.isEmpty())) {
            		tempMovie.setYear(Integer.parseInt(result));
            	} else {
            		
            		System.out.println("Movie year for " + tempMovie.getTitle() + " is an EMPTY string, setting the year to 1900 to indicate inconsistent data");
            		tempMovie.setYear(1900); // empty space for <year>
            	}
        	} catch (Exception e) {
        		
        		System.out.println("Movie year for " + tempMovie.getTitle() + " is an INVALID year, setting the year to 1900 to indicate inconsistent data");

        		tempMovie.setYear(1900); // shows that there was something wrong with the data for <year>
        	}
        	
        } else if (qName.equalsIgnoreCase("dirname")) {
        
        	if (!(result.isEmpty())) {
        		tempDirector = result;
        	} else {
        		tempDirector = "Unknown";
        	}
        	
        } else if (qName.equalsIgnoreCase("cat")) {
        	if (!(result.isEmpty())) {
        	
        		allGenres.add(result); // if the "cat" is already in allGenres, then it'll disregard it
        	
            	tempMovie.addGenre(result); // adding "cat" to the genres list of a film
        	}
        } 

    }
    
    public String cleanString(String s) {
    	String result;
    	
    	result = s.replaceAll("\\s","");
    	
    	return result;
    	
    }
    

	private void printData() {
	
	    System.out.println("No of movies '" + myMovies.size() + "'.");
	
	    Iterator<Movie> it = myMovies.iterator();
	    while (it.hasNext()) {
	        System.out.println(it.next().toString());
	    }
	    
	    System.out.println("No of genres '" + allGenres.size() + "'.");

	    Iterator<String> genre_iter = allGenres.iterator();
	    while (genre_iter.hasNext()) {
	        System.out.println(genre_iter.next());
	    }
	 
	    System.out.println("No of movies '" + myMovies.size() + "'.");
	    System.out.println("No of genres '" + allGenres.size() + "'.");

	}
    
    
}
