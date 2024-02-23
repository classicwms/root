import { Component, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts';
import * as theme from 'highcharts/themes/dark-unica';

@Component({
  selector: 'app-stack-bar',
  templateUrl: './stack-bar.component.html',
  styleUrls: ['./stack-bar.component.scss']
})
export class StackBarComponent implements OnInit {

 
  
  Highcharts = Highcharts;
  chartOptions = {};

 

  constructor() { }

  ngOnInit() {
    this.chartOptions = {
      credits: false,
      chart: {
        type: 'column',
        style: {
          fontFamily: 'Roboto Slab'
      }
    },
    title: {
        text: 'Outbound Denials'
    },
    xAxis: {
        categories: ['1', '2', '3', '4', '5','6','7'],
        title: {
          text: 'DAYS'
      },
    },
    yAxis: {
        min: 0,
        title: {
            text: 'DENIALS'
        },
        stackLabels: {
            enabled: true,
            style: {
                fontWeight: 'bold',
                color: 'white',
            }
        }
    },
    legend: {
        align: 'right',
        x: -30,
        verticalAlign: 'top',
        y: 25,
        floating: true,
        backgroundColor:'white',
        borderColor: '#CCC',
        borderWidth: 1,
        shadow: false
    },
    tooltip: {
        headerFormat: '<b>{point.x}</b><br/>',
        pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
    },
    plotOptions: {
        column: {
            stacking: 'normal',
            dataLabels: {
                enabled: true
            }
        }
    },
    series: [{
      color: '#FF0000',
        name: 'Denials',
        data: [4, 3, 4, 5, 3, 2, 4]
    }, {
      color: '#90ED7D',
        name: 'Delivered',
        data: [15, 13, 16, 18, 13, 12, 14]
    }]
    };


  }

}
