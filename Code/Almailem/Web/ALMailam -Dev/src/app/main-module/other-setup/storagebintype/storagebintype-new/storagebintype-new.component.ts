import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { StoragebintypeService } from '../storagebintype.service';
import { MasterService } from 'src/app/shared/master.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-storagebintype-new',
  templateUrl: './storagebintype-new.component.html',
  styleUrls: ['./storagebintype-new.component.scss']
})
export class StoragebintypeNewComponent implements OnInit {

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
    companyCodeId: [,Validators.required],
    createdBy: [],
    createdOn: [],
    createdOnFE: [],
    deletionIndicator: [],
    description: [],
    languageId: [,Validators.required],
    plantId: [,Validators.required],
    storageBinTypeId: [,Validators.required],
    storageClassId: [,Validators.required],
    storageTypeId: [,Validators.required],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE:[],
    warehouseId: [,Validators.required],
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
    private cas: CommonApiService,
    private service: StoragebintypeService,
    private masterService: MasterService
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
   this.form.controls.storageTypeId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
      this.form.controls.companyCodeId.disable();
      this.form.controls.storageClassId.disable();
      this.form.controls.storageBinTypeId.disable();
      this.form.controls.storageClassId.patchValue(this.form.controls.storageClassId.value);
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.storageTypeId,this.data.storageClassId,this.data.warehouseId,this.data.companyCodeId,this.data.plantId,this.data.languageId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    if(this.auth.userTypeId != 1){
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin();
    }
    this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }
  languageidList: any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
  storagetypeidList:any[]=[];
  storageclassidList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.storageclassid.url,
      this.cas.dropdownlist.setup.storagetypeid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    // this.companyidList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
    // this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
    // this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
   // this.storageclassidList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.storageclassid.key);
  //  this.storageclassidList = this.cs.removeDuplicatesFromArraydropdown(this.storageclassidList);
   // this.storagetypeidList = this.cas.forLanguageFilter(results[5], this.cas.dropdownlist.setup.storagetypeid.key);
   // this.storagetypeidList = this.cs.removeDuplicatesFromArrayNewstatus(this.storagetypeidList);
   this.masterService.searchCompany({languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.companyidList = [];
     res.forEach(element => {
    this.companyidList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
     });
   });
 
 //this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
 this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  this.plantidList = [];
  res.forEach(element => {
    this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  });
});  this.masterService.searchWarehouse({languageId: [this.form.controls.languageId.value],companyCodeId:this.form.controls.companyCodeId.value,plantId:this.form.controls.plantId.value}).subscribe(res => {
  this.warehouseidList = [];
   res.forEach(element => {
  this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
   });
 });
    this.masterService.searchstorageclass({companyCodeId: this.auth.companyId ,plantId: this.auth.plantId, warehouseId: this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.storageclassidList = [];
      res.forEach(element => {
        this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
      });
    });
    this.masterService.searchstoragetype({companyCodeId: this.auth.companyId ,plantId: this.auth.plantId, warehouseId: this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.storagetypeidList = [];
      res.forEach(element => {
        this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
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
      this.cas.dropdownlist.setup.storagetypeid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.companyidList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.companyid.key);
    this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);
    this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
   // this.storageclassidList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.storageclassid.key);
// this.storagetypeidList = this.cas.foreachlist2(results[5], this.cas.dropdownlist.setup.storagetypeid.key);
this.masterService.searchstorageclass({companyCodeId: this.form.controls.companyCodeId.value ,plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  this.storageclassidList = [];
  res.forEach(element => {
    this.storageclassidList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
  });
})
this.masterService.searchstoragetype({companyCodeId: this.form.controls.companyCodeId.value ,plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  this.storagetypeidList = [];
  res.forEach(element => {
    this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
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
    this.masterService.searchstoragetype({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,storageClassId:[this.form.controls.storageClassId.value], languageId: [value.value]}).subscribe(res => {
      this.storagetypeidList = [];
      res.forEach(element => {
        this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
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
    this.masterService.searchstoragetype({companyCodeId: value.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,storageClassId:[this.form.controls.storageClassId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storagetypeidList = [];
      res.forEach(element => {
        this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
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
      this.masterService.searchstoragetype({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, warehouseId: this.form.controls.warehouseId.value,storageClassId:[this.form.controls.storageClassId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.storageclassidList = [];
        res.forEach(element => {
          this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
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

    this.masterService.searchstoragetype({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: value.value,storageClassId:[this.form.controls.storageClassId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storagetypeidList = [];
      res.forEach(element => {
        this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
      });
    });
}
onstorageChange(value){
    this.masterService.searchstoragetype({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,storageClassId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storagetypeidList = [];
      res.forEach(element => {
        this.storagetypeidList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
      });
    });
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
    this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.data.storageTypeId,this.data.storageClassId,this.data.warehouseId,this.data.companyCodeId,this.data.plantId,this.data.languageId).subscribe(res => {
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
      this.toastr.success(res.storageBinTypeId + " Saved Successfully!","Notification",{
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
