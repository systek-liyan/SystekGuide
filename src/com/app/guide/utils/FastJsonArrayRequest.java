package com.app.guide.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class FastJsonArrayRequest<T> extends Request<List<T>> {

	private final Listener<List<T>> mListener;
	private Class<T> mClass;
	
	public FastJsonArrayRequest(int method, String url, Class<T> clazz,
			Listener<List<T>> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mClass = clazz;
		mListener = listener;
	}

	public FastJsonArrayRequest(String url, Class<T> clazz, Listener<List<T>> listener,
			ErrorListener errorListener) {
		this(Method.GET, url, clazz, listener, errorListener);
	}

	@Override
	protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(JSON.parseArray(jsonString, mClass),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(List<T> response) {
		// TODO Auto-generated method stub
		mListener.onResponse(response);
	}

}
