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


    // if (JSON.parse(localStorage.getItem("user") as '{}')) {
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
            1001, 2101, 2102,2103,2104,2105, 2106,
            1002,2201, 2202, 2203,2204,2205,2206,2207,2208,2209,2210,
            1003,2301,2302,
            1004,2401,2402,2403,2404,
            1005,2501,2502,2503,
          1006,2601,2602,
        1007,
      1008,2801,2802,2803,2805,
    1009,2901,2902,2903,2904,
    1010,3001,3002,3003,3004,3005,3006,
  1011,1101,1102,1103,1104,1105];
            localStorage.setItem('menu', menu.toString());
            localStorage.setItem("user", JSON.stringify(res))
            // let resp: any = res.result.response[0];
            // // For token
            // localStorage.setItem("token", res.result.bearerToken);
            // sessionStorage.setItem("token", res.result.bearerToken);
            this.router.navigate(["main/dashboard"]);

          }
          ,
          (rej) => {
            this.spin.hide();
            this.toastr.error("", rej.error['error-message']);
            resolve(false);
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
  //       // localStorage.setItem("token", res.result.bearerToken);
  //       // sessionStorage.setItem("token", res.result.bearerToken);
  //       localStorage.setItem('token', res.access_token);
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


  refreshToken(token: string, wmsApiName: string) {
    return this.http.post('/auth-token', {
      clientId: "pixeltrice",
      clientSecretKey: "pixeltrice-secret-key",
      grantType: "password",
      oauthPassword: "wms",
      oauthUserName: "wms",
      apiName: wmsApiName
    });
  }
  public saveToken(token: any, wmsApiName: string): void {
    window.sessionStorage.removeItem(wmsApiName);
    window.sessionStorage.setItem(wmsApiName, token);
  }

  public getToken(wmsApiName: string): string | null {
    return window.sessionStorage.getItem(wmsApiName);
  }
  get userID() {
    return JSON.parse(localStorage.getItem("user") as '{}').userId;
  }

  get username() {
    return JSON.parse(localStorage.getItem("user") as '{}')
      ? JSON.parse(localStorage.getItem("user") as '{}').username
      : null;
  }

  get userType() {
    return JSON.parse(localStorage.getItem("user") as '{}').userType;
  }

  get warehouseType() {
    return JSON.parse(localStorage.getItem("user") as '{}').warehouseType;
  }

  get warehouseTypeName() {
    return JSON.parse(localStorage.getItem("user") as '{}').warehouseTypeName;
  }

  get warehouseList() {
    const warehouseList = JSON.parse(localStorage.getItem("user") as '{}')
      .warehouseList;
    if (warehouseList) {
      return warehouseList;
    } else {
      return [];
    }
  }

  get company() {
    return JSON.parse(localStorage.getItem("user") as '{}').company;
  }

  get companyCurrency() {
    return JSON.parse(localStorage.getItem("user") as '{}').companyCurrency;
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
    return JSON.parse(localStorage.getItem("user") as '{}').accessJson;
  }

  logout() {
    window.sessionStorage.clear();

    localStorage.clear();
    this.userData = false;
    const data = {
      userName: [this.username],
      requestType: "Logout",
    };
    this.spin.show();
    this.router.navigate(["/"]);

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
