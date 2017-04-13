import java.util.ArrayList;

import org.json.simple.JSONObject;

public class QueryData {
	JSONObject errorResponse;
	ArrayList<JSONObject> successOutcome;
	
	public QueryData(JSONObject errorResponse, ArrayList<JSONObject> successOutcome){
		this.errorResponse = errorResponse;
		this.successOutcome = successOutcome;
	}
}
