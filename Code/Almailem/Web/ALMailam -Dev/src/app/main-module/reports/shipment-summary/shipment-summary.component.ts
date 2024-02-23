import { SelectionModel } from '@angular/cdk/collections';
import { DatePipe } from '@angular/common';
import { Component, Injectable, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of, Subscription } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from 'src/app/common-service/common-service.service';
import { ReportsService } from '../reports.service';
// import the pdfmake library
import pdfMake from "pdfmake/build/pdfmake";
// importing the fonts and icons needed
import pdfFonts from "../../../../assets/font/vfs_fonts.js"
import { defaultStyle } from "../../../config/customStyles";
import { fonts } from "../../../config/pdfFonts";
import { logo } from "../../../../assets/font/logo.js";
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { AuthService } from 'src/app/core/Auth/auth.service';
import { MasterService } from 'src/app/shared/master.service';
import { ShipmentDispatchComponent } from '../shipment-dispatch/shipment-dispatch.component';

@Injectable({
  providedIn: 'root'
})

@Component({
  selector: 'app-shipment-summary',
  templateUrl: './shipment-summary.component.html',
  styleUrls: ['./shipment-summary.component.scss']
})
export class ShipmentSummaryComponent implements OnInit {

  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;
  div1Function() {
    this.table = !this.table;
  }
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  showFiller = false;
  displayedColumns: string[] = ['warehouseno', 'countno', 'by', 'preinboundno', 'status', 'damage', 'hold', 'available', 'stype', 'time',];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);
  form = this.fb.group({
    fromDeliveryDate: [, [Validators.required]],
    toDeliveryDate: [, [Validators.required]],
  });

  constructor(
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService,private masterService: MasterService,  private auth: AuthService,
    public fb: FormBuilder, public datePipe: DatePipe, public service: ReportsService, private cs: CommonService,     private dispatchService: ShipmentDispatchComponent,
  ) { }

  ngOnInit(): void {
    let previousDate = new Date();
    previousDate.setDate(previousDate.getDate() - 1);
this.dropdownfill();
    this.form.controls.fromDeliveryDate.patchValue(previousDate)
    this.form.controls.toDeliveryDate.patchValue(previousDate)
  }

  // file: File;
  // onFilechange(event: any) {
  //   console.log(event)
  //   this.file = event.target.files[0]
  // }

  // uploadFile() {
  //   if (this.file) {
  //     console.log(this.file)
  //     this.spin.show();
  //     this.service.uploadfile(this.file, 'document').subscribe((resp: any) => {
  //       this.toastr.success("Document uploaded successfully.", "Notification", {
  //         timeOut: 2000,
  //         progressBar: false,
  //       });
  //       this.spin.hide();
  //     })
  //   }
  // }

  dropdownSettings: IDropdownSettings = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  warehouseList: any[] = [];
  multiWarehouseList: any[] = [];
  multiselectWarehouseList: any[] = [];
  selectedItems: any[] = [];

  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
      //  itemCode: this.reportService.getItemCodeDetails().pipe(catchError(err => of(err))),
      storageSection: this.masterService.getStorageSectionMasterDetails().pipe(catchError(err => of(err)))

    })
      .subscribe(({ warehouse, storageSection }) => {
        //   .subscribe(({ warehouse, itemCode, storageSection }) => {
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
        //   this.itemCodeList = itemCode;
        //   this.itemCodeList.forEach(x => this.multiItemCodeList.push({ item_id: x.itemCode, item_text: x.itemCode + ' - ' + x.description }))
        //      this.multiselectItemCodeList = this.multiItemCodeList;

      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  selectedWarehouseList: any[] = [];
  sendingWarehouseList: any[] = [];
  

  reset(){
    this.form.reset();
  }
  filtersearch() {
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

    // let obj: any = {};
    // let from = new Date(this.form.controls.fromDeliveryDate.value);
    // let transformedFrom = this.datePipe.transform(from, 'MM-dd-yyyy');
    // obj.fromDeliveryDate = (transformedFrom == undefined ? '' : transformedFrom.toString());

    // let to = new Date(this.form.controls.toDeliveryDate.value);
    // let transformedTo = this.datePipe.transform(to, 'MM-dd-yyyy');
    // obj.toDeliveryDate = (transformedTo == undefined ? '' : transformedTo.toString());

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
    this.sub.add(this.service.getShipmentDeliverySummarynew(obj).subscribe(res => {
      this.spin.hide();
      if (res) {
        this.generatePdf(res);
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

  generatePdf(element: any) {
    let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy : hh:mm');
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
                  { text: '\nShipment Delivery Report', alignment: 'center', bold: true, fontSize: 14 }
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
          margin: [0, 2, 10, 0]
        }
      )
    }
   pdfMake.createPdf(dd).open();


    this.spin.hide();
  }
  




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

  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
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

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }

    // downloadexcel() {
  //   // if (excel)
  //   var res: any = [];
  //   this.dataSource.data.forEach(x => {
  //     res.push({

  //       "Printed on":this.cs.dateapi(x.orderReceivedDate),
  //       'SO Number': x.soNumber,
  //       'Delivery Date / Time': this.cs.dateapi(x.orderReceivedDate),
  //       "Branch Code ": x.customerName,
  //       "Order Type": x.sku,
  //       "Line Ordered": x.skuDescription,
  //       " Line Shipped": x.orderedQty,
  //       'Ordered Qty ': x.deliveredQty,
  //       'Shipped Qty': this.cs.dateapi(x.orderReceivedDate),
  //       ' % Shipped ': x.deliveryConfirmedOn,
  //       'Exp Delivery Date': this.cs.dateapi(x.expectedDeliveryDate),
  //       '% of Delivered': x.percentageOfDelivered,
  //       'Status': x.statusId,
  //       // 'Date': this.cs.dateapi(x.createdOn),
  //     });

  //   })
  //   this.cs.exportAsExcel(res, "Shipment Delivery Report");
  // }


scheduleSearch(warehouseId) {
  const todayDate = new Date();  // Gives today's date
  const yesterdayDate = new Date(); 
  const todaysDayOfMonth = todayDate.getDate();
  yesterdayDate.setDate(todaysDayOfMonth - 1);
  
    let obj: any = {};
    // obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
    // obj.toDeliveryDate = this.cs.uploadDate(todayDate);
    obj.fromDeliveryDate = '01-27-2023';
    obj.toDeliveryDate = '03-27-2023';
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
      // obj.fromDeliveryDate = this.cs.uploadDate(yesterdayDate);
      // obj.toDeliveryDate = this.cs.uploadDate(todayDate);
      obj.fromDeliveryDate = '01-27-2023';
      obj.toDeliveryDate = '03-27-2023';
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
    let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy : hh:mm');
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
                  { text: '\nShipment Delivery Report', alignment: 'center', bold: true, fontSize: 14 }
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
           this.dispatchService.scheduleSearch(110);
          }
        }
});
    this.spin.hide();
  }
}