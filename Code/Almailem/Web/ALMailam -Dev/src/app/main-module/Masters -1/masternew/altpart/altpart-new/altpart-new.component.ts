import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControl, FormGroup, FormArray } from '@angular/forms';
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

    form: FormGroup;
    warehouseId: any;
    languageId: any;
    plantId: any;
    companyCodeId: any;
    itemCode: any;
      alternateUom:any;
      uomId:any;
    // id: any;
    createdOn: any;
    createdBy:any;
    updatedBy:any;
    updatedOn:any;
    createdOnFE:any;
    updatedOnFE:any;
    private createTableRow(): FormGroup {
      return this.fb.group({
    companyCodeId: new FormControl(),
    plantId: new FormControl(),
    languageId: new FormControl(),
    warehouseId: new FormControl(),
    itemCode: new FormControl(),
    altItemCode: new FormControl(),
    subBarcode: new FormControl(),
    manufacturer: new FormControl(),
    brand: new FormControl(),
    referenceField1: new FormControl(),
    referenceField2: new FormControl(),
    referenceField3: new FormControl(),
    referenceField4: new FormControl(),
    referenceField5: new FormControl(),
    referenceField6: new FormControl(),
    referenceField7: new FormControl(),
    referenceField8: new FormControl(),
    referenceField9: new FormControl(),
    referenceField10: new FormControl(),
    deletionIndicator: new FormControl(),
    createdBy: new FormControl(),
    createdOn: new FormControl(),
    createdOnFE:new FormControl(),
    updatedBy: new FormControl(),
    updatedOn: new FormControl(),
    updatedOnFE:new FormControl(),
  
  });
}
get tableRowArray(): FormArray {
  return this.form.get('tableRowArray') as FormArray;
}

onDeleteRow(rowIndex: number): void {
  this.tableRowArray.removeAt(rowIndex);
}

addNewRow(): void {
  this.tableRowArray.push(this.createTableRow());
}

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
  js: any = {}

  createForm(): void {
    this.form = this.fb.group({
      tableRowArray: this.fb.array([
        this.createTableRow()
      ])
    })
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }
 
  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
console.log(this.js);
   console.log(this.companySelection);
  
this.warehouseId = (this.auth.warehouseId);
this.languageId = (this.auth.languageId);
this.plantId = (this.auth.plantId);
this.companyCodeId = (this.auth.companyId);
if (this.js.pageflow == "Edit") {
  this.fill();
}
this.dropdownlist();
if (this.js.pageflow == 'basicDataNew') {
  this.itemCode = (this.js.basicdataresult.itemCode);
  //  this.itemCode.disable();
}
this.dropdownlist();


this.createForm();


if (this.js.pageflow != 'New' && this.js.pageflow != 'basicDataNew') {
  if (this.js.pageflow == 'Display')
    this.form.disable();
  this.fill();
}
if (this.js.pageflow == 'basicDataEdit'|| this.js.pageflow == 'basicDataEdit3') {

  this.fill();

}
  }
  sub = new Subscription();
  warehouseSelection: any;
  languageSelection: any;
  companySelection: any;
  plantSelection: any;
  fill(){
    this.spin.show();
    // this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    // this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    if(this.js.pageflow!="Display"){ if(this.js.pageflow=='basicDataEdit3' || this.js.pageflow == 'basicDataEdit2' || this.js.pageflow =='basicDataEdit' || this.js.pageflow =='basicDataNew' ){
     let obj: any = {};
    obj.companyCodeId = [this.js.basicdataresult.companyCodeId];
    obj.plantId = [this.js.basicdataresult.plantId];
   obj.languageId = [this.js.basicdataresult.languageId];
   obj.warehouseId = [this.js.basicdataresult.warehouseId];
   obj.itemCode=[this.js.basicdataresult.itemCode];
    this.sub.add(this.service.search(obj).subscribe(res => {
      if(res.length>0){
      this.form.patchValue(res[0], { emitEvent: false });
      this.warehouseSelection = res[0].warehouseId;
      this.companySelection = res[0].companyCodeId;
      this.plantSelection = res[0].plantId;
      this.itemCode = res[0].itemCode;
      this.createdOn=res[0].createdOn;
      this.createdBy=res[0].createdBy;
      this.updatedOn=res[0].updatedOn;
      this.createdOnFE=this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE=this.cs.dateTimeApi(res[0].updatedOn);
      this.updatedBy=res[0].updatedBy;
    
      }
      if(res.length==0){
        console.log(this.js.pageflow);
        this.js.pageflow='basicDataNew'
        console.log(this.js.pageflow);
        this.form.controls.itemCode.patchValue(this.js.basicdataresult.itemCode);
      }
      this.tableRowArray.patchValue(res);
    }))
  
  }
}
  if(this.js.pageflow == "Display" || this.js.pageflow == "Edit"){
    let obj: any = {};
    obj.companyCodeId = [this.js.companyCodeId];
    obj.plantId = [this.js.plantId];
   obj.languageId = [this.js.languageId];
   obj.warehouseId = [this.js.warehouseId];
   obj.itemCode=[this.js.itemCode];

    this.sub.add(this.service.search(obj).subscribe(res => {
      this.form.patchValue(res[0], { emitEvent: false });
      this.languageSelection = res[0].languageId;
      this.warehouseSelection = res[0].warehouseId;
      this.companySelection = res[0].companyCodeId;
      this.plantSelection = res[0].plantId;
      this.itemCode = res[0].itemCode;
      this.createdOn=res[0].createdOn;
      this.createdBy=res[0].createdBy;
      this.updatedOn=res[0].updatedOn;
      this.createdOnFE=this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE=this.cs.dateTimeApi(res[0].updatedOn);
      this.updatedBy=res[0].updatedBy;
      this.tableRowArray.patchValue(res);
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
 
  this.warehouseId = (this.auth.warehouseId);
  this.warehouseSelection = this.auth.warehouseId;
  this.languageId = (this.auth.languageId);
  this.languageSelection = this.auth.languageId;
  this.plantId = (this.auth.plantId);
  this.plantSelection = this.auth.plantId;
  this.companyCodeId = (this.auth.companyId);
  this.companySelection = this.auth.companyId;
  
      });
      this.spin.hide();
    }
  
    onWarehouseChange(value) {
      this.companyCodeId = (value.companyCodeId);
      this.languageId = (value.languageId);
      this.plantId = (value.plantId);
      this.warehouseId = (value.warehouseId);
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
    this.form.controls.tableRowArray.value.forEach(element => {
      console.log(this.itemCode)
      element.warehouseId = this.warehouseId;
      console.log(this.warehouseId)
      element.languageId = this.languageId;
      element.companyCodeId = this.companyCodeId;
      element.plantId = this.plantId;
      element.itemCode = this.itemCode;
      // element.uomId=this.uomId;
      // element.alternateUom=this.alternateUom;
      element.id = element.id ? element.id : null;
    })

  this.cs.notifyOther(false);
  this.spin.show();
  // this.form.controls.createdOn.patchValue(this.cs.day_callapiSearch(this.form.controls.createdOn.value));
  // this.form.controls.updatedOn.patchValue(this.cs.day_callapiSearch(this.form.controls.updatedOn.value));
  if (this.js.pageflow == 'Edit') {
    console.log(this.js);
    if ((this.js.code) && (this.js.pageflow == 'Edit') && (this.js.pageflow != "New")) {
      this.sub.add(this.service.Update(this.form.controls.tableRowArray.value,this.js.itemCode,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId).subscribe(res => {
        this.toastr.success(this.itemCode + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
  
        this.router.navigate(['/main/masternew/altpart']);
        this.spin.hide();
  
  
      }, err => {
  
        this.cs.commonerrorNew(err);
        this.spin.hide();
  
      }));
    }
  }
  if (this.js.pageflow == "basicDataEdit") {
    if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit' && this.js.pageflow != "New") {
    this.sub.add(this.service.Update(this.form.controls.tableRowArray.value,this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId).subscribe(res => {
      this.toastr.success(this.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow == "basicDataEdit"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit', basicdataresult: res[0]});
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
    this.sub.add(this.service.Update(this.form.controls.tableRowArray.value,this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId).subscribe(res => {
      this.toastr.success(this.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow == "basicDataEdit2"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2', basicdataresult: res[0]});
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
  this.sub.add(this.service.Update(this.form.controls.tableRowArray.value,this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId).subscribe(res => {
    this.toastr.success(this.itemCode + " updated successfully!","Notification",{
      timeOut: 2000,
      progressBar: false,
    });
    if(this.js.pageflow == "basicDataEdit3"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2', basicdataresult: res[0]});
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
    if(this.itemCode == null){
      this.toastr.error( "Please Enter Part No to Continue ","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      return
    }
    if ((this.js.pageflow == 'New') || (this.js.pageflow == 'basicDataNew') && (this.js.pageflow != 'Edit') && (this.js.pageflow != 'basicDataEdit') && (this.js.pageflow != 'basicDataEdit2') && (this.js.pageflow != 'basicDataEdit3')) {
    this.sub.add(this.service.Create(this.form.controls.tableRowArray.value).subscribe(res => {
      this.toastr.success(res.altItemCode + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow=="basicDataNew"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: res[0]});

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

}



