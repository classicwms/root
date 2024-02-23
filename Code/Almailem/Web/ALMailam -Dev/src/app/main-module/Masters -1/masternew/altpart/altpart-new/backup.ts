import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { AltpartService } from '../altpart.service';

@Component({
  selector: 'app-altpart-new',
  templateUrl: './altpart-new.component.html',
  styleUrls: ['./altpart-new.component.scss']
})
export class AltpartNewComponent implements OnInit {

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
    private service :AltpartService ,
    private masterService: MasterService
    ) { }



  form = this.fb.group({
    companyCodeId: [],
    plantId: [],
    languageId: [],
    warehouseId: [this.auth.warehouseId,Validators.required],
    itemCode: [,Validators.required],
    altItemCode: [,Validators.required],
    subBarcode: [],
    manufacturer: [],
    brand: [],
    referenceField1: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    referenceField10: [],
    deletionIndicator: [],
    createdBy: [],
    createdOn: [],
    createdOnFE:[],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE:[],
  
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
console.log(this.js);
    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdOnFE.disable();
   
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.companyCodeId.patchValue(this.auth.companyId);
   this.form.controls.warehouseId.disable();
   
      this.dropdownlist();      
    
      if(this.js.pageflow=='basicDataNew'){
        this.form.controls.itemCode.patchValue(this.js.basicdataresult.itemCode);
        this.form.controls.itemCode.disable();
      }
      if (this.js.pageflow != 'basicDataNew' && this.js.pageflow != 'New') {
       this.form.controls.itemCode.disable();
      if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
      }

     if(this.js.pageflow=='basicDataEdit'){
     // this.form.controls.altItemCode.disable();
   
        this.fill();
       
      
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    if(this.js.pageflow!="Display"){ if(this.js.pageflow=='basicDataEdit3' || this.js.pageflow == 'basicDataEdit2' || this.js.pageflow =='basicDataEdit' || this.js.pageflow =='basicDataNew'){
     let obj: any = {};
    obj.companyCodeId = [this.js.basicdataresult.companyCodeId];
    obj.plantId = [this.js.basicdataresult.plantId];
   obj.languageId = [this.js.basicdataresult.languageId];
   obj.warehouseId = [this.js.basicdataresult.warehouseId];
   obj.itemCode=[this.js.basicdataresult.itemCode];
    this.sub.add(this.service.search(obj).subscribe(res => {
      if(res.length>0){
      this.form.patchValue(res[0], { emitEvent: false });
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
      }
      if(res.length==0){
        console.log(this.js.pageflow);
        this.js.pageflow='basicDataNew'
        console.log(this.js.pageflow);
        this.form.controls.itemCode.patchValue(this.js.basicdataresult.itemCode);
      }
    }))
  
  }
}
  else{
    let obj: any = {};
    obj.companyCodeId = [this.js.companyCodeId];
    obj.plantId = [this.js.plantId];
   obj.languageId = [this.js.languageId];
   obj.warehouseId = [this.js.warehouseId];
   obj.itemCode=[this.js.itemCode];
    this.sub.add(this.service.search(obj).subscribe(res => {
      this.form.patchValue(res[0], { emitEvent: false });
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
  }
    this.spin.hide();
  }
  floorList: any[] = [];
  levelList:any[]=[];
  storagesectionList: any[] = [];
  itemgroupList: any[] = [];
  warehouseIdList: any[] = [];
    partnercodeList: any[] = [];
    binclassList:any[]=[];
    aisleList:any[]=[];
    companyList: any[] = [];
    plantList: any[]=[];
    languageList: any[]=[];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
     this.cas.dropdownlist.setup.floorid.url,
        this.cas.dropdownlist.setup.levelid.url,
        this.cas.dropdownlist.setup.storagesectionid.url,
       this.cas.dropdownlist.setup.binclassid.url,
       this.cas.dropdownlist.setup.aisleid.url,
       this.cas.dropdownlist.setup.companyid.url,
       this.cas.dropdownlist.setup.plantid.url,
       this.cas.dropdownlist.setup.languageid.url,
      ]).subscribe((results) => {
     this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
      this.floorList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.floorid.key);
      this.levelList=this.cas.forLanguageFilter(results[2],this.cas.dropdownlist.setup.levelid.key);
   this.storagesectionList=this.cas.forLanguageFilter(results[3],this.cas.dropdownlist.setup.storagesectionid.key);
  this.binclassList=this.cas.forLanguageFilter(results[4],this.cas.dropdownlist.setup.binclassid.key);
  this.aisleList=this.cas.forLanguageFilter(results[5],this.cas.dropdownlist.setup.aisleid.key);
  this.companyList = this.cas.forLanguageFilter(results[6], this.cas.dropdownlist.setup.companyid.key);
  this.plantList = this.cas.forLanguageFilter(results[7], this.cas.dropdownlist.setup.plantid.key);
  this.languageList=this.cas.foreachlist2(results[8],this.cas.dropdownlist.setup.languageid.key);
 
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.companyCodeId.patchValue(this.auth.companyId);
    this.form.controls.warehouseId.disable();
    this.form.controls.companyCodeId.disable();
    this.form.controls.plantId.disable();
    this.form.controls.languageId.disable();
  
      });
      this.spin.hide();
    }
  
    onWarehouseChange(value){
      console.log(55);
      this.form.controls.companyCodeId.patchValue(value.companyCodeId);
      this.form.controls.languageId.patchValue(value.languageId);
      this.form.controls.plantId.patchValue(value.plantId);
      
     
    }
   
    Skip() {
   
      if(this.js.pageflow=='basicDataNew'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
      }
        if(this.js.pageflow=='basicDataEdit'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
          
        }
        if(this.js.pageflow=='basicDataEdit2'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
          
        }
        if(this.js.pageflow=='basicDataEdit3'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
          
        }
    }
    Back(){
      if(this.js.pageflow=='basicDataNew' || this.js.pageflow=='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow:'basicDataEdit2', basicdataresult: this.js.basicdataresult})
      
      
        this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
      
      }
      if(this.js.pageflow=='basicDataEdit' || this.js.pageflow=="basicDataEdit3"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow:'basicDataEdit3', basicdataresult: this.js.basicdataresult})
      
      
        this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
      
      }
  
      
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
  if (this.js.pageflow == "basicDataEdit") {

    if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit' && this.js.pageflow != "New") {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.form.controls.altItemCode.value,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId,this.js.basicdataresult.itemCode).subscribe(res => {
      this.toastr.success(res.altItemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow == "basicDataEdit"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit', basicdataresult: res});
        this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
        this.spin.hide();
      }
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }
  
  }
  if (this.js.pageflow == "basicDataEdit2") {

    if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit2' && this.js.pageflow != "New") {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.form.controls.altItemCode.value,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId,this.js.basicdataresult.itemCode).subscribe(res => {
      this.toastr.success(res.altItemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow == "basicDataEdit2"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2', basicdataresult: res});
        this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
        this.spin.hide();
      }
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }
 }
 if (this.js.pageflow == "basicDataEdit3") {

  if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit3' && this.js.pageflow != "New") {
  this.sub.add(this.service.Update(this.form.getRawValue(),this.form.controls.altItemCode.value,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId,this.js.basicdataresult.itemCode).subscribe(res => {
    this.toastr.success(res.altItemCode + " updated successfully!","Notification",{
      timeOut: 2000,
      progressBar: false,
    });
    if(this.js.pageflow == "basicDataEdit3"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2', basicdataresult: res});
      this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
      this.spin.hide();
    }

  }, err => {

    this.cs.commonerrorNew(err);
    this.spin.hide();

  }));
}
}
  else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.altItemCode + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow=="basicDataNew"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: res});

      this.router.navigate(['/main/masternew/startegyNew/' + paramdata]);
      this.spin.hide();
      }
      if(this.js.pageflow=="New"){
        this.router.navigate(['/main/masternew/altpart']);
      }
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}



