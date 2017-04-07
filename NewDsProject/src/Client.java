import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLNonTransientConnectionException;
import java.text.ParseException;
import java.time.chrono.JapaneseChronology;
import java.util.logging.Handler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Client {
	public static String ip = "localhost";
	public static String ip2 = "192.168.1.110";
	public static int port = 3000;
	
	public static void main(String[] args){
		try {
			Socket socket = new Socket(ip2,port);
			//inputStream
			DataInputStream in = new DataInputStream(socket.getInputStream());
			//outputSteam
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			JSONObject userinput = handleClientInput();
			out.writeUTF(userinput+"\n");
		
			out.flush();
			
			while(true){
				if(in.available()>0){
					String responseMessage = in.readUTF();
					handleServerResponse(responseMessage);
					
					/*String message = in.readUTF();
					System.out.println(message);*/
				}
			}
			
			
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public static JSONObject handleClientInput(){
		return null;
	}
	
	public static void handleServerResponse(String input){
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		JSONObject sendClientInput;
		
		try {
			jsonObject = (JSONObject)parser.parse(input);
			String response =  (String)jsonObject.get(ConstantEnum.CommandType.response.name());
			switch (response) {
			case "success":
				
				break;
			
			case "error":

			default:
				break;
			}
		} catch (org.json.simple.parser.ParseException e) {
			
			e.printStackTrace();
		}
	}
}
