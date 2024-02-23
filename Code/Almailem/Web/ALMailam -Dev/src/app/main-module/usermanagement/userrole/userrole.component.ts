import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  hhtuser {


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
const ELEMENT_DATA:  hhtuser[] = [
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
  selector: 'app-userrole',
  templateUrl: './userrole.component.html',
  styleUrls: ['./userrole.component.scss']
})
export class UserroleComponent implements OnInit {

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


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'order','outboundno','refno','scode','sname',];
  dataSource = new MatTableDataSource< hhtuser>(ELEMENT_DATA);
  selection = new SelectionModel< hhtuser>(true, []);

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
  checkboxLabel(row?:  hhtuser): string {
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
