import { Component } from '@angular/core';
import { Router, NavigationStart, NavigationEnd, NavigationCancel } from '@angular/router';
import { CronJob } from 'cron';
import { NgxSpinnerService } from 'ngx-spinner';
import { ShipmentDispatchComponent } from './main-module/reports/shipment-dispatch/shipment-dispatch.component';
import { ShipmentSummaryComponent } from './main-module/reports/shipment-summary/shipment-summary.component';
import { ScheduleReportComponent } from './main-module/reports/schedule-report/schedule-report.component';
import { AuthService } from './core/core';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  
  title = 'Classic WMS';
  cronJob: CronJob;
  constructor(
    private router: Router,
    private spinner: NgxSpinnerService,
    private shipmentService: ShipmentSummaryComponent,
    private dispatchService: ShipmentDispatchComponent,
    private schedule: ScheduleReportComponent,
    private auth: AuthService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    // private auth: AuthService,
    private http: HttpClient,
    // private rs: RoleAccessService
  ) { 
    //   this.cronJob = new CronJob('30 11 * * *', async () => {
    //   try {
    //     await this.schedular();
    //   } catch (e) {
    //     console.error(e);
    //   }
    // });
    // if (!this.cronJob.running) {
    //   this.cronJob.start();
    // }
  }
  // async schedular(): Promise<void> {
  //     console.log('schedular started')
  //   let user: any = {};
  //   user.userName = 'admin';
  //   user.password = 'TV@2023';
  //   this.loginSchedule(user)
  // }
  ngAfterViewInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.spinner.show();
        // if (this.auth.userID) {
        //   console.log(event.url);
        //   console.log(this.rs.getAccessBySearch(event.url));
        // }
      } else if (
        event instanceof NavigationEnd ||
        event instanceof NavigationCancel
      ) {
        this.spinner.hide();
      }
    });


    // window.addEventListener('beforeunload', (event) => {
    //  // console.log(event)
    //    event.returnValue = `You have unsaved changes, leave anyway?`;
    // });
    
    
  }

  
  //   ngOnDestroy(): void {
  //     window.addEventListener('beforeunload', (event) => {
  //      // console.log(event)
  //        event.returnValue = `You have unsaved changes, leave anyway?`;
  //     });
  // }


  
//   sub = new Subscription();
//   loginSchedule(user: { userName: string; password: any; }) {
//     sessionStorage.clear();
//     sessionStorage.clear();
//     this.spin.show();

//     return new Promise((resolve, reject) => {
//       this.sub.add(
//         this.http.get<any>(`/wms-idmaster-service/login?name=${user.userName}&password=${user.password}`).subscribe(
//           (res) => {
//             console.log(res);
//             this.spin.hide();
//             // let menu = [1000, 1001, 1002, 1003,1005,1006, 1004, 2101, 2102, 2202, 2203, 2201,];
//             let menu = [1000,
//               1001, 2101, 2102, 2103, 2104, 2105, 2106,
//               1002, 2201, 2202, 2203, 2204, 2205, 2206, 2207, 2208, 2209, 2210,
//               1003, 2301, 2302,
//               1004, 2401, 2402, 2403, 2404, 2905,
//               1005, 2501, 2502, 2503,
//               1006, 2601, 2602,
//               1007, 2701,

//               1008, 2801, 2802, 2803, 2805,
//               1009, 2901, 2902, 2903, 2904,
//               1010, 3001, 3002, 3003, 3004, 3005, 3006,
//               1011, 1101, 1102, 1103, 1104, 1105];
//             sessionStorage.setItem('menu', menu.toString());
//             sessionStorage.setItem("user", JSON.stringify(res))
//             // let resp: any = res.result.response[0];
//             // // For token
//             // sessionStorage.setItem("token", res.result.bearerToken);
//             // sessionStorage.setItem("token", res.result.bearerToken);
//             this.schedule.scheduleSearch(110);
//          //   this.router.navigate(["main/dashboard"]);

//           }
//           ,
//           (rej) => {
//             this.spin.hide();
//             this.toastr.error(rej.error.error, 'Notification');
//             resolve(false);
//             setTimeout(() => {
//               window.location.reload();
//           }, 1000);
//           }
//         )
//       );
//     });
//  }
}
