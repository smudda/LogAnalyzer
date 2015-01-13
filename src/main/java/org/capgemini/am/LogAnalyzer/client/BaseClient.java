package org.capgemini.am.LogAnalyzer.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class BaseClient {
	final static Logger baseClientLog = Logger.getLogger(BaseClient.class);
	protected String host;
	protected int port;

	public BaseClient(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * create socket connection
	 *  
	 */
	protected Socket createSocket() throws IOException {
		return new Socket(host, port);
	}
	/**
	 * Send a set of metrics with the current time as timestamp to graphite.
	 * 
	 * @param metrics
	 *            the metrics as key-value-pairs
	 */
	public abstract void sendMetrics(Map<String, Integer> metrics);

	/**
	 * Send a set of metrics with a given timestamp to graphite.
	 * 
	 * @param metrics
	 *            the metrics as key-value-pairs
	 * @param timeStamp
	 *            the timestamp
	 */
	public abstract void sendMetrics(Map<String, Integer> metrics, long timeStamp);

	/**
	 * Send a single metric with the current time as timestamp to graphite.
	 * 
	 * @param key
	 *            The metric key
	 * @param value
	 *            the metric value
	 * @throws GraphiteException
	 *             if writing to graphite fails
	 */
	public abstract void sendMetric(String key, int value);

	/**
	 * Send a single metric with a given timestamp to graphite.
	 * 
	 * @param key
	 *            The metric key
	 * @param value
	 *            The metric value
	 * @param timeStamp
	 *            the timestamp to use in seconds
	 * @throws GraphiteException
	 *             if writing to graphite fails
	 */
	public abstract void sendMetric(final String key, final int value, long timeStamp);

	/**
	 * Compute the current graphite timestamp.
	 * 
	 * @return Seconds passed since 1.1.1970
	 */
	public long getCurrentTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	@Override
	public String toString() {
		return host+":"+port;
	}
}
