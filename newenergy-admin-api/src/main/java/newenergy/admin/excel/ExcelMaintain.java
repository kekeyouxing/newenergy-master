package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelMaintain {
    ExcelUtil excelUtil = null;

    private void createExcel(){
        HSSFWorkbook workbook = new HSSFWorkbook();
        excelUtil = new ExcelUtil(workbook);
        createHeader();
    }
    public void createHeader(){
        HSSFRow firstRow = excelUtil.createRow(0);
        excelUtil.createCell(firstRow, 0, "工号: ");
        excelUtil.createCell(firstRow, 2, "售后人员: ");
        excelUtil.createCell(firstRow, 6, "制表时间: ");

        CellRangeAddress region=new CellRangeAddress(0, 0, 0, 1);
        excelUtil.addMergedRegion(region);

        CellRangeAddress secondRegion=new CellRangeAddress(0, 0, 2, 5);
        excelUtil.addMergedRegion(secondRegion);

        CellRangeAddress thirdRegion=new CellRangeAddress(0, 0, 6, 7);
        excelUtil.addMergedRegion(thirdRegion);

        String[] headers = new String[]{"序号","登记号","地址","房间号","报修现象",
                "报修时间","响应时间","处理时间"};

        HSSFRow secondRow = excelUtil.createRow(1);
        for(int i=0;i<headers.length; i++){
            excelUtil.setColumnWidth(i, 20 * 256);
            excelUtil.createCell(secondRow, i, headers[i]);
        }

    }
}
