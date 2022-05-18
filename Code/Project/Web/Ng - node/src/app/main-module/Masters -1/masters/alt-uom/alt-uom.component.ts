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
  barcode: string;
}
const ELEMENT_DATA:  batch[] = [
  { no: "1", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "2", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "3", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "4", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "5", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "6", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "7", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "8", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },
  { no: "9", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },



];


@Component({
  selector: 'app-alt-uom',
  templateUrl: './alt-uom.component.html',
  styleUrls: ['./alt-uom.component.scss']
})


export class AltUomComponent implements OnInit {

  constructor() { }

  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'x', 'altuom', 'y', 'base','qpc20','qpc','barcode', ];
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

