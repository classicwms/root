








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
  
 
    warehouseno:  string;
    preinboundno:  string;
    countno:  string;
    by:  string;
  } 
  const ELEMENT_DATA:  variant[] = [
    { warehouseno:  'True Value-Amghara',by:  '19-05-2022',countno:  'BMOU5924040',preinboundno:  'ASN Received', },
    { warehouseno:  'True Value-Amghara',by:  '10-05-2022',countno:  'FFAU3944373',preinboundno:  'ASN Received', },
    { warehouseno:  'True Value-Amghara',by:  '07-05-2022',countno:  'PIFFANY',preinboundno:  'ASN Received', },
    { warehouseno:  'True Value-Amghara',by:  '06-05-2022',countno:  'TGBU2657284',preinboundno:  'ASN Received', },
  
  ];


  @Component({
    selector: 'app-received',
    templateUrl: './received.component.html',
    styleUrls: ['./received.component.scss']
  })
  export class ReceivedComponent implements OnInit {
  
    
    
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
    displayedColumns: string[] = ['countno','by', 'preinboundno',];
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