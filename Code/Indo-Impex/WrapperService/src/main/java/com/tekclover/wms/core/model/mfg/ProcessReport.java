package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;

@Data
public class ProcessReport {

//    private SortingImpl sorting;
//
//    private SoakingImpl soaking;
//
//    private PeelingImpl peeling;
//
//    private PasteImpl paste;
//
//    private DiceSliceChopImpl diceSliceChop;
//
//    private PowderImpl powder;
//
//    private CookingImpl cooking;
String itemCode;
    String bomItem;
    String productionOrderNo;
    String phaseNumber;
    String operationNumber;
    String batchNumber;
    String phaseDescription;
    Double waterQuantity;
    String statusDescription;
    String numberOfWorker;
    String supervisorName;
    String storageLocation;
    Double outputQuantity;
    String updatedBy;
    Date updatedOn;
    Date startTime;
    Date endTime;
    
}