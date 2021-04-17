package org.chai.resolver;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private ExcelUtil() {
    }

    public static ExcelFileContent readExcel(File file) {
        ExcelFileContent excelFileContent = new ExcelFileContent();
        String fileName = file.getName();
        String inputFilePathStr = file.getAbsolutePath();
        List<Map<String, String>> content = new ArrayList<>();
        try {
            if (fileName.contains("xlsx")) {
                content = readXlsx(excelFileContent);
            }else if (fileName.contains("xls")){
                content = readXls(excelFileContent);
            }else if (fileName.contains("csv")) {
                content = readCsv(excelFileContent);
            }
        } catch (Exception e) {
            System.out.println("读取["+fileName+"]出错：" + e.getCause());
        }
        excelFileContent.setContent(content);
        return excelFileContent;
    }

    private static List<Map<String, String>> readCsv(ExcelFileContent excel) throws IOException {
        InputStream is = new FileInputStream(path);
        List<Map<String, String>> content = new ArrayList<>();
        CsvReader csvReader = new CsvReader(is, StandardCharsets.UTF_8);
        csvReader.readHeaders();
        String[] headers = csvReader.getHeaders();
        String[] title = headers.clone();
        title[0] = title[0].replaceAll("\ufeff", "");
        while (csvReader.readRecord()) {
            Map<String, String> map = new HashMap<>();
            for (String key : headers) {
                String value = csvReader.get(key);
                key = key.replaceAll("\ufeff", "");
                key = key.replaceAll("\uFEFF", "");
                map.put(key, value);
            }
            content.add(map);
        }
        csvReader.close();
        return content;
    }

    private static List<Map<String, String>> readXlsx(ExcelFileContent excel) throws IOException {
        InputStream is = new FileInputStream(path);
        List<Map<String, String>> content = new ArrayList<>();
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
                content.add(m);
            }
        }
        return content;
    }

    private static List<Map<String, String>> readXls(ExcelFileContent excel) throws Exception {
        InputStream is = new FileInputStream(path);
        List<Map<String, String>> content = new ArrayList<>();
        // HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        int size = hssfWorkbook.getNumberOfSheets();
        if (size == 1) {
            HSSFRow row = null;
            HSSFCell cell = null;
            String[] title = null;
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
                content.add(m);
            }
        }
        return content;
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
