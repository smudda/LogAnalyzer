/**
 * NOTE: The classes has been taken out from https://github.com/zanox/simpleinfluxDBclient
 * Has been copied because the maven distribution does not exist anymore.
 */

package org.capgemini.am.LogAnalyzer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Simple client for writing one shot metrics to influxDB.
 * No socket reuse or other optimisations are implemented.
 *
 * @author Helmut Zechmann
 */
public class SimpleInfluxDBClient {
  private String influxDBHost;
  private int influxDBPort;
  HttpClient client;
  
  //http://10.75.196.228/db/graphite/series?u=root&p=root
  /**
   * Create a new influxDB client.
   *
   * @param influxDBHost Host name to write to.
   * @param influxDBPort influxDB socket. Default is 2003
   */
  public SimpleInfluxDBClient(String influxDBHost, Integer influxDBPort) {
    this.influxDBHost = influxDBHost;
    this.influxDBPort = influxDBPort;
    client = new HttpClient();
  }

  /**
   * Send a single metric with the current time as timestamp to influxDB.
   *
   * @param key   The metric key
   * @param value the metric value
   * @throws influxDBException if writing to influxDB fails
   */
  public void sendMetric(String key, int value) {
    sendMetric(key, value, getCurrentTimestamp());
  }

  /**
   * Send a single metric with a given timestamp to influxDB.
   *
   * @param key       The metric key
   * @param value     The metric value
   * @param timeStamp the timestamp to use in seconds
   * @throws influxDBException if writing to influxDB fails
   */
  public void sendMetric(final String key, final int value, long timeStamp) {
	  try{
		  	PostMethod method = new PostMethod("http://"+influxDBHost+":"+influxDBPort+"/db/graphite/series");
			method.setQueryString("u=root&p=root");		    
		    method.setRequestBody("[{\"name\":\""+key+"\",\"columns\":[\"time\",\"value\"],\"points\":[["+value+","+timeStamp+"]]}]");
		    client.executeMethod(method);
		    if(method.getStatusCode() != 200){
		    	throw new Exception("Error response code from InfluxDB");
		    }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  	
  }
  /**
   * Compute the current influxDB timestamp.
   *
   * @return Seconds passed since 1.1.1970
   */
  public long getCurrentTimestamp() {
    return System.currentTimeMillis();
  }
}
