package newenergy.admin.util;

import jodd.http.HttpResponse;
import org.apache.poi.hssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ExcelExport {

    HSSFWorkbook workbook = null;

    public ExcelExport(String[] headers, List<String[]> values){
        workbook = createExcel(headers, values);
    }

    private HSSFWorkbook createExcel(String[] headers, List<String[]> values){

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet");

        HSSFRow row = sheet.createRow(0);
        HSSFCell orderCell = row.createCell(0);
        orderCell.setCellValue("序号");
        for(int i = 0; i < headers.length; i++){
            HSSFCell cell = row.createCell(i+1);
            cell.setCellValue(headers[i]);
        }

        if(values != null){
            for(int i = 0; i < values.size(); i++){
                row = sheet.createRow(i+1);
                HSSFCell valueCell = row.createCell(0);
                valueCell.setCellValue((i+1)+"");
                String[] cols = values.get(i);
                for(int j = 0; j<cols.length;j++){
                    row.createCell(j+1).setCellValue(cols[j]);
                }
            }
        }
        return workbook;
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
}
