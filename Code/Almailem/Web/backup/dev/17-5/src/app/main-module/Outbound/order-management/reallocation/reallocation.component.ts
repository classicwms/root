import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface ordermanagement {
  no: string;
  lineno: string;
  partner: string;
  product: string;
  description: string;
  refdocno: string;
  variant: string;
  order: string;
  type: string;
  preoutboundno: string;
  reallocated: string;
  inventory: string;
  allocated: string;
  status: string;
  
  }
  
  const ELEMENT_DATA: ordermanagement[] = [
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',inventory: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  
  ];
@Component({
  selector: 'app-reallocation',
  templateUrl: './reallocation.component.html',
  styleUrls: ['./reallocation.component.scss']
})
export class ReallocationComponent implements OnInit {

  title1 = "Outbound";
  title2 = "Reallocation";


  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no','refdocno','preoutboundno','type', 'lineno', 'partner', 'product',  'description', 'variant', 'order','allocated', 'inventory', 'reallocated',  'status',];
  dataSource = new MatTableDataSource< ordermanagement>(ELEMENT_DATA);
  selection = new SelectionModel< ordermanagement>(true, []);

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
  checkboxLabel(row?:  ordermanagement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }}