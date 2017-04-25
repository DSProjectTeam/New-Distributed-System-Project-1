import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExchangeTask extends TimerTask{
	EZshareServer eZshareServer;
	public static boolean hasDebugOption;
	public ExchangeTask(EZshareServer ez, boolean hasDebugOption) {
		this.eZshareServer = ez;
		this.hasDebugOption = hasDebugOption;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		exchangeWithOtherServer(this.eZshareServer.serverList);
		System.out.println(this.eZshareServer.serverList.size());
	}

	
	public synchronized static void exchangeWithOtherServer(ArrayList<String> serverList){
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
					if(hasDebugOption){
						System.out.println("SENT: "+exchangeOutput.toJSONString());
					}
					System.out.println("command sent to server: "+exchangeOutput.toJSONString());
					
					//DataInputStream in = new DataInputStream(socket.getInputStream());
					/*while(true){
						if(in.available()>0){
							String responseMessage = in.readUTF();							
							JSONObject jsonObject;
							JSONObject sendResponse;
							JSONParser parser = new JSONParser();
							jsonObject = (JSONObject) parser.parse(responseMessage);
							if(hasDebugOption){
								System.out.println("RECEIVED: "+jsonObject.toJSONString());
							}
							JSONArray serverListJSONArray = (JSONArray) jsonObject.get("serverList");// need to deal with "serverList" missing	!
							ArrayList<String> serverList_exchange = new ArrayList<>();
							ArrayList<String> hostnameList_exchange = new ArrayList<>();
							ArrayList<String> portList_exchange = new ArrayList<>();
							if(serverListJSONArray.size()>0){
								for(int i=0; i<serverListJSONArray.size(); i++){
									JSONObject serverJSONObject = (JSONObject)serverListJSONArray.get(i);
									String hostname = serverJSONObject.get("hostname").toString();
									String port = serverJSONObject.get("port").toString();
									String hostnameAndPort = hostname+":"+port;
									hostnameList_exchange.add(hostname);
									portList_exchange.add(port);
									serverList_exchange.add(hostnameAndPort);
								}
							}
							sendResponse = ServerHandler.handlingExchange(serverList, serverList_exchange, hostnameList_exchange, portList_exchange);
							out.writeUTF(sendResponse.toJSONString());
							out.flush();
							if(hasDebugOption){
								System.out.println("SENT: "+sendResponse.toJSONString());
							}
						}
					}	
			} catch (ParseException e) {
				e.printStackTrace();
			}*/ }catch (ConnectException e) {
				serverList.remove(randomIndex);
				System.out.println("The server is not reachable, so it has been removed from serverList");
			}catch (IOException e) {
				e.printStackTrace();
			}
		       
		      
				
		 }else{
			 System.out.println("empty server list");
		 }
	}
	
}
