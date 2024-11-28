package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class LineReportProcess {

    private List<Sorting> sorting;

    private List<Soaking> soaking;

    private List<Peeling> peeling;

    private List<Paste> paste;

    private List<DiceSliceChop> diceSliceChop;

    private List<Powder> powder;

    private List<Cooking> cooking;
}