import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";

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
  status: string;
  actions: string;
  sname: string;
  scode: string;
  reallocated: string;
  }
  
  const ELEMENT_DATA: ordermanagement[] = [
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  { no: "readonly", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',uom: 'dropdowm',req: 'readonly',allocated: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', actions: 'readonly',sname: 'readonly',scode: 'readonly',reallocated: 'readonly', },
  
  ];
@Component({
  selector: 'app-picking-main',
  templateUrl: './picking-main.component.html',
  styleUrls: ['./picking-main.component.scss']
})
export class PickingMainComponent implements OnInit {

  
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

  constructor(private spinner: NgxSpinnerService) {}
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

  displayedColumns: string[] = ['select','actions1','preoutboundno','scode','sname','refdocno','type', 'partner', 'product', 'order', 'description',  'req',  'reallocated','uom', 'allocated', 'status','actions','no',];
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
