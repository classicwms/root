import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  storagebin {


  no: string;
  section: string;
  aisle: string;
  binno: string;
  palletcode: string;
  barcode: string;
  batch: string;
  qty: string;
  pick: string;
}
const ELEMENT_DATA:  storagebin[] = [
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },
  { no: "1", section: 'Value', aisle: 'Enter', binno: 'Enter', palletcode: 'Enter', barcode: 'dropdown', batch: 'dropdown', qty: 'dropdown', pick: 'dropdown' },



];
@Component({
  selector: 'app-proposed-locations',
  templateUrl: './proposed-locations.component.html',
  styleUrls: ['./proposed-locations.component.scss']
})
export class ProposedLocationsComponent implements OnInit {

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'section', 'aisle', 'binno', 'palletcode', 'barcode','batch','qty','pick'];
  dataSource = new MatTableDataSource< storagebin>(ELEMENT_DATA);
  selection = new SelectionModel< storagebin>(true, []);

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
  checkboxLabel(row?:  storagebin): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
}
