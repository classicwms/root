import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { PutawayDetailsComponent } from "src/app/main-module/Inbound/Goods-receipt/putawaycreation-create/putaway-details/putaway-details.component";
import { Bintobin2Component } from "./bintobin2/bintobin2.component";
import { SkutoskuComponent } from "./skutosku/skutosku.component";
import { StockTypeComponent } from "./stock-type/stock-type.component";

export interface clientcategory {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  five: string;
}
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },

];
export interface sku {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  five: string;
}
const ELEMENT_DATA2: sku[] = [
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },

];
export interface bin {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  five: string;
}
const ELEMENT_DATA3: bin[] = [
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },
{ no: "1", lineno: 'readonly', supcode: 'readonly', one: 'Enter',       five: 'readonly', },

];
@Component({
  selector: 'app-inhouse-new',
  templateUrl: './inhouse-new.component.html',
  styleUrls: ['./inhouse-new.component.scss']
})
export class InhouseNewComponent implements OnInit {

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
    stock(): void {
    
        const dialogRef = this.dialog.open(StockTypeComponent, {
          disableClose: true,
          width: '100%',
          maxWidth: '90%',
        });
    
        dialogRef.afterClosed().subscribe(result => {
          console.log('The dialog was closed');
          this.animal = result;
        });
      }
      sku(): void {
    
        const dialogRef = this.dialog.open(SkutoskuComponent, {
          disableClose: true,
          width: '100%',
          maxWidth: '90%',
        });
    
        dialogRef.afterClosed().subscribe(result => {
          console.log('The dialog was closed');
          this.animal = result;
        });
      }
       bin(): void {
    
        const dialogRef = this.dialog.open(Bintobin2Component, {
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
  
    displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode','five', 'one', ];
    displayedColumns2: string[] = ['select', 'no', 'lineno', 'supcode','five', 'one', ];
    displayedColumns3: string[] = ['select', 'no', 'lineno', 'supcode','five', 'one', ];
    
    dataSource = new MatTableDataSource<clientcategory>(ELEMENT_DATA);
    selection = new SelectionModel<clientcategory>(true, []);

    dataSource2 = new MatTableDataSource<sku>(ELEMENT_DATA2);
    selection2 = new SelectionModel<sku>(true, []);

    dataSource3 = new MatTableDataSource<bin>(ELEMENT_DATA3);
    selection3 = new SelectionModel<bin>(true, []);
  
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
  
  