package com.mnrclara.spark.core.model.mnrclara;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindClientGeneral {

    private List<String> clientId;

    private List<String> intakeFormNumber;

    private List<Long> statusId;

    private List<Long> classId;

    private String firstNameLastName;

    private String emailId;

    private String contactNumber;

    private String addressLine1;

    private Date startDate;

    private Date endDate;

}
