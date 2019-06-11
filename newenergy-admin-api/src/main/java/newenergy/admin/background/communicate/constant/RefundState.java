package newenergy.admin.background.communicate.constant;

/**
 * Created by HUST Corey on 2019-05-21.
 */
public class RefundState {
    public static final Integer SUCCESS = 0;
    public static final Integer FAILED = -1;

    public static final Integer STATE_UNCHECK = 1;
    public static final Integer STATE_CHECK_FAILED = 2;
    public static final Integer STATE_CHECK_UNREFUND = 4;
    public static final Integer STATE_CHECK_REFUND_FAILED = 5;
    public static final Integer STATE_CHECK_REFUND_SUCCESS = 0;
}
