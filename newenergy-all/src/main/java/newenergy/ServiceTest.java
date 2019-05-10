package newenergy;

import newenergy.db.domain.DeviceRequire;
import newenergy.db.predicate.DeviceRequirePredicate;
import newenergy.admin.background.service.DeviceRequireService;
import newenergy.db.service.NewenergyAdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by HUST Corey on 2019-05-05.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {
    @Autowired
    private DeviceRequireService deviceRequireService;
    @Autowired
    private NewenergyAdminService newenergyAdminService;

    @Test
    public void test1() {
        DeviceRequirePredicate predicate = new DeviceRequirePredicate();
        predicate.setPlotDtl("测试一号");
        Page<DeviceRequire> res = deviceRequireService.findByPredicateWithAive(predicate,null,null);
        res.forEach(e-> System.out.println(e.getPlotNum()));
        predicate.setPlotDtl("测试");
        Page<DeviceRequire> res2 = deviceRequireService.findByPredicateWithAive(predicate,null,null);
        res2.forEach(e-> System.out.println(e.getPlotNum()));
    }
    @Test
    public void test2(){
        //expected : true
        System.out.println(
                newenergyAdminService.contains(
                        new Integer[]{1,2,3},
                        new Integer[]{1,2,3}
                )
        );
        //expected : false
        System.out.println(
                newenergyAdminService.contains(
                        new Integer[]{1,2},
                        new Integer[]{1,2,3}
                )
        );
        //expected : true
        System.out.println(
                newenergyAdminService.contains(
                        new Integer[]{1,2,3},
                        new Integer[]{1,2}
                )
        );
        //expected : false
        System.out.println(
                newenergyAdminService.contains(
                        new Integer[]{1,2,3},
                        new Integer[]{4,5,6}
                )
        );
        //expected : false
        System.out.println(
                newenergyAdminService.contains(
                        new Integer[]{1,2,3},
                        new Integer[]{3,4,5}
                )
        );
    }
}
