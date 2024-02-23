import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { StratergyService } from '../stratergy.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-stratergy-new',
  templateUrl: './stratergy-new.component.html',
  styleUrls: ['./stratergy-new.component.scss']
})
export class StratergyNewComponent implements OnInit {

 
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
  form = this.fb.group({
    companyCodeId: [,Validators.required],
    plantId: [,Validators.required],
    warehouseId: [,Validators.required],
    strategyTypeId: [,Validators.required],
    strategyNo: [,Validators.required],
    languageId: [,Validators.required],
    strategyTypeText: [],
    strategyText: [],
    deletionIndicator: [],
    createdBy: [],
    createdOn: [],
    createdOnFE:[],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE:[],
    companyIdAndDescription:[],
    plantIdAndDescription:[],
    warehouseIdAndDescription:[],
  });
  panelOpenState = false;
  constructor(
    //public dialogRef: MatDialogRef<any>,
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: StratergyService,
    private cas: CommonApiService,
    private masterService: MasterService,
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
    if(this.auth.userTypeId != 1){
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
      this.form.controls.companyCodeId.disable();
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin();
    }
    if (this.data.pageflow != 'New') {
      this.form.controls.strategyTypeId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
      this.form.controls.companyCodeId.disable();
      this.form.controls.strategyNo.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;

  languageidList: any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
 
  storageclassidList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.storageclassid.url,

    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.companyidList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
    this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
   this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
    //this.storageclassidList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.storageclassid.key);

    this.masterService.searchstorageclass({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, warehouseId: this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.storageclassidList = [];
      res.forEach(element => {
        this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
      });
    });
    
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.storageclassid.url,
    this.cas.dropdownlist.setup.plantid.url,
    ]).subscribe((results) => {
      this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    console.log(this.data);
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
    this.masterService.searchstorageclass({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value, languageId: [value.value]}).subscribe(res => {
      this.storageclassidList = [];
      res.forEach(element => {
        this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
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
    this.masterService.searchstorageclass({companyCodeId: value.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storageclassidList = [];
      res.forEach(element => {
        this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
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
      this.masterService.searchstorageclass({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, warehouseId: this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.storageclassidList = [];
        res.forEach(element => {
          this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
        });
      });
    
  }

  onWarehouseChange(value){
    this.masterService.searchstorageclass({companyCodeId: this.form.controls.companyCodeId.value, plantId:this.form.controls.plantId.value, warehouseId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storageclassidList = [];
      res.forEach(element => {
        this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
      });
    });
}
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code, this.data.warehouseId,this.data.companyCodeId,this.data.plantId,this.data.languageId,this.data.strategyNo,this.data.strategyTypeId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
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

if (this.data.code) {
  this.sub.add(this.service.Update(this.form.getRawValue(),this.data.code,this.data.warehouseId,this.data.companyCodeId,this.data.plantId,this.data.languageId,this.data.strategyNo,this.data.strategyTypeId).subscribe(res => {
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
}else{
  this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
    this.toastr.success(res.strategyTypeId + " Saved Successfully!","Notification",{
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


