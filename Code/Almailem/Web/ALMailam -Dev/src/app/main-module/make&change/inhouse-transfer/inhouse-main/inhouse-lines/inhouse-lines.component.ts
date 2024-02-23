import { SelectionModel } from '@angular/cdk/collections';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ReportsService } from 'src/app/main-module/reports/reports.service';
import { InhouseTransferService } from '../../inhouse-transfer.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-inhouse-lines',
  templateUrl: './inhouse-lines.component.html',
  styleUrls: ['./inhouse-lines.component.scss']
})
export class InhouseLinesComponent implements OnInit {
  screenid=3058;
  inhouseline: any[] = [];
  selectedinhouse : any[] = [];
  @ViewChild('inhouselinesTag') inhouselinesTag: Table | any;
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }



 

  

  ngOnInit(): void {
    this.spin.show();
    this.service.getTransferReport({transferNumber: [this.data], warehouseId: [this.auth.warehouseId]}).subscribe(res => {
      this.inhouseline = res;
   
    })
    this.spin.hide();
  }
  constructor(public dialogRef: MatDialogRef<any>, 
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, @Inject(MAT_DIALOG_DATA) public data: any,
    private auth: AuthService,
    private cs: CommonService,
    private service: ReportsService,) { }
  sub = new Subscription();



 



  onChange() {
    const choosen= this.selectedinhouse[this.selectedinhouse.length - 1];   
    this.selectedinhouse.length = 0;
    this.selectedinhouse.push(choosen);
  }
 
}
