import {
  ArrayDataSource
} from '@angular/cdk/collections';
import {
  NestedTreeControl
} from '@angular/cdk/tree';
import {
  DatePipe
} from '@angular/common';
import {
  Component,
  OnInit
} from '@angular/core';
import {
  Router
} from '@angular/router';
import {
  NgxSpinnerService
} from 'ngx-spinner';
import {
  ToastrService
} from 'ngx-toastr';
import {
  AuthService
} from 'src/app/core/core';
import {
  DashboardService
} from './dashboard.service';


import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})

export class DashboardComponent implements OnInit {
  planModel: any = {
    start_time: new Date()
  };
  showDashboard: boolean;
  day_callapiSearch(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-ddT00:00:00.000');
    return del_date;
  }
  panelOpenState = false;
  dashboardCountData: any = {};
  date = new Date();
  constructor(
    private datepipe: DatePipe,
    public auth: AuthService,
    public spin: NgxSpinnerService,
    public toastr: ToastrService,
    public dashboardService: DashboardService
  )  {  this.currentEnv = environment.name; }

  currentEnv: string;
  prod: boolean;
  dev: boolean;

  product: boolean;
  ngOnInit() {

    if(this.currentEnv == 'prod'){
      this.prod = true;
    }else{
      this.prod = false;
    }
    
    if(this.currentEnv == 'product'){
      this.product = true;
    }else{
      this.product = false;
    }

    if(this.currentEnv == 'dev'){
      this.dev = true;
    }else{
      this.dev = false;
    }

    this.showDashboard = false
    this.date.setDate(this.date.getDate() - 1);
    this.spin.show();
     this.getDashboardData();
     this.spin.hide();
  }


  statusEqualToZeroCount = '';
  statusNotEqualToZeroCount = '';
  dayawaitingASN = '';
  daycontainerReceived = '';
  dayitemReceived = ''
  daynormal = '';
  dayshippedLine = '';
  dayspecial = '';
  monthawaitingASN = '';
  monthcontainerReceived = '';
  monthitemReceived = ''
  monthnormal = '';
  monthshippedLine = '';
  monthspecial = '';

  getDashboardData() {

    this.awaitingASN();

  }


  awaitingASN() {
    this.dashboardService.dashboardNew(this.auth.warehouseId).subscribe(res => {
        this.dayawaitingASN = res.day.receipts.awaitingASN;
        this.monthawaitingASN = res.month.receipts.awaitingASN;
        //Bin Status
        this.statusEqualToZeroCount = res.binStatus.statusEqualToZeroCount;
        this.statusNotEqualToZeroCount = res.binStatus.statusNotEqualToZeroCount;
        //Container
        this.daycontainerReceived = res.day.receipts.containerReceived;
        this.monthcontainerReceived = res.month.receipts.containerReceived;

        //ITEM 
        this.dayitemReceived = res.day.receipts.itemReceived;
        this.monthitemReceived = res.month.receipts.itemReceived;

        // SHIPPING NORMAL
        this.daynormal = res.day.shipping.normal;
        this.monthnormal = res.month.shipping.normal;

        // SHIPPING LINE
        this.dayshippedLine = res.day.shipping.shippedLine;
        this.monthshippedLine = res.month.shipping.shippedLine;
        
        //SHIPPING SPECIAL
        this.dayspecial = res.day.shipping.special;
        this.monthspecial = res.month.shipping.special;


      },
      error => {
        this.spin.hide();
      }
    );
  }
  //Bin status
  getAllBinStatus() {
    this.dashboardService.dashboardNew(this.auth.warehouseId).subscribe(
      binStatus => {
        this.statusEqualToZeroCount = binStatus.binStatus.statusEqualToZeroCount;
        this.statusNotEqualToZeroCount = binStatus.binStatus.statusNotEqualToZeroCount;
      },
      error => {
        this.spin.hide();
      }
    );
  }

  //container Status
  getAllContainerReceived() {
    this.dashboardService.getAllContainerReceived(this.auth.warehouseId).subscribe(
      containerStatus => {
        this.daycontainerReceived = containerStatus.day.receipts.containerReceived;
        this.monthcontainerReceived = containerStatus.month.receipts.containerReceived;
      },
      error => {
        this.spin.hide();
      }
    );
  }


  getAllItemReceived() {
    this.dashboardService.getAllItemReceived(this.auth.warehouseId).subscribe(
      itemStatus => {
        this.dayitemReceived = itemStatus.day.receipts.itemReceived;
        this.monthitemReceived = itemStatus.month.receipts.itemReceived;
      },
      error => {
        this.spin.hide();
      }
    );
  }

  getAllNormalCount() {
    this.dashboardService.getAllNormalCount(this.auth.warehouseId).subscribe(
      normalCount => {
        this.daynormal = normalCount.day.shipping.normal;
        this.monthnormal = normalCount.month.shipping.normal;
      },
      error => {
        this.spin.hide();
      }
    );
  }

  getAllShippedLine() {
    this.dashboardService.getAllShippedLine(this.auth.warehouseId).subscribe(
      shippedLine => {
        this.dayshippedLine = shippedLine.day.shipping.shippedLine;
        this.monthshippedLine = shippedLine.month.shipping.shippedLine;
      },
      error => {
        this.spin.hide();
      }
    );
  }

  getAllSpecialCount() {
    this.dashboardService.getAllSpecialCount(this.auth.warehouseId).subscribe(
      specialcount => {
        this.dayspecial = specialcount.day.shipping.special;
        this.monthspecial = specialcount.month.shipping.special;
      },

      error => {
        this.spin.hide();
      }
    );
    this.spin.hide();
    this.showDashboard = true

  }

  floorList= 'Ground'

}
