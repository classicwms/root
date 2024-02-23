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
import { QualityService } from "../quality.service";
import { Location } from "@angular/common";
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
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
  mfr: string;
}

const ELEMENT_DATA: ordermanagement[] = [
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', mfr: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },

];
@Component({
  selector: 'app-quality-main',
  templateUrl: './quality-main.component.html',
  styleUrls: ['./quality-main.component.scss']
})
export class QualityMainComponent implements OnInit {
  screenid= 3065 ;
  quality: any[] = [];
  selectedquality : any[] = [];
  @ViewChild('qualityTag') qualityTag: Table | any;
  
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
  constructor(private service: QualityService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private location: Location,
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
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  searhform = this.fb.group({
    actualHeNo: [],
    endqualityCreatedOn: [],
    outboundOrderTypeId: [],
    partnerCode: [],
    qualityInspectionNo: [],
    refDocNumber: [],
    soType: [],
    companyCodeId:[[this.auth.companyId]],
    languageId:[[this.auth.languageId]],
    plantId:[[this.auth.plantId]],
    startqualityCreatedOn: [],
    statusId: [[54]],
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

  outboundOrderTypeIdListselected: any[] = [];
  outboundOrderTypeIdList: any[] = [];

  actualHeNoListselected: any[] = [];
  actualHeNoList: any[] = [];

  qualityInspectionNoselected: any[] = [];
  qualityInspectionNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];


  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  getstorename() {
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

      //dateconvertion
      this.searhform.controls.endqualityCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endqualityCreatedOn.value));
      this.searhform.controls.startqualityCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startqualityCreatedOn.value));


    
    }

    //this.searhform.controls.statusId.value.push(54);
    this.service.searchSpark(this.searhform.value).subscribe(res => {

  
      if (ispageload) {
        this.spin.show();
        let tempoutboundOrderTypeIdList: any[] = []
        const outboundOrderTypeId = [...new Set(res.map(item => item.outboundOrderTypeId))].filter(x => x != null)
        outboundOrderTypeId.forEach(x => tempoutboundOrderTypeIdList.push({ value: x, label: this.cs.getoutboundOrderType_text(x) }));
        this.outboundOrderTypeIdList = tempoutboundOrderTypeIdList;

        let tempactualHeNoList: any[] = []
        const actualHeNo = [...new Set(res.map(item => item.actualHeNo))].filter(x => x != null)
        actualHeNo.forEach(x => tempactualHeNoList.push({ value: x, label: x }));
        this.actualHeNoList = tempactualHeNoList;

        let tempqualityInspectionNoList: any[] = []
        const qualityInspectionNo = [...new Set(res.map(item => item.qualityInspectionNo))].filter(x => x != null)
        qualityInspectionNo.forEach(x => tempqualityInspectionNoList.push({ value: x, label: x }));
        this.qualityInspectionNoList = tempqualityInspectionNoList;

        let temppartnerCodeList: any[] = []
        const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
        this.partnerCodeList = temppartnerCodeList;

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


      res.forEach(element => {
        element.qualityQty = (Math.round(element.qcToQty * 100) / 100);
        element.rejectQty = 0;
        element.pickConfirmQty = (Math.round(element.qcToQty * 100) / 100);
      });

      
      res = this.cs.removeDuplicatesFromArrayList(res , 'refDocNumber')
      this.quality = res;
    
      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);;
      this.spin.hide();

    });

  }
  reload() {
    this.searhform.reset();
  }
  openConfirm(data: any) {
    if(data.statusId != 54){
      this.toastr.error("Quality Process of this Particular Order is Completed", "Norification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/outbound/quality-confirm/' + paramdata]);

  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selectedquality.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }

    let paramdata = "";

    if (this.selectedquality.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedquality[0], pageflow: data });
      this.router.navigate(['/main/outbound/quality-confirm/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/outbound/quality-confirm/' + paramdata]);
    }
  }




 

  

  
  
  onKey(element: any) { // without type info
    if (element.pickConfirmQty < element.qualityQty) {
      element.qcToQty = '';
      this.toastr.error("To Qty is greater than picking Qty.", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
    element.rejectQty = (element.pickConfirmQty as number) - (element.qualityQty as number);
  }

   multipleQualityConfirm(){
    
    if(this.selectedquality.length < this.quality.length){
    //  thi
    }
 if(this.selectedquality.length == 0){
  this.toastr.error(
    "Please select a line to confirm",
    "Notification"
  );
   return;
 }
     let data: any[] = [];
     // start CWMS/IW/2022/018 mugilan 03-10-2022
    let filteredData: any[] = [];
     if(this.quality.length < this.selectedquality.length){
       filteredData = this.quality
    }
   else{
      filteredData = this.selectedquality
    }
     console.log(filteredData)
    filteredData.forEach((x: any, index) => {
      data.push({
        "actualHeNo": x.actualHeNo,
         "companyCodeId": x.companyCodeId,
        "description": x.description,
         "itemCode": x.referenceField4,
        "languageId": x.languageId,
        "lineNumber": x.referenceField5,      
        "outboundOrderTypeId": x.outboundOrderTypeId,
        "Sales Order No":x.salesOrderNumber,
        "plantId": x.plantId,
         "pickConfirmQty": parseInt(x.qualityQty),
         "preOutboundNo": x.preOutboundNo,
         "qualityInspectionNo": x.qualityInspectionNo,
       "pickPackBarCode": x.referenceField2,
        "qualityQty": parseInt(x.qualityQty),
        "qualityConfirmUom": x.pickUom,
        "refDocNumber": x.refDocNumber,
        "stockTypeId": x.stockTypeId,
        "warehouseId": x.warehouseId,


       });
    
      });
        // end CWMS/IW/2022/018 mugilan 03-10-2022
     this.spin.show();
       this.sub.add(this.service.confirmv2(data).subscribe(res => {
  
        this.toastr.success('Quality confirmed Successfully for the selected items', "Notifications", {
          timeOut: 2000,
          progressBar: false,
      })
  
        this.spin.hide();
      // this.location.back()
        // this.getclient_class(this.form.controls.classId.value);
       this.search(true);
      }, err => {
        this.cs.commonerrorNew(err);;
        this.spin.hide();
        this.search(true);
      }));
  
   }

  deleteDialog() {
    if (this.selectedquality.length === 0) {
      this.toastr.error("Kindly select any row", "Norification");
      return;
    }
    if (this.selectedquality[0].statusId != 54) {
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
        this.deleterecord(this.selectedquality[0]);

      }
    });
  }
  deleterecord(obj: any) {
    this.spin.show();
    this.sub.add(this.service.deleteQualitypHeaderv2(obj).subscribe((ress) => {
      this.toastr.success(obj.qualityInspectionNo + " Deleted successfully.", "Notification",{
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
  
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.quality.forEach(x => {
      res.push({
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Target Branch":x.targetBranchCode,
        "Token Number":x.tokenNumber,
        "Sales Order No":x.salesOrderNumber,
        "Status": x.statusDescription,
        "Order No  ": x.refDocNumber,
        "Mfr Name":x.manufacturerName,
        "Barcode Id":x.referenceField2,
        "Quality By":x.qualityCreatedBy,
        "Qulaity Date":this.cs.dateapiwithTime(x.qualityCreatedOn),
        
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Quality");
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
  onChange() {
    const choosen= this.selectedquality[this.selectedquality.length - 1];   
    this.selectedquality.length = 0;
    this.selectedquality.push(choosen);
  }
}
