import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { NumberrangestoragebinService } from '../numberrangestoragebin.service';

@Component({
  selector: 'app-numberrangestoragebin-new',
  templateUrl: './numberrangestoragebin-new.component.html',
  styleUrls: ['./numberrangestoragebin-new.component.scss']
})
export class NumberrangestoragebinNewComponent implements OnInit {

 
 
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
    private service : NumberrangestoragebinService,
    private masterService: MasterService
    )  { 
      
    }



  form = this.fb.group({
    aisleNumber: [,Validators.required],
  companyCodeId: [,Validators.required],
  createdBy: [],
  createdOn: [],
  currentNumberRange: [],
  deletionIndicator: [],
  floorId: [,Validators.required],
  languageId: [,Validators.required],
  numberRangeFrom: [],
  numberRangeTo: [],
  numberRangeType: [],
  plantId: [,Validators.required],
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
  rowId: [,Validators.required],
  shelfId: [],
  spanId: [],
  storageSectionId: [,Validators.required],
  updatedBy: [],
  updatedOn: [],
  warehouseId: [,Validators.required],
  updatedOnFE:[],
  createdOnFE:[],
  });


  filterpartnercodeList: any[] = [];
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
  public errorHandling = (control:  string, error:  string = "required") => {
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
      this.dropdownlist();      
 
    if (this.js.pageflow != 'New') {
      this.form.controls.floorId.disable();
  this.form.controls.storageSectionId.disable();
  this.form.controls.rowId.disable();
  this.form.controls.aisleNumber.disable();
      if (this.js.pageflow == 'Display')
     
      
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId,this.js.aisleNumber,this.js.floorId,this.js.rowId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
    this.spin.hide();
  }
  floorList: any[] = [];
  aisleList:any[]=[];
  storagesectionList: any[] = [];
  itemtypeList: any[] = [];
  warehouseIdList: any[] = [];
  shelfList: any[] = [];
  rowList:any[]=[];
  companyList: any[] = [];
  plantList: any[]=[];
  languageList: any[]=[];
    spanList:any[]=[];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.itemtypeid.url,
        this.cas.dropdownlist.setup.floorid.url,
        this.cas.dropdownlist.setup.storagesectionid.url,
        this.cas.dropdownlist.setup.aisleid.url,
        this.cas.dropdownlist.setup.rowid.url,
        this.cas.dropdownlist.setup.spanid.url,
        this.cas.dropdownlist.setup.shelfid.url,
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
      ]).subscribe((results) => {
        this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
      this.itemtypeList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.itemtypeid.key);
      //this.floorList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.floorid.key);
      this.storagesectionList=this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.storagesectionid.key);
      this.aisleList=this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.aisleid.key);
      this.rowList=this.cas.forLanguageFilter(results[5], this.cas.dropdownlist.setup.rowid.key);
      this.spanList=this.cas.forLanguageFilter(results[6], this.cas.dropdownlist.setup.spanid.key);
      this.shelfList=this.cas.forLanguageFilter(results[7],this.cas.dropdownlist.setup.shelfid.key);
      this.companyList = this.cas.forLanguageFilter(results[8], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[9], this.cas.dropdownlist.setup.plantid.key);
      this.languageList=this.cas.foreachlist2(results[10],this.cas.dropdownlist.setup.languageid.key);
      });
      this.masterService.searchFloor({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
        this.floorList = [];
        res.forEach(element => {
          this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
      });
        this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
        this.form.controls.languageId.patchValue(this.auth.languageId);
        this.form.controls.plantId.patchValue(this.auth.plantId);
        this.form.controls.companyCodeId.patchValue(this.auth.companyId);
        this.form.controls.warehouseId.disable();
        this.form.controls.companyCodeId.disable();
        this.form.controls.plantId.disable();
        this.form.controls.languageId.disable();
    
       
      this.spin.hide();
    }
  
 
  onfloorChange(value){
    this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value],floorId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[value.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.aisleList = [];
      res.forEach(element => {
        this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
      });
    });
    this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[value.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.rowList = [];
      res.forEach(element => {
        this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
      });
    });
    this.masterService.searchspan({aiselId:[this.form.controls.aisleNumber.value],rowId:[this.form.controls.rowId.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[value.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.spanList = [];
      res.forEach(element => {
        this.spanList.push({value: element.spanId, label: element.spanId + '-' + element.spanDescription});
      });
    });
    this.masterService.searchshelf({aiselId:[this.form.controls.aisleNumber.value],rowId:[this.form.controls.rowId.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[value.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.shelfList = [];
      res.forEach(element => {
        this.shelfList.push({value: element.shelfId, label: element.shelfId + '-' + element.shelfDescription});
      });
    });
  }
  onstoragesectionChange(value){
    this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.aisleList = [];
      res.forEach(element => {
        this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
      });
    });
    this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[value.value], languageId: [this.form.controls.languageId.value],}).subscribe(res => {
      this.rowList = [];
      res.forEach(element => {
        this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
      });
    });
    this.masterService.searchspan({aiselId:[this.form.controls.aisleNumber.value],rowId:[this.form.controls.rowId.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.spanList = [];
      res.forEach(element => {
        this.spanList.push({value: element.spanId, label: element.spanId + '-' + element.spanDescription});
      });
    });
    this.masterService.searchshelf({aiselId:[this.form.controls.aisleNumber.value],rowId:[this.form.controls.rowId.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.shelfList = [];
      res.forEach(element => {
        this.shelfList.push({value: element.shelfId, label: element.shelfId + '-' + element.shelfDescription});
      });
    });
  }
  onaisleChange(value){
  
    this.masterService.searchspan({aiselId:[value.value],rowId:[this.form.controls.rowId.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.spanList = [];
      res.forEach(element => {
        this.spanList.push({value: element.spanId, label: element.spanId + '-' + element.spanDescription});
      });
    });
    this.masterService.searchshelf({aiselId:[value.value],rowId:[this.form.controls.rowId.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.shelfList = [];
      res.forEach(element => {
        this.shelfList.push({value: element.shelfId, label: element.shelfId + '-' + element.shelfDescription});
      });
    });
  }
  onrowChange(value){
   
    this.masterService.searchspan({aiselId:[this.form.controls.aisleNumber.value],rowId:[value.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.spanList = [];
      res.forEach(element => {
        this.spanList.push({value: element.spanId, label: element.spanId + '-' + element.spanDescription});
      });
    });
    this.masterService.searchshelf({aiselId:[this.form.controls.aisleNumber.value],rowId:[value.value],companyCodeId:this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId:this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.shelfList = [];
      res.forEach(element => {
        this.shelfList.push({value: element.shelfId, label: element.shelfId + '-' + element.shelfDescription});
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
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId,this.js.aisleNumber,this.js.floorId,this.js.rowId).subscribe(res => {
      this.toastr.success(res.storageSectionId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/other-masters/numberrangestoragebin']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.storageSectionId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/other-masters/numberrangestoragebin']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}









