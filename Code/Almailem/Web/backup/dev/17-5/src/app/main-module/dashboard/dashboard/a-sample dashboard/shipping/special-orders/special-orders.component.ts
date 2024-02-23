

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
  status:  string;
  warehouseno:  string;
  preinboundno:  string;
  countno:  string;
  by:  string;
  damage:  string;
  available:  string;
  hold:  string;
} 
const ELEMENT_DATA:  variant[] = [
  { no: "1",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '102-ShuwaikhShowroom',damage:  '83',hold:  '83',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056260'  },  
  { no: "2",warehouseno:  '26-05-2022',countno:  '04-05-2022',by:  '102-ShuwaikhShowroom',damage:  '127',hold:  '117',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056263'  },  
  { no: "3",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '103-AlRai',damage:  '85',hold:  '85',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056264'  },  
  { no: "4",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '103-AlRai',damage:  '96',hold:  '96',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056265'  },  
  { no: "5",warehouseno:  '26-05-2022',countno:  '02-05-2022',by:  '103-AlRai',damage:  '20',hold:  '20',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056266'  },  
  { no: "6",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '107-EgailaShowroom',damage:  '140',hold:  '140',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056269'  },  
  { no: "7",warehouseno:  '26-05-2022',countno:  '01-05-2022',by:  '107-EgailaShowroom',damage:  '206',hold:  '206',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056274'  },  
  { no: "8",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '108-Corner',damage:  '94',hold:  '94',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-056279'  },  
  { no: "9",warehouseno:  '26-05-2022',countno:  '02-05-2022',by:  '108-Corner',damage:  '3',hold:  '3',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-054937'  },  
  { no: "10",warehouseno:  '26-05-2022',countno:  '04-05-2022',by:  '108-Corner',damage:  '2',hold:  '2',available:  'Delivery Confirmed',preinboundno:  'S-Special',  status: 'TO-055665'  },  
  

];


@Component({
  selector: 'app-special-orders',
  templateUrl: './special-orders.component.html',
  styleUrls: ['./special-orders.component.scss']
})
export class SpecialOrdersComponent implements OnInit {
  
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
  displayedColumns: string[] = [ 'warehouseno','countno','by', 'preinboundno','status','damage','hold',];
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
  
    dataSource = new MatTableDataSource<variant>(ELEMENT_DATA);
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

