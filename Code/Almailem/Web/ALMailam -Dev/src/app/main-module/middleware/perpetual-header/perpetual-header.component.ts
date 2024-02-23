import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MiddlewareService } from '../middleware.service';

@Component({
  selector: 'app-perpetual-header',
  templateUrl: './perpetual-header.component.html',
  styleUrls: ['./perpetual-header.component.scss']
})
export class PerpetualHeaderComponent implements OnInit {

  screenid = 3051;
  periodicHeader: any[] = [];
  selectedPeriodicHeader: any[] = [];
  @ViewChild('periodicHeaderTag') periodicHeaderTag: Table | any;

  selectedStatusIdList: any[] = [];
  constructor(
    private service: MiddlewareService,
    public toastr: ToastrService,
    public dialog: MatDialog,
    private spin: NgxSpinnerService,
    private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService
  ) { }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.search(true);
    this.getDropdown();
  }

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
      this.icon = 'expand_more';
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }

  form = this.fb.group({
    branchCode: [[this.auth.plantId],],
    companyCode: [[this.auth.companyId],],
    cycleCountNo: [],
    fromOrderProcessedOn: [],
    fromOrderReceivedOn: [this.cs.todayapi(),],
    periodicHeaderId: [],
    processedStatusId: [],
    toOrderProcessedOn: [],
    toOrderReceivedOn: [this.cs.todayapi(),],
  });

  search(ispageload = false) {
    this.spin.show();
    this.form.controls.fromOrderProcessedOn.patchValue(this.cs.day_callapi(this.form.controls.fromOrderProcessedOn.value));
    this.form.controls.toOrderProcessedOn.patchValue(this.cs.day_callapi(this.form.controls.toOrderProcessedOn.value));
    this.form.controls.fromOrderReceivedOn.patchValue(this.cs.day_callapi(this.form.controls.fromOrderReceivedOn.value));
    this.form.controls.toOrderReceivedOn.patchValue(this.cs.day_callapi(this.form.controls.toOrderReceivedOn.value));
    this.service.perpertualHeader(this.form.value).subscribe(
      (res) => {
        this.spin.hide();
        this.periodicHeader = res;
      },
      (err) => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }
    );
  }
  reload() {
    this.form.reset();
    this.form.controls.branchCode.patchValue([this.auth.plantId]);
    this.form.controls.companyCode.patchValue([this.auth.companyId]);
    // this.form.controls.fromOrderReceivedOn.patchValue(this.cs.todayapi());
    // this.form.controls.toOrderReceivedOn.patchValue(this.cs.todayapi());
    this.sub.add(this.service.perpertualHeader({ companyCode:[this.auth.companyId],branchCode:[this.auth.plantId] }).subscribe(res => {
      res.forEach((x: any) => this.multiCycleCountNo.push({ value: x.cycleCountNo, label: x.cycleCountNo }));
      res.forEach((x: any) => this.multiprocessedStatusId.push({ value: x.processedStatusId, label: x.processedStatusId }));  
    }))
  }
  multiCycleCountNo:any[]=[];
  multiprocessedStatusId:any[]=[];
  multipickuplistNo:any[]=[];
  multicustomer:any[]=[];
  getDropdown() {
    this.sub.add(this.service.perpertualHeader({ companyCode:[this.auth.companyId],branchCode:[this.auth.plantId] }).subscribe(res => {
      res.forEach((x: any) => this.multiCycleCountNo.push({ value: x.cycleCountNo, label: x.cycleCountNo }));
      res.forEach((x: any) => this.multiprocessedStatusId.push({ value: x.processedStatusId, label: x.processedStatusId }));  
    }))
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.periodicHeader.forEach((x) => {
      res.push({
        'Branch': x.branchCode,
        'Company': x.companyCode,
        'Status ': x.processedStatusId,
        'Cycle Count No': x.cycleCountNo,
        'Cycle Count Type ID': x.cycleCountTypeId,
        'Counted By': x.countedBy,
        'Counted On': this.cs.dateapiwithTime(x.countedOn)
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
    });
    this.cs.exportAsExcel(res, 'Perpetual Count');
  }
  openConfirm(data: any) {
    console.log(data);   
    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/middleware/perpetualHeaderlines/' + paramdata]);

  }
}


