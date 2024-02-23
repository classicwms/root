import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { CompanySetupService } from '../company-setup.service';
import { Location } from '@angular/common';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { Subscription } from 'rxjs';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-company-new',
  templateUrl: './company-new.component.html',
  styleUrls: ['./company-new.component.scss']
})
export class CompanyNewComponent implements OnInit {
  
  isLinear = false;
  constructor(private fb: FormBuilder,
    public auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private location: Location,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : CompanySetupService,
    private masterService: MasterService) { }



  form = this.fb.group({
    address1: [],
    address2: [],
    city: [],
    companyId: [, Validators.required],
    contactName: [],
    country: [],
    createdBy: [],
    createdOn: [],
    createdOnFE:[],
    currencyId: [],
    deletionIndicator: [],
    desigination: [],
    description:[],
    emailId: [],
    languageId: [],
    noOfOutlets: [],
    noOfPlants: [],
    noOfWarehouse: [],
    phoneNumber: [],
    registrationNo: [],
    state: [],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE:[],
    verticalId: [],
    zipCode: [],
  });



  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  toggleFloat() {

    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;

  }
    
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
  js: any = {}
  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOn.disable();
    this.form.controls.createdOn.disable();
    if(this.auth.userTypeId != 1){
      this.dropdownlist();
      
    }
    else{
      this.dropdownlistSuperAdmin();
    }
    
    if (this.js.pageflow != 'New') {
      if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.languageId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
      this.form.controls.createdOnFE.patchValue(this.cs.dateapiutc0(this.form.controls.createdOn.value));
      this.form.controls.updatedOnFE.patchValue(this.cs.dateapiutc0(this.form.controls.updatedOn.value));
    }))
  }
  companyList: any[] = [];
  currencyList: any[] = [];
  verticalList: any[] = [];
  stateList: any[] = [];
  cityList: any[] = [];
  countryList: any[] = [];
  languageidList: any[] = [];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      
      this.cas.dropdownlist.setup.companyid.url,
     this.cas.dropdownlist.setup.currency.url,
       this.cas.dropdownlist.setup.vertical.url,
      this.cas.dropdownlist.setup.city.url,
      this.cas.dropdownlist.setup.state.url,
      this.cas.dropdownlist.setup.country.url,
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
    this.companyList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.companyid.key);
   this.currencyList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.currency.key);
    this.verticalList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.vertical.key);
    this.cityList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.city.key);
    this.stateList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.state.key);
    this.countryList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.country.key);
    this.languageidList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.languageid.key);
    if(this.auth.userTypeId != 1){
      this.form.controls.languageId.patchValue(this.auth.languageId);
    
      this.form.controls.languageId.disable();
    }
    this.masterService.searchvertical({languageId: [this.auth.languageId]}).subscribe(res => {
      this.verticalList = [];
      res.forEach(element => {
        this.verticalList.push({value: element.verticalId, label: element.verticalId + '-' + element.verticalName});
      });
    });
        
    this.masterService.searchcurrency({languageId: [this.auth.languageId]}).subscribe(res => {
      this.currencyList = [];
      res.forEach(element => {
        this.currencyList.push({value: element.currencyId, label: element.currencyId + '-' + element.currencyDescription});
      });
    });
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.companyid.url,
       this.cas.dropdownlist.setup.currency.url,
        this.cas.dropdownlist.setup.vertical.url,
      this.cas.dropdownlist.setup.city.url,
      this.cas.dropdownlist.setup.state.url,
      this.cas.dropdownlist.setup.country.url,
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
    this.companyList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.companyid.key);
     this.currencyList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.currency.key);
      this.verticalList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.vertical.key);
    this.cityList = this.cas.foreachlist2(results[3], this.cas.dropdownlist.setup.city.key);
    this.stateList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.state.key);
    this.countryList = this.cas.foreachlist2(results[5], this.cas.dropdownlist.setup.country.key);
    this.languageidList = this.cas.foreachlist2(results[6], this.cas.dropdownlist.setup.languageid.key);
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
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
  this.form.controls.createdOn.patchValue(this.cs.day_callapiSearch(this.form.controls.createdOn.value));
  this.form.controls.updatedOn.patchValue(this.cs.day_callapiSearch(this.form.controls.updatedOn.value));
  if (this.js.code) {
    this.sub.add(this.service.Update(this.form.getRawValue(), this.js.code,this.js.languageId).subscribe(res => {
      this.toastr.success(res.companyId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/organisationsetup/company']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.companyId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/organisationsetup/company']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }
  onLanguageChange(value){
    this.masterService.searchCompany({languageId: [value.value]}).subscribe(res => {
      this.companyList = [];
      res.forEach(element => {
        this.companyList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
      });
    });
    
    this.masterService.searchcurrency({languageId: [value.value]}).subscribe(res => {
      this.currencyList = [];
      res.forEach(element => {
        this.currencyList.push({value: element.currencyId, label: element.currencyId + '-' + element.currencyDescription});
      });
    });
    this.masterService.searchvertical({languageId: [value.value]}).subscribe(res => {
      this.verticalList = [];
      res.forEach(element => {
        this.verticalList.push({value: element.verticalId, label: element.verticalId + '-' + element.verticalName});
      });
    });
    this.masterService.searchcountry({countryId: this.form.controls.country.value, languageId: [value.value]}).subscribe(res => {
      this.countryList = [];
      res.forEach(element => {
        this.countryList.push({value: element.countryId, label: element.countryId + '-' + element.countryName});
      });
    });
    this.masterService.searchState({countryId: this.form.controls.country.value, languageId: [value.value]}).subscribe(res => {
      this.stateList = [];
      res.forEach(element => {
        this.stateList.push({value: element.stateId, label: element.stateId + '-' + element.stateName});
      });
    });
    this.masterService.searchCity({countryId: this.form.controls.country.value, stateId: this.form.controls.state.value, languageId: [value.value]}).subscribe(res => {
      this.cityList = [];
      res.forEach(element => {
        this.cityList.push({value: element.cityId, label: element.cityId + '-' + element.cityName});
      });
    });
  }

  onCountryChange(value){
    this.masterService.searchState({countryId: this.form.controls.country.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.stateList = [];
      res.forEach(element => {
        this.stateList.push({value: element.stateId, label: element.stateId + '-' + element.stateName});
      });
    });
    }

    onStateChange(value){
      this.masterService.searchCity({countryId: this.form.controls.country.value, stateId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.cityList = [];
        res.forEach(element => {
          this.cityList.push({value: element.cityId, label: element.cityId + '-' + element.cityName});
        });
      });
    }
    onItemTypeChange(value){
      this.masterService.searchCompany({ 
        languageId: [this.form.controls.languageId.value], companyCodeId: [value.value]}).subscribe(res => {
          this.form.controls.description.patchValue(res[0].description);
      });
    }
}
