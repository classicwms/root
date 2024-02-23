import { Component, Inject, OnInit } from '@angular/core';
import { Validators, FormBuilder, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PreinboundService } from '../preinbound.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cancel',
  templateUrl: './cancel.component.html',
  styleUrls: ['./cancel.component.scss']
})
export class CancelComponent implements OnInit {

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
    oldInvoiceNumber: [, Validators.required],
    newInvoiceNumber: [],
  });
  panelOpenState = false;
  constructor(
    private router: Router,
    private service: PreinboundService,
    public dialogRef: MatDialogRef<CancelComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private cas: CommonApiService,
  ) { }
  ngOnInit(): void {
    this.form.controls.oldInvoiceNumber.disable();
    this.form.controls.oldInvoiceNumber.patchValue(this.data.code.refDocNumber);
    console.log(this.data);
    this.getDropdown();
  }
  sub = new Subscription();
  submitted = false;
  // fill() {
  //   this.spin.show();
  //   this.sub.add(this.service.Get(this.data.code,this.data.countryId,this.data.languageId,this.data.stateId,this.data.zipCode).subscribe(res => {
  //     this.form.patchValue(res, { emitEvent: false });
  //    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
  //   this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
  //     this.spin.hide();
  //   },
  //    err => {
  //   this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }
  //   ));
  // }
  multiOrderNo: any[] = [];
  getDropdown() {
    this.sub.add(
      this.service.search({
        warehouseId: [this.auth.warehouseId],
        companyCodeId: [this.auth.companyId],
        plantId: [this.auth.plantId],
        languageId: [this.auth.languageId],
      }).subscribe(
        (res: any[]) => {
          // Filter out items where statusId is equal to 24
          this.multiOrderNo = res
            .filter((item) => item.statusId !== 24 && item.refDocNumber !== this.data.code.refDocNumber)
            .map((x: any) => ({
              value: x.refDocNumber,
              label: x.refDocNumber,
            }));
        },

      )
    );
    this.multiOrderNo = this.cas.removeDuplicatesFromArrayNew(this.multiOrderNo);
  }
  submit() {
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );

      this.cs.notifyOther(true);
      return;
    }
    this.spin.show();
    this.sub.add(this.service.UpdateSalesInvoice(this.auth.warehouseId, this.auth.plantId, this.auth.companyId, this.auth.languageId, this.form.controls.oldInvoiceNumber.value, this.form.controls.newInvoiceNumber.value).subscribe(res => {
      this.toastr.success("Order Replaced Sucessfully!", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      this.dialogRef.close();
      this.spin.hide();

    }, err => {
      this.cs.commonerrorNew(err);
      this.dialogRef.close();
      this.spin.hide();
    }));


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









