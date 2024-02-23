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
  { no: "1",warehouseno:  '003192850',by:  'PA09001/PA09005',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '116',preinboundno:  'SOFA COVER ONE SEATER ASSORTED',  status: '116' ,actions: 's' },
  { no: "2",warehouseno:  '003193457',by:  'CH09234',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '708',preinboundno:  'DINNERWARE STORAGE 5PCS -GREY',  status: '708' ,actions: 's' },
  { no: "3",warehouseno:  '003194243',by:  'CH07164',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '161',preinboundno:  'CANNON CRUSHED FOAM PILLOW',  status: '161' ,actions: 's' },
  { no: "4",warehouseno:  '003194792',by:  'WE01018',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '748',preinboundno:  'CANNON PLN COTTON TOWEL SET 4PC 33*33 ASSORTED',  status: '748' ,actions: 's' },
  { no: "5",warehouseno:  '003195001',by:  'CH09234',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '924',preinboundno:  'DINNERWARE STORAGE 5PCS -GOLD',  status: '928' ,actions: 's' },
  { no: "6",warehouseno:  '003195002',by:  'CH09234/792343',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '476',preinboundno:  'DINNER WARE STORAGE 5PCS -CREAM',  status: '476' ,actions: 's' },
  { no: "7",warehouseno:  '0057510099',by:  '0',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '44',preinboundno:  'DANDY PLUS STORAGE BOX M W/LID 8.5 LTR',  status: '44' ,actions: 's' },
  { no: "8",warehouseno:  '0057510714',by:  '4228.10.11',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '400',preinboundno:  '10PC CHILDRENS HANGER SET -ELITE BIMBO4228.10',  status: '400' ,actions: 's' },
  { no: "9",warehouseno:  '0057510965',by:  '044447/044449',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '11',preinboundno:  'SALAD SPINNER -TROPICANA MARGHERITA044449/002595',  status: '11' ,actions: 's' },
  { no: "10",warehouseno:  '0057510966',by:  '55000',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '546',preinboundno:  'VINTAGE FRESH 3pc CONTAINER 0.5Ltr SQURE055000',  status: '546' ,actions: 's' },
  { no: "11",warehouseno:  '0057510967',by:  '55005',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '534',preinboundno:  'LIGHT CONTAINER 1Ltr 3pc SQUR055005/002878',  status: '531' ,actions: 's' },
  { no: "12",warehouseno:  '0057510968',by:  '55010',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '120',preinboundno:  'FRESH CONTAINER 1Ltr 3pc RECT055010',  status: '120' ,actions: 's' },
  { no: "12",warehouseno:  '0057510974',by:  '43408',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '14',preinboundno:  'BUTTER CONTAINER 0.5LTR043409',  status: '14' ,actions: 's' },
  { no: "12",warehouseno:  '0057510975',by:  '43420',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '16',preinboundno:  'BUTTER KEEPER YLW/AMBER - MARGHERITA043408',  status: '16' ,actions: 's' },
  { no: "12",warehouseno:  '0057511089',by:  '46.0471.0.24',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '80',preinboundno:  'DECORATED DUST PAN SET -SOFTY46.0471',  status: '88' ,actions: 's' },
  { no: "12",warehouseno:  '005751135',by:  '3109.2.013',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '1190',preinboundno:  '2pc MULTY HANGER SET3109.2',  status: '1190' ,actions: 's' },
  { no: "12",warehouseno:  '005751136',by:  '3141.10.005',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '4536',preinboundno:  '10pc HANGER SET-TWISTY3141.10',  status: '4536' ,actions: 's' },
  { no: "12",warehouseno:  '005751143',by:  '7240.2',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '166',preinboundno:  '2PC HANGER SET- FUTURA',  status: '166' ,actions: 's' },
  { no: "12",warehouseno:  '005751151',by:  '0315.1.022',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '2062',preinboundno:  'SHOE TREE GENTS PERFECT -0316180315',  status: '2062' ,actions: 's' },
  { no: "12",warehouseno:  '0057511525',by:  '10.7001.1.12',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '292',preinboundno:  'FOLDING BASKET 32 LTR W/CLRD BOTTOM AND BLK SIDES10.7001.1.12/PLS3305',  status: '291' ,actions: 's' },
  { no: "1",warehouseno:  '0057511527',by:  '021042/021040',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '524',preinboundno:  'QUICK ICE HEART SHAPE021042',  status: '524' ,actions: 's' },
  { no: "2",warehouseno:  '0057511529',by:  '020741/020755',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '171',preinboundno:  'SPONGE HOLDER -ORDINELLO020741/020740',  status: '171' ,actions: 's' },
  { no: "3",warehouseno:  '0057511535',by:  '077069/70',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '671',preinboundno:  'REMOTE CONTROL HOLDER077069/077070',  status: '636' ,actions: 's' },
  { no: "4",warehouseno:  '0057511916',by:  '077111/077107',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '742',preinboundno:  'WATER-MELON SAVER077111/077110',  status: '742' ,actions: 's' },
  { no: "5",warehouseno:  '0057511917',by:  '020570/020571',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '900',preinboundno:  'ICE MOULD 4PC.020571',  status: '888' ,actions: 's' },
  { no: "6",warehouseno:  '0057511918',by:  '044994/044987',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '27',preinboundno:  'MAXICLICK ICE CONTAINER 7LTR044994/044999',  status: '27' ,actions: 's' },
  { no: "7",warehouseno:  '0057511926',by:  '21270',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '630',preinboundno:  'BANANA SAVER021269/000180',  status: '116' ,actions: 's' },
  { no: "8",warehouseno:  '0057512260',by:  '10.3538.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '1',preinboundno:  'STORAGE BOX  SIMPLY ROLLER TRANSP BOX.W/LID&WHEEL ASST 44LTR10.3578.0.06',  status: '630' ,actions: 's' },
  { no: "9",warehouseno:  '0057512261',by:  '10.3539.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '2',preinboundno:  'STORAGE BOX  SIMPLY ROLLER TRANSP BOX.W/LID&WHEEL ASST 64LTR10.3579/3539.0.06',  status: '1' ,actions: 's' },
  { no: "10",warehouseno:  '0057512262',by:  '10.0705.0.24',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '403',preinboundno:  'DUST BIN W/SWING LID FLIP FLAP 1.7LTR10.0705.0.24 ',  status: '2' ,actions: 's' },
  { no: "11",warehouseno:  '0057512263',by:  '10.0706.0.12',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '456',preinboundno:  'DUST BIN W/SWING LID FLIP FLAP 5.5LTR10.0706.0.12',  status: '403' ,actions: 's' },
  { no: "12",warehouseno:  '0057512264',by:  '62.0206.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '27',preinboundno:  'DUST BIN W/SWING LID FLIP FLAP 21.5LTR10.0707.0.06',  status: '456' ,actions: 's' },
  { no: "12",warehouseno:  '0057512265',by:  '62.0207.0.04',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '1',preinboundno:  'DUST BIN W/SWING LID FLIP FLAP 34LTR10.0708.0.06',  status: '27' ,actions: 's' },
  { no: "12",warehouseno:  '0057512266',by:  '10.0138.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '57',preinboundno:  'STORAGE BIN W/LOCKING COVER TOMMY  21LTR10.0606.0.12',  status: '1' ,actions: 's' },
  { no: "12",warehouseno:  '0057512267',by:  '10.3115.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '109',preinboundno:  'STORAGE BIN W/REMOVABLE PAIL BINNY 6LTR62.0206.0.06 ',  status: '57' ,actions: 's' },
  { no: "12",warehouseno:  '0057512268',by:  '10.3108.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '17',preinboundno:  'STORAGE BIN W/REMOVABLE PAIL BINNY 16LTR62.0207.0.04',  status: '109' ,actions: 's' },
  { no: "12",warehouseno:  '0057512269',by:  '10.3110.0.06',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '25',preinboundno:  'FANCY BASKET 34X43.5X23CM10.0138.0.06',  status: '13' ,actions: 's' },
  { no: "12",warehouseno:  '0057512274',by:  '10.3007.0.08',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '1',preinboundno:  'STORAGE BOX W/LID&WHEEL 47.5LTR10.3115.0.06',  status: '25' ,actions: 's' },
  { no: "12",warehouseno:  '0057512276',by:  '10.3008.0.08',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '134',preinboundno:  'STORAGE ROLL BOX W/LOCKING LID&WHEEL 45LTR10.3108.0.06',  status: '1' ,actions: 's' },
  { no: "12",warehouseno:  '0057512278',by:  '10.3121.1.24',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '9',preinboundno:  'STORAGE ROLL BOX W/LOCKING LID&WHEEL 65LTR10.3110.0.06',  status: '9' ,actions: 's' },
  { no: "1",warehouseno:  '0057512279',by:  '10.3122.0.12',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '3',preinboundno:  'BED ROLLER W/LOCKING LIID&WHEEL 22.5LTR10.3007.0.08',  status: '1' ,actions: 's' },
  { no: "2",warehouseno:  '0057512280',by:  '10.3124.0.12',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '26',preinboundno:  'BED ROLLER W/LOCKING LIID&WHEEL 31LTR10.3008.0.08',  status: '8' ,actions: 's' },
  { no: "3",warehouseno:  '0057512281',by:  '10.3125.0.08',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '2',preinboundno:  'PLASTIC BOX W/LID TRANSPARENT 1.9LTR10.3121.0.24',  status: '2' ,actions: 's' },
  { no: "4",warehouseno:  '0057512282',by:  '10.3134.0.12',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '23',preinboundno:  'PLASTIC BOX W/LID TRANSPARENT 5.7LTR10.3122.0.12',  status: '9' ,actions: 's' },
  { no: "5",warehouseno:  '0057512283',by:  '6143.4.003',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '330',preinboundno:  'PLASTIC BOX W/LID TRANSPARENT 16.9LTR10.3124.0.12',  status: '330' ,actions: 's' },
  { no: "6",warehouseno:  '0057512285',by:  '6143.10.002',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '66',preinboundno:  'PLASTIC BOX W/LID ASSTD 34LTR10.3125.0.08',  status: '66' ,actions: 's' },
  { no: "7",warehouseno:  '0057512286',by:  '6142.10.003',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '78',preinboundno:  'PLASTIC BOX W/LID TRANSPARENT 34.5LTR10.3134.0.12',  status: '77' ,actions: 's' },
  { no: "8",warehouseno:  '0057512287',by:  '6142.10.003',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '23',preinboundno:  '4PC HANGER SET - MULTIMO SMART SPACELESS6143.4.003',  status: '9' ,actions: 's' },
  { no: "9",warehouseno:  '0057512418',by:  '6142.10.003',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '180',preinboundno:  '10PC HANGER SET -MULTIMO6143.10.002',  status: '180' ,actions: 's' },
  { no: "10",warehouseno:  '0057512419',by:  '6142.10.003',countno:  '3192850',stype:  'readonly',opening:  'readonly',balance:  'readonly',time:  'readonly',damage:  '0',hold:  '0',available:  '1992',preinboundno:  '10PC HANGER SET-HARMONY6142.10.003',  status: '1992' ,actions: 's' },

];

@Component({
  selector: 'app-sku-list',
  templateUrl: './sku-list.component.html',
  styleUrls: ['./sku-list.component.scss']
})
export class SkuListComponent implements OnInit {

  
  
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
  displayedColumns: string[] = ['countno','by', 'preinboundno', 'status','damage','hold','available',];
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