import { Component, OnInit } from '@angular/core';
import {  FormArray,
  FormBuilder,
  FormControl,FormGroup,
  Validators} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ImvariantService } from '../imvariant.service';
import { MatDialog } from '@angular/material/dialog';
import { AddlinesComponent } from '../addlines/addlines.component';

@Component({
  selector: 'app-imvariant-new',
  templateUrl: './imvariant-new.component.html',
  styleUrls: ['./imvariant-new.component.scss']
})
export class ImvariantNewComponent implements OnInit {

  warehouseidDropdown: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    public dialog: MatDialog,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : ImvariantService,
    private masterService: MasterService
    ) { }
    warehouseId: any;
    languageId: any;
    plantId: any;
    companyCodeId: any;
    itemCode: any;
    variantCode: any;
    variantType: any;
    variantSubCode: any;
    createdOn: any;
    createdBy:any;
    updatedBy:any;
    updatedOn:any;
    createdOnFE:any;
    id:any;
    updatedOnFE:any;
    warehouseSelection: any;
    languageSelection: any;
    companySelection: any;
    plantSelection: any;


    form: FormGroup;
    resultTable : any[] = [];


 private createTableRow(): FormGroup {
      return this.fb.group({
    companyCodeId: new FormControl(this.companyCodeId),
  companyIdAndDescription: new FormControl(),
  createdBy: new FormControl(),
  createdOn: new FormControl(),
  deletionIndicator: new FormControl(),
  languageId: new FormControl(this.languageId),
  plantId: new FormControl(this.plantId),
  plantIdAndDescription: new FormControl(),
  referenceField1: new FormControl(),
  referenceField10: new FormControl(),
  referenceField2: new FormControl(),
  referenceField3: new FormControl(),
  referenceField4: new FormControl(),
  referenceField5: new FormControl(),
  referenceField6: new FormControl(),
  referenceField7: new FormControl(),
  referenceField8: new FormControl(),
  referenceField9: new FormControl(),
  specificationFrom: new FormControl(),
  specificationTo: new FormControl(),
  specificationUom:new FormControl(),
  updatedBy: new FormControl(),
  itemCode:new FormControl(),
  updatedOn: new FormControl(),
  variantCode: new FormControl(),
  variantSubCode: new FormControl(),
  variantSubType: new FormControl(),
  variantText: new FormControl(),
  variantType: new FormControl(),
  warehouseId: new FormControl(this.warehouseId),
  createdOnFE:new FormControl(),
  updatedOnFE: new FormControl(),
  warehouseIdAndDescription: new FormControl(),
  
  });
}

  get tableRowArray(): FormArray {
    return this.form.get('tableRowArray') as FormArray;
  }

  onDeleteRow(rowIndex: number): void {
    this.tableRowArray.removeAt(rowIndex);
  }

  delete(i){ 
    //this.resultTable = this.resultTable.filter(val => val.description !== i.description );
    this.resultTable.splice(i, 1);
//(this.js.deletionIndicator)=1;
  }
   
  addNewRow(): void {
    this.tableRowArray.push(this.createTableRow());
  }

  createForm(): void {
    this.form = this.fb.group({
      tableRowArray: this.fb.array([
        this.createTableRow()
      ])
    })
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
  public errorHandling = (control:  string, error:  string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }
  js: any = {};

  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
console.log(this.js);
    // this.form.controls.updatedBy.disable();
    // this.form.controls.createdBy.disable();
    // this.form.controls.updatedOnFE.disable();
    // this.form.controls.createdOnFE.disable();
    if (this.js.pageflow == "Edit") {
      this.fill();
    }
       
  
    this.warehouseId = (this.auth.warehouseId);  
    this.languageId = (this.auth.languageId);
    this.plantId = (this.auth.plantId);
    this.companyCodeId = (this.auth.companyId);
    
  
      this.dropdownlist();
   
   this.createForm();
   
      if(this.js.pageflow=='basicDataNew'){
        this.itemCode=(this.js.basicdataresult.itemCode);
       // this.form.controls.itemCode.disable();
      }
      if (this.js.pageflow != 'basicDataNew' && this.js.pageflow != 'New') {
      // this.form.controls.itemCode.disable();
      if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
      }

     if(this.js.pageflow=='basicDataEdit'){
   
        this.fill();
       
      
    }
  }
  sub = new Subscription();
  fill() {
    this.spin.show();
    let obj: any = {};
    if(this.js.pageflow!="Display"){
      if(this.js.pageflow=='basicDataNew' || this.js.pageflow =='basicDataEdit2' || this.js.pageflow =='basicDataEdit3' || this.js.pageflow =='basicDataEdit'){
    obj.companyCodeId = [this.js.basicdataresult.companyCodeId];
    obj.plantId = [this.js.basicdataresult.plantId];
    obj.languageId = [this.js.basicdataresult.languageId];
    obj.warehouseId = [this.js.basicdataresult.warehouseId];
    obj.itemCode = [this.js.basicdataresult.itemCode];
    this.sub.add(this.service.search(obj).subscribe(res => {
      console.log(this.js.pageflow);
      console.log(res.length);
      if(res.length>0){
      this.languageSelection = res[0].languageId
      this.warehouseSelection = res[0].warehouseId;
      this.companySelection = res[0].companyCodeId;
      this.plantSelection = res[0].plantId;
      this.itemCode = res[0].itemCode;
      this.variantCode = res[0].variantCode;
      this.variantType = res[0].variantType;
      this.variantSubCode = res[0].variantSubCode;
      this.createdOn=res[0].createdOn;
      this.createdBy=res[0].createdBy;
      this.updatedOn=res[0].updatedOn;
      this.createdOnFE=this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE=this.cs.dateTimeApi(res[0].updatedOn);
      this.updatedBy=res[0].updatedBy;
      this.tableRowArray.patchValue(res);
      }
      if(res.length==0){
        console.log(this.js.pageflow);
        this.js.pageflow='basicDataNew'
        console.log(this.js.pageflow);
      }
      this.resultTable = res;
    }))
  }
  }
  if(this.js.pageflow == "Edit" || this.js.pageflow =="Display"){
    obj.companyCodeId = [this.js.companyCodeId];
    obj.plantId = [this.js.plantId];
    obj.languageId = [this.js.languageId];
    obj.warehouseId = [this.js.warehouseId];
    obj.itemCode = [this.js.code];
    this.sub.add(this.service.search(obj).subscribe(res => {
      this.languageSelection = res[0].languageId
      this.warehouseSelection = res[0].warehouseId;
      this.companySelection = res[0].companyCodeId;
      this.plantSelection = res[0].plantId;
      this.itemCode = res[0].itemCode;
      this.variantCode = res[0].variantCode;
      this.variantType = res[0].variantType;
      this.variantSubCode = res[0].variantSubCode;
      this.createdOn=res[0].createdOn;
      this.createdBy=res[0].createdBy;
      this.updatedOn=res[0].updatedOn;
      this.createdOnFE=this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE=this.cs.dateTimeApi(res[0].updatedOn);
      this.updatedBy=res[0].updatedBy;

    

      this.tableRowArray.patchValue(res);
      this.resultTable = res;
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
    varianttList:any[]=[];
    variantList:any[]=[];
    variantsubList:any[]=[];
    companyList: any[] = [];
    plantList: any[]=[];
    languageList: any[]=[];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.variantid.url,
        this.cas.dropdownlist.setup.variantType.url,
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
      ]).subscribe((results) => {
     this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
      this.variantList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.variantid.key);
      this.varianttList=this.cas.forLanguageFilter(results[2],this.cas.dropdownlist.setup.variantType.key);
      this.companyList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.plantid.key);
      this.languageList=this.cas.foreachlist2(results[5],this.cas.dropdownlist.setup.languageid.key);
      this.warehouseId = (this.auth.warehouseId);
      this.languageId = (this.auth.languageId);
      this.plantId = (this.auth.plantId);
      this.companyCodeId = (this.auth.companyId);
        this.masterService.searchvariant({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
          this.variantList = [];
          res.forEach(element => {
            this.variantList.push({value: element.variantCode, label: element.variantCode + '-' + element.variantText});
          })
        });
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
   
 
    Skip() {
   
      if(this.js.pageflow=='basicDataNew'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
      }
        if(this.js.pageflow=='basicDataEdit'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
          
        }
        if(this.js.pageflow=='basicDataEdit2'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
          
        }
        if(this.js.pageflow=='basicDataEdit3'){
          let paramdata = "";
          paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3',basicdataresult: this.js.basicdataresult});
          this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
          
        }
    }
    Back(){
      if(this.js.pageflow=='basicDataNew' || this.js.pageflow=='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow:'basicDataEdit2', basicdataresult: this.js.basicdataresult})
      
      
        this.router.navigate(['/main/masternew/batchserialNew/' + paramdata]);
      
      }
      if(this.js.pageflow=='basicDataEdit' || this.js.pageflow=="basicDataEdit3"){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow:'basicDataEdit3', basicdataresult: this.js.basicdataresult})
      
      
        this.router.navigate(['/main/masternew/batchserialNew/' + paramdata]);
      
      }
  
      
       }
  submit() {
    console.log(this.form);
    console.log(this.resultTable);
    
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
    // this.form.controls.tableRowArray.value.forEach(element => {
    //   element.warehouseId = this.warehouseId;
    //   element.languageId = this.languageId;
    //   element.companyCodeId = this.companyCodeId;
    //   element.plantId = this.plantId;
    //   element.itemCode = this.itemCode;
    // })

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
      this.sub.add(this.service.Update(this.resultTable,this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId).subscribe(res => {
        this.toastr.success(this.js.code + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
  
        this.router.navigate(['/main/masternew/imvariant']);
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
      this.toastr.success(this.js.basicdataresult.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow =='basicDataEdit'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit', basicdataresult: res[0]});
        this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
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
      this.toastr.success(this.js.basicdataresult.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow =='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2', basicdataresult: res[0]});
        this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
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
      this.toastr.success(this.js.basicdataresult.itemCode + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      if(this.js.pageflow =='basicDataEdit3'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3', basicdataresult: res[0]});
        this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
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

      this.router.navigate(['/main/masternew/palletizationNew/' + paramdata]);
      this.spin.hide();
      }
      if(this.js.pageflow=="New"){
        this.router.navigate(['/main/masternew/imvariant']);
      }
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  }
  }
  openDialog(data: any = 'New', element: any = null, index: any = null): void {
    const dialogRef = this.dialog.open( AddlinesComponent, {
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





