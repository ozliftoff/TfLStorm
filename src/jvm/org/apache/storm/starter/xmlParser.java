package org.apache.storm.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.ArrayList;
import java.net.*;
import javax.xml.transform.stream.*;
import javax.xml.*;
import javax.xml.bind.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.storm.starter.xml.*;

public class xmlParser {
 
        private static final Logger LOG = LoggerFactory.getLogger(xmlParser.class);
    
    public xmlParser(){
        
    }
    
    public String parseXml(){
        try{
            JAXBContext jc = JAXBContext.newInstance("org.apache.storm.starter.xml");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            URL url = new URL("https://data.tfl.gov.uk/tfl/syndication/feeds/tims_feed.xml?app_id=ef3b4027&app_key=5c0b4a956599179156d4979df6bcb346");
            InputStream xml = url.openStream();
            JAXBElement<Root> feed = unmarshaller.unmarshal(new StreamSource(xml), Root.class);
            xml.close();
            Root xmlRoot = feed.getValue();
            ArrayList<Object> headerOrDisrupt = (ArrayList<Object>) xmlRoot.getHeaderOrDisruptions();
            Root.Disruptions dis = (Root.Disruptions) headerOrDisrupt.get(1);
            ArrayList <Root.Disruptions.Disruption> disruptionList = (ArrayList<Root.Disruptions.Disruption>) dis.getDisruption();
            LOG.info("XML output: "+xmlRoot);
            LOG.info("XML list length: "+headerOrDisrupt.size());
            LOG.info("XML Disruption list length: "+ disruptionList.size());
            return "done";
        }catch(Exception e){
            LOG.error("XML: "+e.toString());
        }
        return "its fucked";
    }
    
}