import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { AssignuserPopupComponent } from "../../prepetual-count/prepetual-confirm/assignuser-popup/assignuser-popup.component";
import { PhysicalInventoryService } from "../physical-inventory.service";
@Component({
  selector: 'app-physical-assign',
  templateUrl: './physical-assign.component.html',
  styleUrls: ['./physical-assign.component.scss']
})
export class PhysicalAssignComponent implements OnInit {

  title1 = "Cycle count";
  title2 = "Prepetual Confirm";
  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;

  data: any = {};

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  disabled = false;
  step = 0;
  panelOpenState = false;

  selectedUser: any = {};
  dataSource = new MatTableDataSource<any>();
  selection = new SelectionModel<any>(true, []);
  displayedColumns: string[] = ['select', 'no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stock', 'inventoryQuantity', 'countedQty', 'variance', 'countedBy', 'countedOn', 'statusId',];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;


  constructor(
    public dialog: MatDialog,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    public auth: AuthService,
    public router: Router,
    private route: ActivatedRoute,
    public periodicService: PhysicalInventoryService,
  ) { }

  ngOnInit(): void {
    this.data = this.cs.decrypt(this.route.snapshot.params.data);
    console.log(this.data)
    if(this.data.type == 'edit'){
      this.displayedColumns = ['select', 'no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stock', 'countedBy', 'countedOn', 'statusId'];
    }
    if (this.data && this.data.periodicHeaderData) {
      this.data.periodicHeaderData.periodicLine.forEach(element => {
        if (element.inventoryQuantity == null) {
          element.inventoryQuantity = 0;
        }
        if (element.statusId == 72) {
          element.countedQty = element.inventoryQuantity;
        }
      });
      this.dataSource = new MatTableDataSource(this.data.periodicHeaderData.periodicLine);
      this.dataSource.paginator = this.paginator;
      this.selection.select(...this.dataSource.data);
    }
  }

  assign(): void {
    const dialogRef = this.dialog.open(AssignuserPopupComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },
      data: this.selectedUser
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.selectedUser = result;
      }
    });
  }

  confirm() {
    if (this.data.type == 'edit') {
      if (!this.selectedUser.userId) {
        this.toastr.error(
          "Please assign a user to confirm",
          "Notification"
        );
        return;
      } else if (this.selection.selected.length == 0) {
        this.toastr.error(
          "Please select a line to confirm",
          "Notification"
        );
        return;
      } else {
        let saveArray: any[] = [];
        this.selection.selected.forEach(element => {
          let obj: any = {};
          obj.cycleCountNo = element.cycleCountNo;
          obj.cycleCounterId = this.selectedUser.userId;
          obj.cycleCounterName = this.selectedUser.userName;
          obj.itemCode = element.itemCode;
          obj.packBarcodes = element.packBarcodes;
          obj.storageBin = element.storageBin;
          obj.warehouseId = element.warehouseId;
          saveArray.push(obj);
        })
        this.spin.show();
        this.periodicService.confirmHHTUser(saveArray).subscribe(
          result => {
            this.spin.hide();
            this.toastr.success(
              "User Updated Successfully",
              "Notification"
            );
            this.router.navigate(['/main/cycle-count/physical-main'])
          },
          error => {
            this.spin.hide();
            this.toastr.error(
              "Error",
              "Notification"
            );
          }
        );
      }
    } else {
      if (this.selection.selected.length == 0) {
        this.toastr.error(
          "Please select a line to confirm",
          "Notification"
        );
        return;
      } else {
        let obj: any = {};
        obj = this.data.periodicHeaderData;
        obj.updatePeriodicLine = this.selection.selected;
        obj.periodicLine = [];
        this.spin.show();
        this.periodicService.updatePeriodicHeader(obj).subscribe(
          result => {
            this.spin.hide();
            this.toastr.success(
              "Count updated Successfully",
              "Notification"
            );
            this.router.navigate(['/main/cycle-count/physical-main'])
          },
          error => {
            this.spin.hide();
            this.toastr.error(
              "Error",
              "Notification"
            );
          }
        );
      }
    }
  }
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
    if (!this.selection.isSelected(row)) {
      this.selection.clear();
    }
    this.selection.toggle(row);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({
        "Product Code": x.itemCode,
        "Descrtiption ": x.itemDesc,
        "Mfr SKU": x.manufacturerPartNo,
        'Section': x.storageSectionId,
        'Bin Location': x.storageBin,
        'Pallet Code': x.packBarcodes,
        "Stock Type": x.stockTypeId,
        "Counted By": x.countedBy,
        "Counted Date": this.cs.dateapi(x.countedOn),
        "Status": this.cs.getstatus_text(x.statusId)
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Periodic Count Confirm");
  }


}

