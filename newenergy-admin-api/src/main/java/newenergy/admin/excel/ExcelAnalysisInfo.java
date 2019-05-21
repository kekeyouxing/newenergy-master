package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class ExcelAnalysisInfo {

    ExcelUtil excelUtil = null;

    public void createExcel(String[] firstRowValue, String[] secondRowValue, List<String[]> values){
        HSSFWorkbook workbook = new HSSFWorkbook();

        excelUtil = new ExcelUtil(workbook);

        createHeader(firstRowValue, secondRowValue);

        createValue(values);
    }

    private void createHeader(String[] firstRowValue, String[] secondRowValue) {
        HSSFRow firstRow = excelUtil.createRow(0);
        excelUtil.createCell(firstRow, 0, firstRowValue[0]);

        CellRangeAddress region=new CellRangeAddress(0, 0, 0, secondRowValue.length-2);
        excelUtil.addMergedRegion(region);

        excelUtil.createCell(firstRow,secondRowValue.length-1,firstRowValue[1]);

        HSSFRow secondRow = excelUtil.createRow(1);
        for(int i=0;i<secondRowValue.length; i++){
            excelUtil.setColumnWidth(i, 20 * 256);
            excelUtil.createCell(secondRow, i, secondRowValue[i]);
        }

    }

    private void createValue(List<String[]> values) {
        for(int i=0;i<values.size(); i++){
            HSSFRow row = excelUtil.createRow(i+2);
            excelUtil.createCell(row,0,(i+1)+"");
            String[] cols = values.get(i);
            for(int j = 0; j<cols.length;j++){
                excelUtil.createCell(row,j+1,cols[j]);
            }
        }
    }
    public void exportExcel(String fileName, HttpServletResponse response){
        excelUtil.exportExcel(fileName, response);
    }
}
