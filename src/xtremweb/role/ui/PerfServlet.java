package xtremweb.role.ui;

import xtremweb.core.perf.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Describe class PerfServlet here.
 *
 *
 * Created: Thu Jul 19 15:10:33 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class PerfServlet extends HttpServlet {
    int refresh = 3000;
    /**
     * Creates a new <code>PerfServlet</code> instance.
     *
     */
    public PerfServlet() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//	OutputStream out = response.getOutputStream();
	PrintWriter out = new PrintWriter(response.getWriter());
	try {
	    response.setContentType("text/html");
	    out.println("<HTML><HEAD><TITLE>BitDew Performance Monitoring</TITLE></HEAD>");
	    out.println("<H2>Performance Counters</H2>");
	    out.println("<TABLE><TD>");
	    for (String name : PerfMonitorFactory.browse()) 
		out.println("<TR><a href=\"" + request.getRequestURI() + "?graph=" + name + "\">" + name + "</a></TR>");

	    out.println("</TD></TABLE></HTML>");
	    out.println();
	    String graph  = request.getParameter("graph"); 
	    if (graph != null) {
		out.println("<IMG src=\"ts.png?graph="+ graph +"\" width=\"400\" height=\"300\" border=\"1\" name=\"refresh\">");
		out.println("<SCRIPT language=\"JavaScript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("var t = " + refresh);
		out.println("image = \"ts.png?graph="+ graph +"\"");
		out.println("function Start() {");
		out.println("tmp = new Date();");
		out.println("tmp = \"?\"+tmp.getTime()");
		out.println("document.images[\"refresh\"].src = image+tmp");
		out.println("setTimeout(\"Start()\", t)");
		out.println("}");
		out.println(" Start();");
		out.println("// -->");

		out.println(" </SCRIPT>");
	    }
	    out.flush();
	    out.close();
	    
	} catch (Exception e) {
	    System.out.println(e.toString());
	} finally {
	    out.close();
	}
    }
}
