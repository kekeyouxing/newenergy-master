package newenergy.db.global;

import newenergy.db.constant.DeviceRequireConstant;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by HUST Corey on 2019-04-17.
 */
public class DeviceRequireGlobal {
    public static AtomicInteger updateLoop = new AtomicInteger(DeviceRequireConstant.DEFAULT_LOOP);
}
