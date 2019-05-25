package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ExcelCommon {

    ExcelUtil excelUtil = null;
    public void createExcel(String[] headers, List<String[]> values){

        HSSFWorkbook workbook = new HSSFWorkbook();
        excelUtil = new ExcelUtil(workbook);

        createHeader(headers);

        createValue(values);
    }

    private void createHeader(String[] headers) {
        HSSFRow row = excelUtil.createRow(0);
        excelUtil.createCell(row,0, "序号");

        for(int i = 0; i < headers.length; i++){
            excelUtil.setColumnWidth(i, 20 * 256);
            excelUtil.createCell(row, i+1, headers[i]);
        }
    }
    private void createValue(List<String[]> values) {
        if(values != null){
            for(int i = 0; i < values.size(); i++){
                HSSFRow row = excelUtil.createRow(i+1);
                excelUtil.createCell(row,0,(i+1)+"");
                String[] cols = values.get(i);
                for(int j = 0; j<cols.length;j++){
                    excelUtil.createCell(row,j+1,cols[j]);
                }
            }
        }
    }
    public void exportExcel(String fileName, HttpServletResponse response){
        excelUtil.exportExcel(fileName, response);
    }
}
