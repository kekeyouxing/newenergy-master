package newenergy.admin.controller;

import newenergy.admin.background.service.StorageService;
import newenergy.db.constant.AdminConstant;
import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.predicate.AdminPredicate;
import newenergy.db.service.BackupService;
import newenergy.db.service.NewenergyAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
public class TestController {

}
