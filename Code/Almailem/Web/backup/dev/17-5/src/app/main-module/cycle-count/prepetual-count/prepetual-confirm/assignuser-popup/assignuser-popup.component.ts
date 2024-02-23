import { SelectionModel } from "@angular/cdk/collections";
import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { MasterService } from "src/app/shared/master.service";
import { PhysicalInventoryService } from "../../../physical-inventory/physical-inventory.service";
@Component({
  selector: 'app-assignuser-popup',
  templateUrl: './assignuser-popup.component.html',
  styleUrls: ['./assignuser-popup.component.scss']
})
export class AssignuserPopupComponent implements OnInit {
  screenid: 1073 | undefined;

  displayedColumns: string[] = ['select', 'no', 'dimensions', 'width',];
  dataSource = new MatTableDataSource<any>();
  selection = new SelectionModel<any>(true, []);


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    public dialog: MatDialog,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    public auth: AuthService,
    public periodicService: PhysicalInventoryService,
    public toastr: ToastrService,
    public router: Router,
    public masterService: MasterService,
    public dialogRef: MatDialogRef<AssignuserPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any = {}
  ) { }


  ngOnInit(): void {
    this.getAllHHTUser();
  }

  getAllHHTUser() {
    this.spin.show();
    this.masterService.getAllHHTUser().subscribe(
      result => {
        this.spin.hide();
        this.dataSource = new MatTableDataSource(result);
        this.dataSource.paginator = this.paginator;
        if (this.data != null) {
          for (let i = 0; i < this.dataSource.data.length; i++) {
            if (this.dataSource.data[i].userId == this.data.userId) {
              this.selection.select(...[this.dataSource.data[i]]);
              break;
            }
          };
        }
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

  selectUser() {
    if (this.selection.selected.length == 0) {
      this.toastr.error(
        "Please select a user to assign",
        "Notification"
      );
      return;
    } else {
      this.data.userId = this.selection.selected[0].userId;
      this.data.userName = this.selection.selected[0].userName;
      this.dialogRef.close(this.data);
    }
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

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
}
