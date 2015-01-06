package org.capgemini.am.LogAnalyzer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

public class GraphiteReporter {

	public static final String GraphiteHost = "10.91.80.132";
	public static final short GraphitePort = 2003;
	public static Socket socket;
	public static PrintWriter out;
	
	public static void openConnection() {
		try{
			socket = new Socket(GraphiteHost, GraphitePort);
			OutputStream s = socket.getOutputStream();
			out = new PrintWriter(s, true);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static void sendMetrics(String identifier ,Long requestTime ,Long responseTime) {
		try 
		{
			out.printf("%s %d %d%n", identifier, responseTime, requestTime);		
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
		try{
			out.close();
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
