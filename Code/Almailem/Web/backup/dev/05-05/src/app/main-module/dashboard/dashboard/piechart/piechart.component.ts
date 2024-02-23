import { Component, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts';
import variablepie from 'highcharts/modules/variable-pie.js';
variablepie(Highcharts);


@Component({
  selector: 'app-piechart',
  templateUrl: './piechart.component.html',
  styleUrls: ['./piechart.component.scss']
})
export class PiechartComponent implements OnInit {

  Highcharts = Highcharts;
  chartOptions = {};

 

  constructor() { }

  ngOnInit() {
      
    this.chartOptions = {
        credits: false,
        chart: {
            type: 'variablepie',
            style: {
                fontFamily: 'Roboto Slab'
            }
        },
        title: {
            text: 'WIP Aging '
        },
        tooltip: {
            headerFormat: '',
            pointFormat: '<span style="color:{point.color}">\u25CF</span> <b> {point.name}</b><br/>' +
                'Group A: <b>{point.y}</b><br/>' +
                'Group B: <b>{point.z}</b><br/>'
        },
        series: [{
            minPointSize: 10,
            innerSize: '20%',
            zMin: 0,
            name: 'countries',
            data: [{
                name:'> 151 Days',
                y: 7,
                z:30
            }, {
                name:  '121-150 Days',
                y: 5,
                z: 30
            }, {
                name:  '91-120 Days',
                y: 4,
                z: 30
            }, {
                name:  '61-90 Days',
                y: 3,
                z: 30
            }, {
                name:  ' 31-60 Days',
                y: 1,
                z: 30
            }, {
                name: '0-30 Days',
                y: 10,
                z: 30
            },]
        }]
    };


  }

}
