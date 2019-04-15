package newenergy.admin.util;

public class GetNumCode {
    //生成一位编码0-z
    public String getOneNum(Integer num) {
        char code = ' ';
        if(num>=0 && num<36) {
            if(num<10) {
                code = (char)(num+48);
            }
            else {
                code = (char)('a'+ (num-10));
            }
        }
        else {
            System.out.println("超出一位编码范围");
        }
        return String.valueOf(code);
    }


    //生成两位编码00-zz（相当于36进制表示）
    public String getTwoNum(Integer num) {
        char[] code = new char[2];
        if(num>=0 && num<1296) {
            int ten = num/36;
            int digit = num%36;
            code[0] = getOneNum(ten).charAt(0);
            code[1] = getOneNum(digit).charAt(0);
        }
        else {
            System.out.println("超出两位编码范围");
        }
        return String.valueOf(code);
    }

    //生成地址编号
    public String getAddressNum(String plot_num, Integer block, Integer unit) {
        String block_num = getTwoNum(block - 1);
        String unit_num = getOneNum(unit-1);
        String address_num = plot_num + block_num + unit_num;
        return address_num;
    }

    public static void main(String[] args) {
        GetNumCode getNumCode = new GetNumCode();
        System.out.println(getNumCode.getTwoNum(1295));
    }
}
