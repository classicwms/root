import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface clientcategory {


  no: string;
  lineno: string;
  supcode: string;
  one: string;
  two: string;
  three: string;
  four: string;
  five: string;
  six: string;
}
  
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown', },

];
@Component({
  selector: 'app-warehouse-create',
  templateUrl: './warehouse-create.component.html',
  styleUrls: ['./warehouse-create.component.scss']
})
export class WarehouseCreateComponent implements OnInit {

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode', 'one', 'two', 'three', 'four','five','six',];
  dataSource = new MatTableDataSource<clientcategory>(ELEMENT_DATA);
  selection = new SelectionModel<clientcategory>(true, []);

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
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: clientcategory): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }



  
  disabled =false;
  step = 0;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  panelOpenState = false;
  constructor() { }


}

