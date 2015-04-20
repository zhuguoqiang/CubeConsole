package com.Cube.Console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConvertTask {

	StateCode state; 
	String filePath = null;
	String filePrefix = null;
	String fileExtension = null;
	String subPath = null;
	String taskTag = null;
	String tag = null;
	List<String> convertedFileList = null;
	
	private static final String rootPath = "/home/wwwroot/default/cubewhiteboard/shared/";
	private static final String URL = "http:211.103.217.154/cubewhiteboard/shared/";
	private final String UNOCONV_PDF = "unoconv -f pdf";
	private final String PDFTOPPM_PNG = "pdftoppm -png";
	private final String FILE_EXTENSION = "png";
	
	public ConvertTask(String filePath, String subPath, String tag, String taskTag){
			this.filePath = filePath;
			this.filePrefix = ConvertUtils.extractFileNameWithoutExtension(filePath);
			this.fileExtension = FILE_EXTENSION;
			this.subPath = subPath;
			this.tag = tag;
			this.taskTag = taskTag;
	}
	
	public static String getRootPath() {
		return rootPath;
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
	
	public void setSubPath(String subPath) {
		this.subPath = subPath;
	}

	public String getSubPath() {
		return this.subPath;
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
		String unovonvCmd = UNOCONV_PDF+" "+this.filePath;
		JavaExeLinuxCmd.execut(new String[]{"/bin/sh","-c",
				unovonvCmd
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
		String pdftoppmCmd = PDFTOPPM_PNG+" "+ pdfFilePath + " " + this.filePrefix;
		JavaExeLinuxCmd.execut(new String[]{"/bin/sh","-c",
				pdftoppmCmd
		},null,null).toString();
		
		this.state = StateCode.Successed;
	}
	
	public List<String> moveFileToWorkspace() {
		//1 查找文件 
		// find -name "*.png" | grep -H "MCE"
		String findCmd = "find -name '*." + this.getFileExtension()
				+ "' | grep -H " + "'" + this.getFilePrefix() + "'";
//		System.out.println(this.getClass() + " : " + cmd);
		String fileNames = JavaExeLinuxCmd.execut(
				new String[] { "/bin/sh", "-c", findCmd }, null,
				null).toString();
		
		//2 提取转换后 文件名 数组
		List<String> fileNameArray = new ArrayList<String>();
		if (null != fileNames) {
			String[] names = fileNames.split("\n");
			for (String fileName : names) {
				int index = fileName.lastIndexOf("/");
				String subStr = fileName.substring(index + 1, fileName.length());
				fileNameArray.add(subStr);
			}
		}
		
		//3 移动转换后的文件到工作目录  
		
		int endIndex = filePath.lastIndexOf("/");
		String uploadDirPath = filePath.substring(0, endIndex + 1);
		// uploadDirPath：  /home/lztxhost/Documents/upload
		String workDirPath = rootPath + subPath;
		// workDirPath： /home/wwwroot/default/cubewhiteboard/shared/ + subPath
		File tmpFile = new File(workDirPath);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		
		List<String> convertedFileURLArray = new ArrayList();
		for (String name: fileNameArray){
			String pngFilePath = uploadDirPath + name;
			convertedFileURLArray.add( URL + subPath + name);
			
			// mv -t /opt/soft/test/test4/ log1.txt log2.txt log3.txt
			String moveCmd = "mv -t " + workDirPath + " " + pngFilePath;
//			System.out.println(this.getClass() + " MOVE_PNG_FILE : " + moveCmd);
			String r = JavaExeLinuxCmd.execut(
					new String[] { "/bin/sh", "-c", moveCmd}, null,
					null).toString();
		}
		// 返回URLs
		return convertedFileURLArray;
	}
}
