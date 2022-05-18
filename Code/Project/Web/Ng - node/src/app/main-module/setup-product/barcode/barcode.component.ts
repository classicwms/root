import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  variant {


  no: string;
  parameter:  string;
  from:  string;
  to:  string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1", parameter:  'Value', from: 'date', to: 'date' },
  { no: "2", parameter:  'Value', from: 'date', to: 'date' },
  { no: "3", parameter:  'Value', from: 'date', to: 'date' },
  { no: "4", parameter:  'Value', from: 'date', to: 'date' },
  { no: "5", parameter:  'Value', from: 'date', to: 'date' },
  { no: "6", parameter:  'Value', from: 'date', to: 'date' },
  { no: "7", parameter:  'Value', from: 'date', to: 'date' },
  { no: "8", parameter:  'Value', from: 'date', to: 'date' },

];

@Component({
  selector: 'app-barcode',
  templateUrl: './barcode.component.html',
  styleUrls: ['./barcode.component.scss']
})
export class BarcodeComponent implements OnInit {
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

  displayedColumns: string[] = ['select', 'no', 'parameter','from','to'];
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
