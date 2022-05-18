import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { PutawayDetailsComponent } from "./putaway-details/putaway-details.component";

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
  qty:string;
  to:string;
  confirmed:string;
  remaining:string;
}
const ELEMENT_DATA: clientcategory[] = [
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },
{ no: "Enter", lineno: 'readonly', supcode: 'readonly', one: 'Enter',  to: 'Enter',  confirmed: 'Enter',  remaining: 'Enter', two: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', six: 'readonly',seven: 'readonly',eight: 'Enter',nine: 'readonly',ten: 'dropdown',eleven: 'dropdown',twelve: 'dropdown',qty: 'dropdown', },

];
@Component({
  selector: 'app-putawaycreation-create',
  templateUrl: './putawaycreation-create.component.html',
  styleUrls: ['./putawaycreation-create.component.scss']
})
export class PutawaycreationCreateComponent implements OnInit {

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
    constructor(public dialog: MatDialog,
      private spinner: NgxSpinnerService,) { }
    subscreen(): void {
    
        const dialogRef = this.dialog.open(PutawayDetailsComponent, {
          disableClose: true,
          width: '100%',
          maxWidth: '90%',
        });
    
        dialogRef.afterClosed().subscribe(result => {
          console.log('The dialog was closed');
          this.animal = result;
        });
      }

    ngOnInit(): void {
          /** spinner starts on init */
    this.spinner.show();

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 500);
    }
  
    displayedColumns: string[] = ['select',  'supcode','no',  'to','one','confirmed','lineno'];
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
  
  