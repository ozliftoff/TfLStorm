package org.apache.storm.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.*;

public class ArrivalCommunicator1 {
 
    private ArrayList<ArrivalBean> beanList = new ArrayList<ArrivalBean>();
    private static final Logger LOG = LoggerFactory.getLogger(ArrivalCommunicator1.class);
    private static final String arrivalUrl = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?ReturnList=StopPointName,StopCode2,Towards,Latitude,Longitude,VehicleID,RegistrationNumber,LineID,DestinationName,EstimatedTime,ExpireTime";

    private static ArrivalCommunicator1 instance = null;
    protected ArrivalCommunicator1() {
      // Exists only to defeat instantiation.
       }
    
    public static ArrivalCommunicator1 getInstance() {
      if(instance == null) {
         instance = new ArrivalCommunicator1();
         instance.beginArrivalRequestLoop();
      }
      return instance;
    }
    
    public void beginArrivalRequestLoop(){
        
        Timer t = new Timer( );
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                  java.util.Date date = new java.util.Date();
                  makeAndFormArrivalRequest();
            }
         }, 1000,60000);
    }
    
    private void makeAndFormArrivalRequest(){
        try{
            LOG.info("Making new data request");
            String rawResult = makeArrivalRequest(arrivalUrl);
            beanList.clear();
            beanList.addAll(formatArrivalResult(rawResult));
            LOG.info("beanList now: "+beanList.size());
        }catch(Exception e){
            LOG.error("Failed to make data request:");
            LOG.error(e.toString());   
        }
       
    }
    
    private String makeArrivalRequest(String urlToRead) throws Exception {
        
          StringBuilder result = new StringBuilder();
          URL url = new URL(urlToRead);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("GET");
          BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line;
          while ((line = rd.readLine()) != null) {
             result.append(line);
          }
          rd.close();
          return result.toString(); 
    }
    
    private ArrayList<ArrivalBean> formatArrivalResult(String rawArrivalResult){
        String[] rows = rawArrivalResult.split("\\[");
        LOG.info("result "+rows.length+" chars "+rawArrivalResult.length());
        ArrayList<ArrivalBean> beansToReturn = new ArrayList<ArrivalBean>();
        for(int i =2; i<rows.length; i++){
            String row = "{data:["+rows[i]+"}";
            JSONObject jsonObj = new JSONObject(row);
            JSONArray jsonarray = jsonObj.getJSONArray("data");
            
            if(jsonarray.length()!=12){
                LOG.error("Number of elements in arrival data. Expected 12, got "+jsonarray.length());
                LOG.error("Offending Array: "+jsonarray.toString());
                continue;
            }else{
                try{
                    ArrivalBean bean = new ArrivalBean();
                    bean.setStopPointName(jsonarray.getString(1));
                    bean.setStopCode2(jsonarray.getString(2));
                    bean.setTowards(jsonarray.getString(3));
                    bean.setLatitude(jsonarray.getDouble(4));
                    bean.setLongitude(jsonarray.getDouble(5));
                    bean.setLineID(jsonarray.getString(6));
                    bean.setDestinationName(jsonarray.getString(7));
                    bean.setVehicleID(jsonarray.getInt(8));
                    bean.setRegistrationNumber(jsonarray.getString(9));
                    bean.setEstimatedTime(jsonarray.getInt(10));
                    bean.setExpireTime(jsonarray.getInt(11));
                    bean.setHasExpired(false);
                    beansToReturn.add(bean);
                }catch(Exception e){
                    //LOG.error("A JSON row item was malformed and not parsed: "+e.toString());
                   // LOG.error("Offending Array: "+jsonarray.toString());
                }
            }
        }
        LOG.info("added "+beansToReturn.size()+" items to the array, out of "+rows.length+" total");
        return beansToReturn;
    }
    
    public ArrayList<ArrivalBean> getArrivalUpdates(){
        
        if(beanList.size() > 0){
            ArrayList<ArrivalBean> beansToReturn = new ArrayList<ArrivalBean>();
            beansToReturn.addAll(beanList);
            beanList.clear();
            LOG.info("sending "+beansToReturn.size()+" to topology");
            return beansToReturn;
        }else{
            //Just send empty list.
            LOG.info("sending empty list to topology.");
            return beanList;
        }
    }
}











