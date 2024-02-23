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
  selector: 'app-stockadjustment',
  templateUrl: './stockadjustment.component.html',
  styleUrls: ['./stockadjustment.component.scss']
})
export class StockadjustmentComponent implements OnInit {

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
    branchCode: [[this.auth.plantId]],
  companyCode: [[this.auth.companyId]],
  fromOrderProcessedOn: [],
  fromOrderReceivedOn: [this.cs.todayapi(),],
  itemCode: [],
  manufacturerCode: [],
  processedStatusId: [],
  stockAdjustmentId: [],
  toOrderProcessedOn: [],
  toOrderReceivedOn: [this.cs.todayapi(),],
  unitOfMeasure: [],
  });

  search(ispageload = false) {
    this.spin.show();
    this.form.controls.fromOrderProcessedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.fromOrderProcessedOn.value));
    this.form.controls.toOrderProcessedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.toOrderProcessedOn.value));
    this.form.controls.fromOrderReceivedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.fromOrderReceivedOn.value));
    this.form.controls.toOrderReceivedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.toOrderReceivedOn.value));
    this.service.stockAdjustment(this.form.value).subscribe(
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
    this.form.controls.branchCode.patchValue([this.auth.plantId]);
    this.form.controls.companyCode.patchValue([this.auth.companyId]);
    // this.form.controls.fromOrderReceivedOn.patchValue(this.cs.todayapi());
    // this.form.controls.toOrderReceivedOn.patchValue(this.cs.todayapi());
  }
  multipartNo:any[]=[];
  multiamso:any[]=[];
  multistatusNo:any[]=[];
  multimfr:any[]=[];
  getDropdown() {
    this.sub.add(this.service.stockAdjustment({ companyCode:[this.auth.companyId],branchCode:[this.auth.plantId] }).subscribe(res => {
      res.forEach((x: any) => this.multipartNo.push({ value: x.itemCode, label: x.itemDescription }));
      res.forEach((x: any) => this.multiamso.push({ value: x.amsReferenceNo, label: x.amsReferenceNo }));
      res.forEach((x: any) => this.multistatusNo.push({ value: x.statusId, label: x.statusDescription }));
      res.forEach((x: any) => this.multimfr.push({ value: x.manufacturerCode, label: x.manufacturerName }));
    }))
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.salesInvoice.forEach((x) => {
      res.push({
        'Branch': x.branchName,
        'Company': x.companyDescription,
        'Status ': x.statusDescription,
        'Mfr Name ': x.manufacturerName,
        'Part No': x.itemCode,
        'Description': x.itemDescription,
        'Origin': x.origin,
        'Stock Adjustment Id': x.stockAdjustmentId,
        'Before Adjustment': x.beforeAdjustment,
        'After Adjustment': x.afterAdjustment,
        "Adjustment Qty": x.adjustmentQty,
        'Inventory Qty': x.inventoryQuantity,
        "AMS Reference No": x.amsReferenceNo,
        "Date Of Adjustment": this.cs.dateapiwithTime(x.dateOfAdjustment),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
    });
    this.cs.exportAsExcel(res, 'Stock Adjustment');
  }
}


