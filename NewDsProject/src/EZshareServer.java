import java.awt.image.AreaAveragingScaleFilter;
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


public class EZshareServer {
	static ServerSocket server;
	
	/**key of this hash map is the URI of a resource, value is resource*/
	public HashMap<String, Resource> resources;
	public static String secert = "12345678";
	
	public ArrayList<String> serverList;

	private String secret;
	
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
	
	public static void main(String[] args){
		initializeServer(3780);
	}
	
	protected void finalize() throws Throwable{
		try {
				System.out.println("Server shutdown");
				server.close();
		}finally {
				super.finalize();
		}
	}
	
	public static void initializeServer(int serverPort){
		try {
			EZshareServer eZshareServer = new EZshareServer(serverPort);
			
			Timer timer = new Timer();
			long delay1 = 1000*60*10; //10mins
			long delay2 = 1000*60*10; //10mins
			
			ExchangeTask task = new ExchangeTask(eZshareServer);
			
			/**every 10 mins, contact a randomly selected server in the server list*/
			
			timer.schedule(task, delay1,delay2);
			
			
			while(true){
				Socket client = EZshareServer.server.accept();
				System.out.println("client applying for connection");
				new ServerThread(client, eZshareServer.resources, eZshareServer.secret, eZshareServer.server, eZshareServer.serverList).start();
				/*new Thread(new ServerThread(client, eZshareServer.resources,secert, eZshareServer.server,eZshareServer.serverList)).start();*/
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	
	
	
	

}
