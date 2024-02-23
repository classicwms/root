import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ImpalletizationService } from '../impalletization.service';
import { MatDialog } from '@angular/material/dialog';
import { PalletaddlinesComponent } from '../palletaddlines/palletaddlines.component';








@Component({
  selector: 'app-impalletization-new',
  templateUrl: './impalletization-new.component.html',
  styleUrls: ['./impalletization-new.component.scss']
})
export class ImpalletizationNewComponent implements OnInit {
  warehouseId: any;
  languageId: any;
  plantId: any;
  companyCodeId: any;
  warehouseidDropdown: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public dialog: MatDialog,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : ImpalletizationService,
    private masterService: MasterService
    )  { 
      
    }

   
    form: FormGroup;
   
    itemCode: any;
      alternateUom:any;
      uomId:any;
    // id: any;
    createdOn: any;
    createdBy:any;
    updatedBy:any;
    height:any;
    width:any;
    length:any;
    uom:any;
    perquantity:any;
    updatedOn:any;
    createdOnFE:any;
    updatedOnFE:any;
    private createTableRow(): FormGroup {
      return this.fb.group({
        caseDimensionUom: new FormControl(),
        caseHeight: new FormControl(),
        caseLength:  new FormControl(),
        caseWidth:  new FormControl(),
        casesPerPallet:  new FormControl(),
        companyCodeId:  new FormControl(),
        deletionIndicator:  new FormControl(),
        itemCaseQuantity:  new FormControl(),
        itemCode:  new FormControl(),
        itemPerPalletQuantity:  new FormControl(),
        languageId:  new FormControl(),
        palletDimensionUom:  new FormControl(),
        palletHeight:  new FormControl(),
        palletLength:  new FormControl(),
        palletWidth:  new FormControl(),
        palletizationIndicator: new FormControl(),
        palletizationLevel:  new FormControl(),
        plantId:  new FormControl(),
        referenceField1:  new FormControl(),
        referenceField10:  new FormControl(),
        referenceField2:  new FormControl(),
        referenceField3:  new FormControl(),
        referenceField4:  new FormControl(),
        referenceField5:  new FormControl(),
        referenceField6:  new FormControl(),
        referenceField7:  new FormControl(),
        referenceField8:  new FormControl(),
        referenceField9:  new FormControl(),
        statusId:  new FormControl(),
        warehouseId:  new FormControl(),
  
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
resultTable : any[] = [];
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
 
}
this.createForm();


if (this.js.pageflow != 'New' && this.js.pageflow != 'basicDataNew') {
  if (this.js.pageflow == 'Display')
    this.form.disable();
  this.fill();
}
if (this.js.pageflow == 'basicDataEdit'|| this.js.pageflow == 'basicDataEdit3' || this.js.pageflow =='basicDataEdit2' || this.js.pageflow =='basicDataNew') {

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
    if(this.js.pageflow!="Display"){ 
  if(this.js.pageflow=='basicDataEdit3' || this.js.pageflow == 'basicDataEdit2' || this.js.pageflow =='basicDataEdit' || this.js.pageflow =='basicDataNew' ){
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
       this.itemCode=(this.js.basicdataresult.itemCode);
      
      }
      console.log(res);
    this.resultTable=res;
    }))
  
  }
}
  if(this.js.pageflow=='Display' || this.js.pageflow == 'Edit'){
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
      console.log(res);
      console.log(this.tableRowArray);
    this.tableRowArray.patchValue(res);
    this.resultTable=res;
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
    palletizationlevelList:any[]=[];
  
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.palletizationlevelid.url,
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
      ]).subscribe((results) => {
     this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
     // this.palletizationlevelList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.palletizationlevelid.key);
     this.masterService.searchpalletization({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.palletizationlevelList = [];
      res.forEach(element => {
        this.palletizationlevelList.push({value: element.palletizationLevelId, label: element.palletizationLevelId + '-' + element.palletizationLevel});
      })
    });
      this.companyList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
      this.languageList=this.cas.foreachlist2(results[4],this.cas.dropdownlist.setup.languageid.key);
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
        this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
      }
        if(this.js.pageflow=='basicDataEdit'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
          
        }
        if(this.js.pageflow=='basicDataEdit2'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
          
        }
        if(this.js.pageflow=='basicDataEdit3'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
          
        }
    }''
    Back(){
      if(this.js.pageflow== 'basicDataNew' || this.js.pageflow=='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow:'basicDataEdit2', basicdataresult: this.js.basicdataresult})
      
      
        this.router.navigate(['/main/masternew/imvariantNew/' + paramdata]);
      
      }
      if(this.js.pageflow== 'basicDataEdit' || this.js.pageflow=="basicDataEdit3"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow:'basicDataEdit3', basicdataresult: this.js.basicdataresult})
      
      
        this.router.navigate(['/main/masternew/imvariantNew/' + paramdata]);
      
      }
  
      
       }
       delete(i){ 
        //this.resultTable = this.resultTable.filter(val => val.description !== i.description );
        this.resultTable.splice(i, 1);
    //(this.js.deletionIndicator)=1;
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
    this.resultTable.forEach(x => {
      x.warehouseId = this.warehouseId;
      x.languageId = this.languageId;
      x.companyCodeId = this.companyCodeId;
      x.plantId = this.plantId;
      x.itemCode = this.itemCode;
      x.id = x.id ? x.id : null;
    });

  this.cs.notifyOther(false);
  this.spin.show();
  // this.form.controls.createdOn.patchValue(this.cs.day_callapiSearch(this.form.controls.createdOn.value));
  // this.form.controls.updatedOn.patchValue(this.cs.day_callapiSearch(this.form.controls.updatedOn.value));
  if (this.js.pageflow == 'Edit') {
    console.log(this.js);
    if ((this.js.code) && (this.js.pageflow == 'Edit') && (this.js.pageflow != "New")) {
      this.sub.add(this.service.Update(this.resultTable,this.js.itemCode,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId).subscribe(res => {
        this.toastr.success(this.itemCode + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
  
        this.router.navigate(['/main/masternew/palletization']);
        this.spin.hide();
  
  
      }, err => {
  
        this.cs.commonerrorNew(err);
        this.spin.hide();
  
      }));
    }
  }
  if(this.js.pageflow=="basicDataEdit"){
    if (this.js.basicdataresult.itemCode && this.js.pageflow =='basicDataEdit'&&this.js.pageflow!="New") {
    this.sub.add(this.service.Update(this.resultTable,this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId).subscribe(res => {
      this.toastr.success(this.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow =='basicDataEdit'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit', basicdataresult: res[0]});
        this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
        this.spin.hide();
      }
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }
  }
  if(this.js.pageflow=="basicDataEdit2"){
    if (this.js.basicdataresult.itemCode && this.js.pageflow =='basicDataEdit2'&&this.js.pageflow!="New") {
    this.sub.add(this.service.Update(this.resultTable,this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId).subscribe(res => {
      this.toastr.success(this.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow =='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit', basicdataresult: res[0]});
        this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
        this.spin.hide();
      }
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }
  }
  if(this.js.pageflow=="basicDataEdit3"){
    if (this.js.basicdataresult.itemCode && this.js.pageflow =='basicDataEdit3'&&this.js.pageflow!="New") {
    this.sub.add(this.service.Update(this.resultTable,this.js.basicdataresult.itemCode,this.js.basicdataresult.warehouseId,this.js.basicdataresult.languageId,this.js.basicdataresult.plantId,this.js.basicdataresult.companyCodeId).subscribe(res => {
      this.toastr.success(this.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow =='basicDataEdit3'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3', basicdataresult: res[0]});
        this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
        this.spin.hide();
      }
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }
  }
  if(this.itemCode == null){
    this.toastr.error( "Please Enter Part No to Continue ","Notification",{
      timeOut: 2000,
      progressBar: false,
    });
    this.spin.hide();
    return
  }
  if(this.itemCode != null){
  if(this.js.pageflow=='New' || this.js.pageflow=='basicDataNew'){
    this.sub.add(this.service.Create(this.resultTable).subscribe(res => {
      this.toastr.success(this.itemCode + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow=="basicDataNew"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: res[0]});

      this.router.navigate(['/main/masternew/altpartNew/' + paramdata]);
      this.spin.hide();
      }
      if(this.js.pageflow=="New"){
        this.router.navigate(['/main/masternew/palletization']);
      }
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  }
  }
  openDialog(data: any = 'New', element: any = null, index: any = null): void {
    const dialogRef = this.dialog.open( PalletaddlinesComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? element : null}
    });
  
    dialogRef.afterClosed().subscribe(result => {
   
        if(result.pageflow == 'New'){
          this.resultTable.push(result.data);
        }if(result.pageflow == 'Edit'){
          console.log(this.resultTable)
          this.resultTable.splice(index, 1);
          this.resultTable.splice(2, 0, result.data);
        }
    });
  }
}







