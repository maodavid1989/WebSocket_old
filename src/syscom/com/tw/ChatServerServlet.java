package syscom.com.tw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

@ServerEndpoint(value = "/ws")
public class ChatServerServlet{
 
    private static final String GUEST_PREFIX = "¨Ï¥ÎªÌ";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatServerServlet> connections = new CopyOnWriteArraySet<>();
    private final String nickname;
    private Session session;
    
    
    public ChatServerServlet() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }
 
    @OnOpen
    public void start(Session session) {
    	this.session = session;
        connections.add(this);
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("type", "login");
        jsonObj.put("name", nickname);
        jsonObj.put("text", "has joined.");
        broadcastJSON(jsonObj.toString());
    }
 
    @OnClose
    public void end() {
        connections.remove(this);
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("type", "logout");
        jsonObj.put("name", nickname);
        jsonObj.put("text", "has disconnected.");
        broadcastJSON(jsonObj.toString());
    }
    
    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println("Chat Error: " + t.toString());
    }
    
    @OnMessage
    public void onMessage(String message) throws Exception {
    	//System.out.println("parser start");
    	//add stock number
    	List stockNumber= new ArrayList();
    	stockNumber.add("8086");
    	stockNumber.add("3105");
    	stockNumber.add("2455");
    	
    	JSONObject jsonObj= StockParser.stockData(stockNumber);
    	jsonObj.put("type", "OnStock"); 
    	jsonObj.put("nowTime", Dateutil.getYYYY_MM_DD());  //time  		
    	broadcastJSON(jsonObj.toString());
    }
 
    //broadcast to front
    private static void broadcastJSON(String msg) {
    	System.out.println("msg :"+msg);
        for (ChatServerServlet client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                System.out.println("Chat Error: Failed to send message to client");
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcastJSON(message);
            }
        }
    } 
}
