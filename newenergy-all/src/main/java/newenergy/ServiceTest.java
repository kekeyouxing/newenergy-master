package newenergy;

import newenergy.db.domain.DeviceRequire;
import newenergy.db.predicate.DeviceRequirePredicate;
import newenergy.admin.background.service.DeviceRequireService;
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
}
