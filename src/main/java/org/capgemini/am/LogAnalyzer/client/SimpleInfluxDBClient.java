/**
 * NOTE: The classes has been taken out from https://github.com/zanox/simpleinfluxDBclient
 * Has been copied because the maven distribution does not exist anymore.
 */

package org.capgemini.am.LogAnalyzer.client;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Simple client for writing one shot metrics to influxDB. No socket reuse or
 * other optimisations are implemented.
 * 
 * @author 
 */
public class SimpleInfluxDBClient extends BaseClient {

	HttpClient client;

	/**
	 * Create a new influxDB client.
	 * 
	 * @param influxDBHost
	 *            Host name to write to.
	 * @param influxDBPort
	 *            influxDB socket. Default is 2003
	 */
	public SimpleInfluxDBClient(String influxDBHost, Integer influxDBPort) {
		super(influxDBHost, influxDBPort);
		client = new HttpClient();
	}

	/**
	 * Send a single metric with the current time as timestamp to influxDB.
	 * 
	 * @param key
	 *            The metric key
	 * @param value
	 *            the metric value
	 * @throws influxDBException
	 *             if writing to influxDB fails
	 */
	public void sendMetric(String key, int value) {
		sendMetric(key, value, getCurrentTimestamp());
	}

	/**
	 * Send a single metric with a given timestamp to influxDB.
	 * 
	 * @param key
	 *            The metric key
	 * @param value
	 *            The metric value
	 * @param timeStamp
	 *            the timestamp to use in seconds
	 * @throws influxDBException
	 *             if writing to influxDB fails
	 */
	public void sendMetric(final String key, final int value, long timeStamp) {
		try {
			baseClientLog.info("Sending data to InfluxDB");
			PostMethod method = new PostMethod("http://" + host + ":" + port
					+ "/db/graphite/series");
			method.setQueryString("u=root&p=root");
			method.setRequestBody("[{\"name\":\"" + key
					+ "\",\"columns\":[\"value\",\"time\"],\"points\":[["
					+ value + "," + timeStamp + "]]}]");
			client.executeMethod(method);
			baseClientLog.info("Successfully data sent to InfluxDB");
			if (method.getStatusCode() != 200) {
				throw new Exception("Error response code from InfluxDB : "+method.getStatusCode());
			}
		} catch (Exception e) {
			baseClientLog.error("Error while sending data :"+e);
		}

	}

	@Override
	public void sendMetrics(Map<String, Integer> metrics) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMetrics(Map<String, Integer> metrics, long timeStamp) {
		// TODO Auto-generated method stub

	}
}
