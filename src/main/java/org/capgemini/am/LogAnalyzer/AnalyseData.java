package org.capgemini.am.LogAnalyzer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;


public class AnalyseData{

    final static Logger ADLog = Logger.getLogger(AnalyseData.class);
    SimpleGraphiteClient objSimpleGraphiteClient;
    SimpleInfluxDBClient objSimpleInfluxDBClient;
    //Constants
    final static byte RequestTime = 0;
    final static byte ResponseTime = 1;
            
	//third party call / (correlation ids / response time)
    Map<String, Map<String, Long[]>> thirdPartyResponse = new LinkedHashMap<String, Map<String, Long[]>>();	
	
	public AnalyseData() {
		objSimpleGraphiteClient = new SimpleGraphiteClient("10.91.80.132", 2003);
		objSimpleInfluxDBClient = new SimpleInfluxDBClient("10.75.196.228", 8086);
	}
	
	public void extractData(String fileName){
		
		String sCurrentLine;		
		try {
			
			//read the file
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			while ((sCurrentLine = br.readLine()) != null) {	
				
				if(sCurrentLine.contains("Requesting")) {
					
				}
				
			}
		}catch(Exception e){
			
		}
	}	
	
	public int analyseData(String fileName){
		
		String sCurrentLine;		
		try {
			
			//read the file
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			while ((sCurrentLine = br.readLine()) != null) {				
				
				if(sCurrentLine.contains("Requesting")) {
					
					sCurrentLine = sCurrentLine.replaceFirst(",", ":");
					
					//time stamp of request received
					Date timeStampRequestReceived = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.ENGLISH).parse(sCurrentLine.substring(0,sCurrentLine.indexOf(",")));
					
					//get the third party call name
					String thirdParty = sCurrentLine.substring(sCurrentLine.indexOf("INFO") + 5 , sCurrentLine.indexOf("-",sCurrentLine.indexOf("INFO") + 5) -1);
					
					//get the correlation id
					String CorrelationID = sCurrentLine.substring( sCurrentLine.indexOf( "CorrelationID:" )+14);
					
					Map<String, Long[]> CorrelationIDsandReqResTime = null ; 
					
					if(thirdPartyResponse.containsKey(thirdParty)) {
						
						CorrelationIDsandReqResTime = thirdPartyResponse.get(thirdParty);						
						
						if(CorrelationIDsandReqResTime.containsKey(CorrelationID) == false){
							Long[] times = new Long[2];
							//save received time against correlation id
							times[RequestTime] = timeStampRequestReceived.getTime();
							CorrelationIDsandReqResTime.put(CorrelationID, times);
						}
						else //if correlation id already exist reject it as duplicate. 
						{
							ADLog.info("Duplicate Request : thirdParty - "+thirdParty+" / CorrelationID - "+CorrelationID+"\n");					
						}
						
					}
					else //create entry for new third party call 
					{
						CorrelationIDsandReqResTime = new LinkedHashMap<String, Long[]>();
						
						Long[] times = new Long[2];						
						times[RequestTime] = timeStampRequestReceived.getTime();
						
						CorrelationIDsandReqResTime.put(CorrelationID, times);
						
						thirdPartyResponse.put(thirdParty, CorrelationIDsandReqResTime);
					}
				}
				else if(sCurrentLine.contains("Response received")) {
					
					sCurrentLine = sCurrentLine.replaceFirst(",", ":");
					
					Date timeStampResponseReceived = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.ENGLISH).parse(sCurrentLine.substring(0,sCurrentLine.indexOf(",")));
					
					//get the third party call name
					String thirdParty = sCurrentLine.substring(sCurrentLine.indexOf("INFO")+5,sCurrentLine.indexOf("-",sCurrentLine.indexOf("INFO")+5)-1);
					
					//get the correlation id
					String CorrelationID = sCurrentLine.substring(sCurrentLine.indexOf("CorrelationID:")+14);
					
					if(thirdPartyResponse.containsKey(thirdParty))
					{
						//get the correlationIDs and corresponding request time in from the Map
						Map<String, Long[]> CorrelationIDsandReqResTime = thirdPartyResponse.get(thirdParty);
						
						if(CorrelationIDsandReqResTime.containsKey(CorrelationID)) {
							
							Long[] times = CorrelationIDsandReqResTime.get(CorrelationID);							
							Long requestedTime = times[RequestTime];
							Long responseTime = timeStampResponseReceived.getTime();
							Long timeTaken = responseTime - requestedTime;
							//save response time
							times[ResponseTime] = timeTaken;
						}
						else // reject the response as no request time is present
						{
							ADLog.info("Unable to Calculate Time Taken : thirdParty - "+thirdParty+" / CorrelationID - "+CorrelationID+"\n");						
						}
					}
					else // reject the response as no request data is present
					{
						ADLog.info("Unable to Calculate Time Taken : thirdParty - "+thirdParty+" / CorrelationID - "+CorrelationID+"\n");
					}
				}
			}
			
			br.close();
					
			Set<Entry<String, Map<String, Long[]>>> thirdPartiesSet = thirdPartyResponse.entrySet();
			
			Iterator<Entry<String, Map<String, Long[]>>> thirdPartiesIterator = thirdPartiesSet.iterator();
			
			
			//iterate through the third party calls
			while(thirdPartiesIterator.hasNext()){				
				
				Entry<String, Map<String, Long[]>> CorrelationIDsandReqResTimeEntry = thirdPartiesIterator.next();
				Map<String, Long[]> CorrelationIDsandReqResTime = CorrelationIDsandReqResTimeEntry.getValue();
								
				Set<Entry<String, Long[]>> corrlationIDsEntry = CorrelationIDsandReqResTime.entrySet();
				Iterator<Entry<String, Long[]>> corrlationIDsIterator = corrlationIDsEntry.iterator();			
				ADLog.info("Third Party : "+CorrelationIDsandReqResTimeEntry.getKey());
				//iterate through correlation ids
				long previousrequestTimeinMinutes = 0L;
				long TotalResponseTime = 0L;
				byte countOfRequests = 0;
				while(corrlationIDsIterator.hasNext()){			
					Entry<String, Long[]> corrlationID = corrlationIDsIterator.next();
					Long times[] = corrlationID.getValue();
					
					if(times[1] != null)
					{
						long requestTimeinMinutes = (times[RequestTime]/1000)/60;
						ADLog.info("RequestTime : "+new Date(requestTimeinMinutes*60*1000)+" ResponseTime : "+times[ResponseTime]);
											
						if(previousrequestTimeinMinutes == 0){
							previousrequestTimeinMinutes = requestTimeinMinutes;
							TotalResponseTime = times[ResponseTime];
							countOfRequests = 1;
						}else if(requestTimeinMinutes == previousrequestTimeinMinutes){
							TotalResponseTime += times[ResponseTime];
							countOfRequests ++;
						}else{							
							//sending response time data to Graphite
							objSimpleInfluxDBClient.sendMetric("am.logAnalyzer.responsetime."+CorrelationIDsandReqResTimeEntry.getKey().trim(), new Integer(""+(TotalResponseTime / countOfRequests)), previousrequestTimeinMinutes*60*1000);
							ADLog.info("Sent : RequestTime : "+previousrequestTimeinMinutes*60*1000+" Average ResponseTime : "+(TotalResponseTime / countOfRequests)+" Total request received : "+countOfRequests);
							
							TotalResponseTime = times[ResponseTime];
							previousrequestTimeinMinutes = requestTimeinMinutes;
							countOfRequests = 1;
						}
						if(corrlationIDsIterator.hasNext() == false){
							objSimpleInfluxDBClient.sendMetric("am.logAnalyzer.responsetime."+CorrelationIDsandReqResTimeEntry.getKey().trim(), new Integer(""+(TotalResponseTime / countOfRequests)), previousrequestTimeinMinutes*60*1000);
							ADLog.info("Sent : RequestTime : "+previousrequestTimeinMinutes*60*1000+" Average ResponseTime : "+(TotalResponseTime / countOfRequests)+" Total request received : "+countOfRequests);
						}
					}
					else // incoming request without response 
					{						
						ADLog.info("NO response/Error response CorrelationID - "+corrlationID.getKey()+"\n");
					}					
				}			
			}
			thirdPartyResponse = null;
			
			//delete the file
			new File(fileName).delete();
			
			return 0;
		} catch (Exception e) {	
			e.printStackTrace();
			return 1;
		}
	}
}
