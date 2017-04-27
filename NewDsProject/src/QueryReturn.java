import java.io.BufferedWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.simple.JSONObject;


//local return
public class QueryReturn {
	Boolean hasMatch;
	/*org.json.simple.JSONArray returnArray;*/
	ArrayList<JSONObject> returnList = new ArrayList<>();
	JSONObject reponseMessage;	
	
	public QueryReturn(ArrayList<JSONObject> returnList){
		this.returnList = returnList;
		hasMatch = true;
	}
	
	public QueryReturn(JSONObject jsonObject){
		this.reponseMessage = jsonObject;
		hasMatch = false;
	}
	
	
}
