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
import { GoodsReceiptService } from "../../Goods-receipt/goods-receipt.service";
import { ItemReceiptService } from "../item-receipt.service";
import { CasenoPopupComponent } from "./caseno-popup/caseno-popup.component";
import { Table } from "primeng/table";

@Component({
  selector: 'app-item-main',
  templateUrl: './item-main.component.html',
  styleUrls: ['./item-main.component.scss']
})
export class ItemMainComponent implements OnInit {

  itemReceipt: any[] = [];
  selecteditemReceipt : any[] = [];
  @ViewChild('itemReceiptTag') itemReceiptTag: Table | any;

  screenid: 1049 | undefined;

  caseno(): void {

    const dialogRef = this.dialog.open(CasenoPopupComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '50%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  constructor(private service: ItemReceiptService,
    private grservice: GoodsReceiptService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService, private itemService: ItemReceiptService) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.search(true);
  }

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




  searchCaseNo(event: Event) {

//     const filterValue = (event.target as HTMLInputElement).value;
//     this.itemReceipt.filter = filterValue.trim();
// if(this.dataSource.filter != null && this.dataSource.filter != undefined  && this.dataSource.filter != '') {
//       this.barCodeConfirm(this.dataSource.filter)
//    }
  }



  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.itemReceipt.forEach(x => {
      res.push({

        "Status ": this.cs.getstatus_text(x.statusId),
        "Order No ": x.refDocNumber,
        'Preinbound No': x.preInboundNo,
        "Case No": x.caseCode,
        "Case Received By ": x.createdBy,
        'Case Received On': this.cs.dateExcel(x.createdOn),

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Goods Receipt");
  }
  deleteDialog() {
    if (this.selecteditemReceipt.length === 0) {
      this.toastr.error("Kindly select any row", "Norification");
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
        this.deleterecord(this.selecteditemReceipt[0]);

      }
    });
  }
  deleterecord(id: any) {
    this.spin.show();
    this.sub.add(this.itemService.deleteGRHeader(id).subscribe((ress) => {
      this.sub.add(this.itemService.searchline({caseCode: [id.caseCode]}).subscribe(res => {
      res.forEach(element => {
        this.sub.add(this.itemService.deleteGRLine(element).subscribe((ress) => {
          this.toastr.success(id.goodsReceiptNo + " Deleted successfully.", "Notification",{
            timeOut: 2000,
            progressBar: false,
          });
        }))
      });
      }))
    //  this.toastr.success(id.preInboundNo + " Deleted successfully.");
      this.spin.hide(); 
     this.search(true);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selecteditemReceipt.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";
    if (this.selecteditemReceipt.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selecteditemReceipt[0], pageflow: data });
      this.router.navigate(['/main/inbound/item-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/item-create/' + paramdata]);
    }

  }


  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId


  searhform = this.fb.group({
    caseCode: [],
    itemCode: [],
    lineNo: [],
    packBarcodes: [],
    preInboundNo: [],
    refDocNumber: [],
    statusId: [[16, 17]],

  });




  dropdownSettings = {
    singleSelection: false, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };


  caseCodeListselected: any[] = [];
  caseCodeList: any[] = [];

  itemCodeListselected: any[] = [];
  itemCodeList: any[] = [];

  lineNoListselected: any[] = [];
  lineNoList: any[] = [];

  packBarcodesListselected: any[] = [];
  packBarcodesList: any[] = [];

  preInboundNoListselected: any[] = [];
  preInboundNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  search(ispageload = false) {

    if (!ispageload) {

    }
    this.spin.show();
    this.service.search(this.searhform.value).subscribe(res => {
    let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let tempcaseCodeList: any[] = []
        const caseCode = [...new Set(res.map(item => item.caseCode))].filter(x => x != null)
        caseCode.forEach(x => tempcaseCodeList.push({ value: x, label: x }));
        this.caseCodeList = tempcaseCodeList;

        let tempitemCodeList: any[] = []
        const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
        itemCode.forEach(x => tempitemCodeList.push({ value: x, label: x }));
        this.itemCodeList = tempitemCodeList;

        let templineNoList: any[] = []
        const lineNo = [...new Set(res.map(item => item.lineNo))].filter(x => x != null)
        lineNo.forEach(x => templineNoList.push({ value: x, label: x }));
        this.lineNoList = templineNoList;

        let temppackBarcodesList: any[] = []
        const packBarcodes = [...new Set(res.map(item => item.packBarcodes))].filter(x => x != null)
        packBarcodes.forEach(x => temppackBarcodesList.push({ value: x, label: x }));
        this.packBarcodesList = temppackBarcodesList;

        let temppreInboundNoList: any[] = []
        const preInboundNo = [...new Set(res.map(item => item.preInboundNo))].filter(x => x != null)
        preInboundNo.forEach(x => temppreInboundNoList.push({ value: x, label: x }));
        this.preInboundNoList = temppreInboundNoList;

        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(res.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;


        let tempstatusIdList: any[] = []
        const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
        this.statusIdList = tempstatusIdList;
      }
      this.itemReceipt = result;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    });



  }
  reload() {
   this.searhform.reset();
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

barCodeConfirm(data): void {
  console.log(data)
      this.sub.add(this.grservice.searchLine({caseCode: [data], }).subscribe(res => {
    if(res.length>0){
      
      let paramdata = "";
        paramdata = this.cs.encrypt({ code: res[0], pageflow: data });
        this.router.navigate(['/main/inbound/item-create/' + paramdata]);
    }
    else {
      this.toastr.error("Invalid Case No", "Notification",{
        timeOut: 2000,
        progressBar: false,
      });
  //    this.dataSource.filter = ''
      return;
  
  }
          }));

 

}

onChange() {
  const choosen= this.selecteditemReceipt[this.selecteditemReceipt.length - 1];   
  this.selecteditemReceipt.length = 0;
  this.selecteditemReceipt.push(choosen);
}
}
