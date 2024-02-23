import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PlantService } from '../plant.service';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-plant-new',
  templateUrl: './plant-new.component.html',
  styleUrls: ['./plant-new.component.scss']
})
export class PlantNewComponent implements OnInit {
  companyidDropdown: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : PlantService,
    private masterService: MasterService ) { }



  form = this.fb.group({
    address1:          [],
    address2:          [],
    city:              [],
    companyId:         [, Validators.required],
    contactName:       [],
    country:           [],
    createdBy:         [],
    companyIdAndDescription:[],
    createdOn:         [],
    createdOnFE:       [],
    deletionIndicator: [],
    designation:       [],
    description:        [],
    emailId:             [],
    languageId:        [this.auth.languageId,Validators.required],
    phoneNumber:       [],
    plantId:           [, Validators.required],
    state:             [],
    updatedBy:         [],
    updatedOn:         [],
    updatedOnFE:       [],
    zipCode:           [],
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
  emailId = new FormControl('', [Validators.required, Validators.email]);
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.emailId.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.emailId.hasError('emailId') ? 'Not a valid emailId' : '';
  }
  js: any = {}
  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdOnFE.disable();
   
      this.form.controls.companyId.disable();
      this.dropdownlist();
      
     
    if (this.js.pageflow != 'New') {
      this.form.controls.plantId.disable();
      if (this.js.pageflow == 'Display')
     // this.form.controls.plantId.disable();
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.companyId,this.js.languageId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
         this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
        this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
  }
  companyList: any[] = [];
  plantList: any[] = [];
  stateList: any[] = [];
  cityList: any[] = [];
  languageidList: any[] = [];
  countryList: any[] = [];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.companyid.url,
       this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.city.url,
      this.cas.dropdownlist.setup.state.url,
      this.cas.dropdownlist.setup.country.url,
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
    this.companyList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.companyid.key);
     this.plantList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.plantid.key);
    this.cityList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.city.key);
    this.stateList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.state.key);
    this.countryList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.country.key);
    this.languageidList = this.cas.foreachlist2(results[5], this.cas.dropdownlist.setup.languageid.key);
     this.form.controls.languageId.patchValue(this.auth.languageId);
     this.form.controls.languageId.disable();
    this.form.controls.companyId.patchValue(this.auth.companyId);
    this.masterService.searchPlant({companyCodeId: [this.form.controls.companyId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.plantList = [];
      res.forEach(element => {
        this.plantList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
 
  oncompanyChange(value){
    this.form.controls.languageId.patchValue(value.languageId); 
    this.masterService.searchPlant({companyCodeId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res=>{
      this.plantList=[];
      res.forEach(element=>{
        this.plantList.push({value:element.plantId,label:element.plantId+'-'+element.description})
      })
    })
   
    this.masterService.searchcountry({languageId:[this.form.controls.languageId.value]}).subscribe(res=>{
      this.countryList=[];
      res.forEach(element=>{
        this.countryList.push({value:element.countryId,label:element.countryId+'-'+element.countryName})
      })
    })
    
  
  }
  oncountryChange(value){
    this.masterService.searchState({countryId:value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res=>{
      this.stateList=[];
      res.forEach(element=>{
        this.stateList.push({value:element.stateId,label:element.stateId+'-'+element.stateName})
      })
    })
    this.masterService.searchCity({countryId:value.value, languageId: [this.form.controls.languageId.value],stateId:this.form.controls.state.value}).subscribe(res => {
      this.cityList = [];
      res.forEach(element => {
        this.cityList.push({value: element.cityId, label: element.cityId + '-' + element.cityName});
      })
    });
  }
  onstateChange(value){
    this.masterService.searchCity({countryId:this.form.controls.country.value, languageId: [this.form.controls.languageId.value], stateId:value.value}).subscribe(res => {
      this.cityList = [];
      res.forEach(element => {
        this.cityList.push({value: element.cityId, label: element.cityId + '-' + element.cityName});
      })
    });
  }
  onplantchange(value){
    this.masterService.searchPlant({companyCodeId:[this.form.controls.companyId.value],languageId:[this.form.controls.languageId.value],plantId:[value.value]}).subscribe(res => {
      this.form.controls.description.patchValue(res[0].description);
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
  this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
  this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
  if (this.js.code) {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code,this.js.companyId,this.js.languageId).subscribe(res => {
      this.toastr.success(res.plantId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/organisationsetup/plant']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.plantId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/organisationsetup/plant']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}
