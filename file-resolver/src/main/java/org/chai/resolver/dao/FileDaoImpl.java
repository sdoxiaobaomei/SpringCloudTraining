package org.chai.resolver.dao;

import com.csvreader.CsvReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellBase;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chai.resolver.TimeStampUtil;
import org.chai.resolver.entity.ResolverResultSet;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Component
public class FileDaoImpl implements FileDao{
//	private String[] title = {""};
//	private List<Map<String, String>> result = new ArrayList<>();
	private final static String TIMESTAMP = TimeStampUtil.getTimeStamp("yyyyMMdd-HHmmss");
	private final static Path OUTPUT_DIR_PATH = Paths.get("output/", TIMESTAMP);

	@Override
	public ResolverResultSet readInputFile(File inputFile) {
		ResolverResultSet resultSet = new ResolverResultSet();
		String fileName = inputFile.getName();
		String inputFilePathStr = inputFile.getAbsolutePath();

		try {
			if (fileName.contains("xlsx")) {
				resultSet = readXlsx(inputFilePathStr);
			}else if (fileName.contains("xls")){
				resultSet = readXls(inputFilePathStr);
			}else if (fileName.contains("csv")) {
				resultSet = readCsv(inputFilePathStr);
			}else {
				return resultSet;
			}
		} catch (IOException e) {
			System.out.println("读取[" + fileName + "]出错：" + e.getCause());
			e.printStackTrace();
		}
		try {
			if (!Files.isDirectory(OUTPUT_DIR_PATH)) {
				Files.createDirectories(OUTPUT_DIR_PATH);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	@Override
	public void writeOutputFile(Path outputPath, HSSFWorkbook workbook) {
		try {
			if (!Files.exists(outputPath.getParent())) {
				Files.createDirectories(outputPath.getParent());
			}
		} catch (IOException e) {
			System.out.println("输出文件"+outputPath+"创建失败 " + e.getMessage());
			e.printStackTrace();
		}

		try (FileOutputStream outputStream = new FileOutputStream(outputPath.toString())){
			System.out.println("开始输出文件：" + outputPath.getFileName());
			workbook.write(outputStream);
			System.out.println("输出文件结束！");
		} catch (IOException e) {
			System.out.println("输出文件失败" + e.getCause());
		}
	}

	@Override
	public void moveFile(Path source, Path destination, String fileName) {

	}

	private ResolverResultSet readCsv(String path) throws IOException {
		ResolverResultSet resultSet = new ResolverResultSet();
		String[] title;
		InputStream is = new FileInputStream(path);
		CsvReader csvReader = new CsvReader(is, StandardCharsets.UTF_8);
		csvReader.readHeaders();
		String[] headers = csvReader.getHeaders();
		title = headers.clone();
		title[0] = title[0].replaceAll("\ufeff", "");
		resultSet.setHeader(title);
		while (csvReader.readRecord()) {
			Map<String, String> map = new HashMap<>();
			for (String key : headers) {
				String value = csvReader.get(key);
				key = key.replaceAll("\ufeff", "");
				key = key.replaceAll("\uFEFF", "");
				map.put(key, value);
			}
			resultSet.add(map);
		}
		csvReader.close();
		return resultSet;
	}

	private ResolverResultSet readXlsx(String path) throws IOException {
		ResolverResultSet resultSet = new ResolverResultSet();
		InputStream is = new FileInputStream(path);
		// HSSFWorkbook 标识整个excel
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
		int size = xssfWorkbook.getNumberOfSheets();
		if (size == 1) {
			XSSFRow row = null;
			XSSFCell cell = null;
			String[] title = {""};
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
			row = sheet.getRow(0);
			//取标题
			if (row != null) {
				title = new String[row.getLastCellNum()];
				for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
					cell = row.getCell(y);
					title[y] = getStringVal(cell).replaceAll("\ufeff", "");
				}
			}
			resultSet.setHeader(title);
			// 遍历当前sheet中的所有行
			for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
				row = sheet.getRow(j);
				Map<String, String> m = new HashMap<>();
				// 遍历所有的列
				for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
					cell = row.getCell(y);

					String key = title[y];
					// log.info(JSON.toJSONString(key));
					m.put(key, getStringVal(cell));
				}
				resultSet.add(m);
			}
		}
		return resultSet;
	}

	private ResolverResultSet readXls(String path) throws IOException {
		ResolverResultSet resultSet = new ResolverResultSet();
		InputStream is = new FileInputStream(path);
		// HSSFWorkbook 标识整个excel
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		String[] title={""};
		int size = hssfWorkbook.getNumberOfSheets();
		if (size == 1) {
			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
			row = sheet.getRow(0);
			//取标题
			if (row != null) {
				title = new String[row.getLastCellNum()];
				for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
					cell = row.getCell(y);
					title[y] = getStringVal(cell);
				}
			}
			resultSet.setHeader(title);
			// 遍历当前sheet中的所有行
			for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
				row = sheet.getRow(j);
				Map<String, String> m = new HashMap<>();
				// 遍历所有的列
				for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
					cell = row.getCell(y);
					String key = title[y];
					// log.info(JSON.toJSONString(key));
					m.put(key, getStringVal(cell));
				}
				resultSet.add(m);
			}
		}
		return resultSet;
	}

	private static String getStringVal(CellBase cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
			case BOOLEAN:
				return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
			case FORMULA:
				return cell.getCellFormula();
			case NUMERIC:
				cell.setCellType(CellType.STRING);
				return cell.getStringCellValue();
			case STRING:
				return cell.getStringCellValue();
			default:
				return "";
		}
	}
}
