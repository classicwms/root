import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService, dropdownelement1 } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { AssignPickerComponent } from "../../assignment/assignment-main/assign-picker/assign-picker.component";
import { DeliveryService } from "../delivery.service";
// import the pdfmake library
import pdfMake from "pdfmake/build/pdfmake";
// importing the fonts and icons needed
import pdfFonts from "../../../../../assets/font/vfs_fonts.js"
import { defaultStyle } from "../../../../config/customStyles";
import { fonts } from "../../../../config/pdfFonts";
import { logo } from "../../../../../assets/font/logo.js";
import { DatePipe } from "@angular/common";
import { Table } from "primeng/table";

// PDFMAKE fonts
pdfMake.vfs = pdfFonts.pdfMake.vfs;
pdfMake.fonts = fonts;

export interface ordermanagement {
  no: string;
  lineno: string;
  partner: string;
  product: string;
  description: string;
  refdocno: string;
  variant: string;
  order: string;
  type: string;
  preoutboundno: string;
  uom: string;
  req: string;
  allocated: string;
  status: string;
  actions: string;

}

const ELEMENT_DATA: ordermanagement[] = [
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },

];
@Component({
  selector: 'app-delivery-main',
  templateUrl: './delivery-main.component.html',
  styleUrls: ['./delivery-main.component.scss']
})
export class DeliveryMainComponent implements OnInit {
  delivery: any[] = [];
  selecteddelivery : any[] = [];
  @ViewChild('deliveryreadyTag') deliveryreadyTag: Table | any;
  screenid: 1067 | undefined;
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  storecodeList: any;
  yesterday: Date;
  currentDate = new Date();
  oneMonthDate = new Date();
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }

  

  assign(): void {

    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '55%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: DeliveryService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    public datePipe: DatePipe) { }
  sub = new Subscription();

  ngOnInit(): void {
    let date = new Date();
    date.setDate(1);

    this.oneMonthDate.setDate(this.currentDate.getDate() - 30);

   

    this.searhform.controls.statusId.patchValue([59, 51, 47]);
    this.getstorename();
    this.search(true);

  }


 
 
  searhform = this.fb.group({
    endDeliveryConfirmedOn: [],
    endDeliveryConfirmedOnFE: [],
    endOrderDate: [],
    endOrderDateFE: [],
    endRequiredDeliveryDate: [],
    endRequiredDeliveryDateFE: [],
    outboundOrderTypeId: [],
    partnerCode: [],
    refDocNumber: [],
    soType: [],
    startDeliveryConfirmedOn: [],
    startDeliveryConfirmedOnFE: [],
    startOrderDate: [],
    startOrderDateFE: [],
    startRequiredDeliveryDate: [],
    startRequiredDeliveryDateFE: [],
    statusId: [[]],
    warehouseId: [[this.auth.warehouseId]],
  });





  dropdownSettings = {
    singleSelection: false,
    text: "Select",
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

  // itemCodeListselected: any[] = [];
  // itemCodeList: any[] = [];

  outboundOrderTypeIdListselected: any[] = [];
  outboundOrderTypeIdList: any[] = [];

  // partnerCodeListselected: any[] = [];
  // partnerCodeList: any[] = [];

  // preOutboundNoselected: any[] = [];
  // preOutboundNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  soTypeListselected: any[] = [];
  soTypeList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];
  warehouseId = this.auth.warehouseId;

  getstorename() {
    //this.spin.show();
    this.sub.add(this.service.GetStoreCode().subscribe(res => {
      this.storecodeList = res;
      console.log(this.storecodeList)
      //this.spin.hide();
    },
      err => {
        this.cs.commonerrorNew(err);;
        //this.spin.hide();
      }));
  }

  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endDeliveryConfirmedOn.patchValue(this.cs.dateNewFormat(this.searhform.controls.endDeliveryConfirmedOnFE.value));
      this.searhform.controls.startDeliveryConfirmedOn.patchValue(this.cs.dateNewFormat(this.searhform.controls.startDeliveryConfirmedOnFE.value));
      this.searhform.controls.endOrderDate.patchValue(this.cs.dateNewFormat(this.searhform.controls.endOrderDateFE.value));
      this.searhform.controls.startOrderDate.patchValue(this.cs.dateNewFormat(this.searhform.controls.startOrderDateFE.value));
      this.searhform.controls.endRequiredDeliveryDate.patchValue(this.cs.dateNewFormat(this.searhform.controls.endRequiredDeliveryDateFE.value));
      this.searhform.controls.startRequiredDeliveryDate.patchValue(this.cs.dateNewFormat(this.searhform.controls.startRequiredDeliveryDateFE.value));


      //patching
      // const itemCode = [...new Set(this.itemCodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.itemCode.patchValue(itemCode);

      // const outboundOrderTypeId = [...new Set(this.outboundOrderTypeIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.outboundOrderTypeId.patchValue(outboundOrderTypeId);

      // const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.partnerCode.patchValue(partnerCode);

      // const preOutboundNo = [...new Set(this.preOutboundNoselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.preOutboundNo.patchValue(preOutboundNo);

      // const refDocNumber = [...new Set(this.refDocNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.refDocNumber.patchValue(refDocNumber);

      // const soType = [...new Set(this.soTypeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.soType.patchValue(soType);

      // const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.statusId.patchValue(statusId);
    }

    this.spin.show();
    this.service.search(this.searhform.value).subscribe(res => {

      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      // let result1 = result.filter((x: any) => x.statusId == 59);

      // result1.forEach((x) => {
      //   // x.noteTypeId = this.noteTypeIdlist.find(y => y.key == x.noteTypeId)?.value;
      // x.partnerCode = this.storecodeList.find(y => y.partnerCode == x.partnerCode)?.partnerName;
      // })

      if (ispageload) {
        //  let tempitemCodeList: any[] = []
        //  const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
        //  itemCode.forEach(x => tempitemCodeList.push({ id: x, itemName: x }));
        //  this.itemCodeList = tempitemCodeList;

        let tempoutboundOrderTypeIdList: any[] = []
        const outboundOrderTypeId = [...new Set(res.map(item => item.outboundOrderTypeId))].filter(x => x != null)
        outboundOrderTypeId.forEach(x => tempoutboundOrderTypeIdList.push({ value: x, label: this.cs.getoutboundOrderType_text(x) }));
        this.outboundOrderTypeIdList = tempoutboundOrderTypeIdList;

        //  let temppartnerCodeList: any[] = []
        //  const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
        //  partnerCode.forEach(x => temppartnerCodeList.push({ id: x, itemName: x }));
        //  this.partnerCodeList = temppartnerCodeList;

        //  let temppreOutboundNoList: any[] = []
        //  const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
        //  preOutboundNo.forEach(x => temppreOutboundNoList.push({ id: x, itemName: x }));
        //  this.preOutboundNoList = temppreOutboundNoList;

        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(res.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;

        let tempsoTypeList: any[] = []
        const soType = [...new Set(res.map(item => item.referenceField1))].filter(x => x != null)
        soType.forEach(x => tempsoTypeList.push({ value: x, label:x }));
        this.soTypeList = tempsoTypeList;

        let tempstatusIdList: any[] = []
        const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
        this.statusIdList = tempstatusIdList;
      }
      this.delivery = res;
      
      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);;
      this.spin.hide();

    });


  }
  reload() {
    this.searhform.reset();
    this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
  }


  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selecteddelivery.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    if (data != 'Display')
      if (this.selecteddelivery[0].statusId == 24) {
        this.toastr.error("Order can't be edited as it's already confirmed.", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";

    if (this.selecteddelivery.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selecteddelivery[0], pageflow: data });
      this.router.navigate(['/main/outbound/delivery-confirm/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/outbound/delivery-confirm/' + paramdata]);
    }
  }

  openConfirm(data: any) {
    debugger
    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/outbound/delivery-confirm/' + paramdata]);

  }
 

  

 

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.delivery.forEach(x => {
      res.push({

        "Status  ": this.cs.getstatus_text(x.statusId),
        " Order No ": x.refDocNumber,
        " Order Category ": x.referenceField1,
        'Order Type': this.cs.getoutboundOrderType_text(x.outboundOrderTypeId),
        "Store": x.partnerCode,
        " Order Lines ": x.referenceField10,
        "Total Qty  ": x.referenceField9,
        " Delivered Lines ": x.referenceField8,
        " Delivered Qty ": x.referenceField7,
        "Req Date  ": this.cs.dateExcel(x.requiredDeliveryDate),
        "Order Date": this.cs.dateExcel(x.deliveryConfirmedOn),
        "Delivery Date ": this.cs.dateExcel(x.deliveryConfirmedOn),
      });

    })
    this.cs.exportAsExcel(res, "Delivery");
  }


  onItemSelect(item: any) {
    console.log(item);
  }

  OnItemDeSelect(item: any) {
    console.log(item);
  }
  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any) {
    console.log(items);
  }

  generatePdf(element: any) {
    this.spin.show();
    this.sub.add(this.service.getOtBoundDeliveryFormPdf(element.refDocNumber, element.warehouseId).subscribe((res: any) => {
      //this.spin.hide();
      console.log(res)
      //Receipt List
      if (res.length > 0) {
        let date = this.datePipe.transform(element.requiredDeliveryDate, 'dd-MMM-yyyy');
        let currentDate = this.datePipe.transform(new Date, 'dd-MMM-yyyy HH:mm')
        var dd: any;
        let headerTable: any[] = [];
        headerTable.push([
          { image: logo.headerLogo, fit: [100, 100], bold: true, fontSize: 9, border: [false, false, false, false] },
          { text: 'Shipment Delivery Sheet', bold: true, fontSize: 12, border: [false, false, false, false] },
          { text: '', bold: true, fontSize: 9, border: [false, false, false, false] },
        ]);
        headerTable.push([
          { text: 'Delivery To:' + res[0].deliveryTo + '-' + res[0].partnerName, bold: true, fontSize: 9, border: [false, false, false, false] },
          { text: '', bold: true, fontSize: 9, border: [false, false, false, false] },
          { text: 'Printed Date:' + currentDate, bold: true, alignment: 'right', fontSize: 9, border: [false, false, false, false] },
        ]);
        headerTable.push([
          { text: 'Order Type :' + res[0].orderType, bold: true, fontSize: 9, border: [false, false, false, false] },
          { text: '', bold: true, fontSize: 9, border: [false, false, false, false] },
          { text: 'Delivery Date:' + date, bold: true, alignment: 'right', fontSize: 9, border: [false, false, false, false] },
        ]);
        dd = {
          pageSize: "A4",
          pageOrientation: "portrait",
          pageMargins: [40, 130, 40, 40],
          header(currentPage: number, pageCount: number, pageSize: any): any {
            return [
              {
                table: {
                  // layout: 'noBorders', // optional
                  // heights: [,60,], // height for each row
                  headerRows: 1,
                  widths: ['*', 150, '*'],
                  body: headerTable
                },
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

        // border: [left, top, right, bottom]
        // border: [false, false, false, false]
        let bodyArray: any[] = [];
        bodyArray.push([
          { text: 'Customer Ref', bold: true, fontSize: 9, border: [false, false, false, true] },
          { text: 'Commodity', bold: true, fontSize: 9, border: [false, false, false, true] },
          { text: 'Description', bold: true, fontSize: 9, border: [false, false, false, true] },
          { text: 'Manf.Code', bold: true, fontSize: 9, border: [false, false, false, true] },
          { text: 'Quantity', bold: true, fontSize: 9, border: [false, false, false, true] }
        ]);
        let totalQuantity = 0;
        res.forEach((delivery, i) => {
          totalQuantity = totalQuantity + (delivery.quantity != null ? delivery.quantity : 0);
          if (delivery.quantity != null && delivery.quantity != 0) {
            bodyArray.push([
              { text: delivery.customerRef != null ? delivery.customerRef : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
              { text: delivery.commodity != null ? delivery.commodity : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
              { text: delivery.description != null ? delivery.description : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
              { text: delivery.manfCode != null ? delivery.manfCode : '', fontSize: 8, border: [false, false, false, true] },
              { text: delivery.quantity != null ? delivery.quantity : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 }
            ])
          }
          if ((i + 1) == res.length) {
            bodyArray.push([
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false] },
              { text: 'Total  :', fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalQuantity, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
            ])
          }
        });
        dd.content.push(
          {
            table: {
              // layout: 'noBorders', // optional
              // heights: [,60,], // height for each row
              headerRows: 1,
              widths: [80, 80, 180, 70, 70],
              body: bodyArray
            }
          },
          '\n\n',
          {
            columns: [
              {
                stack: [
                  { text: 'Special Instruction : ', bold: true, fontSize: 9, lineHeight: 2 },
                  { text: 'InnerWorks Name & Signature :', fontSize: 9, bold: true, lineHeight: 2 },
                  { text: 'Date :', fontSize: 9, bold: true, lineHeight: 2 }
                ]
              },
              {
                stack: [
                  { text: 'Customer Name & Signature : ', bold: true, fontSize: 9, lineHeight: 2 },
                  { text: 'Goods Received in Good Condition :', bold: true, fontSize: 9, lineHeight: 2 }
                ]
              },
            ]
          }
        )

        pdfMake.createPdf(dd).open();
      } else {
        this.toastr.info("No data available", "Pdf Download", {
          timeOut: 2000,
          progressBar: false,
        });
      }
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);;
      this.spin.hide();
    }));
  }
  onChange() {
    const choosen= this.selecteddelivery[this.selecteddelivery.length - 1];   
    this.selecteddelivery.length = 0;
    this.selecteddelivery.push(choosen);
  }
}
