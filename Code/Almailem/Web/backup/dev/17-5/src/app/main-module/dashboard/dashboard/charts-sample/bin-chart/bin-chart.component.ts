

  

  import { Component, OnInit } from '@angular/core';
  import * as Highcharts from 'highcharts';// import highcharts3d from "highcharts/highcharts-3d";
  import cylinder from "highcharts/modules/cylinder";
  import highcharts3d from "highcharts/highcharts-3d";
import { Router } from '@angular/router';
  highcharts3d(Highcharts);
  cylinder(Highcharts);


  const dataPrev = {
    2016: [
        ['South Korea', 0],
        ['Japan', 0],
        ['Australia', 0],
        ['Germany', 11],
        ['Russia', 24],
        ['China', 38],
        ['Great Britain', 29],
        ['United States', 46]
    ],
    2012: [
        ['South Korea', 13],
        ['Japan', 0],
        ['Australia', 0],
        ['Germany', 0],
        ['Russia', 22],
        ['China', 51],
        ['Great Britain', 19],
        ['United States', 36]
    ],
    2008: [
        ['South Korea', 0],
        ['Japan', 0],
        ['Australia', 0],
        ['Germany', 13],
        ['Russia', 27],
        ['China', 32],
        ['Great Britain', 9],
        ['United States', 37]
    ],
    2004: [
        ['South Korea', 0],
        ['Japan', 5],
        ['Australia', 16],
        ['Germany', 0],
        ['Russia', 32],
        ['China', 28],
        ['Great Britain', 0],
        ['United States', 36]
    ],
    2000: [
        ['South Korea', 0],
        ['Japan', 0],
        ['Australia', 9],
        ['Germany', 20],
        ['Russia', 26],
        ['China', 16],
        ['Great Britain', 0],
        ['United States', 44]
    ]
};

const data = {
    2016: [
        ['South Korea', 0],
        ['Japan', 0],
        ['Australia', 0],
        ['Germany', 17],
        ['Russia', 19],
        ['China', 26],
        ['Great Britain', 27],
        ['United States', 46]
    ],
    2012: [
        ['South Korea', 13],
        ['Japan', 0],
        ['Australia', 0],
        ['Germany', 0],
        ['Russia', 24],
        ['China', 38],
        ['Great Britain', 29],
        ['United States', 46]
    ],
    2008: [
        ['South Korea', 0],
        ['Japan', 0],
        ['Australia', 0],
        ['Germany', 16],
        ['Russia', 22],
        ['China', 51],
        ['Great Britain', 19],
        ['United States', 36]
    ],
    2004: [
        ['South Korea', 0],
        ['Japan', 16],
        ['Australia', 17],
        ['Germany', 0],
        ['Russia', 27],
        ['China', 32],
        ['Great Britain', 0],
        ['United States', 37]
    ],
    2000: [
        ['South Korea', 0],
        ['Japan', 0],
        ['Australia', 16],
        ['Germany', 13],
        ['Russia', 32],
        ['China', 28],
        ['Great Britain', 0],
        ['United States', 36]
    ]
};

const countries = [{
    name: 'South Korea',
    color: 'rgb(201, 36, 39)'
}, {
    name: 'Japan',
    color: 'rgb(201, 36, 39)'
}, {
    name: 'Australia',
    color: 'rgb(0, 82, 180)'
}, {
    name: 'Germany',
    color: 'rgb(0, 0, 0)'
}, {
    name: 'Russia',
    color: 'rgb(240, 240, 240)'
}, {
    name: 'China',
    color: 'rgb(255, 217, 68)'
}, {
    name: 'Great Britain',
    color: 'rgb(0, 82, 180)'
}, {
    name: 'United States',
    color: 'rgb(215, 0, 38)'
}];


const getData = data => data.map((country, i) => ({
    name: country[0],
    y: country[1],
    color: countries[i].color
}));

  @Component({
    selector: 'app-bin-chart',
    templateUrl: './bin-chart.component.html',
    styleUrls: ['./bin-chart.component.scss']
  })
  export class BinChartComponent implements OnInit {
  
    Highcharts = Highcharts;
    chartOptions = {};
  
   po(){
    //this.router.navigate(["main/dashboard/bin-empty"]);
   }
  
    constructor(
        public router: Router) { }
  
    ngOnInit() {
      this.chartOptions = {
          credits: false,
          chart: {
            type: 'column',
            backgroundColor: 'transparent'
        },
        title: {
            text: 'BIN STATUS'
        },
        xAxis: {
            categories: [
                'Basement',
                'Ground',
                'Corner',
            ],
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Count'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
            name: 'Full',
            data: [7, 8, 8],
            color: '#90ED7D',
            point: {
                events: {
                  click: function (event: any,) {
                  //  location.href = 'http://localhost:4200/#/main/dashboard/bin-full' ; 
                     location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/bin-full' ; 
              
                  }
                }
              },
    
        }, {
            name: 'Empty',
            data: [4, 5, 3],
            color: '#434348',
            point: {
                events: {
                  click: function (event: any,) {
                   // location.href = 'http://localhost:4200/#/main/dashboard/bin-empty' ; 
                     location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/bin-empty' ; 
              
                  }
                }
              },
    
        },]
      };
  
  
    }
  
  }
  
