package com.emenu.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.emenu.dao.EMenuDao;
import com.emenu.dao.impl.EMenuDaoImpl;

public class XmlUtils {

	public final static String DEFAULT_NUMBER = "0";

	private final static String DATA = "data/";
	private final static String MAIN_MENU_XML = "/MainMenu.xml";
	private final static String DISHES_XML = "/Dishes.xml";
	private String appPath = "";

	private long maxId4Menu = 0;
	private long maxId4Dish = 0;

	private EMenuDao dao;

	private static Map<String, XmlUtils> cache = new HashMap<String, XmlUtils>();
	private String language = Languages.en_US.name();
	private static XmlUtils instance = null;

	private XmlUtils() {
	}

	public static XmlUtils build(String language, String appPath) {
		XmlUtils xu = cache.get(language);
		if (xu != null) {
			instance = xu;
			return xu;
		}
		xu = new XmlUtils();
		xu.language = language;
		xu.appPath = appPath;
		instance = xu;
		xu.dao = new EMenuDaoImpl();
		xu.maxId4Menu = xu.dao.getMaxId4Menu();
		xu.maxId4Dish = xu.dao.getMaxId4Dish();
		cache.put(language, xu);
		return xu;
	}

	public static XmlUtils getInstance() {
		return instance;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMainMenuXml() {
		return getPath(MAIN_MENU_XML);
	}

	public String getDishesXml() {
		return getPath(DISHES_XML);
	}

	public String getImagePath() {
		return getPath("/dishes/images/");
	}

	public String getAudioPath() {
		return getPath("/dishes/audios/");
	}

	public String getVideoPath() {
		return getPath("/dishes/videos/");
	}

	public String getPath(String file) {
		return appPath + DATA + language + file;
	}

	public String getDescFilePath() {
		return getPath("/dishes/");
	}

	public String fixPath4HtmlWrite(String str) {
		return str.replaceAll("data/.{3,10}/dishes/", "");
	}

	public String fixPath4HtmlRead(String str) {
		return str.replaceAll("src=\"", "src=\"data/" + this.language + "/dishes/");
	}

	public String removeHtmlTags(String str) {
		return str.replaceAll("<!DOCTYPE.+<body>", "").replaceAll("</body></html>", "");
	}

	public String readHtml(String fileName) {
		File file = new File(this.getPath("/" + fileName));
		String fcontent = readFile(file);
		return fixPath4HtmlRead(removeHtmlTags(fcontent));
	}

	public String loadMenu() {
		File xml = new File(getMainMenuXml());
		return readFile(xml);
	}

	public String loadDish() {
		File xml = new File(getDishesXml());
		return readFile(xml);
	}

	private String readFile(File file) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return sb.toString();
	}

	public long getMaxId4Menu() {
		this.maxId4Menu++;
		return maxId4Menu;
	}

	public long getMaxId4Dish() {
		this.maxId4Dish++;
		return maxId4Dish;
	}

	public boolean isImage(String fileName) {
		return fileName.matches(".*\\.(jpg|jpeg|png|gif)$");
	}

	public boolean isAudio(String fileName) {
		return fileName.matches(".*\\.mp3$");
	}

	public boolean isVideo(String fileName) {
		return fileName.matches(".*\\.mp4$");
	}

	public static void main(String[] args) {
		XmlUtils.build("en_US", "E:/emenu/MenuEditor/MenuEditor/WebContent/");
		System.out.println(XmlUtils.getInstance().removeHtmlTags("<!DOCTYPE HTML><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test</title></head><body>This is a test dish</body></html>"));

		System.out.println("xxx.aaa.jpg=" + XmlUtils.getInstance().isImage("xxx.aaa.jpg"));
		System.out.println("xxx.aaa.PNG=" + XmlUtils.getInstance().isImage("xxx.aaa.png"));

		System.out.println("xxx.aaa.mp3=" + XmlUtils.getInstance().isAudio("xxx.aaa.mp3"));
		System.out.println("xxx.aaa.mp4=" + XmlUtils.getInstance().isVideo("xxx.aaa.mp4"));
		System.out.println("xxx.aaa.mp4=" + XmlUtils.getInstance().isAudio("xxx.aaa.mp4"));
	}

}
