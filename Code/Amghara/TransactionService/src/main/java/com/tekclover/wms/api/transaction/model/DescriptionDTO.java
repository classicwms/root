package com.tekclover.wms.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DescriptionDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String companyDesc;
    private String plantDesc;
    private String warehouseDesc;
}