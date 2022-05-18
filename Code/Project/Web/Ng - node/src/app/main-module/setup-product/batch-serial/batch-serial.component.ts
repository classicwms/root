import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  batch {


  no: string;
  values: string;
}
const ELEMENT_DATA:  batch[] = [
  { no: "1", values: 'Value' },
  { no: "2", values: 'Value' },
  { no: "3", values: 'Value' },
  { no: "4", values: 'Value' },
  { no: "5", values: 'Value' },
  { no: "6", values: 'Value' },
  { no: "7", values: 'Value' },
  { no: "8", values: 'Value' },



];

@Component({
  selector: 'app-batch-serial',
  templateUrl: './batch-serial.component.html',
  styleUrls: ['./batch-serial.component.scss']
})
export class BatchSerialComponent implements OnInit {
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

  displayedColumns: string[] = ['select', 'no', 'values'];
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
}
