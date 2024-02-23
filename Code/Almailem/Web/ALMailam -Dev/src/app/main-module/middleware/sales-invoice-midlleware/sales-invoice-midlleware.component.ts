import { Component, OnInit, ViewChild } from '@angular/core';
import { MiddlewareService } from '../middleware.service';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-sales-invoice-midlleware',
  templateUrl: './sales-invoice-midlleware.component.html',
  styleUrls: ['./sales-invoice-midlleware.component.scss']
})
export class SalesInvoiceMidllewareComponent implements OnInit {

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
    branchCode: [[this.auth.plantId],],
    companyCode: [[this.auth.companyId],],
    customerId: [],
    fromOrderProcessedOn: [],
    fromOrderReceivedOn: [this.cs.todayapi(),],
    pickListNumber: [],
    processedStatusId: [],
    salesInvoiceId: [],
    salesInvoiceNumber: [],
    salesOrderNumber: [],
    toOrderProcessedOn: [],
    toOrderReceivedOn: [this.cs.todayapi(),],
  });

  search(ispageload = false) {
    this.spin.show();
    this.form.controls.fromOrderProcessedOn.patchValue(this.cs.day_callapi(this.form.controls.fromOrderProcessedOn.value));
    this.form.controls.toOrderProcessedOn.patchValue(this.cs.day_callapi(this.form.controls.toOrderProcessedOn.value));
    this.form.controls.fromOrderReceivedOn.patchValue(this.cs.day_callapi(this.form.controls.fromOrderReceivedOn.value));
    this.form.controls.toOrderReceivedOn.patchValue(this.cs.day_callapi(this.form.controls.toOrderReceivedOn.value));
    this.service.salesInvoiceSearch(this.form.value).subscribe(
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
  multisalesOrderNo:any[]=[];
  multisalesInvoiceNo:any[]=[];
  multipickuplistNo:any[]=[];
  multicustomer:any[]=[];
  getDropdown() {
    this.sub.add(this.service.salesInvoiceSearch({ companyCode:[this.auth.companyId],branchCode:[this.auth.plantId] }).subscribe(res => {
      res.forEach((x: any) => this.multisalesOrderNo.push({ value: x.salesOrderNumber, label: x.salesOrderNumber }));
      res.forEach((x: any) => this.multisalesInvoiceNo.push({ value: x.salesInvoiceNumber, label: x.salesInvoiceNumber }));
      res.forEach((x: any) => this.multipickuplistNo.push({ value: x.pickListNumber, label: x.pickListNumber }));
      res.forEach((x: any) => this.multicustomer.push({ value: x.customerId, label: x.customerName }));
    }))
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.salesInvoice.forEach((x) => {
      res.push({
        'Branch': x.branchCode,
        'Company': x.companyCode,
        'Status ': x.status,
        'Sales Order Number ': x.salesInvoiceNumber,
        'Sales Invoice Number': x.salesInvoiceNumber,
        'Pick List Number': x.pickListNumber,
        'Customer Id': x.customerId,
        'Customer Name': x.customerName,
        'Address': x.address,
        'Phone Number': x.phoneNumber,
        "Alternate No ": x.alternateNo,
        'Invoice  Date': this.cs.dateapiwithTime(x.invoiceDate),
        "Order Received On": this.cs.dateapiwithTime(x.orderReceivedOn),
        "Order Processed On": this.cs.dateapiwithTime(x.orderProcessedOn),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
    });
    this.cs.exportAsExcel(res, 'Sales Invoice Number');
  }
}

