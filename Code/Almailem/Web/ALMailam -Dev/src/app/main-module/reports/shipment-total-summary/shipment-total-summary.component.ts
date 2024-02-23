import { SelectionModel } from '@angular/cdk/collections';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, Injectable, OnInit, ViewChild } from '@angular/core';
import { Validators, FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ReportsService } from '../reports.service';
import { ShipmentDispatchComponent } from '../shipment-dispatch/shipment-dispatch.component';
import pdfMake from "pdfmake/build/pdfmake";
// importing the fonts and icons needed
import pdfFonts from "../../../../assets/font/vfs_fonts.js"
import { defaultStyle } from "../../../config/customStyles";
import { fonts } from "../../../config/pdfFonts";
import { logo } from "../../../../assets/font/logo.js";
import { almaielmLogo} from "../../../../assets/font/almaielm.js"


@Component({
  selector: 'app-shipment-total-summary',
  templateUrl: './shipment-total-summary.component.html',
  styleUrls: ['./shipment-total-summary.component.scss']
})

@Injectable({
  providedIn: 'root'
})


export class ShipmentTotalSummaryComponent implements OnInit {
screenid=3184;
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
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService,private masterService: MasterService,  public auth: AuthService,
    public fb: FormBuilder, public datePipe: DatePipe,  private decimalPipe: DecimalPipe,  public service: ReportsService, private cs: CommonService,     private dispatchService: ShipmentDispatchComponent,
  ) { }

  ngOnInit(): void {
    let previousDate = new Date();
    previousDate.setDate(previousDate.getDate() - 1);
this.dropdownfill();

    this.form.controls.fromDeliveryDate.patchValue(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()-2, 14, 0))
    this.form.controls.toDeliveryDate.patchValue(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()-1, 13, 59))
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
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
        this.multiselectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.item_id == this.auth.warehouseId)
            this.selectedItems = [warehouse];
        })
        //   this.itemCodeList = itemCode;
        //   this.itemCodeList.forEach(x => this.multiItemCodeList.push({ item_id: x.itemCode, item_text: x.itemCode + ' - ' + x.description }))
        //      this.multiselectItemCodeList = this.multiItemCodeList;
this.multiselectWarehouseList=this.cs.removeDuplicatesFromArrayNew(this.multiselectWarehouseList);
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
    if(this.selectedItems == null){
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );
      return;
    }
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
    let transformedFrom = this.datePipe.transform(from, 'MM-dd-yyyyTHH:mm:ss');
    obj.fromDeliveryDate = (transformedFrom == undefined ? '' : transformedFrom.toString());

    let to = new Date(this.form.controls.toDeliveryDate.value);
    let transformedTo = this.datePipe.transform(to, 'MM-dd-yyyyTHH:mm:ss');
    obj.toDeliveryDate = (transformedTo == undefined ? '' : transformedTo.toString());
   obj.warehouseId = this.auth.warehouseId;
   obj.companyCodeId=this.auth.companyId;
   obj.plantId=this.auth.plantId;
   obj.languageId=this.auth.languageId;
console.log(obj);
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
  branchList: any[] = [];
  generatePdf(element: any) {
    let currentDate = this.datePipe.transform(new Date, 'MM-dd-yyyy : hh:mm');
    let fromDate = this.datePipe.transform(this.form.controls.fromDeliveryDate.value, 'dd-MM-yyyy : hh:mm');
    let toDate = this.datePipe.transform(this.form.controls.toDeliveryDate.value, 'dd-MM-yyyy : hh:mm');
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
                  { image: almaielmLogo.headerLogo, fit: [100, 100] }
                ]
              },
              {
                stack: [
                  { text: '\nDelivery Report', alignment: 'center', bold: true, fontSize: 14 }
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

    // dd.content.push({
    //   stack: [
    //     { text: "Amghara Warehouse", alignment: 'center', bold: true, fontSize: 12 }
    //   ]
    // }, '\n\n');

    //const personGroupedByColor = this.cs.groupBy(element.shipmentDeliverySummary, 'branchCode');
    if (element.shipmentDeliverySummary.length > 0) {
      this.branchList = [];
    let groupedTimeTickets = this.cs.groupByData(element.shipmentDeliverySummary, (data: any) => data.branchCode);
    element.shipmentDeliverySummary.forEach(x => this.branchList.push({ value: x.branchCode, label: x.branchDesc }));

    this.branchList = this.cs.removeDuplicatesFromArrayNewstatus(this.branchList);
    
    let summaryTotalQty = 0;
  let summaryShippedQty = 0;
    this.branchList.forEach(x =>{
      dd.content.push({
        stack: [
          { text: x.value + ' - ' + x.label, alignment: 'center', bold: true, fontSize: 12 }
        ]
      }, '\n\n')
      let bodyArray: any[] = [];
      bodyArray.push([
        { text: 'Expected Delivery Date', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Delivery Date/Time', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Branch Code', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Order Type', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# S.O', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# Lines Ordered', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# Lines Shipped ', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '# Lines Picked ', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Ordered Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: 'Shipped Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
        { text: '% Shipped', bold: true, fontSize: 8, border: [false, false, false, true] },
      ]);
     // let totalOrders = 0;
      let totalLinesOrdered = 0;
      let totalLinesShipped = 0;
      let totalOrderedQuantity = 0;
      let totalShippedQuantity = 0;
      let totalPercent = 0;
      let totalPickedQuantity = 0;
      groupedTimeTickets.get(x.value).forEach((data, i) => {
     //   totalOrders = totalOrders + (partner.shipmentDispatchList.length);
        totalLinesOrdered = totalLinesOrdered + (data.lineOrdered != null ? data.lineOrdered : 0);
        totalLinesShipped = totalLinesShipped + (data.lineShipped != null ? data.lineShipped : 0);
        totalPickedQuantity = totalPickedQuantity + (data.pickedQty != null ? data.pickedQty : 0);
        totalOrderedQuantity = totalOrderedQuantity + (data.orderedQty != null ? data.orderedQty : 0);
        totalShippedQuantity = totalShippedQuantity + (data.shippedQty != null ? data.shippedQty : 0);
        totalPercent = totalPercent + (data.percentageShipped != null ? data.percentageShipped : 0);
        

          bodyArray.push([
            { text: data.expectedDeliveryDate != null ? this.datePipe.transform(data.expectedDeliveryDate,'dd-MM-yyyy','GMT' ) : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: data.deliveryDateTime != null ? this.datePipe.transform(data.deliveryDateTime,'dd-MM-yyyy HH:mm','GMT' ) : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: data.branchCode != null ? data.branchCode : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: data.orderType != null ? data.orderType : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: data.so != null ? data.so : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: data.lineOrdered != null ? data.lineOrdered : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: data.lineShipped != null ? data.lineShipped : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: data.pickedQty != null ? data.pickedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: data.orderedQty != null ? data.orderedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: data.shippedQty != null ? data.shippedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: data.percentageShipped != null ? data.percentageShipped : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 }
          ])
          
          if ((i + 1) == groupedTimeTickets.get(x.value).length) {
            summaryTotalQty = summaryTotalQty + (totalOrderedQuantity != null ? totalOrderedQuantity : 0);
            summaryShippedQty = summaryShippedQty + (totalShippedQuantity != null ? totalShippedQuantity : 0);
            bodyArray.push([
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: 'Total', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalLinesOrdered, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalLinesShipped, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalPickedQuantity, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalOrderedQuantity, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalShippedQuantity, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: this.decimalPipe.transform((totalPercent / groupedTimeTickets.get(x.value).length), "1.2-2"), fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
            ])
          }
      })  
      dd.content.push(
        {
          table: {
            headerRows: 1,
            widths: [50, 60, 60, 40, 50, 30, 30, 30, 30, 30, 30],
            body: bodyArray,
          },
        }, '\n'
      )
    })
    if (element.summaryMetrics.length > 0) {
      dd.content.push(
        {
          stack: [{
            text: '',
            alignment: 'center',
            fontSize: 12,
            lineHeight: 1.3,
            bold: true,
            pageBreak: 'before',
            margin: [0, 0, 0, 30]
          }]
        })

      let bottomTable = ['N', 'S'];
    let bodyArray1: any[] = [];
    dd.content.push({
      stack: [
        { text: 'Report Summary', alignment: 'left', bold: true, fontSize: 14 }
      ]
    }, '\n\n')
    bodyArray1.push([
          { text: 'Location', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: 'Type', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: 'No of  Orders', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: '# SKUs Ordered', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: '# SKUs Shipped', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: '#Lines Picked', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: 'Order Qty', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: 'Shipped Qty', bold: true, fontSize: 8, border: [true, true, true, true] },
          { text: '% Shipped', bold: true, fontSize: 8, border: [true, true, true, true] },
        ]
        );
        let totalOrder = 0;
        let lineItems = 0;
        let shippedLines = 0;
        let lineItemPicked = 0;
        let percentageShipped = 0;
        let orderQty = 0;
        let shippedQty = 0;
        element.summaryMetrics.sort((a, b) => (a.partnerCode > b.partnerCode) ? 1 : -1).forEach((summary, i) => {
      //  element.summaryMetrics.forEach((summary, i) => {
          totalOrder = totalOrder + (summary.metricsSummary.totalOrder != null ? summary.metricsSummary.totalOrder : 0);
          lineItems = lineItems + (summary.metricsSummary.lineItems != null ? summary.metricsSummary.lineItems : 0);
          shippedLines = shippedLines + (summary.metricsSummary.shippedLines != null ? summary.metricsSummary.shippedLines : 0);
          lineItemPicked = lineItemPicked + (summary.metricsSummary.lineItemPicked != null ? summary.metricsSummary.lineItemPicked : 0);
          orderQty = orderQty + (summary.metricsSummary.orderedQty != null ? summary.metricsSummary.orderedQty : 0);
          shippedQty = shippedQty + (summary.metricsSummary.deliveryQty != null ? summary.metricsSummary.deliveryQty : 0);
          percentageShipped = percentageShipped + (summary.metricsSummary.percentageShipped != null ? summary.metricsSummary.percentageShipped : 0);

          bodyArray1.push([
              { text: summary.partnerCode != null ? summary.partnerCode : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.type != null ? summary.type == 'N' ? 'Normal' : summary.type == 'S' ? 'Special' : '' : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.totalOrder != null ? summary.metricsSummary.totalOrder : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.lineItems != null ? summary.metricsSummary.lineItems : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.shippedLines != null ? summary.metricsSummary.shippedLines : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.lineItemPicked != null ? summary.metricsSummary.lineItemPicked : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.orderedQty != null ? summary.metricsSummary.orderedQty : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.deliveryQty != null ? summary.metricsSummary.deliveryQty : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
              { text: summary.metricsSummary.percentageShipped != null ? summary.metricsSummary.percentageShipped : '', fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
            ])

            if((i+1) == element.summaryMetrics.length){
              bodyArray1.push([
                { text: '', fontSize: 8,  border: [false, false, false, false], lineHeight: 2 },
                { text: 'Total',  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: totalOrder,   bold: true, fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: lineItems,  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: shippedLines,  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: lineItemPicked,  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: orderQty,  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text: shippedQty,  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
                { text:  this.decimalPipe.transform((percentageShipped / element.summaryMetrics.length), "1.2-2"),  bold: true,  fontSize: 8, border: [true, true, true, true], lineHeight: 2 },
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
                  widths: [120, 50, 40, 40, 40, 40, 40, 40, 40],
                  body: bodyArray1,
                  alignment: "center",
                },
              },
              { width: '*', text: '' },
            ]
          }, '\n'
        )
    }
  }

    else{
      let body11Array: any[] = [];
      body11Array.push([
        { text: 'There are No Shipments for the Day.', bold: true, alignment: 'center', fontSize: 18, border: [false, false, false, false] },
       
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





}