import java.awt.List;
import java.io.File;

public class Resource {
	
	/**optional user supplied name, default is " " */
	private String name;
	
	/**optional user supplied description, default is " "*/
	private String description;
	
	/**optional user supplied list of tag, default is empty list */
	private List tag;
	
	/**mandatory user supplied absolute URI */ 
	private String URI;
	
	/**optional user supplied channel name*/
	private String channel;
	
	/**optional user supplied owner name */
	private String owner;
	
	/**system supplied server:port name that lists the Resource*/
	private String EZserver;
	
	private resourceFile file;
	
	/**constructor for this class, each instance variables are "" by default
	 * except URI should be initialized*/
	public Resource(String uri){
		this.name = "";
		this.description = "";
		this.tag = new List();
		this.URI = uri;
		this.channel = "";
		this.owner = "";
		this.EZserver = "";
		this.file = new resourceFile(uri);
	}
	
	public void setDefaultChannel(){
		this.channel = "public";
	}
	
	public void setPrivateChannel(String channel){
		this.channel = channel;
	}
	
	
	
	
 
}
