import {
  Component,
  OnInit
} from '@angular/core';
import {
  FormBuilder
} from '@angular/forms';
import * as Highcharts from 'highcharts';
import HC_more from 'highcharts/highcharts-more';
HC_more(Highcharts);
import lollipop from 'highcharts/modules/lollipop.js';
import {
  Subscription
} from 'rxjs';
import {
  DashboardService
} from '../../dashboard.service';

import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { CommonService } from 'src/app/common-service/common-service.service';
import { MatDialog } from '@angular/material/dialog';
import { FastSlowListComponent } from '../../list/fast-slow-list/fast-slow-list.component';
import { DatePipe } from '@angular/common';
//lollipop(Highcharts);


//dumbbell(Highcharts)

//let indicators = require('highcharts/modules/lollipop');

// import item from 'highcharts/modules/item-series.js';
// item(Highcharts);


// import highcharts3d from "highcharts/highcharts-3d";
// import cylinder from "highcharts/modules/cylinder";

// highcharts3d(Highcharts);
// cylinder(Highcharts);


@Component({
  selector: 'app-skuchart',
  templateUrl: './skuchart.component.html',
  styleUrls: ['./skuchart.component.scss']
})
export class SkuchartComponent implements OnInit {

  Highcharts = Highcharts;
  chartOptions = {};

  sub = new Subscription();
  currentDate: Date;

  constructor(private service: DashboardService,   public datepipe: DatePipe, public dialog: MatDialog,   private cs: CommonService, private fb: FormBuilder,private router: Router ) {  this.currentEnv = environment.name; }


  form = this.fb.group({
    fromDate: [, ],
    toDate: [, ],
    fromDateFE: [new Date("11/01/22 00:00:00"),],
    toDateFE: [this.cs.todayapi(), ],
    warehouseId: ["110", ],
  });


  openDialog(): void {
    const dialogRef = this.dialog.open(FastSlowListComponent, {
      disableClose: true,
     // width: '50%',
     width: '100%',
     // position: { top: '9%', },
      data: { fromDate: this.form.controls.fromDate.value, toDate: this.form.controls.toDate.value,}
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  fastArray = [{
      name: 'Germany',
      value: 767.1
    },
    {
      name: 'Germany',
      value: 767.1
    }
  ];
  averagerray = [{
      name: 'Germany',
      value: 767.1
    },
    {
      name: 'Germany',
      value: 767.1
    }
  ];
  slowArray = [{
      name: 'Germany',
      value: 37.1
    },
    {
      name: 'Germany',
      value: 767.1
    }
  ]

  currentEnv: string;
  prod: boolean;

  startDate: any;

  ngOnInit() {

    if(this.currentEnv == 'prod'){
      this.prod = true;
    }else{
      this.prod = false;
    }
    this.getData();
    this.form.controls.toDateFE.valueChanges.subscribe(x => {
      
      this.getData();
    })
  //   this.currentDate = new Date();
  //   let yesterdayDate = new Date();
  //   let currentMonthStartDate = new Date();
  //   yesterdayDate.setDate(this.currentDate.getDate() - 1);
  //   this.startDate = this.datepipe.transform(yesterdayDate, 'dd');
  //  currentMonthStartDate.setDate(this.currentDate.getDate() - this.startDate);
  // this.form.controls.fromDateFE.patchValue(currentMonthStartDate);

    
    this.chartBind(this.fastArray, this.averagerray, this.slowArray);
  }



goTo(){

};

  getData() {
    
    this.form.controls.fromDate.patchValue(this.cs.dateNewFormat(this.form.controls.fromDateFE.value));
    this.form.controls.toDate.patchValue(this.cs.dateNewFormat(this.form.controls.toDateFE.value));

    this.sub.add(this.service.fastSlow(this.form.getRawValue()).subscribe(res => {
      let fastArray: any[] = [];
      let averageArray: any[] = [];
      let slowArray: any[] = [];
      res.forEach(element => {
        if (element.type == "FAST") {
          fastArray.push({
            name: element.itemCode,
            value: element.deliveryQuantity,
            text: element.itemText
          })
        }
        if (element.type == "AVERAGE") {
          averageArray.push({
            name: element.itemCode,
            value: element.deliveryQuantity,
            text: element.itemText
          })
        }
        if (element.type == "SLOW") {
          slowArray.push({
            name: element.itemCode,
            value: element.deliveryQuantity,
            text: element.itemText
          })
        }
      });

      this.chartBind(fastArray, averageArray, slowArray)
    }));
  }


  chartBind(fastArray, averageArray, slowArray) {
    
    this.chartOptions = {
      credits: false,
      chart: {
        type: 'packedbubble',
        height: '80%',
        backgroundColor: 'transparent',
        
      },
      
      title: {
        text: 'Fast Slow Moving'
      },
      tooltip: {
        useHTML: true,
        pointFormat: 'Product Code: <b>{point.name}</b> </br>Delivery Qty: <b>{point.value}</b> </br> Description: <b>{point.text}</b>'
      },
      plotOptions: {
        packedbubble: {
          minSize: this.prod == true ? '100%' : '30%',
          maxSize: '100%',
          zMin: 0,
          zMax: 1000,
          layoutAlgorithm: {
            splitSeries: false,
          },
          legend: {
            enabled: false
          },
          dataLabels: {
            enabled: true,
            format: '{point.value}',
            filter: {
              property: 'y',
              operator: '>',
              value: -1
            },
            style: {
              color: 'black',
              textOutline: 'none',
              fontWeight: 'normal'
            },
            //showInLegend: true,
            //   startAngle: -90,
            //   endAngle: 90,
            //   center: ['50%', '50%'],
            //   size: '230%',
          }
        }
      },
      
      series: [{
          name: 'Fast',
          data: fastArray
        },
        {
          name: 'Average',
          data: averageArray
        },
        {
          name: 'Slow',
          data: slowArray,
        //   point: {
        //     events: {
        //       click: function (event: any,) {
        //         location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/sku' ; 
          
        //       }
        //     }
        //   }
        },
        
      ],
  
    };
    
  }
}


// point: {
//   events: {
//     click: function (event: any, ) {

//       //    location.href = 'http://localhost:4200/#/main/dashboard/sku' ; 
//       location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/sku';

//     }
//   }
// }
