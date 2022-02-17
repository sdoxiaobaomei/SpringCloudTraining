package org.chai.resolver.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.chai.resolver.TimeStampUtil;
import org.chai.resolver.dao.FileDao;
import org.chai.resolver.entity.ResolverResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ResolverServiceImpl implements ResolverService {
	private final static String TIMESTAMP = TimeStampUtil.getTimeStamp("yyyyMMdd-HHmmss");
	private final static Path OUTPUT_DIR_PATH = Paths.get("C:\\file_resolver\\output\\");
	private String inputDirStr = "C:\\file_resolver\\input";
	private String backupDirStr = "C:\\file_resolver\\backup";
	@Autowired
	FileDao fileDao;
	private Boolean isYesterday;


	@Override
	public void resolveOrder(Boolean isYesterday) throws Exception {
		File inputDir = new File(inputDirStr);
		if (!inputDir.exists()) {
			boolean mkdirs = inputDir.mkdirs();
		}
		File[] inputFileList = inputDir.listFiles();
		if (inputFileList == null) {
			throw new Exception("解析文件失败， 未找到输入文件");
		}
		this.isYesterday = isYesterday;
		ResolverResultSet resultSet = new ResolverResultSet();
		for (File file : inputFileList) {
			String fileName = file.getName();
			String inputFilePathStr = file.getAbsolutePath();
			resultSet = fileDao.readInputFile(file);
		}
//		List<Map<String, String>> content = resultSet.getContent();
		//创建输出文件夹
		resolveFilter(resultSet);
		try {
			moveInputFileToBackup();
		} catch (IOException e) {
			System.out.println("移动文件失败：" + e.getLocalizedMessage());
		}

	}
	private void resolveFilter(ResolverResultSet resultSet) {
		filterRemove(resultSet, "订单状态", "已失效", "已失效");
		filterOutput(resultSet, "推广位名称", "博观达人", "博观");
		filterOutput(resultSet, "推广位名称", "客户自己的达人", "KS达人", "邦盟达人");
		filterOutput(resultSet, "推广位名称", "德","德");
		filterOutput(resultSet, "推广位名称", "橙子","橙子");
	}

	private void filterOutput(ResolverResultSet resultSet, String columnName, String outputName, String... keyWords) {
		List<Map<String, String>> content = resultSet.getContent();
		if (keyWords == null) {
			return;
		}
		List<Map<String, String>> outputRows = new ArrayList<>();
 		for (String keyWord : keyWords) {
			List<Map<String, String>> filterRows = content.stream().filter(Objects::nonNull).filter(row -> Optional.ofNullable(row.get(columnName)).orElse("").trim().contains(keyWord)).collect(Collectors.toList());
			content.removeAll(filterRows);
			outputRows.addAll(filterRows);
		}
		exportXls(resultSet.getHeader(), outputRows, outputName);
	}

	private void filterRemove(ResolverResultSet resultSet, String columnName, String keyWord, String outputName) {
		List<Map<String, String>> content = resultSet.getContent();
		List<Map<String, String>> removeRows = content.stream().filter(row -> row.get(columnName).contains(keyWord)).collect(Collectors.toList());
		resultSet.setContents(content.stream().filter(row -> !row.get(columnName).contains(keyWord)).collect(Collectors.toList()));
		exportXls(resultSet.getHeader(), removeRows, outputName);
	}

	private void moveInputFileToBackup() throws IOException {
		Path backupPath = Paths.get(this.backupDirStr);
		if (!Files.exists(backupPath)) {
			Files.createDirectories(backupPath);
		}
		File[] files = new File(this.inputDirStr).listFiles();
		List<Path> inputFilePaths = new ArrayList<>();
		if (files == null || files .length == 0) {
			return;
		}
		for (File file : files) {
			inputFilePaths.add(file.toPath());
		}
		for (Path inputFilePath: inputFilePaths) {
			Path backupFilePath = Paths.get(this.backupDirStr + "\\" + inputFilePath.getFileName());
			Files.move(inputFilePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void exportXls(String[] header, List<Map<String, String>> rows, String outputName) {
		String timeStamp = TimeStampUtil.getTimeStamp("yyyyMMdd-HHmm");
		String outputFileName = outputName + "_" + timeStamp + ".xls";
		if (isYesterday) {
			outputFileName = outputName + "_" + TimeStampUtil.getYesterdayYearMonthDay("yyyyMMdd") + ".xls";
		}
		Path outputPath = OUTPUT_DIR_PATH.resolve(TIMESTAMP).resolve(outputFileName);

		//创建工作薄对象
		HSSFWorkbook workbook=new HSSFWorkbook();
		//创建工作表对象
		HSSFSheet sheet = workbook.createSheet();
		//创建工作表的行
		HSSFRow titleRow = sheet.createRow(0);//设置第一行，从零开始
		for(int i = 0 ; i < header.length ; i++){
			titleRow.createCell(i).setCellValue(header[i]);
		}
		for(int i = 1 ; i <= rows.size() ; i++){
			HSSFRow row = sheet.createRow(i);
			Map<String, String> rowMap = rows.get(i-1);
			for (int j = 0; j < header.length; j++) {
				row.createCell(j).setCellValue(rowMap.get(header[j]));
			}
		}

		fileDao.writeOutputFile(outputPath, workbook);

	}


}
