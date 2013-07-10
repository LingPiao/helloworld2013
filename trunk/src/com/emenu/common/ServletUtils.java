package com.emenu.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils {

	public static String getStringValue(HttpServletRequest request, String fieldName) {
		Object v = request.getParameter(fieldName);
		if (v == null) {
			return null;
		}
		return (String) v;
	}

	public static boolean getBooleanValue(HttpServletRequest request, String fieldName) {
		Object v = request.getParameter(fieldName);
		if (v == null) {
			return false;
		}
		String sv = (String) v;
		return "1".equals(sv) || "true".equals(sv);
	}

	public static long getLongValue(HttpServletRequest request, String fieldName) {
		String v = getStringValue(request, fieldName);
		if (v == null) {
			return 0;
		}
		return Long.parseLong(v.toString());
	}

	public static void responseJSonWrite(HttpServletResponse response, String msg) throws IOException {
		setEncodingType(response);
		PrintWriter out = response.getWriter();
		out.print("{\"success\":true,\"msg\":\"" + msg + "\"}");
	}

	public static void responseWrite(HttpServletResponse response, String msg) throws IOException {
		setEncodingType(response);
		PrintWriter out = response.getWriter();
		out.print(msg);
	}

	public static void responseWriteXML(HttpServletResponse response, String msg) throws IOException {
		setEncodingXML(response);
		PrintWriter out = response.getWriter();
		out.print(msg);
	}

	public static void responseJSonWrite(HttpServletResponse response, Map<String, String> values) throws IOException {
		setEncodingType(response);
		PrintWriter out = response.getWriter();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		int i = 0;
		int s = values.size();
		for (String key : values.keySet()) {
			sb.append("\"").append(key).append("\"").append(":").append("\"").append(values.get(key)).append("\"");
			if (i++ < s - 1) {
				sb.append(",");
			}
		}
		sb.append("}");
		out.print(sb.toString());
	}

	private static void setEncodingType(HttpServletResponse response) {
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
	}

	private static void setEncodingXML(HttpServletResponse response) {
		response.setHeader("Content-type", "text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
	}

	public static List<Long> getIds(HttpServletRequest request, String fieldName) {
		String v = getStringValue(request, fieldName);
		if (v == null) {
			return new ArrayList<Long>();
		}

		return getIds(v);
	}

	public static String getIds(List<Long> ids) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = ids.size(); i < len; i++) {
			sb.append(ids.get(i));
			if (i < len - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static List<Long> getIds(String ids) {
		String[] s = ids.split(",");
		List<Long> r = new ArrayList<Long>();
		for (String id : s) {
			r.add(Long.parseLong(id));
		}
		return r;
	}
}
