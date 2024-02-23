import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { CompanyService } from '../company.service';

@Component({
  selector: 'app-company-new',
  templateUrl: './company-new.component.html',
  styleUrls: ['./company-new.component.scss']
})
export class CompanyNewComponent implements OnInit {

  disabled = false;
  step = 0;
  //dialogRef: any;

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
    companyCodeId: [,Validators.required],
    createdBy: [],
    createdOn: [],
    createdOnFE: [],
    deletionIndicator: [],
    description: [],
    languageId: [,Validators.required],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE: [],
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
    private cas: CommonApiService,
    private service: CompanyService,
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
    //this.form.controls.languageId.patchValue(this.auth.languageId);
   
    if(this.auth.userTypeId != 1){
      this.dropdownlist();
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.languageId.disable();
    }else{
      this.dropdownlistSuperAdmin()
    }
    if (this.data.pageflow != 'New') {
      this.form.controls.companyCodeId.disable();
      this.form.controls.languageId.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
  }
  }
  languageidList: any[] = [];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
   
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  sub = new Subscription();
  submitted = false;
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.languageId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
      this.spin.hide();

    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
 
   this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
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

if (this.data.code) {
  this.sub.add(this.service.Update(this.form.getRawValue(),this.data.code,this.data.languageId).subscribe(res => {
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
    this.toastr.success(res.companyCodeId + " Saved Successfully!","Notification",{
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
 email = new FormControl('', [Validators.required, Validators.email]);
   public errorHandling = (control: string, error: string = "required") => {
     return this.form.controls[control].hasError(error) && this.submitted;
   }
   getErrorMessage() {
     if (this.email.hasError('required')) {
       return ' Field should not be blank';
     }
     return this.email.hasError('email') ? 'Not a valid email' : '';
   }
}

