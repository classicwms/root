import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { Location } from "@angular/common";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { AssignHEComponent } from "src/app/main-module/Inbound/Item receipt/item-create/assign-he/assign-he.component";
import { DeliveryService } from "../delivery.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
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

export interface clientcategory {

  lineno: string;
  supcode: string;
  one: string;
  two: string;
  three: string;
  accepted: string;
  qa: string;
  delivery: string;

}
const ELEMENT_DATA: clientcategory[] = [
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', qa: 'AP-Editable', delivery: 'AP-Editable', },

];
@Component({
  selector: 'app-delivery-confirm',
  templateUrl: './delivery-confirm.component.html',
  styleUrls: ['./delivery-confirm.component.scss']
})
export class DeliveryConfirmComponent implements OnInit {
  deliveryconfirm: any[] = [];
  selecteddelivery : any[] = [];
  @ViewChild('deliveryTag') deliveryTag: Table | any;
  screenid: 1068 | undefined;
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
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

 

  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: DeliveryService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService, private location: Location,
    private route: ActivatedRoute, private router: Router,
    public cs: CommonService, public dialog: MatDialog,
    public datePipe: DatePipe) { }
  sub = new Subscription();
  code: any;


 
  ngOnInit(): void {

    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);


      this.code = js.code;
      this.fill(js);
    }

  }
  pageflow = 'New';
  isbtntext: boolean = true;
  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;
      if (data.pageflow == 'Display') {

        this.isbtntext = false;
      }


      this.spin.show();
      this.sub.add(this.service.searchLinenew({ preOutboundNo: [this.code.preOutboundNo], refDocNumber: [this.code.refDocNumber], warehouseId: [this.code.warehouseId] }).subscribe(res => {

        res.forEach((x: any) => {
          if (!x.deliveryQty)
            x.deliveryQty = 0;
          if (!x.referenceField10)
            x.referenceField10 = 0;
          if (!x.referenceField9)
            x.referenceField9 = 0;

        });

        this.deliveryconfirm = res;
      
        res.forEach((x: any) => {
          x.deliveryQty == null ? x.deliveryQty = 0 : x.deliveryQty;
        });

        this.spin.hide();
        // this.getclient_class(this.form.controls.classId.value);
      }, err => {
        this.cs.commonerrorNew(err);;
        this.spin.hide();
      }));
    }
  }

  assignhe(): void {

    const dialogRef = this.dialog.open(AssignHEComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '40%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.animal = result;
    });
  }


 

  
 

  





  disabled = false;
  step = 0;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  panelOpenState = false;
  confirm() {
    let dataErrorArray: any = [];
    this.deliveryconfirm.forEach(data => {
      //referenceField9 -> picked qty
      //referenceField10 -> qa qty
      if ((data.deliveryQty > data.orderQty) || (data.referenceField10 > data.deliveryQty) || (data.deliveryQty > data.referenceField9) ) {
        dataErrorArray.push(data.lineNumber);
      }
    });
    if (dataErrorArray.length > 0) {
      this.toastr.error(
        "Quantities mismatched in line no : " + dataErrorArray,
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.spin.show();
    this.sub.add(this.service.confirm(this.code).subscribe(res => {
      this.generatePdf(this.code);
      this.toastr.success(this.code.refDocNumber + ' confirmed Successfully', "", {
        timeOut: 2000,
        progressBar: false,
      })

      this.spin.hide();
      this.location.back()
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      if (err.error.error) {
        this.toastr.error(err.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      } else {
        this.cs.commonerrorNew(err);;
      }
      this.spin.hide();
    }));

  }


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.deliveryconfirm.forEach(x => {
      res.push({

        "Status ": this.cs.getstatus_text(x.statusId),
        'Line No': x.lineNumber,
        "Product Code": x.itemCode,
        "Description": x.description,
        "Order Qty": x.orderQty,
        "Picked Qty": x.referenceField9,
        "QA Qty": x.referenceField10,
        "Delivery Qty": x.deliveryQty,
        "Order Category": x.referenceField1,
        "Order Type": x.outboundOrderTypeId,

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Delivery Confirm");
  }

back(){
  this.location.back();
}

  generatePdf(element: any) {
    this.spin.show();
    this.sub.add(this.service.getOtBoundDeliveryFormPdf(element.refDocNumber, element.warehouseId).subscribe((res: any) => {
      this.spin.hide();
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



