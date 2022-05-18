import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface clientcategory {


  no: string;
  type: string;
  seq: string;
  one: string;
  two: string;
  three: string;
  four: string;
  five: string;
  six: string;
  seven: string;
  eight: string;
  nine: string;
  ten: string;
}
const ELEMENT_DATA: clientcategory[] = [
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },
  { no: "1", type: 'Value', seq: 'Enter', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown', },

];
@Component({
  selector: 'app-strategies',
  templateUrl: './strategies.component.html',
  styleUrls: ['./strategies.component.scss']
})
export class StrategiesComponent implements OnInit {
title1="Storage Setup";
title2="strategies";
 
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'type', 'seq', 'one', 'two', 'three', 'four','five','six','seven','eight','nine','ten',];
  dataSource = new MatTableDataSource<clientcategory>(ELEMENT_DATA);
  selection = new SelectionModel<clientcategory>(true, []);

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
  checkboxLabel(row?: clientcategory): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
}
