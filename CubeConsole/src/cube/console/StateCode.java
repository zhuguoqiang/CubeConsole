package cube.console;

public enum StateCode {

	Queueing(100),			//!< 排队中
	
	Started(200),           //!< 开始转换

	Executing(300),			//!< 执行中
	
	Failed(400),			//!< 转换失败

	Successed(500),		    //!< 转换成功

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
		}else if (code == StateCode.Started.getCode()){
			str = "Executing";
		}else if (code == StateCode.Executing.getCode()){
			str = "Started";
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
