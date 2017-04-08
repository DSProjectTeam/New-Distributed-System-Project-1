import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerThread extends Thread{
	Socket clientSocket;
	private HashMap<String, Resource> resources;
	
	private BufferedReader input;
	
	private BufferedWriter output;
	
	private String secret;
	
	public ServerSocket serverSocket;
	
	public ServerThread(Socket socket, HashMap<String, Resource> resources, String secret, ServerSocket serverSocket){
		try {
			this.clientSocket = socket;
			this.resources = resources;	
			this.secret = secret;
			this.output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"));
			this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
			this.serverSocket = serverSocket;
		} catch (IOException e) {
			if(clientSocket!=null){
				try {
					clientSocket.close();
				} catch (IOException e2) {
					// TODO: handle exception
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {
			String inputMessage = input.readLine();
			handleCommand(inputMessage);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void handleCommand (String string){
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		JSONObject sendResponse;
		try {
			jsonObject = (JSONObject) parser.parse(string);
			ConstantEnum.CommandType command  = ConstantEnum.CommandType.valueOf((String)jsonObject.get("command"));
						switch (command) {
			case debug:
				
				break;
			case publish:
				/**取出嵌套在jsonObject中的resource字段（同样也是jsonObjecy）*/
				JSONObject resource_publish = (JSONObject) jsonObject.get("resource");
				String [] tags = (String[]) resource_publish.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag = tagTolist(tags);
				String name = (String) resource_publish.get(ConstantEnum.CommandArgument.name.name());
				String description = (String) resource_publish.get(ConstantEnum.CommandArgument.description.name());
				String uri = (String) resource_publish.get(ConstantEnum.CommandArgument.uri.name());
				String channel = (String) resource_publish.get(ConstantEnum.CommandArgument.channel.name());
				String owner = (String) resource_publish.get(ConstantEnum.CommandArgument.owner.name());
				//EZserver is not here!
				
				/**get response with the publish command*/
				sendResponse = ServerHandler.handlingPublish(new Resource(name, tag, 
						description, uri, channel, owner),this.resources);
				sendMessage(sendResponse);
				
				break;
			case remove:
				JSONObject resource_remove = (JSONObject) jsonObject.get("resource");
				
				String [] tags_remove = (String[]) resource_remove.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_remove = tagTolist(tags_remove);
				String name_remove = (String) resource_remove.get(ConstantEnum.CommandArgument.name.name());
				String description_remove = (String) resource_remove.get(ConstantEnum.CommandArgument.description.name());
				String uri_remove = (String) resource_remove.get(ConstantEnum.CommandArgument.uri.name());
				String channel_remove = (String) resource_remove.get(ConstantEnum.CommandArgument.channel.name());
				String owner_remove = (String) resource_remove.get(ConstantEnum.CommandArgument.owner.name());
				//EZserver is not here!
				
				/**get response with the remove command*/
				sendResponse = ServerHandler.handlingRemove(new Resource(name_remove, tag_remove, description_remove, 
						uri_remove, channel_remove, owner_remove),this.resources);
				sendMessage(sendResponse);
				
				break;
			case share:
				JSONObject resource_share = (JSONObject) jsonObject.get("resource");
				
				String [] tags_share = (String[]) resource_share.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_share = tagTolist(tags_share);
				String name_share = (String) resource_share.get(ConstantEnum.CommandArgument.name.name());
				String description_share = (String) resource_share.get(ConstantEnum.CommandArgument.description.name());
				String uri_share = (String) resource_share.get(ConstantEnum.CommandArgument.uri.name());
				String channel_share = (String) resource_share.get(ConstantEnum.CommandArgument.channel.name());
				String owner_share = (String) resource_share.get(ConstantEnum.CommandArgument.owner.name());
				String secret_share = (String) resource_share.get(ConstantEnum.CommandArgument.secret.name());
				//EZserver is not here!
				
				/**get response with the share command*/
				sendResponse = ServerHandler.HandlingShare(new Resource(name_share, tag_share, description_share, uri_share, channel_share, owner_share),
						secret_share,this.secret,this.resources);
				sendMessage(sendResponse);
				break;
			case fetch:
				
				break;
			case query:
				JSONObject template_resource = (JSONObject)jsonObject.get("resourceTemplate");
				
				Boolean relay = (Boolean) template_resource.get(ConstantEnum.CommandArgument.relay.name());
				String [] tags_query = (String[]) template_resource.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_query = tagTolist(tags_query);
				String name_query = (String) template_resource.get(ConstantEnum.CommandArgument.name.name());
				String description_query = (String) template_resource.get(ConstantEnum.CommandArgument.description.name());
				String uri_query = (String) template_resource.get(ConstantEnum.CommandArgument.uri.name());
				String channel_query = (String) template_resource.get(ConstantEnum.CommandArgument.channel.name());
				String owner_query = (String) template_resource.get(ConstantEnum.CommandArgument.owner.name());
				
				sendResponse = ServerHandler.handlingQuery(name_query, tags_query, description_query, uri_query, channel_query, owner_query,relay,this.resources, this.serverSocket);
				
				sendMessage(sendResponse);
				break;
			case exchange:
					
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
			output.write(message.toJSONString());
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
