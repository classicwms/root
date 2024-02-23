
  
  // PDFMAKE fonts
  // pdfMake.vfs = pdfFonts.pdfMake.vfs;
  // pdfMake.fonts = fonts;

import { SelectionModel } from "@angular/cdk/collections";
import { DatePipe } from "@angular/common";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService, dropdownelement1 } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { AssignPickerComponent } from "../../assignment/assignment-main/assign-picker/assign-picker.component";
import { DeliveryService } from "../delivery.service";

  
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
    selector: 'app-delivery-tab2',
    templateUrl: './delivery-tab2.component.html',
    styleUrls: ['./delivery-tab2.component.scss']
  })
  export class DeliveryTab2Component implements OnInit {
    screenid: 1067 | undefined;
    isShowDiv = false;
    public icon = 'expand_more';
    showFloatingButtons: any;
    toggle = true;
    storecodeList: any;
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
  
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
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
      this.getstorename();
      this.search(true);
  
    }
  
  
    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator!: MatPaginator; // Pagination
    // Pagination
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
      statusId: [[57,48,55]],
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

    getstorename(){
      this.spin.show();
      this.sub.add(this.service.GetStoreCode().subscribe(res => {
        this.storecodeList = res;
        console.log(this.storecodeList)
        this.spin.hide();
      },
      err => {
        this.cs.commonerror(err);
        this.spin.hide();
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
    
        this.searhform.controls.statusId.patchValue([57,48,55]);
  

      this.service.search(this.searhform.value).subscribe(res => {
        this.spin.show();
        // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
        //  let result1 = res.filter((x: any) => x.statusId == 57 || x.statusId == 48 || x.statusId == 55);
        //  result1.forEach((x) => {
        // x.partnerCode = this.storecodeList.find(y => y.partnerCode == x.partnerCode)?.partnerName;
        // })
        
        if (ispageload) {
          //  let tempitemCodeList: any[] = []
          //  const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
          //  itemCode.forEach(x => tempitemCodeList.push({ id: x, itemName: x }));
          //  this.itemCodeList = tempitemCodeList;
  
          let tempoutboundOrderTypeIdList: any[] = []
          const outboundOrderTypeId = [...new Set(res.map(item => item.outboundOrderTypeId))].filter(x => x != null)
          outboundOrderTypeId.forEach(x => tempoutboundOrderTypeIdList.push({value: x, label: this.cs.getoutboundOrderType_text(x) }));
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
          soType.forEach(x => tempsoTypeList.push({ value: x, label: x }));
          this.soTypeList = tempsoTypeList;
  
          let tempstatusIdList: any[] = []
          const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
          statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
          this.statusIdList = tempstatusIdList;
        }
        this.dataSource = new MatTableDataSource<any>(res);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.spin.hide();
      }, err => {
  
        this.cs.commonerror(err);
        this.spin.hide();
  
      });
  
  
    }
    reload() {
      this.searhform.reset();
      this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
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
    displayedColumns: string[] = [ 'select', 'actions1', 'actions', 'refDocNumber', 'referenceField1','outboundOrderTypeId','partnerCode', 'referenceField10', 'referenceField9', 'referenceField8', 'referenceField7', 'requiredDeliveryDate', 'refDocDate', 'deliveryConfirmedOn'];
    dataSource = new MatTableDataSource<any>([]);
    selection = new SelectionModel<any>(true, []);
  
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
    checkboxLabel(row?: ordermanagement): string {
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
          "Order Date": this.cs.dateExcel(x.refDocDate),
          "Delivery Date ": this.cs.dateExcel(x.refDocDate),
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
  }
  
