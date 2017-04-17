import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import org.json.simple.JSONArray;
import org.json.JSONArray;

/**
 * This class is the only class for Client.
 * @author zizheruan
 *
 */
public class Client {
	public static String ip = "localhost";
	public static String ip2 = "sunrise.cis.unimelb.edu.au";
	public static String ip3 = "10.12.162.15";
	public static int port = 3780;
	public static String commandType;
	
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args){
		try {
			Socket socket = new Socket(ip2,port);
			//inputStream
			DataInputStream in = new DataInputStream(socket.getInputStream());
			//outputSteam
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			commandType = "";
			JSONObject userInput = handleClientInput(args);
			out.writeUTF(userInput.toJSONString());
			out.flush();
			System.out.println("command sent to server: "+userInput.toJSONString());
			
			while(true){//当返回多个包时，in.available始终大于0，接受多个包，在此期间String commandType值不变
				if(in.available()>0){
					String responseMessage = in.readUTF();
					handleServerResponse(responseMessage, in);
					
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * read the user's input in argument, put the content into JSONObject, then return the JSONObeject. 
	 * @param args
	 * @return The JSONObeject
	 */
	public static JSONObject handleClientInput(String[] args){
		
		if (args[0].equals("-publish")||args[0].equals("-remove")||args[0].equals("-share")||
				args[0].equals("-query")||args[0].equals("-fetch")||args[0].equals("-exchange")){
			commandType = args[0];//extract command type, so we still remember the command type when handling response.
	    		String[] argsWithCommand = new String[args.length+1];
	    		argsWithCommand[0] = "-command";
	    		System.arraycopy(args, 0, argsWithCommand, 1, args.length);
	    		
	    		args = new String [args.length+1];
	    		System.arraycopy(argsWithCommand, 0, args, 0, argsWithCommand.length);
	    		//System.out.println(args.length+""+argsWithCommand.length);// just for test
		}
	 	
		String command = "";
		String name = "";
	    String description = "";    
	    String uri = "";
	    String channel = "";
	    String owner = "";
	    String ezserver = null;// assigned to null !!!according to instruction!!!
	    String secret = "";
	    String relay = "";
	    String serversAll = "";
	    //还没有debug一项，参考Enum那个类。
	
	    Options options = new Options();
	    options.addOption("command",true,"input command"); 
	    options.addOption("name",true,"input name");
	    options.addOption("tags",true,"input tags");
	    options.addOption("description",true, "input description");
	    options.addOption("uri",true, "input uri");
	    options.addOption("channel",true, "input channel");
	    options.addOption("owner",true, "input owner");
	    options.addOption("ezserver",true, "input ezserver");
	    options.addOption("secret",true, "input secret");
	    options.addOption("relay",true, "input relay");
	    options.addOption("servers",true, "input servers");//-debug command has not been handled.
	    
	    CommandLineParser parser = new DefaultParser();
	    CommandLine cmd = null;
	
	    try{
	        cmd = parser.parse(options,args);      
	    } 
	    catch (org.apache.commons.cli.ParseException e) {
			e.printStackTrace();
		}
	    
	    JSONObject userinputTemp = new JSONObject();
	    JSONObject resource = new JSONObject();

	    
	    if(cmd.hasOption("name")){
	       name = cmd.getOptionValue("name"); 
	   }
	    resource.put(ConstantEnum.CommandArgument.name.name(),name);
	   	    
	    if(cmd.hasOption("tags")){
	       String[] tags = cmd.getOptionValue("tags").split(",");
	       //Array<String> -> List -> JSONArray -> JSONObject
	       resource.put(ConstantEnum.CommandArgument.tags.name(), new JSONArray(Arrays.asList(tags)));
	   }
	    else {
//	    	String [] empty= new String[1];
//	    	empty[0] = "";
	    	//resource.put(ConstantEnum.CommandArgument.tags.name(), new JSONArray(Arrays.asList(empty)));
	    	JSONArray emptyJSONArray = new JSONArray();
	    	resource.put(ConstantEnum.CommandArgument.tags.name(), emptyJSONArray);
	    	//resource.put(ConstantEnum.CommandArgument.tags.name(), "");
	    }
	   	
	    if(cmd.hasOption("description")){
	       description = cmd.getOptionValue("description");
	   }
	    resource.put(ConstantEnum.CommandArgument.description.name(),description);	 
	    
	    if(cmd.hasOption("uri")){
	       uri = cmd.getOptionValue("uri");
	   }
	    resource.put(ConstantEnum.CommandArgument.uri.name(),uri);
	    
	    if(cmd.hasOption("channel")){
	       channel = cmd.getOptionValue("channel");
	   }
	    resource.put(ConstantEnum.CommandArgument.channel.name(),channel);	 
	       
	    if(cmd.hasOption("owner")){
	       owner = cmd.getOptionValue("owner");
	   }
	    resource.put(ConstantEnum.CommandArgument.owner.name(),owner);
	       
	    if(cmd.hasOption("ezserver")){
	       ezserver = cmd.getOptionValue("ezserver");
	   }
	    resource.put(ConstantEnum.CommandArgument.ezserver.name(),ezserver);
	    
	    if(cmd.hasOption("secret")){//it seems it's only used in SHARE
	       secret = cmd.getOptionValue("secret");
	   }  
	       
	    if(cmd.hasOption("relay")){
	       relay = cmd.getOptionValue("relay");
	   }   
	       
	    if(cmd.hasOption("servers")){
	       serversAll = cmd.getOptionValue("servers");
	       String[] serversArray = serversAll.split(",");
	       JSONArray serversJSONArray = new JSONArray();
	       for (int i=0; i<serversArray.length; i++){
	    	   		JSONObject temp = new JSONObject();
	    	   		String[] hostnameAndPort = serversArray[i].split(":");
	    	   		temp.put("hostname", hostnameAndPort[0]);
	    	   		temp.put("port", hostnameAndPort[1]);  
	    	   		serversJSONArray.put(temp);
	       }
	       userinputTemp.put(ConstantEnum.CommandArgument.serverList.name(),serversJSONArray);   
	   }
	   
	    
	    if(cmd.hasOption("command")){//switch?
	        command = cmd.getOptionValue("command");
	        switch (command){
	        case "-publish":	userinputTemp.put(ConstantEnum.CommandType.command.name(),"PUBLISH");
	        					//command is a String
	    						userinputTemp.put(ConstantEnum.CommandType.resource.name(),resource); 
	    						//resource is a JSONObject
	    						break;
	        case "-remove":	userinputTemp.put(ConstantEnum.CommandType.command.name(),"REMOVE");
							userinputTemp.put(ConstantEnum.CommandType.resource.name(),resource); 
							break;
	        case "-share":	userinputTemp.put(ConstantEnum.CommandType.command.name(),"SHARE"); 
							userinputTemp.put(ConstantEnum.CommandType.resource.name(),resource);
							userinputTemp.put(ConstantEnum.CommandArgument.secret.name(),secret);
							break;
	        case "-query":	userinputTemp.put(ConstantEnum.CommandType.command.name(),"QUERY");
	        				userinputTemp.put(ConstantEnum.CommandArgument.relay.name(),relay); 
							userinputTemp.put(ConstantEnum.CommandArgument.resourceTemplate.name(),resource); 
							//resource & rsourceTemplate are with different names but in same format, so 1 JSONObject 'resource' is used as their format
							break;
	        case "-fetch":	userinputTemp.put(ConstantEnum.CommandType.command.name(),"FETCH");
	        					userinputTemp.put(ConstantEnum.CommandArgument.resourceTemplate.name(),resource); 
	        					break;
	        case "-exchange":	userinputTemp.put(ConstantEnum.CommandType.command.name(),"EXCHANGE");	
	       						//serverArray已经在前面put过了，这里只普通put command就行了 
	       						break;
	        default: break;	
	        }
	    }
	    else {
	    		command ="";
	    		userinputTemp.put(ConstantEnum.CommandType.command.name(), "");//more to be handled.
	    }    
			return userinputTemp;
	}
	
	
	

	/**
	 * print out the response.
	 * @param input
	 */
	public static void handleServerResponse(String input, DataInputStream in){
		JSONParser parser = new JSONParser();
		JSONObject serverResponse;
		
		try {
			serverResponse = (JSONObject)parser.parse(input);
			//String response =  (String)serverResponse.get(ConstantEnum.CommandType.response.name());
			//use the command type recorded when reading user's input
			switch (commandType){//How to print out resource properly?
			case "-publish":
			case "-remove":
			case "-share":
			case "-exchange":
				System.out.println("response received from server: "+serverResponse.toJSONString());
				break;
			case "-query":
				System.out.println("response received from server: "+serverResponse.toJSONString());
				break;
			case "-fetch":
				System.out.println("response received from server: "+serverResponse.toJSONString());
				handleDownload(serverResponse,in);
				break;
			default: break;//How to deal with default?	
			}

		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * This method handles the downloading part of the Fetch response.
	 * @param serverResponse
	 * @param in
	 */
	public static void handleDownload(JSONObject serverResponse, DataInputStream in) {
			if(serverResponse.containsKey("resourceSize")){
				try{
					// The file location
					String fileName = "client_files/"+serverResponse.get("name");
					
					System.out.println("client_files/"+serverResponse.get("name"));
					// Create a RandomAccessFile to read and write the output file.
					RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");
					
					// Find out how much size is remaining to get from the server.
					long fileSizeRemaining = (Long) serverResponse.get("resourceSize");
					
					int chunkSize = setChunkSize(fileSizeRemaining);
					
					// Represents the receiving buffer
					byte[] receiveBuffer = new byte[chunkSize];
					
					// Variable used to read if there are remaining size left to read.
					int num;
					
	//				System.out.println("Downloading "+fileName+" of size "+fileSizeRemaining);
					while((num=in.read(receiveBuffer))>0){
						// Write the received bytes into the RandomAccessFile
						downloadingFile.write(Arrays.copyOf(receiveBuffer, num));
						
						// Reduce the file size left to read..
						fileSizeRemaining-=num;
						
						// Set the chunkSize again
						chunkSize = setChunkSize(fileSizeRemaining);
						receiveBuffer = new byte[chunkSize];
						
						// If you're done then break
						if(fileSizeRemaining==0){
							break;
					}
				}
	//			System.out.println("File received!");
				downloadingFile.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				
			}
			
		}
	}
	
	
	/**
	 * This method set the chunk size in each downloading. It's for handleDownload method.
	 * @param fileSizeRemaining
	 * @return the appropriate chunk size of next downloading.
	 */
	public static int setChunkSize(long fileSizeRemaining){
		// Determine the chunkSize
		int chunkSize=1024*1024;
		
		// If the file size remaining is less than the chunk size
		// then set the chunk size to be equal to the file size.
		if(fileSizeRemaining<chunkSize){
			chunkSize=(int) fileSizeRemaining;
		}
		return chunkSize;
	}
	
	
	
}

