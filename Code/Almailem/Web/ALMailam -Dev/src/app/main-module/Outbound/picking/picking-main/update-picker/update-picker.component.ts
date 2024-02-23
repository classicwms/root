


import { SelectionModel } from "@angular/cdk/collections";
import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PickerService } from "../../../assignment/assignment-main/picker.service";
import { variant } from "../../../order-management/ordermanagement-main/bin-location/bin-location.component";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Table } from "primeng/table";
@Component({
  selector: 'app-update-picker',
  templateUrl: './update-picker.component.html',
  styleUrls: ['./update-picker.component.scss']
})
export class UpdatePickerComponent implements OnInit {
  assignpicker: any[] = [];
  selectedassign: any[] = [];
  @ViewChild('assignppickerTag') assignppickerTag: Table | any;
  screenid: 1062 | undefined;
  constructor(private auth: AuthService, public dialogRef: MatDialogRef<UpdatePickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: PickerService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();
  ngOnInit(): void {
    // if (this.data && this.auth.warehouseId != 100) {
    //   this.spin.show();
    //   //this.form.controls.levelId.patchValue(this.data.)
    //   this.sub.add(this.service.search1({ companyCodeId: [this.auth.companyId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId], plantId: [this.auth.plantId], levelId: [this.data.levelID] }).subscribe(res => {
    //     this.spin.hide();
    //     this.assignpicker = res;

    //   }, err => {
    //     this.cs.commonerrorNew(err);;
    //     this.spin.hide();
    //   }));
    // } else {
    //   this.spin.show();
    //   let obj: any = {};
    //   obj.companyCodeId = [this.auth.companyId];
    //   obj.plantId = [this.auth.plantId];
    //   obj.languageId = [this.auth.languageId];
    //   obj.warehouseId = [this.auth.warehouseId];
    //   this.sub.add(this.service.search1(obj).subscribe(res => {
    //     this.spin.hide();
    //     this.assignpicker = res;
    //   }, err => {
    //     this.cs.commonerrorNew(err);;
    //     this.spin.hide();
    //   }));
    // }

    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.plantId = [this.auth.plantId];
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = [this.auth.warehouseId];
    this.sub.add(this.service.search1(obj).subscribe(res => {
      this.spin.hide();
      this.assignpicker = res;
    }, err => {
      this.cs.commonerrorNew(err);;
      this.spin.hide();
    }));
  }












  confirm() {
    if (this.selectedassign.length === 0) {
      this.toastr.error("Kindly select one Row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selectedassign.length > 1) {
      this.toastr.error("Kindly select one Row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.dialogRef.close(this.selectedassign[0].userId);
  }
  onChange() {
    const choosen = this.selectedassign[this.selectedassign.length - 1];
    this.selectedassign.length = 0;
    this.selectedassign.push(choosen);
  }
}
