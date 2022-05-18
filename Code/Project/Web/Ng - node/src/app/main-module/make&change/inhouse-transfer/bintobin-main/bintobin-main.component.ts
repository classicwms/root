import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ReceiptDetailsComponent } from "./receipt-details/receipt-details.component";

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
  receipt: string;  
  status: string;
}
  
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'readonly', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',  receipt: 'readonly', status: 'readonly', },

];
@Component({
  selector: 'app-bintobin-main',
  templateUrl: './bintobin-main.component.html',
  styleUrls: ['./bintobin-main.component.scss']
})
export class BintobinMainComponent implements OnInit {
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
  constructor(public dialog: MatDialog) { }
  receiptdetails(): void {
  
      const dialogRef = this.dialog.open(ReceiptDetailsComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '83%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });
    }
  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode', 'one','four','five','six','status','actions'];
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

}

