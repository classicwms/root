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
import { PutawayService } from "../putaway.service";

@Component({
  selector: 'app-putaway-main',
  templateUrl: './putaway-main.component.html',
  styleUrls: ['./putaway-main.component.scss']
})
export class PutawayMainComponent implements OnInit {
  selectedStatusIdList: any[] = [];
  constructor(private service: PutawayService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) {
      this.selectedStatusIdList = [
        {value: 19, label: 'Putaway Created'},
        {value: 20, label: 'Putaway Confirmed'},
    ];
     }
  sub = new Subscription();
  ngOnInit(): void {
    this.search(true);
  }

  screenid: 1051 | undefined;

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

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }


  displayedColumns: string[] = ['select', 'statusId','itemCode', 'refDocNumber', 'putAwayNumber', 'packBarcodes', 'proposedStorageBin', 'proposedHandlingEquipment', 'putAwayQuantity', 'createdBy', 'createdOn',];
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


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Status ": this.cs.getstatus_text(x.statusId),
        'Order No': x.refDocNumber,
        "Puytaway No": x.putAwayNumber,
        "Pallet ID": x.packBarcodes,
        "Storage Bin": x.proposedStorageBin,
        "HE No": x.proposedHandlingEquipment,
        "To Qty": x.putAwayQuantity,
        "Created By": x.createdBy,
        'Created On': this.cs.dateExcel(x.createdOn),


        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Putaway");
  }


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
    statusId: [[19],],
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
      let result = res.filter((x: any) => x.statusId != 22);
      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let temppackBarcodesList: any[] = []
        const packBarcodes = [...new Set(result.map(item => item.packBarcodes))].filter(x => x != null)
        packBarcodes.forEach(x => temppackBarcodesList.push({ value: x, label: x }));
        this.packBarcodesList = temppackBarcodesList;

        let tempproposedHandlingEquipmentList: any[] = []
        const proposedHandlingEquipment = [...new Set(result.map(item => item.proposedHandlingEquipment))].filter(x => x != null)
        proposedHandlingEquipment.forEach(x => tempproposedHandlingEquipmentList.push({ value: x, label: x }));
        this.proposedHandlingEquipmentList = tempproposedHandlingEquipmentList;

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

        // let tempstatusIdList: any[] = []
        // const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
        // statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
        // this.statusIdList = tempstatusIdList;
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

  
  deleteDialog() {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "Norification");
      return;
    }
    if (this.selection.selected[0].statusId != 19) {
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
        this.deleterecord(this.selection.selected[0]);

      }
    });
  }
  deleterecord(obj: any) {
    this.spin.show();
    this.sub.add(this.service.deletePutawayHeader(obj).subscribe((ress) => {
      this.toastr.success(obj.putAwayNumber + " Deleted successfully.", "Notification",{
        timeOut: 2000,
        progressBar: false,
      });
     this.search(true);
     this.spin.hide(); 
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  reload() {
    this.searhform.reset();
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selection.selected.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }

    if (data != 'Display')
      if (![19, 21].includes(this.selection.selected[0].statusId)) {
        this.toastr.error("Order can't be edited as it's already proccessed.", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";

    if (this.selection.selected.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selection.selected[0], pageflow: data });
      this.router.navigate(['/main/inbound/putaway-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/putaway-create/' + paramdata]);
    }
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
