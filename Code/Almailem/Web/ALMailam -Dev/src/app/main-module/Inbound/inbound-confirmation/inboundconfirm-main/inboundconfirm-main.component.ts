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
import { InboundConfirmationService } from "../inbound-confirmation.service";
import { ReplaceASNComponent } from "../replace-asn/replace-asn.component";
// import the pdfmake library
import pdfMake from "pdfmake/build/pdfmake";
// importing the fonts and icons needed
import pdfFonts from "../../../../../assets/font/vfs_fonts.js"
import { defaultStyle } from "../../../../config/customStyles";
import { fonts } from "../../../../config/pdfFonts";
import { almaielmLogo} from "../../../../../assets/font/almaielm.js";
import { Table } from "primeng/table";
import { DatePipe } from "@angular/common";

// PDFMAKE fonts
pdfMake.vfs = pdfFonts.pdfMake.vfs;
pdfMake.fonts = fonts;


@Component({
  selector: 'app-inboundconfirm-main',
  templateUrl: './inboundconfirm-main.component.html',
  styleUrls: ['./inboundconfirm-main.component.scss']
})
export class InboundconfirmMainComponent implements OnInit {
screenid= 3145 ;
  ready: any[] = [];
  selectedready : any[] = [];
  @ViewChild('readyTag') readyTag: Table | any;
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
    console.log('show:' + this.showFloatingButtons);
  }






  



  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.ready.forEach(x => {
      res.push({
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Status ": x.statusDescription,
        'Order No': x.refDocNumber,
        'Order Type': x.referenceDocumentType,
        "Container No": x.containerNo,
        "Order Date":this.cs.dateapiwithTime(x.createdOn),
        "Receipt Confirmation Date": this.cs.dateTimeApi(x.confirmedOn),
        "Lead Time": x.confirmedOn ? this.cs.getDataDiff(x.createdOn,x.confirmedOn) : '',
        "Confirmed By": x.confirmedBy,

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Inbound Summary");
  }



  constructor(private service: InboundConfirmationService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,public datePipe: DatePipe,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.search(true);
  }




 
  warehouseId = this.auth.warehouseId

  searhform = this.fb.group({
    containerNo: [],
    endConfirmedOn: [],
    endCreatedOn: [],
    inboundOrderTypeId: [],
    refDocNumber: [],
    startConfirmedOn: [],
    startCreatedOn: [],
    statusId: [[24],], //24
    warehouseId: [[this.auth.warehouseId]],
    companyCodeId: [[this.auth.companyId]],
    languageId: [[this.auth.languageId]],
    plantId: [[this.auth.plantId]],

  });

  dropdownSettings = {
    singleSelection: false,
    text: "Select",
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

  inboundOrderTypeIdListselected: any[] = [];
  inboundOrderTypeIdList: any[] = [];

  containerNoListselected: any[] = [];
  containerNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion

      this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
      this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
      this.searhform.controls.endConfirmedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endConfirmedOn.value));
      this.searhform.controls.startConfirmedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startConfirmedOn.value));
      //patching
      // const packBarcodes = [...new Set(this.inboundOrderTypeIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.packBarcodes.patchValue(packBarcodes);

      // const inboundOrderTypeId = [...new Set(this.inboundOrderTypeIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.inboundOrderTypeId.patchValue(inboundOrderTypeId);

      // const refDocNumber = [...new Set(this.refDocNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.refDocNumber.patchValue(refDocNumber);

      // const containerNo = [...new Set(this.containerNoListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.containerNo.patchValue(containerNo);

      // const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.statusId.patchValue(statusId);
    }
this.spin.show();
    this.service.search(this.searhform.value).subscribe(res => {
      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let tempinboundOrderTypeIdList: any[] = []
        const inboundOrderTypeId = [...new Set(res.map(item => item.inboundOrderTypeId))].filter(x => x != null)
        inboundOrderTypeId.forEach(x => tempinboundOrderTypeIdList.push({ value: x, label: x }));
        this.inboundOrderTypeIdList = tempinboundOrderTypeIdList;

        let tempcontainerNoList: any[] = []
        const containerNo = [...new Set(res.map(item => item.containerNo))].filter(x => x != null)
        containerNo.forEach(x => tempcontainerNoList.push({ value: x, label: x }));
        this.containerNoList = tempcontainerNoList;

        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(res.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;

        let tempstatusIdList: any[] = []
       // const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        res.forEach(x => tempstatusIdList.push({ value: x.statusId, label: x.statusDescription }));
        this.statusIdList = tempstatusIdList;
        this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
      }
      this.ready = res;
      ;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    });

  }
  reload() {
    this.searhform.reset();
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selectedready.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    if (data != 'Display')
      if (this.selectedready[0].statusId == 24) {
        this.toastr.error("Order can't be edited as it's already confirmed.", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";

    if (this.selectedready.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedready[0], pageflow: data ,pageflow1:'Inbound Summary'});
      this.router.navigate(['/main/inbound/inbound-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data,pageflow1:'Inbound Summary' });
      this.router.navigate(['/main/inbound/cinbound-create/' + paramdata]);
    }
  }

  replace() {
    if (this.selectedready.length === 0) {
      this.toastr.error("Kindly select any row", "Norification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selectedready[0].statusId != 24) {
      this.toastr.error("Order can't repalce.", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }


    const dialogRef = this.dialog.open(ReplaceASNComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '40%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.spin.show();
        this.sub.add(this.service.replaceASN(this.selectedready[0].warehouseId, this.selectedready[0].preInboundNo, result, this.selectedready[0].refDocNumber).subscribe((res) => {
          this.toastr.success(result + " replaced successfully.", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });

          this.spin.hide(); //this.getAllListData();
          this.search();
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
      }
    });
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
    let currentDate = this.datePipe.transform(new Date, 'dd-MMM-yyyy HH:mm', 'GMT');
    let confirmedOn = this.datePipe.transform(element.confirmedOn, 'dd-MMM-yyyy HH:mm', 'GMT')
    this.sub.add(this.service.getInboundFormPdf(element.refDocNumber).subscribe((res: any) => {
      this.spin.hide();
      var dd: any;
      dd = {
        pageSize: "A4",
        pageOrientation: "portrait",
        pageMargins: [40, 140, 40, 40],
        header(currentPage: number, pageCount: number, pageSize: any): any {
          return [
            {
              columns: [
                {
                  stack: [
                   
                    {
                      columns: [
                        {
                          stack: [
                            { text: '\n\n' },
                            { text: 'Supplier Name:', margin:[0, 20, 0, 0], bold: true, fontSize: 8, lineHeight: 2 },
                             { text: 'Container NO #', fontSize: 8, bold: true, lineHeight: 2 },
                          ],
                          width: 55, 
                        },
                        {
                          stack: [
                            { text: '\n\n' },
                            { text: ':  ' + res.receiptHeader.supplierName != null ? res.receiptHeader.supplierName : '', margin:[0, 20, 0, 0],  bold: true, fontSize: 8, lineHeight: 2 },
                            { text: ': ' + (res.receiptHeader.containerNo != null ? res.receiptHeader.containerNo : ''), fontSize: 8, bold: true, lineHeight: 2 },
                          ],
                          width: 100
                        },
                      ]
                    }
                  ]

                },
                {
                  stack: [
                    { text: '     Receipt Confirmation Report',  margin:[0, 5, 0, 0], alignment: 'center', bold: true, fontSize: 14 }

                  ],
                  width: 220
                },
            
                {
                  stack: [

                   { image: almaielmLogo.headerLogo, fit: [130, 130],  margin:[0, 5, 0, 0],   },
                   { text: 'Printed Date: '+' '+currentDate, alignment: 'left', margin:[0, 20, 0, 0], bold: true, fontSize: 8,lineHeight: 2},
                   { text: 'Receipt Confirmed On: '+' '+confirmedOn, alignment: 'left', bold: true, fontSize: 8,lineHeight: 2},
                   { text: 'Order No #'  + element.refDocNumber, bold: true, alignment: 'left', fontSize: 8, lineHeight: 2 },
                   { text: 'Order Type #' + res.receiptHeader.orderType, alignment: 'left', bold: true, fontSize: 8, lineHeight: 2 },
              
                  ]
                }
              ],
              margin: [30, 5]
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

      //Receipt List
      if (res.receiptList.length > 0) {
        // border: [left, top, right, bottom]
        // border: [false, false, false, false]
        let bodyArray: any[] = [];
        bodyArray.push([
          { text: 'Part No', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Description', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Mfr Name', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Ordered Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Accepted Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Damaged Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Missing Qty', bold: true, fontSize: 8, border: [false, false, false, true] },
          { text: 'Status', bold: true, fontSize: 8, border: [false, false, false, true] },
        ]);
        let totalExpected = 0;
        let totalAccepted = 0;
        let totalDamaged = 0;
        let totalMissing = 0;
        res.receiptList.forEach((receipt, i) => {
          totalExpected = totalExpected + (receipt.expectedQty != null ? receipt.expectedQty : 0);
          totalAccepted = totalAccepted + (receipt.acceptedQty != null ? receipt.acceptedQty : 0);
          totalDamaged = totalDamaged + (receipt.damagedQty != null ? receipt.damagedQty : 0);
          totalMissing = totalMissing + (receipt.missingORExcess != null ? receipt.missingORExcess : 0);
          bodyArray.push([
            { text: receipt.sku != null ? receipt.sku : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: receipt.description != null ? receipt.description : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 },
            { text: receipt.mfrSku != null ? receipt.mfrSku : '', fontSize: 8, border: [false, false, false, true] },
            { text: receipt.expectedQty != null ? receipt.expectedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: receipt.acceptedQty != null ? receipt.acceptedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: receipt.damagedQty != null ? receipt.damagedQty : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: receipt.missingORExcess != null ? (receipt.expectedQty > receipt.acceptedQty ? '(' : '') + receipt.missingORExcess + (receipt.expectedQty > receipt.acceptedQty ? ')' : '') : '0', fontSize: 8, alignment: 'left', border: [false, false, false, true], lineHeight: 2 },
            { text: receipt.status != null ? receipt.status : '', fontSize: 8, border: [false, false, false, true], lineHeight: 2 }
          ])
          if ((i + 1) == res.receiptList.length) {
            bodyArray.push([
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: 'Total :', fontSize: 9, bold: true, border: [false, false, false, false] },
              { text: totalExpected, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalAccepted, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: totalDamaged, fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: (totalExpected > totalMissing ? '(' : '') + totalMissing + (totalExpected > totalMissing ? ')' : ''), fontSize: 9, alignment: 'left', bold: true, border: [false, false, false, false], lineHeight: 2 },
              { text: '', fontSize: 9, bold: true, border: [false, false, false, false], lineHeight: 2 }
            ])
          }
        });
        dd.content.push(
          '\n', '\n',
          {
            table: {
              // layout: 'noBorders', // optional
              // heights: [,60,], // height for each row
              headerRows: 1,
              widths: [50, 110, 50, 40, 40, 40, 40, 60],
              body: bodyArray
            }
          }
        )
      }

      pdfMake.createPdf(dd).open();
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  onChange() {
    const choosen= this.selectedready[this.selectedready.length - 1];   
    this.selectedready.length = 0;
    this.selectedready.push(choosen);
  }
}
