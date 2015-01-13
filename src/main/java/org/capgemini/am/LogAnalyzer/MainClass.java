package org.capgemini.am.LogAnalyzer;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class MainClass {
	
	public static ApplicationContext applicationContext;
	public static Properties applicationProperties;
	final static Logger mainLog = Logger.getLogger(MainClass.class);
	
	public static void main(String args[]){		
		
		// Get the application context.
		applicationContext = new FileSystemXmlApplicationContext("spring-config.xml");

	    // Get the application Properties.
		applicationProperties = applicationContext.getBean("appProperties", Properties.class);
		  
		AnalyseData objAnalyseData = new AnalyseData();
				
		int result = objAnalyseData.analyseData(""+applicationProperties.get("dss.file.location"));
				
		if(result == 0)
		{
			mainLog.info("Log file processed successfully.");
		}
		else 
		{
			mainLog.error("Error while processing logs.");
		}
	}
}
