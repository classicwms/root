import { SelectionModel } from "@angular/cdk/collections";
import { Component, Inject, OnInit } from "@angular/core";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";


@Component({
  selector: 'app-packdetails1',
  templateUrl: './packdetails1.component.html',
  styleUrls: ['./packdetails1.component.scss']
})
export class Packdetails1Component implements OnInit {
  constructor(public dialog: MatDialog, public dialogRef: MatDialogRef<Packdetails1Component>, @Inject(MAT_DIALOG_DATA) public data: any,) { }
  packdetails(): void {
    sessionStorage.setItem(
      'barcode',
      JSON.stringify({ BCfor: "barcode", list: this.data })
    );
    window.open('#/barcode', '_blank');

    this.dialogRef.close(this.data);
    // const dialogRef = this.dialog.open(PackDetailsComponent, {
    //   disableClose: true,
    //   width: '100%',
    //   maxWidth: '55%',
    // });

    // dialogRef.afterClosed().subscribe(result => {
    //   console.log('The dialog was closed');
    // });
  }
  ngOnInit(): void {

    this.dataSource = new MatTableDataSource<any>(this.data);
    this.selection = new SelectionModel<any>(true, []);
  }

  displayedColumns: string[] = ['select', 'no', 'dimensions', 'width', 'type', 'uom'];
  dataSource = new MatTableDataSource<any>(this.data);
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
}
