import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { StoragePopupComponent } from "./storage-popup/storage-popup.component";

export interface selection {
  no: string;
  lineno: string;
  partner: string;
  product: string;
  description: string;
  refdocno: string;
  variant: string;
  type: string;
  level: string;
  
  }
  
  const ELEMENT_DATA: selection[] = [
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',level: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',type: 'readonly', },
  
  ];
@Component({
  selector: 'app-storage-selection',
  templateUrl: './storage-selection.component.html',
  styleUrls: ['./storage-selection.component.scss']
})
export class StorageSelectionComponent implements OnInit {

  constructor(private spinner: NgxSpinnerService,
    public dialog: MatDialog) {}


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

  selectionpara(): void {

    const dialogRef = this.dialog.open(StoragePopupComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
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

  displayedColumns: string[] = ['select','lineno', 'partner', 'product',  'description','refdocno', 'variant','type','level'];
  dataSource = new MatTableDataSource< selection>(ELEMENT_DATA);
  selection = new SelectionModel< selection>(true, []);

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
  checkboxLabel(row?:  selection): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }
}
