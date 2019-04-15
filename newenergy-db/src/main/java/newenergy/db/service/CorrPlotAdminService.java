package newenergy.db.service;

import newenergy.db.repository.CorrPlotAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HUST Corey on 2019-04-11.
 */
@Service
public class CorrPlotAdminService {
    @Autowired
    private CorrPlotAdminRepository repository;

}
