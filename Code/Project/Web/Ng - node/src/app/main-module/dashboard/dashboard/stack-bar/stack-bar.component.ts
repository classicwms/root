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
      chart: {
        type: 'column',
    },
    title: {
        text: 'Stacked column chart'
    },
    xAxis: {
        categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Total fruit consumption'
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
      color: '#7CB5EC',
        name: 'John',
        data: [5, 3, 4, 7, 2]
    }, {
      color: '#90ED7D',
        name: 'Jane',
        data: [2, 2, 3, 2, 1]
    }, {
      color: '#F7A35C',
        name: 'Joe',
        data: [3, 4, 4, 2, 5]
    }]
    };


  }

}
