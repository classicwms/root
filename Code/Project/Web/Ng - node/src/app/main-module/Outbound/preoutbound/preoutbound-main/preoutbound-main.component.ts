import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  preoutbound {


  no: string;
  actions:  string;
  status:  string;
   order:  string;
  orderedlines:  string;
  date:  string;
  outboundno:  string;
   refno:  string;
   required:  string;
   scode:  string;
   sname:  string;
}
const ELEMENT_DATA:  preoutbound[] = [
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },

];
@Component({
  selector: 'app-preoutbound-main',
  templateUrl: './preoutbound-main.component.html',
  styleUrls: ['./preoutbound-main.component.scss']
})
export class PreoutboundMainComponent implements OnInit {
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
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'actions', 'order','outboundno','refno','scode','sname', 'orderedlines','no', 'date', 'required', 'status',];
  dataSource = new MatTableDataSource< preoutbound>(ELEMENT_DATA);
  selection = new SelectionModel< preoutbound>(true, []);

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
  checkboxLabel(row?:  preoutbound): string {
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
