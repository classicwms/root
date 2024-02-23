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

@Component({
  selector: 'app-reversal',
  templateUrl: './reversal.component.html',
  styleUrls: ['./reversal.component.scss']
})
export class ReversalComponent implements OnInit {
  screenid: 1055 | undefined;

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


  new(): void {

    const dialogRef = this.dialog.open(ReversalPopupComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.search();
    });
  }

  displayedColumns: string[] = ['select','statusId','inboundOrderTypeId', 'putAwayNumber', 'packBarcodes', 'proposedStorageBin', 'createdBy', 'createdOn',];
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

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  constructor(private service: PutawayService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();
  ngOnInit(): void {
    this.search(true);
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Status ": this.cs.getstatus_text(x.statusId),
        'Order Type': this.cs.getinboundorderType_text(x.inboundOrderTypeId),
        'Putaway No': x.refDocNumber,
        "Pallet ID": x.palletCode,
        "BinLocation": x.proposedStorageBin,
        "Created By": x.createdBy,
        "Created On": this.cs.dateExcel(x.createdOn),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Reversals");
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
    createdBy: [],
    endCreatedOn: [],
    packBarcodes: [],
    proposedHandlingEquipment: [],
    proposedStorageBin: [],
    putAwayNumber: [],
    refDocNumber: [],
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
 
  search(ispageload = false) {
    if (!ispageload) {
 
      //dateconvertion
  
  
  
      //patching
      // const packBarcodes = [...new Set(this.packBarcodesListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.packBarcodes.patchValue(packBarcodes);
  
      // const proposedHandlingEquipmentList = [...new Set(this.proposedHandlingEquipmentListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.proposedHandlingEquipmentList.patchValue(proposedHandlingEquipmentList);
  
      // const proposedStorageBin = [...new Set(this.proposedStorageBinListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.proposedStorageBin.patchValue(proposedStorageBin);
  
      // const putAwayNumber = [...new Set(this.putAwayNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.putAwayNumber.patchValue(putAwayNumber);
   
      // const refDocNumber = [...new Set(this.refDocNumberListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.refDocNumber.patchValue(refDocNumber);
  
      // const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.statusId.patchValue(statusId);
  }
  this.spin.show();

   this.service.search(this.searhform.value).subscribe(res => {
     // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
     let result = res.filter((x: any) => x.statusId == 22)
     this.spin.hide();
     if (ispageload) {
       let temppackBarcodesList: any[] = []
       const packBarcodes = [...new Set(result.map(item => item.packBarcodes))].filter(x => x != null)
       packBarcodes.forEach(x => temppackBarcodesList.push({ value: x, label: x }));
       this.packBarcodesList = temppackBarcodesList;
 
      //  let tempproposedHandlingEquipmentList: any[] = []
      //  const proposedHandlingEquipment = [...new Set(result.map(item => item.proposedHandlingEquipment))].filter(x => x != null)
      //  proposedHandlingEquipment.forEach(x => tempproposedHandlingEquipmentList.push({ id: x, itemName: x }));
      //  this.proposedHandlingEquipmentList = tempproposedHandlingEquipmentList;
 
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
 
       let tempstatusIdList: any[] = []
       const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
       statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
       this.statusIdList = tempstatusIdList;
     }
     this.dataSource = new MatTableDataSource<any>(result);
     this.selection = new SelectionModel<any>(true, []);
     this.dataSource.sort = this.sort;
     this.dataSource.paginator = this.paginator;
   }, err => {
 
     this.cs.commonerrorNew(err);
     this.spin.hide();
 
   });   

 
 }
 reload(){
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
}
