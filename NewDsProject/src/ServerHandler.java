import java.util.Queue;

import org.json.JSONObject;

public class ServerHandler {
	
	public Queue<String> clientQueue;
	
	public synchronized static void handlingPublish(Resource resource){
		String errorMessage;
		String response;
		Boolean success = false;	
		JSONObject errorMsg = new JSONObject();
		if(resource.URI==null){
			errorMessage = "missing resource";
			
		}else{
			
		}
	}
	
	public synchronized static void handlingRemove (Resource resource){
		
	}
	
	public synchronized static void HandlingShare (Resource resource, String secret){
		
	}
	

}
