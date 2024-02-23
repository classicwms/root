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
import { BomNewComponent } from "../bom/bom-new/bom-new.component";
import { BOMService } from "../bom/bom.service";
import { EquipmentNewComponent } from "../handling-equipment/equipment-new/equipment-new.component";
import { PackingmaterialElement } from "../packing-material/packing-material.service";
import { BusinessNewComponent } from "./business-new/business-new.component";
import { BusinesspartnerElement, BusinessPartnerService } from "./business-partner.service";

export interface  variant {


  no: string;
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  date:  string;
  refdocno:  string;
  type:  string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "2", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "3", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "4", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "5", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "6", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "7", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "8", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "9", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "10", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  { no: "11", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },


];
@Component({
  selector: 'app-business-partner',
  templateUrl: './business-partner.component.html',
  styleUrls: ['./business-partner.component.scss']
})
export class BusinessPartnerComponent implements OnInit {



  screenid: 1038 | undefined;
  displayedColumns: string[] = ['select', 'businessPartnerType','partnerCode', 'partnerName', 'statusId','createdby', 'createdon', ];
  sub = new Subscription();
  ELEMENT_DATA: BusinesspartnerElement[] = [];
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
    private service: BusinessPartnerService,
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

  //  this.getAll();
  this.search(true);
  }

  dataSource = new MatTableDataSource<BusinesspartnerElement>(this.ELEMENT_DATA);
  selection = new SelectionModel<BusinesspartnerElement>(true, []);




  deleteDialog() {
 
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
      data: this.selection.selected[0].partnerCode,
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selection.selected[0].partnerCode);

      }
    });
  }
  deleterecord(id: any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id).subscribe((res) => {
      // console.log(id);
      // console.log(id.handlingEquipmentId);
      // console.log(res);
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
    const dialogRef = this.dialog.open(BusinessNewComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
      data: { pageflow: data, code: data != 'New' ? this.selection.selected[0].partnerCode : null }
    });

    dialogRef.afterClosed().subscribe(result => {

     // this.getAll();
     window.location.reload();
    });
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
 // Pagination
 warehouseId=this.auth.warehouseId

 searhform = this.fb.group({
  businessPartnerType: [],
  createdBy:[],
  endCreatedOn: [],
  endUpdatedOn: [],
  partnerCode:[],
  partnerName:[],
  startCreatedOn: [],
  startUpdatedOn: [],
  statusId: [],
  updatedBy:[],
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

businessPartnerTypeListselected: any[] = [];
businessPartnerTypeList: dropdownelement[] = [];

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
  
    const businessPartnerType = [...new Set(this.businessPartnerTypeListselected.map(item => item.item_id))].filter(x => x != null);
    this.searhform.controls.businessPartnerType.patchValue(businessPartnerType);
  
    const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.item_id))].filter(x => x != null);
    this.searhform.controls.partnerCode.patchValue(partnerCode);
  
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

     let tempbusinessPartnerTypeList: dropdownelement[] = []
     const businessPartnerType = [...new Set(res.map(item => item.businessPartnerType))].filter(x => x != null)
     businessPartnerType.forEach(x => tempbusinessPartnerTypeList.push({ item_id: x, item_text: x }));
     this.businessPartnerTypeList = tempbusinessPartnerTypeList;

     let temppartnerCodeList: dropdownelement[] = []
     const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
     partnerCode.forEach(x => temppartnerCodeList.push({ item_id: x, item_text: x }));
     this.partnerCodeList = temppartnerCodeList;

    //  let temppreOutboundNoList: dropdownelement[] = []
    //  const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
    //  preOutboundNo.forEach(x => temppreOutboundNoList.push({ item_id: x, item_text: x }));
    //  this.preOutboundNoList = temppreOutboundNoList;


     let tempstatusIdList: dropdownelement[] = []
     const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
     statusId.forEach(x => tempstatusIdList.push({ item_id: x, item_text: x }));
     this.statusIdList = tempstatusIdList;
   }
   this.dataSource = new MatTableDataSource<any>(res);
   this.selection = new SelectionModel<BusinesspartnerElement>(true, []);
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
  this.sub.add(this.service.Getall().subscribe((res: BusinesspartnerElement[]) => {
    let result = res.filter((x: any) =>   x.warehouseId == this.warehouseId);
    this.ELEMENT_DATA = result;
    console.log(this.ELEMENT_DATA);
    this.dataSource = new MatTableDataSource<any>(result);
    this.selection = new SelectionModel<BusinesspartnerElement>(true, []);
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
  checkboxLabel(row?: BusinesspartnerElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.partnerCode + 1}`;
  }






  clearselection(row: any) {

    this.selection.clear();
    this.selection.toggle(row);
  }
}