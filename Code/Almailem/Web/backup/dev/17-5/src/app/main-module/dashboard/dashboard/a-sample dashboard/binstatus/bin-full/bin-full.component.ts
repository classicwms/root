import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
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
  { no: "1",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14D02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "2",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14D01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "3",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14C03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "4",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14C02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "5",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14C01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "6",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14B03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "7",warehouseno:  'True Value-Amghara',by:  'Basement',countno:  'GB1BB14B02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "8",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BG14B01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "9",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BB14C03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "10",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BB14A02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "11",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BG09E03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "12",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BG09E02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "13",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BG09E01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "14",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BG09D03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "15",warehouseno:  'True Value-Amghara',by:  'Ground',countno:  'GB1BG09D02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "16",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09D01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "17",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09C03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "18",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09C02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "19",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09C01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "20",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09B03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "21",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09B02',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "22",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09B01',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },
  { no: "23",warehouseno:  'True Value-Amghara',by:  'Corner',countno:  'GB1BG09A03',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  'readonly',hold:  'readonly',available:  'readonly',preinboundno:  'OCCUPIED',  status: 'readonly' ,actions: 's' },


];
@Component({
  selector: 'app-bin-full',
  templateUrl: './bin-full.component.html',
  styleUrls: ['./bin-full.component.scss']
})
export class BinFullComponent implements OnInit {

  
  
  isShowDiv = false;
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
  displayedColumns: string[] = [ 'warehouseno','countno','by', 'preinboundno',];
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
    ngAfterViewInit() {
      this.dataSource.paginator = this.paginator;
    }
  
    dataSource = new MatTableDataSource< variant>(ELEMENT_DATA);
    selection = new SelectionModel< variant>(true, []);
  
   
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