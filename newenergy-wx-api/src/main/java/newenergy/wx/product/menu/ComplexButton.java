package newenergy.wx.product.menu;

/**
 * 复合类型的按钮
 *
 * @author yangq
 * @date 2019-04-15
 */
public class ComplexButton extends Button{
    private Button[] sub_button;

    public Button[] getSub_button() {
        return sub_button;
    }

    public void setSub_button(Button[] subButton) {
        this.sub_button = subButton;
    }
}
