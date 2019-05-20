package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ExcelUtil {

    private HSSFCellStyle style = null;
    private HSSFWorkbook workbook = null;
    private HSSFSheet sheet = null;
    public ExcelUtil(HSSFWorkbook workbook){
        this.workbook = workbook;
        this.sheet = workbook.createSheet("sheet");
    }
    public HSSFCell createCell(HSSFRow row, int col, String cellValue){
        HSSFCell cell = row.createCell(col);
        cell.setCellValue(cellValue);
        cell.setCellStyle(createCellStyle());
        return cell;
    }

    private HSSFCellStyle createCellStyle(){
        if(style==null){
            style=workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
        }
        return style;
    }


    public void exportExcel(String fileName, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream;charset=utf-8");
        try {
            fileName = new String(fileName.getBytes("GBK"), "ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xls");

        try {
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HSSFRow createRow(int index){
        return sheet.createRow(index);
    }

    public void addMergedRegion(CellRangeAddress region){
        sheet.addMergedRegion(region);
    }

    public void setColumnWidth(int columnIndex, int width){
        sheet.setColumnWidth(columnIndex, width);
    }
}
