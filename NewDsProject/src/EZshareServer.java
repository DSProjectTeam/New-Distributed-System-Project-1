import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class EZshareServer {
	static ServerSocket server;
	public static ArrayList<Resource> resources;
	
	public EZshareServer(){};
	
	
	
	protected void finalize() throws Throwable{
		try {
				System.out.println("Server shutdown");
				server.close();
		}finally {
				super.finalize();
		}
	}
	
	public void initializeServer(int serverPort){
		try {
			this.server  = new ServerSocket(serverPort);
			this.resources = new ArrayList<Resource>();
			
			while(true){
				Socket client = server.accept();
				new Thread(new ServerThread(client, resources)).start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	
	

}
