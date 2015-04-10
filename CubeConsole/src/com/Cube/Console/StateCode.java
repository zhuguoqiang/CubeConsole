package com.Cube.Console;

public enum StateCode {

	Queueing(100),			//!< 排队中

	Executing(200),			//!< 执行中
	
	Failed(300),			//!< 转换失败

	Successed(400),		    //!< 转换成功

	Unknown(0);				//!< 未知错误

	private int code;
	private String description;

	StateCode(int code) {
		this.code = code;
	}
	
	public StateCode StateCode(int result) {
	
		return StateCode(result);
	}

	public int getCode() {
		return this.code;
	}
	
	public  String getDescription(){
		String str = null;
		if (code == StateCode.Queueing.getCode()){
			str = "Queueing";
		}else if (code == StateCode.Executing.getCode()){
			str = "Executing";
		}else if (code == StateCode.Failed.getCode()){
			str = "Failed";
		}else if (code == StateCode.	Successed.getCode()){
			str = "Successed";
		}else if (code == StateCode.Unknown.getCode()){
			str = "Unknown";
		}else{
			
		}
		return str;
	}
}
