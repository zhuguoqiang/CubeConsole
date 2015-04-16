package com.Cube.Console;

public class ConvertTask {

	StateCode state; 
	String filePath = null;
	String filePrefix = null;
	String fileExtension = null;
	String taskTag = null;
	String tag = null;
	
	private final String UNOCONV_PDF = "unoconv -f pdf";
	private final String PDFTOPPM_PNG = "pdftoppm -png";
	private String result1;
	private String result2;
	
	public ConvertTask(String tag, String taskTag){
		this.tag = tag;
		this.taskTag = taskTag;
	}
	
	public ConvertTask(String filePath, String tag, String taskTag){
		this.filePath = filePath;
		this.taskTag = taskTag;
		this.tag = tag;
	}
	
	public ConvertTask(String filePath, String filePrefix, String fileExtension, String tag, String taskTag){
			this.filePath = filePath;
			this.filePrefix = filePrefix;
			this.fileExtension = fileExtension;
			this.tag = tag;
			this.taskTag = taskTag;
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
	
	public void setFilePath(String fileP) {
		this.filePath = fileP;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePrefix(String filePre) {
		this.filePrefix = filePre;
	}

	public String getFilePrefix() {
		return this.filePrefix;
	}

	public void setFileExtension(String fileExt) {
		this.fileExtension = fileExt;
	}

	public String getFileExtension() {
		return this.fileExtension;
	}
	
	public void setTaskTag(String taskTag){
		this.taskTag = taskTag;
	}
	
	public String getTaskTag(){
		return this.taskTag;
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
		
		if(null == this.fileExtension){
			this.fileExtension = "png";
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
