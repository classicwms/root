import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { DeliveryService } from '../../delivery.service';

@Component({
  selector: 'app-delivery-update',
  templateUrl: './delivery-update.component.html',
  styleUrls: ['./delivery-update.component.scss']
})
export class DeliveryUpdateComponent implements OnInit {
  form = this.fb.group({
    salesInvoiceNumber:[],
   });
  constructor(public dialogRef: MatDialogRef<DeliveryUpdateComponent>,private fb: FormBuilder, public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private service: DeliveryService,
    public cs: CommonService,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
    console.log(this.data);
    if(this.data.code[0].salesInvoiceNumber != null){
      this.form.controls.salesInvoiceNumber.patchValue(this.data.code[0].salesInvoiceNumber);
    }
  }
cancel(){
  this.dialogRef.close();
}
count:any;
sub = new Subscription();
  submitted = false;
submit(){
  console.log(this.form.controls.salesInvoiceNumber.value);
  this.data.code[0].salesInvoiceNumber=this.form.controls.salesInvoiceNumber.value;

    console.log(this.count);
  this.sub.add(this.service.Updateheader(this.data.code[0], this.data.code[0].preOutboundNo, this.auth.languageId,this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.data.code[0].refDocNumber,this.data.code[0].partnerCode).subscribe(res => {
    this.toastr.success(res.refDocNumber + " updated successfully!","Notification",{
      timeOut: 2000,
      progressBar: false,
    });
    this.spin.hide();
    this.dialogRef.close();

  }, err => {

    this.cs.commonerrorNew(err);
    this.spin.hide();

  }));
}
}
