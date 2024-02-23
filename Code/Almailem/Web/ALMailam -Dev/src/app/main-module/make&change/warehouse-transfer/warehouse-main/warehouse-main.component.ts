import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  variant {


  no: string;
  actions:  string;
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  date:  string;
  refdocno:  string;
  type:  string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "2", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "3", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "4", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "5", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "6", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "7", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },
  { no: "8", warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly', date: 'readonly', status: 'readonly' ,actions: 's' },

];
@Component({
  selector: 'app-warehouse-main',
  templateUrl: './warehouse-main.component.html',
  styleUrls: ['./warehouse-main.component.scss']
})
export class WarehouseMainComponent implements OnInit {
 
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'warehouseno','type','refdocno', 'preinboundno', 'date', 'status', 'actions',];
  dataSource = new MatTableDataSource< variant>(ELEMENT_DATA);
  selection = new SelectionModel< variant>(true, []);

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
  checkboxLabel(row?:  variant): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
   clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }}
