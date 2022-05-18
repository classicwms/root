import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  variant {


  no: string;
  dimensions: string;
  width: string;
  uom: string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },
  { no: "1", dimensions: 'Value',width: 'Enter', uom: 'dropdown',  },



];
@Component({
  selector: 'app-assign-picker',
  templateUrl: './assign-picker.component.html',
  styleUrls: ['./assign-picker.component.scss']
})
export class AssignPickerComponent implements OnInit {
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'dimensions', ];
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
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
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
