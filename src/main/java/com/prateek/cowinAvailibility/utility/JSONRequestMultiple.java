package com.prateek.cowinAvailibility.utility;

/**
 * @author prateek.mishra Custom class to send back response in Spring Rest JSON
 *         format only. I don't prefer Sending simple text as returns, hence
 *         using a wrapper
 */
public class JSONRequestMultiple {

	private String commaSeperatedChatId;
	private String message;



	public String getCommaSeperatedChatId() {
		return commaSeperatedChatId;
	}



	public void setCommaSeperatedChatId(String commaSeperatedChatId) {
		this.commaSeperatedChatId = commaSeperatedChatId;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public String getMessage() {
		return message;
	}
}