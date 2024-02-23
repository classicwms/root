import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
import { CommonService, dropdownelement } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { BusinesspartnerElement } from "../business-partner/business-partner.service";
import { BomNewComponent } from "./bom-new/bom-new.component";
import { BOMElement, BOMService } from "./bom.service";

// export interface  variant {


//   no: string;
//   status:  string;
//   preinboundno:  string;
//   date:  string;
//   refdocno:  string;
//   type:  string;
// }
// const ELEMENT_DATA:  variant[] = [
//   { no: "1", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "2", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "3", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "4", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "5", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "6", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "7", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "8", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "9", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "10", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
//   { no: "11", type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },


// ];
@Component({
  selector: 'app-bom',
  templateUrl: './bom.component.html',
  styleUrls: ['./bom.component.scss']
})
export class BomComponent implements OnInit {
  displayedColumns: string[] = ['select', 'parentItemCode','childItemCode', 'childItemQuantity','referenceField2','statusId', 'createdBy', 'createdOn', ];
  sub = new Subscription();
  ELEMENT_DATA: BOMElement[] = [];
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
    private service: BOMService,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService) { }
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
  showFiller = false;
  animal: string | undefined;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  ngOnInit(): void {
   // this.auth.isuserdata();

   // this.getAll();
   this.search(true);
  }

  dataSource = new MatTableDataSource<BOMElement>(this.ELEMENT_DATA);
  selection = new SelectionModel<BOMElement>(true, []);




  deleteDialog() {
  debugger
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    const dialogRef = this.dialog.open(DeleteComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
      data: this.selection.selected[0],
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selection.selected[0]);

      }
    });
  }
  deleterecord(id: any) {

    this.spin.show();

    this.sub.add(this.service.Delete(id.parentItemCode,id.warehouseId).subscribe((res) => {
      
      console.log(id);
      console.log(res);
      this.toastr.success(id + " Deleted successfully.","",{
        timeOut: 2000,
        progressBar: false,
      });

      this.spin.hide(); //this.getAll();
      window.location.reload();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  openDialog(data: any = 'New'): void {
    if (data != 'New')
      if (this.selection.selected.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    const dialogRef = this.dialog.open(BomNewComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
      data: { pageflow: data, code: data != 'New' ? this.selection.selected[0] : null }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.search(true);
     // this.getAll();
   //  window.location.reload();
    });
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
 // warehouseId : any;
  warehouseId = this.auth.warehouseId;
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
 // Pagination.

 
 searhform = this.fb.group({
  createdBy: [],
  endCreatedOn: [],
  endUpdatedOn: [],
  parentItemCode: [],
  startCreatedOn: [],
  startUpdatedOn: [],
  statusId: [],
  updatedBy: [],
  warehouseId: [[this.auth.warehouseId]],

});




dropdownSettings: IDropdownSettings = {
  idField: 'item_id',
  textField: 'item_text',
  selectAllText: 'Select All',
  unSelectAllText: 'UnSelect All',
  itemsShowLimit: 3,
  allowSearchFilter: true
};

// itemCodeListselected: any[] = [];
// itemCodeList: dropdownelement[] = [];

parentItemCodeListselected: any[] = [];
parentItemCodeList: dropdownelement[] = [];

partnerCodeListselected: any[] = [];
partnerCodeList: dropdownelement[] = [];

// preOutboundNoselected: any[] = [];
// preOutboundNoList: dropdownelement[] = [];


statusIdListselected: any[] = [];
statusIdList: dropdownelement[] = [];

search(ispageload = false) {
  if (!ispageload) {

    //dateconvertion
    this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
    this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
  
  
    //patching
    // const itemCode = [...new Set(this.itemCodeListselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.itemCode.patchValue(itemCode);
  
    const parentItemCode = [...new Set(this.parentItemCodeListselected.map(item => item.item_id))].filter(x => x != null);
    this.searhform.controls.parentItemCode.patchValue(parentItemCode);
  
    // const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.partnerCode.patchValue(partnerCode);
  
    // const preOutboundNo = [...new Set(this.preOutboundNoselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.preOutboundNo.patchValue(preOutboundNo);
  
  
  
    const statusId = [...new Set(this.statusIdListselected.map(item => item.item_id))].filter(x => x != null);
    this.searhform.controls.statusId.patchValue(statusId);
  }
 this.service.search(this.searhform.value).subscribe(res => {
   // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
   this.spin.hide();
   
   if (ispageload) {
    //  let tempitemCodeList: dropdownelement[] = []
    //  const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
    //  itemCode.forEach(x => tempitemCodeList.push({ item_id: x, item_text: x }));
    //  this.itemCodeList = tempitemCodeList;

     let tempparentItemCodeList: dropdownelement[] = []
     const parentItemCode = [...new Set(res.map(item => item.parentItemCode))].filter(x => x != null)
     parentItemCode.forEach(x => tempparentItemCodeList.push({ item_id: x, item_text: x }));
     this.parentItemCodeList = tempparentItemCodeList;

    //  let temppartnerCodeList: dropdownelement[] = []
    //  const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
    //  partnerCode.forEach(x => temppartnerCodeList.push({ item_id: x, item_text: x }));
    //  this.partnerCodeList = temppartnerCodeList;

    //  let temppreOutboundNoList: dropdownelement[] = []
    //  const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
    //  preOutboundNo.forEach(x => temppreOutboundNoList.push({ item_id: x, item_text: x }));
    //  this.preOutboundNoList = temppreOutboundNoList;


     let tempstatusIdList: dropdownelement[] = []
     const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
     statusId.forEach(x => tempstatusIdList.push({ item_id: x, item_text: x }));
     this.statusIdList = tempstatusIdList;
   }
   let result = res.filter((x: any) =>   x.deletionIndicator == 0);

   let finalData : any[] = [];

   result.forEach(bomheaderdata => {
    bomheaderdata.bomLines.forEach(bomlinedata => {
      bomlinedata['parentItemCode'] = bomheaderdata.parentItemCode;
      bomlinedata['createdBy'] = bomheaderdata.createdBy;
      bomlinedata['createdOn'] = bomheaderdata.createdOn;
      bomlinedata['statusId'] = bomheaderdata.statusId;
      finalData.push(bomlinedata)
    });
   });

   this.dataSource = new MatTableDataSource<any>(finalData);
   this.selection = new SelectionModel<BOMElement>(true, []);
   this.dataSource.sort = this.sort;
  this.dataSource.paginator = this.paginator;
 }, err => {

   this.cs.commonerrorNew(err);
   this.spin.hide();

 });   


}
reload(){
 window.location.reload();
}


 getAll() {
  this.spin.show();
  console.log(this.warehouseId)
  this.sub.add(this.service.Getall().subscribe((res: BOMElement[]) => {
    let result = res.filter((x: any) =>   x.warehouseId == this.warehouseId && x.deletionIndicator == 0);
    this.ELEMENT_DATA = result;
    console.log(this.ELEMENT_DATA);
    this.dataSource = new MatTableDataSource<any>(result);
    this.selection = new SelectionModel<BOMElement>(true, []);
    this.dataSource.sort = this.sort;
   this.dataSource.paginator = this.paginator;
    this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}


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
  checkboxLabel(row?: BOMElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.bomNumber + 1}`;
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
        " Parent Item Code": x.parentItemCode,
        "Child ItemCode ": x.childItemCode,
        "Child Item Qty": x.childItemQuantity,
        'Seq No': x.referenceField2,
        'Status': this.cs.getstatus_text(x.statusId),
        'Created By': x.createdby,
        "Created On": this.cs.dateapi(x.createdOn)
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "BOM");
  }
}