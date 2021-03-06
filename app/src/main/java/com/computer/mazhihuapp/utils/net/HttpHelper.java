package com.computer.mazhihuapp.utils.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpHelper {

	public static final String URL = "http://127.0.0.1:8090/";

	public static HttpResult get(String url) {
		HttpGet httpGet = new HttpGet(url);
		return execute(url, httpGet);
	}

	public static HttpResult post(String url, byte[] bytes) {
		HttpPost httpPost = new HttpPost(url);
		ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
		httpPost.setEntity(byteArrayEntity);
		return execute(url, httpPost);
	}


	public static HttpResult download(String url) {
		HttpGet httpGet = new HttpGet(url);
		return execute(url, httpGet);
	}

	private static HttpResult execute(String url, HttpRequestBase requestBase) {
		boolean isHttps = url.startsWith("https://");
		AbstractHttpClient httpClient = HttpClientFactory.create(isHttps);
		HttpContext httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		HttpRequestRetryHandler retryHandler = httpClient.getHttpRequestRetryHandler();//��ȡ���Ի���
		int retryCount = 0;
		boolean retry = true;
		while (retry) {
			try {
				// System.out.println("HH---------55");
				HttpResponse response = httpClient.execute(requestBase, httpContext);//��������
				if (response != null) {
					// System.out.println("HH---------57");
					return new HttpResult(response, httpClient, requestBase);
				}
				// System.out.println("HH---------61");
			} catch (Exception e) {
				// System.out.println("HH---------63");
				IOException ioException = new IOException(e.getMessage());
				retry = retryHandler.retryRequest(ioException, ++retryCount, httpContext);//�Ѵ����쳣�������Ի��ƣ����ж��Ƿ���Ҫ��ȡ����
			}
		}
		return null;
	}

	public static class HttpResult {
		private HttpResponse mResponse;
		private InputStream mIn;
		private String mStr;
		private HttpClient mHttpClient;
		private HttpRequestBase mRequestBase;
		public HttpResult(HttpResponse response, HttpClient httpClient, HttpRequestBase requestBase) {
			mResponse = response;
			mHttpClient = httpClient;
			mRequestBase = requestBase;
		}
		
	


		public int getCode() {
			StatusLine status = mResponse.getStatusLine();
			return status.getStatusCode();
		}

		public String getString() {
			if (!StringUtil.isBlank(mStr)) {
				return mStr;
			}
			InputStream inputStream = getInputStream();
			ByteArrayOutputStream out = null;
			if (inputStream != null) {
				try {
					out = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024 * 4];
					int len = -1;
					while ((len = inputStream.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
					byte[] data = out.toByteArray();
					mStr = new String(data, "utf-8");
				} catch (Exception e) {
				} finally {
					IOUtils.close(out);
					close();
				}
			}
			return mStr;
		}

		public InputStream getInputStream() {
			if (mIn == null && getCode() < 300) {
				HttpEntity entity = mResponse.getEntity();
				try {
					mIn = entity.getContent();
				} catch (Exception e) {
				}
			}
			return mIn;
		}

		public void close() {
			if (mRequestBase != null) {
				mRequestBase.abort();
			}
			IOUtils.close(mIn);
			if (mHttpClient != null) {
				mHttpClient.getConnectionManager().closeExpiredConnections();
			}
		}
	}
}
