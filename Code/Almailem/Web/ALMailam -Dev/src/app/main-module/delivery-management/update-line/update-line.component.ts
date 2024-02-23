import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { DeliveryService } from '../delivery.service';

@Component({
  selector: 'app-update-line',
  templateUrl: './update-line.component.html',
  styleUrls: ['./update-line.component.scss']
})
export class UpdateLineComponent implements OnInit {

  invoiceLine: any[] = [];
  selectedInvoiceLine: any[] = [];
  @ViewChild('lineTag') lineTag: Table | any;
  constructor(private auth: AuthService, public dialogRef: MatDialogRef<UpdateLineComponent>,
    private service: DeliveryService,
    public toastr: ToastrService, public deliverService: DeliveryService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.getAll()
  }

  refDocList: any[] = [];
  getAll() {
    this.refDocList = [];
    this.spin.show();
    let obj: any = {};
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.statusId = [59];
    this.sub.add(this.service.searchV2(obj).subscribe((res: any[]) => {
      res.forEach(x => this.refDocList.push({ value: x.refDocNumber, label: x.refDocNumber }));
      this.spin.hide();
    },err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    }))
  }
  invoice: any[] = [];
  selectedinvoice: any[] = [];
  getOutboundLines(value) {
    this.invoice = [];
    this.spin.show();
    this.deliverService.searchLinev2({ refDocNumber: [value], warehouseId: [this.auth.warehouseId], companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], languageId: [this.auth.languageId] }).subscribe(res => {
      this.invoiceLine = res;
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    })
  }

  confirm() {
    if (this.selectedInvoiceLine.length === 0) {
      this.toastr.error("Kindly select one Row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.dialogRef.close(this.selectedInvoiceLine);
  }

  showTable = false;
  onOrderChange(e){
    console.log(e)
    this.showTable = true;
    this.getOutboundLines(e.value);
  }
}
