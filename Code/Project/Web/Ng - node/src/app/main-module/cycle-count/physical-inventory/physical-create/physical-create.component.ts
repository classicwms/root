import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AssignUserComponent } from "../../prepetual-count/prepetual-create/assign-user/assign-user.component";
import { AssignPhysicalComponent } from "./assign-physical/assign-physical.component";

export interface clientcategory {


  no: string;
  sku: string;
  section: string;
  product: string;
  description: string;
  bin: string;
  variant: string;
  batch: string;
  uom: string;
  pallet: string;
  stock: string;
  spl: string;
  inventory: string;
  pack: string;
  user: string;
}
  
const ELEMENT_DATA: clientcategory[] = [
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },
{ no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',uom: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly', user: 'readonly', },

];
@Component({
  selector: 'app-physical-create',
  templateUrl: './physical-create.component.html',
  styleUrls: ['./physical-create.component.scss']
})
export class PhysicalCreateComponent implements OnInit {

  
  isShown: boolean = false ; // hidden by default
toggleShow() {this.isShown = ! this.isShown;}
animal: string | undefined;
name: string | undefined;
constructor(public dialog: MatDialog) { }
create(): void {

    const dialogRef = this.dialog.open(AssignPhysicalComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '83%',
      position: { top: '8.5%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

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

  displayedColumns: string[] = ['select', 'batch', 'no', 'product', 'description', 'sku',  'section',  'bin',  'pallet', 'pack', 'stock', 'spl', 'inventory', 'uom','user',];
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

