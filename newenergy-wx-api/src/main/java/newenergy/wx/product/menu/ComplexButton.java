package newenergy.wx.product.menu;

/**
 * 复合类型的按钮
 *
 * @author yangq
 * @date 2019-04-15
 */
public class ComplexButton extends Button{
    private Button[] subButton;

    public Button[] getSubButton() {
        return subButton;
    }

    public void setSubButton(Button[] subButton) {
        this.subButton = subButton;
    }
}
