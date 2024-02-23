  import { Component, Inject, OnInit } from '@angular/core';
  import { FormBuilder, FormControl, Validators } from '@angular/forms';
  import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
  import { NgxSpinnerService } from 'ngx-spinner';
  import { ToastrService } from 'ngx-toastr';
  import { Subscription } from 'rxjs';
  import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
  import { CommonService } from 'src/app/common-service/common-service.service';
  import { AuthService } from 'src/app/core/core';
  import { HandlingUnitElement, HandlingUnitService } from '../handling-unit.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

  @Component({
    selector: 'app-unit-new',
    templateUrl: './unit-new.component.html',
    styleUrls: ['./unit-new.component.scss']
  })
  export class UnitNewComponent implements OnInit {
    screenid: 1033 | undefined;
    warehouseidDropdown: any;
    sub = new Subscription();
    //creation of Form
     email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
    form = this.fb.group({
      handlingUnit: [,Validators.required],
      length: [],
      lengthUom: [],
      width: [],
      widthUom: [],
      height: [],
      heightUom: [],
      volume: [],
      volumeUom: [],
      weight: [],
      weightUom: [],
      createdOn: [],
      languageId: [,Validators.required],
      warehouseId: [,Validators.required],
      plantId: [,Validators.required],
      statusId: ["1",],
      companyCodeId: [,],
      createdBy: [this.auth.username,],
      updatedBy: [this.auth.username,],
      updatedOn: [],
      
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
    public errorHandling = (control: string, error: string = "required") => {
      return this.form.controls[control].hasError(error) && this.submitted;
    }
    getErrorMessage() {
      if (this.email.hasError('required')) {
        return ' Field should not be blank';
      }
      return this.email.hasError('email') ? 'Not a valid email' : '';
    }

    // statusList: any[] = [
    //   { key: "Active", value: 'Active' },
    //   { key: "InActive", value: 'InActive' }];
      
    formgr: HandlingUnitElement | undefined;

    panelOpenState = false;
    constructor(
      public dialogRef: MatDialogRef<DialogExampleComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any,
      private service: HandlingUnitService,
      public toastr: ToastrService,
      private spin: NgxSpinnerService,
      private auth: AuthService,
      private fb: FormBuilder,
      private cas: CommonApiService,
      private cs: CommonService,
    ) { }
    ngOnInit(): void {
      this.form.controls.createdBy.disable();
      this.form.controls.createdOn.disable();
      this.form.controls.updatedBy.disable();
      this.form.controls.updatedOn.disable();
     
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
     // this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
      this.form.controls.warehouseId.disable();
        this.dropdownlist();      
      
      
    
      if (this.data.pageflow != 'New') {
      this.form.controls.handlingUnit.disable();
        if (this.data.pageflow == 'Display')
          this.form.disable();
        this.fill();
    }
    }
    companyList: any[] = [];
    plantList: any[]=[];
    languageList: any[]=[];
    warehouseIdList: any[] = [];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
      
      ]).subscribe((results) => {
        this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
        this.companyList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
        this.plantList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.plantid.key);
        this.languageList=this.cas.foreachlist2(results[3],this.cas.dropdownlist.setup.languageid.key);
          this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
          this.form.controls.languageId.patchValue(this.auth.languageId);
          this.form.controls.plantId.patchValue(this.auth.plantId);
          this.form.controls.companyCodeId.patchValue(this.auth.companyId);
         // this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
          this.form.controls.warehouseId.disable();
          this.form.controls.companyCodeId.disable();
          this.form.controls.plantId.disable();
          this.form.controls.languageId.disable();
        
    
      });
      this.spin.hide();
    }
     
    onWarehouseChange(value){
      this.form.controls.companyCodeId.patchValue(value.companyCodeId);
      this.form.controls.languageId.patchValue(value.languageId);
      this.form.controls.plantId.patchValue(value.plantId);
    
     
  }
  superadmindropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
    
    ]).subscribe((results) => {
    this.warehouseidDropdown = results[0];
    this.warehouseidDropdown.forEach(element => {
      this.warehouseIdList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc, companyCodeId: element.companyCodeId, plantId: element.plantId, languageId: element.languageId});
    });
 ;
    });
    if(this.auth.userTypeId == 1){
      this.form.controls.warehouseId.patchValue(this.form.controls.warehouseId.value);  
      this.form.controls.languageId.patchValue(this.form.controls.languageId.value);
      this.form.controls.plantId.patchValue(this.form.controls.plantId.value);
      this.form.controls.companyCodeId.patchValue(this.form.controls.companyCodeId.value);
     // this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
      //this.form.controls.warehouseId.disable();
    }
    this.spin.hide();
  }
    fill() {
      this.spin.show();
      this.sub.add(this.service.Get(this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId).subscribe(res => {
        console.log(res)
        this.form.controls.statusId.patchValue(res.statusId != null ? res.statusId.toString() : '');
        this.form.patchValue(res, { emitEvent: false });
        this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
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
      this.form.removeControl('updatedOn');
      this.form.removeControl('createdOn');
      this.form.patchValue({ updatedby: this.auth.username });
      if (this.data.code) {
        this.sub.add(this.service.Update(this.form.getRawValue(),this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId).subscribe(res => {
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
          this.toastr.success(res.handlingUnit + " Saved Successfully!","Notification",{
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




