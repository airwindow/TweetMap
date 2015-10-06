//import org.json.JSONException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.util.json.JSONArray;
 

public class parseCoordinates {
	static public ItemCollection<QueryOutcome> getSample() {
    	System.out.println("Enter here!!!!");
//    	DocumentAPIQuery dataset = new DocumentAPIQuery();
    	ItemCollection<QueryOutcome> items = DocumentAPIQuery.QueryDocument();
    	return items;
    	
    	/*
        Iterator<Item> iterator = items.iterator();
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> list = new ArrayList<String> ();
        String item = null;
        while (iterator.hasNext()) {
        	item = iterator.next().toJSON();
        	//System.out.println(s);
        	list.add(item);
        }
        
        JSONArray json_array = new JSONArray(list);
        System.out.println(json_array.toString());
        
        JSONObject jo = new JSONObject();
        
        
		try {
			jo.put("lat", "-25.363882");
			jo.put("lon", "131.044922");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return json_array.toString();
		*/
        //return jo.toString();
	}
}
