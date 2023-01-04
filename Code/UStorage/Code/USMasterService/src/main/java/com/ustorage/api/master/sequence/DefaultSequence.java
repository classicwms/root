package com.ustorage.api.master.sequence;

import java.io.Serializable;

public class DefaultSequence extends BaseSequence<Object> {

    @Override
    protected Serializable generate(Object object, Serializable currentCounter) {
        return valuePrefix + String.format(numberFormat, currentCounter);
    }

}