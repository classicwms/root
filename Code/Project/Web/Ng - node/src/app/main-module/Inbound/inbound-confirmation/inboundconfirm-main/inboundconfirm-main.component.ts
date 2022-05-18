import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  variant {


  no: string;
  actions:  string;
  status:  string;
  warehouseno:  string;
  warehouseno1:  string;
  otype:  string;
  preinboundno:  string;
  date:  string;
  refdocno:  string;
  type:  string;
  pack:  string;
  pallet:  string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "2", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "3", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "4", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "5", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "6", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "7", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "8", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "9", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "10", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },
  { no: "11", warehouseno:  'Value',warehouseno1:  'Value',otype:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date',  pallet: 'date',  pack: 'date', status: 'date' ,actions: 's' },


];
@Component({
  selector: 'app-inboundconfirm-main',
  templateUrl: './inboundconfirm-main.component.html',
  styleUrls: ['./inboundconfirm-main.component.scss']
})
export class InboundconfirmMainComponent implements OnInit {
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

  
  displayedColumns: string[] = ['select',  'actions','warehouseno1','type','otype', 'warehouseno','pallet', 'date', 'pack','refdocno',  'preinboundno',  'status','receiptdetail'];
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
