


  

  import { Component, OnInit } from '@angular/core';
  import * as Highcharts from 'highcharts';// import highcharts3d from "highcharts/highcharts-3d";
  import cylinder from "highcharts/modules/cylinder";
  import highcharts3d from "highcharts/highcharts-3d";
  highcharts3d(Highcharts);
  cylinder(Highcharts);



  @Component({
    selector: 'app-deliver-chart',
    templateUrl: './deliver-chart.component.html',
    styleUrls: ['./deliver-chart.component.scss']
  })
  export class DeliverChartComponent implements OnInit {
  
  
    Highcharts = Highcharts;
    chartOptions = {};
  
   
  
    constructor() { }
  
    ngOnInit() {
      this.chartOptions = {
          credits: false,
          chart: {
            backgroundColor: 'transparent'
        },
          title: {
            text: 'DELIVERY'
        },
        xAxis: {
            categories: ['TV Wh', 'Shuwaikh', 'Alrai', 'Egaila', 'Corner']
        },
        labels: {
            items: [{
                style: {
                    left: '50px',
                    top: '18px',
                }
            }]
        },
        series: [{
            type: 'column',
            name: 'Shippied Lines',
            data: [16, 162, 336, 162, 57],
            color: "#7CB5EC",
            point: {
              events: {
                click: function (event: any,) {
                 // location.href = 'http://localhost:4200/#/main/dashboard/delivery' ; 
                  location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/delivery' ; 
            
                }
              }
            }
        }, {
            type: 'spline',
            name: 'Shipped Qty',
            data: [124,1646, 3187, 1392, 440],
            color: "#F7A35C",
            marker: {
                lineWidth: 2,
                fillColor: 'blace'
            },
            point: {
              events: {
                click: function (event: any,) {
                  //location.href = 'http://localhost:4200/#/main/dashboard/delivery' ; 
                  location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/delivery' ; 
            
                }
              }
            }
        }]
      };
  
  
    }
  
  }
  
