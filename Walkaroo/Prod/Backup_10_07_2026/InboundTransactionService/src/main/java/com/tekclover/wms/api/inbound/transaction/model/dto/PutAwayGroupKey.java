package com.tekclover.wms.api.inbound.transaction.model.dto;

import java.util.Objects;

public class PutAwayGroupKey {

//    private String refDocNumber;
    private String palletId;
    private String proposedStorageBin;

    public PutAwayGroupKey(String palletId, String proposedStorageBin) {
//        this.refDocNumber = refDocNumber;
        this.palletId = palletId;
        this.proposedStorageBin = proposedStorageBin;
    }

    // Getters (optional if not using them outside grouping)
//    public String getRefDocNumber() {
//        return refDocNumber;
//    }

    public String getPalletId() {
        return palletId;
    }

    public String getProposedStorageBin() {
        return proposedStorageBin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PutAwayGroupKey)) return false;
        PutAwayGroupKey that = (PutAwayGroupKey) o;
        return Objects.equals(palletId, that.palletId) &&
                Objects.equals(proposedStorageBin, that.proposedStorageBin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(palletId, proposedStorageBin);
    }

    @Override
    public String toString() {
        return "PutAwayGroupKey{" +
//                "refDocNumber='" + refDocNumber + '\'' +
                "palletId='" + palletId + '\'' +
                ", proposedStorageBin='" + proposedStorageBin + '\'' +
                '}';
    }
}
