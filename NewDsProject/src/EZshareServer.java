import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import org.json.simple.*;
import java.util.Random;
import java.io.DataOutputStream;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;

public class EZshareServer {
	static ServerSocket server;
	
	/**key of this hash map is the URI of a resource, value is resource*/
	public HashMap<String, Resource> resources;
	/*public String secret = "12345678";*/
	
	public ArrayList<String> serverList;
	public static String commandType;
	public static boolean hasDebugOption = false;
	
	public static String command = "";
	public static String hostName = "";
	public static int connectioninterval;
	public static int port = 3780;
	public static String secret = "";
	public static int exchangeInterval = 0;
	
	public EZshareServer(){};
	
	public EZshareServer(int serverPort) {
		try {
			this.server  = new ServerSocket(serverPort);
			this.resources = new HashMap<String, Resource>();
			this.serverList = new ArrayList<String>();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	public static void handleServerInput(String[] args){
		/*if (args[0].equals("-advertisedhostname")||args[0].equals("-connectionintervallimit")||
				args[0].equals("-exchangeinterval")||args[0].equals("-port")||args[0].equals("-secret")
				||args[0].equals("-debug")) {
			commandType = args[0];
			String[] argsWithCommand = new String[args.length+1];
    		argsWithCommand[0] = "-command";
    		System.arraycopy(args, 0, argsWithCommand, 1, args.length);
    		
    		args = new String [args.length+1];
    		System.arraycopy(argsWithCommand, 0, args, 0, argsWithCommand.length);
		}*/
		
		for(String str: args){
			if(str.equals("-debug")){
				hasDebugOption=true;
	    		String[] argsWithDebug = new String[args.length+1];
	    		argsWithDebug[argsWithDebug.length-1] = "";
	    		System.arraycopy(args, 0, argsWithDebug, 0, args.length);
	    		
	    		args = new String [args.length+1];
	    		System.arraycopy(argsWithDebug, 0, args, 0, argsWithDebug.length);
	    		break;
			}
		}
		
		
		
		Options options = new Options();
		options.addOption("exchangeinterval",true,"input exchange interval"); 
		options.addOption("advertisedhostname",true, "input host");
	    options.addOption("port",true, "input port");
	    options.addOption("connectioninterval",true, "input interval");
	    options.addOption("secret",true, "input secret");
	    options.addOption("debug",true,"debug option");
	    
	    CommandLineParser parser = new DefaultParser();
	    CommandLine cmd = null;
	    try{
	        cmd = parser.parse(options,args);      
	    } 
	    catch (org.apache.commons.cli.ParseException e) {
			e.printStackTrace();
		}
	    
	    
	    if(cmd.hasOption("exchangeinterval")){
	    	exchangeInterval = Integer.parseInt(cmd.getOptionValue("exchangeinterval"));
	    }else{
	    	exchangeInterval = 1000*60*10; 
	    }
	    
	    if (cmd.hasOption("secret")) {
			secret = cmd.getOptionValue("secret");
		}else{
			//random string as  default secret
			Random random = new Random();
			secret = RandomStringUtils.randomAlphabetic(20);
			System.out.println("random secret as default: "+secret);
		}
	    
	    if(cmd.hasOption("advertisedhostname")){
	    	hostName = cmd.getOptionValue("hostName");
	    }else{
	    	hostName = "localhost";
	    }
	    
	    if(cmd.hasOption("port")){
	    	port =Integer.parseInt(cmd.getOptionValue("port"));
	    }
	    
	    if(cmd.hasOption("connectioninterval")){
	    	connectioninterval = Integer.parseInt(cmd.getOptionValue("connectioninterval"));
	    }else{
	    	//default 1000ms 
	    	connectioninterval = 1000;
	    }
	    
	    
	}
	
	public static void main(String[] args){
		System.out.println("Welcome to the EZShare");
		handleServerInput(args);
		initializeServer();
		
		/*initializeServer(3780);*/
	}
	
	protected void finalize() throws Throwable{
		try {
				System.out.println("Server shutdown");
				server.close();
		}finally {
				super.finalize();
		}
	}
	
	public static void initializeServer(){
		try {
			EZshareServer eZshareServer = new EZshareServer(port);
			
			eZshareServer.server.setSoTimeout(connectioninterval);
			
			Timer timer = new Timer();
			/*long delay1 = 1000*60*10; //10mins
			long delay2 = 1000*60*10; //10mins
*/			

			ExchangeTask task = new ExchangeTask(eZshareServer);
			
			/**every 10 mins, contact a randomly selected server in the server list*/
			
			timer.schedule(task, exchangeInterval,exchangeInterval);
			
			
			while(true){
				Socket client = EZshareServer.server.accept();
				System.out.println("client applying for connection");
				new ServerThread(client, eZshareServer.resources, eZshareServer.secret, eZshareServer.server, eZshareServer.serverList, eZshareServer.hasDebugOption).start();
				/*new Thread(new ServerThread(client, eZshareServer.resources,secert, eZshareServer.server,eZshareServer.serverList)).start();*/
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	
	
	
	

}
