package syscom.com.tw.base;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public abstract class BaseServlet extends HttpServlet {
    protected static Logger logger = Logger.getLogger(BaseServlet.class);
    
    public BaseServlet() {
    	super();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    execute(request, response);
		} catch (Exception e) {
		    logger.error("BaseServlet doGet error!");
		}
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    execute(request, response);
		} catch (Exception e) {
			logger.error("BaseServlet doPost error!");
		}
    }

    protected abstract void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}