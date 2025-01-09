package com.mnrclara.spark.core.model.mnrclara;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ClientGeneral {

    private String clientId;
    private Long classId;
    private String firstNameLastName;
    private String corporationClientId;
    private String emailId;
    private String contactNumber;
    private String addressLine1;
//    private String addressLine2;
    private String intakeFormNumber;
    private Long intakeFormId;
//    private String city;
//    private String state;
//    private String zipCode;
    private Long statusId;
    private Timestamp createdOnString;

}
