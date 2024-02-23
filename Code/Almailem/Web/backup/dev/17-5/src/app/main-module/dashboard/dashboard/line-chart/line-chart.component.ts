import { Component, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts';
import HC_heatmap from 'highcharts/modules/heatmap';
HC_heatmap(Highcharts);
@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.scss']
})
export class LineChartComponent implements OnInit {

  Highcharts = Highcharts;
  chartOptions = {};

 

  constructor() { }

  ngOnInit() {
    this.chartOptions = {
      credits: false,
        chart: {
            type: 'heatmap',
            style: {
                fontFamily: 'Roboto Slab'
            },
            marginTop: 40,
            marginBottom: 80,
            plotBorderWidth: 0,
            events: {
              load: function () {
                // this.myTooltip = new Highcharts.Tooltip(this, this.options.tooltip);
              },
            },
          },
          title: {
            text: 'Bin Status',
          },
          xAxis: {
            categories: [
              '11',
              '12',
              '13',
              '14',
              '15',
              '16',
              '17',
              '18',
              '19',
              '20',
            ],
            title: {
              text: 'Rows'
          }
          },
          yAxis: {
            categories: ['0', '1', '2', '3', '4'],
            title: {
              text: 'Zone'
          }
          },
          colorAxis: {
            stops: [
              // [2.6, '#666666'],
        //     [10, '#7CB5EC'],
            [0.1, '#ffffff'],
            [0.3, '#7CB5EC'],
            [0.5, '#F7A35C'],
            [0.7, '#ffffff'],
             [1, '#ffffff'],
             //[0.225, '#90ED7D'],
            ],
          },
          tooltip: {
            headerFormat: '',
            pointFormat: 
              '</b><br>SKU: 902561 <br>Qty: 150<br>Batch: 253722 <b>'
       
         
         //     pointFormat: '</b> SKU: 902561<br>Qty: 150<br>Batch: 253722 <b>'
        },
          legend: {
            align: 'right',
            layout: 'vertical',
            margin: 0,
            verticalAlign: 'top',
            y: 25,
            symbolHeight: 280,
          },
          plotOptions: {
            heatmap: {
              pointPadding: 5,
            },
            series: {
              stickyTracking: false,
            },
          },
    
          series: [
            {
              name: 'Sales per employee',
              borderWidth: 1,
              data: [
                 [0, 1, 111,],
                [0, 2, 211,],
               [0, 3, 311],
                 [0, 4, 411],
                // [0, 5, 511],
                [1, 1, 112],
                [1, 2, 212],
                [1, 3, 312],
                [1, 4, 412],
            //    [1, 5, 512],
                [2, 1, 113],
                [2, 2, 213],
                [2, 3, 313],
                [2, 4, 413],
            //    [2, 5, 513],
                [3, 1, 114],
                [3, 2, 214],
                [3, 3, 314],
                [3, 4, 414],
           //     [3, 5, 514],
                [4, 1, 115],
                [4, 2, 215],
                [4, 3, 315],
                [4, 4, 415],
             //   [4, 5, 515],
                [5, 1, 116],
                [5, 2, 216],
                [5, 3, 316],
                [5, 4, 416],
             //   [5, 5, 516],
                [6, 1, 117],
                [6, 2, 217],
                [6, 3, 317],
                [6, 4, 417],
            //    [6, 5, 517],
                [7, 1, 118],
                [7, 2, 218],
                [7, 3, 318],
                [7, 4, 418],
             //   [7, 5, 518],
                [8, 1, 119],
                [8, 2, 219],
                [8, 3, 319],
                [8, 4, 419],
              //  [8, 5, 519],
                [9, 1, 120],
                [9, 2, 220],
                [9, 3, 320],
                [9, 4, 420],
            //    [9, 5, 520],
              ],
              dataLabels: {
                enabled: true,
                color: '#000000',
              },
            },
          ],
    };


  }

}
