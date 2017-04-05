import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerThread extends Thread{
	Socket clientSocket;
	private HashMap<String, Resource> resources;
	
	private BufferedReader input;
	
	private BufferedWriter output;
	
	public ServerThread(Socket socket, HashMap<String, Resource> resources){
		try {
			this.clientSocket = socket;
			this.resources = resources;	
			this.output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
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
				ServerHandler.handlingPublish(new Resource(name, tag, description, uri, channel, owner),this.resources);
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
				ServerHandler.handlingRemove(new Resource(name_remove, tag_remove, description_remove, uri_remove, channel_remove, owner_remove));
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
				ServerHandler.HandlingShare(new Resource(name_share, tag_share, description_share, uri_share, channel_share, owner_share),secret_share);
				break;
			case fetch:
				
				break;
			case query:
				
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
	
	public static  ArrayList<String> tagTolist (String[] str){
		ArrayList<String> list = new ArrayList<String>();
		for(String string: str){
			list.add(string);
			}
		return list;
	}
	
	
	
}
