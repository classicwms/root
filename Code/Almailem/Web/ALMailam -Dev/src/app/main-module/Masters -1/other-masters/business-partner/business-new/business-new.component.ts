import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BusinesspartnerElement, BusinessPartnerService } from '../business-partner.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-business-new',
  templateUrl: './business-new.component.html',
  styleUrls: ['./business-new.component.scss']
})
export class BusinessNewComponent implements OnInit {
  
  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  screenid: 1039 | undefined;
  sub = new Subscription();
  //creation of Form
  form = this.fb.group({
    address1: [],
    address2: [],
    civilId:[],
    alternatePhoneNumber:[],
    remarks:[],
    isNew:[],
    isUpdate:[],
    isCompleted:[],
    middlewareId:[],
    middlewareTable:[],
    businessPartnerType: [,Validators.required],
    companyCodeId: [this.auth.companyId,],
    country: [],
    createdBy: [this.auth.username,],
    createdon: [],
   // deletionIndicator: [],
    emailId: [],
    faxNumber: [],
    languageId: [this.auth.languageId,],
    lattitude: [],
    location: [],
    longitude: [],
    partnerCode: [,Validators.required],
    partnerName: [],
    phoneNumber: [],
    plantId: ['1001'],
    // referenceField1: [],
    // referenceField10: [],
    // referenceField2: [],
    // referenceField3: [],
    // referenceField4: [],
    // referenceField5: [],
    // referenceField6: [],
    // referenceField7: [],
    // referenceField8: [],
    // referenceField9: [],
    referenceText: [],
    state: [],
    statusId: ["1"],
    //storageBin: [],
    //storageTypeId: [],
    updatedBy: [this.auth.username,],
    updatedon: [],
    warehouseId: [this.auth.warehouseId,],
    zone: [],
    createdOnFE:[],
    updatedOnFE:[],
    updatedOn:[],
    createdOn:[],
  
  });
  submitted = false;
  disabled = false;
  step = 0;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }


  // statusList: any[] = [
  //   { key: "Active", value: 'Active' },
  //   { key: "InActive", value: 'InActive' }];
    
  formgr: BusinesspartnerElement | undefined;

  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: BusinessPartnerService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    private cas: CommonApiService,
    private cs: CommonService,
  ) { }
  ngOnInit(): void {
     this.form.controls.createdBy.disable();
  this.form.controls.createdOnFE.disable();
    this.form.controls.updatedBy.disable();
     this.form.controls.updatedOnFE.disable();

     this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
     this.form.controls.languageId.patchValue(this.auth.languageId);
     this.form.controls.plantId.patchValue(this.auth.plantId);
     this.form.controls.companyCodeId.patchValue(this.auth.companyId);
     this.form.controls.warehouseId.disable();
     this.dropdownlist();            
    
      
    
   
    if (this.data.pageflow != 'New') {
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }
  }



  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId,this.data.businessPartnerType).subscribe(res => {
      console.log(res)
      
      this.form.patchValue(res, { emitEvent: false });
      this.form.controls.businessPartnerType.patchValue(res.businessPartnerType != null ? res.businessPartnerType.toString() : '');
      this.form.controls.statusId.patchValue(res.statusId != null ? res.statusId.toString() : '');
      console.log(this.form.controls.businessPartnerType.value);
      // this.form.controls.civilId.disable();
      // this.form.controls.alternatePhoneNumber.disable();
      // this.form.controls.remarks.disable();
      this.form.controls.createdOnFE.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
      this.form.controls.updatedOnFE.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
      this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }
  warehouseIdList: any[] = [];
  companyList: any[] = [];
  plantList: any[]=[];
  languageList: any[]=[];
  warehouseidDropdown:any;
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
    
    ]).subscribe((results) => {
      this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
      this.companyList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.plantid.key);
      this.languageList=this.cas.foreachlist2(results[3],this.cas.dropdownlist.setup.languageid.key);
        this.form.controls.warehouseId.patchValue(this.form.controls.warehouseId.value);  
        this.form.controls.languageId.patchValue(this.form.controls.languageId.value);
        this.form.controls.plantId.patchValue(this.form.controls.plantId.value);
        this.form.controls.companyCodeId.patchValue(this.form.controls.companyCodeId.value);
       // this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
       this.form.controls.warehouseId.disable();
       this.form.controls.warehouseId.disable();
       this.form.controls.companyCodeId.disable();
       this.form.controls.plantId.disable();
       this.form.controls.languageId.disable();
      
  
    });
    this.spin.hide();
  }
   
  onWarehouseChange(value){
    this.form.controls.companyCodeId.patchValue(value.companyCodeId);
    this.form.controls.languageId.patchValue(value.languageId);
    this.form.controls.plantId.patchValue(value.plantId);
  
   
}

public errorHandling = (control: string, error: string = "required") => {
  return this.form.controls[control].hasError(error) && this.submitted;
}
getErrorMessage() {
  if (this.email.hasError('required')) {
    return ' Field should not be blank';
  }
  return this.email.hasError('email') ? 'Not a valid email' : '';
}
  submit() {
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.cs.notifyOther(false);
    this.spin.show();
    // this.form.removeControl('updatedOn');
    // this.form.removeControl('createdOn');
    this.form.patchValue({ updatedby: this.auth.username });
    if (this.data.code) {
      this.sub.add(this.service.UpdateV2(this.form.getRawValue(),this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId,this.data.businessPartnerType).subscribe(res => {
        this.toastr.success(this.data.code + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.dialogRef.close();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.partnerCode + " Saved Successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.dialogRef.close();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  };

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
}




