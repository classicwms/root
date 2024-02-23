

import { SelectionModel } from "@angular/cdk/collections";
import { HttpClient } from "@angular/common/http";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of, Subscription } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { MasterService } from "src/app/shared/master.service";
import { ContainerReceiptService } from "../../Inbound/Container-receipt/container-receipt.service";
import { PickingService } from "../../Outbound/picking/picking.service";
import { ReportsService } from "../reports.service";
import { stockElement, StocksampleService } from "../stocksamplereport/stocksample.service";
import { HhtPickupLinesComponent } from "./hht-pickup-lines/hht-pickup-lines.component";
import { Table } from "primeng/table";
import { HhtuserService } from "../../userman/hhtuser/hhtuser.service";

@Component({
  selector: 'app-hht-picking-report',
  templateUrl: './hht-picking-report.component.html',
  styleUrls: ['./hht-picking-report.component.scss']
})
export class HhtPickingReportComponent implements OnInit {
  screenid = 3172;
  hhtPicker: any[] = [];
  selectedinbound: any[] = [];
  @ViewChild('hhtPickupTag') hhtPickupTag: Table | any;

  form = this.fb.group({
    assignedPickerId: [],
    toDate: [],
    fromDate: [],
    statusId: [],
    warehouseId: [[this.auth.warehouseId]],
    companyCodeId: [[this.auth.companyId]],
    languageId: [[this.auth.languageId]],
    plantId: [[this.auth.plantId]],
  });

  isShowDiv = false;
  table = true;
  fullscreen = false;
  search = true;
  back = false;
  div1Function() {
    this.table = !this.table;
  }
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  toggleFloat() {

    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;

  }
  showFiller = false;

  displayedColumns: string[] = ['statusId', 'docLines', 'refDocNumber', 'inboundOrderTypeId', 'containerNo', 'createdOn', 'confirmedOn', 'refdocno', 'confirmedBy'];
  sub = new Subscription();




  constructor(
    private router: Router,
    private spin: NgxSpinnerService,
    public toastr: ToastrService,
    private fb: FormBuilder,
    public cs: CommonService,
    public dialog: MatDialog,
    private service: ReportsService,
    private hhtService: HhtuserService,
    public auth: AuthService,
  ) { }

  ngOnInit(): void {
    this.form.controls.companyCodeId.patchValue(this.auth.companyId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.plantId.disable();
    this.form.controls.companyCodeId.disable();
    this.dropdown()
  }

  userIdDropdown: any[] = [];

  dropdown() {
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    this.sub.add(this.service.getHhtreport(obj).subscribe(res => {
      res.pickerReportList.forEach((x: any) => this.userIdDropdown.push({ value: x.assignedPickerId, label: x.assignedPickerId }));

    }))
  }
  togglesearch() {
    this.search = false;
    this.table = true;
    this.fullscreen = false;
    this.back = true;
  }
  backsearch() {
    this.table = true;
    this.search = true;
    this.fullscreen = true;
    this.back = false;
  }

  submitted = false;
  filtersearch() {

    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill the required fields to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.form.controls.fromDate.patchValue(this.cs.day_callapi(this.form.controls.fromDate.value));
    this.form.controls.toDate.patchValue(this.cs.day_callapi(this.form.controls.toDate.value));
    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.fromDate = this.form.controls.fromDate.value;
    obj.toDate = this.form.controls.toDate.value;
    obj.assignedPickerId=this.form.controls.assignedPickerId.value;
    console.log(obj);
    this.sub.add(this.service.getHhtreport(obj).subscribe(res => {
      this.hhtPicker = res.pickerReportList;
      this.spin.hide()


      this.spin.hide();
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
  }


  reset() {
    this.form.reset();
    this.form.controls.companyCodeId.patchValue([this.auth.companyId])
    this.form.controls.languageId.patchValue([this.auth.languageId])
    this.form.controls.plantId.patchValue([this.auth.plantId])
    this.form.controls.warehouseId.patchValue([this.auth.warehouseId])
    this.form.controls.startCreatedOn.patchValue("");
    this.form.controls.endCreatedOn.patchValue("");
  }
  downloadexcel() {
    var res: any = [];
    this.hhtPicker.forEach(x => {
      res.push({
      "Picker":x.assignedPickerId,
        "Level ": x.level,
        "No Of Orders": x.pickUpHeaderCount,
      
      });

    })
    this.cs.exportAsExcel(res, "HHT Order Status");
  }

  lines(res) {
    console.log(res)
    const dialogRef = this.dialog.open(HhtPickupLinesComponent, {
      //width: '55%',
      maxWidth: '90%',
      position: { top: '8.5%' },
      data: res.pickupHeaderV2
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      // this.animal = result;
    });
  }

}

