
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

public class ClientThread extends Thread{
	Socket clientSocket;

	private BufferedReader input;
	
	private BufferedWriter output;
	
	
	
	public ClientThread(Socket socket){
		try {
			this.clientSocket = socket;
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
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	public void getReadyAndSend(String args[]){
		JSONObject publish = new JSONObject();
		String command = args[0];
		switch (command){//要考虑command为空的错误情况
		case "-publish":
			
			publish.put(ConstantEnum.CommandType.publish.publish, command);//command等的值均来自ClientHandler.java中command的值
			publish.put("name",name);
			publish.put("tags",tags);//注意格式
			publish.put("description",description);
			publish.put("uri",uri);
			publish.put("channel",name);
			publish.put("owner",owner);
			publish.put("ezserver",ezserver);
			
			break;
			
		case "-remove":
			break;
			
		default: break;
		}
	}
	

		try {//不会写哭哭😢 >...<
			out.write("?");
			out.flush();
		} catch (IOException e) {
			
		}
	
	
	
	
	
	
	
	
	public void handleResponse (String string){
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) parser.parse(string);
			ConstantEnum.CommandType command  = ConstantEnum.CommandType.valueOf((String)jsonObject.get("command"));
			
			switch (command) {
			case debug:	
				break;
			case publish:
				String resoponse = (String) jsonObject.get("resoponse");
				String errorMessage = (String) jsonObject.get("errorMessage");
				System.out.println("{\"response\" : "+response+"\",\n\"errorMessage\" : \""+errorMessage+"\"}");
				break;
			case remove:
				break;
			case share:
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
	
	
	
	
	
}