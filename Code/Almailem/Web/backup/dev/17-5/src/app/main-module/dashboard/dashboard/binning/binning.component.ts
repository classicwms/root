import {
  Component,
  OnInit
} from '@angular/core';
import * as Highcharts from 'highcharts';

@Component({
  selector: 'app-binning',
  templateUrl: './binning.component.html',
  styleUrls: ['./binning.component.scss']
})
export class BinningComponent implements OnInit {


  Highcharts = Highcharts;
  chartOptions = {};



  constructor() {}

  ngOnInit() {
      
    this.chartOptions = {
        credits: false,
      title: {
        text: 'Productivity - Binning'
    },
    

    yAxis: {
        title: {
            text: 'BINNING'
        },
        
    },

    xAxis: {
        categories: ['Hour 1', 'Hour 2', 'Hour 3', 'Hour 4', 'Hour 5','Hour 6','Hour 7','Hour 8']
    },

    legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle'
    },

    plotOptions: {
        series: {
            label: {
                connectorAllowed: false
            },
        }
    },

    series: [{
        name: 'Abdul',
        data: [2, 3, 4, 5, 6, 4, 7, 9]
    }, {
        name: 'Salim',
        data: [5, 6, 7, 8, 9, 8, 7, 6]
    }, {
        name: 'Dhanish',
        data: [10, 11, 12, 13, 12, 11, 10, 9]
    }, {
        name: 'Akbar',
        data: [1, 2, 3, 4, 5, 6, 4, 5]
    }, {
        name: 'Aazir',
        data: [8, 9, 10, 11, 10, 9, 8, 7]
    }],

    responsive: {
        rules: [{
            condition: {
                maxWidth: 500
            },
            chartOptions: {
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    verticalAlign: 'bottom'
                }
            }
        }]
    }
    };


  }

}
