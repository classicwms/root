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
import { variant } from "../../../order-management/ordermanagement-main/bin-location/bin-location.component";
import { PickerService } from "../picker.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Table } from "primeng/table";
import { HhtuserService } from "src/app/main-module/userman/hhtuser/hhtuser.service";
import { MasterService } from "src/app/shared/master.service";
import { FormBuilder } from "@angular/forms";

@Component({
  selector: 'app-assign-picker',
  templateUrl: './assign-picker.component.html',
  styleUrls: ['./assign-picker.component.scss']
})
export class AssignPickerComponent implements OnInit {
  screenid = 3062;
  assignpicker: any[] = [];
  selectedassign: any[] = [];
  @ViewChild('assignppickerTag') assignppickerTag: Table | any;
  constructor(private auth: AuthService, public dialogRef: MatDialogRef<AssignPickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: HhtuserService,
    private fb: FormBuilder,
    public toastr: ToastrService,
    private masterService: MasterService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();
  levelList: any[] = [];
  form = this.fb.group({

    levelId: [],


  });
  ngOnInit(): void {
    console.log(this.data)
    this.spin.show();
    this.masterService.searchFloor({ companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId] }).subscribe(res => {
      this.levelList = [];
      res.forEach(element => {
        this.levelList.push({ value: element.floorId, label: element.floorId + '-' + element.description });
      });
    });
    // if (this.data && this.auth.warehouseId != 100) {
    //   //this.form.controls.levelId.patchValue(this.data.)
    //   this.sub.add(this.service.search({ companyCodeId: [this.auth.companyId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId], plantId: [this.auth.plantId], levelId: [this.data.levelID] }).subscribe(res => {
    //     this.spin.hide();
    //     this.assignpicker = res;

    //   }, err => {
    //     this.cs.commonerrorNew(err);;
    //     this.spin.hide();
    //   }));
    // } else {
    //   this.sub.add(this.service.search({ companyCodeId: [this.auth.companyId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId], plantId: [this.auth.plantId] }).subscribe(res => {
    //     this.spin.hide();
    //     this.assignpicker = res;
    //   }, err => {
    //     this.cs.commonerrorNew(err);;
    //     this.spin.hide();
    //   }));
    // }
    this.sub.add(this.service.search({ companyCodeId: [this.auth.companyId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId], plantId: [this.auth.plantId] }).subscribe(res => {
      this.spin.hide();
      this.assignpicker = res;
    }, err => {
      this.cs.commonerrorNew(err);;
      this.spin.hide();
    }));
  }
  onfloorChange(value) {
    this.service.search({ companyCodeId: [this.auth.companyId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId], plantId: [this.auth.plantId], levelId: [value.value] }).subscribe(res => {
      this.assignpicker = res;
    });
  }
  onclick() {
    this.service.search({ companyCodeId: [this.auth.companyId], languageId: [this.auth.languageId], warehouseId: [this.auth.warehouseId], plantId: [this.auth.plantId] }).subscribe(res => {
      this.assignpicker = res;
      this.form.controls.levelId.patchValue([]);
    })
  }

  applyFilterGlobal($event: any, stringVal: any) {
    this.assignppickerTag!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
    console.log(this.selectedassign)
    // if(this.selectedassign.length == 1){
    this.dialogRef.close(this.selectedassign[0].userId);
    //}
  }
  onChange() {
    const choosen = this.selectedassign[this.selectedassign.length - 1];
    this.selectedassign.length = 0;
    this.selectedassign.push(choosen);
  }
}
