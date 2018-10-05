package com.greenback.GreenbackProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping(value = "/")
	public ModelAndView convert(@RequestParam("fileupload") MultipartFile input) throws IOException, ParseException {
		String fileName = input.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		String pre = fileName.substring(0, fileName.lastIndexOf('.'));
		if (!ext.equals("html")) {
			return new ModelAndView("index", "error", "The selected file is not an HTML file");
		}

		File convFile = new File(input.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(input.getBytes());
		fos.close();
		Document doc = Jsoup.parse(convFile, "UTF-8");
		Element arr = doc.getElementById("main");

		JSONObject json = new JSONObject();
		DateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		DateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");

		String key = "";
		double amount = 0;
		int j = 0;

		for (int i = 0; i < 3; i++) {
			String value = arr.select("strong").get(j).text();
			if (i == 0) {
				key = "ref";
				if (!arr.getElementsByClass("invoice-no").isEmpty()) {
					value = arr.getElementsByClass("invoice-no").text();
				} else {
					j++;
				}
			} else if (i == 1) {
				key = "date";
				Date date = null;
				try {
					date = sdf.parse(value);

				} catch (ParseException e) {
					date = sdf1.parse(value);
				}
				DateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");
				value = formattedDate.format(date);
				j++;
			} else if (i == 2) {
				key = "currency";
				amount = Double.parseDouble(arr.select("strong").get(j).text().substring(1,
						arr.select("strong").get(j).text().lastIndexOf('.') + 3));
				if (arr.select("strong").get(j).text()
						.substring(arr.select("strong").get(j).text().lastIndexOf('.') + 3,
								arr.select("strong").get(j).text().length())
						.isEmpty()) {
					value = "USD";
				} else {
					value = arr.select("strong").get(j).text().substring(
							arr.select("strong").get(j).text().lastIndexOf('.') + 4,
							arr.select("strong").get(j).text().length());
				}

			}

			if (amount != 0) {
				json.put(key, value);
				json.put("amount", amount);
			} else {
				json.put(key, value);
			}

		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File file = new File(pre + ".json");
		file.createNewFile();
		FileWriter write = new FileWriter(file);
		write.write(gson.toJson(json));
		write.flush();
		write.close();
		return new ModelAndView("index", "file", file.getPath());

	}

	@RequestMapping(value = "{filename:.+}")
	public void getSteamingFile1(@PathVariable String filename, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		InputStream inputStream = new FileInputStream(new File(filename));
		int nRead;
		while ((nRead = inputStream.read()) != -1) {
			response.getWriter().write(nRead);
		}
		inputStream.close();
	}

}
