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
  
    constructor(private service: DashboardService, private fb: FormBuilder, ) {}
  
  
    form = this.fb.group({
      fromDate: [, ],
      toDate: [, ],
      warehouseId: ["110", ],
    });
  
    fastArray = [{
      name: 'Germany',
      value: 767.1
    },
  {
      name: 'Germany',
      value: 767.1
  }];
  averagerray = [{
      name: 'Germany',
      value: 767.1
    },
  {
      name: 'Germany',
      value: 767.1
  }];
  slowArray = [{
      name: 'Germany',
      value: 37.1
    },
  {
      name: 'Germany',
      value: 767.1
  }]
  
  
    ngOnInit() {
  
      this.form.controls.toDate.valueChanges.subscribe(x => {
        this.getData();
      })
  this.chartBind(this.fastArray, this.averagerray, this.slowArray)
  
  
  
    }
  
  
  
  
  
    getData() {
      this.sub.add(this.service.fastSlow(this.form.getRawValue()).subscribe(res => {
         let fastArray: any[] = [];
         let averageArray: any[] = [];
         let slowArray: any[] = [];
        res.forEach(element => {
       if(element.type == "FAST"){
          fastArray.push(
              {
               name: element.itemCode,
               value: element.deliveryQuantity,
               text: element.itemText
              }
           )
       }
       if(element.type == "AVERAGE"){
          averageArray.push(
              {
               name: element.itemCode,
               value: element.deliveryQuantity
              }
           )
       }
       if(element.type == "SLOW"){
          slowArray.push(
              {
               name: element.itemCode,
               value: element.deliveryQuantity,
              }
           )
       }
        });
        
        this.chartBind(fastArray, averageArray, slowArray)
      }));
    }
  
  
    chartBind(fastArray, averageArray, slowArray){
      this.chartOptions = {
          credits: false,
          chart: {
            type: 'packedbubble',
            height: '100%',
            backgroundColor: 'transparent'
          },
          title: {
            text: 'SKU - INVENTORY'
          },
          tooltip: {
              useHTML: true,
              pointFormat: 'Product Code: <b>{point.name}</b> </br>Delivery Qty: <b>{point.value}</b> </br> Description: <b>{point.text}</b>'
          },
          plotOptions: {
            packedbubble: {
              minSize: '30%',
              maxSize: '120%',
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
              data: slowArray
            }
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