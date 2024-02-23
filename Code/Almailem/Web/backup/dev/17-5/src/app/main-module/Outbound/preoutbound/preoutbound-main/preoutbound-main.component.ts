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
import { PreoutboundCreateComponent } from "../preoutbound-create/preoutbound-create.component";
import { PreoutboundNewComponent } from "../preoutbound-new/preoutbound-new.component";
import { PreoutboundService } from "../preoutbound.service";
import { Table } from "primeng/table";

export interface preoutbound {


  no: string;
  actions: string;
  status: string;
  order: string;
  orderedlines: string;
  date: string;
  outboundno: string;
  refno: string;
  required: string;
  scode: string;
  sname: string;
}
const ELEMENT_DATA: preoutbound[] = [
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },
  { no: "Value", order: 'Value', refno: 'Value', outboundno: 'Value', orderedlines: 'Value', scode: 'Value', sname: 'Value', date: 'date', required: 'date', status: 'date', actions: 's' },

];

interface soTypeList {
  value: string,
  label: string
}

@Component({
  selector: 'app-preoutbound-main',
  templateUrl: './preoutbound-main.component.html',
  styleUrls: ['./preoutbound-main.component.scss']
})
export class PreoutboundMainComponent implements OnInit {
  preoutboundMain: any[] = [];
  selectedpreoutboundManin : any[] = [];
  @ViewChild('preoutboundMainTag') preoutboundMainTag: Table | any;
  screenid: 1059 | undefined;
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


  soTypeList1: soTypeList[];

  constructor(private service: PreoutboundService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { 
      this.soTypeList1 = [
        {value: 'N', label: 'N'},
        {value: 'S', label: 'S'},
    ];
    }
  sub = new Subscription();

  ngOnInit(): void {
    this.getstorename()
    this.search(true);

  }


  warehouseId = this.auth.warehouseId
 
  searhform = this.fb.group({
    createdBy: [],
    endCreatedOn:[],
    endOrderDate:[],
    endRequiredDeliveryDate:[],
    outboundOrderTypeId: [],
    partnerCode: [],
    preOutboundNo: [],
    soNumber: [],
    soType: [,],
    startCreatedOn:[],
    startOrderDate:[],
    startRequiredDeliveryDate:[],
    statusId: [],
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

  outboundOrderTypeIdListselected: any[] = [];
  outboundOrderTypeIdList: any[] = [];
 
  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];
 
  preOutboundNoselected: any[] = [];
  preOutboundNoList: any[] = [];
 
  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];
 

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];
 
  getstorename(){
    this.sub.add(this.service.GetStoreCode().subscribe(res => {
      this.storecodeList = res;
      console.log(this.storecodeList)
    },
    err => {
      this.cs.commonerrorNew(err);;
    }));
  }


  search(ispageload = false) {
    if (!ispageload) {
 
      //dateconvertion
      this.searhform.controls.endRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endRequiredDeliveryDate.value));
      this.searhform.controls.startRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startRequiredDeliveryDate.value));
      this.searhform.controls.endOrderDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endOrderDate.value));
      this.searhform.controls.startOrderDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startOrderDate.value));
  
  
   
  }
  this.spin.show();
   this.service.search(this.searhform.value).subscribe(res => {
    //  let result = res.filter((x: any) => x.statusId == 39);
    let result = res
    
        
  
     if (ispageload) {

       let tempoutboundOrderTypeIdList: any[] = []
       const outboundOrderTypeId = [...new Set(result.map(item => item.outboundOrderTypeId))].filter(x => x != null)
       outboundOrderTypeId.forEach(x => tempoutboundOrderTypeIdList.push({ value: x, label: this.cs.getoutboundOrderType_text(x) }));
       this.outboundOrderTypeIdList = tempoutboundOrderTypeIdList;
 
       let temppartnerCodeList: any[] = []
       const partnerCode = [...new Set(result.map(item => item.partnerCode))].filter(x => x != null)
       partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
       this.partnerCodeList = temppartnerCodeList;
 
       let temppreOutboundNoList: any[] = []
       const preOutboundNo = [...new Set(result.map(item => item.preOutboundNo))].filter(x => x != null)
       preOutboundNo.forEach(x => temppreOutboundNoList.push({ value: x, label: x }));
       this.preOutboundNoList = temppreOutboundNoList;
 
      //  let temprefDocNumberList: any[] = []
      //  const refDocNumber = [...new Set(result.map(item => item.refDocNumber))].filter(x => x != null)
      //  refDocNumber.forEach(x => temprefDocNumberList.push({ id: x, itemName: x }));
      //  this.refDocNumberList = temprefDocNumberList;


       let tempstatusIdList: any[] = []
       const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
       statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
       this.statusIdList = tempstatusIdList;

       

     }
     this.preoutboundMain = result;
  
     this.spin.hide();
   }, err => {
 
     this.cs.commonerrorNew(err);;
     this.spin.hide();
 
   });   

 
 }
 reload(){
   this.searhform.reset();
 }


  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selectedpreoutboundManin.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";
    debugger
    if (this.selectedpreoutboundManin.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedpreoutboundManin[0], pageflow: data });
      this.router.navigate(['/main/outbound/preoutbound-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/outbound/preoutbound-create/' + paramdata]);
    }
  }






 
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.preoutboundMain.forEach(x => {
      res.push({

        "Status  ": this.cs.getstatus_text(x.statusId),
        'Store': x.partnerCode,
        'Order Type': this.cs.getoutboundOrderType_text(x.outboundOrderTypeId),
        "Order Category": x.referenceField1,
        "Preoutbound No": x.preOutboundNo,
        "Order No": x.refDocNumber,
        "Ordered Date": this.cs.dateExcel(x.refDocDate),
        "Required Date": this.cs.dateExcel(x.requiredDeliveryDate),


        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Preoutbound");
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

preOutboundUpload(){
  const dialogRef = this.dialog.open(PreoutboundNewComponent, {
    disableClose: true,
    width: '80%',
    maxWidth: '80%',
    position: { top: '9%', },
  });
  
  dialogRef.afterClosed().subscribe(result => {
})


}
onChange() {
  const choosen= this.selectedpreoutboundManin[this.selectedpreoutboundManin.length - 1];   
  this.selectedpreoutboundManin.length = 0;
  this.selectedpreoutboundManin.push(choosen);
}
}
