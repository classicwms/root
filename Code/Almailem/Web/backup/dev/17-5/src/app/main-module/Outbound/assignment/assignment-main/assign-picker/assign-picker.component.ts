import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
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

@Component({
  selector: 'app-assign-picker',
  templateUrl: './assign-picker.component.html',
  styleUrls: ['./assign-picker.component.scss']
})
export class AssignPickerComponent implements OnInit {
  screenid: 1062 | undefined;
  assignpicker: any[] = [];
  selectedassign : any[] = [];
  @ViewChild('assignppickerTag') assignppickerTag: Table | any;
  constructor(private auth: AuthService, public dialogRef: MatDialogRef<AssignPickerComponent>,
    private service: PickerService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();
  ngOnInit(): void {

    this.spin.show();
    this.sub.add(this.service.GetWarehousehht_login().subscribe(res => {
      this.spin.hide();
      this.assignpicker = res;
    
    }, err => {
      this.cs.commonerrorNew(err);;
      this.spin.hide();
    }));
  }

 

  


  confirm() {
    if (this.selectedassign.length === 0) {
      this.toastr.error("Kindly select one Row", "",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selectedassign.length > 1) {
      this.toastr.error("Kindly select one Row", "",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.dialogRef.close(this.selectedassign[0].userId);
  }
  onChange() {
    const choosen= this.selectedassign[this.selectedassign.length - 1];   
    this.selectedassign.length = 0;
    this.selectedassign.push(choosen);
  }
}
