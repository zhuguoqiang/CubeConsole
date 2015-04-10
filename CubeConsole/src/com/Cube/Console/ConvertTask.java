package com.Cube.Console;

public class ConvertTask {

	StateCode state; 
	String filePath;
	String filePrefix;
	String tag;
	
	private final String UNOCONV_PDF = "unoconv -f pdf";
	private final String PDFTOPPM_PNG = "pdftoppm -png";
	private String result1;
	private String result2;
	
	public ConvertTask(String tag){
		this.tag = tag;
	}
	
	public ConvertTask(String filePath, String tag){
		this.filePath = filePath;
		this.tag = tag;
	}
	
	public ConvertTask(String filePath, String filePrefix, String tag){
			this.filePath = filePath;
			this.filePrefix = filePrefix;
			this.tag = tag;
	}
	
	public void setStateCode(StateCode state){
		this.state = state;
	}
	
	public StateCode getStateCode(){
		return this.state;
	}
	
	public void setTag(String tag){
		this.tag = tag;
	}
	
	public String getTag(){
		return this.tag;
	}	
	
	public void convert(){
		//unoconv -f pdf /home/lztxhost/Documents/dddd.doc
		String cmd1 = UNOCONV_PDF+" "+this.filePath;
		result1 = JavaExeLinuxCmd.execut(new String[]{"/bin/sh","-c",
				cmd1
		},null,null).toString();
		
		int index = this.filePath.lastIndexOf(".");
		String pdfFile = this.filePath.substring(0, index+1)+"pdf";
		
		if(null == this.filePrefix){
			this.filePrefix = "Cube";
		}
	
		//pdftoppm -png file.pdf  file_prefix
		String cmd2 = PDFTOPPM_PNG+" "+ pdfFile + " " + this.filePrefix;
		result2 = JavaExeLinuxCmd.execut(new String[]{"/bin/sh","-c",
				cmd2
		},null,null).toString();
		
		//TODO
		this.state = StateCode.Successed;
		
	}
	
}
