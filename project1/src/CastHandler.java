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

public class CastHandler extends DefaultHandler{
	/* Star class: id, name, dob*/
	
	Map<String, ArrayList<String>> movieIdToStar;
	
	
	private String tempStarName;
	private String tempMovieId;
	private String tempVal;
	
	
   public CastHandler() {
    	movieIdToStar = new HashMap<String, ArrayList<String>>();
    }

   
   public static void main(String[] args) {
       CastHandler mh = new CastHandler();
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
           // System.out.println("About to parse....");
           sp.parse("casts124.xml", this);
           System.out.println("Done parsing casts124.xml");


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
        if (qName.equalsIgnoreCase("filmc")) {

        } 
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	String result = cleanString(tempVal);
    	
    	if (qName.equalsIgnoreCase("m")) {
    		
    		if (movieIdToStar.containsKey(tempMovieId)) { // id key already exists -> add star to arraylist
    			
    			movieIdToStar.get(tempMovieId).add(tempStarName);
    		
    		} else {
    	    	ArrayList<String> tempMovieCast = new ArrayList<String>();
    	    	
    	    	tempMovieCast.add(tempStarName);
    			movieIdToStar.put(tempMovieId, tempMovieCast);
    		}
    		
    		
    	} else if (qName.equalsIgnoreCase("f")) {
    		tempMovieId = result;

        } else if (qName.equalsIgnoreCase("a")) {
        	if (!(tempVal.isEmpty())) {
        		tempStarName = tempVal;
        	} else {
        		tempStarName = "Unknown Star"; /// report inconsistent data???
        	}        	
        } 
        
        

    }
    
    public String cleanString(String s) {
    	String result;
    	
    	result = s.replaceAll("\\s","");
    	
    	return result;
    	
    }
    

	private void printData() {
//	
		System.out.println("No of casts parsed '" + movieIdToStar.size() + "'.");
//	
	    for (Map.Entry<String, ArrayList<String>> entry : movieIdToStar.entrySet()) {
	    	
	    	System.out.println("--- Cast for Movie Id: " + entry.getKey() + " ---");
	        for (int i = 0; i < entry.getValue().size(); i++) {
				System.out.println(entry.getValue().get(i).toString());
	        }
	    }
//	    
		System.out.println("No of casts parsed '" + movieIdToStar.size() + "'.");

	}
    
    
}
