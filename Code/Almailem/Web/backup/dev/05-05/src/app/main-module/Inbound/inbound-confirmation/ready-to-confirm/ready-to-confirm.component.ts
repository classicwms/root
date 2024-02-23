
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
  import { logo } from "../../../../../assets/font/logo.js";
  
  // PDFMAKE fonts
  pdfMake.vfs = pdfFonts.pdfMake.vfs;
  pdfMake.fonts = fonts;
  

  @Component({
    selector: 'app-ready-to-confirm',
    templateUrl: './ready-to-confirm.component.html',
    styleUrls: ['./ready-to-confirm.component.scss']
  })
  export class ReadyToConfirmComponent implements OnInit {
    screenid: 1053 | undefined;
  
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
  
  
  
    displayedColumns: string[] = ['select', 'statusId', 'refDocNumber', 'inboundOrderTypeId', 'containerNo', 'doc', 'createdOn', 'confirmedOn', 'refdocno', 'confirmedBy',];
    dataSource = new MatTableDataSource<any>([]);
    selection = new SelectionModel<any>(true, []);
  
    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
      const numSelected = this.selection.selected.length;
      const numRows = this.dataSource.data.length;
      return numSelected === numRows;
    }
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
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
      return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
    }
    clearselection(row: any) {
      this.selection.clear();
      this.selection.toggle(row);
    }
    downloadexcel() {
      // if (excel)
      var res: any = [];
      this.dataSource.data.forEach(x => {
        res.push({
  
          "Status ": this.cs.getstatus_text(x.statusId),
          'Order No': x.refDocNumber,
          'Order Type': this.cs.getinboundorderType_text(x.inboundOrderTypeId),
          "Container No": x.containerNo,
          "Order Date": this.cs.dateExcel(x.createdOn),
          "Receipt Confirmation Date": this.cs.dateapi(x.confirmedOn),
          "Lead Time": x.confirmedOn ? this.cs.getDataDiff(x.createdOn,x.confirmedOn) : '',
          "Confirmed By": x.confirmedBy,
  
          // 'Created By': x.createdBy,
          // 'Date': this.cs.dateapi(x.createdOn),
        });
  
      })
      this.cs.exportAsExcel(res, "Confirmation");
    }
  
  
  
    constructor(private service: InboundConfirmationService,
      public toastr: ToastrService, public dialog: MatDialog,
      private spin: NgxSpinnerService, private router: Router,
      public auth: AuthService,
      private fb: FormBuilder,
      public cs: CommonService,) { }
    sub = new Subscription();
    ngOnInit(): void {
      this.search(true);
    }
  
  
  
  
    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator!: MatPaginator; // Pagination
    // Pagination
    warehouseId = this.auth.warehouseId
  
    searhform = this.fb.group({
      containerNo: [],
      endConfirmedOn: [],
      endCreatedOn: [],
      inboundOrderTypeId: [],
      refDocNumber: [],
      startConfirmedOn: [],
      startCreatedOn: [],
      statusId: [],
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
        let result = res.filter((x: any) => x.statusId != 24);
        this.spin.hide();
        if (ispageload) {
          let tempinboundOrderTypeIdList: any[] = []
          const inboundOrderTypeId = [...new Set(result.map(item => item.inboundOrderTypeId))].filter(x => x != null)
          inboundOrderTypeId.forEach(x => tempinboundOrderTypeIdList.push({ value: x, label: x }));
          this.inboundOrderTypeIdList = tempinboundOrderTypeIdList;
  
          let tempcontainerNoList: any[] = []
          const containerNo = [...new Set(result.map(item => item.containerNo))].filter(x => x != null)
          containerNo.forEach(x => tempcontainerNoList.push({ value: x, label: x }));
          this.containerNoList = tempcontainerNoList;
  
          let temprefDocNumberList: any[] = []
          const refDocNumber = [...new Set(result.map(item => item.refDocNumber))].filter(x => x != null)
          refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
          this.refDocNumberList = temprefDocNumberList;
  
          let tempstatusIdList: any[] = []
          const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
          statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
          this.statusIdList = tempstatusIdList;
        }
        this.dataSource = new MatTableDataSource<any>(result);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
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
        if (this.selection.selected.length === 0) {
          this.toastr.error("Kindly select any row", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          return;
        }
      if (data != 'Display')
        if (this.selection.selected[0].statusId == 24) {
          this.toastr.error("Order can't be edited as it's already confirmed.", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          return;
        }
      let paramdata = "";
  
      if (this.selection.selected.length > 0) {
        paramdata = this.cs.encrypt({ code: this.selection.selected[0], pageflow: data });
        this.router.navigate(['/main/inbound/inbound-create/' + paramdata]);
      }
      else {
        paramdata = this.cs.encrypt({ pageflow: data });
        this.router.navigate(['/main/inbound/cinbound-create/' + paramdata]);
      }
    }
  
    replace() {
      if (this.selection.selected.length === 0) {
        this.toastr.error("Kindly select any row", "Norification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
  
      if (this.selection.selected[0].statusId != 24) {
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
          this.sub.add(this.service.replaceASN(this.selection.selected[0].warehouseId, this.selection.selected[0].preInboundNo, result, this.selection.selected[0].refDocNumber).subscribe((res) => {
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
      this.sub.add(this.service.getInboundFormPdf(element.refDocNumber).subscribe((res: any) => {
        this.spin.hide();
        var dd: any;
        dd = {
          pageSize: "A4",
          pageOrientation: "portrait",
          pageMargins: [40, 130, 40, 40],
          header(currentPage: number, pageCount: number, pageSize: any): any {
            return [
              {
                columns: [
                  {
                    stack: [
                      { image: logo.headerLogo, fit: [100, 100] },
                      {
                        columns: [
                          {
                            stack: [
                              { text: '\n\n' },
                              { text: 'Supplier', bold: true, fontSize: 8, lineHeight: 2 },
                              // { text: 'Container NO #', fontSize: 8, bold: true, lineHeight: 2 },
                            ],
                            width: 45
                          },
                          {
                            stack: [
                              { text: '\n\n' },
                              { text: ':  ' + res.receiptHeader.supplier, bold: true, fontSize: 8, lineHeight: 2 },
                              // { text: ': ' + res.receiptHeader.containerNo, fontSize: 8, bold: true, lineHeight: 2 },
                            ]
                          },
                        ]
                      }
                    ]
  
                  },
                  {
                    stack: [
                      { text: '     Receipt Confirmation Report', alignment: 'center', bold: true, fontSize: 12 }
                    ],
                    width: 270
                  },
                  {
                    stack: [
                      { text: '\n' },
                      {
                        columns: [
                          {
                            stack: [
                              { text: '\n\n' },
                              { text: 'ASN NO #', bold: true, fontSize: 8, lineHeight: 2 },
  
                              { text: 'ASN Type', fontSize: 8, bold: true, lineHeight: 2 }
                            ],
                          },
                          {
                            stack: [
                              { text: '\n\n' },
                              { text: ': ' + element.refDocNumber, bold: true, fontSize: 8, lineHeight: 2 },
                              { text: ': ' + res.receiptHeader.orderType, fontSize: 8, bold: true, lineHeight: 2 }
                            ]
                          },
                        ]
                      }
                    ]
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
  
        //Receipt List
        if (res.receiptList.length > 0) {
          // border: [left, top, right, bottom]
          // border: [false, false, false, false]
          let bodyArray: any[] = [];
          bodyArray.push([
            { text: 'SKU', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Description', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Mfr.SKU', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Expected', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Accepted', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Damaged', bold: true, fontSize: 8, border: [false, false, false, true] },
            { text: 'Missing/Excess', bold: true, fontSize: 8, border: [false, false, false, true] },
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
            { text: 'Container : ' + res.receiptHeader.containerNo, alignment: 'left', bold: true, fontSize: 8 },
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
  
  }
  