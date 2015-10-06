import java.io.IOException;
import java.util.Iterator;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
 
/** 
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://localhost:8080/EchoChamber/echo
 * Where "localhost" is the address of the host,
 * "EchoChamber" is the name of the package
 * and "echo" is the address to access this class from the server
 */
@ServerEndpoint("/getcoord") 
public class getCoordinates {
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection"); 
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
    	
    	
    	
    	ItemCollection<QueryOutcome> items = parseCoordinates.getSample();
    	
    	Iterator<Item> iterator = items.iterator();
    	JSONObject jo = new JSONObject();
    	Item item = null;
    	String lat = null;
    	String lon = null; 
    	
    	while (iterator.hasNext()) {
    		
        	item = iterator.next();
        	lat = item.getJSON("lat").toString();
        	lon = item.getJSON("lon").toString();
        	jo = new JSONObject();
        	try {
				jo.put("lat", lat);
	        	jo.put("lon", lon);
	        	session.getBasicRemote().sendText(jo.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    	
    }
 
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
    }
}