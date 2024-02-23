import { Component, OnInit, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { UserroleService } from '../userrole.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from "@angular/common";
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { AuthService } from 'src/app/core/core';
import { ToastrService } from 'ngx-toastr';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-user-role-new',
  templateUrl: './user-role-new.component.html',
  styleUrls: ['./user-role-new.component.scss']
})
export class UserRoleNewComponent implements OnInit {

  btntext = "Save";
  menuList: any[] = [];
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
    private fb: FormBuilder, private auth: AuthService,   private masterService: MasterService,  public toastr: ToastrService,) { }

  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;  
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
     console.log('show:' + this.showFloatingButtons);
  }


  sub = new Subscription();
  js: any;
  ngOnInit(): void {
  //  this.reset();
    this.getModuleID()
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
    if (this.js.pageflow != 'new') {
      this.fill(this.js);

      this.form.controls.roleId.disable();
     
    }
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
      this.sub.add(this.service.Get(data.code.roleId , data.code.warehouseId, data.code.companyCodeId, data.code.plantId, data.code.languageId).subscribe(res => {
        this.form.patchValue(res[0]);
        this.form.controls.roleStatus.patchValue(this.form.controls.roleStatus.value ? 'Active' : 'Inactive');
        this.menuList = [];
        this.menuList.push({
          mainMenu: "Setup",
          Menu: res.filter((x: any) => x.menuId == 1)
        });
        this.spin.hide();
      }, err => {
        this.cs.commonerror(err);
        this.spin.hide();
      }));
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

  checkUncheckAll(menu: any, event: any) {
    if (event.checked) { this.menuList.find(x => x.mainMenu == menu).Menu.forEach((x: any) => { x.createUpdate = true, x.delete = true, x.view = true }) }
    else { this.menuList.find(x => x.mainMenu == menu).Menu.forEach((x: any) => { x.createUpdate = false, x.delete = false, x.view = false }) }
  }

  onChange(menu: any, event: any) {
    menu.view = event.checked; menu.createUpdate = event.checked; menu.delete = event.checked;
  }

  form = this.fb.group({
    roleStatus: ["Active", [Validators.required]],
    roleId: [,],
    userRoleName: [, [Validators.required]],


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
        y.languageId = "EN"
        y.roleStatus = this.form.controls.roleStatus.value
        // if (this.pageflow != "New")
        //   y.userRoleId = this.form.controls.userRoleId.value
        y.edit = true;
        y.userRoleName = this.form.controls.userRoleName.value,
      //  y.ddsd = 22,

        data.push(y);
      });
    })
    console.log(JSON.stringify(this.menuList));

    this.cs.notifyOther(false);
    this.spin.show();

    if (this.js.pageflow != "New") {
      this.sub.add(this.service.Update(data, this.form.controls.roleId.value, this.auth.warehouseId).subscribe(res => {
        this.toastr.success(this.form.controls.roleId.value + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.location.back();
      }, err => {

        this.form.controls.roleStatus.patchValue(this.form.controls.roleStatus.value ? 'Active' : 'Inactive');
        this.cs.commonerror(err);
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

        this.cs.commonerror(err);
        this.spin.hide();

      }));
    }
  };


  getModuleID(){
    this.masterService.searchModule({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.menuList = [];
      this.menuList.push({
        mainMenu: "Setup",
        Menu: res.filter((x: any) => x.menuId == 1)
      });
      // this.menuList.push({
      //   mainMenu: "Product Master",
      //   Menu: res.filter((x: any) => x.menuId == 2)
      // });
    });

  }
}


