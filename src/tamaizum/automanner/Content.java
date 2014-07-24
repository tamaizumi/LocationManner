package tamaizum.automanner;

public class Content {
	public static final int MAP_CONTENT = -1;
	public static final int SCHEDULE_CONTENT = 1;
	
	private int id;
	private String title;
	private int tag;
	
	public Content(int id , String title , int tag){
		this.id = id;
		this.title = title;
		this.tag = tag;
	}
	
	public int getId(){
		return id;
	}
	
	public int getTag(){
		return tag;
	}
	
	public String getTitle(){
		return title;
	}
	
}
