import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  variant {


  no: string;
  dimensions: string;
  width: string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },
  { no: "1", dimensions: 'Value',width: 'Enter',   },



];
@Component({
  selector: 'app-assignuser-popup',
  templateUrl: './assignuser-popup.component.html',
  styleUrls: ['./assignuser-popup.component.scss']
})
export class AssignuserPopupComponent implements OnInit {

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'dimensions', 'width',];
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
