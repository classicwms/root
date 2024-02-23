import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { CreatePopupComponent } from "../prepetual-count/prepetual-main/create-popup/create-popup.component";

export interface  variant {


  no: string;
  actions:  string;
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  refdocno:  string;
  type:  string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },

];
@Component({
  selector: 'app-variant-analysis',
  templateUrl: './variant-analysis.component.html',
  styleUrls: ['./variant-analysis.component.scss']
})
export class VariantAnalysisComponent implements OnInit {
  screenid: 1074 | undefined;
  constructor(public dialog: MatDialog,
    private spinner: NgxSpinnerService,) { }
  create(): void {
  
      const dialogRef = this.dialog.open(CreatePopupComponent, {
        disableClose: true,
        width: '80%',
        maxWidth: '50%',
        position: { top: '9%', },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
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

  displayedColumns: string[] = ['select','warehouseno','type','refdocno', 'preinboundno',  'no', 'status',];
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
