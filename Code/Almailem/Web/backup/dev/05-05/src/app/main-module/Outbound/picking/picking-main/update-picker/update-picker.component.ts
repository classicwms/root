


  import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PickerService } from "../../../assignment/assignment-main/picker.service";
import { variant } from "../../../order-management/ordermanagement-main/bin-location/bin-location.component";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
@Component({
  selector: 'app-update-picker',
  templateUrl: './update-picker.component.html',
  styleUrls: ['./update-picker.component.scss']
})
export class UpdatePickerComponent implements OnInit {

  screenid: 1062 | undefined;
  constructor(private auth: AuthService, public dialogRef: MatDialogRef<UpdatePickerComponent>,
    private service: PickerService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();
  ngOnInit(): void {

    this.spin.show();
    this.sub.add(this.service.GetWarehousehht_login().subscribe(res => {
      this.spin.hide();
      this.dataSource = new MatTableDataSource<any>(res);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }, err => {
      this.cs.commonerror(err);
      this.spin.hide();
    }));
  }

  displayedColumns: string[] = ['select', 'no', 'dimensions',];
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

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: variant): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }


  confirm() {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select one Row", "",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selection.selected.length > 1) {
      this.toastr.error("Kindly select one Row", "",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.dialogRef.close(this.selection.selected[0].userId);
  }
}
