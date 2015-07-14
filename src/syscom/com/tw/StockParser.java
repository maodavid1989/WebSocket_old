package syscom.com.tw;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class StockParser {

	public static Map Parsing(String number) throws Exception {
		URL url = new URL("https://tw.stock.yahoo.com/q/q?s="+number); 
		Document xmlDoc =  Jsoup.parse(url, 3000); //ㄏノJsoup jar 秆猂呼
		//(璶秆猂ゅン,timeout)
		Map map=new HashMap();
		Elements td = xmlDoc.select("td");  //璶秆猂tagじtd
		for (int i=0; i<td.size(); i++){
			map.put("field"+i, td.get(i).text());
			//System.out.println("fiedld"+i+": " + td.get(i).text());
		}
		return map;
	}
	
	
	public static JSONObject stockData(List stockNumber) throws Exception{
		JSONObject jsonObj= new JSONObject();
		
		for(int i=0; i<stockNumber.size();i++){
    		Map stockMap=StockParser.Parsing(stockNumber.get(i).toString());
    		JSONArray Jarray = new JSONArray();
//        	jsonObj.put("price_"+i, stockMap.get("field27").toString());//基窥
//        	jsonObj.put("upanddown_"+i, stockMap.get("field30").toString());//害禴
//        	jsonObj.put("count_"+i, stockMap.get("field31").toString());//Θユ眎计
//        	jsonObj.put("open_"+i, stockMap.get("field33").toString());//秨絃基
//        	jsonObj.put("top_"+i, stockMap.get("field34").toString());//程蔼基
//        	jsonObj.put("bottom_"+i, stockMap.get("field35").toString());//程基 	
        	Jarray.put(stockMap.get("field27").toString());//基窥
        	Jarray.put(stockMap.get("field30").toString());//害禴
        	Jarray.put(stockMap.get("field31").toString());//Θユ眎计
        	Jarray.put(stockMap.get("field33").toString());//秨絃基
        	Jarray.put(stockMap.get("field34").toString());//程蔼基
        	Jarray.put(stockMap.get("field35").toString());//程基 	
        	jsonObj.put("JsonArray"+i, Jarray);
    	}
		return jsonObj;
		
	}
}
