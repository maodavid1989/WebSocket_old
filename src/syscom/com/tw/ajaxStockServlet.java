package syscom.com.tw;

import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import syscom.com.tw.base.AjaxBaseServlet;


@WebServlet("/ajaxStockServlet")
public class ajaxStockServlet extends AjaxBaseServlet{

	Logger logger = Logger.getLogger(ajaxStockServlet.class);

    @Override
    protected void executeAjax(HttpServletRequest request, HttpServletResponse response, HttpSession session,
        JSONObject argJsonObj, JSONObject returnJasonObj) throws Exception {
        String ajaxAction=request.getParameter("ajaxAction");
        switch(ajaxAction){
        	case "getStockAhref":
        		logger.info("----getStockAhref");
                String SN=request.getParameter("stockNumber");
                Document xmlDoc=StockParserUtil.getHTMLDoc(SN);//取得htmldoc
                StockParserUtil sp= new StockParserUtil();
        		Map stockAhref=sp.getAhref(xmlDoc);
                
                //JSONArray dataArray = new JSONArray();
                JSONObject ahrefObject=new JSONObject();
                ahrefObject.put("text1",stockAhref.get("field31"));
                ahrefObject.put("href1",stockAhref.get("href31"));
                ahrefObject.put("text2",stockAhref.get("field32"));
                ahrefObject.put("href2",stockAhref.get("href32"));
                ahrefObject.put("text3",stockAhref.get("field33"));
                ahrefObject.put("href3",stockAhref.get("href33"));
                //logger.info(ahrefObject);
                this.setFormData(returnJasonObj, ahrefObject);
                break;
        	case "getTaiwanIndex":
        		logger.info("----getTaiwanIndex");
//        		String TaiwanIndex=StockParserUtil.getJsonTaiwanIndex();//取得htmldoc
//        		logger.info(TaiwanIndex);
//        		
//        		JSONObject JObject=new JSONObject(TaiwanIndex);
//        		this.setFormData(returnJasonObj, JObject);
        		break;

        }
                   
    }
	
}
