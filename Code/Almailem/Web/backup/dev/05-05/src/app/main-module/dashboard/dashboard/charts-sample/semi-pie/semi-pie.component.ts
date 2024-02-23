

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
    selector: 'app-semi-pie',
    templateUrl: './semi-pie.component.html',
    styleUrls: ['./semi-pie.component.scss']
  })
  export class SemiPieComponent implements OnInit {
  
    Highcharts = Highcharts;
    chartOptions = {};
  
   
  
    constructor() { }
  
    ngOnInit() {
      this.chartOptions = {
          credits: false,
          chart: {
            type: 'line',
            backgroundColor: 'transparent'
        },
        title: {
            text: 'PRODUCTIVITY BINNING'
        },
        xAxis: {
            categories: ['21-12-22', '22-12-22', '23-12-22', '24-12-22', '25-12-22', '26-12-22', '27-12-22']
        },
        yAxis: {
            title: {
                text: 'Lead Time'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true
                },
                enableMouseTracking: true
            }
        },
        series: [{
          name: 'Afsal',
          data: [6, 12, 8, 13, 8, 14, 6],
        //  color: "#90ED7D"
      }, 
      {
          name: 'Aji',
          data: [4, 1, 6, 2, 6, 3, 4],
      },
      {
        name: 'Noufal',
       
        data: [2, 3, 4, 5, 4, 3, 2],
    },
    {
      name: 'Jineesh',
      data: [8, 9, 10, 7, 10, 6, 8],
  }
    ]
      };
  
  
    }
  
  }
  