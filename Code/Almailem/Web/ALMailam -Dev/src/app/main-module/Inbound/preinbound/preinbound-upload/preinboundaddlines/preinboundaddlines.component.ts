import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PreinboundService } from '../../preinbound.service';
import { Location } from "@angular/common";
@Component({
  selector: 'app-preinboundaddlines',
  templateUrl: './preinboundaddlines.component.html',
  styleUrls: ['./preinboundaddlines.component.scss']
})
export class PreinboundaddlinesComponent implements OnInit {
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private service : PreinboundService,
    private location: Location,
    ) { }


  form = this.fb.group({
    containerNumber: [],
    expectedDate: [],
    expectedDate1: [ ,Validators.required],
    expectedQty:[,Validators.required],
    invoiceNumber:[],
    lineReference: [,Validators.required],
    manufacturerName: [],
    manufacturerCode: [,Validators.required],
    packQty: [,Validators.required],
    sku: [,Validators.required],
    skuDescription: [,Validators.required],
    supplierCode: [],
    supplierPartNumber: [],
    uom: [,Validators.required],
  });

 cancel(){
  this.dialogRef.close();
 }

  panelOpenState = false;
  submitted = false;
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
  ngOnInit(): void {
    this.form.patchValue(this.data);
  }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

  inputChange(value){
     this.form.controls.expectedDate.patchValue(this.cs.dateddMMYY(this.form.controls.expectedDate1.value))
   }
   back() {
    this.location.back();
  }

submit(){
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
  let obj: any = {};
  obj.data=this.form.value;
  this.dialogRef.close(this.form.getRawValue());
}

}


