package com.emenu.dao.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.emenu.common.ServletUtils;
import com.emenu.common.XmlUtils;
import com.emenu.dao.EMenuDao;
import com.emenu.models.Dish;
import com.emenu.models.MenuItem;

public class EMenuDaoImpl implements EMenuDao {

	@Override
	public List<MenuItem> loadMenus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MenuItem> loadMenuById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dish> loadDishes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dish> loadDishes(long menuItemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveMenu(MenuItem menuItem) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		XMLWriter output = null;
		String xml = XmlUtils.getInstance().getMainMenuXml();
		try {
			document = saxReader.read(xml);

			Element root = document.getRootElement();
			Element itemElement = DocumentHelper.createElement("MenuItem");
			root.add(itemElement);
			itemElement.addAttribute("id", String.valueOf(menuItem.getId()));
			itemElement.addAttribute("name", menuItem.getName());
			itemElement.addAttribute("isSpecial", menuItem.isSpecial() ? "true" : "false");

			output = new XMLWriter(new FileWriter(new File(xml)));
			output.write(document);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) try {
				output.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void saveDish(Dish dish) {

		String html = saveDesc2Html4Dish(dish);
		if (html == null) {
			return;
		}
		String descFile = "dishes/" + getHtmlFileName(dish.getName());

		SAXReader saxReader = new SAXReader();
		Document document = null;
		XMLWriter output = null;
		String xml = XmlUtils.getInstance().getDishesXml();
		try {
			document = saxReader.read(xml);

			Element root = document.getRootElement();
			Element itemElement = DocumentHelper.createElement("Dish");
			root.add(itemElement);
			itemElement.addAttribute("id", String.valueOf(dish.getId()));
			itemElement.addAttribute("name", dish.getName());
			itemElement.addAttribute("belongsTo", ServletUtils.getIds(dish.getBelongsTo()));
			itemElement.addAttribute("image", dish.getImage());
			itemElement.addAttribute("file", dish.getFile());
			itemElement.addAttribute("enabled", dish.isEnabled() ? "true" : "false");
			itemElement.addAttribute("price", String.valueOf(dish.getPrice()));
			itemElement.addAttribute("file", descFile);
			itemElement.addText(dish.getIntroduction());

			output = new XMLWriter(new FileWriter(new File(xml)));
			output.write(document);
		} catch (Exception e) {
			e.printStackTrace();
			removeFile(html);
		} finally {
			if (output != null) try {
				output.close();
			} catch (IOException e) {
			}
		}

	}

	private void removeFile(String file) {
		File f = new File(file);
		f.delete();
	}

	/**
	 * 
	 * @param dish
	 * @return the name of the saved file - when success, null - otherwise
	 * 
	 */
	private String saveDesc2Html4Dish(Dish dish) {
		Writer writer = null;
		try {
			String fn = XmlUtils.getInstance().getDescFilePath() + getHtmlFileName(dish.getName());
			writer = new FileWriter(fn, true);

			StringBuilder sb = new StringBuilder();
			sb.append("<!DOCTYPE HTML><html xmlns=\"http://www.w3.org/1999/xhtml\">");
			sb.append("<head>");
			sb.append("<title>").append(dish.getName()).append("</title>");
			sb.append("</head><body>");
			sb.append("\n");

			sb.append(XmlUtils.getInstance().fixPath4HtmlWrite(dish.getDescription()));

			sb.append("\n");
			sb.append("</body></html>");

			writer.write(sb.toString());

			return fn;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private String getHtmlFileName(String fileName) {
		return fileName + ".html";
	}

	@Override
	public boolean removeMenus(List<Long> ids) {
		return removeItems(true, ids);
	}

	@SuppressWarnings("rawtypes")
	private boolean removeItems(boolean isMenuItem, List<Long> ids) {
		boolean r = false;
		SAXReader saxReader = new SAXReader();
		Document document = null;
		XMLWriter output = null;
		String xmlFile = XmlUtils.getInstance().getMainMenuXml();

		List<String> htmlFiles = new ArrayList<String>();
		if (!isMenuItem) {
			xmlFile = XmlUtils.getInstance().getDishesXml();
		}

		try {
			document = saxReader.read(xmlFile);
			String xpath = "//MainMenu/MenuItem";
			if (!isMenuItem) {
				xpath = "//Dishes/Dish";
			}
			Iterator iter = document.selectNodes(xpath).iterator();
			while (iter.hasNext()) {
				Element e = (Element) iter.next();
				Attribute i = e.attribute("id");
				if (ids.contains(Long.parseLong(i.getText()))) {
					if (e.attribute("file") != null) {
						htmlFiles.add(e.attribute("file").getText());
					}
					e.detach();
					r = true;
				}
			}
			if (isMenuItem) {
				cleanReferences(ids);
			}
			output = new XMLWriter(new FileWriter(new File(xmlFile)));
			output.write(document);

			for (String f : htmlFiles) {
				if (f != null) {
					removeFile(XmlUtils.getInstance().getPath("/" + f));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			r = false;
		} finally {
			if (output != null) try {
				output.close();
			} catch (IOException e) {
			}
		}
		return r;
	}

	@SuppressWarnings("rawtypes")
	private boolean cleanReferences(List<Long> menuIds) {
		boolean r = false;
		SAXReader saxReader = new SAXReader();
		Document document = null;
		XMLWriter output = null;
		String xmlFile = XmlUtils.getInstance().getDishesXml();
		try {
			document = saxReader.read(xmlFile);
			Iterator iter = document.selectNodes("//Dishes/Dish").iterator();
			while (iter.hasNext()) {
				Element e = (Element) iter.next();
				Attribute aRefIds = e.attribute("belongsTo");
				List<Long> rids = ServletUtils.getIds(aRefIds.getText());
				rids.removeAll(menuIds);
				aRefIds.setText(ServletUtils.getIds(rids));
				r = true;
			}
			output = new XMLWriter(new FileWriter(new File(xmlFile)));
			output.write(document);
		} catch (Exception e) {
			e.printStackTrace();
			r = false;
		} finally {
			if (output != null) try {
				output.close();
			} catch (IOException e) {
			}
		}
		return r;
	}

	@Override
	public boolean removeDishes(List<Long> ids) {
		return removeItems(false, ids);
	}

	@Override
	public long getMaxId4Menu() {
		return getMaxId(XmlUtils.getInstance().getMainMenuXml(), "//MainMenu/MenuItem");
	}

	@Override
	public long getMaxId4Dish() {
		return getMaxId(XmlUtils.getInstance().getDishesXml(), "//Dishes/Dish");
	}

	@SuppressWarnings("rawtypes")
	private long getMaxId(String xml, String xpath) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		long max = 1;
		try {
			document = saxReader.read(xml);
			Iterator iter = document.selectNodes(xpath).iterator();
			while (iter.hasNext()) {
				Element e = (Element) iter.next();
				Iterator it = e.attributes().iterator();
				while (it.hasNext()) {
					Attribute a = (Attribute) it.next();
					if ("id".equals(a.getName())) {
						long id = Long.parseLong(a.getText());
						if (id > max) {
							max = id;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return max;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean updateMenu(MenuItem menuItem) {
		boolean r = false;
		SAXReader saxReader = new SAXReader();
		Document document = null;
		XMLWriter output = null;
		String xmlFile = XmlUtils.getInstance().getMainMenuXml();
		try {
			document = saxReader.read(xmlFile);
			Iterator iter = document.selectNodes("//MainMenu/MenuItem").iterator();
			while (iter.hasNext()) {
				Element e = (Element) iter.next();
				Attribute i = e.attribute("id");
				if (Long.parseLong(i.getText()) == menuItem.getId()) {
					Attribute n = e.attribute("name");
					n.setValue(menuItem.getName());

					Attribute isp = e.attribute("isSpecial");
					isp.setValue(menuItem.isSpecial() ? "true" : "false");

					r = true;
				}
			}
			output = new XMLWriter(new FileWriter(new File(xmlFile)));
			output.write(document);
		} catch (Exception e) {
			e.printStackTrace();
			r = false;
		} finally {
			if (output != null) try {
				output.close();
			} catch (IOException e) {
			}
		}
		return r;
	}

	@Override
	public boolean updateDish(Dish dish) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(dish.getId());

		if (removeDishes(ids)) {
			saveDish(dish);
			return true;
		}
		return false;
	}

}
