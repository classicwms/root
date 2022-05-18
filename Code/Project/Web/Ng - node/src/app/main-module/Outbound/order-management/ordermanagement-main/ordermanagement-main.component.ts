import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { AssignInvoiceComponent } from "src/app/main-module/Inbound/preinbound/preinbound-new/assign-invoice/assign-invoice.component";
import { BinLocationComponent } from "./bin-location/bin-location.component";
import { ReallocateComponent } from "./reallocate/reallocate.component";

export interface ordermanagement {
no: string;
lineno: string;
partner: string;
product: string;
description: string;
refdocno: string;
variant: string;
order: string;
type: string;
preoutboundno: string;
uom: string;
req: string;
allocated: string;
reallocated: string;
status: string;
sname: string;
sotype: string;
ref1: string;
ref2: string;
ref3: string;
}

const ELEMENT_DATA: ordermanagement[] = [
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
{ no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',sotype: 'readonly', sname: 'readonly',ref1: 'readonly',ref2: 'readonly',ref3: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',reallocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },

];
@Component({
  selector: 'app-ordermanagement-main',
  templateUrl: './ordermanagement-main.component.html',
  styleUrls: ['./ordermanagement-main.component.scss']
})
export class OrdermanagementMainComponent implements OnInit {
  
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

  constructor(private spinner: NgxSpinnerService , public dialog: MatDialog) {}
  reallocate(): void {
  
      const dialogRef = this.dialog.open(ReallocateComponent, {
        disableClose: true,
        width: '50%',
        maxWidth: '80%',
        position: { top: '14%', },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });
    }

    binlocation(): void {
  
      const dialogRef = this.dialog.open(BinLocationComponent, {
        disableClose: true,
        width: '50%',
        maxWidth: '80%',
        position: { top: '14%', },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });
    }
  title1 = "Outbound";
  title2 = "Order Management";
  ngOnInit(): void {
    /** spinner starts on init */
    this.spinner.show();

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 500);
 }

  displayedColumns: string[] = ['select','actions','refdocno','preoutboundno','type','sotype', 'partner','sname', 'req','product',  'description', 'allocated', 'reallocated',  'order','uom','ref1','ref2','ref3',  'binlocation','status',];
  dataSource = new MatTableDataSource< ordermanagement>(ELEMENT_DATA);
  selection = new SelectionModel< ordermanagement>(true, []);

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
  checkboxLabel(row?:  ordermanagement): string {
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
