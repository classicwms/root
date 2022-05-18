import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AssignuserPopupComponent } from "./assignuser-popup/assignuser-popup.component";

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
  variance: string;
  by: string;
  date: string;
  status: string;
  remarks: string;
  }
  
  const ELEMENT_DATA: clientcategory[] = [
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly',description: 'readonly',bin: 'readonly',variant: 'readonly',batch: 'readonly',counted: 'dropdowm',pallet: 'readonly',stock: 'readonly',spl: 'readonly',inventory: 'readonly',by: 'readonly',date: 'readonly',pack: 'readonly',status: 'readonly', variance: 'readonly',remarks: 'readonly', },
  
  ];
@Component({
  selector: 'app-prepetual-confirm',
  templateUrl: './prepetual-confirm.component.html',
  styleUrls: ['./prepetual-confirm.component.scss']
})
export class PrepetualConfirmComponent implements OnInit {
  title1 = "Cycle count";
  title2 = "Prepetual Confirm";
  isShown: boolean = false ; // hidden by default
  toggleShow() {this.isShown = ! this.isShown;}
  animal: string | undefined;
  name: string | undefined;
  constructor(public dialog: MatDialog) { }
  assign(): void {
  
      const dialogRef = this.dialog.open(AssignuserPopupComponent, {
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
    ngOnInit(): void {
    }

    
  
    displayedColumns: string[] = ['select', 'no', 'product', 'description', 'sku',  'section',  'bin','pallet', 'pack', 'stock', 'spl', 'inventory',  ];

    //for confirm
    //displayedColumns: string[] = ['select', 'no', 'product', 'description', 'sku',  'section',  'bin','pallet', 'pack', 'stock', 'spl', 'inventory', 'counted', 'variance', 'by', 'date', 'status', 'remarks', 'actions'];
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
  
  