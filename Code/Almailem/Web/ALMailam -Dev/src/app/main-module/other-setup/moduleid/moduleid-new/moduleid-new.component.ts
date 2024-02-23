import { Component, Inject, OnInit, ViewChild } from '@angular/core';
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
import { ActivatedRoute, Router } from '@angular/router';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-moduleid-new',
  templateUrl: './moduleid-new.component.html',
  styleUrls: ['./moduleid-new.component.scss']
})
export class ModuleidNewComponent implements OnInit {

  module: any[] = [];
  selectedmoduleNew : any[] = [];
  @ViewChild('moduleTag') moduleTag: Table | any;
  
 
  newAddedModules: any[] = [];
  newSelectedModules : any[] = [];
  @ViewChild('newModuleTag') newModuleTag: Table | any;

  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  disabled = false;
  step = 0;
  //dialogRef: any;
  isLinear = false;
 toggleFloat() {

      this.isShowDiv = !this.isShowDiv;
      this.toggle = !this.toggle;
  
      if (this.icon === 'expand_more') {
        this.icon = 'chevron_left';
      } else {
        this.icon = 'expand_more'
      }
      this.showFloatingButtons = !this.showFloatingButtons;
  
    }
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
  createUpdate: [false],
  createdBy: [],
  createdOn: [],
  delete: [false],
  deletionIndicator: [],
  languageId: [,Validators.required],
  menuId: [,Validators.required],
  menuName: [],
  moduleDescription: [],
  moduleId: [, Validators.required],
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
  view: [false],
  warehouseId: [,Validators.required],
  warehouseIdAndDescription: [],
  updatedOnFE: [],
  createdOnFE: [],
  });

  panelOpenState = false;

  moduleID: any[] = [];
  constructor(
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private cas: CommonApiService,
    private service: ModuleidService,
    private masterService: MasterService,
    private route: ActivatedRoute,
    private router: Router,
  ) { 

    this.moduleID = [
      {value: 1000, label: 'Dashboard'},
      {value: 2000, label: 'Setup'},
      {value: 3000, label: 'Master'},
      {value: 4000, label: 'Inbound'},
      {value: 5000, label: 'Make & Change'},
      {value: 6000, label: 'Outbound'},
  ];

  }
  
  js: any = {}
  ngOnInit(): void {

    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);

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
    if (this.js.pageflow != 'New') {
      this.form.controls.moduleId.disable();
      this.form.controls.languageId.disable();
     this.form.controls.plantId.disable();
      this.form.controls.companyCodeId.disable();
      this.form.controls.warehouseId.disable();
      if (this.js.pageflow == 'Display')
        this.form.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  menuID: any[] = [];
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.languageId,this.js.companyCodeId,this.js.plantId).subscribe(res => {
    //  this.form.patchValue(res, { emitEvent: false });
    this.form.controls.moduleId.disable();
    this.form.controls.languageId.patchValue(res[0].languageId);
    this.form.controls.warehouseId.patchValue(res[0].warehouseId);
    this.form.controls.companyCodeId.patchValue(res[0].companyCodeId);
    this.form.controls.plantId.patchValue(res[0].plantId);
    this.form.controls.menuId.patchValue(res[0].menuId);
    this.form.controls.moduleDescription.patchValue(res[0].moduleDescription);
    this.form.controls.moduleId.patchValue(res[0].moduleId);
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));

    this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, menuId: [this.form.controls.menuId.value], plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(menures => {
      menures.forEach(x=> {
        x.addModule = false;
      })
      let combined = this.cs.removeDuplicateObj(res, menures);
      console.log(combined)
      this.module = combined;
      this.selectedmoduleNew  = res;
    },err => {
      this.cs.commonerrorNew(err);
    });
    let menu = this.cs.removeDuplicatesFromArrayList(res, 'menuId')
    menu.forEach(x => {
      this.menuID.push(x.menuId);
    })
    this.form.controls.menuId.patchValue(this.menuID);
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
  this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
    this.menuList = [];
    res.forEach(element => {
      this.menuList.push({value: element.menuId, label: element.menuId + '-' + element.menuName});
      this.menuList = this.cs.removeDuplicatesFromArrayNewstatus(this.menuList);
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
    // this.companyidList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.companyid.key);
    // this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);
   
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
    this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
      this.menuList = [];
      res.forEach(element => {
        this.menuList.push({value: element.menuId, label: element.menuId + '-' + element.menuName});
        this.menuList = this.cs.removeDuplicatesFromArrayNewstatus(this.menuList);
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
    this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, languageId: [value.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
      this.menuList = [];
      res.forEach(element => {
        this.menuList.push({value: element.menuId, label: element.menuId + '-' + element.menuName});
        this.menuList = this.cs.removeDuplicatesFromArrayNewstatus(this.menuList);
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
    
    this.masterService.searchMenu({companyCodeId: value.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
      this.menuList = [];
      res.forEach(element => {
              this.menuList.push({value: element.menuId, label: element.menuId + '-' + element.menuName});
              this.menuList = this.cs.removeDuplicatesFromArrayNewstatus(this.menuList);
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
      this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
        this.menuList = [];
        res.forEach(element => {
                this.menuList.push({value: element.menuId, label: element.menuId + '-' + element.menuName});
                this.menuList = this.cs.removeDuplicatesFromArrayNewstatus(this.menuList);
        });
      });
  }


  onWarehouseChange(value){
    this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value], warehouseId: value.value}).subscribe(res => {
      this.menuList = [];
      res.forEach(element => {
              this.menuList.push({value: element.menuId, label: element.menuId + '-' + element.menuName});
              this.menuList = this.cs.removeDuplicatesFromArrayNewstatus(this.menuList);
      });
    });
  }


  selectedMenuList: any[] = [];
  onMenuIdChange(value){
    console.log(value)
      if(this.js.pageflow =='New'){
        this.module = [];
        this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, menuId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
          this.module = res;
          this.selectedmoduleNew  = this.module;
        })  
      }
      if(this.js.pageflow != 'New'){
        this.masterService.searchMenu({companyCodeId: this.form.controls.companyCodeId.value, menuId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
          res.forEach(x=> {
            x.addModule = false;
          })
          const filteredArray1: any[] = res.filter(obj => !this.module.some(o => o.subMenuId === obj.subMenuId));
          this.newAddedModules = filteredArray1;
        //  this.newSelectedModules  = this.newAddedModules;  
        })
      }

   
    

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

  this.form.controls.createUpdate.patchValue(false);
  this.form.controls.delete.patchValue(false);
  this.form.controls.view.patchValue(false);

  this.selectedmoduleNew.forEach(x => {
    x.moduleDescription = this.form.controls.moduleDescription.value;
    x.moduleId = this.form.controls.moduleId.value;
    x.createUpdate = false;
    x.delete = false;
    x.view = false;
  })
  
  if (this.js.code) {
    // this.sub.add(this.service.Delete(this.js.code,this.js.warehouseId,this.js.languageId,this.js.companyCodeId,this.js.plantId).subscribe((res) => {
    //   this.sub.add(this.service.Create(this.selectedmoduleNew).subscribe(res => {
    //     this.toastr.success(res.moduleId + " Saved Successfully!","Notification",{
    //       timeOut: 2000,
    //       progressBar: false,
    //     });
    //     this.router.navigate(['/main/otherSetup/moduleid']);
    //   }, err => {
    //     this.cs.commonerrorNew(err);
    //     this.spin.hide();
    
    //   }));
    //   this.spin.hide();
    // }, err => {
    //   this.cs.commonerrorNew(err);
    //   this.spin.hide();
    // }));

    this.sub.add(this.service.Update(this.changedRow, this.js.code,this.js.warehouseId,this.js.languageId,this.js.companyCodeId,this.js.plantId).subscribe(res => {
      this.toastr.success(this.js.code + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.router.navigate(['/main/otherSetup/moduleid']);
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.selectedmoduleNew).subscribe(res => {
      this.toastr.success(res.moduleId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.router.navigate(['/main/otherSetup/moduleid']);
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


   changedRow: any[] = [];
  unselectRow(event, rowData: any) {
    console.log(event)
    if(event.checked == false){
      this.selectedmoduleNew = this.selectedmoduleNew.filter(row => row !== rowData);
      this.changedRow.push(rowData);
      this.changedRow = this.cs.removeDuplicatesFromArrayList(this.changedRow, 'subMenuId');
    }
    if(event.checked == true){
      this.selectedmoduleNew.push(rowData);
      this.selectedmoduleNew = this.cs.removeDuplicatesFromArrayList(this.selectedmoduleNew, 'subMenuId');

      this.changedRow.push(rowData);
      this.changedRow = this.cs.removeDuplicatesFromArrayList(this.changedRow, 'subMenuId');
    }
  }
  unselectRow1(event, rowData: any) {
    console.log(event)
    if(event.checked == false){
      this.newSelectedModules = this.newSelectedModules.filter(row => row !== rowData);
      this.changedRow.push(rowData);
      this.changedRow = this.cs.removeDuplicatesFromArrayList(this.changedRow, 'subMenuId');
    }
    if(event.checked == true){
      this.newSelectedModules.push(rowData);
      this.newSelectedModules = this.cs.removeDuplicatesFromArrayList(this.newSelectedModules, 'subMenuId');

      this.changedRow.push(rowData);
      this.changedRow = this.cs.removeDuplicatesFromArrayList(this.changedRow, 'subMenuId');
    }
  }

}













 
