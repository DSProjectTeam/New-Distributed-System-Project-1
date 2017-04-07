
import java.lang.reflect.Array;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class ClientHandler { // change name to ClientHandler.

    /**
     * @param args the command line arguments
     */

    
    public static void main(String[] args) {// change name to method ClientHandler
    		
    		if (args[0].equals("-publish")||args[0].equals("-remove")||args[0].equals("-share")||
    				args[0].equals("-query")||args[0].equals("-fetch")||args[0].equals("-exchange")){
    			
        		String[] argsWithCommand = new String[args.length+1];
        		argsWithCommand[0] = "-command";
        		System.arraycopy(args, 0, argsWithCommand, 1, args.length);
        		
        		args = new String [args.length+1];
        		System.arraycopy(argsWithCommand, 0, args, 0, argsWithCommand.length);
        		System.out.println(args.length+""+argsWithCommand.length);// just for test
    		}
    		/*else {
    			String[] argsWithCommand = new String[args.length];
    			System.arraycopy(args, 0, argsWithCommand, 0, args.length);
    		}*/
    	 	
        
    		String command = "";
    		String name = "";
        String tags = "";
        String description = "";    
        String uri = "";
        String channel = "";
        String owner = "";
        String ezserver = "";
        String secret = "";
        String relay = "";
        String servers = "";//Caution!怎么用
        //还没有debug一项，参考Enum那个类。

        Options options = new Options();
        options.addOption("command",true,"input command"); 
        options.addOption("name",true,"input name");
        options.addOption("tags",true,"input tags");
        options.addOption("description",true, "input description");
        options.addOption("uri",true, "input ");
        options.addOption("channel",true, "input ");
        options.addOption("owner",true, "input ");
        options.addOption("ezserver",true, "input ");
        options.addOption("secret",true, "input ");
        options.addOption("relay",true, "input ");
        options.addOption("servers",true, "input ");
        
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try{
            cmd = parser.parse(options,args);      
        } catch (ParseException e){
            //help(options);
        }
        
        

        if(cmd.hasOption("command")){
            command = cmd.getOptionValue("command");
        } else {
            command = "";
        }
        
        if(cmd.hasOption("name")){
           name = cmd.getOptionValue("name");
       } else {
           name = "";
       }
       
        if(cmd.hasOption("tags")){
           tags = cmd.getOptionValue("tags");
       } else {
           tags = "";
       }
       
        if(cmd.hasOption("description")){
           description = cmd.getOptionValue("description");
       } else {
           description = "";
       }
       
        if(cmd.hasOption("uri")){
           uri = cmd.getOptionValue("uri");
       } else {
           uri = "The user does not provide email address";
       }
       
        if(cmd.hasOption("channel")){
           channel = cmd.getOptionValue("channel");
       } else {
           channel = "";
       }
       
        if(cmd.hasOption("owner")){
           owner = cmd.getOptionValue("owner");
       } else {
           owner = "";
       }
       
        if(cmd.hasOption("ezserver")){
           ezserver = cmd.getOptionValue("ezserver");
       } else {
           ezserver = "";
       }
       
        if(cmd.hasOption("secret")){
           secret = cmd.getOptionValue("secret");
       } else {
           secret = "";
       }
       
        if(cmd.hasOption("relay")){
           relay = cmd.getOptionValue("relay");
       } else {
           relay = "";
       }
       
        if(cmd.hasOption("servers")){
           servers = cmd.getOptionValue("servers");
       } else {
           servers = "";
       }
       
        

        System.out.println("command: "+command+" name: "+name);//just for test
        
    }
    
}
