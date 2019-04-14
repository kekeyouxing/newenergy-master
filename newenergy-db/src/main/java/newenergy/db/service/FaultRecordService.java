package newenergy.db.service;

import newenergy.db.domain.*;
import newenergy.db.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Service
public class FaultRecordService {
    @Autowired
    private FaultRecordRepository repository;
    @Autowired
    private ResidentRepository residentRepository;
    @Autowired
    private CorrAddressRepository corrAddressRepository;
    @Autowired
    private CorrPlotAdminRepository corrPlotAdminRepository;
    @Autowired
    private CorrTypeRepository corrTypeRepository;
    /**
     * 默认质保期1年
     */
    public final Integer warranty = 1;

    public List<FaultRecord> getRecordsByRegisterId(String registerId){
        return repository.findAllByRegisterId(registerId);
    }

    public Resident getResident(String registerId){
        return residentRepository.findFirstByRegisterIdAndSafeDelete(registerId,0);
    }
    public CorrAddress getCorrAddress(String addressNum){
        return corrAddressRepository.findByAddressNumAndSafeDelete(addressNum,0);
    }
    public CorrPlotAdmin getCorrPlotAdmin(String plotNum){
        return corrPlotAdminRepository.findFirstByPlotNumAndSafeDelete(plotNum,0);
    }
    public CorrType getCorrType(String typeNum){
        return corrTypeRepository.findFirstByTypeNumAndSafeDelete(typeNum,0);
    }

    public Page<FaultRecord> findBySpecificate(Specification<FaultRecord> specification){
        return findBySpecificate(specification,null,null);
    }

    public Page<FaultRecord> findBySpecificate(Specification<FaultRecord> specification, Pageable pageable){
        return findBySpecificate(specification,pageable,null);
    }

    public Page<FaultRecord> findBySpecificate(Specification<FaultRecord> specification, Pageable pageable, Sort sort){
        Sort newSort = null;
        Pageable newPageable = null;
        if(pageable == null){
            newSort = sort==null?Sort.by(Sort.Direction.ASC, "id"):sort;
            newPageable = PageRequest.of(0,(int)repository.count(),newSort);
        }else{
            if(sort != null){
                newPageable = PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort);
            }else{
                newPageable = pageable;
            }
        }
        return repository.findAll(specification,newPageable);
    }

    /**
     * TODO: 生成Specification
     *
     */


    public List<FaultRecord> getRecordsByMonitorId(Integer id){
        return repository.findAllByMonitorId(id);
    }
    public List<FaultRecord> getRecordsByServiverId(Integer id){
        return repository.findAllByServicerId(id);
    }
    public List<FaultRecord> getRecordsByState(Integer state){
        return repository.findAllByState(state);
    }
    public List<FaultRecord> getRecordsByResult(Integer result){
        return repository.findAllByResult(result);
    }
    public FaultRecord addRecord(FaultRecord record){
        return repository.save(record);
    }
}
