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
import { Table } from "primeng/table/table";
import { Subscription } from "rxjs";
import { CommonService, dropdownelement } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { SelectionElement } from "../../masters/selection/masterproductselection.service";
import { StorageselectionElement, MasterstorageselectionService } from "./masterstorageselection.service";
import { StoragePopupComponent } from "./storage-popup/storage-popup.component";

// export interface selection {
//   no: string;
//   lineno: string;
//   partner: string;
//   product: string;
//   description: string;
//   refdocno: string;
//   variant: string;
//   type: string;
//   level: string;
  
//   }
  
//   const ELEMENT_DATA: selection[] = [
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  
//   ];
@Component({
  selector: 'app-storage-selection',
  templateUrl: './storage-selection.component.html',
  styleUrls: ['./storage-selection.component.scss']
})
export class StorageSelectionComponent implements OnInit {
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  advanceFilterShow: boolean;
  @ViewChild('Setupstorageselection') Setupstorageselection: Table | undefined;
  storageselection: any;
  selectedstorage : any;
  displayedColumns: string[] = ['select','statusId', 'warehouseId', 'storageBin', 'floorId', 'storageSectionId', 'spanId','createdBy','createdOn',];
  sub = new Subscription();
  ELEMENT_DATA: StorageselectionElement[] = [];
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
    private service: MasterstorageselectionService,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    private router: Router, 
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

  dataSource = new MatTableDataSource<StorageselectionElement>(this.ELEMENT_DATA);
  selection = new SelectionModel<StorageselectionElement>(true, []);


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.storageselection.forEach(x => {
      res.push( {
        "Status": x.statusId == 1 ? 'Active' : x.statusId == 0 ? 'InActive' : ''  ,
          "Warehouse No ": x.warehouseId,
          'Storage Bin': x.storageBin,
          ' Floor': x.floorId,
          "Storage Section": x.storageSectionId,
          "Level  ": x.spanId,
          "Created By": x.createdBy,
          "Created On":this.cs.dateapiwithTime(x.createdOn),
          // 'Created By': x.createdBy,
          // 'Date': this.cs.dateapi(x.createdOn),
        });

    })
    this.cs.exportAsExcel(res, "Storage Master");
  }

  // deleteDialog() {
    
  //   if (this.selection.selected.length === 0) {
  //     this.toastr.error("Kindly select any row", "");
  //     return;
  //   }
  //   const dialogRef = this.dialog.open(DeleteComponent, {
      
  //     disableClose: true,
  //     width: '50%',
  //     maxWidth: '80%',
  //     position: { top: '9%', },
  //     data: this.selection.selected[0].handlingUnit,
  //   });

  //   dialogRef.afterClosed().subscribe(result => {

  //     if (result) {
  //       this.deleterecord(this.selection.selected[0].handlingUnit);

  //     }
  //   });
  // }
  // deleterecord(id: any) {
  //   this.spin.show();
  //   this.sub.add(this.service.Delete(id).subscribe((res) => {
  //     // console.log(id);
  //     // console.log(id.handlingEquipmentId);
  //     // console.log(res);
  //     this.toastr.success(id + " Deleted successfully.");
  //     this.spin.hide(); //this.getAll();
  //     window.location.reload();
  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }));
  // }
  // openDialog(data: any = 'New'): void {
  //   if (data != 'New')
  //     if (this.selection.selected.length === 0) {
  //       this.toastr.error("Kindly select any row", "");
  //       return;
  //     }
  //   const dialogRef = this.dialog.open(UnitNewComponent, {
  //     disableClose: true,
  //     width: '50%',
  //     maxWidth: '80%',
  //     position: { top: '9%', },
  //     data: { pageflow: data, code: data != 'New' ? this.selection.selected[0].handlingUnit : null }
  //   });

  //   dialogRef.afterClosed().subscribe(result => {

  //    // this.getAll();
  //    window.location.reload();
  //   });
  // }
  selectionpara(): void {

    const dialogRef = this.dialog.open(StoragePopupComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
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
 warehouseId = this.auth.warehouseId;
 getAll() {
  this.spin.show();
  this.sub.add(this.service.Getall().subscribe((res: StorageselectionElement[]) => {
    let result = res.filter((x: any) =>   x.warehouseId == this.warehouseId);
    this.ELEMENT_DATA = result;
    console.log(this.ELEMENT_DATA);
    this.dataSource = new MatTableDataSource<any>(result);
    this.selection = new SelectionModel<StorageselectionElement>(true, []);
    this.dataSource.sort = this.sort;
   this.dataSource.paginator = this.paginator;
    this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}


searhform = this.fb.group({
  aisleNumber: [],
  createdBy: [],
  endCreatedOn: [],
  endUpdatedOn: [],
  floorId: [],
  rowId: [],
  shelfId: [],
  spanId: [],
  startCreatedOn: [],
  startUpdatedOn: [],
  storageBin: [],
  storageSectionId: [],
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

storageSectionIdListselected: any[] = [];
storageSectionIdList: any[] = [];

storageBinListselected: any[] = [];
storageBinList: any[] = [];

floorIdListselected: any[] = [];
floorIdList: any[] = [];

// preOutboundNoselected: any[] = [];
// preOutboundNoList: any[] = [];


spanIdListselected: any[] = [];
spanIdList: any[] = [];

search(ispageload = false) {
  if (!ispageload) {

    //dateconvertion
    // this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
    // this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
  
  
  //  patching
    // const storageSectionId = [...new Set(this.storageSectionIdListselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.storageSectionId.patchValue(storageSectionId);
  
    // const storageBin = [...new Set(this.storageBinListselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.storageBin.patchValue(storageBin);
  
    // const floorId = [...new Set(this.floorIdListselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.floorId.patchValue(floorId);
  
    // const preOutboundNo = [...new Set(this.preOutboundNoselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.preOutboundNo.patchValue(preOutboundNo);
  
    // const spanId = [...new Set(this.spanIdListselected.map(item => item.item_id))].filter(x => x != null);
    // this.searhform.controls.spanId.patchValue(spanId);
  }
 this.service.search(this.searhform.value).subscribe(res => {
   // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
   this.spin.hide();
   console.log(res)
   if (ispageload) {
     let tempstorageSectionIdList: any[] = []
     const storageSectionId = [...new Set(res.map(item => item.storageSectionId))].filter(x => x != null)
     storageSectionId.forEach(x => tempstorageSectionIdList.push({ value: x, label: x }));
     this.storageSectionIdList = tempstorageSectionIdList;

     let tempstorageBinList: any[] = []
     const storageBin = [...new Set(res.map(item => item.storageBin))].filter(x => x != null)
     storageBin.forEach(x => tempstorageBinList.push({ value: x, label: x }));
     this.storageBinList = tempstorageBinList;

     let tempfloorIdList: any[] = []
     const floorId = [...new Set(res.map(item => item.floorId))].filter(x => x != null)
     floorId.forEach(x => tempfloorIdList.push({ value: x, label: x }));
     this.floorIdList = tempfloorIdList;

    //  let temppreOutboundNoList: any[] = []
    //  const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
    //  preOutboundNo.forEach(x => temppreOutboundNoList.push({ item_id: x, item_text: x }));
    //  this.preOutboundNoList = temppreOutboundNoList;


     let tempspanIdList: any[] = []
     const spanId = [...new Set(res.map(item => item.spanId))].filter(x => x != null)
     spanId.forEach(x => tempspanIdList.push({ value: x, label: x }));
     this.spanIdList = tempspanIdList;
   }
   this.storageselection = res;
  //  this.dataSource = new MatTableDataSource<any>(res);
  //  this.selection = new SelectionModel<StorageselectionElement>(true, []);
  //  this.dataSource.sort = this.sort;
  // this.dataSource.paginator = this.paginator;
 }, err => {

   this.cs.commonerrorNew(err);
   this.spin.hide();

 });   


}
reload(){
this.searhform.reset();
}

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selectedstorage[0].length;
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
  checkboxLabel(row?: StorageselectionElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.storageBin + 1}`;
  }






  clearselection(row: any) {

    this.selection.clear();
    this.selection.toggle(row);
  }

  onEditClick()
  {
    sessionStorage.setItem('binLocation', JSON.stringify(this.selectedstorage[0]));
    this.routeto('/main/masters-storage/basic-data', 1008);
  }

  onNewClick()
  {
    sessionStorage.removeItem('binLocation');
    this.routeto('/main/masters-storage/basic-data', 1008);
  }
  onChange() {
    console.log(this.selectedstorage.length)
    const choosen= this.selectedstorage[this.selectedstorage.length - 1];   
    this.selectedstorage.length = 0;
    this.selectedstorage.push(choosen);
  } 
}