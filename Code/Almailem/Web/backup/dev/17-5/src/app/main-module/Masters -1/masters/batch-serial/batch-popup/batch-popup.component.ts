import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";



export interface  storagebin {


  no: string;
  dimensions: string;
  length: string;
  width: string;
  height: string;
}
const ELEMENT_DATA:  storagebin[] = [
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', },



];


@Component({
  selector: 'app-batch-popup',
  templateUrl: './batch-popup.component.html',
  styleUrls: ['./batch-popup.component.scss']
})
export class BatchPopupComponent implements OnInit {

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'dimensions', 'length', 'width', 'height',];
  dataSource = new MatTableDataSource< storagebin>(ELEMENT_DATA);
  selection = new SelectionModel< storagebin>(true, []);

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
  checkboxLabel(row?:  storagebin): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
}
