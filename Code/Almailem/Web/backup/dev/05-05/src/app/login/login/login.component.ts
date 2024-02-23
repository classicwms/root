import { Component, Injectable, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/core/core';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserprofileService } from 'src/app/main-module/userman/userprofile/userprofile.service';


@Injectable({
  providedIn: 'root'
})

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  lgForm: any;
  error: any;
  hide = true;
  title = environment.title;
  loginTrue: boolean;
  resetTrue: boolean;
  warehouseSelection: boolean;
  userResult: any;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    public dialog: MatDialog,
    private auth: AuthService,
    public toastr: ToastrService,
    private http: HttpClient,
    private spin: NgxSpinnerService,
    private usermanagement: UserprofileService
  ) {  this.currentEnv = environment.name; }

  currentEnv: string;
  prod: boolean;
  
  ngOnInit() {

    if(this.currentEnv == 'prod'){
      this.prod = true;
    }else{
      this.prod = false;
    }

    sessionStorage.clear();
    localStorage.clear();

    this.inForm();
  }

  wareHouseList: any[] = [];
  inForm() {
    this.lgForm = this.fb.group({
      userName: ["", Validators.required],
      password: ["", Validators.required],
      newPassword: ["", ],
      confirmPassword: ["", ],
      selectedWarehouse: ["", ],
    });
    this.loginTrue = true;
    this.resetTrue = false;
    this.warehouseSelection = false;
  }

  login() {
    this.spin.show();
    sessionStorage.clear();
    sessionStorage.clear();
    // stop here if form is invalid
    if (this.lgForm.invalid) {
      this.error = "Please fill required fields to continue";

      this.toastr.error(
        "Invalid User name or Password!",
        "Login Failed",{
          timeOut: 2000,
          progressBar: false,
        }
      );  
      return;
    }
    this.http.get<any>(`/wms-idmaster-service/login?name=${this.lgForm.controls.userName.value}&password=${this.lgForm.controls.password.value}`).subscribe((res) => {
      this.userResult = res;
      sessionStorage.setItem("user", JSON.stringify(res));
    this.usermanagement.findProfile({userId: [this.lgForm.controls.userName.value]}).subscribe(findResult => {
   
      if(findResult.length > 1){
        this.warehouseSelection = true;
        this.resetTrue = false;
        this.loginTrue = false;

        findResult.forEach(element => {
          this.wareHouseList.push({value: element.warehouseId, label: element.warehouseId, result: element});
        });
        this.spin.hide();
      }
      else{
        this.auth.login(this.lgForm.value);
        this.spin.hide();
      }
    });
    },
    (rej) => {
      this.spin.hide();
      this.toastr.error(rej.error.error, 'Notification');
      setTimeout(() => {
        window.location.reload();
    }, 1000);
    })
  };


  confirmPassword(){
  this.spin.show();
    if(this.lgForm.controls.newPassword.value != this.lgForm.controls.confirmPassword.value){
      this.toastr.error(
        "Password didn't match",
        "Login Failed",{
          timeOut: 2000,
          progressBar: false,
        }
      );
      this.spin.hide();
      return;
    }
    if(this.lgForm.controls.newPassword.value && this.lgForm.controls.newPassword.value){
      this.http.patch<any>(`/wms-idmaster-service/usermanagement/${this.userResult.userId}?warehouseId=${this.userResult.warehouseId}`, {resetPassword: false, password: this.lgForm.controls.confirmPassword.value, companyCode: this.auth.companyId, plantId: this.auth.plantId, languageId: this.auth.languageId, userRoleId: this.auth.userRoleId}).subscribe(userres =>{
        this.toastr.success(
          "Password changed Successfully",
          "Notification"
        );
        this.resetTrue = false;
        this.loginTrue = true;
        this.spin.hide();
        setTimeout(() => {
          window.location.reload();
      }, 1000);
      });
    }else{
      this.toastr.error(
        "Please fill the fields to continue",
        "Login Failed",{
          timeOut: 2000,
          progressBar: false,
        }
      );
      this.spin.hide()
      return;
    }
  }
  authResult: any = {};
  onWarehouseChange(value){
   this.authResult = (value.result);
  }
  warehouseConfirm(){

    if(this.authResult.resetPassword == true){
      this.resetTrue = true;
      this.loginTrue = false;
      this.warehouseSelection = false;
      this.userResult = null;
      this.userResult = this.authResult;
      this.spin.hide();
    }
    else{
      this.auth.login1(this.lgForm.value, this.authResult);
    }
  }
}
