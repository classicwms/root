package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.idmaster.WarehouseId;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
import com.tekclover.wms.api.masters.model.threepl.billing.FindBilling;
import com.tekclover.wms.api.masters.model.threepl.cbminbound.*;
import com.tekclover.wms.api.masters.repository.CbmInboundRepository;
import com.tekclover.wms.api.masters.repository.ImBasicData1Repository;
import com.tekclover.wms.api.masters.repository.PriceListRepository;
import com.tekclover.wms.api.masters.repository.specification.BillingSpecification;
import com.tekclover.wms.api.masters.repository.specification.CbmInboundSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
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
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CbmInboundService {

    @Autowired
    private ImBasicData1Service imBasicData1Service;

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private IDMasterService idMasterService;
    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private ImBasicData1Repository imBasicData1Repository;
    @Autowired
    private CbmInboundRepository cbmInboundRepository;

    /**
     * getCbmInbounds
     * @return
     */
    public List<CbmInbound> getCbmInbounds () {
        List<CbmInbound>CbmInboundList =  cbmInboundRepository.findAll();
        CbmInboundList = CbmInboundList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        List<CbmInbound> newCbmInbound = new ArrayList<>();
        for (CbmInbound dbCbmInbound : CbmInboundList) {
            if (dbCbmInbound.getCompanyIdAndDescription() != null && dbCbmInbound.getPlantIdAndDescription() != null && dbCbmInbound.getWarehouseIdAndDescription() != null && dbCbmInbound.getItemCodeAndDescription() != null) {

                IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbCbmInbound.getCompanyCodeId(), dbCbmInbound.getLanguageId());
                IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbCbmInbound.getPlantId(), dbCbmInbound.getLanguageId(), dbCbmInbound.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbCbmInbound.getWarehouseId(), dbCbmInbound.getLanguageId(), dbCbmInbound.getCompanyCodeId(), dbCbmInbound.getPlantId());
                IKeyValuePair iKeyValuePair3 = imBasicData1Repository.getItemCodeAndDescription(dbCbmInbound.getWarehouseId(), dbCbmInbound.getLanguageId(), dbCbmInbound.getItemCode(), dbCbmInbound.getCompanyCodeId(), dbCbmInbound.getPlantId(),dbCbmInbound.getUomId());
                if (iKeyValuePair != null) {
                    dbCbmInbound.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbCbmInbound.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbCbmInbound.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbCbmInbound.setItemCodeAndDescription(iKeyValuePair3.getItemCode() + "-" + iKeyValuePair3.getDescription());
                }
                newCbmInbound.add(dbCbmInbound);
            }
        }
        return newCbmInbound;
    }

    /**
     * getCbmInbound
     * @param itemCode
     * @return
     */
    public CbmInbound getCbmInbound (String warehouseId,String itemCode,String companyCodeId,String languageId,String plantId) {
        Optional<CbmInbound> dbCbmInbound =
                cbmInboundRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        itemCode,
                        languageId,
                        0L
                );
        if (dbCbmInbound.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "itemCode - " + itemCode +
                    " doesn't exist.");
        }
        CbmInbound newCbmInbound=new CbmInbound();
        BeanUtils.copyProperties(dbCbmInbound.get(),newCbmInbound, CommonUtils.getNullPropertyNames(dbCbmInbound));

        IKeyValuePair iKeyValuePair= priceListRepository.getCompanyIdAndDescription(newCbmInbound.getCompanyCodeId(), newCbmInbound.getLanguageId());
        IKeyValuePair iKeyValuePair1=priceListRepository.getPlantIdAndDescription(newCbmInbound.getPlantId(), newCbmInbound.getLanguageId(), newCbmInbound.getCompanyCodeId());
        IKeyValuePair iKeyValuePair2=priceListRepository.getWarehouseIdAndDescription(newCbmInbound.getWarehouseId(), newCbmInbound.getLanguageId(), newCbmInbound.getCompanyCodeId(), newCbmInbound.getPlantId());
        IKeyValuePair iKeyValuePair3= imBasicData1Repository.getItemCodeAndDescription(newCbmInbound.getWarehouseId(), newCbmInbound.getLanguageId(), newCbmInbound.getItemCode(), newCbmInbound.getCompanyCodeId(), newCbmInbound.getPlantId(),newCbmInbound.getUomId());
        if(iKeyValuePair!=null) {
            newCbmInbound.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if(iKeyValuePair1!=null) {
            newCbmInbound.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if(iKeyValuePair2!=null) {
            newCbmInbound.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        if(iKeyValuePair3!=null) {
            newCbmInbound.setItemCodeAndDescription(iKeyValuePair3.getItemCode() + "-" + iKeyValuePair3.getDescription());
        }
        return dbCbmInbound.get();
    }

    /**
     * createCbmInbound
     * @param newCbmInbound
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public CbmInbound createCbmInbound (AddCbmInbound newCbmInbound, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        CbmInbound dbCbmInbound = new CbmInbound();
        Optional<CbmInbound>duplicateCbmInbound=cbmInboundRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndLanguageIdAndDeletionIndicator(
                newCbmInbound.getCompanyCodeId(), newCbmInbound.getPlantId(), newCbmInbound.getWarehouseId(), newCbmInbound.getItemCode(), newCbmInbound.getLanguageId(), 0L);
        if(!duplicateCbmInbound.isEmpty()){
            throw new EntityNotFoundException("Record is Getting Duplicated");
        }
        else {
            AuthToken authTokenForIdMasterService = authTokenService.getIDMasterServiceAuthToken();
            IKeyValuePair iKeyValuePair =imBasicData1Repository.getItemCodeAndDescription(newCbmInbound.getWarehouseId(), newCbmInbound.getLanguageId(), newCbmInbound.getItemCode(),
                    newCbmInbound.getCompanyCodeId(), newCbmInbound.getPlantId(),newCbmInbound.getUomId());
//           if(iKeyValuePair== null){
//               throw new BadRequestException("The given values : " + "languageId - " + newCbmInbound.getLanguageId() +" plantId - " + newCbmInbound.getPlantId() +
//                       "warehouseId - " + newCbmInbound.getWarehouseId() +  "itemCode - " + newCbmInbound.getItemCode() + " UomId - " + newCbmInbound.getUomId() +
//                       " doesn't exist in ImBasicData");
//           }
            WarehouseId warehouseId=idMasterService.getWarehouseId(newCbmInbound.getWarehouseId(), newCbmInbound.getPlantId(), newCbmInbound.getCompanyCodeId(), newCbmInbound.getLanguageId(), authTokenForIdMasterService.getAccess_token());
            log.info("newCbmInbound : " + newCbmInbound);
            BeanUtils.copyProperties(newCbmInbound, dbCbmInbound, CommonUtils.getNullPropertyNames(newCbmInbound));
            dbCbmInbound.setCompanyIdAndDescription(warehouseId.getCompanyIdAndDescription());
            dbCbmInbound.setPlantIdAndDescription(warehouseId.getPlantIdAndDescription());
            dbCbmInbound.setWarehouseIdAndDescription(warehouseId.getWarehouseId()+"-"+warehouseId.getWarehouseDesc());
            if(iKeyValuePair != null && iKeyValuePair.getItemCode() != null) {
                dbCbmInbound.setItemCodeAndDescription(iKeyValuePair.getItemCode() + "-" + iKeyValuePair.getDescription());
            }
            dbCbmInbound.setCreatedBy(loginUserID);
            dbCbmInbound.setUpdatedBy(loginUserID);
            dbCbmInbound.setDeletionIndicator(0L);
            dbCbmInbound.setCreatedOn(new Date());
            dbCbmInbound.setUpdatedOn(new Date());
            log.info("dbCbmInound:" + dbCbmInbound);
            return cbmInboundRepository.save(dbCbmInbound);
        }
    }

    /**
     * updateCbmInbound
     * @param loginUserID
     * @param itemCode
     * @param updateCbmInbound
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public CbmInbound updateCbmInbound(String warehouseId, String itemCode,String companyCodeId,String languageId,String plantId,String loginUserID,
                                 UpdateCbmInbound updateCbmInbound)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        CbmInbound dbCbmInbound = getCbmInbound(warehouseId,itemCode,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updateCbmInbound, dbCbmInbound, CommonUtils.getNullPropertyNames(updateCbmInbound));
        dbCbmInbound.setUpdatedBy(loginUserID);
        dbCbmInbound.setUpdatedOn(new Date());
        return cbmInboundRepository.save(dbCbmInbound);
    }

    /**
     * deleteCbmInbound
     * @param loginUserID
     * @param itemCode
     */
    public void deleteCbmInbound(String warehouseId,String itemCode,String companyCodeId,String languageId,
                                 String plantId,String loginUserID) {
        CbmInbound dbCbmInbound = getCbmInbound(warehouseId,itemCode,companyCodeId,languageId,plantId);
        if ( dbCbmInbound != null) {
            dbCbmInbound.setDeletionIndicator(1L);
            dbCbmInbound.setUpdatedBy(loginUserID);
            cbmInboundRepository.save(dbCbmInbound);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + itemCode);
        }
    }
    //Find CbmInbound

    public List<CbmInbound> findCbmInbound(FindCbmInbound findCbmInbound) throws ParseException {

        CbmInboundSpecification spec = new CbmInboundSpecification(findCbmInbound);
        List<CbmInbound> results = cbmInboundRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
//        log.info("results: " + results);
        List<CbmInbound> newCbmInbound = new ArrayList<>();
        for (CbmInbound dbCbmInbound : results) {
            if (dbCbmInbound.getCompanyIdAndDescription() != null && dbCbmInbound.getPlantIdAndDescription() != null && dbCbmInbound.getWarehouseIdAndDescription() != null && dbCbmInbound.getItemCodeAndDescription() != null) {
                IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbCbmInbound.getCompanyCodeId(), dbCbmInbound.getLanguageId());
                IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbCbmInbound.getPlantId(), dbCbmInbound.getLanguageId(), dbCbmInbound.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbCbmInbound.getWarehouseId(), dbCbmInbound.getLanguageId(), dbCbmInbound.getCompanyCodeId(), dbCbmInbound.getPlantId());
                IKeyValuePair iKeyValuePair3 = imBasicData1Repository.getItemCodeAndDescription(dbCbmInbound.getWarehouseId(), dbCbmInbound.getLanguageId(), dbCbmInbound.getItemCode(), dbCbmInbound.getCompanyCodeId(), dbCbmInbound.getPlantId(),dbCbmInbound.getUomId());
                if (iKeyValuePair != null) {
                    dbCbmInbound.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbCbmInbound.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbCbmInbound.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbCbmInbound.setItemCodeAndDescription(iKeyValuePair3.getItemCode() + "-" + iKeyValuePair3.getDescription());
                }
                newCbmInbound.add(dbCbmInbound);
            }
        }
        return newCbmInbound;
    }
}
