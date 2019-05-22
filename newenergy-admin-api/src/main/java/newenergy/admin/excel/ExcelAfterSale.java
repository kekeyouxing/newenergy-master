package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ExcelAfterSale {
    ExcelUtil excelUtil = null;
    private String time;
    private String servicerId;
    private String servicerName;

    public void createExcel(List<String[]> values){
        HSSFWorkbook workbook = new HSSFWorkbook();
        excelUtil = new ExcelUtil(workbook);
        createHeader();
        createValues(values);
    }

    private void createValues(List<String[]> values) {
        if(values != null){
            for(int i = 0; i < values.size(); i++){
                HSSFRow row = excelUtil.createRow(i+2);
                excelUtil.createCell(row,0,(i+1)+"");
                String[] cols = values.get(i);
                for(int j = 0; j<cols.length;j++){
                    excelUtil.createCell(row,j+1,cols[j]);
                }
            }
        }
    }

    public void createHeader(){
        HSSFRow firstRow = excelUtil.createRow(0);
        excelUtil.createCell(firstRow, 0, "工号: "+servicerId);
        excelUtil.createCell(firstRow, 2, "售后人员: "+servicerName);
        excelUtil.createCell(firstRow, 6, "制表时间: "+time);

        CellRangeAddress region=new CellRangeAddress(0, 0, 0, 1);
        excelUtil.addMergedRegion(region);

        CellRangeAddress secondRegion=new CellRangeAddress(0, 0, 2, 5);
        excelUtil.addMergedRegion(secondRegion);

        CellRangeAddress thirdRegion=new CellRangeAddress(0, 0, 6, 9);
        excelUtil.addMergedRegion(thirdRegion);

        String[] headers = new String[]{"序号","登记号","地址","房间号","报修现象",
                "报修时间","响应时间","处理时间","处理经过", "状态"};

        HSSFRow secondRow = excelUtil.createRow(1);
        for(int i=0;i<headers.length; i++){
            excelUtil.setColumnWidth(i, 20 * 256);
            excelUtil.createCell(secondRow, i, headers[i]);
        }

    }

    public void exportExcel(String fileName, HttpServletResponse response){
        excelUtil.exportExcel(fileName, response);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setServicerId(String servicerId) {
        this.servicerId = servicerId;
    }

    public void setServicerName(String servicerName) {
        this.servicerName = servicerName;
    }
}
