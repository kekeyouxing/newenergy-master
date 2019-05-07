package newenergy.admin.background.communicate.constant;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class StateShift {
    public static final Integer COLDMOD = 15;
    public static final Integer STARTED = 14;
    public static final Integer FAULT_START = 0;
    public static final Integer FAULT_END = 13;
    /**
     * 0011 1111 1111 1111
     */
    public static final Integer FAULT_MASK = 16383;
}
