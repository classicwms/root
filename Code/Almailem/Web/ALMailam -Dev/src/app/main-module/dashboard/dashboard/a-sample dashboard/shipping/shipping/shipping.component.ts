


  



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
  { no: "1",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '101-TVWarehouse',damage:  '1143',hold:  '1143',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055676'  },  
  { no: "2",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '101-TVWarehouse',damage:  '6281',hold:  '6281',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055753'  },  
  { no: "3",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '101-TVWarehouse',damage:  '4938',hold:  '4936',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055754'  },  
  { no: "4",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',damage:  '158',hold:  '158',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056249'  },  
  { no: "5",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',damage:  '138',hold:  '130',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056252'  },  
  { no: "6",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',damage:  '148',hold:  '142',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056254'  },  
  { no: "7",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',damage:  '112',hold:  '112',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056257'  },  
  { no: "8",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',damage:  '136',hold:  '136',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056259'  },  
  { no: "9",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '119',hold:  '119',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055445'  },  
  { no: "10",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '72',hold:  '71',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055799'  },  
  { no: "11",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '108',hold:  '102',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055820'  },  
  { no: "12",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '162',hold:  '157',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055931'  },  
  { no: "13",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '549',hold:  '427',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055959'  },  
  { no: "14",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '636',hold:  '551',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055960'  },  
  { no: "15",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '311',hold:  '11',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-055983'  },  
  { no: "16",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '272',hold:  '264',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056022'  },  
  { no: "17",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '486',hold:  '485',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056194'  },  
  { no: "18",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '82',hold:  '34',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056217'  },  
  { no: "19",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '219',hold:  '219',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056219'  },  
  { no: "20",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '199',hold:  '193',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056219'  },  
  { no: "21",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',damage:  '172',hold:  '170',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056219'  },  
  { no: "22",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '214',hold:  '204',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056225'  },  
  { no: "23",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '98',hold:  '96',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056226'  },  
  { no: "24",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '144',hold:  '140',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056228'  },  
  { no: "25",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '60',hold:  '60',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056229'  },  
  { no: "26",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '204',hold:  '194',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056238'  },  
  { no: "27",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '234',hold:  '234',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056239'  },  
  { no: "28",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '6',hold:  '6',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056241'  },  
  { no: "29",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '178',hold:  '178',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056111'  },  
  { no: "30",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '9',hold:  '9',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056222'  },  
  { no: "31",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '75',hold:  '74',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056233'  },  
  { no: "32",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',damage:  '116',hold:  '110',available:  'Delivery Confirmed',preinboundno:  'N-Normal',  status: 'TO-056243'  },  
 
];


@Component({
  selector: 'app-shipping',
  templateUrl: './shipping.component.html',
  styleUrls: ['./shipping.component.scss']
})
export class ShippingComponent implements OnInit {
  
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
