package com.xiaoxinyun.commonutil.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxinyun.commonutil.entity.BaseMessage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static String XLSX = "xlsx";
    public static String XLS = "xls";



    /**
     * 通用的 转换excel 到 json数组
     * "{excel_field:'takeDrugWay',excel_notnull:'true',title:'取药方式（在线配送、到店自取、同时支持、医院自取）',keys:['在线配送','到店自取','同时支持','医院自取'],values:[1,2,3,4],default:'同时支持',charater_size:150}
     * excel_field --> 对应 model中的字段
     * excel_notnull --> true：该字段非空  false或者没有这个字段则可空
     * title --> excel的表头
     * keys --> excel中可选的值   keys和 values 需要同时存在或者同时不存在
     * values --> 对应model中可选的值（实际需要保存的值）
     * default --> 默认值 （keys 中的值）
     * charater_size --> 字符长度限制 该字段最大长度
     *
     * @param excelFile
     * @param columnConfig
     * @param maxRows      最大行数 为空或者为0 则不限制
     * @return
     * @throws Exception
     */
    public static BaseMessage excelToJson(InputStream excelFile, JSONArray columnConfig, Integer maxRows) throws Exception {
        BaseMessage baseMessage = new BaseMessage();
        JSONArray JsonExcel = new JSONArray();

        if (excelFile == null) {
            baseMessage.initStateAndMessage(1001, "文件不能为空");
            return baseMessage;
        }
        if (columnConfig == null || columnConfig.size() == 0) {
            baseMessage.initStateAndMessage(1002, "配置文件有误");
            return baseMessage;
        }

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(excelFile); // 这种方式 Excel 2003/2007/2010 都是可以处理的

            // 获取第一个表格
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows(); // 获取总行数

            if (maxRows != null && maxRows > 0 && rowCount - 1 > maxRows) {
                baseMessage.initStateAndMessage(1003, CommUtil.format("最多允许一次导入{0}条，请删减数据后再导入", maxRows));
                return baseMessage;
            }
            Row headRow = sheet.getRow(0);
            if (headRow == null) {
                baseMessage.initStateAndMessage(1004, "表格为空");
                return baseMessage;
            }
            int cellCount = headRow.getPhysicalNumberOfCells();
            // 封装表头
            List<String> head = new ArrayList<>();
            if (columnConfig.size() > cellCount) {
                baseMessage.initStateAndMessage(1004, "excel表头数量不对");
                return baseMessage;
            }
            for (int i = 0; i < columnConfig.size(); i++) {
                JSONObject colConfig = columnConfig.getJSONObject(i);
                String title = colConfig.getString("title");
                String cellValue = getStringValue(headRow.getCell(i));
                cellValue = cellValue == null ? "" : cellValue.trim();
                //判断表头是否为空
                if (CommUtil.isEmpty(cellValue)) {
                    baseMessage.initStateAndMessage(1008, CommUtil.format("第[{0}]列表头不能为空", i + 1));
                    return baseMessage;
                }
                //判断表头是否和配置中表头相等
                if (!CommUtil.isEquals(title, cellValue)) {
                    baseMessage.initStateAndMessage(1009,
                            CommUtil.format("第[{0}]列表头错误，应该为[{1}]", i + 1, title));
                    return baseMessage;
                }
                head.add(colConfig.getString("excel_field"));
            }
            if (head.size() == 0 || head.size() != cellCount || head.size() != columnConfig.size()) {
                baseMessage.initStateAndMessage(1004, "excel表头与配置的表头名不匹配");
                return baseMessage;
            }

            // 封装所有数据到jsonarray
            for (int r = 1; r < rowCount; r++) {// 遍历每一行
                JSONObject rowData = new JSONObject();
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                //如果前四列都为空 则跳过 防止最后空白行导致出错
                if (CommUtil.isEmpty(getStringValue(row.getCell(0))) && CommUtil.isEmpty(getStringValue(row.getCell(1)))
                        && CommUtil.isEmpty(getStringValue(row.getCell(2))) && CommUtil.isEmpty(getStringValue(row.getCell(3)))) {
                    continue;
                }
                int nullCell = 0;
                for (int c = 0; c < cellCount; c++) {// 遍历每一列
                    JSONObject config = (JSONObject) columnConfig.get(c);
                    String cellValue = getStringValue(row.getCell(c));
                    cellValue = cellValue == null ? "" : cellValue.trim();

                    if (CommUtil.isEmpty(cellValue)) {
                        //判断是否是必填项
                        if (config.get("excel_notnull") != null && config.get("excel_notnull").equals("true")) {
                            baseMessage.initStateAndMessage(1005,
                                    CommUtil.format("第[{0}]行的字段[{1}]是必填项", r, config.getString("title")));
                            return baseMessage;
                        } else if (CommUtil.isNotEmpty(config.getString("default"))) {
                            //如果设置了默认值则 使用默认值
                            cellValue = config.getString("default");
                        }
                        nullCell++;
                    }
                    if (CommUtil.isNotEmpty(cellValue) &&
                            config.get("charater_size") != null && config.getInteger("charater_size") < cellValue.length()) {
                        baseMessage.initStateAndMessage(1006,
                                CommUtil.format("第[{0}]行的字段[{1}]字符数太长", r, config.getString("title")));
                        return baseMessage;
                    }
                    JSONArray keys = config.getJSONArray("keys");
                    JSONArray values = config.getJSONArray("values");
                    if (CommUtil.isNotEmpty(cellValue) && keys != null && keys.size() > 0 &&
                            values != null && values.size() > 0 && keys.size() == values.size()) {
                        boolean ishava = false;
                        for (int i = 0; i < keys.size(); i++) {
                            if (CommUtil.isEquals(keys.getString(i), cellValue)) {
                                cellValue = values.getString(i);
                                ishava = true;
                                break;
                            }
                        }
                        if (!ishava) {

                            baseMessage.initStateAndMessage(1006,
                                    CommUtil.format("第[{0}]行的字段[{1}],内容不正确,应该从{2}中选择一个",
                                            r, config.getString("title"), JSONObject.toJSONString(keys).replace("\"", "")));
                            return baseMessage;
//                            if (config.containsKey("excel_notnull") && config.getString("excel_notnull").equals("true")) {
//                                baseMessage.initStateAndMessage(1006,
//                                        CommUtil.format("第{0}行的{1},内容不正确", r, config.getString("title")));
//                                return baseMessage;
//                            } else if (CommUtil.isNotEmpty(config.getString("default"))) {
//                                //如果设置了默认值则 使用默认值
//                                cellValue = config.getString("default");
//                            }else {
//                                cellValue = null;
//                            }
                        }
                    }
                    rowData.put(head.get(c), cellValue);
                }
                //不是所有列都为空则添加
                if (nullCell != cellCount) {
                    JsonExcel.add(rowData);
                }
            }
            if (JsonExcel.size() == 0) {
                baseMessage.initStateAndMessage(1006, "表格数据为空");
            }
        } catch (Exception ex) {
            logger.error("解析excel异常", ex);
            baseMessage.initErrorMessage(ex);
        } finally {
            workbook.close();
            excelFile.close();
        }
        baseMessage.setData(JsonExcel);

        return baseMessage;
    }

    public static void downExcel(OutputStream os, JSONArray dataList, JSONArray columnConfig) throws Exception {
        downExcel(os, dataList, columnConfig, null);
    }

    /**
     * 数据转excel文件并下载
     *
     * @param ，此类以配置正确为前提
     * @param
     * @param os
     * @param dataList
     * @param excelType
     * @throws Exception
     */
    public static void downExcel(OutputStream os, JSONArray dataList, JSONArray columnConfig, String excelType) throws Exception {
        if (columnConfig == null || os == null) {
            return;
        }
        if (CommUtil.isEmpty(excelType)) {
            excelType = XLSX;
        }
        Workbook wb = null;
        try {
            if (CommUtil.isEquals(excelType, XLS)) {
                wb = new HSSFWorkbook();
            } else {
                wb = new SXSSFWorkbook();
            }
            Sheet sheet = wb.createSheet("Sheet1");// 主表
            Sheet dataSheet = wb.createSheet("DataSheet");// 数据表
            dataSheet.protectSheet("ys");
            wb.setSheetHidden(1, true);
            //Row headRow0 = sheet.createRow(0);
            Row headRow = sheet.createRow(0);
            int k = 0;// excel中的列数
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setWrapText(true);
            CellStyle redStyle = wb.createCellStyle();
            Font redFont = wb.createFont();
            redFont.setColor(IndexedColors.RED.getIndex());
            redFont.setBold(true);
            redStyle.setFont(redFont);
            redStyle.setWrapText(true);
            // 表头的样式
            CellStyle titleStyle = wb.createCellStyle();// 创建样式对象
            titleStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中

            // 设置字体
            Font titleFont = wb.createFont(); // 创建字体对象
            titleFont.setFontHeightInPoints((short) 15); // 设置字体大小
            titleFont.setBold(true);// 设置粗体
            // titleFont.setFontName("黑体"); // 设置为黑体字
            titleStyle.setFont(titleFont);

            // 封装主表
            for (int i = 0; i < columnConfig.size(); i++) {
                JSONObject config = columnConfig.getJSONObject(i);
                if (config.get("excel_export") == null || config.getString("excel_export").equals("true")) {
                    Cell cell = headRow.createCell(k);
                    cell.setCellValue(config.getString("title"));
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(style);
                    sheet.setColumnWidth(k, 17 * 256);// 调整列宽
                    if (config.get("excel_notnull") != null && config.get("excel_notnull").equals("true")) {
                        cell.setCellStyle(redStyle);
                    }
                    //隐藏数据不显示
                    if (config.get("hidden") != null && config.get("hidden").equals("true")) {
                        sheet.setColumnWidth(k, 0);// 调整列宽
                    }
                    k++;
                }
            }
            int i = 0;
            if (dataList != null) {
                for (Object o : dataList) {
                    JSONObject json = (JSONObject) o;
                    Row dataRow = sheet.createRow(i + 1);
                    for (int j = 0; j < k; j++) {
                        Cell cell = dataRow.createCell(j);
                        cell.setCellType(CellType.STRING);
                        JSONObject config = columnConfig.getJSONObject(j);
                        String cellValue = json.getString(config.getString("excel_field"));
                        if (CommUtil.isNotEmpty(cellValue)) {
                            //判断是否是带选项的字段
                            JSONArray keys = config.getJSONArray("keys");
                            JSONArray values = config.getJSONArray("values");
                            if (keys != null && keys.size() > 0 &&
                                    values != null && values.size() > 0 && keys.size() == values.size()) {
                                for (int index = 0; index < values.size(); index++) {
                                    if (CommUtil.isEquals(values.getString(index), cellValue)) {
                                        cellValue = keys.getString(index);
                                        break;
                                    }
                                }
                            }
                            cell.setCellValue(cellValue);

                        } else {
                            cell.setCellValue("");
                        }
                    }
                    i++;
                }
            }

            wb.write(os);
            os.flush();

        } catch (Exception ex) {
            logger.error("导出excel异常", ex);
        } finally {
            if (wb != null) {
                wb.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }


    @SuppressWarnings("deprecation")
    private static String getStringValue(Cell cell) throws Exception {
        if (cell == null) {
            return "";
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int cellType = cell.getCellType();
        String cellValue = null;
        switch (cellType) {
            case Cell.CELL_TYPE_STRING: // 文本
                cellValue = cell.getStringCellValue().trim();
                break;
            case Cell.CELL_TYPE_NUMERIC: // 数字、日期
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = fmt.format(cell.getDateCellValue()); // 日期型
                } else {
                    double a = cell.getNumericCellValue();
                    int b = (int) a;
                    if (a == b) {
                        cellValue = Integer.toString(b);// 整数
                    } else {
                        cellValue = Double.toString(cell.getNumericCellValue()); // 小数
                    }
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN: // 布尔型
                cellValue = Boolean.toString(cell.getBooleanCellValue()).trim();
                break;
            case Cell.CELL_TYPE_BLANK: // 空白
                cellValue = cell.getStringCellValue().trim();
                break;
            case Cell.CELL_TYPE_ERROR: // 错误
                throw new Exception("有非法值导入");
            case Cell.CELL_TYPE_FORMULA: // 公式
                throw new Exception("不支持公式导入");
            default:
                throw new Exception("获取值失败");
        }
        return cellValue;
    }
}
