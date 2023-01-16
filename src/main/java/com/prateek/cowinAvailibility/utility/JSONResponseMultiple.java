package com.prateek.cowinAvailibility.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prateek.mishra Custom class to send back response in Spring Rest JSON
 *         format only. I don't prefer Sending simple text as returns, hence
 *         using a wrapper
 */
public class JSONResponseMultiple {

	private List<String> successList;
	private List<ErrorData> errorDataList;

	public JSONResponseMultiple(){
		successList = new ArrayList<>();
		errorDataList = new ArrayList<>();
	}

	public void addtoSuccessList(String chatId) {
		this.successList.add(chatId);
	}
	public void addtoErrorList(String chatId, String message) {
		this.errorDataList.add(new ErrorData(chatId, message));
	}
	public List<String> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<String> successList) {
		this.successList = successList;
	}
	public List<ErrorData> getErrorDataList() {
		return errorDataList;
	}
	public void setErrorDataList(List<ErrorData> errorDataList) {
		this.errorDataList = errorDataList;
	}



}