import java.util.HashMap;
import java.util.Queue;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class ServerHandler {
	
	public Queue<String> clientQueue;
	
	public synchronized static void handlingPublish(Resource resource,HashMap<String, Resource> resources){
		String errorMessage;
		String response;
		Boolean success = false;	
		JSONObject errorMsg = new JSONObject();
		
		/**Regexp for filePath*/
		String filePathPattern = "^[a-zA-Z*]:?([\\\\/]?|([\\\\/]([^\\\\/:\"<>|]+))*)[\\\\/]?$|^\\\\\\\\(([^\\\\/:\"<>|]+)[\\\\/]?)+$";
		/**Regexp for invalid resource contains whitespace or /o */
		String invalidString = "(^\\s.+\\s$)|((\\\\0)+)";
		
		/**invalid resource contains whitespace or /o */
		boolean invalidTag = true;
		for(String str: resource.tag){
			if(Pattern.matches(invalidString, str)){
				invalidTag = false;
			}
		}
		boolean invalidResourceValue = Pattern.matches(invalidString, resource.name)||Pattern.matches(invalidString, resource.channel)||
				Pattern.matches(invalidString, resource.description)||Pattern.matches(invalidString, resource.URI)||
				Pattern.matches(invalidString, resource.owner)||invalidTag;
		
		if (invalidResourceValue) {
			errorMessage = "invalid resource";
		}else{
			/**resource field not given or uri is not file scheme*/
			if(resource.URI==""||Pattern.matches(filePathPattern, resource.URI)){
				errorMessage = "missing resource";
				
			}else{
				if(resources.containsKey(resource.URI)){
					/**same URI, same channel,different owner or owner contains * */
					if((resources.get(resource.URI).owner!=resource.owner||resource.owner=="*")
							&&resources.get(resource.URI).channel==resource.channel){
						errorMessage = "cannot publish resource";
					}else{
						/**same primary key*/
						resources.remove(resource.URI);
						resources.put(resource.URI, resource);
						errorMessage = "success";
						success = true;
					}
				}else{
					/**valid URI*/
					
					
					
				}
				
				
				
				
				
			}
			
		}
		
		
	}
	
	public synchronized static void handlingRemove (Resource resource){
		
	}
	
	public synchronized static void HandlingShare (Resource resource, String secret){
		
	}
	

}
