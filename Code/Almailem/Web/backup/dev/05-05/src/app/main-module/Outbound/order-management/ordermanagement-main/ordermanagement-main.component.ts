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
import { AssignInvoiceComponent } from "src/app/main-module/Inbound/preinbound/preinbound-new/assign-invoice/assign-invoice.component";
import { PreoutboundService } from "../../preoutbound/preoutbound.service";
import { BinLocationComponent } from "./bin-location/bin-location.component";
import { OrdermanagementService } from "./ordermanagement.service";
import { ReallocateComponent } from "./reallocate/reallocate.component";

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
  reallocated: string;
  status: string;
  sname: string;
  sotype: string;
  ref1: string;
  ref2: string;
  ref3: string;
}

const ELEMENT_DATA: ordermanagement[] = [
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', sotype: 'readonly', sname: 'readonly', ref1: 'readonly', ref2: 'readonly', ref3: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },

];
@Component({
  selector: 'app-ordermanagement-main',
  templateUrl: './ordermanagement-main.component.html',
  styleUrls: ['./ordermanagement-main.component.scss']
})
export class OrdermanagementMainComponent implements OnInit {
  selectedRowIndex:any;
  screenid: 1060 | undefined;
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  storecodeList: any;
  currentDate = new Date();
  fifteenDaysDate = new Date();
  ondeDaysBeforeDate = new Date();
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

  reallocate(): void {

    const dialogRef = this.dialog.open(ReallocateComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  binlocation(): void {

    const dialogRef = this.dialog.open(BinLocationComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: OrdermanagementService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {

   
    this.fifteenDaysDate.setDate(this.currentDate.getDate() + 7);
    this.ondeDaysBeforeDate.setDate(this.currentDate.getDate() - 1);

    // this.searhform.controls.endRequiredDeliveryDate.patchValue(this.fifteenDaysDate);
    // this.searhform.controls.startRequiredDeliveryDate.patchValue(this.ondeDaysBeforeDate);

    this.getstorename()
    this.search(true);


  }


warehouseId=this.auth.warehouseId
statusId=48;
@ViewChild(MatSort, { static: true })
sort!: MatSort;
@ViewChild(MatPaginator, { static: true })
paginator!: MatPaginator; // Pagination
  // Pagination

  searhform = this.fb.group({
    endRequiredDeliveryDate: [],
    endOrderDate : [, ],
    //endOrderDate : [this.currentDate, ],
    itemCode: [],
    outboundOrderTypeId: [],
    partnerCode: [],
    preOutboundNo: [],
    refDocNumber: [],
    soType: [],
    startOrderDate: [, ],
   // startOrderDate: [this.yesterdayDate, ],
    startRequiredDeliveryDate: [],
    statusId: [[42, 43, 47]],
   warehouseId: [[this.auth.warehouseId]],
 
  });
 
 
 
 
  dropdownSettings = {
    singleSelection: false, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
 
  itemCodeListselected: any[] = [];
  itemCodeList: any[] = [];
 
  outboundOrderTypeIdListselected: any[] = [];
  outboundOrderTypeIdList: any[] = [];
 
  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];
 
  preOutboundNoselected: any[] = [];
  preOutboundNoList: any[] = [];
 
  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];
 
  soTypeListselected: any[] = [];
  soTypeList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  getstorename(){



    this.sub.add(this.service.GetStoreCode().subscribe(res => {
      this.storecodeList = res;
      console.log(this.storecodeList)
     // this.spin.hide();
    },
    err => {
      this.cs.commonerror(err);
    //  this.spin.hide();
    }));
  }
  
 
  search(ispageload = false) {
    if (!ispageload) {
      //dateconvertion
      this.searhform.controls.endRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endRequiredDeliveryDate.value));
      this.searhform.controls.startRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startRequiredDeliveryDate.value));
  
      this.searhform.controls.endOrderDate .patchValue(this.cs.day_callapiSearch(this.searhform.controls.endOrderDate .value));
      this.searhform.controls.startOrderDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startOrderDate.value));

      console.log(this.searhform.controls.startOrderDate.value)
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
  this.searhform.controls.endOrderDate .patchValue(this.cs.day_callapiSearch(this.searhform.controls.endOrderDate .value));
  this.searhform.controls.startOrderDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startOrderDate.value));
  this.spin.show();
   this.service.searchLine(this.searhform.value).subscribe(res => {
     // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
    // let result = res.filter((x: any) => x.statusId == 47 || 42 || 43);
     let result = res.filter((x: any) => x.statusId == 47 || x.statusId == 42 || x.statusId == 43);

     let deletionResult = result.filter((x: any) => x.deletionIndicator == 0);

    //  result.forEach((x) => {
    //   x.partnerCode = this.storecodeList.find(y => y.partnerCode == x.partnerCode)?.partnerName;
    //   })

      
 
     if (ispageload) {
       let tempitemCodeList: any[] = []
       const itemCode = [...new Set(deletionResult.map(item => item.itemCode))].filter(x => x != null)
       itemCode.forEach(x => tempitemCodeList.push({ value: x, label: x }));
       this.itemCodeList = tempitemCodeList;
 
       let tempoutboundOrderTypeIdList: any[] = []
       const outboundOrderTypeId = [...new Set(deletionResult.map(item => item.outboundOrderTypeId))].filter(x => x != null)
       outboundOrderTypeId.forEach(x => tempoutboundOrderTypeIdList.push({ value: x, label: this.cs.getoutboundOrderType_text(x)  }));
       this.outboundOrderTypeIdList = tempoutboundOrderTypeIdList;
 
       let temppartnerCodeList: any[] = []
       const partnerCode = [...new Set(deletionResult.map(item => item.partnerCode))].filter(x => x != null)
       partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
       this.partnerCodeList = temppartnerCodeList;
 
       let temppreOutboundNoList: any[] = []
       const preOutboundNo = [...new Set(deletionResult.map(item => item.preOutboundNo))].filter(x => x != null)
       preOutboundNo.forEach(x => temppreOutboundNoList.push({ value: x, label: x }));
       this.preOutboundNoList = temppreOutboundNoList;
 
       let temprefDocNumberList: any[] = []
       const refDocNumber = [...new Set(deletionResult.map(item => item.refDocNumber))].filter(x => x != null)
       refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
       this.refDocNumberList = temprefDocNumberList;
 
       let tempsoTypeList: any[] = []
       const soType = [...new Set(deletionResult.map(item => item.soType))].filter(x => x != null)
       soType.forEach(x => tempsoTypeList.push({ value: x, label: x }));
       this.soTypeList = tempsoTypeList;

       let tempstatusIdList: any[] = []
       const statusId = [...new Set(deletionResult.map(item => item.statusId))].filter(x => x != null)
       statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
       this.statusIdList = tempstatusIdList;
     }

     
     this.dataSource = new MatTableDataSource<any>(deletionResult);
     this.selection = new SelectionModel<any>(true, []);
     this.dataSource.sort = this.sort;
     this.dataSource.paginator = this.paginator;
     this.spin.hide();
   }, err => {
 
     this.cs.commonerror(err);
     this.spin.hide();
 
   });   

 
 }
 reload(){
   this.searhform.reset();
 }

 
  Unallocate(data: any) {
    this.spin.show();
    this.sub.add(this.service.Unallocate(data.itemCode, data.lineNumber, data.partnerCode, data.preOutboundNo, data.refDocNumber, data.warehouseId, data.proposedPackBarCode, data.proposedStorageBin).subscribe(res => {
      this.spin.hide();
      this.toastr.success("Unallocated successfully","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.search();
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    }));
  }
  Allocate(data: any) {
    this.spin.show();
    this.sub.add(this.service.Allocate(data.itemCode, data.lineNumber, data.partnerCode, data.preOutboundNo, data.refDocNumber, data.warehouseId).subscribe(res => {
      console.log(res)
      this.spin.hide();
      if(res.allocatedQty > 0){
        this.toastr.success("Allocated successfully","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      }

      if(res.allocatedQty <= 0){
        this.toastr.info("Stock not available for allocation",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      }
    
      this.search();
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    }));
  }
  displayedColumns: string[] = ['select','actions', 'statusId', 'refDocNumber','partnerCode',  'outboundOrderTypeId',   'lineNumber', 'itemCode','orderQty', 'inventoryQty', 'allocatedQty', 'requiredDeliveryDate','preOutboundNo', 'proposedPackBarCode', 'proposedStorageBin', 'description', ];
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
  selectRow(row) {
    this.selection.toggle(row);
    console.log(this.selection.selected);
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Status  ": this.cs.getstatus_text(x.statusId),
        "Order No  ": x.refDocNumber,
        'Store': x.partnerCode,
        'Order Type': this.cs.getoutboundOrderType_text(x.outboundOrderTypeId),
        "Line No": x.lineNumber,
        "Product Code": x.itemCode,
        "Order Qty": x.orderQty,
        "Inv Qty": x.inventoryQty,
        "Allocated Qty": x.allocatedQty,
        "Required Date": this.cs.dateExcel(x.requiredDeliveryDate),
        "Preoutbound No": x.preOutboundNo,
        "Description": x.description,

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Order Management");
  }

  onItemSelect(item: any) {
    console.log(item);
  }
  
  OnItemDeSelect(item:any){
    console.log(item);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}
}
