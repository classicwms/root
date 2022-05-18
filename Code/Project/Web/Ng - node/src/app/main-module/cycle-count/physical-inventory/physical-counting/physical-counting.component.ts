import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

export interface clientcategory {
  no: string;
  sku: string;
  section: string;
  product: string;
  description: string;
  bin: string;
  variant: string;
  batch: string;
  counted: string;
  pallet: string;
  stock: string;
  spl: string;
  inventory: string;
  pack: string;
  status: string;
  asile: string;
  rack: string;
  }
  
  const ELEMENT_DATA: clientcategory[] = [
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',pack: 'readonly',asile: 'readonly',rack: 'readonly', status: 'readonly', },
  
  ];
@Component({
  selector: 'app-physical-counting',
  templateUrl: './physical-counting.component.html',
  styleUrls: ['./physical-counting.component.scss']
})
export class PhysicalCountingComponent implements OnInit {

  isShown: boolean = false ; // hidden by default
  toggleShow() {this.isShown = ! this.isShown;}
  animal: string | undefined;
  name: string | undefined;
  constructor() { }
  
  
    ngOnInit(): void {
    }
  
    displayedColumns: string[] = ['select', 'no', 'product', 'description', 'sku',  'section', 'asile', 'rack', 'bin', 'variant', 'batch', 'pallet', 'pack', 'stock', 'spl', 'inventory', 'counted', 'status'];
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
  
  