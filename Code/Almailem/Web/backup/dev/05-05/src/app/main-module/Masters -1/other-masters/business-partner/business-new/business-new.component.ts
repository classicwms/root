import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BusinesspartnerElement, BusinessPartnerService } from '../business-partner.service';

@Component({
  selector: 'app-business-new',
  templateUrl: './business-new.component.html',
  styleUrls: ['./business-new.component.scss']
})
export class BusinessNewComponent implements OnInit {
  
  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  screenid: 1039 | undefined;
  sub = new Subscription();
  //creation of Form
  form = this.fb.group({
    address1: [],
    address2: [],
    businessPartnerType: [,Validators.required],
    companyCodeId: ['1000'],
    country: [],
    createdBy: [this.auth.username],
    createdon: [],
   // deletionIndicator: [],
    emailId: [],
    faxNumber: [],
    languageId: ['EN'],
    lattitude: [],
    location: [],
    longitude: [],
    partnerCode: [,Validators.required],
    partnerName: [,Validators.required],
    phoneNumber: [],
    plantId: ['1001'],
    // referenceField1: [],
    // referenceField10: [],
    // referenceField2: [],
    // referenceField3: [],
    // referenceField4: [],
    // referenceField5: [],
    // referenceField6: [],
    // referenceField7: [],
    // referenceField8: [],
    // referenceField9: [],
    referenceText: [],
    state: [],
    statusId: ["1"],
    //storageBin: [],
    //storageTypeId: [],
    updatedBy: [this.auth.username],
    updatedon: [],
    warehouseId: [this.auth.warehouseId],
    zone: [],
  
  });
  submitted = false;
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


  // statusList: any[] = [
  //   { key: "Active", value: 'Active' },
  //   { key: "InActive", value: 'InActive' }];
    
  formgr: BusinesspartnerElement | undefined;

  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: BusinessPartnerService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    private cs: CommonService,
  ) { }
  ngOnInit(): void {
    // this.form.controls.createdBy.disable();
    // this.form.controls.createdOn.disable();
    // this.form.controls.updatedBy.disable();
    // this.form.controls.updatedOn.disable();
    if (this.data.pageflow != 'New') {
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }
  }



  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code).subscribe(res => {
      console.log(res)
      this.form.patchValue(res, { emitEvent: false });
      // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
      // this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
      this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }

  submit() {
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
    // this.form.removeControl('updatedOn');
    // this.form.removeControl('createdOn');
    this.form.patchValue({ updatedby: this.auth.username });
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
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.partnerCode + " Saved Successfully!","Notification",{
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
  };

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
}




