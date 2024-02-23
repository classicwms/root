import { Component, Inject, OnInit } from '@angular/core';
import { Validators, FormBuilder, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { UserprofileService } from '../userprofile.service';
import { HhtuserService } from '../../hhtuser/hhtuser.service';

@Component({
  selector: 'app-userprofile-new',
  templateUrl: './userprofile-new.component.html',
  styleUrls: ['./userprofile-new.component.scss']
})
export class UserprofileNewComponent implements OnInit {

 
  hide = true;
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
  companyCode: [,Validators.required],
  companyCodeId:[],
  createdBy: [],
  createdOn: [],
  createdOnFE: [],
  currencyDecimal: [],
  dateFormatId: [],
  deletionIndicator: [],
  emailId: [,],
  firstName: [],
  portalLoggedIn: [false,],
  hhtLoggedIn: [false,],
  levelId: [],
  resetPassword: [false, ],
  languageId: [],
  lastName: [],
  password: [,],
  confirmPassword: [,],
  passwordFE: [,Validators.required],
  plantId: [,Validators.required],
  statusId: [,Validators.required],
  timeZone: [],
  updatedBy: [],
  updatedOn: [],
  updatedOnFE: [],
  userId: [,Validators.required],
  userName: [,Validators.required],
  userRoleId: [,Validators.required],
  userTypeId: [,Validators.required],
  warehouseId: [,Validators.required],

  
  createHhtUser: [false,],
  caseReceipts: [true,],
  itemReceipts: [true,],
  putaway: [true,],
  transfer: [true,],
  picking: [true,],
  quality: [true,],
  inventory: [true,],
  customerReturn: [true,],
  supplierReturn: [true,],
  
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
    private service: UserprofileService,
    private cas: CommonApiService,
    private masterService: MasterService,
    private HhtuserService: HhtuserService
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
    if(this.auth.userTypeId != 1){
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyCode.patchValue(this.auth.companyId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
    
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin();
    }
    if (this.data.pageflow != 'New') {
      this.form.controls.userRoleId.disable();
      this.form.controls.userTypeId.disable();
      this.form.controls.userId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
   //this.form.controls.floorId.disable();
      this.form.controls.companyCode.disable();
     //this.form.controls.rowNumber.disable();   
    // this.form.controls.storageSectionId.disable();
      if (this.data.pageflow == 'Display')
     // this.form.controls.floorId.disable();
        this.form.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.warehouseId,this.data.companyCode,this.data.languageId,this.data.plantId,this.data.userRoleId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
     
      this.form.controls.passwordFE.patchValue(this.form.controls.password.value);
      this.form.controls.password.patchValue(null);
      this.form.controls.statusId.patchValue(res.statusId ? res.statusId.toString() : '');
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

  passwordChange(e){
    console.log(this.form.controls.passwordFE.value);
    this.form.controls.password.patchValue(this.form.controls.passwordFE.value);
  }

  confirmPassword(e){
    if(this.form.controls.passwordFE.value != this.form.controls.lastName.value){
      this.toastr.error(
        "Password Mismatch",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );
    }
  }
  
  languageidList: any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
  flooridList:any[]=[];
  storagesectionList:any[]=[];
  usertypeList:any[]=[];
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.userRole.url,
      this.cas.dropdownlist.setup.levelid.url,
      this.cas.dropdownlist.setup.userType.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.companyidList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.companyid.key);
    this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);
    this.plantidList = this.cas.foreachlist2(results[3], this.cas.dropdownlist.setup.plantid.key);
    this.userRoleIdList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.userRole.key);
    this.userRoleIdList =  this.cs.removeDuplicatesFromArrayNewstatus(this.userRoleIdList);
    this.levelList=this.cas.forLanguageFilter(results[5],this.cas.dropdownlist.setup.floorid.key);
    this.usertypeList=this.cas.foreachlist2(results[6],this.cas.dropdownlist.setup.userType.key);
    
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  levelList: any[] = [];
  userRoleIdList: any[] = [];
 
   dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.userType.url,
 //     this.cas.dropdownlist.setup.userRole.url,
      
    ]).subscribe((results) => {
      this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
      this.companyidList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
      this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
     this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
     this.usertypeList=this.cas.forLanguageFilter(results[4],this.cas.dropdownlist.setup.userType.key);
   this.masterService.searchusertype({companyCodeId: this.auth.companyId, languageId: [this.auth.languageId],plantId:this.auth.plantId,warehouseId:this.auth.warehouseId}).subscribe(res => {
   
    this.usertypeList = [];
      res.forEach(element => {
        this.usertypeList.push({value: element.userTypeId, label: element.userTypeId + '-' + element.userTypeDescription});
      });
    });

    this.masterService.searchFloor({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], languageId: [this.auth.languageId],warehouseId:[this.auth.warehouseId]}).subscribe(res => {
      this.levelList = [];
      res.forEach(element => {
        this.levelList.push({value: element.floorId, label: element.floorId + '-' + element.description});
      });
      this.levelList = this.cs.removeDuplicatesFromArrayNewstatus(this.levelList);
    });


    this.masterService.searchRole({companyCodeId: this.form.controls.companyCode.value, plantId: this.form.controls.plantId.value, languageId: [this.auth.languageId]}).subscribe(res => {
      this.userRoleIdList = [];
      res.forEach(element => {
        this.userRoleIdList.push({value: element.roleId, label: element.roleId + '-' + element.userRoleName});
        this.userRoleIdList =  this.cs.removeDuplicatesFromArrayNewstatus(this.userRoleIdList);
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
    this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCode.value], languageId: [value.value]}).subscribe(res => {
      this.plantidList = [];
      res.forEach(element => {
        this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCode.value, plantId: this.form.controls.plantId.value, languageId: [value.value]}).subscribe(res => {
      this.warehouseidList = [];
      res.forEach(element => {
        this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
      });
    });

    this.masterService.searchRole({companyCodeId: this.form.controls.companyCode.value, plantId: this.form.controls.plantId.value, languageId: [value.value]}).subscribe(res => {
      this.userRoleIdList = [];
      res.forEach(element => {
        this.userRoleIdList.push({value: element.roleId, label: element.roleId + '-' + element.userRoleName});
        this.userRoleIdList =  this.cs.removeDuplicatesFromArrayNewstatus(this.userRoleIdList);
      });
    });
    this.masterService.searchusertype({companyCodeId: this.form.controls.companyCode.value, plantId: this.form.controls.plantId.value, languageId: [value.value]}).subscribe(res => {
   
      this.usertypeList = [];
        res.forEach(element => {
          this.usertypeList.push({value: element.userTypeId, label: element.userTypeId + '-' + element.userTypeDescription});
        });
      });
      this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], languageId: [value.value],warehouseId:[this.auth.warehouseId]}).subscribe(res => {
        this.levelList = [];
        res.forEach(element => {
          this.levelList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
        this.levelList =  this.cs.removeDuplicatesFromArrayNewstatus(this.levelList);
      });
  
  }
  onCompanyChange(value){
    this.form.controls.companyCodeId.patchValue(value.value);
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

    this.masterService.searchRole({companyCodeId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.userRoleIdList = [];
      res.forEach(element => {
        this.userRoleIdList.push({value: element.roleId, label: element.roleId + '-' + element.userRoleName});
        this.userRoleIdList =  this.cs.removeDuplicatesFromArrayNewstatus(this.userRoleIdList);
      });
    });
    this.masterService.searchusertype({companyCodeId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
   
      this.usertypeList = [];
        res.forEach(element => {
          this.usertypeList.push({value: element.userTypeId, label: element.userTypeId + '-' + element.userTypeDescription});
        });
      });
      this.masterService.searchFloor({companyCodeId: [value.value], plantId: [this.form.controls.plantId.value], languageId: [this.form.controls.languageId.value],warehouseId:[this.auth.warehouseId]}).subscribe(res => {
        this.levelList = [];
        res.forEach(element => {
          this.levelList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
        this.levelList =  this.cs.removeDuplicatesFromArrayNewstatus(this.levelList);
      });
  }
  onPlantChange(value){
      this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCode.value, plantId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.warehouseidList = [];
        res.forEach(element => {
          this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
        });
      });
      this.masterService.searchRole({companyCodeId: this.form.controls.companyCode.value, plantId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.userRoleIdList = [];
        res.forEach(element => {
          this.userRoleIdList.push({value: element.roleId, label: element.roleId + '-' + element.userRoleName});
          this.userRoleIdList =  this.cs.removeDuplicatesFromArrayNewstatus(this.userRoleIdList);
        });
      });
      this.masterService.searchusertype({companyCodeId: this.form.controls.companyCode.value, plantId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
   
        this.usertypeList = [];
          res.forEach(element => {
            this.usertypeList.push({value: element.userTypeId, label: element.userTypeId + '-' + element.userTypeDescription});
          });
        });

        this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [value.value], languageId: [this.form.controls.languageId.value],warehouseId:[this.auth.warehouseId]}).subscribe(res => {
          this.levelList = [];
          res.forEach(element => {
            this.levelList.push({value: element.floorId, label: element.floorId + '-' + element.description});
          });
          this.levelList =  this.cs.removeDuplicatesFromArrayNewstatus(this.levelList);
        });
  }

  onWarehouseChange(value){
    this.masterService.searchRole({companyCodeId: this.form.controls.companyCode.value, plantId: this.form.controls.plantId.value,  warehouseId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.userRoleIdList = [];
      res.forEach(element => {
        this.userRoleIdList.push({value: element.roleId, label: element.roleId + '-' + element.userRoleName});
        this.userRoleIdList =  this.cs.removeDuplicatesFromArrayNewstatus(this.userRoleIdList);
      });
    });
    this.masterService.searchusertype({companyCodeId: this.form.controls.companyCode.value, plantId: this.form.controls.plantId.value,  warehouseId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
   
      this.usertypeList = [];
        res.forEach(element => {
          this.usertypeList.push({value: element.userTypeId, label: element.userTypeId + '-' + element.userTypeDescription});
        });
      });
      this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], languageId: [this.form.controls.langaugeId.value],warehouseId:[value.value]}).subscribe(res => {
        this.levelList = [];
        res.forEach(element => {
          this.levelList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
        this.levelList =  this.cs.removeDuplicatesFromArrayNewstatus(this.levelList);
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

    if (this.form.controls.userTypeId.value == 5 && this.form.controls.levelId.value == null) {
      this.toastr.error(
        "Please fill level field to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );
  
      this.cs.notifyOther(true);
      return;
    }

    if(this.form.controls.passwordFE.value != this.form.controls.lastName.value){
      this.toastr.error(
        "Password Mismatch",
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
    this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.data.warehouseId,this.data.companyCode,this.data.languageId,this.data.plantId,this.data.userRoleId).subscribe(res => {
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
    this.form.controls.resetPassword.patchValue(true);
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.userId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });

      if(this.form.controls.createHhtUser.value == true){
        this.sub.add(this.HhtuserService.Create(this.form.getRawValue()).subscribe(res => {
          this.toastr.success(res.userId + " HHT Saved Successfully!","Notification",{
            timeOut: 2000,
            progressBar: false,
          });
        }))
      }


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













 



