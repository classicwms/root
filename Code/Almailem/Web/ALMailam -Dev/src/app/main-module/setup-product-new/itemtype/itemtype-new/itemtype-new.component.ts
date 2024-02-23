import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ItemtypeService } from '../itemtype.service';
import { MasterService } from 'src/app/shared/master.service';
import { WarehouseService } from 'src/app/main-module/other-setup/warehouse/warehouse.service';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';

@Component({
  selector: 'app-itemtype-new',
  templateUrl: './itemtype-new.component.html',
  styleUrls: ['./itemtype-new.component.scss']
})
export class ItemtypeNewComponent implements OnInit {

  isLinear = false;
  warehouseidList: any[] = [];
 // warehouseidList1: any[] = [];
  warehouseidDropdown: any;
  constructor(private fb: FormBuilder,
    public auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private service : ItemtypeService,
    private cas: CommonApiService,
    private masterService: MasterService, 
    private warehouseService:WarehouseService) { }



  form = this.fb.group({
    companyId: [this.auth.companyId, Validators.required],
    createdBy: [],
    createdOn: [],
    createdOnFE: [],
    deletionIndicator: [],
    description: [],
    itemTypeId: [,Validators.required],
    languageId: [this.auth.languageId,],
    plantId: [,Validators.required],
    storageMethod: [, Validators.required],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE: [],
    variantManagementIndicator: [],
    warehouseId: [,Validators.required],
    companyIdAndDescription:[],
    plantIdAndDescription:[],
    warehouseIdAndDescription:[],

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
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
    this.dropdownlist();
    this.form.controls.plantId.disable();
    this.form.controls.warehouseId.disable();
   
    if (this.js.pageflow != 'New') {
      this.form.controls.plantId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.itemTypeId.disable();
     if (this.js.pageflow == 'Display')
 
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.companyId,this.js.languageId,this.js.plantId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
      this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
      this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
    this.spin.hide();
  }
;
  currencyList: any[] = [];
  itemtypeList: any[] = [];
  stateList: any[] = [];
  cityList: any[] = [];
  countryList: any[] = [];
  companyList: any[] = [];
    plantList: any[] = [];
    languageidList: any[] = [];
    floorList: any[] = [];
    storageList: any[] = [];
  
  plantidList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.itemtypeid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
   ]).subscribe((results) => {
   this.companyList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.companyid.key);
    this.plantList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
    this.warehouseidList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
 this.itemtypeList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.itemtypeid.key);
 this.languageidList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.languageid.key);
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
this.masterService.searchitemtype({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
  languageId: [this.auth.languageId]}).subscribe(res=>{  this.itemtypeList = [];
    res.forEach(element => {
      this.itemtypeList.push({value: element.itemTypeId, label: element.itemTypeId + '-' + element.itemType});
    })
  });
  this.form.controls.companyId.patchValue(this.auth.companyId);
  this.form.controls.languageId.disable();
  this.form.controls.companyId.disable();

    });
    this.spin.hide();
  }
  // onplantchange(value){
   
  //   this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.form.controls.plantId.value}).subscribe(res => {
  //    this.warehouseidList = [];
  //    res.forEach(element => {
  //      this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
  //    });
  //  });
  // }
  // onWarehouseChange(value){
  //   this.masterService.searchitemtype({companyCodeId: this.auth.companyId, warehouseId:value.value, plantId:this.form.controls.plantId.value, 
  //     languageId: [this.auth.languageId]}).subscribe(res=>{  this.itemtypeList = [];
  //       res.forEach(element => {
  //         this.itemtypeList.push({value: element.itemTypeId, label: element.itemTypeId + '-' + element.itemType});
  //       })
  //     });
  // }
  onItemTypeChange(value){
    this.masterService.searchitemtype({companyCodeId: this.form.controls.companyId.value, warehouseId:this.form.controls.warehouseId.value, plantId:this.form.controls.plantId.value, 
      languageId: [this.form.controls.languageId.value], itemTypeId: [value.value]}).subscribe(res => {
        this.form.controls.description.patchValue(res[0].itemType);
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
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code,this.js.warehouseId,this.js.companyId,this.js.languageId,this.js.plantId).subscribe(res => {
      this.toastr.success(res.itemTypeId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productsetup/itemtype']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.itemTypeId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productsetup/itemtype']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}
