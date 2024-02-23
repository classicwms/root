import { Subscription } from "rxjs";
import { Injectable, OnDestroy } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { catchError, switchMap } from "rxjs/operators";
@Injectable()
export class AuthService implements OnDestroy {
  redirectUrl: any;
  user: any;
  userData!: boolean;
  token!: string;
  // Subscription
  sub = new Subscription();
  constructor(
    private http: HttpClient,
    private router: Router,
    private spin: NgxSpinnerService,
    public toastr: ToastrService

  ) {

    this.userData = true;


    // if (JSON.parse(sessionStorage.getItem("user") as '{}')) {
    //   this.userData = true;
    // }
  }

  get isLoggedIn() {
    return this.userData ? true : false;
  }

  login(user: { userName: string; password: any; }) {
    localStorage.clear();
    sessionStorage.clear();
    this.spin.show();

    return new Promise((resolve, reject) => {
      this.sub.add(
        this.http.get<any>(`/wms-idmaster-service/login?name=${user.userName}&password=${user.password}`).subscribe(
          (res) => {
            console.log(res);
            this.spin.hide();
         
            // let menu = [1000, 1001, 1002, 1003,1005,1006, 1004, 2101, 2102, 2202, 2203, 2201,];
            let menu = [1000,
              1001, 2101, 2102, 2103, 2104, 2105, 2106,
              1002, 2201, 2202, 2203, 2204, 2205, 2206, 2207, 2208, 2209, 2210,
              1003, 2301, 2302,
              1004, 2401, 2402, 2403, 2404, 2905,
              1005, 2501, 2502, 2503,
              1006, 2601, 2602,
              1007, 2701,
              1013, 3101,
              1008, 2801, 2802, 2803, 2805,
              1009, 2901, 2902, 2903, 2904,
              1010, 3001, 3002, 3003, 3004, 3005, 3006,
              1011, 1101, 1102, 1103, 1104, 1105];
            sessionStorage.setItem('menu', menu.toString());
            sessionStorage.setItem("user", JSON.stringify(res))

            // if(res.isLoggedIn == true){
            //   this.toastr.error(
            //     "User already logged in",
            //     "Notification",{
            //       timeOut: 2000,
            //       progressBar: false,
            //     }
            //   );
            //   this.spin.hide();
            //   return;
            // }else{
            //   this.http.patch<any>(`/wms-idmaster-service/usermanagement/${res.userId}?warehouseId=${res.warehouseId}`, {isLoggedIn: true, companyCode: res.companyCode, plantId: res.plantId, languageId: res.languageId, userRoleId: res.userRoleId}).subscribe(userres =>{
            //   });
            // }
            
            
            // let resp: any = res.result.response[0];
            // // For token
            // sessionStorage.setItem("token", res.result.bearerToken);
            // sessionStorage.setItem("token", res.result.bearerToken);

            this.router.navigate(["main/dashboard"]);

          }
          ,
          (rej) => {
            this.spin.hide();
            this.toastr.error(rej.error.error, 'Notification');
            resolve(false);
            setTimeout(() => {
              window.location.reload();
          }, 1000);
          }
        )
      );
    });







  }
  login1(user: { userName: string; password: any; }, authResult) {
    localStorage.clear();
    sessionStorage.clear();
    this.spin.show();

    return new Promise((resolve, reject) => {
      this.sub.add(
        this.http.get<any>(`/wms-idmaster-service/login?name=${user.userName}&password=${user.password}`).subscribe(
          (res) => {
            console.log(res);
            this.spin.hide();
         
            // let menu = [1000, 1001, 1002, 1003,1005,1006, 1004, 2101, 2102, 2202, 2203, 2201,];
            let menu = [1000,
              1001, 2101, 2102, 2103, 2104, 2105, 2106,
              1002, 2201, 2202, 2203, 2204, 2205, 2206, 2207, 2208, 2209, 2210,
              1003, 2301, 2302,
              1004, 2401, 2402, 2403, 2404, 2905,
              1005, 2501, 2502, 2503,
              1006, 2601, 2602,
              1007, 2701,
              1013, 3101,
              1008, 2801, 2802, 2803, 2805,
              1009, 2901, 2902, 2903, 2904,
              1010, 3001, 3002, 3003, 3004, 3005, 3006,
              1011, 1101, 1102, 1103, 1104, 1105];
            sessionStorage.setItem('menu', menu.toString());
            sessionStorage.setItem("user", JSON.stringify(authResult))

            // if(authResult.isLoggedIn == true)  {
            //   this.toastr.error(
            //     "User already logged in",
            //     "Notification",{
            //       timeOut: 2000,
            //       progressBar: false,
            //     }
            //   );
            //   this.spin.hide();
            //   return;
            // }
            // else{
            //   this.http.patch<any>(`/wms-idmaster-service/usermanagement/${res.userId}?warehouseId=${res.warehouseId}`, {isLoggedIn: true, companyCode: res.companyCode, plantId: res.plantId, languageId: res.languageId, userRoleId: res.userRoleId}).subscribe(userres =>{
            //   });
            // }
            
            
            // let resp: any = res.result.response[0];
            // // For token
            // sessionStorage.setItem("token", res.result.bearerToken);
            // sessionStorage.setItem("token", res.result.bearerToken);

            this.router.navigate(["main/dashboard"]);

          }
          ,
          (rej) => {
            this.spin.hide();
            this.toastr.error(rej.error.error, 'Notification');
            resolve(false);
            setTimeout(() => {
              window.location.reload();
          }, 1000);
          }
        )
      );
    });







  }
  // RefreshToken() {
  //   var use2r = {
  //     clientId: "pixeltrice",
  //     clientSecretKey: "pixeltrice-secret-key",
  //     grantType: "password",
  //     oauthPassword: "welcome",
  //     oauthUserName: "muru"
  //   }


  //   this.http.post<any>(`/auth-token`, use2r).subscribe(
  //     (res) => {
  //       console.log(res);
  //       this.spin.hide();
  //       // let resp: any = res.result.response[0];
  //       // // For token
  //       // sessionStorage.setItem("token", res.result.bearerToken);
  //       // sessionStorage.setItem("token", res.result.bearerToken);
  //       sessionStorage.setItem('token', res.access_token);
  //       this.token = res.access_token;
  //       var token = res.access_token;
  //       return this.token;
  //     }
  //     ,
  //     (rej) => {
  //       this.spin.hide();
  //       // this.toastr.error(rej.result.error_description, "Error");
  //     }
  //   );
  // }

  refreshToken(apiName: string) {
    return this.http.post<any>('/auth-token', {
      clientId: "pixeltrice",
      clientSecretKey: "pixeltrice-secret-key",
      grantType: "password",
      oauthPassword: "welcome",
      oauthUserName: "muru",
      apiName: apiName
    });
  }
  public saveToken(token: any, apiName: string): void {
    window.sessionStorage.removeItem(apiName);
    window.sessionStorage.setItem(apiName, token);
  }
  public saveTokenfrom(token: any, apiName: string): void {
    window.sessionStorage.removeItem(apiName);
    window.sessionStorage.setItem(apiName, token.access_token);
  }

  public getToken(apiName: string): string | null {
    return window.sessionStorage.getItem(apiName);
  }
  get userID() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').userId;
    //return JSON.parse(sessionStorage.getItem("user") as '{}').username;
  }
  get plantId() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').plantId;
    //return JSON.parse(sessionStorage.getItem("user") as '{}').username;
  }
  get userRoleId() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').userRoleId;
  }
  get warehouseId() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').warehouseId;
    //return JSON.parse(sessionStorage.getItem("user") as '{}').username;
  }
  get companyId() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').companyCode;
    //return JSON.parse(sessionStorage.getItem("user") as '{}').username;
  }
  get languageId() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').languageId;
    //return JSON.parse(sessionStorage.getItem("user") as '{}').username;
  }
  get username() {
    return JSON.parse(sessionStorage.getItem("user") as '{}')
      ? JSON.parse(sessionStorage.getItem("user") as '{}').username
      : null;


  }

  get userType() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').userType;
  }

  get userTypeId() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').userTypeId;
  }

  get warehouseType() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').warehouseType;
  }

  get warehouseTypeName() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').warehouseTypeName;
  }

  get warehouseList() {
    const warehouseList = JSON.parse(sessionStorage.getItem("user") as '{}')
      .warehouseList;
    if (warehouseList) {
      return warehouseList;
    } else {
      return [];
    }
  }

  get company() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').company;
  }

  get companyCurrency() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').companyCurrency;
  }

  get isAdmin() {
    if (
      this.userType == "SuperAdmin" &&
      this.userID == "1"
    ) {
      return true;
    }
    return false;
  }

  get accessJson() {
    return JSON.parse(sessionStorage.getItem("user") as '{}').accessJson;
  }

  logout() {


    sessionStorage.clear();
    this.userData = false;
    // const data = {
    //   userName: [this.username],
    //   requestType: "Logout",
    // };
    this.spin.show();
    this.router.navigate(["/"]);
    window.sessionStorage.clear();
    this.spin.hide();
    // this.http.post<any>(`Login/Logout`, data).subscribe(
    //   (res) => {

    //     this.router.navigate(["/"]);
    //     this.spin.hide();
    //   },
    //   (err) => {
    //     this.userData = false;
    //     this.router.navigate(["/"]);
    //     this.spin.hide();
    //   }
    // );
  }

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }
  // get dateformat() {
  //   if (this.userData) {
  //     return this.company.dateformat;
  //   }
  //   return "";
  // }
}
