package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelAfterSales {


    ExcelUtil excelUtil = null;
    public void createExcel(){

        HSSFWorkbook workbook = new HSSFWorkbook();
        excelUtil = new ExcelUtil(workbook);

        createHeader();
    }

    private void createHeader() {
        HSSFRow firstRow = excelUtil.createRow(0);
        excelUtil.createCell(firstRow,0, "用户售后记录明细表");

        CellRangeAddress region=new CellRangeAddress(0, 0, 0, 9);
        excelUtil.addMergedRegion(region);

        HSSFRow secondRow = excelUtil.createRow(1);
        String[] secondHead= new String[]{"登记号","用户姓名","装机地址","房间号","联系电话","安装机型",
                "竣工验收日期", "质保期", "质保状态","小区名称"};
        for(int i=0;i<secondHead.length;i++){
            excelUtil.createCell(secondRow, i, secondHead[i]);
        }

        HSSFRow forthRow = excelUtil.createRow(3);
        excelUtil.createCell(forthRow,0, "保修时间");
        excelUtil.createCell(forthRow,1, "报修现象");
        excelUtil.createCell(forthRow,2, "处理过程");
        excelUtil.createCell(forthRow,7, "处理时间");
        excelUtil.createCell(forthRow,8, "售后人员");
        excelUtil.createCell(forthRow,9, "备注");
        CellRangeAddress secondRegion=new CellRangeAddress(3, 3, 2, 6);
        excelUtil.addMergedRegion(secondRegion);
    }
}
