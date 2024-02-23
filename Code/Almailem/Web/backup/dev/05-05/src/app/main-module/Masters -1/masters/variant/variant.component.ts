import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  batch {


  no: string;
  x: string;
  altuom: string;
  y: string;
  base: string;
  qpc20: string;
  qpc: string;
}
const ELEMENT_DATA:  batch[] = [
  { no: "1", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "2", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "3", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "4", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "5", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "6", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "7", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "8", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },
  { no: "9", x: 'AP_readonly', altuom: 'dropdown', y: 'Enter',base: 'Enter',qpc20: 'dropdown',qpc: 'Enter', },



];


@Component({
  selector: 'app-variant',
  templateUrl: './variant.component.html',
  styleUrls: ['./variant.component.scss']
})
export class VariantComponent implements OnInit {

  constructor() { }

  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'x', 'altuom', 'y', 'base','qpc20','qpc',];
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

