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
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },
  { no: "Value", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' ,actions: 's' },

];

@Component({
  selector: 'app-goodsreceipt-main',
  templateUrl: './goodsreceipt-main.component.html',
  styleUrls: ['./goodsreceipt-main.component.scss']
})
export class GoodsreceiptMainComponent implements OnInit {
  ngOnInit(): void {
  }

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;  
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }


  displayedColumns: string[] = ['select', 'no','warehouseno','refdocno','type',  'preinboundno','date','status', ];
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
