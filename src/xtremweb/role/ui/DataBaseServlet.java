package xtremweb.role.ui;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import xtremweb.core.obj.ds.Attribute;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.log.*;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.DaoJDOImpl;
import xtremweb.serv.dc.DataUtil;
import xtremweb.serv.ds.AttributeUtil;

import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * Describe class DataBaseServlet here.
 *
 *
 * Created: Fri Aug 24 10:54:32 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DataBaseServlet  extends HttpServlet  {

    protected Logger log = LoggerFactory.getLogger("Data base ui");

    /**
     * Creates a new <code>DataBaseServlet</code> instance.
     *
     */
    public DataBaseServlet(){
	
    }
    
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//	OutputStream out = response.getOutputStream();
	PrintWriter out = new PrintWriter(response.getWriter());
	try {
	    response.setContentType("text/html");
	    out.println("<HTML><HEAD><TITLE>BitDew Database Monitor</TITLE></HEAD>");
	    out.println(browse(Data.class));
	    out.println(browse(Attribute.class));
	    log.debug("end");
	    out.flush();
	    out.close();
	    
	} catch (Exception e) {
	    log.debug(e.toString());
	} finally {
	    out.close();
	}
    }
    
    public String browse(Class className) {
	String retour = "<H3>"+ className + "</H3>";
	log.debug(retour);
	retour += "<table>";
	DaoJDOImpl dao = (DaoJDOImpl)DaoFactory.getInstance("xtremweb.dao.DaoJDOImpl");
	
	try {
	    dao.beginTransaction();
          
            Iterator iter= dao.getAll(className).iterator();
            while (iter.hasNext())
            {
		retour += "<tr>";
		Object o = iter.next();
		if (o instanceof Data) {
		    Data data = (Data) o;
		    retour += "<td>" + data.getuid() + "</td><td>"+ DataUtil.toString(data) + "</td>";
		    log.debug( DataUtil.toString(data) );
		}

		if (o instanceof Attribute) {
		    Attribute attr = (Attribute) o;
		    retour += "<td>" + attr.getuid() + "</td><td>"+ AttributeUtil.toString(attr) + "</td>";
		    log.debug(AttributeUtil.toString(attr));
		}     
		retour += "</tr>";
            }
	    dao.commitTransaction();
        } finally {
            if (dao.transactionIsActive())
                dao.transactionRollback();
            dao.close();
	}	
	retour += "</table>";
	return retour;
    }

}
