package com.Cube.Console;
/**
 * @discript java在linux环境下执行cmd, DEMO
 * @author zhgq
 * @date 2015-04-02
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;  
import java.io.LineNumberReader;

public class JavaExeLinuxCmd {
	
	public JavaExeLinuxCmd()
	{
		
	}

 /**
  * 在有指定环境和工作目录的独立进程中执行指定的字符串命令,命令无参数
  * cmd：一条指定的系统命令
  */
	public static Object exec(String cmd, String[] envp, File dir) {
		try {
			Process child = Runtime.getRuntime().exec(cmd,envp,dir);
			InputStreamReader ir = new InputStreamReader(child.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);//创建IO管道，准备输出命令执行后的显示内容
			LineNumberReader stdError = new LineNumberReader(
					new InputStreamReader(child.getErrorStream()
							)
					);
			String lineStr = null;
			StringBuffer sb = new StringBuffer();
			while ((lineStr = input.readLine()) != null){
				System.out.println("INPUT: "+lineStr);  //按行打印输出内容
				sb.append(lineStr).append("\n");
			}
			
			while ((lineStr = stdError.readLine()) != null){
				System.out.println("ERROR: "+lineStr);
				sb.append(lineStr).append("\n");
			}
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  在指定环境和工作目录的独立进程中执行指定的命令和变量 
	 *  
	 */
	public static Object execut(String[] cmdarray, String[] envp, File dir) {
		try {
			
			Process child = Runtime.getRuntime().exec(cmdarray,envp,dir);
			child.waitFor(); 
			InputStreamReader ir = new InputStreamReader(child.getInputStream());
//			BufferedReader input = new BufferedReader(ir);//创建IO管道，准备输出命令执行后的显示内容
			LineNumberReader input = new LineNumberReader(ir);
			LineNumberReader stdError = new LineNumberReader(
					new InputStreamReader(child.getErrorStream()
							)
					);
			String lineStr = null;
			StringBuffer sb = new StringBuffer();
			
			while ((lineStr = input.readLine()) != null){
				sb.append(lineStr).append("\n");
			}
			
			while ((lineStr = stdError.readLine()) != null){
				sb.append(lineStr).append("\n");
			}
			
			return sb.toString();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行shell脚本 
	 */
	public static Object executShell(String shPath){
		try{
			Process process = null;
			String cmd = "chmod 777"+ shPath;
			process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
			
			String cmd2 = "/bin/sh "+ shPath;
			Process p = Runtime.getRuntime().exec(cmd2);
			p.waitFor();
			InputStreamReader ir = new InputStreamReader(p.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			LineNumberReader stdError = new LineNumberReader(
					new InputStreamReader(p.getErrorStream()
							)
					);
			String lineStr = null;
			StringBuffer sb = new StringBuffer();
			
			while ((lineStr = input.readLine()) != null){
				System.out.println("INPUT: "+lineStr);  //打印输出内容
				sb.append(lineStr).append("\n");
			}
			
			while ((lineStr = stdError.readLine()) != null){
				System.out.println("ERROR: "+lineStr);
				sb.append(lineStr).append("\n");
			}
			
			return sb.toString();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
