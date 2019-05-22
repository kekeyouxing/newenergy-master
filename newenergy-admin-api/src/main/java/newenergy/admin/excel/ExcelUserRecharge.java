package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ExcelUserRecharge {
    ExcelUtil excelUtil = null;

    public void createExcel(String[] firstLineValue, String[] secondLineValue, List<String[]> values){
        HSSFWorkbook workbook = new HSSFWorkbook();
        excelUtil = new ExcelUtil(workbook);
        createHeader();
        createValues(firstLineValue, secondLineValue, values);
    }

    private void createValues(String[] firstLineValue, String[] secondLineValue, List<String[]> values) {
        HSSFRow thirdRow = excelUtil.createRow(2);
        for(int i=0;i<firstLineValue.length;i++){
            excelUtil.createCell(thirdRow,i, firstLineValue[i]);
        }
        HSSFRow fifthRow = excelUtil.createRow(4);
        excelUtil.createCell(fifthRow,0, secondLineValue[0]);
        excelUtil.createCell(fifthRow,1, secondLineValue[1]);
        CellRangeAddress secondRegion=new CellRangeAddress(4, 4, 1, 3);
        excelUtil.addMergedRegion(secondRegion);
        excelUtil.createCell(fifthRow,4, secondLineValue[2]);
        excelUtil.createCell(fifthRow,5, secondLineValue[3]);

        for(int i = 0; i<values.size() ;i++){
            HSSFRow row = excelUtil.createRow(i+6);
            String[] cols = values.get(i);
            for(int j = 0; j<cols.length;j++){
                excelUtil.createCell(row,j,cols[j]);
            }
        }
    }

    private void createHeader() {
        HSSFRow firstRow = excelUtil.createRow(0);
        excelUtil.createCell(firstRow,0, "用户充值记录明细表");

        CellRangeAddress region=new CellRangeAddress(0, 0, 0, 5);
        excelUtil.addMergedRegion(region);

        HSSFRow secondRow = excelUtil.createRow(1);
        String[] secondHead= new String[]{"登记号","用户姓名","联系电话","安装类型","额定流量","小区名称"};
        for(int i=0;i<secondHead.length;i++){
            excelUtil.createCell(secondRow, i, secondHead[i]);
        }

        HSSFRow forthRow = excelUtil.createRow(3);
        excelUtil.createCell(forthRow,0, "机器编码");

        excelUtil.createCell(forthRow,1, "装机地址");
        CellRangeAddress secondRegion=new CellRangeAddress(3, 3, 1, 3);
        excelUtil.addMergedRegion(secondRegion);

        excelUtil.createCell(forthRow,4, "房间号");
        excelUtil.createCell(forthRow,5, "单位(元/吨)");

        String[] sixthHead= new String[]{"充值时间","充值金额","充值流量","剩余流量","可用流量","备注"};
        HSSFRow sixthRow = excelUtil.createRow(5);
        for(int i=0;i<sixthHead.length;i++){
            excelUtil.createCell(sixthRow, i, sixthHead[i]);
        }
    }
    public void exportExcel(String fileName, HttpServletResponse response){
        excelUtil.exportExcel(fileName, response);
    }
}
