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
import { DooridService } from '../../doorid/doorid.service';
import { ModuleidService } from '../moduleid.service';

@Component({
  selector: 'app-moduleid-new',
  templateUrl: './moduleid-new.component.html',
  styleUrls: ['./moduleid-new.component.scss']
})
export class ModuleidNewComponent implements OnInit {
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
  companyIdAndDescription: [],
  createUpdate: [true],
  createdBy: [],
  createdOn: [],
  delete: [true],
  deletionIndicator: [],
  languageId: [,Validators.required],
  menuId: [],
  menuName: [],
  moduleDescription: [,Validators.required],
  moduleId: [,Validators.required],
  plantId: [,Validators.required],
  plantIdAndDescription: [],
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
  subMenuId: [],
  subMenuName: [],
  updatedBy: [],
  updatedOn: [],
  view: [true],
  warehouseId: [,Validators.required],
  warehouseIdAndDescription: [],
  updatedOnFE: [],
  createdOnFE: [],
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
    private service: ModuleidService,
    private masterService: MasterService
    
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
  
    if(this.auth.userTypeId != 1){
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
       this.form.controls.companyCodeId.disable();
       this.form.controls.warehouseId.disable();
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin();
    }
    if (this.data.pageflow != 'New') {
      this.form.controls.moduleId.disable();
      this.form.controls.languageId.disable();
     this.form.controls.plantId.disable();
      this.form.controls.companyCodeId.disable();
      this.form.controls.warehouseId.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.warehouseId,this.data.languageId,this.data.companyCodeId,this.data.plantId).subscribe(res => {
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
  languageidList:any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
  menuList:any[]=[];
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
   // this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
   this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.plantidList = [];
    res.forEach(element => {
      this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
    });
  });
  this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.menuList = [];
    res.forEach(element => {
      this.menuList.push({value: element.subMenuId, label: element.subMenuId + '-' + element.subMenuName, menuId: element.menuId, menuName: element.menuName, subMenuId: element.subMenuId, subMenuName: element.subMenuName});
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
     
    //this.cas.dropdownlist.setup.plantid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.companyidList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.companyid.key);
    this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);
   
   // this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
   this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.plantidList = [];
      res.forEach(element => {
        this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.menuList = [];
      res.forEach(element => {
        this.menuList.push({value: element.subMenuId, label: element.subMenuId + '-' + element.subMenuName, menuId: element.menuId, menuName: element.menuName, subMenuId: element.subMenuId, subMenuName: element.subMenuName});
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
  }

  onscreenIDChange(value){
    console.log(value)
    this.form.controls.menuId.patchValue(value.menuId);
    this.form.controls.menuName.patchValue(value.menuName);
    this.form.controls.subMenuName.patchValue(value.subMenuName);
   // this.form.controls.moduleId.patchValue(value.menuId);
    this.form.controls.moduleDescription.patchValue(value.subMenuName);
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
    this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.data.warehouseId,this.data.languageId,this.data.companyCodeId,this.data.plantId).subscribe(res => {
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
      this.toastr.success(res.moduleId + " Saved Successfully!","Notification",{
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













 