import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { ReceiptconfirmationListComponent } from './receiptconfirmation-list/receiptconfirmation-list.component';

export interface  variant {


  no: string;
  actions:  string;
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  countno:  string;
  by:  string;
  damage:  string;
  available:  string;
  hold:  string;
  stype:string;
  balance:string;
  opening:string;
  time:string;
} 
const ELEMENT_DATA:  variant[] = [
  { no: "1",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "2",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "3",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "4",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "5",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "6",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "7",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "8",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "9",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "10",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "11",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'readonly',by:  'readonly',countno:  'readonly',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },

];
@Component({
  selector: 'app-receipt-confirmation',
  templateUrl: './receipt-confirmation.component.html',
  styleUrls: ['./receipt-confirmation.component.scss']
})
export class ReceiptConfirmationComponent implements OnInit {

 
  
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;
  div1Function(){
    this.table = !this.table;
  }
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  toggleFloat() {

    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;

  }
  showFiller = false;
  displayedColumns: string[] = [ 'warehouseno','countno','by', 'preinboundno', 'status','damage','hold','available','stype','time','balance','opening'];
  sub = new Subscription();
    ELEMENT_DATA: variant[] = [];
    constructor(
      private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService, ) { }
      routeto(url: any, id: any) {
        sessionStorage.setItem('crrentmenu', id);
        this.router.navigate([url]);
      }
    animal: string | undefined;
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
  
      this.dataSource.filter = filterValue.trim().toLowerCase();
  
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }
    ngOnInit(): void {
     // this.auth.isuserdata();
  
    }
  
    dataSource = new MatTableDataSource<variant>(this.ELEMENT_DATA);
    selection = new SelectionModel<variant>(true, []);
  
   
    ngOnDestroy() {
      if (this.sub != null) {
        this.sub.unsubscribe();
      }
  
    }
    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator!: MatPaginator; // Pagination
   // Pagination


   


  filtersearch() {
    this.spin.show();
        this.spin.hide();
        this.table = true;
        this.search = true;
        this.fullscreen = true;
        this.back = false;
     
   
  }
  togglesearch(){
    this.search = false;
    this.table = true;
    this.fullscreen = false;
    this.back = true;
  }
  backsearch(){
    this.table = true;
    this.search = true;
    this.fullscreen = true;
    this.back = false;
  }

  




  
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
    checkboxLabel(row?: variant): string {
      if (!row) {
        return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
      }
      return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseno + 1}`;
    }
  
  
  
    clearselection(row: any) {
  
      this.selection.clear();
      this.selection.toggle(row);
    }
  }