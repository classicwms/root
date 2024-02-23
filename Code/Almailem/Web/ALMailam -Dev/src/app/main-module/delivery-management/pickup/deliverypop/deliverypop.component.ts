import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';

import { UpdatePickerComponent } from 'src/app/main-module/Outbound/picking/picking-main/update-picker/update-picker.component';
import { DeliveryService } from '../../delivery.service';

@Component({
  selector: 'app-deliverypop',
  templateUrl: './deliverypop.component.html',
  styleUrls: ['./deliverypop.component.scss']
})
export class DeliverypopComponent implements OnInit {
  assignpicker: any[] = [];
  selectedassign : any[] = [];
  @ViewChild('assignppickerTag') assignppickerTag: Table | any;
  screenid: 1062 | undefined;
  constructor(private auth: AuthService, public dialogRef: MatDialogRef<UpdatePickerComponent>,
    private service: DeliveryService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  sub = new Subscription();
  ngOnInit(): void {

    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.plantId = [this.auth.plantId];
   obj.languageId = [this.auth.languageId];
   obj.warehouseId = [this.auth.warehouseId];
    this.sub.add(this.service.search1(obj).subscribe(res => {
      this.spin.hide();
      this.assignpicker = res;
      console.log(res);
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
    this.dialogRef.close(this.selectedassign[0]);
  }
  onChange() {
    const choosen= this.selectedassign[this.selectedassign.length - 1];   
    this.selectedassign.length = 0;
    this.selectedassign.push(choosen);
  }
}
