/**
 * NOTE: The classes has been taken out from https://github.com/zanox/simplegraphiteclient
 * Has been copied because the maven distribution does not exist anymore.
 */

package org.capgemini.am.LogAnalyzer.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple client for writing one shot metrics to graphite.
 * No socket reuse or other optimisations are implemented.
 *
 * @author Helmut Zechmann
 */
public class SimpleGraphiteClient extends BaseClient {
  
  /**
   * Create a new Graphite client.
   *
   * @param graphiteHost Host name to write to.
   * @param graphitePort Graphite socket. Default is 2003
   */
  public SimpleGraphiteClient(String graphiteHost, Integer graphitePort) {
    super(graphiteHost,graphitePort);
  }

  /**
   * Send a set of metrics with the current time as timestamp to graphite.
   *
   * @param metrics the metrics as key-value-pairs
   */
  public void sendMetrics(Map<String, Integer> metrics) {
    sendMetrics(metrics, getCurrentTimestamp());
  }

  /**
   * Send a set of metrics with a given timestamp to graphite.
   *
   * @param metrics   the metrics as key-value-pairs
   * @param timeStamp the timestamp
   */
  public void sendMetrics(Map<String, Integer> metrics, long timeStamp) {
    try {
      baseClientLog.info("Sending data to Graphite");
      Socket socket = createSocket();
      OutputStream s = socket.getOutputStream();
      PrintWriter out = new PrintWriter(s, true);
      System.out.println(metrics+" :: "+timeStamp);
      for (Map.Entry<String, Integer> metric : metrics.entrySet()) {
        out.printf("%s %d %d%n", metric.getKey(), metric.getValue(), timeStamp);
      }
      out.close();
      socket.close();
      baseClientLog.info("Successfully data sent to Graphite");
    } catch (UnknownHostException e) {
    	baseClientLog.error("Error while sending data :"+e);
    } catch (IOException e) {
    	baseClientLog.error("Error while sending data :"+e);
    }
  }

  /**
   * Send a single metric with the current time as timestamp to graphite.
   *
   * @param key   The metric key
   * @param value the metric value
   * @throws GraphiteException if writing to graphite fails
   */
  public void sendMetric(String key, int value) {
    sendMetric(key, value, getCurrentTimestamp());
  }

  /**
   * Send a single metric with a given timestamp to graphite.
   *
   * @param key       The metric key
   * @param value     The metric value
   * @param timeStamp the timestamp to use in seconds
   * @throws GraphiteException if writing to graphite fails
   */
  @SuppressWarnings("serial")
  public void sendMetric(final String key, final int value, long timeStamp) {
    sendMetrics(new HashMap<String, Integer>() {{
      put(key, value);
    }}, timeStamp);
  }

  /**
   * Compute the current graphite timestamp.
   *
   * @return Seconds passed since 1.1.1970
   */
  public long getCurrentTimestamp() {
    return System.currentTimeMillis() / 1000;
  }
}
