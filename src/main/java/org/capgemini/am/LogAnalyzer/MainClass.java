package org.capgemini.am.LogAnalyzer;

public class MainClass {
	
	public static void main(String args[]){		
		if(args.length == 0)
		{
			System.out.println("No file name mentioned for processing . Using default : dss.log");
			args = new String[]{"dss.log"};
		}
		
		AnalyseData objAnalyseData = new AnalyseData();
		
		int result = objAnalyseData.analyseData(args[0]);
				
		if(result == 0)
		{
			System.out.println("Log file processed successfully.");
		}
		else 
		{
			System.out.println("Error while processing logs.");
		}
	}
}
