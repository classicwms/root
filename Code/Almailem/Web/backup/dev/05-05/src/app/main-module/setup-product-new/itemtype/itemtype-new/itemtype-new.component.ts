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
  warehouseidList1: any[] = [];
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
    companyId: [,Validators.required],
    createdBy: [],
    createdOn: [],
    createdOnFE: [],
    deletionIndicator: [],
    description: [],
    itemTypeId: [,Validators.required],
    languageId: [,Validators.required],
    plantId: [,Validators.required],
    storageMethod: [,Validators.required],
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
    if(this.auth.userTypeId != 1){
    this.dropdownlist();
    }
    if(this.auth.userTypeId == 1){
      this.superadmindropdownlist();
      }
    if (this.js.pageflow != 'New') {
      this.form.controls.itemTypeId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
    
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
  languageidList: any[] = [];
  companyidList:any[]=[];
  itemtypedesList:any[]=[];
  
  plantidList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.itemtypeid.url,
    ]).subscribe((results) => {
   this.warehouseidList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
 this.itemtypeList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.itemtypeid.key)
 if(this.auth.userTypeId != 1){
  this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
  this.form.controls.languageId.patchValue(this.auth.languageId);
  this.form.controls.plantId.patchValue(this.auth.plantId);
  this.form.controls.companyId.patchValue(this.auth.companyId);
  this.form.controls.warehouseId.disable();
}
    });
    this.spin.hide();
  }
  superadmindropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
   
      this.cas.dropdownlist.setup.itemtypeid.url,
    ]).subscribe((results) => {
    this.warehouseidDropdown = results[0];
    this.warehouseidDropdown.forEach(element => {
      this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc, companyCodeId: element.companyCodeId, plantId: element.plantId, languageId: element.languageId});
    });
  
 this.itemtypeList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.itemtypeid.key);
    });
    this.spin.hide();
  }
  onWarehouseChange(value){
    this.form.controls.companyId.patchValue(value.companyCodeId);
    this.form.controls.languageId.patchValue(value.languageId);
    this.form.controls.plantId.patchValue(value.plantId);

    this.masterService.searchitemtype({companyCodeId: this.form.controls.companyId.value, warehouseId:value.value, plantId:this.form.controls.plantId.value, 
      languageId: [this.form.controls.languageId.value]}).subscribe(res=>{  this.itemtypeList = [];
        res.forEach(element => {
          this.itemtypeList.push({value: element.itemTypeId, label: element.itemTypeId + '-' + element.itemType});
        })
      });
  }
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
