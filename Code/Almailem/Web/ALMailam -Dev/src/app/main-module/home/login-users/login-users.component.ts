import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { UpdateInventoryComponent } from '../update-inventory/update-inventory.component';
import { Table } from 'primeng/table';
import { UserprofileService } from '../../userman/userprofile/userprofile.service';

@Component({
  selector: 'app-login-users',
  templateUrl: './login-users.component.html',
  styleUrls: ['./login-users.component.scss']
})
export class LoginUsersComponent implements OnInit {

  constructor(private spin: NgxSpinnerService, public cs: CommonService, public auth: AuthService, private UserprofileService: UserprofileService,
    public toastr: ToastrService,
    public dialogRef: MatDialogRef<UpdateInventoryComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }



  loginUser: any[] = [];
  selectedloginUser: any[] = [];
  @ViewChild('loginUserTag') loginUserTag: Table | any;

  loginSelected = "portal";

  ngOnInit(): void {
    this.loginSelected = 'portal';
    this.search();
  }

  search() {
    this.loginUser = [];
    let obj: any = {};
    if(this.loginSelected == 'portal'){ obj.portalLoggedIn = true;} else{ obj.hhtLoggedIn = true;}
     
    this.UserprofileService.search(obj).subscribe(res => {
      let result = res.filter(x => x.userId != 'SuperAdmin')
      this.loginUser = result;

      if(result.length == 0){
        this.toastr.warning("No records found!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
      }
    })
  }
  submit() {
    this.spin.show();
    let obj: any = {};
      obj.portalLoggedIn = false,
      obj.companyCode = this.selectedloginUser[0].companyId,
      obj.plantId = this.selectedloginUser[0].plantId,
      obj.languageId = this.selectedloginUser[0].languageId,
      obj.userRoleId = this.selectedloginUser[0].userRoleId,

      this.UserprofileService.Update(obj, this.selectedloginUser[0].userId, this.selectedloginUser[0].warehouseId, this.selectedloginUser[0].companyCode,
        this.selectedloginUser[0].languageId, this.selectedloginUser[0].plantId, this.selectedloginUser[0].userRoleId,).subscribe(res => {
          this.toastr.success("User logged out successfully!", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          this.search();
          this.spin.hide();
        }, err => {
          this.spin.hide();
          this.cs.commonerrorNew(err);
        })
  }

  onChange() {
    const choosen = this.selectedloginUser[this.selectedloginUser.length - 1];
    this.selectedloginUser.length = 0;
    this.selectedloginUser.push(choosen);
  }

}
