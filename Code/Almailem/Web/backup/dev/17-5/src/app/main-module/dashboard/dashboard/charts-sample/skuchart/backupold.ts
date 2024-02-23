
  import { Component, OnInit } from '@angular/core';
  import * as Highcharts from 'highcharts';
    import HC_more from 'highcharts/highcharts-more';
    HC_more(Highcharts);
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
      selector: 'app-skuchart',
      templateUrl: './skuchart.component.html',
      styleUrls: ['./skuchart.component.scss']
    })
    export class SkuchartComponent implements OnInit {
    
      Highcharts = Highcharts;
      chartOptions = {};
    
     
    
      constructor() { }
    
      ngOnInit() {
        this.chartOptions = {
          credits: false,
          chart: {
            type: 'packedbubble',
            height: '100%',
            backgroundColor: 'transparent'
        },
        title: {
            text: 'SKU - INVENTORY'
        },
        tooltip: {
            useHTML: true,
            pointFormat: '<b>{point.itemCode}</b> <b>Available Qty: {point.qty}</b>'
        },
        plotOptions: {
            packedbubble: {
                minSize: '30%',
                maxSize: '120%',
                zMin: 0,
                zMax: 1000,
                layoutAlgorithm: {
                    splitSeries: false,
                },
                 legend: {
                  enabled: false
              },
                dataLabels: {
                    enabled: true,
                    format: '{point.name}',
                    filter: {
                        property: 'y',
                        operator: '>',
                        value: 250
                    },
                    style: {
                        color: 'black',
                        textOutline: 'none',
                        fontWeight: 'normal'
                    },
                    //showInLegend: true,
                    startAngle: -90,
                    endAngle: 90,
                    center: ['50%', '50%'],
                    size: '180%',
                }
            }
        },
        series: [{
            name: '',
            data: [{
              name: "",
              value: 100
          },
          {
              name: "",
              value:100
          },
          {
              name: "",
              value: 100
          },
          {
              name: "",
              value: 100
          },
          {
              name: "",
              value: 100
          },
          {
              name: "",
              value: 100
          },
          {
              name: "",
              value: 100
          },
          {
              name: "",
              value: 100
          },
          {
              name: "",
              value:100
          }, {
              name: "",
              value: 100
          },
          {
              name: "",
              value: 100
          }]
        }, {
            name: '',
            data: [{
              name: "",
              value: 60
          },
          {
              name: "",
              value:60
          },
          {
              name: "",
              value: 60
          },
          {
              name: "",
              value: 60
          },
          {
              name: "",
              value: 60
          },
          {
              name: "",
              value: 60
          },
          {
              name: "",
              value: 60
          },
          {
              name: "",
              value: 60
          },
          {
              name: "",
              value:60
          }, {
              name: "",
              value: 60
          },
          {
              name: "",
              value: 60
          }]
        },  {
            name: '',
            data: [{
              name: "",
              value: 130
          },
          {
              name: "",
              value:130
          },
          {
              name: "",
              value: 130
          },
          {
              name: "",
              value: 130
          },
          {
              name: "",
              value: 130
          },
          {
              name: "",
              value: 130
          },
          {
              name: "",
              value: 130
          },
          {
              name: "",
              value: 130
          },
          {
              name: "",
              value:130
          }, {
              name: "",
              value: 130
          },
          {
              name: "",
              value: 130
          }]
        }, {
            name: '',
            data: [{
                name: "",
                value: 50
            },
            {
                name: "",
                value:50
            },
            {
                name: "",
                value: 50
            },
            {
                name: "",
                value: 50
            },
            {
                name: "",
                value: 0
            },
            {
                name: "",
                value: 50
            },
            {
                name: "",
                value: 50
            },
            {
                name: "",
                value: 50
            },
            {
                name: "",
                value:50
            }, {
                name: "",
                value:50
            },
            {
                name: "",
                value: 50
            }]
        }, {
            name: 'Item Code',
            color: '#F15C80',
            fontsize: '1px',
            data: [{
                name: "",
                value: 400,
                qty: 708,
                itemCode: "3193457"
            },
            {
              name: "",
              value: 450,
              qty: 924,
              itemCode: "3195001"
          },
          {
              name: "57510967",
              value: 180,
              qty: 534,
              itemCode: "57510967"
          },
          {
              name: "",
              value: 400,
              qty: 534,
              itemCode: "57510967"
          },
          {
              name: "",
              value: 600,
              qty: 1190,
              itemCode: "5751135"
          },
          {
              name: "5751136   ",
              value: 1000,
              qty: 4536,
              itemCode: "5751136"
          },
          {
              name: "5751151",
              value: 750,
              qty: 2062,
              itemCode: "5751151"
          }, 
          {
              name: "",
              value: 400,
              qty: 671,
              itemCode: "57511535"
          },
          {
              name: "",
              value: 400,
              qty: 671,
              itemCode: "57511535"
          },
          {
              name: "",
              value: 400,
              qty: 742,
              itemCode: "57511916"
          },
          {
              name: "",
              value: 400,
              qty: 630,
              itemCode: "57511926"
          },
          {
              name: "",
              value: 150,
              qty: 403,
              itemCode: "57512262"
          },
          {
              name: "0057512288",
              value: 600,
              qty: 1992,
              itemCode: "0057512288"
          },
          {
              name: "57512289",
              value: 750,
              qty: 2009,
              itemCode: "57512289"
          },
          {
              name: "",
              value: 450,
              qty: 116,
              itemCode: "3192850"
          },
          {
              name: "",
              value: 450,
              qty: 161,
              itemCode: "3194243"
          },
          {
              name: "",
              value: 476,
              qty: 161,
              itemCode: "3195002"
          },
          {
              name: "",
              value: 476,
              qty: 44,
              itemCode: "57510099"
          },
          {
              name: "",
              value: 476,
              qty: 400,
              itemCode: "57510714"
          },
          {
              name: "",
              value: 476,
              qty: 11,
              itemCode: "57510965"
          },
          {
              name: "",
              value: 546,
              qty: 11,
              itemCode: "57510966"
          },
          {
              name: "",
              value: 546,
              qty: 120,
              itemCode: "57510968"
          },
          {
              name: "",
              value: 546,
              qty: 14,
              itemCode: "57510974"
          },
          {
              name: "",
              value: 546,
              qty: 16,
              itemCode: "57510975"
          },
          {
              name: "",
              value: 546,
              qty: 88,
              itemCode: "57511089"
          },
          {
              name: "",
              value: 546,
              qty: 1190,
              itemCode: "5751135"
          },
          {
              name: "",
              value: 546,
              qty: 166,
              itemCode: "5751136"
          },
          {
              name: "",
              value: 546,
              qty: 292,
              itemCode: "5751151"
          },
          {
              name: "",
              value: 546,
              qty: 292,
              itemCode: "57511525"
          },
          {
              name: "",
              value: 546,
              qty: 524,
              itemCode: "57511527"
          },
          {
              name: "",
              value: 546,
              qty: 900,
              itemCode: "57511917"
          },
          {
              name: "",
              value: 546,
              qty: 27,
              itemCode: "0057511918"
          },
          {
              name: "",
              value: 546,
              qty: 1,
              itemCode: "57512260"
          },
          {
              name: "",
              value: 546,
              qty: 2,
              itemCode: "57512262"
          },
          {
              name: "",
              value: 546,
              qty: 27,
              itemCode: "57512264"
          },
          {
              name: "",
              value: 546,
              qty: 1,
              itemCode: "57512265"
          },
          {
              name: "",
              value: 546,
              qty: 57,
              itemCode: "57512267"
          },
          {
              name: "",
              value: 546,
              qty: 109,
              itemCode: "57512268"
          },
          {
              name: "",
              value: 546,
              qty: 17,
              itemCode: "57512269"
          },
          {
              name: "",
              value: 546,
              qty: 1,
              itemCode: "57512274"
          },
          {
              name: "",
              value: 546,
              qty: 9,
              itemCode: "57512276"
          },
          {
              name: "",
              value: 546,
              qty: 9,
              itemCode: "57512278"
          },
          {
              name: "",
              value: 546,
              qty: 3,
              itemCode: "57512279"
          },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 26,
          //     itemCode: "57512280"
          // },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 2,
          //     itemCode: "57512281"
          // },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 23,
          //     itemCode: "0057512282"
          // },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 66,
          //     itemCode: "0057512283"
          // },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 730,
          //     itemCode: "0057512284"
          // },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 78,
          //     itemCode: "0057512285"
          // },
          // {
          //     name: "",
          //     value: 546,
          //     qty: 23,
          //     itemCode: "0057512286"
          // },
           ],
           point: {
              events: {
                click: function (event: any,) {
              
              //    location.href = 'http://localhost:4200/#/main/dashboard/sku' ; 
                  location.href = 'http://27.7.57.232:8000/classicwms/#/main/dashboard/sku' ; 
            
                }
              }
            }
        }],
      
        };
    
    
      }
    
    }