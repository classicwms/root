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
  seven: string;
  eight: string;
  nine: string;
  ten: string;
  twelve:string;
  eleven:string;
}
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'Enter', two: 'dropdown', three: 'dropdown', four: 'dropdown', five: 'dropdown', six: 'dropdown',seven: 'dropdown',eight: 'dropdown',nine: 'dropdown',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown', },

];
@Component({
  selector: 'app-containerreceipt-create',
  templateUrl: './containerreceipt-create.component.html',
  styleUrls: ['./containerreceipt-create.component.scss']
})
export class ContainerreceiptCreateComponent implements OnInit {

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

  displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode', 'one', 'two', 'three', 'four','five','six','seven','eight','nine','ten','eleven','twelve'];
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

