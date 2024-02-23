import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ItemReceiptService } from '../../Item receipt/item-receipt.service';
import { PutawayService } from '../putaway.service';

@Component({
  selector: 'app-putawayheader',
  templateUrl: './putawayheader.component.html',
  styleUrls: ['./putawayheader.component.scss']
})
export class PutawayheaderComponent implements OnInit {

  
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
  form = this.fb.group({
    languageId: [],
  companyCodeId: [],
  plantId: [],
  warehouseId: [],
  preInboundNo: [],
  refDocNumber: [],
  goodsReceiptNo: [],
  palletCode: [],
  caseCode: [],
  packBarcodes:[],
  inboundOrderTypeId: [],
  putAwayNumber: [],
  proposedStorageBin: [],
  putAwayQuantity: [],
  putAwayUom: [],
  strategyTypeId: [],
  strategyNo: [],
  proposedHandlingEquipment: [],
  assignedUserId: [],
  statusId: [],
  quantityType: [],
  createdOnFE:[],
  updatedOnFE:[],
  referenceField1: [],
  referenceField2: [],
  referenceField3: [],
  referenceField4: [],
  referenceField5: [],
  referenceField6: [],
  referenceField7: [],
  referenceField8: [],
  referenceField9: [],
  referenceField10: [],
  deletionIndicator: [],
  createdBy: [],
  createdOn: [],
  confirmedBy: [],
  confirmedOn: [],
  updatedBy: [],
  updatedOn: [],
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
    private service:PutawayService ,
    private cas: CommonApiService,
    private masterService: MasterService,
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
   this.form.controls.languageId.patchValue(this.auth.languageId);
   this.form.controls.plantId.patchValue(this.auth.plantId);
   this.form.controls.companyCodeId.patchValue(this.auth.companyId);
   this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
     this.form.controls.warehouseId.disable();
     this.form.controls.languageId.disable();
   this.form.controls.plantId.disable();
   this.form.controls.preInboundNo.disable();
   this.form.controls.refDocNumber.disable();
   this.form.controls.caseCode.disable();
   this.form.controls.referenceDocumentType.disable();
     //this.form.controls.companyCode.disable();
     this.dropdownlist();
    // }else{
    //   this.dropdownlistSuperAdmin();
    // }
    // if (this.data.pageflow != 'New') {
    //   // this.form.controls.preInboundNo.disable();
    //   // //this.form.controls.handlingEquipmentNumber.disable();
    //   // this.form.controls.warehouseId.disable();
    //   // this.form.controls.languageId.disable();
    //   // this.form.controls.plantId.disable();
    //   // this.form.controls.companyCode.disable();
    
      this.fill();
// if(this.data.pageflow == "Display"){
//   this.fill();
//   this.form.disable();
// }
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
    console.log(this.data);
    this.spin.show();
this.form.patchValue(this.data.code);
this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.data.code.createdOn));
this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.data.code.updatedOn));
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
 
  if (this.form) {
  
    this.sub.add(this.service.Update(this.form.getRawValue(),this.form.controls.putAwayNumber.value,this.form.controls.warehouseId.value,this.form.controls.caseCode.value,this.form.controls.goodsReceiptNo.value,this.form.controls.packBarcodes.value,this.form.controls.palletCode.value,this.form.controls.preInboundNo.value,this.form.controls.proposedStorageBin.value,this.form.controls.refDocNumber.value,this.form.controls.companyCodeId.value,this.form.controls.plantId.value,this.form.controls.languageId.value).subscribe(res => {
      this.toastr.success(this.data.code.putAwayNumber + " updated successfully!","Notification",{
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













 




