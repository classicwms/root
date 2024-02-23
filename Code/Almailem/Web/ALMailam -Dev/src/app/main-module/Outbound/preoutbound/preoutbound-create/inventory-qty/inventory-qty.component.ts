import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  storagebin {


  no: string;
  warehouse: string;
  inventory: string;
}
const ELEMENT_DATA:  storagebin[] = [
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },
  { no: "1", warehouse: 'Value', inventory: 'Enter', },



];
@Component({
  selector: 'app-inventory-qty',
  templateUrl: './inventory-qty.component.html',
  styleUrls: ['./inventory-qty.component.scss']
})
export class InventoryQtyComponent implements OnInit {

  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'warehouse', 'inventory',];
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
