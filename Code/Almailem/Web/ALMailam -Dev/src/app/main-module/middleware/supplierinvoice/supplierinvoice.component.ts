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
  selector: 'app-supplierinvoice',
  templateUrl: './supplierinvoice.component.html',
  styleUrls: ['./supplierinvoice.component.scss']
})
export class SupplierinvoiceComponent implements OnInit {


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
    companyCodeId: [[this.auth.companyId]],
    endConfirmedOn: [],
    endCreatedOn: [this.cs.todayapi(),],
    inboundOrderTypeId: [],
    languageId: [[this.auth.languageId]],
    newContainerNo: [],
    newPreInboundNo: [],
    newRefDocNumber: [],
    newStatusId: [],
    oldContainerNo: [],
    oldPreInboundNo: [],
    oldRefDocNumber: [],
    oldStatusId: [],
    plantId: [[this.auth.plantId]],
    purchaseOrderNumber: [],
    startConfirmedOn: [],
    startCreatedOn: [this.cs.todayapi(),],
    supplierInvoiceCancelHeaderId: [],
    warehouseId: [],
  });

  search(ispageload = false) {
    this.spin.show();
     this.form.controls.startCreatedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.startCreatedOn.value));
     this.form.controls.endCreatedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.endCreatedOn.value));
    // this.form.controls.fromOrderReceivedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.fromOrderReceivedOn.value));
    // this.form.controls.toOrderReceivedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.toOrderReceivedOn.value));
    this.service.supplierinvoice(this.form.value).subscribe(
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
    this.sub.add(this.service.supplierinvoice({ companyCode:[this.auth.companyId],branchCodeOfReceivingWarehouse:[this.auth.plantId] }).subscribe(res => {
      res.forEach((x: any) => this.multisalesOrderNo.push({ value: x.supplierInvoiceNo, label: x.supplierInvoiceNo }));
    
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
    console.log(data.supplierInvoiceLines);
    if(data.supplierInvoiceLines.length == 0){
      this.toastr.error("No Lines Present", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
   
    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/middleware/supplierinvoicelines/' + paramdata]);

  }
}
