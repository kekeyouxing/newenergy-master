package newenergy.db.service;

import newenergy.db.domain.NewenergyOrder;
import newenergy.db.repository.NewenergyOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
public class NewenergyOrderService {
    @Autowired
    private NewenergyOrderRepository newenergyOrderRepository;

    public String findByRegisterId(String register_id){
        return newenergyOrderRepository.findByRegisterId(register_id);
    }
    public Double findByPlotNum(String plot_num){
        return newenergyOrderRepository.findPlotFactorByPlotNum(plot_num);
    }
    public NewenergyOrder add(NewenergyOrder order){
        order.setRecharge_time(LocalDateTime.now());
        order.setState(0);
        return newenergyOrderRepository.save(order);
    }

    //TODO 这里生成一个唯一的商户订单号，但仍有两个订单相同的可能性
    public String generateOrderSn(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        int hashCodev = UUID.randomUUID().toString().hashCode();
        if (hashCodev < 0){
            hashCodev =- hashCodev;
        }
        return "pk"+now+String.format("%012d",hashCodev);
    }
}
