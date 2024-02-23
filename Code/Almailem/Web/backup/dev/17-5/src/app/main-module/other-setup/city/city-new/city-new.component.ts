import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { CityService } from '../city.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-city-new',
  templateUrl: './city-new.component.html',
  styleUrls: ['./city-new.component.scss']
})
export class CityNewComponent implements OnInit {

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
    cityId: [,Validators.required],
    cityName: [],
    countryId: [,Validators.required],
    countryIdAndDescription:[],
    stateIdAndDescription:[],
    deletionIndicator: [],
    languageId: [,Validators.required],
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
    stateId: [,Validators.required],
    zipCode: [,Validators.required],
    createdBy:[],
    createdOn:[],
    createdOnFE:[],
    updatedBy:[],
    updatedOn:[],
    updatedOnFE:[],
  });
  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: CityService,
    private cas: CommonApiService,
    private masterService: MasterService
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
      this.form.controls.updatedOnFE.disable();
      this.form.controls.createdBy.disable();
      this.form.controls.createdOnFE.disable();
 
    if(this.auth.userTypeId != 1){
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin()
    }
    if (this.data.pageflow != 'New') {
      this.form.controls.cityId.disable();
  
      if (this.data.pageflow == 'Display')
      this.form.disabled;
      this.form.controls.countryId.disable();
      this.form.controls.stateId.disable();
      this.form.controls.cityId.disable();
      this.form.controls.languageId.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.countryId,this.data.languageId,this.data.stateId,this.data.zipCode).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    if(this.auth.userTypeId != 1){
      this.dropdownlist
    }else{
      this.dropdownlistSuperAdmin()
    }
      this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }
  languageidList: any[] = [];
  countryList:any[]=[];
  stateList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.country.url,
      this.cas.dropdownlist.setup.state.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.countryList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.country.key);
    
    this.stateList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.state.key);
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
    this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.country.url,
      this.cas.dropdownlist.setup.state.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    //this.countryList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.country.key);
    this.stateList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.state.key);
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  onlangaugeChange(value){
    this.masterService.searchcountry({ languageId: [value.value]}).subscribe(res => {
      this.countryList = [];
      res.forEach(element => {
        this.countryList.push({value: element.countryId, label: element.countryId + '-' + element.countryName});
      });
    });
    this.masterService.searchState({ country:this.form.controls.countryId.value,languageId: [value.value]}).subscribe(res => {
      this.stateList = [];
      res.forEach(element => {
        this.stateList.push({value: element.stateId, label: element.stateId + '-' + element.stateName});
      });
    });}
  onCountryChange(value){
    this.masterService.searchState({countryId: this.form.controls.countryId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.stateList = [];
      res.forEach(element => {
        this.stateList.push({value: element.stateId, label: element.stateId + '-' + element.stateName});
      });
  
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
    this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.data.countryId,this.data.languageId,this.data.stateId,this.data.zipCode).subscribe(res => {
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
      this.toastr.success(res.cityId + " Saved Successfully!","Notification",{
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
   public errorHandling = (control: string, error:string = "required") => {
     return this.form.controls[control].hasError(error) && this.submitted;
   }
   getErrorMessage() {
     if (this.email.hasError('required')) {
       return ' Field should not be blank';
     }
     return this.email.hasError('email') ? 'Not a valid email' : '';
   }
}









