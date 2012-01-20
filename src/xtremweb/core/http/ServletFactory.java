package xtremweb.core.http;

import javax.servlet.http.HttpServlet;

/**
 * Servlet Factory
 * @author josefrancisco
 *
 */
public class ServletFactory {
    
    /**
     * Builds an HttpServlet of a specific class at runtime
     * @param clazz
     * @return a httpservlet
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static HttpServlet getInstance(String clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
	return (HttpServlet)Class.forName(clazz).newInstance();
    }

}
