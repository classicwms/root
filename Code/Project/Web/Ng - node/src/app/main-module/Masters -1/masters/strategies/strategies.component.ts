import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
export interface  batch {


  no: string;
  x: string;
  altuom: string;
  y: string;
}
const ELEMENT_DATA:  batch[] = [
  { no: "1", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "2", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "3", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "4", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "5", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "6", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "7", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "8", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },
  { no: "9", x: 'dropdown', altuom: 'Enter', y: 'dropdown', },



];


@Component({
  selector: 'app-strategies',
  templateUrl: './strategies.component.html',
  styleUrls: ['./strategies.component.scss']
})
export class StrategiesComponent implements OnInit {

  constructor() { }

  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'x', 'altuom', 'y',];
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
  title1 = "Masters";
  title2 = "Product";
}
