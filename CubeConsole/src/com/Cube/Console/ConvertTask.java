package com.Cube.Console;

import java.util.ArrayList;
import java.util.List;

public class ConvertTask {

	StateCode state; 
	String filePath = null;
	String filePrefix = null;
	String fileExtension = null;
	String targetFilePath = null;
	String taskTag = null;
	String tag = null;
	List<String> convertedFileList = null;
	
	private final String UNOCONV_PDF = "unoconv -f pdf";
	private final String PDFTOPPM_PNG = "pdftoppm -png";
	private final String FILE_EXTENSION = "png";
	private String result1;
	private String result2;
	
	public ConvertTask(String filePath, String targetPath, String tag, String taskTag){
			this.filePath = filePath;
			this.filePrefix = ConvertUtils.extractFileNameWithoutExtension(filePath);
			this.fileExtension = FILE_EXTENSION;
			this.targetFilePath = targetPath;
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
	
	public void setTargetFilePath(String targetPath) {
		this.targetFilePath = targetPath;
	}

	public String getTargetFilePath() {
		return this.targetFilePath;
	}
	public void setTaskTag(String taskTag){
		this.taskTag = taskTag;
	}
	
	public String getTaskTag(){
		return this.taskTag;
	}
	
	public void setConvertedFileList(List<String> list) {
		this.convertedFileList = list;
	}

	public List<String> getConvertedFileList() {
		return this.convertedFileList;
	}
	
	public void convert(){
		//unoconv -f pdf /home/lztxhost/Documents/dddd.doc
		String cmd1 = UNOCONV_PDF+" "+this.filePath;
		result1 = JavaExeLinuxCmd.execut(new String[]{"/bin/sh","-c",
				cmd1
		},null,null).toString();
		
		int index = this.filePath.lastIndexOf(".");
		String pdfFilePath = this.filePath.substring(0, index+1)+"pdf";
		
		if(null == this.filePrefix){
			this.filePrefix = "Cube";
		}
		
		if(null == this.fileExtension){
			this.fileExtension = "png";
		}
	
		//pdftoppm -png file.pdf  file_prefix
		String cmd2 = PDFTOPPM_PNG+" "+ pdfFilePath + " " + this.filePrefix;
		result2 = JavaExeLinuxCmd.execut(new String[]{"/bin/sh","-c",
				cmd2
		},null,null).toString();
		
		//TODO
		this.state = StateCode.Successed;
		
	}
	
}
