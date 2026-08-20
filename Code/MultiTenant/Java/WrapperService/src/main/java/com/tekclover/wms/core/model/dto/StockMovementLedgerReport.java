package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StockMovementLedgerReport {

    public Date date;
    public Double outwardCases;
    public Double outwardPallets;
    public Double outwardWeight;
    public Double inwardCases;
    public Double inwardPallets;
    public Double inwardWeight;
    public Double openingCases;
    public Double openingPallets;
    public Double openingWeight;
    public Double closingCases;
    public Double closingPallets;
    public Double closingWeight;
    public String remarks;
}