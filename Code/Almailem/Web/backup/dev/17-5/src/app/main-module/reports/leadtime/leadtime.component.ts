import { SelectionModel } from '@angular/cdk/collections';
import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { variant } from '../new-stock-movement/new-stock-movement.component';
import { ReportsService } from '../reports.service';
import { ResourceResultsComponent } from './resource-results/resource-results.component';

@Component({
  selector: 'app-leadtime',
  templateUrl: './leadtime.component.html',
  styleUrls: ['./leadtime.component.scss']
})
export class LeadtimeComponent implements OnInit {


  
    isShowDiv = false;
    table = false;
    fullscreen = false;
    search = true;
    back = false;
    div1Function() {
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
    sub = new Subscription();
    ELEMENT_DATA: variant[] = [];

    multiselectWarehouseList: any[] = [];
    multisalesassociateList: any[] = [];
    constructor(
      private router: Router,
      public toastr: ToastrService,
      private spin: NgxSpinnerService,
      public dialog: MatDialog,
      private fb: FormBuilder,
      private service: ReportsService,
      public cs: CommonService,
      public datepipe: DatePipe,
      ) { 

        this.multiselectWarehouseList = [
          {label: '110', value: '110'},
          {label: '111', value: '111'},
      ];

  this.multisalesassociateList = [
    {label: '40001', value: '40001'},
    {label: '40002', value: '40002'},
    {label: '40003', value: '40003'},
  ];
      }
    animal: string | undefined;
    RA: any = {};
    startDate: any;
    currentDate: Date;
    ngOnInit(): void {
      
  

     }


 
  

  
  
  
     filtersearch() {
      this.spin.show();
       this.table = true;
       this.search = false;
       this.fullscreen = true;
       this.back = true;
       this.spin.hide();
  
     }
    togglesearch() {
      this.search = false;
      this.table = true;
      this.fullscreen = false;
      this.back = true;
    }
    backsearch() {
      this.table = true;
      this.search = true;
      this.fullscreen = true;
      this.back = false;
    }
  
  
  
  
    results() {
      const dialogRef = this.dialog.open(ResourceResultsComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
      });
  
      dialogRef.afterClosed().subscribe(result => {});
  
    }
  
  
  }
  
  
  


