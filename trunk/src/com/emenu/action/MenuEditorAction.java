package com.emenu.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.emenu.common.ServletUtils;
import com.emenu.common.XmlUtils;
import com.emenu.dao.EMenuDao;
import com.emenu.dao.impl.EMenuDaoImpl;
import com.emenu.models.MenuItem;

/**
 * Servlet implementation class EditorAction
 */
public class MenuEditorAction extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private EMenuDao emenuDao = new EMenuDaoImpl();
	private String appPath = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MenuEditorAction() {
		super();
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
		if (appPath == null) appPath = this.getServletContext().getRealPath("/");
		String action = ServletUtils.getStringValue(request, "action");
		String name = ServletUtils.getStringValue(request, "name");
		String language = ServletUtils.getStringValue(request, "language");
		XmlUtils xmlUtils = XmlUtils.build(language, appPath);
		System.out.println("=============action:" + action);
		if ("add".equals(action)) {
			MenuItem mi = new MenuItem(xmlUtils.getMaxId4Menu(), name);
			emenuDao.saveMenu(mi);
		} else if ("edit".equals(action)) {
			String id = ServletUtils.getStringValue(request, "id");
			MenuItem mi = new MenuItem(Long.parseLong(id), name);
			emenuDao.updateMenu(mi);
		} else if ("remove".equals(action)) {
			List<Long> ids = ServletUtils.getIds(request, "ids");
			emenuDao.removeMenus(ids);
		}
		ServletUtils.responseJSonWrite(response, "OK");
	}
}
