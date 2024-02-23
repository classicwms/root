import { Component, Inject, OnInit } from '@angular/core';
import { Validators, FormBuilder, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PricelistassignmentService } from '../pricelistassignment.service';

@Component({
  selector: 'app-pricelistassignment-new',
  templateUrl: './pricelistassignment-new.component.html',
  styleUrls: ['./pricelistassignment-new.component.scss']
})
export class PricelistassignmentNewComponent implements OnInit {

   
  disabled = false;
  step = 0;
 // dialogRef: any;

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
        companyCodeId: [],
        deletionIndicator: [],
        languageId: [],
        palletizationLevel: [,Validators.required],
        palletizationLevelId: [,Validators.required],
        palletizationLevelReference: [],
        plantId: [],
        referenceField1: [],
        referenceField10: [],
        referenceField2: [],
        referenceField3: [],
        referenceField4: [],
        referenceField5: [],
        referenceField6: [],
        referenceField7: [],
        referenceField8: [],
        referenceField9: [],
        warehouseId: [],
        updatedBy: [],
        updatedOn: [],
        createdBy: [],
        createdOn: [],
       
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
    private service: PricelistassignmentService
  ) { }
  ngOnInit(): void {
    if (this.data.pageflow != 'New') {
      // this.form.controls.palletizationLevelId.disable();
      // this.form.controls.palletizationLevel.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
     // this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  // fill() {
  //   this.spin.show();
  //   this.sub.add(this.service.Get(this.data.code,this.auth.warehouseId,this.auth.languageId,this.auth.plantId,this.auth.companyId,this.data.palletizationLevelReference,this.data.palletizationLevel).subscribe(res => {
  //     this.form.patchValue(res, { emitEvent: false });
  //   // this.form.controls.createdOn.patchValue(this.cs.dateapiutc0(this.form.controls.createdOn.value));
  //   // this.form.controls.updatedOn.patchValue(this.cs.dateapiutc0(this.form.controls.updatedOn.value));
  //     this.spin.hide();
  //   },
  //    err => {
  //   this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }
  //   ));
  // }
  // // submit(){
  //   this.submitted = true;
  //   if (this.form.invalid) {
  //     this.toastr.error(
  //       "Please fill required fields to continue",
  //       "Notification",{
  //         timeOut: 2000,
  //         progressBar: false,
  //       }
  //     );
  
  //     this.cs.notifyOther(true);
  //     return;
  //   }
    
  // this.cs.notifyOther(false);
  // this.spin.show();
  
  // if (this.data.code) {
  //   this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.auth.warehouseId,this.auth.languageId,this.auth.companyId,this.auth.plantId,this.data.palletizationLevelReference,this.data.palletizationLevel).subscribe(res => {
  //     this.toastr.success(this.data.code + " updated successfully!","Notification",{
  //       timeOut: 2000,
  //       progressBar: false,
  //     });
  //     this.spin.hide();
  //     this.dialogRef.close();
  
  //   }, err => {
  
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  
  //   }));
  // }else{
  //   this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
  //     this.toastr.success(res.code + " Saved Successfully!","Notification",{
  //       timeOut: 2000,
  //       progressBar: false,
  //     });
  //     this.spin.hide();
  //     this.dialogRef.close();
  
  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  
  //   }));
  // }
  
  //  }
   
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













 

