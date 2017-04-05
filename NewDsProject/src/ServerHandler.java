import java.util.HashMap;
import java.util.Queue;
import java.util.regex.Pattern;

import javax.swing.OverlayLayout;

import org.json.simple.*;
public class ServerHandler {
	
	public Queue<String> clientQueue;
	
	public synchronized static JSONObject handlingPublish(Resource resource,HashMap<String, Resource> resources){
		String errorMessage;
		String response;
		Boolean success = false;	
		
		/**reponse send back to the client*/
		JSONObject serverResponse = new JSONObject();
		
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
		while(serverResponse==null){
			if (invalidResourceValue||resource.owner=="*") {
				errorMessage = "invalid resource";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}else{
				/**resource field not given or uri is not file scheme*/
				if(resource.URI==""||Pattern.matches(filePathPattern, resource.URI)){
					errorMessage = "missing resource";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					
				}else{
					if(resources.containsKey(resource.URI)){
						/**same URI, same channel,different owner or owner contains * */
						if(resources.get(resource.URI).channel==resource.channel&&resources.get(resource.URI).owner!=resource.owner){
							errorMessage = "cannot publish resource";
							response="error";
							serverResponse.put(ConstantEnum.CommandType.response.name(),response);
							serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
						}else{
							/**same primary key*/
							if(resources.containsKey(resource.URI)&&resources.get(resource.URI).owner==resource.owner
									&&resources.get(resource.URI).channel==resource.channel){
								resources.remove(resource.URI);
								resources.put(resource.URI, resource);
								response = "success";
								success = true;
								serverResponse.put(ConstantEnum.CommandType.response.name(),response);
							}
					else{
						/**valid URI*/
						resources.put(resource.URI,resource);
						response = "success";
						success= true;
						serverResponse.put(ConstantEnum.CommandType.response.name(),response);				
					}
						}
					}
				}
			}
		}
		
		
		return serverResponse;
	}
	
	public synchronized static JSONObject handlingRemove (Resource resource,HashMap<String, Resource> resources){
		String errorMessage;
		String response;
		Boolean success = false;
		/**reponse send back to the client*/
		JSONObject serverResponse = new JSONObject();
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
		
		while(serverResponse == null){
			if (invalidResourceValue) {
				errorMessage = "invalid resource";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}else{
				/**resource field not given or uri is not file scheme*/
				if(resource.URI==""||Pattern.matches(filePathPattern, resource.URI)){
					errorMessage = "missing resource";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					
				}else{
					
					/**successful remove*/
					if (resources.containsKey(resource.URI)&&resources.get(resource.URI).owner==resource.owner&&
							resources.get(resource.URI).channel==resource.channel) {
						response = "success";
						resources.remove(resource.URI);
						serverResponse.put(ConstantEnum.CommandType.response.name(), response);
					}else{
						
						/**resource did not exist*/
						response = "error";
						errorMessage = "cannot remove resource";
						serverResponse.put(ConstantEnum.CommandType.response.name(), response);
						serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					}	
				}
			}
		}
		return serverResponse;	
	}
	
	public synchronized static JSONObject HandlingShare (Resource resource, String ClientSecret, String ServerSecret, HashMap<String, Resource> resources){
		String errorMessage;
		String response;
		Boolean success = false;	
		
		/**reponse send back to the client*/
		JSONObject serverResponse = new JSONObject();
		
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
		
		/** resource or secret field was not given or not of the correct type*/
		while(serverResponse==null){
			if(ClientSecret==""||resource.URI==""||!Pattern.matches(filePathPattern, resource.URI)){
				response = "error";
				errorMessage = "missing resource and\\/or secret";
				serverResponse.put(ConstantEnum.CommandType.response.name(), response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);

			}else{
				if(ClientSecret!=ServerSecret){
					
					/** secret was incorrect*/
					response = "error";
					errorMessage = "incorrect secret";
					serverResponse.put(ConstantEnum.CommandType.response.name(), response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
				}else{
					if (invalidResourceValue||resource.owner=="*") {
						
						/** resource contained incorrect information that could not be recovered from*/
						errorMessage = "invalid resource";
						response = "error";
						serverResponse.put(ConstantEnum.CommandType.response.name(),response);
						serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					}else{
						if(resources.containsKey(resource.URI)){
							/**same URI, same channel,different owner or owner contains * */
							if(resources.get(resource.URI).owner!=resource.owner
									&&resources.get(resource.URI).channel==resource.channel){
								errorMessage = "cannot publish resource";
								response="error";
								serverResponse.put(ConstantEnum.CommandType.response.name(),response);
								serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
							}else{
								/**same primary key*/
								resources.remove(resource.URI);
								resources.put(resource.URI, resource);
								response = "success";
								success = true;
								serverResponse.put(ConstantEnum.CommandType.response.name(),response);
								
								//right here share mechanism is not implement yet.!!!
							}
						}else{
							/**valid URI*/
							resources.put(resource.URI,resource);
							
							//need a share mechanism!!
							response = "success";
							success= true;
							serverResponse.put(ConstantEnum.CommandType.response.name(),response);				
						}
					}
				}	
			}	
		}
	return serverResponse;
	}
	
	
	
	
	

}
