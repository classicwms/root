package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.impl.EfficiencyRecordImpl;
import com.ustorage.api.trans.model.reports.EfficiencyRecord;
import com.ustorage.api.trans.model.reports.RentCalculationInput;
import com.ustorage.api.trans.util.CommonUtils;
import com.ustorage.api.trans.util.DateUtils;
import com.ustorage.api.trans.util.Rent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RentCalculationService {

    public Rent createRentCalculation(RentCalculationInput rentCalculationInput) throws ParseException {


        Rent data = CommonUtils.calculateRent(rentCalculationInput.getPeriod(),
                rentCalculationInput.getStartDate(),
                rentCalculationInput.getEndDate(),
                rentCalculationInput.getRent());
        return data;
    }


}
