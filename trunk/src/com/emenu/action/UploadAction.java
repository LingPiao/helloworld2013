package com.emenu.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.emenu.common.Languages;
import com.emenu.common.ServletUtils;
import com.emenu.common.XmlUtils;

/**
 * Servlet implementation class UploadAction
 */
public class UploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String tempPath = "/temp";
	private String appPath = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = getServletContext();
		tempPath = context.getRealPath(tempPath);
		System.out.println("tempPath=" + tempPath);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String savedFileName = null;
		Map<String, String> jsonResp = new HashMap<String, String>();
		jsonResp.put("success", "true");
		jsonResp.put("msg", "OK");

		try {
			DiskFileItemFactory diskFactory = new DiskFileItemFactory();
			diskFactory.setSizeThreshold(8 * 1024);
			diskFactory.setRepository(new File(tempPath));

			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			upload.setSizeMax(100 * 1024 * 1024);
			List<FileItem> files = upload.parseRequest(request);
			String lan = Languages.en_US.name();
			for (FileItem item : files) {
				if (item.isFormField() && "language".equals(item.getFieldName())) {
					lan = item.getString();
				}
			}

			if (appPath == null)
				appPath = this.getServletContext().getRealPath("/");
			XmlUtils.build(lan, appPath);

			for (FileItem item : files) {
				if (!item.isFormField()) {
					savedFileName = processUploadFile(item);
				}
			}

		} catch (Exception e) {
			System.out.println("Error while uploading file.");
			e.printStackTrace();
		}
		if (savedFileName != null && savedFileName.length() > 0) {
			jsonResp.put("fileName", savedFileName);
		}
		ServletUtils.responseJSonWrite(response, jsonResp);
	}

	private String processUploadFile(FileItem item) {
		String fileName = item.getName();
		if (fileName == null) {
			return null;
		}
		System.out.println("Uploaded file: " + fileName);
		int index = fileName.lastIndexOf("\\");
		fileName = fileName.substring(index + 1, fileName.length());

		long fileSize = item.getSize();

		if ("".equals(fileName) || fileSize == 0) {
			System.out.println("Empty file ignored");
			return null;
		}

		fileName = fileName.toLowerCase();

		String filePath = null;

		if (XmlUtils.getInstance().isImage(fileName)) {
			filePath = XmlUtils.getInstance().getImagePath();
		} else if (XmlUtils.getInstance().isAudio(fileName)) {
			filePath = XmlUtils.getInstance().getAudioPath();
		} else if (XmlUtils.getInstance().isVideo(fileName)) {
			filePath = XmlUtils.getInstance().getVideoPath();
		}

		System.out.println("FilePath=" + filePath);
		File uploadFile = new File(filePath, fileName);

		try {
			item.write(uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("Saved file: " + uploadFile.getPath());
		System.out.println("Size: " + fileSize);
		return fileName;
	}

}
