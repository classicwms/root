import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { UserprofileService } from '../userprofile.service';

@Component({
  selector: 'app-userprofile-new',
  templateUrl: './userprofile-new.component.html',
  styleUrls: ['./userprofile-new.component.scss']
})
export class UserprofileNewComponent implements OnInit {
 

  disabled = false;
  step = 0;

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
    companyCode: [],
    createdBy: [],
    createdOn: [],
    currencyDecimal: [],
    dateFormatId: [],
    deletionIndicator: [],
    emailId: [],
    firstName: [],
    languageId: [],
    lastName: [],
    plantId: [],
    statusId: [],
    timeZone: [],
    updatedBy: [],
    updatedOn: [],
    userId: [],
    userName: [],
    userRoleId: [],
    userTypeId: [],
    warehouseId: [],
    password:[],
  });
  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: UserprofileService,
  ) { }
  ngOnInit(): void {
    if (this.data.pageflow != 'New') {
      this.form.controls.userId.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
  }
}
sub = new Subscription();
submitted = false;


fill() {
  this.spin.show();
  this.sub.add(this.service.Get(this.data.code, this.auth.warehouseId).subscribe(res => {
    this.form.patchValue(res, { emitEvent: false });
  // this.form.controls.createdOn.patchValue(this.cs.dateapiutc0(this.form.controls.createdOn.value));
  // this.form.controls.updatedOn.patchValue(this.cs.dateapiutc0(this.form.controls.updatedOn.value));
    this.spin.hide();
  },
   err => {
  this.cs.commonerrorNew(err);
    this.spin.hide();
  }
  ));
}

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

this.form.controls.createdOn.patchValue(this.cs.day_callapi(this.form.controls.createdOn.value));
this.form.controls.updatedOn.patchValue(this.cs.day_callapi(this.form.controls.updatedOn.value));
if (this.data.code) {
this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code, this.auth.warehouseId).subscribe(res => {
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
  this.toastr.success(res.userId + " Saved Successfully!","Notification",{
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