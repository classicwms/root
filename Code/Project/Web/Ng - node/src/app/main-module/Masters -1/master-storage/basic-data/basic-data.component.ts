import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  batch {


  no: string;
  x: string;
}
const ELEMENT_DATA:  batch[] = [
  { no: "1", x: 'dropdown', },
  { no: "2", x: 'dropdown', },
  { no: "3", x: 'dropdown', },
  { no: "4", x: 'dropdown', },
  { no: "5", x: 'dropdown', },
  { no: "6", x: 'dropdown', },
  { no: "7", x: 'dropdown', },
  { no: "8", x: 'dropdown', },
  { no: "9", x: 'dropdown', },



];
@Component({
  selector: 'app-basic-data',
  templateUrl: './basic-data.component.html',
  styleUrls: ['./basic-data.component.scss']
})
export class BasicDataComponent implements OnInit {

  
  constructor() { }

  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'x',];
  dataSource = new MatTableDataSource< batch>(ELEMENT_DATA);
  selection = new SelectionModel< batch>(true, []);

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
  checkboxLabel(row?:  batch): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  title1 = "Masters-Storage";
  title2 = "Basic Data";
}
