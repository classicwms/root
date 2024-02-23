import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { StockDetailsComponent } from "./stock-details/stock-details.component";

export interface clientcategory {


  no: string;
  lineno: string;
  supcode: string;
  one: string;
  
}
  
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },
{ no: "1", lineno: 'readonly', supcode: 'dropdown', one: 'dropdown', },

];
@Component({
  selector: 'app-sku-sku',
  templateUrl: './sku-sku.component.html',
  styleUrls: ['./sku-sku.component.scss']
})
export class SkuSkuComponent implements OnInit {
isShown: boolean = false ; // hidden by default
toggleShow() {this.isShown = ! this.isShown;}
animal: string | undefined;
name: string | undefined;
constructor(public dialog: MatDialog) { }
stockdetails(): void {

    const dialogRef = this.dialog.open(StockDetailsComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '83%',
      position: { top: '8.5%',right: '0.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode', 'one',];
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

