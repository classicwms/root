import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, Injectable, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { defaultStyle } from 'src/app/config/customStyles';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ReportsService } from '../reports.service';
import { ShipmentDispatchComponent } from '../shipment-dispatch/shipment-dispatch.component';
import { logo } from "../../../../assets/font/logo.js";
import { Subscription } from 'rxjs';
import pdfMake from "pdfmake/build/pdfmake";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-schedule-report',
  templateUrl: './schedule-report.component.html',
  styleUrls: ['./schedule-report.component.scss']
})
export class ScheduleReportComponent implements OnInit {

  constructor(
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService,private masterService: MasterService,  private auth: AuthService,
    public fb: FormBuilder, public datePipe: DatePipe, public service: ReportsService, private cs: CommonService,     private dispatchService: ShipmentDispatchComponent,
    private decimalPipe: DecimalPipe) { }

  ngOnInit(): void {
  }
  sub = new Subscription();

  scheduleSearch(warehouseId) {
    const todayDate = new Date();  // Gives today's date
    const yesterdayDate = new Date();
    const todaysDayOfMonth = todayDate.getDate();
    yesterdayDate.setDate(todaysDayOfMonth - 1);

      let obj: any = {};
      obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
      obj.toDeliveryDate = this.cs.uploadDate(todayDate);
      // obj.fromDeliveryDate = '01-27-2023';
      // obj.toDeliveryDate = '03-27-2023';
      obj.warehouseId = warehouseId;

      this.spin.show();
      this.sub.add(this.service.getShipmentDeliverySummarynew(obj).subscribe(res => {
        this.spin.hide();
        if (res) {
          this.schedulePdf(res, warehouseId, obj);
         }
          // /sessionStorage.clear();
      },
        err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
    }

  scheduleSearch111(warehouseId) {
    const todayDate = new Date();  // Gives today's date
    const yesterdayDate = new Date();
    const todaysDayOfMonth = todayDate.getDate();
    yesterdayDate.setDate(todaysDayOfMonth - 1);

      let obj: any = {};
      obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
      obj.toDeliveryDate = this.cs.uploadDate(todayDate);
      // obj.fromDeliveryDate = '01-27-2023';
      // obj.toDeliveryDate = '03-27-2023';
      obj.warehouseId = warehouseId;

      this.spin.show();
      this.sub.add(this.service.getShipmentDeliverySummarynew(obj).subscribe(res => {
        this.spin.hide();
        if (res) {
          this.schedulePdf(res, warehouseId, obj);
         }
          // /sessionStorage.clear();
      },
        err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
    }
  schedulePdf(element: any, warehouseId, obj) {
    const todayDate = new Date();  // Gives today's date
    const yesterdayDate = new Date();
    const todaysDayOfMonth = todayDate.getDate();
    yesterdayDate.setDate(todaysDayOfMonth - 1);
    let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy : hh:mm');
    let fromDate = this.datePipe.transform(yesterdayDate, 'dd-MM-yyyy');
    let toDate = this.datePipe.transform(todayDate, 'dd-MM-yyyy');
    var dd: any;
    dd = {
      pageSize: "A4",
      pageOrientation: "portrait",
      pageMargins: [40, 70, 40, 40],
      header(): any {
        return [
          {
            columns: [
              {
                stack: [
                  { image: logo.headerLogo, fit: [100, 100] }
                ]
              },
              {
                stack: [
                  { text: '\nShipment Delivery Report', alignment: 'center', bold: true, fontSize: 14 }
                ],
                width: 270
              },
              {
                stack: [
                  // { text: 'Selection Date: ' + fromDate + ' : 08:00 AM' + ' - ' + toDate + ' : 007:59 AM', alignment: 'right', fontSize: 7 },
                  // { text: 'Run Date: ' + currentDate + ' : 12:00 PM', alignment: 'right', fontSize: 7 },
                  { text: 'Selection Date', alignment: 'left', fontSize: 7 },
                  { text: fromDate + ' : 08:00 AM' + ' - ' + toDate + ' : 07:59 AM', alignment: 'left', fontSize: 7 },
                ],
                width: 170
              }
            ],
            margin: [40, 30]
          }
        ]
      },
      footer(currentPage: number, pageCount: number, pageSize: any): any {
        return [{
          text: 'Page ' + currentPage + ' of ' + pageCount,
          style: 'header',
          alignment: 'center',
          bold: true,
          fontSize: 6
        }]
      },
      content: ['\n'],
      defaultStyle
    };

    dd.content.push({
      stack: [
        { text: "Amghara Warehouse", alignment: 'center', bold: true, fontSize: 12 }
      ]
    }, '\n\n')

    //dispatch List
    if (element.shipmentDeliverySummary.length > 0) {
      // border: [left, top, right, bottom]
      // border: [false, false, false, false]

      let bodyArray: any[] = [];
      bodyArray.push([
        { text: 'Expected Delivery Date', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Delivery Date/Time', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Branch Code', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Order Type', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# S.O', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# Lines Ordered', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# Lines Shipped ', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Ordered Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Shipped Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '% Shipped', bold: true, fontSize: 8, border: [false, false, false, true] },
      ]);

      element.shipmentDeliverySummary.forEach(data => {
        bodyArray.push([
          { text: data.expectedDeliveryDate != null ? this.datePipe.transform(data.expectedDeliveryDate,'dd-MM-yyyy','GMT' ) : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
          { text: data.deliveryDateTime != null ? this.datePipe.transform(data.deliveryDateTime,'dd-MM-yyyy HH:mm','GMT' ) : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
          { text: data.branchCode != null ? data.branchCode : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
          { text: data.orderType != null ? data.orderType : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
          { text: data.so != null ? data.so : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
          { text: data.lineOrdered != null ? data.lineOrdered : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
          { text: data.lineShipped != null ? data.lineShipped : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
          { text: data.orderedQty != null ? data.orderedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
          { text: data.shippedQty != null ? data.shippedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
          { text: data.percentageShipped != null ? data.percentageShipped : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 }
        ])
      });
      dd.content.push(
        {
          table: {
            // layout: 'noBorders', // optional
            // heights: [,60,], // height for each row
            headerRows: 1,
            widths: [50, 60, 110, 40, 50, 30, 30, 30, 30, 30],
            body: bodyArray
          },
        }, '\n'
      )

      if (element.summaryMetrics.length > 0) {
        let bottomTable = ['N', 'S'];
        bottomTable.forEach((table, index) => {
          bodyArray = [];
          bodyArray.push([
            { text: '', bold: true, fontSize: 8, border: [false, false, false, false] },
            { text: index == 0 ? 'N-Normal' : 'S-Special', colSpan: 3, bold: true, alignment: 'center', fontSize: 8, border: [true, true, true, true] },
            {},
            {},
          ], [
            { text: '', bold: true, fontSize: 8, border: [false, false, false, false] },
            { text: 'Total Order', bold: true, fontSize: 8, border: [true, true, true, true] },
            { text: '# Line items', bold: true, fontSize: 8, border: [true, true, true, true] },
            { text: '% Shipped', bold: true, fontSize: 8, border: [true, true, true, true] },
          ]
          );
          element.summaryMetrics.forEach(summary => {
            if (summary.type == table) {
              bodyArray.push([
                { text: summary.partnerCode != null ? summary.partnerCode : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: summary.metricsSummary.totalOrder != null ? summary.metricsSummary.totalOrder : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: summary.metricsSummary.lineItems != null ? summary.metricsSummary.lineItems : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: summary.metricsSummary.percentageShipped != null ? summary.metricsSummary.percentageShipped : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 }
              ])
            }
          });
          dd.content.push(
            {
              columns: [
                { width: '*', text: '' },
                {
                  width: 'auto',
                  table: {
                    // layout: 'noBorders', // optional
                    // heights: [,60,], // height for each row
                    widths: [100, 60, 60, 60],
                    body: bodyArray,
                    alignment: "center"
                  },
                },
                { width: '*', text: '' },
              ]
            }, '\n'
          )
        })
      }
    }   
    else{
      let body11Array: any[] = [];
      body11Array.push([
        { text: 'There are No shipments for the Day.', bold: true, alignment: 'center', fontSize: 18, border: [false, false, false, false] },
       
      ]);
      
      dd.content.push(
        {
          table: {
            headerRows: 1,
            widths: ['*'],
            body: body11Array
          },
          margin: [50, 150, 50, 0]
        }
      )
    } 
    
  //  pdfMake.createPdf(dd).open();
      const pdfDocGenerator = pdfMake.createPdf(dd);
      pdfDocGenerator.getBlob((blob) => {
      // var file = new File([blob], "Shipment Delivery Summary Report" + (new Date().getMonth() + 1) + new Date().getFullYear());
      var file = new File([blob], warehouseId + '_TV_'+ "Shipment_Delivery_Summary_"  + (new Date().getDate()) +'-'+ (new Date().getMonth() + 1) + '-' + new Date().getFullYear() + '_' +this.cs.timeFormat(new Date()) + ".pdf");
        if(file){
          this.service.uploadfile(file, 'document').subscribe((resp: any) => {});
          if(warehouseId == 110){
            this.scheduleSearch111(111)
          }
          if(warehouseId == 111){
           this.dispatctScheduleSearch(110);
          }
        }
});
    this.spin.hide();
  }



  dispatctScheduleSearch(wareHouseId) {
    const todayDate = new Date();  // Gives today's date
    const yesterdayDate = new Date();
    const todaysDayOfMonth = todayDate.getDate();
    yesterdayDate.setDate(todaysDayOfMonth - 1);
      let obj: any = {};
      obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
      obj.toDeliveryDate = this.cs.uploadDate(todayDate);
      // obj.fromDeliveryDate = '01-27-2023';
      // obj.toDeliveryDate = '03-27-2023';
      obj.warehouseId = wareHouseId;

      this.spin.show();
      this.sub.add(this.service.getShipmentDispatchSummary(obj).subscribe(res => {
        this.spin.hide();
        if (res) {
          this.dispatctSchedulePdf(res, wareHouseId, obj);
         }
          // /sessionStorage.clear();
      },
        err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
    }
    dispatctScheduleSearch111(wareHouseId) {
      const todayDate = new Date();  // Gives today's date
      const yesterdayDate = new Date();
      const todaysDayOfMonth = todayDate.getDate();
      yesterdayDate.setDate(todaysDayOfMonth - 1);
        let obj: any = {};
        obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
        obj.toDeliveryDate = this.cs.uploadDate(todayDate);
        obj.warehouseId = wareHouseId;

        this.spin.show();
        this.sub.add(this.service.getShipmentDispatchSummary(obj).subscribe(res => {
          this.spin.hide();
          if (res) {
            this.dispatctSchedulePdf(res, wareHouseId, obj);
           }
            // /sessionStorage.clear();
        },
          err => {
            this.cs.commonerrorNew(err);
            this.spin.hide();
          }));
      }

      dispatctSchedulePdf(element: any, wareHouseId, obj) {
        const todayDate = new Date();  // Gives today's date
        const yesterdayDate = new Date();
        const todaysDayOfMonth = todayDate.getDate();
        yesterdayDate.setDate(todaysDayOfMonth - 1);
    if(element){
      element = element.shipmentDispatch.sort((a, b) => (a.header.partnerCode > b.header.partnerCode) ? 1 : -1)
    }
      let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy hh:mm');
      let fromDate = this.datePipe.transform(yesterdayDate, 'dd-MM-yyyy');
      let toDate = this.datePipe.transform(todayDate, 'dd-MM-yyyy');
      var dd: any;
      dd = {
        pageSize: "A4",
        pageOrientation: "portrait",
        pageMargins: [40, 70, 40, 40],
        header(): any {
          return [
            {
              columns: [
                {
                  stack: [
                    { image: logo.headerLogo, fit: [100, 100] }
                  ]
                },
                {
                  stack: [
                    { text: '\nShipment Dispatch Summary Report', alignment: 'center', bold: true, fontSize: 14 }
                  ],
                  width: 270
                },
                {
                  stack: [
                    { text: 'Selection Date', alignment: 'left', fontSize: 7 },
                    { text: fromDate + ' : 08:00 AM' + ' - ' + toDate + ' : 07:59 AM', alignment: 'left', fontSize: 7 },
                  ],
                  width: 170
                }
              ],
              margin: [40, 20]
            }
          ]
        },
        footer(currentPage: number, pageCount: number, pageSize: any): any {
          return [{
            text: 'Page ' + currentPage + ' of ' + pageCount,
            style: 'header',
            alignment: 'center',
            bold: true,
            fontSize: 6
          }]
        },
        content: ['\n'],
        defaultStyle
      };

      //dispatch List
      if (element.length > 0) {
        // border: [left, top, right, bottom]
        // border: [false, false, false, false]
        let totalOrders = 0;
        let totalLinesOrdered = 0;
        let totalLinesShipped = 0;
        let totalOrderedQuantity = 0;
        let totalShippedQuantity = 0;
        let totalPercent = 0;
        element.forEach((partner, index) => {
          totalOrders = totalOrders + (partner.shipmentDispatchList.length);
          totalLinesOrdered = totalLinesOrdered + (partner.header.totalLinesOrdered != null ? partner.header.totalLinesOrdered : 0);
          totalLinesShipped = totalLinesShipped + (partner.header.totalLinesShipped != null ? partner.header.totalLinesShipped : 0);
          totalOrderedQuantity = totalOrderedQuantity + (partner.header.totalOrderedQty != null ? partner.header.totalOrderedQty : 0);
          totalShippedQuantity = totalShippedQuantity + (partner.header.totalShippedQty != null ? partner.header.totalShippedQty : 0);

          totalPercent = totalPercent + (partner.header.averagePercentage != null ? partner.header.averagePercentage : 0);

          dd.content.push({
            stack: [
              { text: partner.header.partnerCode, alignment: 'center', bold: true, fontSize: 12 }
            ]
          }, '\n\n')

          let bodyArray: any[] = [];
          bodyArray.push([
            { text: 'S.O Number', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Order Receipt Date', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Order Receipt Time', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: '# Lines Ordered', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: '# Lines Shipped ', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Ordered Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Shipped Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: '%', bold: true, fontSize: 8, border: [false, false, false, true] },
          ]);
          partner.shipmentDispatchList.forEach((dispatch, i) => {
            bodyArray.push([
              { text: dispatch.soNumber != null ? dispatch.soNumber : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.orderReceiptTime != null ? this.datePipe.transform(dispatch.orderReceiptTime, 'MM-dd-yyyy','GMT') : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.orderReceiptTime != null ? this.datePipe.transform(dispatch.orderReceiptTime, 'HH:mm','GMT') : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.linesOrdered != null ? dispatch.linesOrdered : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.linesShipped != null ? dispatch.linesShipped : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.orderedQty != null ? dispatch.orderedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.shippedQty != null ? dispatch.shippedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
              { text: dispatch.perentageShipped != null ? dispatch.perentageShipped : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 }
            ])
            if ((i + 1) == partner.shipmentDispatchList.length) {
              bodyArray.push([
                { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
                { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
                { text: 'Total :', fontSize: 9, bold: true, border: [false, false, false, false] },
                { text: partner.header.totalLinesOrdered, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
                { text: partner.header.totalLinesShipped, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
                { text: partner.header.totalOrderedQty, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
                { text: partner.header.totalShippedQty, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
                { text: partner.header.averagePercentage, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              ])
            }
            if ((index + 1) == element.length && (i + 1) == partner.shipmentDispatchList.length) {
              bodyArray.push([
                { text: 'Total Orders:' + totalOrders, fontSize: 9, bold: true, border: [false, false, false, false] },
                { text: '', fontSize: 9, bold: true, border: [false, false, false, false] },
                { text: 'Sum of Items:', fontSize: 9, bold: true, border: [false, false, false, false] },
                { text: totalLinesOrdered, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false] },
                { text: totalLinesShipped, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false] },
                { text: totalOrderedQuantity, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false] },
                { text: totalShippedQuantity, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false] },
                { text:  this.decimalPipe.transform((totalShippedQuantity / totalOrderedQuantity)*100, "1.2-2"), fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false] },
              ])
            }
          });
          dd.content.push(
            {
              table: {
                // layout: 'noBorders', // optional
                // heights: [,60,], // height for each row
                headerRows: 1,
                widths: [50, 110, 50, 40, 40, 40, 40, 60],
                body: bodyArray
              },
            }, '\n'
          )
        });
      }
      else{
        let body11Array: any[] = [];
        body11Array.push([
          { text: 'There are No shipments for the Day.', bold: true, alignment: 'center', fontSize: 18, border: [false, false, false, false] },
         
        ]);
        
        dd.content.push(
          {
            table: {
              headerRows: 1,
              widths: ['*'],
              body: body11Array
            },
            margin: [50, 150, 50, 0]
          }
        )
      }

          //  pdfMake.createPdf(dd).open();
          const pdfDocGenerator = pdfMake.createPdf(dd);
          pdfDocGenerator.getBlob((blob) => {
          // var file = new File([blob], "Shipment Delivery Summary Report" + (new Date().getMonth() + 1) + new Date().getFullYear());
            var file = new File([blob], wareHouseId + '_TV_'+"Shipment Dispatch Summary_"  + (new Date().getDate()) +'-'+ (new Date().getMonth() + 1) + '-' + new Date().getFullYear()  + '_' +this.cs.timeFormat(new Date()) + ".pdf");
            if(file){
              this.service.uploadfile(file, 'document').subscribe((resp: any) => {});
              if(wareHouseId == 110){
                this.dispatctScheduleSearch111(111)
              }
              if(wareHouseId == 111){
            //  this.service.sendMail().subscribe(res => {})
            console.log('job done')
              this.auth.logout();
              }
            }
      });
    }

}
