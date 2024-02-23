



  import { Component, OnInit } from '@angular/core';
  import * as Highcharts from 'highcharts';// import highcharts3d from "highcharts/highcharts-3d";
  import cylinder from "highcharts/modules/cylinder";
  import highcharts3d from "highcharts/highcharts-3d";
  highcharts3d(Highcharts);
  cylinder(Highcharts);
  import lollipop from 'highcharts/modules/lollipop.js';
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
    selector: 'app-receipt-bar',
    templateUrl: './receipt-bar.component.html',
    styleUrls: ['./receipt-bar.component.scss']
  })
  export class ReceiptBarComponent implements OnInit {
  
    Highcharts = Highcharts;
    chartOptions = {};
  
    constructor() { }
  
    ngOnInit() {
      this.chartOptions = {
        credits: false,
        chart: {
            type: 'column',
            backgroundColor: 'transparent',
            options3d: {
                enabled: true,
                alpha: 15,
                beta: 15,
                viewDistance: 25,
                depth: 40
            }
        },
    
        title: {
            text: 'RECEIPTS'
        },
    
        xAxis: {
            categories: ['Container Received', 'Awaiting for ASN',],
            labels: {
                skew3d: true,
                style: {
                    fontSize: '16px'
                }
            }
        },
    
        yAxis: {
            allowDecimals: false,
            min: 0,
            title: {
                text: 'No of Containers',
                skew3d: true
            }
        },
    
        tooltip: {
            headerFormat: '<b>{point.key}</b><br>',
            pointFormat: '<span style="color:{series.color}">\u25CF</span> {series.name}: {point.y} / {point.stackTotal}'
        },
    
        plotOptions: {
            column: {
                stacking: 'normal',
                depth: 40
            },
            showInLegend: false,
        },
    
        series: [{
            name: 'Container Received',
            data: [4,-1],
            color: '#F7A35C',
            stack: 'male',
            point: {
                events: {
                  click: function (event: any,) {
                   // location.href = 'http://localhost:4200/#/main/dashboard/received' ; 
                     location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/received' ; 
              
                  }
                }
              },
        },
        {
            name: 'Awaiting for ASN',
            data: [-1,1],
            stack: 'male',
            color: '#F15C80',
            point: {
                events: {
                  click: function (event: any,) {
                   // location.href = 'http://localhost:4200/#/main/dashboard/asn' ; 
                    location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/asn' ; 
              
                  }
                }
              },
        },]
        
      };
  
  
    }
  
  }
  