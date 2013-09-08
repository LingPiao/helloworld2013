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
import com.emenu.models.Dish;

/**
 * Servlet implementation class EditorAction
 */
public class DishEditorAction extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private EMenuDao emenuDao = new EMenuDaoImpl();
	private String appPath = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DishEditorAction() {
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
		String language = ServletUtils.getStringValue(request, "language");
		XmlUtils.build(language, appPath);

		System.out.println("=============action:" + action);
		if ("add".equals(action)) {
			Dish dish = getDish(request);
			emenuDao.saveDish(dish);
		} else if ("edit".equals(action)) {
			Dish dish = getDish(request);
			emenuDao.updateDish(dish);
		} else if ("remove".equals(action)) {
			List<Long> ids = ServletUtils.getIds(request, "ids");
			emenuDao.removeDishes(ids);
		} else if ("getHtml".equals(action)) {
			String html = ServletUtils.getStringValue(request, "file");
			String desc = XmlUtils.getInstance().readHtml(html);
			ServletUtils.responseWrite(response, desc);
			return;
		}
		ServletUtils.responseJSonWrite(response, "OK");
	}

	private Dish getDish(HttpServletRequest request) {
		String id = ServletUtils.getStringValue(request, "id");
		String name = ServletUtils.getStringValue(request, "name");
		String price = ServletUtils.getStringValue(request, "price");
		String img = ServletUtils.getStringValue(request, "img");
		// String descFile = ServletUtils.getStringValue(request, "file");
		String belongsTo = ServletUtils.getStringValue(request, "belongsTo");
		boolean enabled = ServletUtils.getBooleanValue(request, "enabled");
		boolean recommended = ServletUtils.getBooleanValue(request, "recommended");
		String introduction = ServletUtils.getStringValue(request, "introduction");
		String description = ServletUtils.getStringValue(request, "description");
		if (description != null) {
			description = description.replaceAll("http://localhost:8080/menuEditor/", "");
		}

		Dish d = new Dish();
		d.setName(name);
		if (id == null || id.trim().length() < 1) {
			d.setId(XmlUtils.getInstance().getMaxId4Dish());
		} else {
			d.setId(Long.parseLong(id));
		}
		d.setPrice(Float.parseFloat(price));
		String imgParent = "dishes/images/";
		if (img.startsWith(imgParent)) {
			d.setImage(img);
		} else {
			d.setImage(imgParent + img);
		}
		d.setEnabled(enabled);
		d.setRecommended(recommended);
		// d.setFile(descFile);
		d.setIntroduction(introduction);
		d.setBelongsTo(ServletUtils.getIds(belongsTo));
		d.setDescription(description);

		return d;

	}

}
