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

public class StockParserUtil {
	
	//parser靡ユ┮计
	public static String getJsonTaiwanIndex() throws Exception {
		long t0 = System.currentTimeMillis();
		String url ="http://mis.twse.com.tw/stock/api/getChartOhlc.jsp?ex=tse&ch=t00.tw&fqy=5&delay=0&_="+t0; 
		String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
		return json;
	}
	
	//parser yahoo集布
	public static Document getHTMLDoc(String number) throws Exception {
		URL url = new URL("https://tw.stock.yahoo.com/q/q?s="+number); 
		Document xmlDoc =  Jsoup.parse(url, 3000);
		//(璶秆猂ゅン,timeout)
		return xmlDoc;
	}

	//parser yahoo 集布穝籇 禬硈挡
	public Map getAhref(Document xmlDoc){
		Map map=new HashMap();
		Elements links = xmlDoc.select("a[href]");
		for (int i=0; i<links.size(); i++){
//			System.out.println(i+": " + links.get(i).text());
//			System.out.println(i+": " + links.get(i).attr("abs:href"));
			map.put("field"+i, links.get(i).text());
			map.put("href"+i, links.get(i).attr("abs:href"));
		}		
		return map;
	}
	
	public static JSONObject stockData(List stockNumber) throws Exception{
		JSONObject jsonObj= new JSONObject();
		
		for(int i=0; i<stockNumber.size();i++){
    		Document xmlDoc=StockParserUtil.getHTMLDoc(stockNumber.get(i).toString());//眔htmldoc
    		
    		StockParserUtil sp= new StockParserUtil();
    		Map stockMap=sp.getTd(xmlDoc);
    		
    		JSONArray Jarray = new JSONArray();
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
	
	
	public Map getTd(Document xmlDoc){
		Map map=new HashMap();
		Elements td = xmlDoc.select("td");  //璶秆猂tagじtd
		for (int i=0; i<td.size(); i++){
			map.put("field"+i, td.get(i).text());
			//System.out.println("fiedld"+i+": " + td.get(i).text());
		}	
		return map;
	}
}
