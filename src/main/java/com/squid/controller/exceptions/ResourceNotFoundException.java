package com.squid.controller.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/*
 * 404 Not Found Exception
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 4860469083021358207L;
	private String msg;
	   
	   public ResourceNotFoundException(String exceptionMsg) {
	      this.msg = exceptionMsg;
	   }
	   
	   public String getExceptionMsg(){
	      return this.msg;
	   }
	   
	   public void setExceptionMsg(String exceptionMsg) {
	      this.msg = exceptionMsg;
	   }

}
