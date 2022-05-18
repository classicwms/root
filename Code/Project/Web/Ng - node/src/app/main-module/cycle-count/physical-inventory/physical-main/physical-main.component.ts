import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { CreatePopupComponent } from "../../prepetual-count/prepetual-main/create-popup/create-popup.component";
import { AnnualCreateComponent } from "./annual-create/annual-create.component";

export interface  variant {


  no: string;
  actions:  string;
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  countno:  string;
  by:  string;
}
const ELEMENT_DATA:  variant[] = [
  { no: "1",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "2",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "3",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "4",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "5",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "6",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "7",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "8",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "9",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "10",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "11",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },

];
@Component({
  selector: 'app-physical-main',
  templateUrl: './physical-main.component.html',
  styleUrls: ['./physical-main.component.scss']
})
export class PhysicalMainComponent implements OnInit {

  ngOnInit(): void {
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
  constructor(public dialog: MatDialog,
    private spinner: NgxSpinnerService,) { }
  create(): void {
  
      const dialogRef = this.dialog.open(AnnualCreateComponent, {
        disableClose: true,
        width: '70%',
        maxWidth: '90%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });
    }

  displayedColumns: string[] = ['select', 'no', 'warehouseno','countno','by', 'preinboundno', 'status', 'actions',];
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
