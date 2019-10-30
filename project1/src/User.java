import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    
  //  private ArrayList<String> cart = new ArrayList<String>();					// list of movie ids
    
    private Map<String, Integer> cart = new HashMap<String, Integer>();
    private Map<String, String> movieList = new HashMap<String, String>(); 	// id:movie pairs
    

    public User(String username) {
        this.username = username;
    }
    
    public String getUsername() { return this.username; }
    
    public void addToCart(String item){ // only used for "add to cart" buttons on single movie and movielist pages
    	
			if (cart.containsKey(item)) { // if movie_id exist ==> modify quantity
				int newQuantity = cart.get(item) + 1;
				
				cart.replace(item, newQuantity);
			} else {
				cart.put(item, 1);
			}    		
 
    }    
    
    public void clearCart() {
    	cart.clear();
    }
    
    public void modifyQuantity(String item, int amount) { // only used in shopping cart page
		if (cart.containsKey(item)) { // if movie_id exist ==> modify quantity
			if (amount == 0) {
				cart.remove(item);
			}else {
				cart.replace(item, amount);
			}
			
    	}
    }
    
    public int removeCartItem(String item) {
    	if (cart.containsKey(item)) {
    		cart.remove(item);
    		return 1;
    	}
    	return 0;
    }
    
    public int getCartSize() {
    	return cart.size();
    }
    
    public String printCart() {
    	String printedMap = "";
    	for (String name: cart.keySet()){

            String key =name.toString();
            String value = cart.get(name).toString();  
            System.out.println(key + " " + value);  
            printedMap += "(" + key + ", " + value + ")"; 
    	}
    	
    	return printedMap;
    }
    
    
    /////////////////////////////////////////////////////////
    public void updateMovieList(String id, String title) {
    	if (!(this.movieList.containsKey(id))) {
    		this.movieList.put(id, title);
    	}		
    }
    
    public String getMovieTitle(String id) {
    	return this.movieList.get(id);
    }
    
    
    public Map<String, Integer> createCartCopy() { // return copy of Map, called by SCServlet when loading shopping cart page
    	return cart;
    }
}
