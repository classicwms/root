import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators, FormControl, FormArray, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { StratergyService } from '../stratergy.service';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-stratergy-new',
  templateUrl: './stratergy-new.component.html',
  styleUrls: ['./stratergy-new.component.scss']
})
export class StratergyNewComponent implements OnInit {
  storageMethodList: any[] = [];
  maintainanceList: any[] = [];
  warehouseidList: any[] = [];
  warehouseidDropdown: any;
  advanceFilterShow: boolean;
  @ViewChild('Setupbatch') Setupbatch: Table | undefined;
  batch: any;
  selectedbatch : any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : StratergyService,
    private masterService: MasterService) { 
      
    }




  public stx: FormGroup;
  form = this.fb.group({
    companyId: [,Validators.required],
    companyIdAndDescription: [],
    deletionIndicator: [],
    description: [],
    languageId: [,Validators.required],
    levelId: [,Validators.required],
    levelIdAndDescription: [],
    maintenance: [,Validators.required],
    plantId: [,Validators.required],
    plantIdAndDescription: [],
    storageMethod: [,Validators.required],
    warehouseId: [,Validators.required],
    warehouseIdAndDescription: [],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE: [],
    createdOn: [],
    createdOnFE: [],
    createdBy: [],
  });
  
  get formArr() {
    return this.stx.get('Rows') as FormArray;
  }

  initRows() {
    return this.fb.group({
      stx:  []
    });
  }

  addNewRow() {
    this.formArr.push(this.initRows());

    
  }
  removerow(x: any){
    this.formArr.removeAt(x);
  }
  referencelist:any[] = [];
addreference(levelId){

  if(levelId.value==1){
    this.masterService.searchWarehouse({companyCodeId: this.auth.companyId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.referencelist = [];
      res.forEach(element => {
        this.referencelist.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
      })
    });
  }
if(levelId.value==2){
    this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId], plantId:[this.auth.plantId], languageId: [this.auth.languageId]}).subscribe(res => {
      this.referencelist = [];
      res.forEach(element => {
        this.referencelist.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      })
    });
  }
}
  
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

    this.stx = this.fb.group({
      Rows: this.fb.array([this.initRows()]),
    });

    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);

    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOn.disable();
    this.form.controls.createdOn.disable();
    
      this.form.controls.companyId.patchValue(this.auth.companyId);
      this.dropdownlist();
    
      
    
    
    if (this.js.pageflow != 'New') {
   
     // if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.languageId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
      this.stx.get('Rows')?.patchValue(res.stx);
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
  }
  stratergyList: any[]=[];
  companyList: any[] = [];
  languageidList: any[] = [];
  plantList: any[] = [];
  levelList: any[] = [];
  stateList: any[] = [];
  cityList: any[] = [];
  countryList: any[] = [];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([ 
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.levelid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
    this.cas.dropdownlist.setup.strategyid.url,
    ]).subscribe((results) => {
  this.warehouseidList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
   this.levelList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.levelid.key);
   this.companyList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.companyid.key);
    this.plantList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
    this.languageidList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.languageid.key);
    this.stratergyList=this.cas.forLanguageFilter(results[5],this.cas.dropdownlist.setup.strategyid.key);
  //this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
  this.form.controls.languageId.patchValue(this.auth.languageId);
  //this.form.controls.plantId.patchValue(this.auth.plantId);
  this.form.controls.companyId.patchValue(this.auth.companyId);
  this.form.controls.companyId.disable();
this.form.controls.languageId.disable();
this.masterService.searchPlantenter({companyId: this.auth.companyId, languageId: this.auth.languageId}).subscribe(res => {
  this.plantList = [];
  res.forEach(element => {
    this.plantList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  });
});
this.masterService.searchWarehouse({companyCodeId: this.auth.companyId, languageId: [this.auth.languageId],plantId:this.form.controls.plantId.value}).subscribe(res => {
  this.warehouseidList = [];
  res.forEach(element => {
    this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
  });
});
 
this.spin.hide();
}, (err) => {
  this.toastr.error(err, "");
  this.spin.hide();
});
 }
//  onplantchange(value){
   
//   this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.form.controls.plantId.value}).subscribe(res => {
//    this.warehouseidList = [];
//    res.forEach(element => {
//      this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
//    });
//  });
// }
  onWarehouseChange(value){
   
    this.masterService.searchstrategy({companyCodeId: this.auth.companyId, warehouseId:value.value, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.stratergyList = [];
      res.forEach(element => {
        this.stratergyList.push({value: element.strategyTypeId, label: element.strategyTypeId + '-' + element.strategyText});
      })
    });
  }
  submit() {
    let obj: any ={};
    let stx1: any[] = [];
    obj = this.form.getRawValue();
  stx1 = (this.stx.getRawValue().Rows);
    obj.stx = stx1;
    console.log(obj);
    console.log(this.stx.getRawValue().Rows);
    console.log(this.form.getRawValue());
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
    this.sub.add(this.service.Update(obj, this.js.code,this.js.languageId).subscribe(res => {
      this.toastr.success(res.storageMethod + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productsetup/batch']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(obj).subscribe(res => {
      this.toastr.success(res.storageMethod + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productsetup/batch']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }
  onChange() {
    console.log(this.selectedbatch.length)
    const choosen= this.selectedbatch[this.selectedbatch.length - 1];   
    this.selectedbatch.length = 0;
    this.selectedbatch.push(choosen);
  } 
}
