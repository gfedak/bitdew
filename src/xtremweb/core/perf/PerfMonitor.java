package xtremweb.core.perf;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import org.jfree.data.time.*;
import java.util.*;

/**
 * PerfMonitor implements a Performance Monitor as serie of measures
 * (samples). It is based on the jfree time serie. Each series can
 * easly be displayed through a gui or produced for a report.
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class PerfMonitor {

    public static Logger log = LoggerFactory.getLogger(PerfMonitor.class);

    //perf monitor title
    private String _title;

    //dataset
    private TimeSeriesCollection _dataset;

    //hashmap of serie
    private HashMap<String,TimeSeries> _series;

    private int _nbseries = 0;

    //shortcut to serieName, whn the name is unique
    private String _serieName;

    /**
     * Creates a new <code>Monitor</code> instance with a title and a
     * fixed number of series, typically number of graphs.
     *
     * @param title a <code>String</code> value
     */
    public PerfMonitor(String title) {
	_dataset = new TimeSeriesCollection();
	_series = new  HashMap<String,TimeSeries>();
	_title = title;
    }

    /**
     * Creates a new <code>PerfMonitor</code> instance containing only
     * one serie with a title, the title of the serie and the maximum
     * number of samples .
     *
     * @param title a <code>String</code> value
     * @param serieTitle a <code>String</code> value
     * @param maxSamples an <code>int</code> value
     */
    public PerfMonitor(String title, String serieTitle, int maxSamples)  {
	this(title);
	try {	    
	    addSerie(serieTitle, maxSamples);
	    _serieName = serieTitle;
	} catch (Exception e) {
	    //should not exist
	}
    }


    /**
     *  <code>addSerie</code> adds a new serie with a title and a
     *  maximum number of samples
     *
     * @param serieTitle a <code>String</code> value
     * @param maxSamples an <code>int</code> value
     * @return an <code>int</code> value
     */
    public void addSerie(Comparable serieTitle, int maxSamples) throws PerfException {
	TimeSeries ts = new TimeSeries(serieTitle,Millisecond.class);
	ts.setMaximumItemCount(maxSamples);
	_dataset.addSeries(ts);
	_series.put((String)serieTitle,ts);
	_nbseries++;
    }

    /**
     * <code>getTitle</code> return the title of this performance
     * monitor. 
     *
     * @return a <code>String</code> value
     */
    public String getTitle() {
	return _title;
    }

    /**
     * <code>addSample</code> add a new sample to the performance
     * monitor. Here we assume a unique serie.
     *
     * @param value a <code>long</code> value
     */
    public void addSample(long value) {
	addSample(_serieName,value);
    }

    /**
     *  <code>addSample</code> adds a new samples to the specified
     *  serie. 
     *
     * @param serie an <code>String</code> value
     * @param value a <code>long</code> value
     */
    public void addSample(String serie, long value) {
	try {
	    TimeSeries ts = _series.get(serie);
	    ts.add(new Millisecond(), value);
	    //	    log.debug(" [PERF] " + _title + " " + value );
	} catch (Exception e) {
	    log.debug(e.toString());
	    //FIXME delete the latest values
	}
    }

    /**
     * Get the samples
     * @return
     */
    public TimeSeriesCollection getSamples() {
	return _dataset;
    }
    
    /**
     * Get the series
     * @return
     */
    public Set<String> getSeries() {
	return _series.keySet();
    }
}
