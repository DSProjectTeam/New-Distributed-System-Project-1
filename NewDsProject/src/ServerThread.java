import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
				
			/*	String [] tags = (String[]) resource_publish.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag = tagTolist(tags);
				String name = (String) resource_publish.get(ConstantEnum.CommandArgument.name.name());
				System.out.println(name.length()+" "+name);
				String description = (String) resource_publish.get(ConstantEnum.CommandArgument.description.name());
				String uri = (String) resource_publish.get(ConstantEnum.CommandArgument.uri.name());
				String channel = (String) resource_publish.get(ConstantEnum.CommandArgument.channel.name());
				String owner = (String) resource_publish.get(ConstantEnum.CommandArgument.owner.name());*/
				//EZserver is not here!
				
				/**get response with the publish command*/
				
				/*sendResponse = ServerHandler.handlingPublish(new Resource(name, tag, 
						description, uri, channel, owner),this.resources);*/
				
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
				
				String [] tags_fetch = (String[]) fecthTemplate.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_fetch = tagTolist(tags_fetch);
				String name_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.name.name());
				String description_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.description.name());
				String uri_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.uri.name());
				String channel_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.channel.name());
				String owner_fetch = (String) fecthTemplate.get(ConstantEnum.CommandArgument.owner.name());
				
				fetchResult = ServerHandler.handlingFetch(name_fetch, tags_fetch, description_fetch, uri_fetch, channel_fetch, owner_fetch, this.resources,this.serverSocket);
				if(fetchResult.resource == null){
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
						/**jsonobject of a match resource*/
						output.writeUTF(fetchResult.serverResponse.toJSONString());
						
						/**convert file to the byte array for transmission*/
						Path path = fetchResult.resource.file.file.toPath();
						byte[] fileData = Files.readAllBytes(path);
						output.write(fileData);
						
						/**put resource size in a jsonobject*/
						JSONObject resourceSize = new JSONObject();
						resourceSize.put(ConstantEnum.CommandArgument.resourceSize.name(), 1);
						
						output.writeUTF(resourceSize.toJSONString());
						output.flush();
						System.out.println(Thread.currentThread().getName()+":sending response message!");
						
					} catch (IOException e) {
						System.err.println(Thread.currentThread().getName() + ":Error while sending");
					}
					
				}
				
				
				break;
			case "QUERY":
				JSONObject template_resource = (JSONObject)jsonObject.get("resourceTemplate");
				boolean relay1;
				
				String [] tags_query = template_resource.get(ConstantEnum.CommandArgument.tags.name()).toString().split(",");
				String name_query = template_resource.get(ConstantEnum.CommandArgument.name.name()).toString();
				String description_query = template_resource.get(ConstantEnum.CommandArgument.description.name()).toString();
				String uri_query = template_resource.get(ConstantEnum.CommandArgument.uri.name()).toString();
				System.out.println("555");
				String channel_query = template_resource.get(ConstantEnum.CommandArgument.channel.name()).toString();
				String owner_query = template_resource.get(ConstantEnum.CommandArgument.owner.name()).toString();
				System.out.println("555");
				String relay = jsonObject.get(ConstantEnum.CommandArgument.relay.name()).toString();
				if(relay.equals("")){
					relay1 = true;
				}else{
					relay1 = false;
				}
				
				sendResponse = ServerHandler.handlingQuery(name_query, tags_query, description_query, uri_query, channel_query, owner_query,relay1,this.resources, this.serverSocket);
				
				sendMessage(sendResponse);
				break;
			case "EXCHANGE":
					
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
	
	
	
}
