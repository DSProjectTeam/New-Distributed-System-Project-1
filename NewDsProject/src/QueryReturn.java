import org.json.JSONArray;
import org.json.simple.JSONObject;

public class QueryReturn {
	Boolean hasMatch;
	org.json.simple.JSONArray returnArray;
	JSONObject reponseMessage;	
	
	public QueryReturn(org.json.simple.JSONArray jsonArray){
		this.returnArray = jsonArray;
		hasMatch = true;
	}
	
	public QueryReturn(JSONObject jsonObject){
		this.reponseMessage = jsonObject;
		hasMatch = false;
	}
	
	
}
