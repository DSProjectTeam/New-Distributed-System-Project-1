import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	
	public ServerThread(Socket socket, HashMap<String, Resource> resources, String secret){
		try {
			this.clientSocket = socket;
			this.resources = resources;	
			this.secret = secret;
			this.output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"));
			this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
			
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
				String [] tags = (String[]) jsonObject.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag = tagTolist(tags);
				String name = (String) jsonObject.get(ConstantEnum.CommandArgument.name.name());
				String description = (String) jsonObject.get(ConstantEnum.CommandArgument.description.name());
				String uri = (String) jsonObject.get(ConstantEnum.CommandArgument.uri.name());
				String channel = (String) jsonObject.get(ConstantEnum.CommandArgument.channel.name());
				String owner = (String) jsonObject.get(ConstantEnum.CommandArgument.owner.name());
				//EZserver is not here!
				
				/**get response with the publish command*/
				sendResponse = ServerHandler.handlingPublish(new Resource(name, tag, 
						description, uri, channel, owner),this.resources);
				sendMessage(sendResponse);
				
				break;
			case remove:
				String [] tags_remove = (String[]) jsonObject.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_remove = tagTolist(tags_remove);
				String name_remove = (String) jsonObject.get(ConstantEnum.CommandArgument.name.name());
				String description_remove = (String) jsonObject.get(ConstantEnum.CommandArgument.description.name());
				String uri_remove = (String) jsonObject.get(ConstantEnum.CommandArgument.uri.name());
				String channel_remove = (String) jsonObject.get(ConstantEnum.CommandArgument.channel.name());
				String owner_remove = (String) jsonObject.get(ConstantEnum.CommandArgument.owner.name());
				//EZserver is not here!
				
				/**get response with the remove command*/
				sendResponse = ServerHandler.handlingRemove(new Resource(name_remove, tag_remove, description_remove, 
						uri_remove, channel_remove, owner_remove),this.resources);
				sendMessage(sendResponse);
				
				break;
			case share:
				String [] tags_share = (String[]) jsonObject.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_share = tagTolist(tags_share);
				String name_share = (String) jsonObject.get(ConstantEnum.CommandArgument.name.name());
				String description_share = (String) jsonObject.get(ConstantEnum.CommandArgument.description.name());
				String uri_share = (String) jsonObject.get(ConstantEnum.CommandArgument.uri.name());
				String channel_share = (String) jsonObject.get(ConstantEnum.CommandArgument.channel.name());
				String owner_share = (String) jsonObject.get(ConstantEnum.CommandArgument.owner.name());
				String secret_share = (String) jsonObject.get(ConstantEnum.CommandArgument.secret.name());
				//EZserver is not here!
				
				/**get response with the share command*/
				sendResponse = ServerHandler.HandlingShare(new Resource(name_share, tag_share, description_share, uri_share, channel_share, owner_share),
						secret_share,this.secret,this.resources);
				break;
			case fetch:
				
				break;
			case query:
				String [] tags_query = (String[]) jsonObject.get(ConstantEnum.CommandArgument.tags.name());
				ArrayList<String> tag_query = tagTolist(tags_query);
				String name_query = (String) jsonObject.get(ConstantEnum.CommandArgument.name.name());
				String description_query = (String) jsonObject.get(ConstantEnum.CommandArgument.description.name());
				String _query = (String) jsonObject.get(ConstantEnum.CommandArgument.uri.name());
				String channel_query = (String) jsonObject.get(ConstantEnum.CommandArgument.channel.name());
				String owner_query = (String) jsonObject.get(ConstantEnum.CommandArgument.owner.name());
				
				
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
			output.write(message+"\n");
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
