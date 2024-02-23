import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ReversalOutboundService } from '../reversal-outbound.service';


interface SelectItem {
  item_id: number;
  item_text: string;
}


@Component({
  selector: 'app-reversal-outbound-popup',
  templateUrl: './reversal-outbound-popup.component.html',
  styleUrls: ['./reversal-outbound-popup.component.scss']
})
export class ReversalOutboundPopupComponent implements OnInit {


  constructor(private service: ReversalOutboundService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService, public dialogRef: MatDialogRef<ReversalOutboundService>,
    private fb: FormBuilder,
    private cs: CommonService,) { }
  sub = new Subscription();
  refDocNumber: any;
  packBarcodes: any;
  refDocNumberList: any[] = [];
  packBarcodesList: any[] = [];


  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  multipalletsinglelist: any[] = [];
  multipalletselectList: SelectItem[] = [];
  multiorderList: any[] = [];
  multiSelectorderList: any[] = [];


  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };


  ngOnInit(): void {

    this.spin.show();
    this.sub.add(this.service.searchsoLine({ warehouseId: [this.auth.warehouseId], statusId: [50, 57, 51] }).subscribe(res => {
      this.spin.hide();
      const key = 'refDocNumber';

      const arrayUniqueByKey = [...new Map(res.map((item: any) =>
        [item[key], item])).values()];

      this.refDocNumberList = arrayUniqueByKey;
      this.refDocNumberList.forEach(x => this.multiorderList.push({ value: x.refDocNumber, label: x.refDocNumber }))
      this.multiSelectorderList = this.multiorderList;
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    }));



  }
  getpalletid() {
    this.spin.show();
    this.sub.add(this.service.searchsoLine({ refDocNumber: [this.refDocNumber], statusId: [50, 57, 51] }).subscribe(res => {
      this.spin.hide();
      this.packBarcodesList = res;
      this.packBarcodesList.forEach(x => this.multipalletsinglelist.push({ value: x.itemCode, label: x.itemCode }))
      this.multipalletselectList = this.multipalletsinglelist;
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    }));
  }
  submitted = false;
  reverse() {
    this.submitted = true;
    if (!this.packBarcodes) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }

    if (!this.refDocNumber) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }
    this.spin.show();
    this.sub.add(this.service.reversal(this.packBarcodes, this.refDocNumber).subscribe(res => {
      this.spin.hide();
      this.toastr.success(this.refDocNumber + " Saved successfully!","",{
        timeOut: 2000,
        progressBar: false,
      });
      this.dialogRef.close();
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    }));
  }


  onItemSelect(item: any) {
    this.refDocNumber = this.selectedItems;
    this.getpalletid();
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }
}
