package com.shotacon.wx.util.spider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TumblrUtil {

	private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取month个月的日期格式：yyyy/M
	 * 
	 * @return
	 */
	public static List<String> getAllDateByMonth(int num) {
		List<String> list = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < num; i++) {
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			calendar.add(Calendar.MONTH, -1);
			String monthStr = month < 10 ? "0" + month : String.valueOf(month);
			list.add(year + "/" + monthStr);
		}
		return list;
	}

	public static long getFirstDayMillisecondsByMonth(String month) {
		return LocalDateTime.parse(month.replace("/", "-") + "-01 00:00:00", dtf).toInstant(ZoneOffset.of("+8"))
				.toEpochMilli();
	}

	public static List<String> getAllDateByMonthString(String monthStr) {
		List<String> list = new ArrayList<String>();
		for (String m : monthStr.split(",")) {
			list.add(m);
		}
		return list;
	}

	public static String getUsernameByUrl(String url) {
		if (url.startsWith("http://")) {
			url = url.substring(7);
		} else if (url.startsWith("https://")) {
			url = url.substring(8);
		}
		int index = url.indexOf(".");
		String username = url.substring(0, index);
		return username;
	}

	public static String getUrl(String url) {
		if (!url.endsWith("/")) {
			url += "/";
		}
		return url;
	}

	public static String getParentFile(String filePath, String userName) {
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}
		return filePath + userName;
	}

	public static String getFile(String parentFilePath, String yearMonth) {
		yearMonth = yearMonth.replace("/", "_");
		String fileName = parentFilePath + File.separator + yearMonth + ".txt";
		return fileName;
	}

	public static boolean createNewFile(File file) throws IOException {
		if (!file.exists()) {
			makeDir(file.getParentFile());
		}
		return file.createNewFile();
	}

	private static void makeDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	public static void mergeFiles(String parentFilePath) throws IOException {
		Set<String> linesSet = new HashSet<String>();
		Path parentpath = Paths.get(parentFilePath);
		if (!Files.exists(parentpath)) {
			log.info("parentFile [{}] not exist", parentFilePath);
			return;
		}

		Files.list(parentpath).forEach(path -> {
			try {
				if(!path.getFileName().equals(parentpath.getFileName())) {
					linesSet.addAll(Files.readAllLines(path));
				}
			} catch (IOException e) {
				log.info("File {} readAllLines error: {}", path.getFileName(), e.getMessage());
			}
		});

		Path finalFile = Paths.get(parentFilePath + File.separator + parentpath.getFileName() + ".txt");
		Files.write(finalFile, linesSet, StandardCharsets.UTF_8);
	}
	
	public static void main(String[] args) throws IOException {
		mergeFiles("/data/temp/just-an-insane-boy");
	}
}
