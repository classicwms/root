import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { UserprofileService } from '../../userprofile/userprofile.service';
import { HhtuserService } from '../hhtuser.service';

@Component({
  selector: 'app-hhtuser-new',
  templateUrl: './hhtuser-new.component.html',
  styleUrls: ['./hhtuser-new.component.scss']
})
export class HhtuserNewComponent implements OnInit {

  disabled = false;
  step = 0;
  dialogRef: any;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }
  form = this.fb.group({
    languageId: [],
    companyCodeId: [],
    plantId: [],
    warehouseId: [],
    userId: [],
    password: [],
    userName: [],
    statusId: [],
    caseReceipts: [],
    itemReceipts: [],
    putaway: [],
    transfer: [],
    picking: [],
    quality: [],
    inventory: [],
    customerReturn: [],
    supplierReturn: [],
    referenceField1: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    referenceField10: [],
    deletionIndicator: [],
    createdBy: [],
    createdOn: [],
    updatedBy: [],
    updatedOn: [],
  });
  panelOpenState = false;
  constructor(
    //public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: HhtuserService ,
    
  ) { }
  ngOnInit(): void {
    if (this.data.pageflow != 'New') {
      this.form.controls.code.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
      //this.fill();
  }
}
sub = new Subscription();
submitted = false;

submit(){
this.submitted = true;
if (this.form.invalid) {
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

this.cs.notifyOther(false);
this.spin.show();

if (this.data.code) {
this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code).subscribe(res => {
  this.toastr.success(this.data.code + " updated successfully!","Notification",{
    timeOut: 2000,
    progressBar: false,
  });
  this.spin.hide();
  this.dialogRef.close();

}, err => {

  this.cs.commonerrorNew(err);
  this.spin.hide();

}));
}else{
this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
  this.toastr.success(res.code + " Saved Successfully!","Notification",{
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
}
