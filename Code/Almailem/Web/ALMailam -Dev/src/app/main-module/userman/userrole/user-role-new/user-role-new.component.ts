import { Component, OnInit, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { UserroleService } from '../userrole.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from "@angular/common";
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { AuthService } from 'src/app/core/core';
import { ToastrService } from 'ngx-toastr';
import { MasterService } from 'src/app/shared/master.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { WarehouseService } from 'src/app/main-module/setup-organisation/warehouse/warehouse.service';

@Component({
  selector: 'app-user-role-new',
  templateUrl: './user-role-new.component.html',
  styleUrls: ['./user-role-new.component.scss']
})
export class UserRoleNewComponent implements OnInit {

  disabled =false;
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

  panelOpenState = false;

  btntext = "Save";
  menuList: any[] = [];
  moduleResule: any[] = [];
  menuListraw: any[] =
    [
      {
        mainMenu: "Shortcuts", Menu: [  
        { menuId: 1, subMenuId: 1000, referenceField2: 'Setup', referenceField1: "Company", createUpdate: true, delete: true, view: true, },
        { menuId: 1, subMenuId: 1001, referenceField2: 'Setup', referenceField1: "Plant", createUpdate: true, delete: true, view: true, },
        { menuId: 1, subMenuId: 1002, referenceField2: 'Setup', referenceField1: "Warehouse", createUpdate: true, delete: true, view: true, },
        { menuId: 1, subMenuId: 1003, referenceField2: 'Setup', referenceField1: "Floor", createUpdate: true, delete: true, view: true, },
        { menuId: 1, subMenuId: 1004, referenceField2: 'Setup', referenceField1: "Storage Section", createUpdate: true, delete: true, view: true, }
        ]
      },
    ];
    
    email = new FormControl('', [Validators.required, Validators.email]);
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  constructor(private service:UserroleService ,
    private spin: NgxSpinnerService, private cs : CommonService, private route: ActivatedRoute, private location: Location,
    private fb: FormBuilder, private auth: AuthService,   private masterService: MasterService,  public toastr: ToastrService, private router: Router,
    private cas: CommonApiService, private warehouse: WarehouseService) { }

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


  sub = new Subscription();
  js: any;
  modeOfImplementation: any;
  ngOnInit(): void {
  //  this.reset();

      let obj: any = {};
    obj.companyId = this.auth.companyId;
    obj.plantId = this.auth.plantId;
    obj.languageId = this.auth.languageId;
    obj.warehouseId = this.auth.warehouseId;
    this.warehouse.search(obj).subscribe(res => {
      this.modeOfImplementation = res[0].modeOfImplementation;
    })


    this.dropdownlist();
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
    console.log(this.js.pageflow)
    if (this.js.pageflow != 'New') {
      this.fill(this.js); 
      this.form.controls.roleId.disable();
    }
    if (this.js.pageflow == 'New' && this.auth.userTypeId != 1) {
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId); 
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);

      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
      this.form.controls.companyCodeId.disable();
      this.getModuleID();
    }
  }

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

    this.masterService.searchstorageclass({companyCodeId: this.auth.companyCurrency, plantId: this.auth.plantId, warehouseId: this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
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

  isbtntext = true;
  fill(data: any) {
    if (data.pageflow != 'New') {
      this.btntext = "Update";
      if (data.pageflow == 'Display') {
        this.form.disable();
        this.isbtntext = false;
      }

      this.spin.show();
      this.masterService.searchModule({companyCodeId: data.code.companyCodeId, plantId: data.code.plantId, languageId: [data.code.languageId], warehouseId: data.code.warehouseId}).subscribe(moduleRes => {
      this.sub.add(this.service.Get(data.code.roleId , data.code.warehouseId, data.code.companyCodeId, data.code.plantId, data.code.languageId).subscribe(res => {
        this.form.patchValue(res[0]);
        let combined = this.cs.removeDuplicateObj(res, moduleRes);
        this.form.controls.roleStatus.patchValue(this.form.controls.roleStatus.value ? 'Active' : 'Inactive');
        this.menuList = [];
        this.menuList.push({
          mainMenu: "Setup",
          Menu: combined.filter((x: any) => x.moduleId == 2000)
        });
        this.menuList.push({
          mainMenu: "Master",
          Menu: combined.filter((x: any) => x.moduleId == 3000)
        });
        this.menuList.push({
          mainMenu: "Inbound",
          Menu: combined.filter((x: any) => x.moduleId == 4000)
        });
        this.menuList.push({
          mainMenu: "Make & Change",
          Menu: combined.filter((x: any) => x.moduleId == 5000)
        });
        this.menuList.push({
          mainMenu: "Outbound",
          Menu: combined.filter((x: any) => x.moduleId == 6000)
        });
        this.menuList.push({
          mainMenu: "Delivery",
          Menu: combined.filter((x: any) => x.moduleId == 9000)
        });
        this.menuList.push({
          mainMenu: "Stock Count",
          Menu: combined.filter((x: any) => x.moduleId == 7000)
        });
        this.menuList.push({
          mainMenu: "Report",
          Menu: combined.filter((x: any) => x.moduleId == 8000)
        });
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));    
    });
    }
  }
  reset() {
    this.menuList = this.menuListraw;
  }

  back() {
    this.location.back();
  }
  resetAll(){
    this.menuList.forEach((x: any) => { 
      x.Menu.forEach(element => {
        element.createUpdate = false, element.delete = false, element.view = false
      });
    })
  }

  addScreens(){
    let paramdata = "";
    paramdata = this.cs.encrypt({ lines: this.menuList,});
    this.router.navigate(['/main/userman/userrole-addnew/' + paramdata]);
  }

  checkUncheckAll(menu: any, event: any) {
    if (event.checked) { this.menuList.find(x => x.mainMenu == menu).Menu.forEach((x: any) => { x.createUpdate = true, x.delete = true, x.view = true}) }
    else { this.menuList.find(x => x.mainMenu == menu).Menu.forEach((x: any) => { x.createUpdate = false, x.delete = false, x.view = false}) }
  }


  onChange(menu: any, event: any) {
    menu.view = event.checked; menu.createUpdate = event.checked; menu.delete = event.checked;
  }

  form = this.fb.group({
    roleStatus: ["Active", [Validators.required]],
    roleId: [,],
    userRoleName: [, [Validators.required]],
    languageId: [, [Validators.required]],
    companyCodeId: [, [Validators.required]],
    plantId: [, [Validators.required]],
    warehouseId: [, [Validators.required]],


  });
  
  submitted = false;
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }

  languageidList: any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
 
  storageclassidList:any[]=[];
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
    this.masterService.searchModule({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [this.auth.languageId], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
      this.moduleResule = [];
      this.moduleResule.push(res);
      this.menuList = [];
      this.menuList.push({
        mainMenu: "Setup",
        Menu: res.filter((x: any) => x.moduleId == 2000)
      });
      this.menuList.push({
        mainMenu: "Master",
        Menu: res.filter((x: any) => x.moduleId == 3000)
      });
      this.menuList.push({
        mainMenu: "Inbound",
        Menu: res.filter((x: any) => x.moduleId == 4000)
      });
      this.menuList.push({
        mainMenu: "Make & Change",
        Menu: res.filter((x: any) => x.moduleId == 5000)
      });
      this.menuList.push({
        mainMenu: "Outbound",
        Menu: res.filter((x: any) => x.moduleId == 6000)
      });
      this.menuList.push({
        mainMenu: "Delivery",
        Menu: res.filter((x: any) => x.moduleId == 9000)
      });
      this.menuList.push({
        mainMenu: "Stock Count",
        Menu: res.filter((x: any) => x.moduleId == 7000)
      });
      this.menuList.push({
        mainMenu: "Report",
        Menu: res.filter((x: any) => x.moduleId == 8000)
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

    this.masterService.searchModule({companyCodeId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
      this.moduleResule = [];
      this.moduleResule.push(res);
      this.menuList = [];
      this.menuList.push({
        mainMenu: "Setup",
        Menu: res.filter((x: any) => x.moduleId == 2000)
      });
      this.menuList.push({
        mainMenu: "Master",
        Menu: res.filter((x: any) => x.moduleId == 3000)
      });
      this.menuList.push({
        mainMenu: "Inbound",
        Menu: res.filter((x: any) => x.moduleId == 4000)
      });
      this.menuList.push({
        mainMenu: "Make & Change",
        Menu: res.filter((x: any) => x.moduleId == 5000)
      });
      this.menuList.push({
        mainMenu: "Outbound",
        Menu: res.filter((x: any) => x.moduleId == 6000)
      });
      this.menuList.push({
        mainMenu: "Delivery",
        Menu: res.filter((x: any) => x.moduleId == 9000)
      });
      this.menuList.push({
        mainMenu: "Stock Count",
        Menu: res.filter((x: any) => x.moduleId == 7000)
      });
      this.menuList.push({
        mainMenu: "Report",
        Menu: res.filter((x: any) => x.moduleId == 8000)
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
      this.masterService.searchModule({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, languageId: [this.form.controls.languageId.value], warehouseId: this.form.controls.warehouseId.value}).subscribe(res => {
        this.moduleResule = [];
        this.moduleResule.push(res);
        this.menuList = [];
        this.menuList.push({
          mainMenu: "Setup",
          Menu: res.filter((x: any) => x.moduleId == 2000)
        });
        this.menuList.push({
          mainMenu: "Master",
          Menu: res.filter((x: any) => x.moduleId == 3000)
        });   
        this.menuList.push({
          mainMenu: "Inbound",
          Menu: res.filter((x: any) => x.moduleId == 4000)
        });
        this.menuList.push({
          mainMenu: "Make & Change",
          Menu: res.filter((x: any) => x.moduleId == 5000)
        });
        this.menuList.push({
          mainMenu: "Outbound",
          Menu: res.filter((x: any) => x.moduleId == 6000)
        });
        this.menuList.push({
          mainMenu: "Delivery",
          Menu: res.filter((x: any) => x.moduleId == 9000)
        });
        this.menuList.push({
          mainMenu: "Stock Count",
          Menu: res.filter((x: any) => x.moduleId == 7000)
        });
        this.menuList.push({
          mainMenu: "Report",
          Menu: res.filter((x: any) => x.moduleId == 8000)
        });
      });
  }

  onWarehouseChange(value){
    this.masterService.searchModule({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value], warehouseId: value.value}).subscribe(res => {
      this.moduleResule = [];
      this.moduleResule.push(res);
      this.menuList = [];
      this.menuList.push({
        mainMenu: "Setup",
        Menu: res.filter((x: any) => x.moduleId == 2000)
      });
      this.menuList.push({
        mainMenu: "Master",
        Menu: res.filter((x: any) => x.moduleId == 3000)
      });
      this.menuList.push({
        mainMenu: "Inbound",
        Menu: res.filter((x: any) => x.moduleId == 4000)
      });
      this.menuList.push({
        mainMenu: "Make & Change",
        Menu: res.filter((x: any) => x.moduleId == 5000)
      });
      this.menuList.push({
        mainMenu: "Outbound",
        Menu: res.filter((x: any) => x.moduleId == 6000)
      });
      this.menuList.push({
        mainMenu: "Delivery",
        Menu: res.filter((x: any) => x.moduleId == 9000)
      });
      this.menuList.push({
        mainMenu: "Stock Count",
        Menu: res.filter((x: any) => x.moduleId == 7000)
      });
      this.menuList.push({
        mainMenu: "Report",
        Menu: res.filter((x: any) => x.moduleId == 8000)
      });
    });
  }
  submit() {
    this.form.controls.roleStatus.patchValue(this.form.controls.roleStatus.value == 'Active' ? true : false);

    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill the required fields to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.form.updateValueAndValidity;
    let data: any[] = [];
    this.menuList.forEach((x: any) => {
      x.Menu.forEach((y: any) => {
        y.languageId = this.form.controls.languageId.value;
        y.companyCodeId = this.form.controls.companyCodeId.value;
        y.plantId = this.form.controls.plantId.value;
        y.warehouseId = this.form.controls.warehouseId.value;
        y.roleStatus = this.form.controls.roleStatus.value
        y.edit = true;
        y.userRoleName = this.form.controls.userRoleName.value,
        data.push(y);
      });
    })

    this.cs.notifyOther(false);
    this.spin.show();

    if (this.js.pageflow != "New") {
      this.sub.add(this.service.Update(data, this.form.controls.roleId.value, this.form.controls.warehouseId.value, this.form.controls.companyCodeId.value, this.form.controls.plantId.value, this.form.controls.languageId.value).subscribe(res => {
        this.toastr.success(this.form.controls.roleId.value + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.location.back();
      }, err => {

        this.form.controls.roleStatus.patchValue(this.form.controls.roleStatus.value ? 'Active' : 'Inactive');
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(data).subscribe(res => {
        this.toastr.success(res[0].roleId + " saved successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.location.back();

      }, err => {
        this.form.controls.roleStatus.patchValue(this.form.controls.roleStatus.value ? 'Active' : 'Inactive');

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  };


  getModuleID(){
    this.masterService.searchModule({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, languageId: [this.auth.languageId], warehouseId: this.auth.warehouseId}).subscribe(res => {
      this.moduleResule = [];

      if (this.modeOfImplementation == "LEAN") {
        let Result = res.filter(x => x.referenceField1 == "LEAN");
        this.moduleResule = Result;
      }
      else {
        this.moduleResule = res;
      }

      this.menuList = [];
      this.menuList.push({
        mainMenu: "Setup",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 2000)
      });
      this.menuList.push({
        mainMenu: "Master",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 3000)
      });
      this.menuList.push({
        mainMenu: "Inbound",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 4000)
      });
      this.menuList.push({
        mainMenu: "Make & Change",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 5000)
      });
      this.menuList.push({
        mainMenu: "Outbound",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 6000)
      });
      this.menuList.push({
        mainMenu: "Delivery",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 9000)
      });
      this.menuList.push({
        mainMenu: "Stock Count",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 7000)
      });
      this.menuList.push({
        mainMenu: "Report",
        Menu: this.moduleResule.filter((x: any) => x.moduleId == 8000)
      });
    });

  }

  
  getModuleIDSuperAdmin(){
    this.masterService.searchModule({companyCodeId:  this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [ this.form.controls.languageId.value], warehouseId:  this.form.controls.warehouseId.value}).subscribe(res => {
      this.moduleResule = [];
      this.moduleResule.push(res);
      this.menuList = [];
      this.menuList.push({
        mainMenu: "Setup",
        Menu: res.filter((x: any) => x.moduleId == 2000)
      });
      this.menuList.push({
        mainMenu: "Master",
        Menu: res.filter((x: any) => x.moduleId == 3000)
      });
      this.menuList.push({
        mainMenu: "Inbound",
        Menu: res.filter((x: any) => x.moduleId == 4000)
      });
      this.menuList.push({
        mainMenu: "Make & Change",
        Menu: res.filter((x: any) => x.moduleId == 5000)
      });
      this.menuList.push({
        mainMenu: "Outbound",
        Menu: res.filter((x: any) => x.moduleId == 6000)
      });
      this.menuList.push({
        mainMenu: "Delivery",
        Menu: res.filter((x: any) => x.moduleId == 9000)
      });
      this.menuList.push({
        mainMenu: "Stock Count",
        Menu: res.filter((x: any) => x.moduleId == 7000)
      });
      this.menuList.push({
        mainMenu: "Report",
        Menu: res.filter((x: any) => x.moduleId == 8000)
      });
    });

  }
}


