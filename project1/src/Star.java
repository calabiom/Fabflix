import java.util.ArrayList;

public class Star {

	private String id = "";
	private String name = "";
	private int dob;
	private ArrayList<String> movies;
	
	public Star() {
		this.movies = new ArrayList<String>();
	}
	
	public Star(String i, String n, int d) {
		this.id = i;
		this.name = n;
		this.dob = d;
		this.movies = new ArrayList<String>();

	}
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String i) {
		this.id = i;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String t) {
		this.name = t;
	}
	
	
	public int getBirthYear() {
		return dob;
	}
	
	public void setBirthYear(int d) {
		this.dob = d;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Star Details - ");
		sb.append("Stagename/Name: " + getName());
		sb.append(", ");
		sb.append("Year: " + getBirthYear());
//		sb.append(", ");
//		sb.append("Genre(s): ");
		
		return sb.toString();

	}
	
}
