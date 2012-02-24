package xtremweb.serv.dt.jsaga.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Jdom Implementation of the XMLInterface
 * @author josefrancisco
 *
 */
public class XmlDomImpl implements XMLInterface {

    /**
     * The XML context elements
     */
    private List elements;
    
    /**
     * xml Root element
     */
    private Element root;

    /**
     * Builds an instance of XmlDomImpl
     * 
     * @param contexttype the context you want to load (VOMS)
     * @param filePath the path to jsaga-default-context.xml
     * @throws XMlException if an IOException or a JDOMException occurs
     */
    public XmlDomImpl(String contexttype, String filePath) throws XMLException {
	try {
	    SAXBuilder saxb = new SAXBuilder();
	    	    Document xml;
	    xml = saxb.build(new File(filePath));
	    root = xml.getRootElement();
	    XPath xpath = XPath.newInstance("/x:jsaga-default/x:session/x:context[@type='" + contexttype + "']");
	    xpath.addNamespace("x",root.getNamespaceURI());
	    elements = xpath.selectNodes(root);
	    if ((elements == null) || (elements.size() != 1))
		throw new XMLException("Context element not found");
	} catch (IOException e) {
	    throw new XMLException("There was a IOException " + e.getMessage());
	} catch (JDOMException e) {
	    throw new XMLException("There was a JDOMException " + e.getMessage());
	}
    }

    /**
     * Get an attribute value having a specific name
     * 
     * @param name the attribute name
     */
    public String getElement(String name) {
	String done = null;
	Element element = (Element) elements.get(0);
	List nodes = element.getChildren();
	// iterate over the attribute childs
	for (int i = 0; i < nodes.size(); i++) {
	    Element el = (Element)nodes.get(i);
	    if (el.getAttributeValue("name").equals(name)) {
		done = el.getAttributeValue("value");
		break;
	    }
	}
	return done;
    }
}
