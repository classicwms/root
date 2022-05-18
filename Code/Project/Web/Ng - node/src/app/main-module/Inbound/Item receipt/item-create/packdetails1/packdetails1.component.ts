import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { PackDetailsComponent } from "../pack-details/pack-details.component";


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
  selector: 'app-packdetails1',
  templateUrl: './packdetails1.component.html',
  styleUrls: ['./packdetails1.component.scss']
})
export class Packdetails1Component implements OnInit {
  constructor(public dialog: MatDialog) { }
  packdetails(): void {
  
      const dialogRef = this.dialog.open(PackDetailsComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '55%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });
    }
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
