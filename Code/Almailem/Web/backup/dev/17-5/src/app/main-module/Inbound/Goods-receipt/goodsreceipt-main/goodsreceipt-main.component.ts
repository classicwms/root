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
import { GoodsReceiptService } from "../goods-receipt.service";
import { Table } from "primeng/table";


@Component({
  selector: 'app-goodsreceipt-main',
  templateUrl: './goodsreceipt-main.component.html',
  styleUrls: ['./goodsreceipt-main.component.scss']
})
export class GoodsreceiptMainComponent implements OnInit {


  
  caseReceipt: any[] = [];
  selectedcaseReceipt : any[] = [];
  @ViewChild('caseReceiptTag') caseReceiptTag: Table | any;


  displayedColumns: string[] = ['select', 'statusId', 'refDocNumber', 'inboundOrderTypeId', 'preInboundNo', 'stagingNo', 'createdBy','createdOn',];
  constructor(private service: GoodsReceiptService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.search(true);
  }
  screenid: 1042 | undefined;

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
    this.caseReceipt.forEach(x => {
      res.push({
        
        "Status  ": this.cs.getstatus_text(x.statusId),
        "Order No ": x.refDocNumber,
        "Order Type ": this.cs.getinboundorderType_text(x.inboundOrderTypeId),
        'Preinbound No': x.preInboundNo,
        'Staging No': x.stagingNo,
        'Created By': x.createdBy,
        'Staging Date': this.cs.dateExcel(x.createdOn),

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Case Receipt");
  }

  deleteDialog() {
    // if (this.selection.selected.length === 0) {
    //   this.toastr.error("Kindly select any row", "Norification");
    //   return;
    // }
    // const dialogRef = this.dialog.open(DeleteComponent, {
    //   disableClose: true,
    //   width: '50%',
    //   maxWidth: '80%',
    //   position: { top: '9%', },
    //   // data: this.selection.selected[0],
    // });

    // dialogRef.afterClosed().subscribe(result => {

    //   if (result) {
    //     this.deleterecord(this.selection.selected[0]);

    //   }
    // });
  }
  deleterecord(id: any) {
    // this.spin.show();
    // this.sub.add(this.service.Delete(id.preInboundNo, id.warehouseId).subscribe((res) => {
    //   this.toastr.success(id.preInboundNo + " Deleted successfully.");

    //   this.spin.hide(); //this.getAllListData();
    //   window.location.reload();
    // }, err => {
    //   this.cs.commonerrorNew(err);
    //   this.spin.hide();
    // }));
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selectedcaseReceipt.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";
    if (this.selectedcaseReceipt[0].statusId === 24 && data != 'Display') {
      this.toastr.error("Order can't be proccessed as it's already confirmed.", "",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    if (this.selectedcaseReceipt.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedcaseReceipt[0], pageflow: data });
      this.router.navigate(['/main/inbound/goods-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/goods-create/' + paramdata]);
    }

  
  }



  warehouseId = this.auth.warehouseId

  searhform = this.fb.group({
    createdBy: [],
    endCreatedOn: [],
    inboundOrderTypeId: [],
    preInboundNo: [],
    refDocNumber: [],
    stagingNo: [],
    startCreatedOn: [],
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


  inboundOrderTypeListselected: any[] = [];
  inboundOrderTypeList: any[] = [];

  preInboundNoListselected: any[] = [];
  preInboundNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  stagingNoListselected: any[] = [];
  stagingNoList: any[] = [];


  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
      this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));

    }
    this.spin.show();
    this.service.search(this.searhform.value).subscribe(res => {
      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let tempInboundList: any[] = []
        const inboundOrderTypeId = [...new Set(res.map(item => item.inboundOrderTypeId))].filter(x => x != null)
        inboundOrderTypeId.forEach(x => tempInboundList.push({ value: x, label: this.cs.getinboundorderType_text(x)}));
        this.inboundOrderTypeList = tempInboundList;

        let temppreInboundNoList: any[] = []
        const preInboundNo = [...new Set(res.map(item => item.preInboundNo))].filter(x => x != null)
        preInboundNo.forEach(x => temppreInboundNoList.push({ value: x, label: x }));
        this.preInboundNoList = temppreInboundNoList;

        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(res.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;

        // let tempstagingNoList: any[] = []
        // const stagingNo = [...new Set(res.map(item => item.stagingNo))].filter(x => x != null)
        // stagingNo.forEach(x => tempstagingNoList.push({ id: x, itemName: x }));
        // this.refDocNumberList = tempstagingNoList;


        let tempstatusIdList: any[] = []
        const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
        this.statusIdList = tempstatusIdList;
      }
      this.caseReceipt = res;
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


onChange() {
  const choosen= this.selectedcaseReceipt[this.selectedcaseReceipt.length - 1];   
  this.selectedcaseReceipt.length = 0;
  this.selectedcaseReceipt.push(choosen);
}
}
