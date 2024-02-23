import { SelectionModel } from "@angular/cdk/collections";
import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";

export interface variant {


  no: string;
  dimensions: string;
  width: string;
}
const ELEMENT_DATA: variant[] = [
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },
  { no: "1", dimensions: 'Value', width: 'Enter', },



];
@Component({
  selector: 'app-palletdetails',
  templateUrl: './palletdetails.component.html',
  styleUrls: ['./palletdetails.component.scss']
})
export class PalletdetailsComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<PalletdetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }
  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<any>(this.data);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  displayedColumns: string[] = ['no', 'dimensions', 'width',];
  dataSource = new MatTableDataSource<variant>([]);
  selection = new SelectionModel<variant>(true, []);

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

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

}
