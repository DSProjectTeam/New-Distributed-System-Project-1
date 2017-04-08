import java.net.ServerSocket;
import java.util.ArrayList;
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
	
	public synchronized static JSONObject handlingQuery(String name_query,String[] tags_query,
			String description_query, String uri_query,String channel_query, 
			String owner_query, boolean relay,HashMap<String, Resource> resources, ServerSocket serverSocket){
		/**用来存放满足template的resource*/
		ArrayList<Resource> matchResourceSet = new ArrayList<Resource>();
		String errorMessage;
		String response;
		int resultSize = 0;
		Boolean success = false;	
		JSONObject serverResponse = new JSONObject();
		

		/**Regexp for filePath*/
		String filePathPattern = "^[a-zA-Z*]:?([\\\\/]?|([\\\\/]([^\\\\/:\"<>|]+))*)[\\\\/]?$|^\\\\\\\\(([^\\\\/:\"<>|]+)[\\\\/]?)+$";
		/**Regexp for invalid resource contains whitespace or /o */
		String invalidString = "(^\\s.+\\s$)|((\\\\0)+)";
		
		
		/**invalid resource contains whitespace or \o */
		boolean invalidTag = true;
		for(String str: tags_query){
			if(Pattern.matches(invalidString, str)){
				invalidTag = false;
			}
		}
		
		boolean invalidResourceValue = Pattern.matches(invalidString, name_query)||Pattern.matches(invalidString, channel_query)||
				Pattern.matches(invalidString, description_query)||Pattern.matches(invalidString, uri_query)||
				Pattern.matches(invalidString, owner_query)||invalidTag;
		
		while(serverResponse==null){
			if(invalidResourceValue||owner_query =="*"){
				errorMessage = "invalid resourceTemplate";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}else{
				if(!uri_query.equals("")&&Pattern.matches(uri_query, filePathPattern)){
					errorMessage = "missing resourceTemplate";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
				}else{					
					
					boolean hasMacthResource = false;
					do {
						/**遍历装有resource对象的Hashmap resources*/
						for(Resource resource : resources.values()){
							
							/**tagIncluded等于true如果所有template标签包含在候选资源的tags中*/
							boolean tagIncluded = false;
							int tagCount = 0;
							int tagLength = tags_query.length;
							for(int i = 0; i<tags_query.length; i++){
								if(resource.tag.contains(tags_query[i])){
									tagCount++;
								}
							}
							if(tagCount>=tagLength){
								tagIncluded = true;
							}
							
							/**owner or URI not ""*/
							if((channel_query.equals(resource.channel) && (!owner_query.equals("") && owner_query.equals(resource.owner)) && 
									uri_query.equals(resource.URI) && tagIncluded && ( (!name_query.equals("") && name_query.equals(resource.name))|| 
											(!description_query.equals("") && resource.description.contains(channel_query) )|| 
											(name_query.equals("")&&description_query.equals("")))) || 
									/**owner or URI could be "" */
									(channel_query.equals(resource.channel))&& tagIncluded && ( (!name_query.equals("") && name_query.equals(resource.name))|| 
													(!description_query.equals("") && resource.description.contains(channel_query) )|| 
													(name_query.equals("")&&description_query.equals("")))){
							
								Resource matchResource = resource;
								
								hasMacthResource = true;
								
								/**将符合要求的资源放在MatchResourceSet里*/
								matchResourceSet.add(matchResource);
								success = true;
								response = "success";
								serverResponse.put(ConstantEnum.CommandType.response.name(), response);
								for(Resource resouce: matchResourceSet){
									JSONObject MatchResouce = new JSONObject();
									MatchResouce.put(ConstantEnum.CommandArgument.name.name(), resouce.name);
									MatchResouce.put(ConstantEnum.CommandArgument.tags.name(), resouce.tag);
									MatchResouce.put(ConstantEnum.CommandArgument.description.name(), resouce.description);
									MatchResouce.put(ConstantEnum.CommandArgument.uri.name(), resouce.URI);
									MatchResouce.put(ConstantEnum.CommandArgument.channel.name(), resouce.channel);
									MatchResouce.put(ConstantEnum.CommandArgument.owner.name(), resouce.name);
									Integer ezport = serverSocket.getLocalPort();
									String ezserver = serverSocket.getLocalSocketAddress().toString()+":"+ezport.toString();
									MatchResouce.put(ConstantEnum.CommandArgument.ezserver.name(), ezserver);
									serverResponse.put(ConstantEnum.CommandType.resource.name(), MatchResouce);
								}
							}
						}
					} while (hasMacthResource = false);
					errorMessage = "invalid resourceTemplate";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}
		}
		
		/*while(relay = true){
			*//**遍历装有resource对象的Hashmap resources*//*
			for(Resource resource : resources.values()){
				
				*//**tagIncluded等于true如果所有template标签包含在候选资源的tags中*//*
				boolean tagIncluded = false;
				int tagCount = 0;
				int tagLength = tags_query.length;
				for(int i = 0; i<tags_query.length; i++){
					if(resource.tag.contains(tags_query[i])){
						tagCount++;
					}
				}
				if(tagCount>=tagLength){
					tagIncluded = true;
				}
				
				*//**owner or URI not ""*//*
				if((channel_query.equals(resource.channel) && (!owner_query.equals("") && owner_query.equals(resource.owner)) && 
						uri_query.equals(resource.URI) && tagIncluded && ( (!name_query.equals("") && name_query.equals(resource.name))|| 
								(!description_query.equals("") && resource.description.contains(channel_query) )|| 
								(name_query.equals("")&&description_query.equals("")))) || 
						*//**owner or URI could be "" *//*
						(channel_query.equals(resource.channel))&& tagIncluded && ( (!name_query.equals("") && name_query.equals(resource.name))|| 
										(!description_query.equals("") && resource.description.contains(channel_query) )|| 
										(name_query.equals("")&&description_query.equals("")))){
				
					Resource matchResource = resources.get(uri_query);
					
					*//**将符合要求的资源放在MatchResourceSet里*//*
					matchResourceSet.add(matchResource);
				}
			}
			
			success = true;
			response = "success";
			serverResponse.put(ConstantEnum.CommandType.response.name(), response);
			for(Resource resouce: matchResourceSet){
				JSONObject MatchResouce = new JSONObject();
				MatchResouce.put(ConstantEnum.CommandArgument.name.name(), resouce.name);
				MatchResouce.put(ConstantEnum.CommandArgument.tags.name(), resouce.tag);
				MatchResouce.put(ConstantEnum.CommandArgument.description.name(), resouce.description);
				MatchResouce.put(ConstantEnum.CommandArgument.uri.name(), resouce.URI);
				MatchResouce.put(ConstantEnum.CommandArgument.channel.name(), resouce.channel);
				MatchResouce.put(ConstantEnum.CommandArgument.owner.name(), resouce.name);
				Integer ezport = serverSocket.getLocalPort();
				String ezserver = serverSocket.getLocalSocketAddress().toString()+":"+ezport.toString();
				MatchResouce.put(ConstantEnum.CommandArgument.ezserver.name(), ezserver);
				serverResponse.put(ConstantEnum.CommandType.resource.name(), MatchResouce);
			}
		}
		*/
		}
		return serverResponse;
	}
	
	
	
	
	

}
