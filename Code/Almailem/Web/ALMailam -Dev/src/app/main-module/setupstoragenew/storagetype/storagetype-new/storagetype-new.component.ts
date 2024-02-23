import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { StoragetypeService } from '../storagetype.service';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-storagetype-new',
  templateUrl: './storagetype-new.component.html',
  styleUrls: ['./storagetype-new.component.scss']
})
export class StoragetypeNewComponent implements OnInit {

  warehouseidDropdown: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : StoragetypeService,
    private masterService: MasterService,) { }



  form = this.fb.group({
    addToExistingStock: [],
  allowNegativeStock: [],
  capacityByQty:[],
  capacityByWeight: [],
  capacityCheck: [],
  companyId: [this.auth.companyId,Validators.required],
  createdBy: [],
  createdOn: [],
  createdOnFE:[],
  deletionIndicator: [],
  languageId: [this.auth.languageId,Validators.required],
  mixToStock: [0,],
  plantId: [,Validators.required],
  returnToSameStorageType:[],
  storageClassId: [,Validators.required],
  storageTemperatureFrom: [],
  storageTemperatureTo: [],
  storageTypeId: [,Validators.required],
  storageUom: [],
  updatedBy: [],
  updatedOn: [],
  updatedOnFE:[],
  warehouseId: [,Validators.required],
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
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdOnFE.disable();
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.companyId.patchValue(this.auth.companyId);
    this.form.controls.companyId.disable();
    this.form.controls.languageId.disable();
    this.form.controls.plantId.disable();
    this.form.controls.warehouseId.disable();
      this.dropdownlist();
    
      
    
  
    if (this.js.pageflow != 'New') {
      this.form.controls.plantId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.storageClassId.disable();
      this.form.controls.storageTypeId.disable();
      if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.storageClassId,this.js.languageId,this.js.plantId,this.js.companyId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
       this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
       this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
    this.spin.hide();
  }
  warehouseidList: any[] = [];
  companyList: any[] = [];
  languageidList: any[] = [];
  plantList: any[] = [];
  subitemgroupList: any[] = [];
  itemtypeList: any[] = [];
  storagetypeList: any[] = [];
  itemgroupList: any[] = [];
  storageclassList: any[] = [];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.storagetypeid.url,
      this.cas.dropdownlist.setup.storageclassid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
   ]).subscribe((results) => {
   this.companyList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.companyid.key);
    this.plantList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.plantid.key);
    this.warehouseidList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
    this.storagetypeList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.storagetypeid.key);
    this.storagetypeList = this.cs.removeDuplicatesFromArrayNewstatus(this.storagetypeList);
    this.storageclassList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.storageclassid.key);
    this.storageclassList = this.cs.removeDuplicatesFromArrayNewstatus(this.storageclassList);
    this.languageidList = this.cas.foreachlist2(results[5], this.cas.dropdownlist.setup.languageid.key);
    
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.companyId.patchValue(this.auth.companyId);
      this.masterService.searchPlantenter({companyId: this.auth.companyId, languageId: this.auth.languageId}).subscribe(res => {
        this.plantList = [];
        res.forEach(element => {
          this.plantList.push({value: element.plantId, label: element.plantId + '-' + element.description});
          if(this.plantList.length == 1){
            this.form.controls.plantId.patchValue(this.plantList[0].value)
          }
        
        });
        
      });
      this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.form.controls.plantId.value}).subscribe(res => {
        this.warehouseidList = [];
        res.forEach(element => {
          this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
          if(this.warehouseidList.length != 0){
            this.form.controls.warehouseId.patchValue(this.warehouseidList[0].value)
          }
        });
        
      });
      this.masterService.searchstorageclassenter({companyId:this.auth.companyId ,plantId:this.auth.plantId,warehouseId:this.auth.warehouseId, languageId:this.auth.languageId,}).subscribe(res => {
        this.storageclassList = [];
        res.forEach(element => {
          this.storageclassList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
        })
      });  this.masterService.searchstoragetype({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, warehouseId: this.auth.warehouseId,storageClassId:[this.form.controls.storageClassId.value], languageId: [this.auth.languageId]}).subscribe(res => {
        this.storagetypeList = [];
        res.forEach(element => {
          this.storagetypeList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
        });
      });

    this.form.controls.languageId.disable();
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
 
  onstorageclassChange(value){
    this.masterService.searchstoragetype({companyCodeId: this.form.controls.companyId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,storageClassId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storagetypeList = [];
      res.forEach(element => {
        this.storagetypeList.push({value: element.storageTypeId, label: element.storageTypeId + '-' + element.description});
      });
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
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code,this.auth.warehouseId,this.js.storageClassId,this.js.languageId,this.js.plantId,this.js.companyId).subscribe(res => {
      this.toastr.success(res.storageTypeId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productstorage/storagetype']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.storageTypeId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productstorage/storagetype']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}

