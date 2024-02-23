import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ProposedLocationsComponent } from "./proposed-locations/proposed-locations.component";

export interface pickuplocation {
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
  reallocated: string;
  picker: string;
  proposedheno: string;
  status: string;
  
  }
  
  const ELEMENT_DATA: pickuplocation[] = [
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',order: 'readonly',reallocated: 'dropdowm',picker: 'readonly',proposedheno: 'readonly',status: 'readonly',preoutboundno: 'readonly',type: 'readonly', },
  
  ];
@Component({
  selector: 'app-pickup-creation',
  templateUrl: './pickup-creation.component.html',
  styleUrls: ['./pickup-creation.component.scss']
})
export class PickupCreationComponent implements OnInit {

  title1 = "Outbound";
  title2 = "Pickup Creation";

  isShown: boolean = false ; // hidden by default
  toggleShow() {this.isShown = ! this.isShown;}
  animal: string | undefined;
  name: string | undefined;
  constructor(public dialog: MatDialog) { }
  stockdetails(): void {
  
      const dialogRef = this.dialog.open(ProposedLocationsComponent, {
        disableClose: true,
        width: '100%',
        maxWidth: '83%',
        position: { top: '8.5%',right: '0.5%' },
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.animal = result;
      });
    }
  
    ngOnInit(): void {
    }
  
    displayedColumns: string[] = ['select', 'no','refdocno','preoutboundno','type', 'lineno', 'partner', 'product',  'description', 'variant', 'order','reallocated', 'picker', 'proposedheno',  'status',];
    dataSource = new MatTableDataSource<pickuplocation>(ELEMENT_DATA);
    selection = new SelectionModel<pickuplocation>(true, []);
  
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
    checkboxLabel(row?: pickuplocation): string {
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
  
    clearselection(row: any) {
      this.selection.clear();
      this.selection.toggle(row);
    }}
  
  