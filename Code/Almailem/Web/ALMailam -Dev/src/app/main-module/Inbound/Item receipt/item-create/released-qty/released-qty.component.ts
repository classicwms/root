import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { ItemReceiptService } from '../../item-receipt.service';
import { InventoryDetailsComponent } from '../inventory-details/inventory-details.component';

@Component({
  selector: 'app-released-qty',
  templateUrl: './released-qty.component.html',
  styleUrls: ['./released-qty.component.scss']
})
export class ReleasedQtyComponent implements OnInit {

  
  releasedQty: any[] = [];
  @ViewChild('releasedQtyTag') releasedQtyTag: Table | any;

  constructor(public dialog: MatDialog, public dialogRef: MatDialogRef<InventoryDetailsComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
  private spin: NgxSpinnerService, private service: ItemReceiptService, private cs: CommonService, public toastr: ToastrService,) { }
  sub = new Subscription();
  ngOnInit(): void {
    console.log(this.data)
    this.releasedQty = this.data;
  }
}
