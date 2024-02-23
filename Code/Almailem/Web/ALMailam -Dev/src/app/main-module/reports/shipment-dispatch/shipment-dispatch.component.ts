import { SelectionModel } from '@angular/cdk/collections';
import { Component, Injectable, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { catchError } from "rxjs/operators";

import { forkJoin, of, Subscription } from "rxjs";
import { CommonService } from 'src/app/common-service/common-service.service';
import { ReportsService } from '../reports.service';
// import the pdfmake library
import pdfMake from "pdfmake/build/pdfmake";
// importing the fonts and icons needed
import { DatePipe, DecimalPipe } from '@angular/common';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { AuthService } from 'src/app/core/Auth/auth.service';
import { MasterService } from 'src/app/shared/master.service';
import { logo } from "../../../../assets/font/logo.js";
import pdfFonts from "../../../../assets/font/vfs_fonts.js";
import { defaultStyle } from "../../../config/customStyles";
import { fonts } from "../../../config/pdfFonts";
// import { saveAs } from 'file-saver';

// PDFMAKE fonts
pdfMake.vfs = pdfFonts.pdfMake.vfs;
pdfMake.fonts = fonts;

@Injectable({
  providedIn: 'root'
})


@Component({
  selector: 'app-shipment-dispatch',
  templateUrl: './shipment-dispatch.component.html',
  styleUrls: ['./shipment-dispatch.component.scss']
})
export class ShipmentDispatchComponent implements OnInit {


  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;

  form = this.fb.group({
    fromDeliveryDate: [, [Validators.required]],
    toDeliveryDate: [, [Validators.required]],
  });


  div1Function() {
    this.table = !this.table;
  }
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  toggleFloat() {

    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;

  }
  showFiller = false;
  displayedColumns: string[] = ['warehouseno', 'countno', 'by', 'preinboundno', 'status', 'damage', 'hold', 'available', 'stype'];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];
  constructor(
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService, private fb: FormBuilder,
    private cs: CommonService, private masterService: MasterService, private auth: AuthService, public service: ReportsService, public datePipe: DatePipe, private decimalPipe: DecimalPipe) { }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  ngOnInit(): void {
    // this.auth.isuserdata();
    this.dropdownfill();
    let previousDate = new Date();
    previousDate.setDate(previousDate.getDate() - 1);

    this.form.controls.fromDeliveryDate.patchValue(previousDate)
    this.form.controls.toDeliveryDate.patchValue(previousDate)
  }

  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);

  warehouseList: any[] = [];
  multiWarehouseList: any[] = [];
  multiselectWarehouseList: any[] = [];
  selectedItems: any[] = [];


  dropdownSettings: IDropdownSettings = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };


  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
    })
      .subscribe(({ warehouse }) => {
        if(this.auth.userTypeId != 3){
          this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        }else{
          this.warehouseList = warehouse
        }
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ item_id: x.warehouseId, item_text: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
        this.multiselectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.item_id == this.auth.warehouseId)
            this.selectedItems = [warehouse];
        })
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }


  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination

  selectedWarehouseList: any[] = [];
  sendingWarehouseList: any[] = [];



  scheduleSearch(wareHouseId) {
    const todayDate = new Date();  // Gives today's date
    const yesterdayDate = new Date(); 
    const todaysDayOfMonth = todayDate.getDate();
    yesterdayDate.setDate(todaysDayOfMonth - 1);
      let obj: any = {};
      // obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
      // obj.toDeliveryDate = this.cs.uploadDate(todayDate);
      obj.fromDeliveryDate = '01-27-2023';
      obj.toDeliveryDate = '03-27-2023';
      obj.warehouseId = wareHouseId;
  
      this.spin.show();
      this.sub.add(this.service.getShipmentDispatchSummary(obj).subscribe(res => {
        this.spin.hide();
        if (res) {
          this.schedulePdf(res, wareHouseId, obj);
         }
          // /sessionStorage.clear();
      },
        err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
    }
    scheduleSearch111(wareHouseId) {
      const todayDate = new Date();  // Gives today's date
      const yesterdayDate = new Date(); 
      const todaysDayOfMonth = todayDate.getDate();
      yesterdayDate.setDate(todaysDayOfMonth - 1);
        let obj: any = {};
        // obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
        // obj.toDeliveryDate = this.cs.uploadDate(todayDate);
        obj.fromDeliveryDate = '11-15-2022';
        obj.toDeliveryDate = '11-22-2022';
        obj.warehouseId = wareHouseId;
    
        this.spin.show();
        this.sub.add(this.service.getShipmentDispatchSummary(obj).subscribe(res => {
          this.spin.hide();
          if (res) {
            this.schedulePdf(res, wareHouseId, obj);
           }
            // /sessionStorage.clear();
        },
          err => {
            this.cs.commonerrorNew(err);
            this.spin.hide();
          }));
      }

    schedulePdf(element: any, wareHouseId, obj) {
    if(element){
      element = element.shipmentDispatch.sort((a, b) => (a.header.partnerCode > b.header.partnerCode) ? 1 : -1)
    }
      let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy hh:mm');
      // let fromDate = this.datePipe.transform(this.form.controls.fromDeliveryDate.value, 'MM-dd-yyyy');
      // let toDate = this.datePipe.transform(this.form.controls.toDeliveryDate.value, 'MM-dd-yyyy');
      let fromDate =  obj.fromDeliveryDate;
      let toDate =  obj.toDeliveryDate;
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
                    { text: 'Selection Date: ' + fromDate + ' - ' + toDate, alignment: 'right', fontSize: 7 },
                    { text: 'Run Date: ' + currentDate, alignment: 'right', fontSize: 7 },
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
  
          //  pdfMake.createPdf(dd).open();
          const pdfDocGenerator = pdfMake.createPdf(dd);
          pdfDocGenerator.getBlob((blob) => {
          // var file = new File([blob], "Shipment Delivery Summary Report" + (new Date().getMonth() + 1) + new Date().getFullYear());
          var file = new File([blob], wareHouseId + '_TV_'+"Shipment Dispatch Summary_"  + (new Date().getDate()) +'-'+ (new Date().getMonth() + 1) + '-' + new Date().getFullYear() + '_' +this.cs.timeFormat(new Date()) + ".pdf");
            if(file){
              this.service.uploadfile(file, 'document').subscribe((resp: any) => {});
              if(wareHouseId == 110){
                this.scheduleSearch111(111)
              }
              if(wareHouseId == 111){
               this.service.sendMail().subscribe(res => {})
              }
            }
      });
    }
  filtersearch(type: any) {
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );
      return;
    }

    this.selectedWarehouseList = [];
    this.selectedItems.forEach(element => {
      this.selectedWarehouseList.push(element.item_id);
    });
    this.sendingWarehouseList = this.selectedWarehouseList;

    let obj: any = {};
    let from = new Date(this.form.controls.fromDeliveryDate.value);
    let transformedFrom = this.datePipe.transform(from, 'MM-dd-yyyy');
    obj.fromDeliveryDate = (transformedFrom == undefined ? '' : transformedFrom.toString());

    let to = new Date(this.form.controls.toDeliveryDate.value);
    let transformedTo = this.datePipe.transform(to, 'MM-dd-yyyy');
    obj.toDeliveryDate = (transformedTo == undefined ? '' : transformedTo.toString());
    obj.warehouseId = this.sendingWarehouseList;

    this.spin.show();
    this.sub.add(this.service.getShipmentDispatchSummary(obj).subscribe(res => {
      this.spin.hide();
      if (res) {
        this.generatePdf(res.shipmentDispatch, type);
      }
      // this.table = true;
      // this.search = false;
      //this.fullscreen = true;
      // this.back = true;
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
  }
  togglesearch() {
    this.search = false;
    this.table = true;
    this.fullscreen = false;
    this.back = true;
  }
  backsearch() {
    this.table = true;
    this.search = true;
    this.fullscreen = true;
    this.back = false;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseno + 1}`;
  }

  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  generatePdf(element: any, actionType: any) {
    element = element.sort((a, b) => (a.header.partnerCode > b.header.partnerCode) ? 1 : -1)
    let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy hh:mm');
    let fromDate = this.datePipe.transform(this.form.controls.fromDeliveryDate.value, 'MM-dd-yyyy');
    let toDate = this.datePipe.transform(this.form.controls.toDeliveryDate.value, 'MM-dd-yyyy');
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
                  { text: 'Selection Date: ' + fromDate + ' - ' + toDate, alignment: 'right', fontSize: 7 },
                  { text: 'Run Date: ' + currentDate, alignment: 'right', fontSize: 7 },
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
          margin: [0, 2, 10, 0]
        }
      )
    }

    if (actionType == 'PDF') {
      pdfMake.createPdf(dd).open();
    } else {
      const pdfDocGenerator = pdfMake.createPdf(dd);
      pdfDocGenerator.getBuffer((buffer) => {
        let file = new File([buffer], "dispatch.pdf", { type: 'application/pdf' });
        console.log(file)
        // saveAs(file,  'dispatch.pdf')
      });
      this.spin.hide();
    }
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Customer Code": x.partnerCode,
        'SO Number': x.soNumber,
        'Order Receipt Date': this.cs.dateapi(x.orderReceiptTime),
        "Order Receipt Time": x.orderReceiptTime,
        "Lines Ordered": x.linesOrdered,
        "Lines Shipped": x.linesShipped,
        "Ordered Qty": x.orderedQty,
        'Shipped Qty': x.shippedQty,
        '% Shipped': x.perentageShipped,
        // 'Status': x.statusId,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Stock List Report");
  }
  reset(){
    this.form.reset();
  }
}
