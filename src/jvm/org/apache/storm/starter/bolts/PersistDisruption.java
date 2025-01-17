package org.apache.storm.starter.bolts;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.*;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Utils;
import org.apache.storm.starter.xml.*;
import org.apache.storm.starter.*;
import org.apache.storm.starter.data.*;
import org.apache.storm.starter.connection.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Bolt for passing java disruption object into Redis for persistence.
public class PersistDisruption extends BaseBasicBolt {
    RedisConnector RConnect;
    
    private static final Logger LOG = LoggerFactory.getLogger(PersistDisruption.class);
    
    @Override
    public void prepare(Map conf, TopologyContext context) {
        RConnect = new RedisConnector();
    }
        
    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        LOG.info("OUT: received, persisting");
        ArrivalDisruptionPair bean = (ArrivalDisruptionPair) tuple.getValue(0);
        RConnect.persistDisruption(bean);
        RConnect.invalidatePairCache();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("incident"));
    }
}