import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  storagebin {


  no: string;
  dimensions: string;
  length: string;
  width: string;
  height: string;
  uom: string;
  pack: string;
  available: string;
  transfer: string;
}
const ELEMENT_DATA:  storagebin[] = [
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown', pack: 'dropdown', available: 'dropdown', transfer: 'dropdown',},



];
@Component({
  selector: 'app-skutosku',
  templateUrl: './skutosku.component.html',
  styleUrls: ['./skutosku.component.scss']
})
export class SkutoskuComponent implements OnInit {

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'dimensions', 'length', 'width', 'height', 'uom','pack','available','transfer',];
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
