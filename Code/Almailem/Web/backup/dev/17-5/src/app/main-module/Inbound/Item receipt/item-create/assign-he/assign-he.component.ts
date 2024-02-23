import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { ItemReceiptService } from '../../item-receipt.service';

@Component({
  selector: 'app-assign-he',
  templateUrl: './assign-he.component.html',
  styleUrls: ['./assign-he.component.scss']
})
export class AssignHEComponent implements OnInit {
  screenid: 1048 | undefined;
  constructor(public dialogRef: MatDialogRef<AssignHEComponent>, private service: ItemReceiptService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  heno: any;
  ngOnInit(): void {
  }
  sub = new Subscription();
  submit() {


    this.spin.show();
    this.sub.add(this.service.getheVaidate(this.heno).subscribe(res => {
      this.dialogRef.close(this.heno);
      this.spin.hide();
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));




  }
}
