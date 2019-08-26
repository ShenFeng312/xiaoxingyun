import org.apache.poi.hssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author 沈锋
 * @date 2019/8/24
 */
public class ExcelUtils {
    /**
     *
     * @methodName:getHSSFWorkbook
     * * @param sheetName
     * * @param title 表头
     * * @param values 表格内容
     * * @param wb
     * * @return
     * @return HSSFWorkbook
     * @author gw
     * @date 2017年3月10日上午11:19:20
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String []title, List<List<String>>values, HSSFWorkbook wb) throws FileNotFoundException {
        //第一步，创建一个webbook,对应一个Excel文件
        String deptName=values.get(0).get(0);
        OutputStream out = new FileOutputStream(Main.OUTPUT_PATH+deptName+".xls");
        if(wb == null){
            wb = new HSSFWorkbook();
        }
        //第二步，在webbook中添加一个sheet,对应excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        //第三步，在sheet中添加表头第0行
        HSSFRow row = sheet.createRow(0);
        //第四步，创建单元格，并设置表头，设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        HSSFCell cell = null;

        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for(int i=0;i<values.size();i++){
            row = sheet.createRow(i+1);
            for(int j=0;j<values.get(i).size();j++){
                row.createCell(j).setCellValue(values.get(i).get(j));
            }
        }
        try {
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wb;

    }

}
