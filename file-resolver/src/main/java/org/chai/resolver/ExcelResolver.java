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
import org.chai.resolver.filesys.Backup;
import org.chai.resolver.filesys.Input;
import org.chai.resolver.filesys.Output;
import org.chai.resolver.filesys.Template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelResolver {


    private List<Map<String, String>> result = new ArrayList<>();
    private final static String TIMESTAMP = TimeStampUtil.getTimeStamp();
    private final static Path OUTPUT_DIR_PATH = Paths.get("output/", TIMESTAMP);
    private String[] title = {""};
    private String codeDirStr = "";
    private String inputDirStr = "";
    private String backupDirStr = "";
    private String templateDirStr = "";

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        String executeDirStr = args[0];
//        String executeDirStr= "D:\\菜轻松\\菜轻松1.0";
//        Input input = new Input();
//        Output output = new Output();
//        Template template = new Template();
//        Backup backup = new Backup();
        ExcelResolver excelResolver = new ExcelResolver();
        excelResolver.setInputDirStr(executeDirStr + "\\input");
        excelResolver.setCodeDirStr(executeDirStr + "\\code");
        excelResolver.setBackupDirStr(executeDirStr + "\\backup");
        excelResolver.setTemplateDirStr(executeDirStr + "\\template");
        File[] files = new File(excelResolver.getInputDirStr()).listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            String inputFilePathStr = file.getAbsolutePath();
            if (fileName.contains("xlsx")) {
                try {
                    excelResolver.readXlsx(inputFilePathStr);
                } catch (Exception e) {
                    System.out.println("读取["+fileName+"]出错：" + e.getCause());
                }
            }else if (fileName.contains("xls")){
                try {
                    excelResolver.readXls(inputFilePathStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (fileName.contains("csv")) {
                try {
                    excelResolver.readCsv(inputFilePathStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                continue;
            }
            try {
                if (!Files.isDirectory(OUTPUT_DIR_PATH)) {
                    Files.createDirectories(OUTPUT_DIR_PATH);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //创建输出文件夹
        excelResolver.resolveFilter();
        try {
            excelResolver.moveInputFileToBackup();
        } catch (IOException e) {
            System.out.println("移动文件失败：" + e.getLocalizedMessage());
        }
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

    private void readTemplate() {
        try {
            File[] files = new File(getTemplateDirStr()).listFiles();
            InputStream is = new FileInputStream(getTemplateDirStr());
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFCell cell;
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = sheet.getRow(0);
            if (row != null) {
                title = new String[row.getLastCellNum()];
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    title[y] = getStringVal(cell);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resolveFilter() {
        filterRemove("订单状态", "已失效", "已失效");
        filterOutput("推广位名称", "2组","壹者二组", "崔", "直营");
        filterOutput("推广位名称", "嗖扬达人", "SY达人");
        filterOutput("推广位名称", "博观达人", "804博观达人");
        filterOutput("推广位名称", "德","德");
        filterOutput("推广位名称", "达人", "达人", "618开屏", "729壹者达人测试链接");
        filterOutput("推广位名称", "冰晶绿_数智", "冰晶绿_数智");
        filterOutput("推广位名称","1组","壹者", "418薇亚");
        filterOutput("推广位名称","邦盟","邦盟信息流");
    }

    private void filterOutput(String columnName, String outputName, String... keyWords) {
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

    private void filterRemove(String columnName, String keyWord, String outputName) {
        List<Map<String, String>> removeRows = result.stream().filter(row -> row.get(columnName).contains(keyWord)).collect(Collectors.toList());
        result = result.stream().filter(row -> !row.get(columnName).contains(keyWord)).collect(Collectors.toList());
        exportXls(removeRows, outputName);
    }

    private void exportXls(List<Map<String, String>> rows, String outputName) {
        Path outputPath = OUTPUT_DIR_PATH.resolve(outputName + "_" + TimeStampUtil.getTimeStamp() + ".xls");
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

    private void readCsv(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        CsvReader csvReader = new CsvReader(is, StandardCharsets.UTF_8);
        csvReader.readHeaders();
        String[] headers = csvReader.getHeaders();
        title = headers.clone();
        title[0] = title[0].replaceAll("\ufeff", "");
        while (csvReader.readRecord()) {
            Map<String, String> map = new HashMap<>();
            for (String key : headers) {
                String value = csvReader.get(key);
                key = key.replaceAll("\ufeff", "");
                key = key.replaceAll("\uFEFF", "");
                map.put(key, value);
            }
            result.add(map);
        }
        csvReader.close();
    }

    private void readXlsx(String path) throws IOException {
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
                result.add(m);
            }
        }
    }

    private void readXls(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        // HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
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
                result.add(m);
            }
        }
    }

    public static String getStringVal(CellBase cell) {
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

    public List<Map<String, String>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, String>> result) {
        this.result = result;
    }

    public String getCodeDirStr() {
        return codeDirStr;
    }

    public void setCodeDirStr(String codeDirStr) {
        this.codeDirStr = codeDirStr;
    }

    public String getInputDirStr() {
        return inputDirStr;
    }

    public void setInputDirStr(String inputDirStr) {
        this.inputDirStr = inputDirStr;
    }

    public String[] getTitle() {
        return title;
    }

    public void setTitle(String[] title) {
        this.title = title;
    }

    public String getBackupDirStr() {
        return backupDirStr;
    }

    public void setBackupDirStr(String backupDirStr) {
        this.backupDirStr = backupDirStr;
    }

    public String getTemplateDirStr() {
        return templateDirStr;
    }

    public void setTemplateDirStr(String templateDirStr) {
        this.templateDirStr = templateDirStr;
    }
}
