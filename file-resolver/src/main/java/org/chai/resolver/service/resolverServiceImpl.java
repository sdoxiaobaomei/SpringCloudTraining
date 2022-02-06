package org.chai.resolver.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.chai.resolver.TimeStampUtil;
import org.chai.resolver.dao.FileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class resolverServiceImpl implements resolverService{
	@Autowired
	FileDao fileDao;
	private Boolean isYesterday;

	@Override
	public void resolveOrder(Boolean isYesterday, List<File> inputFileList) {
		this.isYesterday = isYesterday;
		List<Map<String, String>> result = new ArrayList<>();
		for (File file : inputFileList) {
			String fileName = file.getName();
			String inputFilePathStr = file.getAbsolutePath();
			result = fileDao.readInputFile(file);
		}
		//创建输出文件夹
		resolveFilter(result);
		try {
			moveInputFileToBackup();
		} catch (IOException e) {
			System.out.println("移动文件失败：" + e.getLocalizedMessage());
		}
	}
	private void resolveFilter(List<Map<String, String>> result) {
		filterRemove(result, "订单状态", "已失效", "已失效");
		filterOutput(result, "推广位名称", "博观达人", "804博观达人", "博观定金达人", "博观达人rose");
		filterOutput(result, "推广位名称", "客户自己的达人", "KS达人", "邦盟达人");
		filterOutput(result, "推广位名称", "德","德");
		filterOutput(result, "推广位名称", "达人", "达人", "618开屏", "729壹者达人测试链接");
//        filterOutput("推广位名称", "冰晶绿_数智", "冰晶绿_数智");
		filterOutput(result, "推广位名称","1组","壹者", "418薇亚");
		filterOutput(result, "推广位名称", "橙子","橙子");
	}

	private void filterOutput(List<Map<String, String>> result, String columnName, String outputName, String... keyWords) {
		if (keyWords == null) {
			return;
		}
		List<Map<String, String>> outputRows = new ArrayList<>();
		for (String keyWord : keyWords) {
			List<Map<String, String>> filterRows = result.stream().filter(Objects::nonNull).filter(row -> Optional.ofNullable(row.get(columnName)).orElse("").trim().contains(keyWord)).collect(Collectors.toList());
			result.removeAll(filterRows);
			outputRows.addAll(filterRows);
		}
		exportXls(outputRows, outputName);
	}

	private void filterRemove(List<Map<String, String>> result, String columnName, String keyWord, String outputName) {
		List<Map<String, String>> removeRows = result.stream().filter(row -> row.get(columnName).contains(keyWord)).collect(Collectors.toList());
		result = result.stream().filter(row -> !row.get(columnName).contains(keyWord)).collect(Collectors.toList());
		exportXls(removeRows, outputName);
	}

	private void moveInputFileToBackup() throws IOException {
		Path backupPath = Paths.get(getBackupDirStr());
		if (Files.isDirectory(backupPath)) {
			Files.createDirectories(backupPath);
		}
		File[] files = new File(getInputDirStr()).listFiles();
		List<Path> inputFilePaths = new ArrayList<>();
		if (files == null || files .length == 0) {
			return;
		}
		for (File file : files) {
			inputFilePaths.add(file.toPath());
		}
		for (Path inputFilePath: inputFilePaths) {
			Path backupFilePath = Paths.get(getBackupDirStr() + "\\" + inputFilePath.getFileName());
			Files.move(inputFilePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void exportXls(List<Map<String, String>> rows, String outputName) {
		String outputFileName = outputName + "_" + TimeStampUtil.getTimeStamp("yyyyMMdd-HHmm") + ".xls";
		if (isYesterday) {
			outputFileName = outputName + "_" + TimeStampUtil.getYesterdayYearMonthDay("yyyyMMdd") + ".xls";
		}
		Path outputPath = OUTPUT_DIR_PATH.resolve(outputFileName);
		//创建工作薄对象
		HSSFWorkbook workbook=new HSSFWorkbook();
		//创建工作表对象
		HSSFSheet sheet = workbook.createSheet();
		//创建工作表的行
		HSSFRow titleRow = sheet.createRow(0);//设置第一行，从零开始
		for(int i = 0 ; i < title.length ; i++){
			titleRow.createCell(i).setCellValue(title[i]);
		}
		for(int i = 1 ; i <= rows.size() ; i++){
			HSSFRow row = sheet.createRow(i);
			Map<String, String> rowMap = rows.get(i-1);
			for (int j = 0; j < title.length; j++) {
				row.createCell(j).setCellValue(rowMap.get(title[j]));
			}
		}

		try {
			if (!Files.isReadable(outputPath)) {
				Files.createFile(outputPath);
			}
		} catch (IOException e) {
			System.out.println("输出文件创建失败 " + e.getCause());
		}
		try (FileOutputStream out = new FileOutputStream(outputPath.toString())){
			System.out.println("开始输出文件：" + outputPath.getFileName());
			workbook.write(out);
			System.out.println("输出文件结束！");
		} catch (FileNotFoundException e) {
			System.out.println("输出文件找不到" + e.getCause());
		} catch (IOException e) {
			System.out.println("输出文件失败" + e.getCause());
		}
	}


}
