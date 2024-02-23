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
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
import { CommonService, dropdownelement1 } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PickingService } from "../picking.service";
import { UpdatePickerComponent } from "./update-picker/update-picker.component";

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
  sname: string;
  scode: string;
  reallocated: string;
}

const ELEMENT_DATA: ordermanagement[] = [
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', sname: 'readonly', scode: 'readonly', reallocated: 'readonly', },

];
@Component({
  selector: 'app-picking-main',
  templateUrl: './picking-main.component.html',
  styleUrls: ['./picking-main.component.scss']
})
export class PickingMainComponent implements OnInit {

  screenid: 1063 | undefined;
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

  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: PickingService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.getstorename()
    this.search(true);

  }

  warehouseId = this.auth.warehouseId
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  // Pagination
  searhform = this.fb.group({
    actualHeNo: [],
    itemCode: [],
    lineNumber: [],
    partnerCode: [],
    pickedPackCode: [],
    pickedStorageBin: [],
    pickupNumber: [],
    preOutboundNo: [],
    refDocNumber: [],
    warehouseId: [[this.auth.warehouseId]],
    statusId: [[]],
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

  lineNumberListselected: any[] = [];
  lineNumberList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];

  pickedStorageBinselected: any[] = [];
  pickedStorageBinList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  pickupNumberListselected: any[] = [];
  pickupNumberList: any[] = [];

  preOutboundNoListselected: any[] = [];
  preOutboundNoList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

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
      // this.searhform.controls.endRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endRequiredDeliveryDate.value));
      // this.searhform.controls.startRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startRequiredDeliveryDate.value));


      //patching
      // const itemCode = [...new Set(this.itemCodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.itemCode.patchValue(itemCode);

      // const lineNumber = [...new Set(this.lineNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.lineNumber.patchValue(lineNumber);

      // const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.partnerCode.patchValue(partnerCode);

      // const preOutboundNo = [...new Set(this.preOutboundNoListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.preOutboundNo.patchValue(preOutboundNo);

      // const refDocNumber = [...new Set(this.refDocNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.refDocNumber.patchValue(refDocNumber);

      // const pickupNumber = [...new Set(this.pickupNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.pickupNumber.patchValue(pickupNumber);

    }
    this.searhform.controls.statusId.value.push(48);
    this.service.search(this.searhform.value).subscribe(res => {
       //let result = res.filter((x: any) => x.statusId == this.warehouseId);
      // let result = res.filter((x: any) => x.statusId == 48 || x.statusId == 51);
      // result.forEach((x) => {
      //   x.partnerCode = this.storecodeList.find(y => y.partnerCode == x.partnerCode)?.partnerName;
      //   })
        

  
      if (ispageload) {
        this.spin.show();
        let tempitemCodeList: any[] = []
        const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
        itemCode.forEach(x => tempitemCodeList.push({ value: x, label: x }));
        this.itemCodeList = tempitemCodeList;

        let templineNumberList: any[] = []
        const lineNumber = [...new Set(res.map(item => item.lineNumber))].filter(x => x != null)
        lineNumber.forEach(x => templineNumberList.push({ value: x, label: x }));
        this.lineNumberList = templineNumberList;

        let temppartnerCodeList: any[] = []
        const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
        this.partnerCodeList = temppartnerCodeList;

        let temppreOutboundNoList: any[] = []
        const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
        preOutboundNo.forEach(x => temppreOutboundNoList.push({ value: x, label: x }));
        this.preOutboundNoList = temppreOutboundNoList;

        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(res.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;

        let temppickedStorageBinList: any[] = []
        const pickedStorageBin = [...new Set(res.map(item => item.pickedStorageBin))].filter(x => x != null)
        pickedStorageBin.forEach(x => temppickedStorageBinList.push({ value: x, label: x }));
        this.pickedStorageBinList = temppickedStorageBinList;


        let temppickupNumberList: any[] = []
        const pickupNumber = [...new Set(res.map(item => item.pickupNumber))].filter(x => x != null)
        pickupNumber.forEach(x => temppickupNumberList.push({ value: x, label: x }));
        this.pickupNumberList = temppickupNumberList;

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

  deleteDialog() {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "Norification");
      return;
    }
    if (this.selection.selected[0].statusId != 48) {
      this.toastr.error("Confirmed items can't be deleted", "Norification");
      return;
    }
    const dialogRef = this.dialog.open(DeleteComponent, {
      disableClose: true,
      width: '40%',
      maxWidth: '80%',
      position: { top: '9%', },
      // data: this.selection.selected[0],
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selection.selected[0]);

      }
    });
  }
  deleterecord(obj: any) {
    this.spin.show();
    this.sub.add(this.service.deletePickupHeader(obj).subscribe((ress) => {
      this.toastr.success(obj.pickupNumber + " Deleted successfully.", "Notification",{
        timeOut: 2000,
        progressBar: false,
      });
     this.search(true);
     this.spin.hide(); 
    }, err => {
      this.cs.commonerror(err);
      this.spin.hide();
    }));
  }
  
  reload() {
    window.location.reload();
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

    let paramdata = "";

    if (this.selection.selected.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selection.selected[0], pageflow: data });
      this.router.navigate(['/main/outbound/pickup-confirm/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/outbound/pickup-confirm/' + paramdata]);
    }
  }

  openConfirm(data: any) {

    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/outbound/pickup-confirm/' + paramdata]);

  }


  displayedColumns: string[] = ['select', 'actions1', 'statusId', 'refDocNumber', 'outboundOrderTypeId','referenceField1', 'lineNumber', 'itemCode', 'proposedPackBarCode', 'proposedStorageBin','assignedPickerId','partnerCode', 'pickToQty', 'pickupCreatedOn',];
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

        "Status": this.cs.getstatus_text(x.statusId),
        'Order No': x.refDocNumber,
        "Order Type": this.cs.getoutboundOrderType_text(x.outboundOrderTypeId),
        "Ord Category": x.referenceField1,
        "Line No": x.lineNumber,
        "Pickup No": x.pickupNumber,
        "Product Code": x.itemCode,
        "Pallet ID": x.proposedPackBarCode,
        "Bin Location": x.proposedStorageBin,
        "Picker": x.assignedPickerId,
        "Store": x.partnerCode,
        "Pick TO Qty": x.pickToQty,
        "Pickup Date": this.cs.dateExcel(x.pickupCreatedOn),

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Picking");
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

form = this.fb.group({
  assignedPickerId: [,],
});

updatePicker(data: any){
  const dialogRef = this.dialog.open(UpdatePickerComponent, {
    disableClose: true,
    width: '100%',
    maxWidth: '55%',
  });
  dialogRef.afterClosed().subscribe(result => {
    console.log(result)
    if(result){
    let data: any[] = [];
    this.selection.selected.forEach((x: any) => {
      data.push({
        itemCode: x.itemCode,
        lineNumber: x.lineNumber,
        partnerCode: x.partnerCode,
        preOutboundNo: x.preOutboundNo,
        pickupNumber: x.pickupNumber,
        refDocNumber: x.refDocNumber,
        warehouseId: x.warehouseId,
      })
      console.log(data)
    });
    this.form.controls.assignedPickerId.patchValue(result)
    console.log(data[0])
    this.spin.show();
    this.sub.add(this.service.updatePicker(this.form.getRawValue(), data[0].pickupNumber, data[0].itemCode, data[0].lineNumber, data[0].partnerCode, data[0].preOutboundNo,  data[0].refDocNumber ,  data[0].warehouseId).subscribe(res => {
      this.spin.hide();
      this.toastr.success(result + " updated successfully.", "Notification", {
        timeOut: 2000,
        progressBar: false,
      })
      this.search(true);

    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    }));
  }
  });
}


multipleReassign(){

  const dialogRef = this.dialog.open(UpdatePickerComponent, {
    disableClose: true,
    width: '100%',
    maxWidth: '55%',
  });
  dialogRef.afterClosed().subscribe(result => {
if(result){
  let objArray: any[] = [];
  this.selection.selected.forEach((x: any) => {
    let obj: any = {};
    obj.itemCode = x.itemCode;
    obj.lineNumber = x.lineNumber;
    obj.partnerCode = x.partnerCode;
    obj.preOutboundNo = x.preOutboundNo;
    obj.proposedPackCode = x.proposedPackBarCode;
    obj.proposedStorageBin = x.proposedStorageBin;
    obj.refDocNumber = x.refDocNumber;
    obj.warehouseId = x.warehouseId;
    objArray.push(obj);
  });

  if (objArray.length > 0) {
    this.spin.show();
    this.selection.selected.forEach((x )=> {
      x.assignedPickerId = result;
    })
    this.spin.show();
    
    this.sub.add(this.service.multipleUpdatePicker(this.selection.selected).subscribe(res => {
      this.spin.hide();
      this.toastr.success(" Reassigned successfully!", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      this.search(true);
    }, err => {
      this.spin.hide();
      this.cs.commonerror(err);
    }));
  }
}
});
}

}
