import { Component, OnInit } from '@angular/core';
import {FormArray, FormBuilder, FormControl,FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ItemtypeService } from '../../itemtype/itemtype.service';
import { ItemgroupService } from '../itemgroup.service';
import { MasterService } from 'src/app/shared/master.service';
import { MatDialog } from '@angular/material/dialog';
import { ItemGroupAddLinesComponent } from './item-group-add-lines/item-group-add-lines.component';

@Component({
  selector: 'app-itemgroup-new',
  templateUrl: './itemgroup-new.component.html',
  styleUrls: ['./itemgroup-new.component.scss']
})
export class ItemgroupNewComponent implements OnInit {

  warehouseidList: any[] = [];
  warehouseidDropdown: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    public auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService, public dialog: MatDialog,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : ItemgroupService,
    private masterService: MasterService,) { }



    form: FormGroup; 
  
    private createTableRow(): FormGroup {  
      return this.fb.group({  
        companyId: new FormControl(this.companyId),
        companyIdAndDescription:new FormControl(),
        createdBy: new FormControl(),
        createdOn: new FormControl(),
        deletionIndicator: new FormControl(),
        description: new FormControl(),
        itemGroupId: new FormControl(),
        itemTypeId: new FormControl(),
        itemTypeIdAndDescription: new FormControl(),
        languageId: new FormControl(),
        plantId: new FormControl(),
        plantIdAndDescription: new FormControl(),
        storageClassId: new FormControl(),
        storageSectionId: new FormControl(),
        subItemGroupId: new FormControl(),
        subItemGroupIdAndDescription: new FormControl(),
        updatedBy: new FormControl(),
        updatedOn: new FormControl(),
        warehouseId: new FormControl(),
        warehouseIdAndDescription: new FormControl(),
      });  
  }
  
  get tableRowArray(): FormArray {  
    return this.form.get('tableRowArray') as FormArray;  
  }
  
  onDeleteRow(rowIndex:number): void {  
    this.tableRowArray.removeAt(rowIndex);  
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
  warehouseId: any;
  languageId: any;
  plantId: any;
  companyId: any;
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
    this.warehouseId = (this.auth.warehouseId);  
    this.languageId = (this.auth.languageId);
    this.plantId = (this.auth.plantId);
    this.companyId = (this.auth.companyId);
    
  
      this.dropdownlist();
   
   this.createForm();
    if (this.js.pageflow != 'New') {
    
     if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
      
    }
  }

  createdBy: any;
  createdOnFE: any;
  updatedOnFE: any;
  updatedBy: any;

  sub = new Subscription();
  fill(){
    this.spin.show();
   this.sub.add(this.service.Get(this.js.warehouseId,this.js.itemTypeId,this.js.companyId,this.js.languageId,this.js.plantId).subscribe(res => {
    let  obj: any = {}

    obj.companyId = this.js.companyId;
    obj.itemTypeId = this.js.itemTypeId;
    obj.languageId = this.js.languageId;
    obj.plantId = this.js.plantId;
    obj.warehouseId = this.js.warehouseId;

   // this.sub.add(this.service.search(obj).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });

      this.createdOnFE = this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE = this.cs.dateTimeApi(res[0].updatedOn);
      this.createdBy = res[0].createdBy;
      this.updatedBy = res[0].updatedBy;
      
      this.languageId =res[0].languageId
      this.warehouseId = res[0].warehouseId;
      this.storageClassId =res[0].storageClassId;
      this.itemTypeId=res[0].itemTypeId;
      this.storageSectionId =res[0].storageSectionId;
     this.companyId =res[0].companyId;
      this.plantId =res[0].plantId; 


      res.forEach((element, index) => {
        if(index != res.length -1){
          this.addNewRow()
        }
        });
        
      this.form.get('tableRowArray')?.patchValue(res);
      this.resultTable = res;
      this.spin.hide();
    }))

  }
 
  currencyList: any[] = [];
  itemtypeList: any[] = [];
  stateList: any[] = [];
  cityList: any[] = [];
  countryList: any[] = [];
  languageidList: any[] = [];
  companyList: any[] = [];
    plantList: any[] = [];
  itemtypedesList:any[]=[];
  subitemgroupList: any[] = [];
  storagesectionList: any[] = [];
  itemgroupList: any[] = [];
  storageclassList: any[] = [];
  plantidList:any[]=[];

  itemTypeId: any;
  storageSectionId: any;
  storageClassId: any;

  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.itemtypeid.url,
      this.cas.dropdownlist.setup.itemgroupid.url,
      this.cas.dropdownlist.setup.storagesectionid.url,
      this.cas.dropdownlist.setup.storageclassid.url,
      this.cas.dropdownlist.setup.subitemgroupid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
  this.warehouseidList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
   this.itemgroupList=this.cas.forLanguageFilter(results[2],this.cas.dropdownlist.setup.itemgroupid.key);
 this.itemtypeList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.itemtypeid.key);
 this.storagesectionList=this.cas.forLanguageFilter(results[3],this.cas.dropdownlist.setup.storagesectionid.key);
 this.storageclassList=this.cas.forLanguageFilter(results[4],this.cas.dropdownlist.setup.storageclassid.key);
 this.subitemgroupList=this.cas.forLanguageFilter(results[5],this.cas.dropdownlist.setup.subitemgroupid.key);
 this.companyList = this.cas.forLanguageFilter(results[6], this.cas.dropdownlist.setup.companyid.key);
 this.plantList = this.cas.forLanguageFilter(results[7], this.cas.dropdownlist.setup.plantid.key);
 this.languageidList = this.cas.foreachlist2(results[8], this.cas.dropdownlist.setup.languageid.key);
  
 this.warehouseId = (this.auth.warehouseId);  
 this.languageId = (this.auth.languageId);
 this.plantId = (this.auth.plantId);
 this.companyId = (this.auth.companyId);

  this.masterService.searchitemtypeenter({companyId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
    languageId: this.auth.languageId}).subscribe(res=>{  this.itemtypeList = [];
      res.forEach(element => {
        this.itemtypeList.push({value: element.itemTypeId, label: element.itemTypeId + '-' + element.description});
      })
    });



    });
  
    this.spin.hide();
  }
 
  
  
  onItemtypeChange(value){
    this.masterService.searchitemgroup({companyCodeId:this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId,languageId: [this.auth.languageId], itemTypeId: [value.value]}).subscribe(res => {
      this.itemgroupList = [];
      res.forEach(element => {
        this.itemgroupList.push({value: element.itemGroupId, label: element.itemGroupId + '-' + element.itemGroup});
     })
    });
    this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId] ,plantId: [this.auth.plantId],warehouseId:[this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      })
    });
    this.masterService.searchstorageclass({companyCodeId:this.auth.companyId ,plantId:this.auth.plantId,warehouseId:this.auth.warehouseId, languageId:[this.auth.languageId],}).subscribe(res => {
      this.storageclassList = [];
      res.forEach(element => {
        this.storageclassList.push({value: element.storageClassId, label: element.storageClassId + '-' + element.description});
      })
    });
  
  }
   
   onItemgroupChange(value){
    this.masterService.searchsubitemgroup({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
      languageId: [this.auth.languageId], itemTypeId: [this.itemTypeId],itemGroupId:[value.value]}).subscribe(res => {
       this.subitemgroupList = [];
       res.forEach(element => {
         this.subitemgroupList.push({value: element.subItemGroupId, label: element.subItemGroupId + '-' + element.subItemGroup});
      })
     });
     this.masterService.searchitemgroup({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
      languageId: [this.auth.languageId], itemTypeId: [this.itemTypeId],itemGroupId:[value.value]}).subscribe(res => {
        this.form.controls.description.patchValue(res[0].itemGroup);
      
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
    // this.form.controls.tableRowArray.value.forEach(element => {
    
    //   element.warehouseId = this.warehouseId;
    //   element.languageId = this.languageId;
    //   element.companyId = this.companyId;
    //   element.plantId = this.plantId;
    //   element.itemTypeId = this.itemTypeId;
    //   element.storageSectionId = this.storageSectionId;
    //   element.storageClassId = this.storageClassId;
     
    // })

    this.resultTable.forEach(x => {
      console.log(this.companyId)
      x.warehouseId = this.warehouseId;
      x.languageId = this.languageId;
      x.companyId = this.companyId;
      x.plantId = this.plantId;
      x.itemTypeId = this.itemTypeId;
      x.storageSectionId = this.storageSectionId;
      x.storageClassId = this.storageClassId;
    });
  this.cs.notifyOther(false);
  this.spin.show();
  
  if (this.js.code) {
    this.sub.add(this.service.Update(this.resultTable,this.js.warehouseId,this.js.itemTypeId,this.js.companyId,this.js.languageId,this.js.plantId).subscribe(res => {
      this.toastr.success(res.itemGroupId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productsetup/itemgroup']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.resultTable).subscribe(res => {
      this.toastr.success(res.itemGroupId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/productsetup/itemgroup']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }


  removerow(x: any){
    this.tableRowArray.removeAt(x)
  }
  delete(i){ 
    //this.resultTable = this.resultTable.filter(val => val.description !== i.description );
    this.resultTable.splice(i, 1);
  }
   
  resultTable: any[] = [];
  openDialog(data: any = 'New', element: any = null, index: any = null): void {
    const dialogRef = this.dialog.open( ItemGroupAddLinesComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? element : null, itemTypeId: this.itemTypeId}
    });
  
    dialogRef.afterClosed().subscribe(result => {
   
        if(result.pageflow == 'New'){
          this.resultTable.push(result.data);
        }if(result.pageflow == 'Edit'){
          this.resultTable.splice(index, 1);
          this.resultTable.splice(2, 0, result.data);
        }
    });
  }
}

