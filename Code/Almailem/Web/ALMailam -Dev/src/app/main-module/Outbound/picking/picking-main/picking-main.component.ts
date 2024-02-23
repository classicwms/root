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
import { Table } from "primeng/table";

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
  screenid=3063;
  picking: any[] = [];
  selectedpicker : any[] = [];
  @ViewChild('pickingTag') pickingTag: Table | any;
 
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

  

  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: PickingService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.getstorename()
    this.search(true);

  }

  warehouseId = this.auth.warehouseId
 
  searhform = this.fb.group({
    actualHeNo: [],
    companyCodeId:[[this.auth.companyId]],
    plantId:[[this.auth.plantId]],
    languageId:[[this.auth.languageId]],
    itemCode: [],
    lineNumber: [],
    partnerCode: [],
    pickedPackCode: [],
    pickedStorageBin: [],
    pickupNumber: [],
    preOutboundNo: [],
    refDocNumber: [],
    warehouseId: [[this.auth.warehouseId]],
    statusId: [[48]],
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
      
      this.spin.hide();
    },
    err => {
      this.cs.commonerrorNew(err);;
      this.spin.hide();
    }));
  }


  search(ispageload = false) {
    if (!ispageload) {

     

    }
    //this.searhform.controls.statusId.value.push(48);
    this.service.searchSpark(this.searhform.value).subscribe(res => {
     
        

  
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
      this.picking = res;
   
      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);;
      this.spin.hide();

    });


  }
  onChange() {
    const choosen= this.selectedpicker[this.selectedpicker.length - 1];   
    this.selectedpicker.length = 0;
    this.selectedpicker.push(choosen);
  }
  deleteDialog() {
    if (this.selectedpicker.length === 0) {
      this.toastr.error("Kindly select any row", "Norification");
      return;
    }
    if (this.selectedpicker[0].statusId != 48) {
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
        this.deleterecord(this.selectedpicker[0]);

      }
    });
  }
  deleterecord(obj: any) {
    this.spin.show();
    this.sub.add(this.service.deletePickupHeaderv2(obj).subscribe((ress) => {
      this.toastr.success(obj.pickupNumber + " Deleted successfully.", "Notification",{
        timeOut: 2000,
        progressBar: false,
      });
     this.search(true);
     this.spin.hide(); 
    }, err => {
      this.cs.commonerrorNew(err);;
      this.spin.hide();
    }));
  }
  
  reload() {
    window.location.reload();
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selectedpicker.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }

    let paramdata = "";

    if (this.selectedpicker.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedpicker[0], pageflow: data });
      this.router.navigate(['/main/outbound/pickup-confirm/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/outbound/pickup-confirm/' + paramdata]);
    }
  }

  openConfirm(data: any) {

    if(data.statusId != 48){
      this.toastr.error("Picking Process of this Particular Order is Completed", "Norification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/outbound/pickup-confirm/' + paramdata]);

  }


 

  
 


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.picking.forEach(x => {
      res.push({
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Target Branch":x.targetBranchCode,
        "Token Number":x.tokenNumber,
        "Sales Order No":x.salesOrderNumber,
        "Status": x.statusDescription,
        'Order No': x.refDocNumber,
        "Order Type": x.referenceDocumentType,
        "Line No": x.lineNumber,
        "Mfr Name":x.manufacturerName,
        "Part No": x.itemCode,
        "Barcode Id": x.partnerItemBarcode,
        "Bin Location": x.proposedStorageBin,
        "Picker": x.assignedPickerId,
        "Order Qty": x.pickToQty,
        "Created Date":this.cs.dateapiwithTime(x.pickupCreatedOn),
        "Level":x.levelId,
        "Order Category": x.referenceField1,
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

updatePicker(Line: any){
  if (this.auth.warehouseId != 100) {
    let dataErrorArray: any = [];
    this.selectedpicker.forEach(data => {
      if (data.levelId != this.selectedpicker[0].levelId) {
        dataErrorArray.push(data.lineNumber);
      }
    });

    if (dataErrorArray.length > 0) {
      this.toastr.error(
        "The selected records have different level Id",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );
      this.cs.notifyOther(true);
      return;
    }
  }
  const dialogRef = this.dialog.open(UpdatePickerComponent, {
    disableClose: true,
    width: '100%',
    maxWidth: '55%',
    data: { levelID: Line.levelId, }
  });
  dialogRef.afterClosed().subscribe(result => {
    if(result){
    let data: any[] = [];
      data.push({
        itemCode: Line.itemCode,
        lineNumber: Line.lineNumber,
        partnerCode: Line.partnerCode,
        preOutboundNo: Line.preOutboundNo,
        pickupNumber: Line.pickupNumber,
        refDocNumber: Line.refDocNumber,
        warehouseId: Line.warehouseId,
      })
    this.form.controls.assignedPickerId.patchValue(result)
    console.log(data)
    this.spin.show();
    this.sub.add(this.service.updatePickerv2(this.form.getRawValue(),data[0].pickupNumber, data[0].itemCode, data[0].lineNumber, data[0].partnerCode, data[0].preOutboundNo,  data[0].refDocNumber ,this.auth.warehouseId,this.auth.companyId,this.auth.plantId,this.auth.languageId).subscribe(res => {
      this.spin.hide();
      this.toastr.success(result + " updated successfully.", "Notification", {
        timeOut: 2000,
        progressBar: false,
      })
      this.search(true);

    }, err => {

      this.cs.commonerrorNew(err);;
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
  this.selectedpicker.forEach((x: any) => {
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
    this.selectedpicker.forEach((x )=> {
      x.assignedPickerId = result;
    })
    this.spin.show();
    
    this.sub.add(this.service.multipleUpdatePickerv2(this.selectedpicker).subscribe(res => {
      this.spin.hide();
      this.toastr.success(" Reassigned successfully!", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      this.search(true);
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);;
    }));
  }
}
});
}

}
