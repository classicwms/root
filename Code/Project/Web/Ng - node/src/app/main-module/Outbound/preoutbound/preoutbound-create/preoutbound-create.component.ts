import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { InventoryQtyComponent } from "./inventory-qty/inventory-qty.component";

export interface clientcategory {


  no: string;
  lineno: string;
  partner: string;
  product: string;
  description: string;
  mfrno: string;
  variant: string;
  order: string;
  uom: string;
  req: string;
  stock: string;
  spl: string;
  sname: string;
}
  
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },
{ no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter',description: 'readonly',mfrno: 'readonly',variant: 'readonly',order: 'enter',uom: 'dropdowm',req: 'enter',stock: 'enter',spl: 'enter', sname: 'enter', },

];
@Component({
  selector: 'app-preoutbound-create',
  templateUrl: './preoutbound-create.component.html',
  styleUrls: ['./preoutbound-create.component.scss']
})
export class PreoutboundCreateComponent implements OnInit {
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
animal: string | undefined;
name: string | undefined;
constructor(public dialog: MatDialog) { }
stockdetails(): void {

    const dialogRef = this.dialog.open(InventoryQtyComponent, {
      disableClose: true,
      width: '45%',
      maxWidth: '83%',
      position: {  },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'no', 'lineno', 'partner', 'sname','product',  'description',  'mfrno', 'variant', 'order', 'uom', 'req', 'stock', 'spl',];
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

