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
  selector: 'app-interwarehousein',
  templateUrl: './interwarehousein.component.html',
  styleUrls: ['./interwarehousein.component.scss']
})
export class InterwarehouseinComponent implements OnInit {

  screenid = 3051;
  salesInvoice: any[] = [];
  selectedSalesInvoice: any[] = [];
  @ViewChild('salesInvoiceTag') salesInvoiceTag: Table | any;

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
    fromOrderProcessedOn: [],
    fromOrderReceivedOn: [this.cs.todayapi(),],
    fromTransferOrderDate: [],
    processedStatusId: [],
    sourceBranchCode: [[this.auth.plantId]],
    sourceCompanyCode: [[this.auth.companyId]],
    targetBranchCode: [],
    targetCompanyCode: [],
    toOrderProcessedOn: [],
    toOrderReceivedOn: [this.cs.todayapi(),],
    toTransferOrderDate: [],
    transferInHeaderId: [],
    transferOrderNo: [],
  });

  search(ispageload = false) {
    this.spin.show();
    //  this.form.controls.startCreatedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.startCreatedOn.value));
    //  this.form.controls.endCreatedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.endCreatedOn.value));
    // this.form.controls.fromOrderReceivedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.fromOrderReceivedOn.value));
    // this.form.controls.toOrderReceivedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.toOrderReceivedOn.value));
    this.service.interwarehousein(this.form.value).subscribe(
      (res) => {
        this.spin.hide();
        this.salesInvoice = res;
      },
      (err) => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }
      
    );
  }
  reload() {
    this.form.reset();
    this.form.controls.sourceBranchCode.patchValue([this.auth.plantId]);
    this.form.controls.sourceCompanyCode.patchValue([this.auth.companyId]);
    // this.form.controls.fromOrderReceivedOn.patchValue(this.cs.todayapi());
    // this.form.controls.toOrderReceivedOn.patchValue(this.cs.todayapi());
    this.sub.add(this.service.interwarehousein({ sourceCompanyCode:[this.auth.companyId],sourceBranchCode:[this.auth.plantId], }).subscribe(res => {
      res.forEach((x: any) => this.multisalesOrderNo.push({ value: x.transferOrderNo, label: x.transferOrderNo }));
    
    }))
  }
  multisalesOrderNo:any[]=[];
  multisalesInvoiceNo:any[]=[];
  multipickuplistNo:any[]=[];
  multicustomer:any[]=[];
  getDropdown() {
    this.sub.add(this.service.b2btransfer({ sourceCompanyCode:[this.auth.companyId],sourceBranchCode:[this.auth.plantId], toOrderReceivedOn: [this.cs.todayapi()], fromOrderReceivedOn: [this.cs.todayapi()],}).subscribe(res => {
      res.forEach((x: any) => this.multisalesOrderNo.push({ value: x.transferOrderNo, label: x.transferOrderNo }));
    
    }))
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.salesInvoice.forEach((x) => {
      res.push({
        'Branch': x.branchCode,
        'Company': x.companyCode,
        'Status ': x.processedStatusId,
        'Supplier Invoice Number ': x.supplierInvoiceNo,
        "Order Received On": this.cs.dateapiwithTime(x.orderReceivedOn),
        "Order Processed On": this.cs.dateapiwithTime(x.orderProcessedOn),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
    });
    this.cs.exportAsExcel(res, 'Supplier Invoice ');
  }
  openConfirm(data: any) {
    console.log(data);
    console.log(data.transferInLines);
  
   
    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/middleware/interwarehouseinlines/' + paramdata]);

  }
}


