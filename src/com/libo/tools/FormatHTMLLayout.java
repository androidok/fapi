package com.libo.tools;

import java.text.SimpleDateFormat;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

public class FormatHTMLLayout extends HTMLLayout {

	public FormatHTMLLayout() {
	}

	protected final int BUF_SIZE = 256;

	protected final int MAX_CAPACITY = 1024;

	static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";

	private StringBuffer sbuf = new StringBuffer(BUF_SIZE);
	
	String title="系统操作日志";

	public static final String TITLE_OPTION = "Title";

	// Print no location info by default
	boolean locationInfo = true;
	
	public static int i = 1 ;
	
	public String format(LoggingEvent event) {
		if (sbuf.capacity() > MAX_CAPACITY) {
			sbuf = new StringBuffer(BUF_SIZE);
		} else {
			sbuf.setLength(0);
		}
		
		if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
			sbuf.append(Layout.LINE_SEP + "<tr style='background-color: red;'>" + Layout.LINE_SEP);
		}else if(event.getLevel() == Level.WARN){
			sbuf.append(Layout.LINE_SEP + "<tr style='background-color: =\"#FFA54F\";'>" + Layout.LINE_SEP);
		}else {
			sbuf.append(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);
		}

 		sbuf.append("<td>");
		sbuf.append(String.valueOf(i++));
		sbuf.append("</td>").append(Layout.LINE_SEP);
		
		sbuf.append("<td>");
		sbuf.append("userId");
		sbuf.append("</td>").append(Layout.LINE_SEP);
		
		sbuf.append("<td>");
		sbuf.append("username");
		sbuf.append("</td>").append(Layout.LINE_SEP);
		
 	
		sbuf.append("<td  width='125px;'>");
		sbuf.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
		sbuf.append("</td>").append(Layout.LINE_SEP);

	/*	String escapedThread = Transform.escapeTags(event.getThreadName());
		sbuf.append("<td title=\"" + escapedThread + " thread\">");
		sbuf.append(escapedThread);
		sbuf.append("</td>").append(Layout.LINE_SEP);
	*/
		sbuf.append("<td  width='125px;' title=\"级别\">");
		if (event.getLevel().equals(Level.FATAL)) {
			sbuf.append("<font color=\"#339933\">");
			sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
			sbuf.append("</font>");
		} else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
			sbuf.append("<font color=\"#FFA54F\"><strong>");
			sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
			sbuf.append("</strong></font>");
		} else if(event.getLevel().isGreaterOrEqual(Level.INFO)) {
			sbuf.append("<font color=\"green\"><strong>");
			sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
			sbuf.append("</strong></font>");
		}else if(event.getLevel().isGreaterOrEqual(Level.ERROR)) {
			sbuf.append("<font color=\"red\"><strong>");
			sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
			sbuf.append("</strong></font>");
		}
		else {
			sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
		}
		sbuf.append("</td>").append(Layout.LINE_SEP);
		
/*		String escapedLogger = Transform.escapeTags(event.getLoggerName().substring(event.getLoggerName().lastIndexOf(".")));
		sbuf.append("<td title=\"类名\">");
		sbuf.append(escapedLogger);
		sbuf.append("</td>").append(Layout.LINE_SEP);
*/
		if (locationInfo) {
			LocationInfo locInfo = event.getLocationInformation();
			sbuf.append("<td  width='125px;' title=\"行号\">");
			sbuf.append(Transform.escapeTags(locInfo.getFileName()));
			sbuf.append(':');
			sbuf.append(locInfo.getLineNumber());
			sbuf.append("</td>").append(Layout.LINE_SEP);
		}
		/*Map session = ActionContext.getContext().getSession();
		if(session!=null){
			User user = (User) session.get(Constants.USER_IN_SESSION);
			sbuf.append("<td>"+user.getName()+"</td>");
		}else{
			sbuf.append("<td>&nbsp;</td>");
		}*/
		
		
		
		
		sbuf.append("<td title=\"日志信息\">");
		sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
		sbuf.append("</td>").append(Layout.LINE_SEP);
		sbuf.append("</tr>" + Layout.LINE_SEP);

		if (event.getNDC() != null) {
			sbuf.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
			sbuf.append("NDC: " + Transform.escapeTags(event.getNDC()));
			sbuf.append("</td></tr>").append(Layout.LINE_SEP);
		}

		String[] s = event.getThrowableStrRep();
		if (s != null) {
			sbuf.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : 12px;\" colspan=\"4\">");
			appendThrowableAsHTML(s, sbuf);
			sbuf.append("</td></tr>").append(Layout.LINE_SEP);
		}
		
		return sbuf.toString();
	}

	private static void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
		if (s != null) {
			int len = s.length;
			if (len == 0)
				return;
			sbuf.append(Transform.escapeTags(s[0]));
			sbuf.append(Layout.LINE_SEP);
			for (int i = 1; i < len; i++) {
				sbuf.append(TRACE_PREFIX);
				sbuf.append(Transform.escapeTags(s[i]));
				sbuf.append(Layout.LINE_SEP);
			}
		}
	}

	/**
	 * Returns appropriate HTML headers.
	 */
	public String getHeader() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Layout.LINE_SEP);
		sbuf.append("<html>" + Layout.LINE_SEP);
//		sbuf.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\">");
		sbuf.append("<head>" + Layout.LINE_SEP);
		sbuf.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
		sbuf.append("<title>" + title + "</title>" + Layout.LINE_SEP);
		sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
		sbuf.append("<!--" + Layout.LINE_SEP);
		sbuf.append("body, table {font-family: '宋体',arial,sans-serif; font-size: 12px;}" + Layout.LINE_SEP);
		sbuf.append("th {background: #228B22; color: #FFFFFF; text-align: left;}" + Layout.LINE_SEP);
		sbuf.append("-->" + Layout.LINE_SEP);
		sbuf.append("</style>" + Layout.LINE_SEP);
		sbuf.append("</head>" + Layout.LINE_SEP);
		sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">" + Layout.LINE_SEP);
	//	sbuf.append("<hr size=\"1\" noshade>" + Layout.LINE_SEP);
	//	sbuf.append("Log session start time " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "<br>" + Layout.LINE_SEP);
	//	sbuf.append("<p>" + Layout.LINE_SEP);
		sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#E8E8E8\" width=\"100%\">" + Layout.LINE_SEP);
		
		
		sbuf.append("<tr>").append(Layout.LINE_SEP);
	 	sbuf.append("<th width='30px;'>序列</th>").append(Layout.LINE_SEP);
	 	sbuf.append("<th width='60px;'>操作人ID</th>").append(Layout.LINE_SEP);
	 	sbuf.append("<th width='50px;'>操作人</th>");
		sbuf.append("<th width='60px;'>执行时间</th>").append(Layout.LINE_SEP);
		sbuf.append("<th width='30px;'>级别</th>").append(Layout.LINE_SEP);
	//	sbuf.append("<th>所在类</th>" + Layout.LINE_SEP);
		if (locationInfo) {
			sbuf.append("<th  width='80px;'>所在行</th>").append(Layout.LINE_SEP);
		}
		
		sbuf.append("<th>信息</th>").append(Layout.LINE_SEP);
		sbuf.append("</tr>").append(Layout.LINE_SEP);
		sbuf.append("<br></br>").append(Layout.LINE_SEP);
		return sbuf.toString();
	}

}
