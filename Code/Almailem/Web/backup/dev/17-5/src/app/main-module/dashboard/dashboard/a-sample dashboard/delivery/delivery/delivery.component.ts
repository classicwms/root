



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
  stype:string;
  time:string;
} 
const ELEMENT_DATA:  variant[] = [
  { no: "1",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '101-TVWarehouse',stype:  '3',time:  '100',damage:  '1',hold:  '1',available:  '3',preinboundno:  'N-Normal',  status: 'TO-054937' ,},
  { no: "2",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '101-TVWarehouse',stype:  '119',time:  '100',damage:  '14',hold:  '14',available:  '119',preinboundno:  'N-Normal',  status: 'TO-055445' ,},
  { no: "3",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '101-TVWarehouse',stype:  '2',time:  '100',damage:  '1',hold:  '1',available:  '2',preinboundno:  'N-Normal',  status: 'TO-055665' ,},
 
  { no: "4",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',stype:  '450',time:  '100',damage:  '25',hold:  '25',available:  '450',preinboundno:  'N-Normal',  status: 'TO-055665' ,},
  { no: "5",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',stype:  '350',time:  '100',damage:  '24',hold:  '24',available:  '350',preinboundno:  'N-Normal',  status: 'TO-055665' ,},
  { no: "6",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',stype:  '425',time:  '100',damage:  '24',hold:  '23',available:  '425',preinboundno:  'N-Normal',  status: 'TO-055665' ,},
  { no: "7",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',stype:  '71',time:  '98.61',damage:  '19',hold:  '19',available:  '72',preinboundno:  'N-Normal',  status: 'TO-055799' ,},
  { no: "8",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '102-ShuwaikhShowroom',stype:  '3',time:  '94.44',damage:  '1',hold:  '1',available:  '3',preinboundno:  'N-Normal',  status: 'TO-055820' ,},
  { no: "9",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '102-ShuwaikhShowroom',stype:  '102',time:  '100',damage:  '14',hold:  '13',available:  '108',preinboundno:  'S-Special',  status: 'TO-056247' ,},
  { no: "10",warehouseno:  '26-05-2022',countno:  '04-05-2022',by:  '102-ShuwaikhShowroom',stype:  '112',time:  '100',damage:  '25',hold:  '25',available:  '112',preinboundno:  'S-Special',  status: 'TO-056248' ,},
 
  { no: "11",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '136',time:  '96.91',damage:  '33',hold:  '33',available:  '136',preinboundno:  'N-Normal',  status: 'TO-055931' ,},
  { no: "12",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '157',time:  '77.78',damage:  '23',hold:  '22',available:  '162',preinboundno:  'N-Normal',  status: 'TO-055959' ,},
  { no: "13",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '427',time:  '86.64',damage:  '23',hold:  '21',available:  '549',preinboundno:  'N-Normal',  status: 'TO-055960' ,},
  { no: "14",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '551',time:  '96.46',damage:  '21',hold:  '19',available:  '636',preinboundno:  'N-Normal',  status: 'TO-055983' ,},
  { no: "15",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '300',time:  '97.06',damage:  '4',hold:  '3',available:  '311',preinboundno:  'N-Normal',  status: 'TO-056022' ,},
  { no: "16",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '178  ',time:  '100',damage:  '18',hold:  '18',available:  '178',preinboundno:  'N-Normal',  status: 'TO-056022' ,},
  { no: "17",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '485',time:  '99.79',damage:  '25',hold:  '25',available:  '486',preinboundno:  'N-Normal',  status: 'TO-056194' ,},
  { no: "18",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '34',time:  '41.46',damage:  '3',hold:  '2',available:  '82',preinboundno:  'N-Normal',  status: 'TO-056217' ,},
  { no: "19",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '219',time:  '100',damage:  '32',hold:  '32 ',available:  '219',preinboundno:  'N-Normal',  status: 'TO-056219' ,},
  { no: "20",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '193',time:  '96.98',damage:  '39',hold:  '39',available:  '199',preinboundno:  'N-Normal',  status: 'TO-056220' ,},
  { no: "21",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '9',time:  '100',damage:  '2',hold:  '2',available:  '9',preinboundno:  'N-Normal',  status: 'TO-056222' ,},
  { no: "22",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '170',time:  '98.84',damage:  '36',hold:  '36',available:  '172',preinboundno:  'N-Normal',  status: 'TO-056223' ,},
  { no: "23",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '204',time:  '95.33',damage:  '36',hold:  '35',available:  '214',preinboundno:  'N-Normal',  status: 'TO-056225' ,},
  { no: "24",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '103-AlRai',stype:  '83',time:  '100',damage:  '17',hold:  '17',available:  '83',preinboundno:  'S-Special',  status: 'TO-056249' ,},
  { no: "25",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '103-AlRai',stype:  '117',time:  '92.13',damage:  '22',hold:  '22',available:  '127',preinboundno:  'S-Special',  status: 'TO-056250' ,},
  { no: "26",warehouseno:  '26-05-2022',countno:  '04-05-2022',by:  '103-AlRai',stype:  '85',time:  '100',damage:  '18',hold:  '18',available:  '85',preinboundno:  'S-Special',  status: 'TO-056251' ,},
 
  { no: "27",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '96',time:  '97.96',damage:  '29',hold:  '29',available:  '98',preinboundno:  'N-Normal',  status: 'TO-056226' ,},
  { no: "28",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '140',time:  '97.22',damage:  '29',hold:  '29',available:  '144',preinboundno:  'N-Normal',  status: 'TO-056228' ,},
  { no: "29",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '60',time:  '100',damage:  '14',hold:  '14',available:  '60',preinboundno:  'N-Normal',  status: 'TO-056229' ,},
  { no: "30",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '0',time:  '0.00',damage:  '24',hold:  '23',available:  '75',preinboundno:  'N-Normal',  status: 'TO-056233' ,},
  { no: "31",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '194',time:  '95.10',damage:  '19',hold:  '19',available:  '204',preinboundno:  'N-Normal',  status: 'TO-056238' ,},
  { no: "32",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '234',time:  '100',damage:  '9',hold:  '9',available:  '234',preinboundno:  'N-Normal',  status: 'TO-056239' ,},
  { no: "33",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '6',time:  '100',damage:  '1',hold:  '1',available:  '6',preinboundno:  'N-Normal',  status: 'TO-056241' ,},
  { no: "34",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '116',time:  '100',damage:  '27',hold:  '27',available:  '116',preinboundno:  'N-Normal',  status: 'TO-056243' ,},
  { no: "35",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '158',time:  '100',damage:  '31',hold:  '31',available:  '158',preinboundno:  'N-Normal',  status: 'TO-056244' ,},
  { no: "36",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '130',time:  '94.20',damage:  '29',hold:  '29',available:  '138',preinboundno:  'N-Normal',  status: 'TO-056245' ,},
  { no: "37",warehouseno:  '24-05-2022',countno:  '24-05-2022',by:  '107-EgailaShowroom',stype:  '142',time:  '95.95',damage:  '32',hold:  '32',available:  '148',preinboundno:  'N-Normal',  status: 'TO-056246' ,},
  { no: "38",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '107-EgailaShowroom',stype:  '96',time:  '100',damage:  '13',hold:  '13',available:  '96',preinboundno:  'S-Special',  status: 'TO-056252' ,},
  { no: "39",warehouseno:  '26-05-2022',countno:  '01-05-2022',by:  '107-EgailaShowroom',stype:  '20',time:  '100',damage:  '6',hold:  '6',available:  '20',preinboundno:  'S-Special',  status: 'TO-056266' ,},
 
  { no: "40",warehouseno:  '26-05-2022',countno:  '09-05-2022',by:  '108-Corner',stype:  '140',time:  '100',damage:  '26',hold:  '26',available:  '140',preinboundno:  'S-Special',  status: 'TO-056269' ,},
  { no: "41",warehouseno:  '26-05-2022',countno:  '02-05-2022',by:  '108-Corner',stype:  '206',time:  '100',damage:  '15',hold:  '15',available:  '206',preinboundno:  'S-Special',  status: 'TO-056274' ,},
  { no: "42",warehouseno:  '26-05-2022',countno:  '04-05-2022',by:  '108-Corner',stype:  '94',time:  '100',damage:  '16',hold:  '16',available:  '94',preinboundno:  'S-Special',  status: 'TO-056274' ,},

];

@Component({
  selector: 'app-delivery',
  templateUrl: './delivery.component.html',
  styleUrls: ['./delivery.component.scss']
})
export class DeliveryComponent implements OnInit {
  
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
  displayedColumns: string[] = [ 'warehouseno','countno','by', 'preinboundno', 'status','damage','hold','available','stype','time'];
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
