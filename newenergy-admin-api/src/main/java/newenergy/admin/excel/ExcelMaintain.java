package newenergy.admin.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ExcelMaintain {


    ExcelUtil excelUtil = null;
    public void createExcel(String[]  firstLineValue, List<String[]> values){

        HSSFWorkbook workbook = new HSSFWorkbook();
        excelUtil = new ExcelUtil(workbook);

        createHeader();

        createValue(firstLineValue, values);
    }

    private void createValue(String[] firstLineValue, List<String[]> values) {
        HSSFRow thirdRow = excelUtil.createRow(2);
        for(int i=0;i<firstLineValue.length;i++){
            excelUtil.createCell(thirdRow, i, firstLineValue[i]);
        }

        for(int i=0;i<values.size();i++){
            HSSFRow row = excelUtil.createRow(i+4);
            String[]  strings= values.get(i);
            excelUtil.createCell(row,0,strings[0]);
            excelUtil.createCell(row,2,strings[1]);
            excelUtil.createCell(row,3,strings[2]);
            excelUtil.createCell(row,7,strings[3]);
            excelUtil.createCell(row,8,strings[4]);
            excelUtil.createCell(row,9,strings[5]);
            CellRangeAddress firstRegion=new CellRangeAddress(i+4, i+4, 0, 1);
            excelUtil.addMergedRegion(firstRegion);

            CellRangeAddress secondRegion=new CellRangeAddress(i+4, i+4, 3, 6);
            excelUtil.addMergedRegion(secondRegion);
        }
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
        excelUtil.createCell(forthRow,0, "报修时间");
        excelUtil.createCell(forthRow,2, "报修现象");
        excelUtil.createCell(forthRow,3, "处理过程");
        excelUtil.createCell(forthRow,7, "处理时间");
        excelUtil.createCell(forthRow,8, "售后人员");
        excelUtil.createCell(forthRow,9, "备注");
        CellRangeAddress firstRegion=new CellRangeAddress(3, 3, 0, 1);
        excelUtil.addMergedRegion(firstRegion);
        CellRangeAddress secondRegion=new CellRangeAddress(3, 3, 3, 6);
        excelUtil.addMergedRegion(secondRegion);
    }
    public void exportExcel(String fileName, HttpServletResponse response){
        excelUtil.exportExcel(fileName, response);
    }
}
