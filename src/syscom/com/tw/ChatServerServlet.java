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

import org.apache.log4j.Logger;
import org.json.JSONObject;

@ServerEndpoint(value = "/ws")
public class ChatServerServlet{
 
    private static final String GUEST_PREFIX = "使用者";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatServerServlet> connections = new CopyOnWriteArraySet<>();
    private final String nickname;
    private Session session;
    
    Logger logger = Logger.getLogger(ChatServerServlet.class);
    
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
    public void onMessage(String stock) throws Exception {
    	//logger.info("*** WebSocket Received from sessionId " + this.session.getId() ); 
    	JSONObject jsonObj;
    	switch(stock){
    	case "getTaiwanIndex":
    		logger.info("*** getTaiwanIndex socket *** ");  
    		String TaiwanIndex=StockParserUtil.getJsonTaiwanIndex();//取得htmldoc
    		logger.info("臺指: "+TaiwanIndex);
    	    jsonObj=new JSONObject(TaiwanIndex);
    	    jsonObj.put("type", "OnIndex"); 
    		break;
    	default:
    		logger.info("*** getStock socket *** "); 
    		List stockNumber= new ArrayList();
        	String[] sa=stock.split(",");
        	for(int i=0; i<sa.length ; i++){
        		stockNumber.add(sa[i]);
        	}
            jsonObj= StockParserUtil.stockData(stockNumber);
            logger.info("股票: "+jsonObj);
        	jsonObj.put("type", "OnStock"); 
        	jsonObj.put("nowTime", DateUtil.getYYYY_MM_DD());  //time 
    		break;
    	}
    	sendText(jsonObj);
    	
    }
 
    private void sendText(JSONObject jsonObj) throws IOException{
        try {
        	this.session.getBasicRemote().sendText(jsonObj.toString());
        } catch (IOException e) {
            System.out.println("Chat Error: Failed to send message to client");
            connections.remove(this.session);
            this.session.close();
        }    	
    }
    
    //broadcast to front
    private static void broadcastJSON(String msg) {
    	System.out.println("msg :"+msg);
        for (ChatServerServlet client : connections) {
            try {
                client.session.getBasicRemote().sendText(msg);
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
