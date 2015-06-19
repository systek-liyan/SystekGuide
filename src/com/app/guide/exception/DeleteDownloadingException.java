package com.app.guide.exception;

/**
 * 自定义异常，当试图删除正在下载的数据包时抛出该异常
 * 
 * @author joe_c
 *
 */
public class DeleteDownloadingException extends Exception {

	private static final long serialVersionUID = 1967734624477054975L;

	public DeleteDownloadingException() {
		// TODO Auto-generated constructor stub
		super("try to delete the downloading data");
	}

}
