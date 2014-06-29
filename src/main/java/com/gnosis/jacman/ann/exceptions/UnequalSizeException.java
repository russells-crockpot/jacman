/**
 * 
 */
package com.gnosis.jacman.ann.exceptions;



/**
 * @author root
 *
 */
@SuppressWarnings("serial")
public class UnequalSizeException extends Exception {

	/**
	 * 
	 */
	public UnequalSizeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UnequalSizeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UnequalSizeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnequalSizeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
