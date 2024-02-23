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
import { PutawayService } from "../putaway/putaway.service";
import { ReversalPopupComponent } from "./reversal-popup/reversal-popup.component";
import { Table } from "primeng/table";
import { ReversalEditComponent } from "./reversal-edit/reversal-edit.component";

@Component({
  selector: 'app-reversal',
  templateUrl: './reversal.component.html',
  styleUrls: ['./reversal.component.scss']
})
export class ReversalComponent implements OnInit {
  screenid= 3055 ;
  reversal: any[] = [];
  selectedreversal : any[] = [];
  @ViewChild('reversalTag') reversalTag: Table | any;

 

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

  openDialog(data: any =! 'New'): void {
    //console.log(this.selectedaisle)
    if (data != 'New')
    if (this.selectedreversal.length === 0) {
      this.toastr.warning("Kindly select any Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(ReversalEditComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: this.reversal[0]}
    });
  
    dialogRef.afterClosed().subscribe(result => {
      this.search();
    });
  }
  new(): void {

    const dialogRef = this.dialog.open(ReversalPopupComponent, {
      disableClose: true,
      width: '45%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.search();
    });
  }

 

  constructor(private service: PutawayService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.search(true);
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.reversal.forEach(x => {
      res.push({
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Status ": x.statusDescription,
        'Order Type': x.referenceDocumentType,
        'Putaway No': x.refDocNumber,
        "Part No":x.referenceField7,
        "Order No":x.refDocNumber,
        "Barcode Id": x.palletCode,
        "BinLocation": x.proposedStorageBin,
        "Case Received By": x.createdBy,
        "Case Received On":this.cs.dateapiwithTime(x.createdOn),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Reversals")
  }

  // searhform = this.fb.group({
  //   statusId: [[22]]
  // });
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId

  searhform = this.fb.group({
    barcodeId:[],
    itemCode:[],
    createdBy: [],
    endCreatedOn: [],
    packBarcodes: [],
    proposedHandlingEquipment: [],
    proposedStorageBin: [],
    putAwayNumber: [],
    refDocNumber: [],
    startCreatedOn: [],
    statusId: [[22],],
   warehouseId: [[this.auth.warehouseId]],
   companyCodeId: [[this.auth.companyId]],
   languageId: [[this.auth.languageId]],
   plantId: [[this.auth.plantId]],
 
  });
 
 
 
  dropdownSettings = {
    singleSelection: false, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
 
  packBarcodesListselected: any[] = [];
  packBarcodesList: any[] = [];
 
  proposedHandlingEquipmentListselected: any[] = [];
  proposedHandlingEquipmentList: any[] = [];
 
  proposedStorageBinListselected: any[] = [];
  proposedStorageBinList: any[] = [];
 
  putAwayNumberListselected: any[] = [];
  putAwayNumberList: any[] = [];
 
  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];
 
  statusIdListselected: any[] = [];
  statusIdList: any[] = [];
  
  itemcodeListSelected: any[]=[];
  itemCodeList: any[]=[];
 
  search(ispageload = false) {
    if (!ispageload) {
  }
  this.spin.show();

   this.service.searchSpark(this.searhform.value).subscribe(res => {
     // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
     let result = res.filter((x: any) => x.statusId == 22)
     this.spin.hide();
     if (ispageload) {
       let temppackBarcodesList: any[] = []
       const packBarcodes = [...new Set(result.map(item => item.barcodeId))].filter(x => x != null)
       packBarcodes.forEach(x => temppackBarcodesList.push({ value: x, label: x }));
       this.packBarcodesList = temppackBarcodesList;
 
       let tempproposedStorageBinList: any[] = []
       const proposedStorageBin = [...new Set(result.map(item => item.proposedStorageBin))].filter(x => x != null)
       proposedStorageBin.forEach(x => tempproposedStorageBinList.push({ value: x, label: x }));
       this.proposedStorageBinList = tempproposedStorageBinList;
 
       let tempputAwayNumberList: any[] = []
       const putAwayNumber = [...new Set(result.map(item => item.putAwayNumber))].filter(x => x != null)
       putAwayNumber.forEach(x => tempputAwayNumberList.push({ value: x, label: x }));
       this.putAwayNumberList = tempputAwayNumberList;
 
       let temprefDocNumberList: any[] = []
       const refDocNumber = [...new Set(result.map(item => item.refDocNumber))].filter(x => x != null)
       refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
       this.refDocNumberList = temprefDocNumberList;
 
       let tempstatusIdList: any[] = [];
       tempstatusIdList.push({ value: 22, label: "Putaway reversed" });
       res.forEach(x => tempstatusIdList.push({ value: x.statusId, label: x.statusDescription }));
       this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(tempstatusIdList);

       let  tempitemCodeList: any[]=[];
       const itemCode= [...new Set(result.map(item => item.referenceField7))].filter(x => x != null)
       itemCode.forEach(x => tempitemCodeList.push({ value: x, label: x }));
       this.itemCodeList=tempitemCodeList;
           }
     this.reversal = res;
   }, err => {
 
     this.cs.commonerrorNew(err);
     this.spin.hide();
 
   });   

 
 }
 reload(){
   this.searhform.reset();
   this.searhform.controls.companyCodeId.patchValue([this.auth.companyId])
   this.searhform.controls.languageId.patchValue([this.auth.languageId])
   this.searhform.controls.plantId.patchValue([this.auth.plantId])
   this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId])
   this.searhform.controls.statusId.patchValue([22])
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


onChange() {
  const choosen= this.selectedreversal[this.selectedreversal.length - 1];   
  this.selectedreversal.length = 0;
  this.selectedreversal.push(choosen);
}
}
