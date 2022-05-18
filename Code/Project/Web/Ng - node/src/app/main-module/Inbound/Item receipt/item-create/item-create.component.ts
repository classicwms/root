import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { CrossDockComponent } from "../../preinbound/goodreceipt-create/cross-dock/cross-dock.component";
import { AssignHEComponent } from "./assign-he/assign-he.component";
import { PackDetailsComponent } from "./pack-details/pack-details.component";
import { Packdetails1Component } from "./packdetails1/packdetails1.component";

export interface clientcategory {

  lineno: string;
  supcode: string;
  one: string;
  two: string;
  three: string;
  four: string;
  five: string;
  accepted: string;
  he:string;
  
  status:string;
  cases:string;
}
const ELEMENT_DATA: clientcategory[] = [
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },
{ lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly',he: 'readonly', },

];
@Component({
  selector: 'app-item-create',
  templateUrl: './item-create.component.html',
  styleUrls: ['./item-create.component.scss']
})
export class ItemCreateComponent implements OnInit {
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
  assignhe(): void {
  
      const dialogRef = this.dialog.open(AssignHEComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '40%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
    pallet(): void {
  
      const dialogRef = this.dialog.open(Packdetails1Component, {
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
  
      const dialogRef = this.dialog.open(PackDetailsComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '40%',
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
  
  ngOnInit(): void {
  }

  displayedColumns: string[] = ['select', 'lineno', 'supcode', 'one', 'two', 'three', 'accepted','four','five','cases','status','he'];
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

