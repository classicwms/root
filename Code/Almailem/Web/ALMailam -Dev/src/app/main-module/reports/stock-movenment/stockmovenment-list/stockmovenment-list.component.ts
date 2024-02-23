import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  variant {


  no: string;
  actions:  string;
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  countno:  string;
  by:  string;
  damage:  string;
  available:  string;
  hold:  string;
  stype:string;
  balance:string;
  opening:string;
  time:string;
} 
const ELEMENT_DATA:  variant[] = [
  { no: "1",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "2",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "3",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "4",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "5",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "6",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "7",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "8",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "9",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "10",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "11",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },

];
@Component({
  selector: 'app-stockmovenment-list',
  templateUrl: './stockmovenment-list.component.html',
  styleUrls: ['./stockmovenment-list.component.scss']
})
export class StockmovenmentListComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  displayedColumns: string[] = [ 'warehouseno','countno','by', 'preinboundno', 'status','damage','hold','available','stype','time','balance','opening'];
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
  }

}
