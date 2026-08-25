package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tekclover.wms.api.idmaster.model.interimbarcode.FindInterimBarcode;
import com.tekclover.wms.api.idmaster.repository.Specification.InterimBarcodeSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.interimbarcode.AddInterimBarcode;
import com.tekclover.wms.api.idmaster.model.interimbarcode.InterimBarcode;
import com.tekclover.wms.api.idmaster.repository.InterimBarcodeRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InterimBarcodeService {

    @Autowired
    private InterimBarcodeRepository interimBarcodeRepository;

    /**
     * getCompanies
     *
     * @return
     */
    public List<InterimBarcode> getAll() {
        List<InterimBarcode> interimBarcodeList = interimBarcodeRepository.findAll();
        interimBarcodeList = interimBarcodeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return interimBarcodeList;
    }

    /**
     * getInterimBarcode
     *
     * @param barcode
     * @return
     */
    public InterimBarcode getInterimBarcode(String storageBin, String itemCode, String barcode) {
        Optional<InterimBarcode> dbInterimBarcode =
                interimBarcodeRepository.findByStorageBinAndItemCodeAndBarcodeAndDeletionIndicator(storageBin, itemCode, barcode, 0L);
        if (dbInterimBarcode.isEmpty()) {
            throw new BadRequestException("Record not found : " + storageBin + "," + itemCode + "," + barcode);
        }

        return dbInterimBarcode.get();
    }

    /**
     * createInterimBarcode
     *
     * @param newInterimBarcode
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InterimBarcode createInterimBarcode(AddInterimBarcode newInterimBarcode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        Optional<InterimBarcode> dbInterimBarcodeDuplicateCheck =
                interimBarcodeRepository.findByItemCodeAndReferenceField1(
                        newInterimBarcode.getItemCode(),
                        newInterimBarcode.getReferenceField1());

        if (!dbInterimBarcodeDuplicateCheck.isEmpty()) {

            throw new BadRequestException(newInterimBarcode.getItemCode() + " Item Code was Scanned already !");

        } else {

            InterimBarcode dbInterimBarcode = new InterimBarcode();
            BeanUtils.copyProperties(newInterimBarcode, dbInterimBarcode, CommonUtils.getNullPropertyNames(newInterimBarcode));
            dbInterimBarcode.setDeletionIndicator(0L);
            dbInterimBarcode.setCreatedBy(loginUserID);
            dbInterimBarcode.setUpdatedBy(loginUserID);
            dbInterimBarcode.setCreatedOn(new Date());
            dbInterimBarcode.setUpdatedOn(new Date());
            return interimBarcodeRepository.save(dbInterimBarcode);
        }
    }

    //Find InterimBarcode
    public List<InterimBarcode> findInterimBarcode(FindInterimBarcode findInterimBarcode) throws ParseException {

        InterimBarcodeSpecification spec = new InterimBarcodeSpecification(findInterimBarcode);
        List<InterimBarcode> results = interimBarcodeRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        return results;
    }
}
