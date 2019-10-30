import java.util.ArrayList;

public class Movie {

	private String id;
	private String title;
	private String director;
	private int year;
	ArrayList<String> genres;
	
	
	public Movie() {
		this.id = "";
		this.title = "";
		this.director = "";
		this.year = 0;
		this.genres = new ArrayList<String>();
	}
	
	public Movie(String i, String t, String d, int y) {
		this.id = i;
		this.title = t;
		this.director = d;
		this.year = y;
		this.genres = new ArrayList<String>();
	}
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String i) {
		this.id = i;
	}
	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String t) {
		this.title = t;
	}
	
	
	public String getDirector() {
		return director;
	}
	
	public void setDirector(String d) {
		this.director = d;
	}
	
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int y) {
		this.year = y;
	}
	
	public void addGenre(String g) {
		genres.add(g);
	}
	
	public void printGenres() {
		for (int i = 0; i < genres.size(); i++) {
			System.out.println(genres.get(i));
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("Title: " + getTitle());
		sb.append(", ");
		sb.append("Dir: " + getDirector());
		sb.append(", ");
		sb.append("Id: " + getId());
		sb.append(", ");
		sb.append("Year: " + getYear());
		sb.append(", ");
		sb.append("Genre(s): ");
		
		for (int i = 0; i < genres.size(); i++) {
			sb.append(genres.get(i) + " . ");
		}
		
		sb.append(".");
		
		return sb.toString();
	}
	
}
