import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { GoodsReceiptService } from '../../Inbound/Goods-receipt/goods-receipt.service';
import { ItemReceiptService } from '../../Inbound/Item receipt/item-receipt.service';
import { PickerService } from '../../Outbound/assignment/assignment-main/picker.service';

@Component({
  selector: 'app-barcode-print',
  templateUrl: './barcode-print.component.html',
  styleUrls: ['./barcode-print.component.scss']
})
export class BarcodePrintComponent implements OnInit {

  constructor(private auth: AuthService, public dialogRef: MatDialogRef<BarcodePrintComponent>,
    private caseservice: GoodsReceiptService,
    private Itemservice: ItemReceiptService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();
  ngOnInit(): void {
  }
  Type: any;
  Barcode: any;
  submit = false;
  data: any[] = []
  Print() {
    this.submit = true;
    if (!this.Type || !this.Barcode) {
      return;
    }
    if (this.Type == 'Case') {
      this.sub.add(this.caseservice.searchLine({ caseCode: [this.Barcode] }).subscribe(res => {

        res.forEach((x: any) => {
          this.data.push({
            asn: x.refDocNumber, inv: x.invoiceNo, container_no: x.containerNo, case_no: x.caseCode, warehouseId: x.warehouseId

          });
        })



        sessionStorage.setItem(
          'barcode',
          JSON.stringify({
            BCfor: "case", list: [this.data[0]]
          })
        );
        window.open('#/barcode', '_blank');

        this.spin.hide();
        // this.dialogRef.close();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    } else {
      this.sub.add(this.Itemservice.searchline({ packBarcodes: [this.Barcode] }).subscribe(res => {
        res.forEach((x: any) => {
          this.data.push({
            packBarcodes: x.packBarcodes, Qty: x.quantityType == 'A' ? x.acceptedQty : x.damageQty, Type: x.quantityType == 'A' ? '' : 'D', warehouseId: x.warehouseId,
            nooflable: 1, quantityType: x.quantityType, itemCode: x.itemCode, invoiceNo: x.invoiceNo, itemDescription: x.itemDescription, manufacturerPartNo: x.manufacturerPartNo

          });
        })
        sessionStorage.setItem(
          'barcode',
          JSON.stringify({
            BCfor: "barcode", list: this.data
          })
        );
        window.open('#/barcode', '_blank');
        this.spin.hide();
        // this.dialogRef.close();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }

  }
}
