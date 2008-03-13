package xtremweb.role.ui;

/**
 * Describe class GraphServlet here.
 *
 *
 * Created: Fri Jul 20 13:12:27 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.perf.*;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.category.*;
import org.jfree.data.time.*;

public class GraphServlet  extends HttpServlet {

    /**
     * Creates a new <code>GraphServlet</code> instance.
     *
     */
    public GraphServlet() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	OutputStream out = response.getOutputStream();
	String graph = request.getParameter("graph"); 

	//dunno why it gives a reply like this : TransferManager?1197318474877
	graph = graph.substring(0,graph.indexOf("?"));

	PerfMonitor perf = PerfMonitorFactory.getPerfMonitor(graph);
	TimeSeriesCollection dataset = (perf).getSamples();
        try {
            JFreeChart chart = ChartFactory.createTimeSeriesChart(perf.getTitle(), "Date", "Hits/sec", dataset, true, false, false);

            response.setContentType("image/png");
            ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
        } catch (Exception e) {
            System.out.println("grrr " + e); 
        } finally {
            out.close();
        }

    }

}
