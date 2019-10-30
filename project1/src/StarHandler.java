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

public class StarHandler extends DefaultHandler{
	/* Star class: id, name, dob*/
	
//	Map<Integer, Star> myStars;
	
	ArrayList<Star> myStars;
	private Star tempStar;
	private String tempVal;
//	int count;
	
	
   public StarHandler() {
        myStars = new ArrayList<Star>();
    }

   
   public static void main(String[] args) {
       StarHandler mh = new StarHandler();
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
           sp.parse("actors63.xml", this);
           System.out.println("Done parsing star-test.xml");


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
        if (qName.equalsIgnoreCase("actor")) {
            tempStar = new Star();
            
           // System.out.println("Created new Star object");
        } 
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	String result = cleanString(tempVal);
    	
    	if (qName.equalsIgnoreCase("actor")) {
//            System.out.println("ADDING star");
            myStars.add(tempStar);
//            System.out.println("ADDED star!");
            
//            System.out.println(tempStar.toString());
            

        } else if (qName.equalsIgnoreCase("stagename")) {
        	
        	if (!(tempVal.isEmpty())) {
        		tempStar.setName(tempVal);
        	} else {
        		tempStar.setName("Untitled Star");
        	}
        	
        } else if (qName.equalsIgnoreCase("dob")) {
        	
           	try{
            	if (!(result.isEmpty())) {
            		tempStar.setBirthYear(Integer.parseInt(result));
            	} else {
            		
            		System.out.println("dob year for " + tempStar.getName() + " is an EMPTY string, setting the year to 1900 to indicate inconsistent data");
            		tempStar.setBirthYear(1900); // empty space for <year>
            	}
        	} catch (Exception e) {
        		
        		System.out.println("dob year for " + tempStar.getName() + " is an INVALID year, setting the year to 1900 to indicate inconsistent data");

        		tempStar.setBirthYear(1900); // shows that there was something wrong with the data for <year>
        	}
        	
        	
        }
        
        

    }
    
    public String cleanString(String s) {
    	String result;
    	
    	result = s.replaceAll("\\s","");
    	
    	return result;
    	
    }
    

	private void printData() {
	
	    System.out.println("No of movies '" + myStars.size() + "'.");
	
		for (int i = 0; i < myStars.size(); i++) {
			System.out.println(myStars.get(i).toString());
		}
	    
	    System.out.println("No of movies '" + myStars.size() + "'.");

	}
    
    
}
