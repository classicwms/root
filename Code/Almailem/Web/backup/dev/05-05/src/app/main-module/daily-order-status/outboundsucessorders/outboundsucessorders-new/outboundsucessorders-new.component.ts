import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
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
import { OutboundsucessordersService } from '../outboundsucessorders.service';

@Component({
  selector: 'app-outboundsucessorders-new',
  templateUrl: './outboundsucessorders-new.component.html',
  styleUrls: ['./outboundsucessorders-new.component.scss']
})
export class OutboundsucessordersNewComponent implements OnInit {

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
    private service: OutboundsucessordersService
  ) { }
  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<any>(this.data.lines);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
 
  }
  sub = new Subscription();
  ELEMENT_DATA: variant[] = [];
  displayedColumns: string[] = [ 'id', 'orderId','orderedQty','lineReference',  'itemCode','itemText','refField1ForOrderType','uom'];

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













 


