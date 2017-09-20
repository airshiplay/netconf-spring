package com.airlenet.netconf.netty;

import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * @author airlenet
 * @version 2017-09-20
 */
public class NetconfMessage {
    private static final Transformer TRANSFORMER;
    private final Document doc;
    static {
        Transformer transformer =null;
        try {
              transformer =TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent","yes");
            transformer.setOutputProperty("omit-xml-declaration","yes");
        } catch (TransformerConfigurationException e) {
            throw  new RuntimeException(e);
        }
        TRANSFORMER =transformer;
    }

    public NetconfMessage(Document document){
        this.doc =document;
    }
    public Document getDocument(){
        return this.doc;
    }

    public String toString(){
        StreamResult result= new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(this.doc.getDocumentElement());
        synchronized (TRANSFORMER){
            try {
                TRANSFORMER.transform(source,result);
            } catch (TransformerException e) {
                throw new IllegalStateException("Failed to encode document",e);
            }
        }
        return result.getWriter().toString();
    }
}
