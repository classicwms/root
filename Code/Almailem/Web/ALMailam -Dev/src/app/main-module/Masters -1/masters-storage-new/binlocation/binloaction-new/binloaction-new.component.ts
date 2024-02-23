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
import { BinlocationService } from '../binlocation.service';
import { ReportsService } from 'src/app/main-module/reports/reports.service';

@Component({
  selector: 'app-binloaction-new',
  templateUrl: './binloaction-new.component.html',
  styleUrls: ['./binloaction-new.component.scss']
})
export class BinloactionNewComponent implements OnInit {

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
    private reportservice: ReportsService,
    private service : BinlocationService,
    private masterService: MasterService
    ) { }



  form = this.fb.group({
    aisleNumber: [],
  binBarcode: [],
  binClassId: [,],
  binSectionId: [],
  quantity:[],
  weight:[],
  blockReason:[],
  companyCodeId: [this.auth.companyId,],
  createdBy: [],
  createdOn: [],
  createdOnFE:[],
  remainingVolume:[],
  deletionIndicator: [],
  description: [],
  floorId: [,Validators.required],
  languageId: [this.auth.languageId,],
  pickingBlock: [],
  plantId: [this.auth.plantId,],
  putawayBlock: [],
  levelId:[],
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
  capacityUnit:['Volume',],
  capacityCheck:[],
  length:[],
  width:[],
  height:[],
  capacityUom:["CBM",],
  totalVolume:[],
  totalWeight:[],
  occupiedWeight:[],
  remainingWeight:[],
  totalQuantity:[],
  occupiedQuantity:[],
  remainingQuantity:[],
  occupiedVolume:[],
  rowId: [],
  shelfId: [],
  spanId: [],
  statusId: [],
  storageBin: [,Validators.required],
  storageSectionId: [],
  storageTypeId: [],
  updatedBy: [],
  updatedOn: [],
  updatedOnFE:[],
  warehouseId: [this.auth.warehouseId,Validators.required],
  
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

    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdOnFE.disable();
if(this.form.controls.occupiedVolume.value == null){
  this.form.controls.occupiedVolume.patchValue(0.0)
  console.log(true);
  console.log(this.form.controls.occupiedVolume.value);
}
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
      this.form.controls.warehouseId.disable();
      this.dropdownlist();
     if(this.js.pageflow != 'Display'){
      console.log(this.form.controls.capacityUnit.value);
      console.log(typeof this.form.controls.capacityUnit)
      if(this.form.controls.capacityUnit.value == 'Quantity'){
        console.log(this.form.controls.capacityUnit.value);
        console.log(1)
  
       this.form.controls.length.patchValue(null);
       this.form.controls.width.patchValue(null);
       this.form.controls.height.patchValue(null);
        this.form.controls.occupiedVolume.patchValue("");
        this.form.controls.remainingVolume.patchValue("");
        this.form.controls.totalVolume.patchValue("");
        this.form.controls.occupiedWeight.patchValue("");
        this.form.controls.remainingWeight.patchValue("");
        this.form.controls.totalWeight.patchValue("");
      }
     if(this.form.controls.capacityUnit.value == 'Weight'){
       this.form.controls.length.patchValue(null);
       this.form.controls.width.patchValue(null);
       this.form.controls.height.patchValue(null);
        this.form.controls.occupiedVolume.patchValue("");
        this.form.controls.remainingVolume.patchValue("");
        this.form.controls.totalVolume.patchValue("");
        this.form.controls.occupiedQuantity.patchValue("");
        this.form.controls.remainingQuantity.patchValue("");
        this.form.controls.totalQuantity.patchValue("");
      
      } 
      if(this.form.controls.capacityUnit.value == 'Volume'){
      
         this.form.controls.occupiedWeight.patchValue("");
         this.form.controls.remainingWeight.patchValue("");
         this.form.controls.totalWeight.patchValue("");
         this.form.controls.occupiedQuantity.patchValue("");
         this.form.controls.remainingQuantity.patchValue("");
         this.form.controls.totalQuantity.patchValue("");
       
       } 
     }
    if(this.js.pageflow == "New"){
      if(this.form.controls.capacityCheck.value == true){
          this.form.controls.capacityUnit.patchValue('Volume');
          this.form.controls.capacityUom.patchValue("CBM")
      }
    }
       if(this.js.pageflow != 'New'){
      
        //this.form.disable();
        this.fill();
       }
    if (this.js.pageflow != 'New') {
      this.form.controls.storageBin.disable();
      this.form.controls.warehouseId.patchValue(this.form.controls.warehouseId.value);
      //this.form.controls.putawayBlock.patchValue(this.form.controls.putawayBlock.value);
      if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
    }
  }
  sub = new Subscription();
  fill(){
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
      this.form.controls.statusId.patchValue(res.statusId != null ? res.statusId.toString() : '');
      this.form.controls.putawayBlock.patchValue(res.putawayBlock != null ? res.putawayBlock.toString() : '');
      this.form.controls.pickingBlock.patchValue(res.pickingBlock != null ? res.pickingBlock.toString() : '');
     this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
    this.spin.hide();
  }
  floorList: any[] = [];
  levelList:any[]=[];
  storagesectionList: any[] = [];
  itemgroupList: any[] = [];
  warehouseIdList: any[] = [];
    partnercodeList: any[] = [];
    binclassList:any[]=[];
    companyList: any[] = [];
    plantList: any[]=[];
    languageList: any[]=[];
    aisleList:any[]=[];
    uomList: any[]=[];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.floorid.url,

        this.cas.dropdownlist.setup.storagesectionid.url,
       this.cas.dropdownlist.setup.levelid.url,
     //  this.cas.dropdownlist.setup.binclassid.url,
      //this.cas.dropdownlist.setup.aisleid.url,
      this.cas.dropdownlist.setup.companyid.url,
       this.cas.dropdownlist.setup.plantid.url,
       this.cas.dropdownlist.setup.languageid.url,
     //  this.cas.dropdownlist.setup.uomid.url,
      ]).subscribe((results) => {
    this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
   this.floorList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.floorid.key);
   this.masterService.searchFloor({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
    this.floorList = [];
    res.forEach(element => {
      this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
    });
  });
   this.storagesectionList=this.cas.forLanguageFilter(results[2],this.cas.dropdownlist.setup.storagesectionid.key);
 this.levelList=this.cas.forLanguageFilter(results[3],this.cas.dropdownlist.setup.levelid.key);
this.masterService.searchlevel({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, warehouseId: this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
  this.levelList = [];
  res.forEach(element => {
    this.levelList.push({value: element.levelId, label: element.levelId + '-' + element.level});
  })
});

    this.masterService.searchbinclass({companyCodeId:this.form.controls.companyCodeId.value ,plantId: this.form.controls.plantId.value,warehouseId:this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.binclassList = [];
        res.forEach(element => {
          this.binclassList.push({value: element.binClassId, label: element.binClassId + '-' + element.binClass});
        })
      });
      this.masterService.searchaisle({companyCodeId:this.auth.companyId.value ,plantId: this.auth.plantId,warehouseId:this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
        this.aisleList = [];
        res.forEach(element => {
          this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
        })
      });
 //this.binclassList=this.cas.forLanguageFilter(results[4],this.cas.dropdownlist.setup.binclassid.key);
 //this.aisleList=this.cas.forLanguageFilter(results[5],this.cas.dropdownlist.setup.aisleid.key);
 this.companyList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.companyid.key);
 this.plantList = this.cas.forLanguageFilter(results[5], this.cas.dropdownlist.setup.plantid.key);
 this.languageList=this.cas.foreachlist2(results[6],this.cas.dropdownlist.setup.languageid.key);
 //this.uomList=this.cas.forLanguageFilter(results[8],this.cas.dropdownlist.setup.uomid.key);
 this.masterService.searchuom({companyCodeId:this.auth.companyId ,plantId: this.auth.plantId,warehouseId:this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
  this.uomList = [];
  res.forEach(element => {
    this.uomList.push({value: element.uomId, label: element.uomId + '-' + element.description});
  })
});
 this.uomList=this.cas.removeDuplicatesFromArrayNew(this.uomList);
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
 
 
   
    // onWarehouseChange(value){
    //   console.log(55);
    //   this.form.controls.companyCodeId.patchValue(value.companyCodeId);
    //   this.form.controls.languageId.patchValue(value.languageId);
    //   this.form.controls.plantId.patchValue(value.plantId);
      
    //   this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], warehouseId:[value.value], plantId:[this.form.controls.plantId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    //     this.floorList = [];
    //     res.forEach(element => {
    //       this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
    //     })
    //   });
    //   this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId:[value.value], languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
    //     this.storagesectionList = [];
    //     res.forEach(element => {
    //       this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
    //     })
    //   });
    //   this.masterService.searchlevel({companyCodeId:this.form.controls.companyCodeId.value ,plantId: this.form.controls.plantId.value,warehouseId:value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    //     this.levelList = [];
    //     res.forEach(element => {
    //       this.levelList.push({value: element.levelId, label: element.levelId + '-' + element.level});
    //     })
    //   });
    //   this.masterService.searchaisle({companyCodeId:this.form.controls.companyCodeId.value ,plantId: this.form.controls.plantId.value,warehouseId:value.value, languageId: [this.form.controls.languageId.value],storageSectionId:[this.form.controls.storageSectionId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
    //     this.aisleList = [];
    //     res.forEach(element => {
    //       this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
    //     })
    //   });
    //   this.masterService.searchbinclass({companyCodeId:this.form.controls.companyCodeId.value ,plantId: this.form.controls.plantId.value,warehouseId:value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    //     this.binclassList = [];
    //     res.forEach(element => {
    //       this.binclassList.push({value: element.binClassId, label: element.binClassId + '-' + element.binClass});
    //     })
    //   });
    // }
    onfloorChange(value){
      this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId:[this.auth.warehouseId], languageId: [this.auth.languageId],floorId:[value.value]}).subscribe(res => {
        this.storagesectionList = [];
        res.forEach(element => {
          this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
       })
      });
      this.masterService.searchaisle({companyCodeId:this.auth.companyId.value ,plantId: this.auth.plantId,warehouseId:this.auth.warehouseId, languageId: [this.auth.languageId],storageSectionId:[this.form.controls.storageSectionId.value],floorId:[value.value]}).subscribe(res => {
        this.aisleList = [];
        res.forEach(element => {
          this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
        })
      });
    }
    onStorageSectionChange(value){
      this.masterService.searchaisle({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, warehouseId:this.auth.warehouseId, languageId: [this.auth.languageId],floorId:[this.form.controls.floorId.value],storageSectionId:[value.value]}).subscribe(res => {
        this.aisleList = [];
        res.forEach(element => {
          this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
       })
      });
    }
    calculateVolume(){
      let volume =  this.form.controls.length.value * this.form.controls.width.value * this.form.controls.height.value;
      this.form.controls.totalVolume.patchValue(volume);
      let remVolume =  ((this.form.controls.totalVolume.value)-(this.form.controls.occupiedVolume.value));
     // console.log(typeof this.form.controls.occupiedVolume);
      this.form.controls.remainingVolume.patchValue(remVolume);
      console.log(volume);
      this.form.controls.occupiedWeight.patchValue("");
      this.form.controls.remainingWeight.patchValue("");
      this.form.controls.totalWeight.patchValue("");
      this.form.controls.occupiedQuantity.patchValue("");
      this.form.controls.remainingQuantity.patchValue("");
      this.form.controls.totalQuantity.patchValue("");
    }
    calculateQuantity(){
      let remQty =  ((this.form.controls.totalQuantity.value)-(this.form.controls.occupiedQuantity.value));
      this.form.controls.remainingQuantity.patchValue(remQty);
      this.form.controls.length.patchValue(null);
      this.form.controls.width.patchValue(null);
      this.form.controls.height.patchValue(null);
       this.form.controls.occupiedVolume.patchValue("");
       this.form.controls.remainingVolume.patchValue("");
       this.form.controls.totalVolume.patchValue("");
       this.form.controls.occupiedWeight.patchValue("");
       this.form.controls.remainingWeight.patchValue("");
       this.form.controls.totalWeight.patchValue("");
    }
    calculateWeight(){
      let remWt =  ((this.form.controls.totalWeight.value)-(this.form.controls.occupiedWeight.value));
      console.log(this.form.controls.occupiedWeight.value);
      this.form.controls.remainingWeight.patchValue(remWt);
      this.form.controls.length.patchValue(null);
      this.form.controls.width.patchValue(null);
      this.form.controls.height.patchValue(null);
       this.form.controls.occupiedVolume.patchValue("");
       this.form.controls.remainingVolume.patchValue("");
       this.form.controls.totalVolume.patchValue("");
       this.form.controls.occupiedQuantity.patchValue("");
       this.form.controls.remainingQuantity.patchValue("");
       this.form.controls.totalQuantity.patchValue("");
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
    this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId).subscribe(res => {
      this.toastr.success(res.storageBin + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/mastersStorageNew/binLocation']);

      this.spin.hide();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.storageBin + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/mastersStorageNew/binLocation']);
      this.spin.hide();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
   }
  
  }

}



