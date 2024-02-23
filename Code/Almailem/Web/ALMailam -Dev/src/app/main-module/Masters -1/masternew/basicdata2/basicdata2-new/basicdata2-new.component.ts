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
import { BasicdataService } from '../../basicdata/basicdata.service';
import { Basicdata2Service } from '../basicdata2.service';

@Component({
  selector: 'app-basicdata2-new',
  templateUrl: './basicdata2-new.component.html',
  styleUrls: ['./basicdata2-new.component.scss']
})
export class Basicdata2NewComponent implements OnInit {

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
    private service : Basicdata2Service,
    private masterService: MasterService
    ) { }



  form = this.fb.group({
    languageId: [],
    companyCodeId: [],
    plantId: [],
    warehouseId: [,Validators.required],
    itemCode: [,Validators.required],
    itemBarcode: [],
    length: [],
    width: [],
    height: [],
    dimensionUom: [],
    volume: [],
    volumeUom: [],
    qpc20Ft: [],
    qpc40Ft: [],
    unitPrice: [],
    currencyId:[],
    grossWeight: [],
    netWeight: [],
    weightUom: [],
    preferredStorageBin: [],
    photo:[],
    itemClass: [],
    crossDockingIndicator: [],
    statusId: [],
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
    updatedBy: [],
    updatedOn: [],
    createdOnFE:[],
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

   
console.log(this.js)

    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdOnFE.disable();
  
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
      this.dropdownlist();
      
    if(this.js.pageflow == 'basicDataNew'){
      this.form.controls.itemCode.patchValue(this.js.basicdataresult.itemCode);
      this.form.controls.itemCode.disable();
    }
    if (this.js.pageflow != 'basicDataNew' && this.js.pageflow != 'New') {
      this.form.controls.itemCode.disable();
    
      if (this.js.pageflow == 'Display'){
        this.form.disable();
       this.fill();
      }
      if(this.js.pageflow == "Edit"){
        this.fill();
      }
       if(this.js.pageflow == 'basicDataEdit'|| this.js.pageflow == 'basicDataEdit3' || this.js.pageflow =='basicDataEdit2' || this.js.pageflow =='basicDataNew' ){
         this.fill();
       }

    }
  }
  
  sub = new Subscription();
  fill(){
    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
   if(this.js.pageflow !='Display'){
    console.log(this.js);
    if(this.js.pageflow == 'basicDataEdit3' || this.js.pageflow == 'basicDataEdit2' || this.js.pageflow == 'basicDataEdit' || this.js.pageflow =='basicDataNew'  ){
    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.plantId = [this.js.basicdataresult.plantId];
   obj.languageId = [this.js.basicdataresult.languageId];
   obj.warehouseId = [this.js.basicdataresult.warehouseId];
   obj.itemCode=[this.js.basicdataresult.itemCode];
   this.sub.add(this.service.search(obj).subscribe(res => {
    console.log(res.length);
    if(res.length > 0){
    this.form.patchValue(res[0], { emitEvent: false });
    
    }
    if(res.length == 0){
      console.log(this.js.pageflow);
    this.js.pageflow ='basicDataNew';
      console.log(this.js.pageflow);
      console.log(this.js.basicdataresult);
      this.form.controls.itemCode.patchValue(this.js.basicdataresult.itemCode);
      
    }
 
  }))
    }
  }
  if(this.js.pageflow == "Edit" || this.js.pageflow == "Display"){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId,this.js.itemCode).subscribe(res => {
    this.form.patchValue(res,{ emitEvent: false });
     
    }
    
))
    
  }

    this.spin.hide();

  }
  currencyList: any[] = [];
  storagesectionList: any[] = [];
  itemgroupList: any[] = [];
  warehouseIdList: any[] = [];
    partnercodeList: any[] = [];
    companyList: any[] = [];
    plantList: any[]=[];
    languageList: any[]=[];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.currency.url,
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
       
      ]).subscribe((results) => {
    this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
    this.currencyList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.currency.key);
    this.companyList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.companyid.key);
    this.plantList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
    this.languageList=this.cas.foreachlist2(results[4],this.cas.dropdownlist.setup.languageid.key);
 
 
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
      console.log(value);
      this.form.controls.companyCodeId.patchValue(value.companyCodeId);
      this.form.controls.languageId.patchValue(value.languageId);
      this.form.controls.plantId.patchValue(value.plantId);
  
      this.masterService.searchcurrency({ languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.currencyList = [];
        res.forEach(element => {
          this.currencyList.push({value: element.currencyId, label: element.currencyId + '-' + element.currencyDescription});
        })
      });
    }
    Skip(){
      if(this.js.pageflow=='basicDataNew'){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: this.js.basicdataresult});
      this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
    }
      if(this.js.pageflow=='basicDataEdit'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit',basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
        
      }
      if(this.js.pageflow=='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2',basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
        
      }
      if(this.js.pageflow=='basicDataEdit3'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3',basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
        
      }
      
     }
     Back(){
    if(this.js.pageflow=='basicDataNew' || this.js.pageflow=='basicDataEdit2'){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow:'basicDataEdit2', basicdataresult: this.js.basicdataresult})
    
    
      this.router.navigate(['/main/masternew/basicdataNew/' + paramdata]);
    
    }
    if(this.js.pageflow=='basicDataEdit' || this.js.pageflow=="basicDataEdit3"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow:'basicDataEdit3', basicdataresult: this.js.basicdataresult})
    
    
      this.router.navigate(['/main/masternew/basicdataNew/' + paramdata]);
    
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
  if(this.js.pageflow=='basicDataEdit' ){
  if (this.js.basicdataresult.itemCode && (this.js.pageflow =='basicDataEdit' )&&this.js.pageflow!="New"  ) {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId,this.js.basicdataresult.itemCode).subscribe(res => {
      this.toastr.success(res.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
    
      if(this.js.pageflow =="basicDataEdit"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit', basicdataresult: res});
        this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
        this.spin.hide();
      }
    }, err => {
  
      this.cs.commonerrorNew(err);
      
  this.spin.hide();
    }));
  
  } 
  
}
if(this.js.pageflow=='basicDataEdit3'){
  if (this.js.basicdataresult.itemCode && this.js.pageflow =='basicDataEdit3' &&this.js.pageflow!="New" ) {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId,this.js.basicdataresult.itemCode).subscribe(res => {
      this.toastr.success(res.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
    
      if(this.js.pageflow =="basicDataEdit3"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3', basicdataresult: res});
        this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
        this.spin.hide();
      }
    }, err => {
  
      this.cs.commonerrorNew(err);
      
  this.spin.hide();
    }));
  
  } 
}
if(this.js.pageflow=='Edit'){
  if (this.js.itemCode && this.js.pageflow =='Edit' &&this.js.pageflow!="New" ) {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.itemCode,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId,this.js.itemCode).subscribe(res => {
      this.toastr.success(res.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/masternew/basicdata2' ]);
    this.spin.hide();
    
    }, err => {
  
      this.cs.commonerrorNew(err);
      
  this.spin.hide();
    }));
  
  } 
}
if(this.js.pageflow=="basicDataEdit2"){
  this.sub.add(this.service.Update(this.form.getRawValue(),this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId,this.js.basicdataresult.itemCode).subscribe(res => {
    this.toastr.success(res.itemCode + " updated successfully!","Notification",{
      timeOut: 2000,
      progressBar: false,
    });
  
 
    if(this.js.pageflow != 'New' && this.js.pageflow!='basicDataNew'){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2', basicdataresult: res});
      this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
      this.spin.hide();
    }
    
   

    this.spin.hide();

  }
  , err => {

    this.cs.commonerrorNew(err);
    this.spin.hide();

  }));
}

if(this.js.pageflow=='New' || this.js.pageflow=='basicDataNew'){
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.itemCode + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
 
      });
      if(this.js.pageflow=="basicDataNew"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: res});
      this.router.navigate(['/main/masternew/altuomNew/' + paramdata]);
      this.spin.hide();
      }
      if(this.js.pageflow=="New"){
        this.router.navigate(['/main/masternew/basicdata2']);
      }
if(this.js.pageflow=="Display"){
  let paramdata = "";
  paramdata = this.cs.encrypt({pageflow: 'Display', res: this.js.basicdataresult})
  this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
}

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}


