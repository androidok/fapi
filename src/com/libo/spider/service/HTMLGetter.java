package com.libo.spider.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.logging.FileHandler;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.security.MD5Encoder;

import sun.security.provider.MD5;

import com.libo.model.AllInfoModel;
import com.libo.spider.model.HTMLContentModel;
import com.libo.tools.StringTool;
import com.libo.tools.XLog;

/**
 * 此类主要获取html内容
 * 
 * @author libo
 * 
 */

public class HTMLGetter {

	public static final String defaultEncoding = "UTF-8";
	public static final String tempFileDir = "/Users/libo/Desktop/log/";

	static {
		File dir = new File(tempFileDir);
		if (!dir.exists()) {
			boolean res = dir.mkdir();
			XLog.logger.info("临时目录不存在，创建临时目录 result:" + res);
		}else {
			XLog.logger.info("临时目录HTML: " + dir.toString());
		}
	}

	public static HTMLContentModel getHTMLContentFromInfo(AllInfoModel info) {

		XLog.logger.info("\n----------Begin To Get: " + info.getTurl());
		HTMLContentModel model = requestForUrl(info.getTurl());
		XLog.logger.info("\n----------End ! ");

		return model;
	}

	private static HTMLContentModel requestForUrl(String urlString) {

		if (StringTool.isEmpty(urlString)) {
			XLog.logger.error("url为空");
			return null;
		}

		CloseableHttpClient client = null;
		try {

			client = HttpClientBuilder.create().build();
			HttpClientContext context = HttpClientContext.create();
			HttpGet get = new HttpGet(urlString);
			get.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:45.0) Gecko/20100101 Firefox/45.0");
			get.setHeader("Referer",  get.getURI().getHost());
			
			CloseableHttpResponse response = client.execute(get, context);
			
			return parse(response, urlString);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static HTMLContentModel parse(CloseableHttpResponse response,
			String urlString) {

		if (response == null) {
			XLog.logger.info("解析失败,response为空");
			return null;
		}

		XLog.logger.info("Start to Parsing...");

		String encode = defaultEncoding;
		HttpEntity entity = response.getEntity();
		long contentLength = response.getEntity().getContentLength();

		if (ContentType.getOrDefault(entity).getCharset() != null) {
			encode = ContentType.getOrDefault(entity).getCharset().name();
		}
		XLog.logger.info("encoding: " + encode);

		try {

			String filepath = tempFileDir + StringTool.MD5(urlString);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), encode));
			FileOutputStream fos = new FileOutputStream(filepath);
			
			IOUtils.copy(reader, fos);
			
			if (contentLength == -1) {
				fos.flush();
				contentLength = fos.getChannel().size();
			}
			
			fos.close();
			reader.close();

			HTMLContentModel model = new HTMLContentModel();
			model.setEncoding(encode);
			model.setContentLength(contentLength);
			model.setFilePath(filepath);
			model.setOriginUrl(urlString);
			model.setRequestDate(new Date());
			
			return model;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}