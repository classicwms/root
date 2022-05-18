import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface  storagebin {


  no: string;
 type:string;
 id:string;
 name:string;
}
const ELEMENT_DATA:  storagebin[] = [
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },
  { no: "1",type:'Value',id:'Enter',name:'Enter', },



];
@Component({
  selector: 'app-assign-user',
  templateUrl: './assign-user.component.html',
  styleUrls: ['./assign-user.component.scss']
})
export class AssignUserComponent implements OnInit {
 
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'type', 'id', 'name'];
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
