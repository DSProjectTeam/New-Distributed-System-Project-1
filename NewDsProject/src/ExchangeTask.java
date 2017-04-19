import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ExchangeTask extends TimerTask{
	EZshareServer eZshareServer;
	public ExchangeTask(EZshareServer ez) {
		this.eZshareServer = ez;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		exchangeWithOtherServer(this.eZshareServer.serverList);
	}

	
	public static void exchangeWithOtherServer(ArrayList<String> serverList){
	    if(!serverList.isEmpty()){
	    		   JSONObject exchangeOutput = new JSONObject();
	    		   JSONArray serversJSONArray = new JSONArray();
		       for (int i=0; i<serverList.size(); i++){
		    	   		JSONObject temp = new JSONObject();
		    	   		String[] hostnameAndPort = serverList.get(i).split(":");
		    	   		temp.put("hostname", hostnameAndPort[0]);
		    	   		temp.put("port", hostnameAndPort[1]);  
		    	   		serversJSONArray.add(temp);
		       }
		       exchangeOutput.put(ConstantEnum.CommandType.command.name(),"EXCHANGE");
		       exchangeOutput.put(ConstantEnum.CommandArgument.serverList.name(),serversJSONArray); 
		       
		       Random randomGenerator = new Random();
		       int randomIndex = randomGenerator.nextInt(serverList.size());
		       String[] randomHostnameAndPort = serverList.get(randomIndex).split(":");
		       String randomHostname = randomHostnameAndPort[0];
		       int randomPort = Integer.parseInt(randomHostnameAndPort[1]);
		       
		       try {
		    	   Socket socket = new Socket(randomHostname,randomPort);
		    	   DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(exchangeOutput.toJSONString());
					out.flush();
					System.out.println("command sent to server: "+exchangeOutput.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		       
		      
				
		 }
	}
	
}
