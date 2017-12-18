package org.apache.storm.starter.util;

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
import org.apache.storm.starter.timetable.xml.*;

public class TimetableXmlParser {
 
    private static final Logger LOG = LoggerFactory.getLogger(TimetableXmlParser.class);
    JAXBContext jc;
    Unmarshaller unmarshaller;
    
    public TimetableXmlParser(){
         try{
             jc = JAXBContext.newInstance("org.apache.storm.starter.timetable.xml");
             unmarshaller = jc.createUnmarshaller();
         }catch(Exception e){
             LOG.error("TIMETABLEXML: "+e);
        }
    }
    
    public TransXChange parseXml(String name){
       
        TransXChange output = null;
        String path = "/opt/apache-storm-1.1.1/examples/storm-starter/src/jvm/org/apache/storm/starter/timetabledata/";
        LOG.info("XML attempting: "+name);
        try{

            jc = JAXBContext.newInstance("org.apache.storm.starter.timetable.xml");
            unmarshaller = jc.createUnmarshaller();
            TransXChange item = (TransXChange) unmarshaller.unmarshal(new File(path+name));
            item = output;

        }catch(Exception e){
            LOG.error("XML: "+e.toString());
        }

        return output;
    }
    
}