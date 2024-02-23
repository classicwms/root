import { SelectionModel } from '@angular/cdk/collections/public-api';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { Validators, FormBuilder, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { variant } from 'src/app/main-module/cycle-count/variant-analysis/variant-analysis.component';
import { DailyOrderStatusService } from '../../daily-order-status.service';

@Component({
  selector: 'app-successful-orders-new',
  templateUrl: './successful-orders-new.component.html',
  styleUrls: ['./successful-orders-new.component.scss']
})
export class SuccessfulOrdersNewComponent implements OnInit {

 
  disabled = false;
  step = 0;
  dataSource: any;
  selection: any;
 
 
  isShowDiv: boolean;
  toggle: boolean;
  icon: string;
  //dialogRef: any;
  @ViewChild(MatSort, {
    static: true
  })
  sort!: MatSort;
  @ViewChild(MatPaginator, {
    static: true
  })
  paginator!: MatPaginator; // Pagination
  // Pagination
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
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: DailyOrderStatusService
  ) { }
  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<any>(this.data.lines);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
 
  }
  sub = new Subscription();
  ELEMENT_DATA: variant[] = [];
  displayedColumns: string[] = ['containerNumber', 'id', 'orderId','orderedQty', 'manufacturerName', 'manufacturerPartNo','expectedDate',  'invoiceNumber', 'itemCode','itemText','itemCaseQty','salesOrderReference','supplierCode','supplierPartNumber','uom'];

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  
  showFloatingButtons: any;
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

}













 


