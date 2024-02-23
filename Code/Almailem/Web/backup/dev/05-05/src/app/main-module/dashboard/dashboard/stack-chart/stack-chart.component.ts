import { Component, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts';
@Component({
  selector: 'app-stack-chart',
  templateUrl: './stack-chart.component.html',
  styleUrls: ['./stack-chart.component.scss']
})
export class StackChartComponent implements OnInit {

  
  Highcharts = Highcharts;
  chartOptions = {};

 

  constructor() { }

  ngOnInit() {
    this.chartOptions = {
        credits: false,
        chart: {
            type: 'spline'
        },
        title: {
            text: 'Productivity - Picking'
        },
        xAxis: {
            categories: ['04-11-22', '05-11-22', '06-11-22', '07-11-22', '08-11-22', '09-11-22', '10-11-22']
        },
        yAxis: {
            title: {
                text: 'PICKING'
            },
        },
        tooltip: {
            headerFormat: '<b>{point.x}</b><br/>',
            pointFormat: '{series.name}: {point.y}'
        },
        plotOptions: {
            spline: {
                marker: {
                    radius: 4,
                    lineColor: '#666666',
                    lineWidth: 1
                }
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
