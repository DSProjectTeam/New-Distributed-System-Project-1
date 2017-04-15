import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.xml.ws.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.simple.JSONArray;

public class ServerThread extends Thread{
	
	Socket clientSocket;
	
	private HashMap<String, Resource> resources;
	
	private DataInputStream input;
	
	private DataOutputStream output;
	
	private String secret;
	
	public ServerSocket serverSocket;
	
	private ArrayList<String> serverList;
	
	public FetchResult fetchResult;
	
	public ServerThread(Socket socket, HashMap<String, Resource> resources, String secret, ServerSocket serverSocket, ArrayList<String> serverList){
		try {
			this.clientSocket = socket;
			this.resources = resources;	
			this.secret = secret;
			this.output = new DataOutputStream(clientSocket.getOutputStream());
			this.input = new DataInputStream(clientSocket.getInputStream());
			this.serverSocket = serverSocket;
			this.serverList = serverList;
		} catch (IOException e) {
			if(clientSocket!=null){
				try {
					clientSocket.close();
				} catch (IOException e2) {
					
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {
			String inputMessage = input.readUTF();
			System.out.println("123");
			handleCommand(inputMessage);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public synchronized void handleCommand (String string){
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		JSONObject sendResponse;
		JSONObject localResponse;
		JsonArray jsonArray;
		ArrayList<JSONObject> otherResponse = new ArrayList<>();
		try {
			jsonObject = (JSONObject) parser.parse(string);
			/*ConstantEnum.CommandType command  = ConstantEnum.CommandType.valueOf((String)jsonObject.get("command"));*/
			String command = (String) jsonObject.get(ConstantEnum.CommandType.command.name());
			System.out.println("456");
						switch (command) {
			case "DEBUG":
				
				break;
			case "PUBLISH":
				/**取出嵌套在jsonObject中的resource字段（同样也是jsonObjecy）*/
				JSONObject resource_publish = (JSONObject) jsonObject.get("resource");
				System.out.println(resource_publish.toJSONString());
				
				String [] tags = resource_publish.get(ConstantEnum.CommandArgument.tags.name()).toString().split(",");
				ArrayList<String> tag = tagTolist(tags);
				String name = resource_publish.get(ConstantEnum.CommandArgument.name.name()).toString();
				System.out.println(name.length()+" "+name);
				String description = resource_publish.get(ConstantEnum.CommandArgument.description.name()).toString();
				String uri = resource_publish.get(ConstantEnum.CommandArgument.uri.name()).toString();
				String channel = resource_publish.get(ConstantEnum.CommandArgument.channel.name()).toString();
				String owner = resource_publish.get(ConstantEnum.CommandArgument.owner.name()).toString();
	
				
				sendResponse = ServerHandler.handlingPublish(name,tags,description,uri,channel,owner,this.resources);
				sendMessage(sendResponse);
				
				break;
			case "REMOVE":
				JSONObject resource_remove = (JSONObject) jsonObject.get("resource");
				
				String [] tags_remove = resource_remove.get(ConstantEnum.CommandArgument.tags.name()).toString().split(",");
				String name_remove = resource_remove.get(ConstantEnum.CommandArgument.name.name()).toString();
				
				String description_remove = resource_remove.get(ConstantEnum.CommandArgument.description.name()).toString();
			
				String uri_remove = resource_remove.get(ConstantEnum.CommandArgument.uri.name()).toString();
				
				String channel_remove = resource_remove.get(ConstantEnum.CommandArgument.channel.name()).toString();
			
				String owner_remove = resource_remove.get(ConstantEnum.CommandArgument.owner.name()).toString();
				//EZserver is not here!
			
				/**get response with the remove command*/
				/*sendResponse = ServerHandler.handlingRemove(new Resource(name_remove, tag_remove, description_remove, 
						uri_remove, channel_remove, owner_remove),this.resources);*/
				sendResponse = ServerHandler.handlingRemove(name_remove,tags_remove,description_remove,uri_remove,channel_remove,owner_remove,this.resources);
				
				
				sendMessage(sendResponse);
				
				break;
			case "SHARE":
				JSONObject resource_share = (JSONObject) jsonObject.get("resource");
				System.out.println("sharing!");
				String [] tags_share = resource_share.get(ConstantEnum.CommandArgument.tags.name()).toString().split(",");
				String name_share = resource_share.get(ConstantEnum.CommandArgument.name.name()).toString();
				String description_share = resource_share.get(ConstantEnum.CommandArgument.description.name()).toString();
				String uri_share = resource_share.get(ConstantEnum.CommandArgument.uri.name()).toString();
				String channel_share = resource_share.get(ConstantEnum.CommandArgument.channel.name()).toString();
				String owner_share = resource_share.get(ConstantEnum.CommandArgument.owner.name()).toString();
				String secret_share = jsonObject.get(ConstantEnum.CommandArgument.secret.name()).toString();
				
				System.out.println("7");
				//EZserver is not here!
				
				/**get response with the share command*/					
				sendResponse = ServerHandler.HandlingShare(name_share, tags_share, description_share, uri_share, 
						channel_share, owner_share, secret_share,this.secret,this.resources);
				sendMessage(sendResponse);
				break;
			case "FETCH":
				JSONObject fecthTemplate = (JSONObject) jsonObject.get("resourceTemplate");
				
				String [] tags_fetch = (String[]) fecthTemplate.get(ConstantEnum.CommandArgument.tags.name()).toString().split(",");
				String name_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.name.name());
				String description_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.description.name());
				String uri_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.uri.name());
				String channel_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.channel.name());
				String owner_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.owner.name());
				
				handlingFetch(name_fetch, tags_fetch, description_fetch, uri_fetch, channel_fetch, owner_fetch, resources, serverSocket);
				
				/*if(fetchResult.resource == null){
					try {
						output.writeUTF(fetchResult.serverResponse.toJSONString());
						output.flush();
						System.out.println(Thread.currentThread().getName()+":sending response message!");
					} catch (IOException e) {
						e.printStackTrace();
						System.err.println(Thread.currentThread().getName() + ":Error while sending");
					}
				}else{
					try {
						*//**jsonobject of a match resource*//*
						output.writeUTF(fetchResult.serverResponse.toJSONString());
						
						*//**convert file to the byte array for transmission*//*
						Path path = fetchResult.resource.file.file.toPath();
						byte[] fileData = Files.readAllBytes(path);
						output.write(fileData);
						
						*//**put resource size in a jsonobject*//*
						JSONObject resourceSize = new JSONObject();
						resourceSize.put(ConstantEnum.CommandArgument.resourceSize.name(), 1);
						
						output.writeUTF(resourceSize.toJSONString());
						output.flush();
						System.out.println(Thread.currentThread().getName()+":sending response message!");
						
					} catch (IOException e) {
						System.err.println(Thread.currentThread().getName() + ":Error while sending");
					}
					
				}*/
				
				
				break;
			case "QUERY":
				
				JSONObject template_resource = (JSONObject)jsonObject.get("resourceTemplate");
				boolean relay1;
				
				String [] tags_query = template_resource.get(ConstantEnum.CommandArgument.tags.name()).toString().split(",");
				String name_query = template_resource.get(ConstantEnum.CommandArgument.name.name()).toString();
				String description_query = template_resource.get(ConstantEnum.CommandArgument.description.name()).toString();
				String uri_query = template_resource.get(ConstantEnum.CommandArgument.uri.name()).toString();
				String channel_query = template_resource.get(ConstantEnum.CommandArgument.channel.name()).toString();
				String owner_query = template_resource.get(ConstantEnum.CommandArgument.owner.name()).toString();
				String relay = jsonObject.get(ConstantEnum.CommandArgument.relay.name()).toString();
				if(relay.equals("")){
					relay1 = true;
				}else{
					relay1 = false;
				}
				/*if(relay1==false){
					sendResponse = ServerHandler.handlingQuery(name_query, tags_query, description_query, uri_query, channel_query, owner_query,relay1,this.resources, this.serverSocket);
				}else{
					localResponse = ServerHandler.handlingQuery(name_query, tags_query, description_query, uri_query, channel_query, owner_query,relay1,this.resources, this.serverSocket);
					otherResponse = ServerHandler.handlingQueryWithRelay(string, this.resources, this.serverSocket, this.serverList);
					sendResponse = handleRelay(otherResponse, localResponse);
				}*/
				QueryReturn queryReturn = ServerHandler.handlingQuery(name_query, tags_query, description_query, uri_query, channel_query, owner_query,relay1,this.resources, this.serverSocket);
				
				if (queryReturn.hasMatch==false) {
					sendMessage(queryReturn.reponseMessage);
				}else{
					try {
						output.writeUTF(queryReturn.returnArray.toString());
						output.flush();
						System.out.println(Thread.currentThread().getName()+": has matched,sending response message!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println(Thread.currentThread().getName() + ":Error while sending");
					}
				}
				
				break;
			case "EXCHANGE":
				JSONArray serverListJSONArray = (JSONArray) jsonObject.get("serverList");// need to deal with "serverList" missing	!
				ArrayList<String> serverList_exchange = new ArrayList<>();
				ArrayList<String> hostnameList_exchange = new ArrayList<>();
				ArrayList<String> portList_exchange = new ArrayList<>();
				for(/*JSONObject serversJSONObject:serverListJSONArray*/int i=0; i<serverListJSONArray.size(); i++){
					JSONObject serverJSONObject = (JSONObject)serverListJSONArray.get(i);
					String hostname = serverJSONObject.get("hostname").toString();
					String port = serverJSONObject.get("port").toString();
					String hostnameAndPort = hostname+":"+port;
					hostnameList_exchange.add(hostname);
					portList_exchange.add(port);
					serverList_exchange.add(hostnameAndPort);
				}
				sendResponse = ServerHandler.handlingExchange(serverList, serverList_exchange, hostnameList_exchange, portList_exchange);
				sendMessage(sendResponse);
				break;
			default:
				break;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**send response message from the server*/
	public synchronized void sendMessage(JSONObject message){
		try {
			output.writeUTF(message.toJSONString());
			output.flush();
			System.out.println(Thread.currentThread().getName()+":sending response message!");
			
		} catch (IOException e) {
			System.err.println(Thread.currentThread().getName() + ":Error while sending");
		}
	}
	
	public static  ArrayList<String> tagTolist (String[] str){
		ArrayList<String> list = new ArrayList<String>();
		for(String string: str){
			list.add(string);
			}
		return list;
	}
	
	public synchronized void handlingFetch(String name,String[] tags,
			String description, String uri,String channel, 
			String owner,HashMap<String, Resource> resources, ServerSocket serverSocket){
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
				try {
					this.output.writeUTF(serverResponse.toJSONString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				if(!uri.equals("") || !Pattern.matches(uri, filePathPattern)){
					errorMessage = "missing resourceTemplate";
					response = "error";
					serverResponse.put(ConstantEnum.CommandType.response.name(),response);
					serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
					try {
						this.output.writeUTF(serverResponse.toJSONString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					boolean hasMacthResource = false;
					hasMacthResource = resources.get("uri").equals(uri)&&resources.get("channel").equals(channel);
					
					if (hasMacthResource) {
						File file = new File(uri);
						
						if(file.exists()){
							JSONObject matchResource = new JSONObject();
							JSONArray jsonArray = new JSONArray();		
							
							response = "success";
							serverResponse.put(ConstantEnum.CommandType.response.name(), response);
						
							jsonArray.add(serverResponse);
							
							matchResource.put(ConstantEnum.CommandArgument.name.name(), resources.get(uri).name);
							matchResource.put(ConstantEnum.CommandArgument.tags.name(), resources.get(uri).tag);
							matchResource.put(ConstantEnum.CommandArgument.description.name(), resources.get(uri).description);
							matchResource.put(ConstantEnum.CommandArgument.uri.name(), resources.get(uri).URI);
							matchResource.put(ConstantEnum.CommandArgument.channel.name(),resources.get(uri).channel);
							
							/**if owner not "", replace it with * */
							if(resources.get(uri).owner.equals("")){
								matchResource.put(ConstantEnum.CommandArgument.owner.name(), resources.get(uri).owner);
							}else{
								matchResource.put(ConstantEnum.CommandArgument.owner.name(), "*");
							}
							
							Integer ezport = serverSocket.getLocalPort();
							String ezserver = serverSocket.getLocalSocketAddress().toString()+":"+ezport.toString();
							matchResource.put(ConstantEnum.CommandArgument.ezserver.name(), ezserver);
							matchResource.put(ConstantEnum.CommandArgument.resourceSize.name(), file.length());
							
							jsonArray.add(matchResource);
							
							try {
								this.output.writeUTF(jsonArray.toJSONString());
								
								// start sending file
								RandomAccessFile byteFile = new RandomAccessFile(file, "r");
								byte[] sendingBuffer = new byte[1024*1024];
								int num;
								while((num = byteFile.read(sendingBuffer))>0){
									System.out.println(num);
									this.output.write(Arrays.copyOf(sendingBuffer, num));
								}
								byteFile.close();
								
								
							} catch (IOException e) {
								e.printStackTrace();
							}		
						}					
					}else{
						errorMessage = "invalid resourceTemplate";
						response = "error";
						serverResponse.put(ConstantEnum.CommandType.response.name(),response);
						serverResponse.put(ConstantEnum.CommandArgument.errorMessage.name(), errorMessage);
						try {
							this.output.writeUTF(serverResponse.toJSONString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}
		
		} while (serverResponse==null);

	}
	
	/*public synchronized JSONObject handleRelay(ArrayList<JSONObject> otherResponse,JSONObject localResponse){
		*//**other server response error, return local response*//*
		JSONObject response = new JSONObject();
		if(otherResponse==null){
			return localResponse;
		}else{
			*//**local query error, return other server's response*//*
			if(localResponse.get(ConstantEnum.CommandType.response.name()).equals("error")){
				
				*//**other server error*//*
				if(otherResponse.get(0).get("response").equals("error")){
					return localResponse;
					
				*//**other server success*//*
				}else{
					response.put("response", "success");
					*//**store every jsonobject(resource) from other server*//*
					JSONObject resources = new JSONObject();
					
					int size=0;
					Integer count = 1;
					for(JSONObject jsonObject:otherResponse){
						size =  size+Integer.parseInt(jsonObject.get("resultSize").toString());
						response.put("resource"+count.toString(), jsonObject.get("resource"));
						count++;
					}
					response.put("resultSize", size);
					return response;					
				}
				
			}else{
				*//**local response success, other server response success, merge*//*
				int localSize = Integer.parseInt(localResponse.get("resultSize").toString());
				int otherSize = 0;
				Integer count = 1;
				response.put("response", "success");
				for(JSONObject jsonObject:otherResponse){
					otherSize =  otherSize+Integer.parseInt(jsonObject.get("resultSize").toString());
					response.put("resource"+count.toString(), jsonObject.get("resource"));
				}
				response.put("resource", value)
				
				
				JSONArray jsonArray = new JSONArray();
				
			}
		}
		
		
		return null;
		
	}*/
	
	
	
}
