import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import javax.swing.OverlayLayout;

import org.json.simple.*;
import org.json.simple.JSONObject;

public class ServerHandler {
	
	public Queue<String> clientQueue;
	
	public synchronized static JSONObject handlingPublish(String name,String[] tags,
			String description, String uri,String channel, 
			String owner,HashMap<String, Resource> resources){
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
		boolean invalidTag = false;
		for(String str: tags){
			if(Pattern.matches(invalidString, str)){
				invalidTag = true;
			}
		}
		System.out.println("command is oas");
		boolean invalidResourceValue = Pattern.matches(invalidString, name)||Pattern.matches(invalidString, channel)||
				Pattern.matches(invalidString, description)||Pattern.matches(invalidString, uri)||
				Pattern.matches(invalidString, owner)||invalidTag;
		do{
			if (invalidResourceValue|| owner.equals("*")) {
				errorMessage = "invalid resource";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}else{
				/**resource field not given or uri is not file scheme*/
				if(uri.equals("") || Pattern.matches(filePathPattern, uri)){
					errorMessage = "missing resource";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					
				}else{
					if(resources.containsKey(uri)){
						/**same URI, same channel,different owner or owner contains * */
						if(resources.get(uri).channel.equals(channel) &&!resources.get(uri).owner.equals(owner)){
							errorMessage = "cannot publish resource";
							response="error";
							serverResponse.put(ConstantEnum.CommandType.response.name(),response);
							serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
						}else{
							/**same primary key*/
							
								resources.remove(uri);
								Resource resource = new Resource(name, tags, description, uri, channel, owner);
								resources.put(resource.URI, resource);
								response = "success";
								success = true;
								serverResponse.put(ConstantEnum.CommandType.response.name(),response);	
						}
					}else{
						/**valid URI*/
						Resource resource = new Resource(name, tags, description, uri, channel, owner);
						resources.put(uri,resource);
						response = "success";
						success= true;
						serverResponse.put(ConstantEnum.CommandType.response.name(),response);				
					}
				}
			}
		}while(serverResponse==null);
		
		
		return serverResponse;
	}
	
	public synchronized static JSONObject handlingRemove (String name,String[] tags,
			String description, String uri,String channel, 
			String owner,HashMap<String, Resource> resources){
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
		boolean invalidTag = false;
		for(String str: tags){
			if(Pattern.matches(invalidString, str)){
				invalidTag = true;
			}
		}
		boolean invalidResourceValue = Pattern.matches(invalidString, name)||Pattern.matches(invalidString, channel)||
				Pattern.matches(invalidString, description)||Pattern.matches(invalidString, uri)||
				Pattern.matches(invalidString, owner)||invalidTag;
		do {
			if (invalidResourceValue || owner.equals("*")) {
				errorMessage = "invalid resource";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}else{
				/**resource field not given or uri is not file scheme*/
				//if(uri.equals("") || Pattern.matches(filePathPattern, uri))
				if(uri.equals("")){
					errorMessage = "missing resource";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					
				}else{
					
					/**successful remove*/
					/*if (resources.containsKey(uri)&&resources.get(uri).owner.equals(owner)&&
							resources.get(uri).channel.equals(channel))*/
					if (resources.containsKey(uri)) {
						response = "success";
						resources.remove(uri);
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
		} while (serverResponse==null);
		
		return serverResponse;	
	}
	
	public synchronized static JSONObject HandlingShare (String name,String[] tags,
			String description, String uri,String channel, 
			String owner, String ClientSecret, String ServerSecret, HashMap<String, Resource> resources){
		String errorMessage;
		String response;
		Boolean success = false;	
		System.out.println("hello233!");
		/**reponse send back to the client*/
		JSONObject serverResponse = new JSONObject();
		
		/**Regexp for filePath*/
		String filePathPattern = "^[a-zA-Z*]:?([\\\\/]?|([\\\\/]([^\\\\/:\"<>|]+))*)[\\\\/]?$|^\\\\\\\\(([^\\\\/:\"<>|]+)[\\\\/]?)+$";
		/**Regexp for invalid resource contains whitespace or /o */
		String invalidString = "(^\\s.+\\s$)|((\\\\0)+)";
		
		/**invalid resource contains whitespace or /o */
		boolean invalidTag = false;
		for(String str: tags){
			if(Pattern.matches(invalidString, str)){
				invalidTag = true;
			}
		}
		boolean invalidResourceValue = Pattern.matches(invalidString, name)||Pattern.matches(invalidString, channel)||
				Pattern.matches(invalidString, description)||Pattern.matches(invalidString, uri)||
				Pattern.matches(invalidString, owner)||invalidTag;
		
		/** resource or secret field was not given or not of the correct type*/
		do {
			if(ClientSecret.equals("") || uri.equals("") || !Pattern.matches(filePathPattern, uri)){
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
					if (invalidResourceValue||owner.equals("*")) {
						
						/** resource contained incorrect information that could not be recovered from*/
						errorMessage = "invalid resource";
						response = "error";
						serverResponse.put(ConstantEnum.CommandType.response.name(),response);
						serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					}else{
						if(resources.containsKey(uri)){
							/**same URI, same channel,different owner */
							if(!resources.get(uri).owner.equals(owner)
									&&resources.get(uri).channel.equals(channel)){
								errorMessage = "cannot publish resource";
								response="error";
								serverResponse.put(ConstantEnum.CommandType.response.name(),response);
								serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
							}else{
								/**same primary key*/
								resources.remove(uri);
								
								
								Resource resource = new Resource(name, tags, description, uri, channel, owner);
								resources.put(resource.URI, resource);
								response = "success";
								success = true;
								serverResponse.put(ConstantEnum.CommandType.response.name(),response);
								
								//right here share mechanism is not implement yet.!!!
							}
						}else{
							/**valid URI*/
							
							Resource resource = new Resource(name, tags, description, uri, channel, owner);
							resources.put(resource.URI,resource);
					
							//need a share mechanism!!
							response = "success";
							success= true;
							serverResponse.put(ConstantEnum.CommandType.response.name(),response);				
						}
					}
				}	
			}	
		} while (serverResponse==null);
		
	return serverResponse;
	}
	
	
	/**relay暂时还没有实现*/
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
		System.out.println("here we are");

		/**Regexp for filePath*/
		String filePathPattern = "^[a-zA-Z*]:?([\\\\/]?|([\\\\/]([^\\\\/:\"<>|]+))*)[\\\\/]?$|^\\\\\\\\(([^\\\\/:\"<>|]+)[\\\\/]?)+$";
		/**Regexp for invalid resource contains whitespace or /o */
		String invalidString = "(^\\s.+\\s$)|((\\\\0)+)";
		
		
		/**invalid resource contains whitespace or \o */
		boolean invalidTag = false;
		for(String str: tags_query){
			if(Pattern.matches(invalidString, str)){
				invalidTag = true;
			}
		}
		
		boolean invalidResourceValue = Pattern.matches(invalidString, name_query)||Pattern.matches(invalidString, channel_query)||
				Pattern.matches(invalidString, description_query)||Pattern.matches(invalidString, uri_query)||
				Pattern.matches(invalidString, owner_query)||invalidTag;
		System.out.println("querying");
		
		boolean hasMacthResource = false;
		do{
			if(invalidResourceValue||owner_query.equals("*")){
				errorMessage = "invalid resourceTemplate";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
			}else{
				/*if(uri_query.equals("") || Pattern.matches(uri_query, filePathPattern)){
					errorMessage = "missing resourceTemplate";
					response = "error";
					System.out.println("aaa");
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
				}else*/	
					
					
					boolean tagIncluded = false;
					for(Resource resource : resources.values()){
						/**tagIncluded等于true如果所有template标签包含在候选资源的tags中*/
						
						int tagCount = 0;
						int tagLength = tags_query.length;
						
						if(tags_query.equals("")||resource.tag.equals("")){
							tagIncluded = true;
						}else{
							for(int i = 0; i<tags_query.length; i++){
								for(int j = 0; j<resource.tag.length; j++){
									if(tags_query[i].equals(resource.tag[j])){
										tagCount++;
									}
								}
							}
							if(tagCount>=tagLength){
								tagIncluded = true;
							}
						}
					}
					
					
					
					/** for query like -query with no parameter*/
					if(channel_query.equals("")&& owner_query.equals("") && uri_query.equals("") && name_query.equals("")
							&& description_query.equals("")){
						ArrayList<Resource> allResource = new ArrayList<Resource>();
						if(!resources.isEmpty()){
							for(Map.Entry<String, Resource> x:resources.entrySet()){
								allResource.add(x.getValue());
							}
							Integer size = 1;
							serverResponse.put(ConstantEnum.CommandType.response, "success");
							for(Resource resourceTemp: allResource){
								
								JSONObject MatchResouce = new JSONObject();
								MatchResouce.put(ConstantEnum.CommandArgument.name.name(), resourceTemp.name);
								JSONArray tagsArray = new JSONArray();
								for (String tag: resourceTemp.tag){
									tagsArray.add(tag);
								}
								MatchResouce.put(ConstantEnum.CommandArgument.tags.name(), tagsArray);
								MatchResouce.put(ConstantEnum.CommandArgument.description.name(), resourceTemp.description);
								MatchResouce.put(ConstantEnum.CommandArgument.uri.name(), resourceTemp.URI);
								MatchResouce.put(ConstantEnum.CommandArgument.channel.name(), resourceTemp.channel);
								
								/**if owner not "", replace it with * */
								if(resourceTemp.owner.equals("")){
									MatchResouce.put(ConstantEnum.CommandArgument.owner.name(), resourceTemp.name);
								}else{
									MatchResouce.put(ConstantEnum.CommandArgument.owner.name(), "*");
								}
								
								Integer ezport = serverSocket.getLocalPort();
								String ezserver = serverSocket.getInetAddress().toString()+":"+ezport.toString();
								MatchResouce.put(ConstantEnum.CommandArgument.ezserver.name(), ezserver);
								serverResponse.put("Resource"+size.toString(), MatchResouce);
								
								size++;
							}
							serverResponse.put(ConstantEnum.CommandType.resultSize, allResource.size());
							hasMacthResource = true;
						}else{
							errorMessage = "no resource in store";
							response = "error";
							serverResponse.put(ConstantEnum.CommandType.response.name(),response);
							serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
						}
						
					}else{
						for(Resource resource : resources.values()){
							
							/**owner or URI not ""*/ 
							//&& tagIncluded
							//channel is working
							//caution: tags name
							//String a = (b ==null) ? true : false ;
						
						boolean ownerMatch = (channel_query.equals("")) ? true: channel_query.equals(resource.channel);
						boolean uriMatch = (uri_query.equals("")) ?  true : uri_query.equals(resource.URI) ;
						
						//&& tagIncluded
								
						if((channel_query.equals(resource.channel)  && ownerMatch && uriMatch && ( (!name_query.equals("") && resource.name.contains(name_query))|| 
								(!description_query.equals("") && resource.description.contains(channel_query) )|| 
								(name_query.equals("")&&description_query.equals(""))))){
							System.out.println("match");
							/**将符合要求的资源放在MatchResourceSet里*/
							matchResourceSet.add(resource);
						}
						
							/*
							if((channel_query.equals(resource.channel)&& tagIncluded && (!owner_query.equals("") && owner_query.equals(resource.owner)) && 
									(!uri_query.equals("")&&uri_query.equals(resource.URI))  && ( (!name_query.equals("") && name_query.equals(resource.name))|| 
											(!description_query.equals("") && resource.description.contains(channel_query) )|| 
											(name_query.equals("")&&description_query.equals("")))) || 
									*//**owner or URI could be "" *//*
									((channel_query.equals(resource.channel)) && tagIncluded&& ( (!name_query.equals("") && name_query.equals(resource.name))|| 
													(!description_query.equals("") && resource.description.contains(channel_query) )|| 
													(name_query.equals("")&&description_query.equals(""))))
									){
							
								Resource matchResource = resource;
								
								hasMacthResource = true;
								System.out.println("match");
								*//**将符合要求的资源放在MatchResourceSet里*//*
								matchResourceSet.add(resource);
							}*/
						}
						if(!matchResourceSet.isEmpty()){
							success = true;
							response = "success";
							serverResponse.put(ConstantEnum.CommandType.response.name(), response);
							for(Resource resouce: matchResourceSet){
								JSONObject MatchResouce = new JSONObject();
								MatchResouce.put(ConstantEnum.CommandArgument.name.name(), resouce.name);
								JSONArray tagsArray = new JSONArray();
								for (String tag: resouce.tag){
									tagsArray.add(tag);
								}
								MatchResouce.put(ConstantEnum.CommandArgument.tags.name(), tagsArray);//------------------------------------------
								MatchResouce.put(ConstantEnum.CommandArgument.description.name(), resouce.description);
								MatchResouce.put(ConstantEnum.CommandArgument.uri.name(), resouce.URI);
								MatchResouce.put(ConstantEnum.CommandArgument.channel.name(), resouce.channel);
								System.out.println("233");
								/**if owner not "", replace it with * */
								if(resouce.owner.equals("")){
									MatchResouce.put(ConstantEnum.CommandArgument.owner.name(), resouce.name);
								}else{
									MatchResouce.put(ConstantEnum.CommandArgument.owner.name(), "*");
								}
								
								Integer ezport = serverSocket.getLocalPort();
	
								String ezserver = serverSocket.getInetAddress().toString()+":"+ezport.toString();
								MatchResouce.put(ConstantEnum.CommandArgument.ezserver.name(), ezserver);
								serverResponse.put(ConstantEnum.CommandType.resource.name(), MatchResouce);
							}
							serverResponse.put(ConstantEnum.CommandType.resultSize, matchResourceSet.size());
							hasMacthResource = true;
						}else{
							errorMessage = "invalid resourceTemplate";
							response = "error";
							System.out.println("dididi");
							serverResponse.put(ConstantEnum.CommandType.response.name(),response);
							serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
						}
						
					}
					
						
				} 
					
				
			
		}while(serverResponse==null);
		return serverResponse;
	}
	
	public synchronized static FetchResult handlingFetch(String name,String[] tags,
			String description, String uri,String channel, 
			String owner,HashMap<String, Resource> resources, ServerSocket serverSocket){
		ArrayList<Resource> matchResourceSet = new ArrayList<Resource>();
		String errorMessage;
		String response;
		JSONObject serverResponse = new JSONObject();
		
		/**a fetchResult store the server response and file data if fetch template is matched*/
		FetchResult fetchResult = new FetchResult();
		
		/**Regexp for filePath*/
		String filePathPattern = "^[a-zA-Z*]:?([\\\\/]?|([\\\\/]([^\\\\/:\"<>|]+))*)[\\\\/]?$|^\\\\\\\\(([^\\\\/:\"<>|]+)[\\\\/]?)+$";
		/**Regexp for invalid resource contains whitespace or /o */
		String invalidString = "(^\\s.+\\s$)|((\\\\0)+)";
		
		/**invalid resource contains whitespace or \o */
		boolean invalidTag = false;
		for(String str: tags){
			if(Pattern.matches(invalidString, str)){
				invalidTag = true;
			}
		}
		
		boolean invalidResourceValue = Pattern.matches(invalidString, name)||Pattern.matches(invalidString, channel)||
				Pattern.matches(invalidString, description)||Pattern.matches(invalidString, uri)||
				Pattern.matches(invalidString, owner)||invalidTag;
		do {
			if(invalidResourceValue || owner.equals("*")){
				errorMessage = "invalid resourceTemplate";
				response = "error";
				serverResponse.put(ConstantEnum.CommandType.response.name(),response);
				serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
				fetchResult = new FetchResult(serverResponse);
			}else{
				if(!uri.equals("") || !Pattern.matches(uri, filePathPattern)){
					errorMessage = "missing resourceTemplate";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					fetchResult = new FetchResult(serverResponse);
				}else{
					boolean hasMacthResource = false;
					hasMacthResource = resources.containsKey("uri");
					if (hasMacthResource) {
						response = "success";
						serverResponse.put(ConstantEnum.CommandType.response.name(), response);
						JSONObject MatchResource = new JSONObject();
						JSONObject matchResource = new JSONObject();
						
						/**nested resource as a jsonobject*/
						
						matchResource.put(ConstantEnum.CommandArgument.name.name(), resources.get(uri).name);
						matchResource.put(ConstantEnum.CommandArgument.tags.name(), resources.get(uri).tag);
						matchResource.put(ConstantEnum.CommandArgument.description.name(), resources.get(uri).description);
						matchResource.put(ConstantEnum.CommandArgument.uri.name(), resources.get(uri).URI);
						matchResource.put(ConstantEnum.CommandArgument.channel.name(),resources.get(uri).channel);
						matchResource.put(ConstantEnum.CommandArgument.owner.name(), resources.get(uri).name);
						Integer ezport = serverSocket.getLocalPort();
						String ezserver = serverSocket.getLocalSocketAddress().toString()+":"+ezport.toString();
						matchResource.put(ConstantEnum.CommandArgument.ezserver.name(), ezserver);
						/**length of the match file*/
						int resourceSize = (int) resources.get(uri).file.file.length();
						matchResource.put(ConstantEnum.CommandArgument.resourceSize.name(),resourceSize);
						
						serverResponse.put(ConstantEnum.CommandType.resource.name(), matchResource);
						fetchResult = new FetchResult(resources.get(uri), serverResponse);
						
					}else{
						errorMessage = "invalid resourceTemplate";
						response = "error";
						serverResponse.put(ConstantEnum.CommandType.response.name(),response);
						serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
						fetchResult = new FetchResult(serverResponse);
					}
					
				}
			}
		
		} while (serverResponse==null);
		
		
		return fetchResult;
	}
	
	
	

}
