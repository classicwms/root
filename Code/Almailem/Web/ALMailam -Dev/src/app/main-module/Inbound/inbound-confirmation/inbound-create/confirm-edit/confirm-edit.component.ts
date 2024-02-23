import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { InboundConfirmationService } from '../../inbound-confirmation.service';

@Component({
  selector: 'app-confirm-edit',
  templateUrl: './confirm-edit.component.html',
  styleUrls: ['./confirm-edit.component.scss']
})
export class ConfirmEditComponent implements OnInit {

  disabled = false;
  step = 0;
  //dialogRef: any;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }
  inboundLine = this.fb.group({
    acceptedQty: [],
    companyCodeId: [this.auth.companyId],
    confirmedBy: [],
    confirmedOn: [],
    containerNo: [],
    createdBy: [],
    createdOn: [],
    damageQty: [],
    deletionIndicator: [],
    description: [],
    expectedArrivalDate: [],
    hsnCode: [],
    inboundOrderTypeId: [],
    invoiceNo: [],
    itemBarcode: [],
    itemCode: [],
    languageId: [],
    lineNo: [],
    manufacturerPartNo: [],
    orderQty: [],
    orderUom: [],
    plantId: [],
    preInboundNo: [],
    putawayConfirmedQty: [],
    refDocNumber: [],
    referenceField1: [],
    referenceField10: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    referenceOrderNo: [],
    specialStockIndicatorId: [],
    statusId: [],
    stockTypeId: [],
    varianceQty: [],
    variantCode: [],
    variantSubCode: [],
    vendorCode: [],
  
    warehouseId: [],
  });
   inboundLines: FormArray = this.fb.array([this.inboundLine]);

  form = this.fb.group({
    acceptedQty: [],
    companyCodeId: [],
    companyDescription: [],
    confirmedBy: [],
    confirmedOn: [],
    containerNo: [],
    createdBy: [],
    createdOn: [],
    damageQty: [],
    deletionIndicator: [],
    description: [],
    expectedArrivalDate:[],
    hsnCode: [],
    inboundOrderTypeId: [],
    invoiceNo: [],
    itemBarcode:[],
    itemCode: [],
    languageId: [],
    lineNo: [],
    manufacturerPartNo:[],
    orderQty: [],
    orderUom: [],
    plantDescription: [],
    plantId: [],
    preInboundNo: [],
    putawayConfirmedQty:[],
    refDocNumber:[],
    createdOnFE:[],
    updatedOnFE:[],
    referenceField1: [],
    referenceField10: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4:  [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    referenceOrderNo: [],
    specialStockIndicatorId: [],
    statusDescription: [],
    statusId: [],
    stockTypeId: [],
    varianceQty: [],
    variantCode: [],
    variantSubCode: [],
    vendorCode: [],
    warehouseDescription: [],
    warehouseId:[],
  });

  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: InboundConfirmationService,
    private cas: CommonApiService,
    private masterService: MasterService,
  ) { }
  ngOnInit(): void {
   
    
   this.form.controls.languageId.patchValue(this.auth.languageId);
   this.form.controls.plantId.patchValue(this.auth.plantId);
   this.form.controls.companyCodeId.patchValue(this.auth.companyId);
   this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
     this.form.controls.warehouseId.disable();
     this.form.controls.languageId.disable();    
   this.form.controls.plantId.disable();
   this.form.controls.warehouseId.disable();
   this.form.controls.languageId.disable();
 this.form.controls.plantId.disable();
 this.form.controls.refDocNumber.disable();
 this.form.controls.preInboundNo.disable();
 this.form.controls.itemCode.disable();
 this.form.controls.description.disable();
     this.dropdownlist();
      this.fill();
  }
  sub = new Subscription();
  submitted = false;
  
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
     this.cas.dropdownlist.setup.plantid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.masterService.searchCompany({languageId: [this.data.languageId]}).subscribe(res => {
      this.companyidList = [];
       res.forEach(element => {
      this.companyidList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
       });
     });
   
   //this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
   this.masterService.searchPlant({companyCodeId: [this.data.companyCodeId], languageId: [this.data.languageId]}).subscribe(res => {
    this.plantidList = [];
    res.forEach(element => {
      this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
    });
  }); 
   this.masterService.searchWarehouse({languageId: [this.data.languageId],companyCodeId:this.data.companyCodeId,plantId:this.data.plantId}).subscribe(res => {
    this.warehouseidList = [];
     res.forEach(element => {
    this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
     });
   });
   
   this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  languageidList: any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
     this.companyidList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
     this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
  this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
 
    this.spin.hide();
     }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
     });
  }
  onLanguageChange(value){
    this.masterService.searchCompany({languageId: [value.value]}).subscribe(res => {
      this.companyidList = [];
      res.forEach(element => {
        this.companyidList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
      });
    });
    this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [value.value]}).subscribe(res => {
      this.plantidList = [];
      res.forEach(element => {
        this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [value.value]}).subscribe(res => {
      this.warehouseidList = [];
      res.forEach(element => {
        this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
      });
    });
 
  }
  onCompanyChange(value){
    this.masterService.searchPlant({companyCodeId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.plantidList = [];
      res.forEach(element => {
        this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    this.masterService.searchWarehouse({companyCodeId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.warehouseidList = [];
      res.forEach(element => {
        this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
      });
    });
    
  }
  onPlantChange(value){
      this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.warehouseidList = [];
        res.forEach(element => {
          this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
        });
      });
     ;
  }
  fill() {
    this.spin.show();
    console.log(this.data.code);
    this.sub.add(this.service.Get1(this.data.code.warehouseId,this.data.code.preInboundNo,this.data.code.refDocNumber,this.auth.companyId,this.data.code.languageId,this.data.code.plantId,this.data.code.itemCode,this.data.code.lineNo).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
      this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
      this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
  
      this.dropdownlist();
 
    this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
   // this.form.controls.varianceQty.patchValue(0);
  // this.form.patchValue(this.data.code);

    console.log(this.form.value);
  }
  submit(){
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
  
  if (this.data) {
    this.sub.add(this.service.updateinboundlineV2(this.form.getRawValue(),this.form.controls.refDocNumber.value,this.form.controls.warehouseId.value,this.auth.companyId,this.form.controls.plantId.value,this.form.controls.languageId.value,this.form.controls.preInboundNo.value,this.form.controls.lineNo.value,this.form.controls.itemCode.value).subscribe(res => {
      this.toastr.success(this.data.code.refDocNumber + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.dialogRef.close(this.form.getRawValue());
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }

  
   }
   email = new FormControl('', [Validators.required, Validators.email]);
   public errorHandling = (control: string, error: string = "required") => {
     return this.form.controls[control].hasError(error) && this.submitted;
   }
   getErrorMessage() {
     if (this.email.hasError('required')) {
       return ' Field should not be blank';
     }
     return this.email.hasError('email') ? 'Not a valid email' : '';
   }
   
}













 





