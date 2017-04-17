import java.util.ArrayList;

import org.json.simple.JSONObject;

//other server return
public class QueryData {
	boolean hasMatch;
	ArrayList<JSONObject> outcome;
	
	public QueryData(){
		
	}
	
	public QueryData(Boolean hasMatch, ArrayList<JSONObject> outcome){
		this.hasMatch = hasMatch;
		this.outcome = outcome;
	}
	
	
}
