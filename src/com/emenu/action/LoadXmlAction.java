package com.emenu.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.emenu.common.ServletUtils;
import com.emenu.common.XmlUtils;

/**
 * Servlet implementation class LoadXmlAction
 */
public class LoadXmlAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String appPath = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoadXmlAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (appPath == null)
			appPath = this.getServletContext().getRealPath("/");
		String xml = ServletUtils.getStringValue(request, "xml");
		String language = ServletUtils.getStringValue(request, "language");
		XmlUtils.build(language, appPath);
		String content = "";
		if ("MainMenu.xml".equals(xml)) {
			content = XmlUtils.getInstance().loadMenu();
		} else if ("Dishes.xml".equals(xml)) {
			content = XmlUtils.getInstance().loadDish();
		}

		ServletUtils.responseWriteXML(response, content);

	}

}
