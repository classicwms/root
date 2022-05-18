import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AssignPickerComponent } from "src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component";
import { BatchComponent } from "./batch/batch.component";
import { CrossDockComponent } from "./cross-dock/cross-dock.component";
import { PalletComponent } from "./pallet/pallet.component";
import { Pallet1Component } from "./pallet1/pallet1.component";
import { ReceiptUomComponent } from "./receipt-uom/receipt-uom.component";
import { VariantComponent } from "./variant/variant.component";

export interface clientcategory {

  lineno: string;
  supcode: string;
  one: string;
  two: string;
  three: string;
  four: string;
  five: string;
  six: string;
  seven: string;
  nine: string;
  ten: string;
  eleven:string;
  remarks:string;
  status:string;
  cases:string;
}
const ELEMENT_DATA: clientcategory[] = [
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',remarks: 'Enter',status: 'readonly', cases: 'readonly', },

];
@Component({
  selector: 'app-goodreceipt-create',
  templateUrl: './goodreceipt-create.component.html',
  styleUrls: ['./goodreceipt-create.component.scss']
})
export class GoodreceiptCreateComponent implements OnInit {
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
  
  isShown: boolean = false ; // hidden by default
toggleShow() {this.isShown = ! this.isShown;}
  animal: string | undefined;
  name: string | undefined;
  constructor(public dialog: MatDialog) { }
  assignhht(): void {
  
      const dialogRef = this.dialog.open(AssignPickerComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '55%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
    pallet(): void {
  
      const dialogRef = this.dialog.open(PalletComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '50%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
    pallet1(): void {
  
      const dialogRef = this.dialog.open(Pallet1Component, {
        disableClose: true,
        width: '100%',
        maxWidth: '40%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
    receiptuom(): void {
  
      const dialogRef = this.dialog.open(ReceiptUomComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '65%',
        position: { top: '24%',right: '0.5%' },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
    variant(): void {
  
      const dialogRef = this.dialog.open(VariantComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '65%',
        position: { top: '24%',right: '0.5%' },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }

    batch(): void {
  
      const dialogRef = this.dialog.open(BatchComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '65%',
        position: { top: '24%',right: '0.5%' },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select','cases', 'lineno', 'supcode', 'one', 'two', 'three', 'four','five','six','seven','nine','ten','eleven','remarks','status'];
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
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.lineno + 1}`;
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

