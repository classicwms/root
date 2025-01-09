package com.mnrclara.spark.core.model.overc360;

import lombok.Data;

import java.util.List;

@Data
public class PieceDetails {

    private String customs_value;
    private String tags;
    private String volume;
    private String weight;
    private String weight_unit;

    private List<ItemDetails> itemDetails;

//    private ItemDetails itemDetails;

}
