
  import { Component, OnInit } from '@angular/core';
  import { FormBuilder, Validators, FormControl } from '@angular/forms';
  import { ActivatedRoute, Router } from '@angular/router';
  import { NgxSpinnerService } from 'ngx-spinner';
  import { ToastrService } from 'ngx-toastr';
  import { Subscription } from 'rxjs';
  import { CommonApiService } from 'src/app/common-service/common-api.service';
  import { CommonService } from 'src/app/common-service/common-service.service';
  import { AuthService } from 'src/app/core/core';
import { StorageSectionService } from '../storage-section.service';
import { MasterService } from 'src/app/shared/master.service';
  

  @Component({
    selector: 'app-storage-section-new',
    templateUrl: './storage-section-new.component.html',
    styleUrls: ['./storage-section-new.component.scss']
  })
  export class StorageSectionNewComponent implements OnInit {
    companyidDropdown: any;
    isLinear = false;
    constructor(private fb: FormBuilder,
      private auth: AuthService,
      public toastr: ToastrService,
      // private cas: CommonApiService,
      private spin: NgxSpinnerService,
      private route: ActivatedRoute, private router: Router,
      private cs: CommonService,
      private cas: CommonApiService,
      private service : StorageSectionService,
      private masterService: MasterService ) { }
  
  
  
    form = this.fb.group({
      companyId:  [, Validators.required],
      createdBy: [],
      createdOn: [],
      deletionIndicator: [],
      floorId:  [, Validators.required],
      itemGroup: [],
      itemType: [],
      languageId: [],
      noAisles: [],
      noRows: [],
      noShelves: [],
      noSpan: [],
      noStorageBins: [],
      plantId:  [, Validators.required],
      storageSectionId:  [, Validators.required],
      storageTypeNo: [],
      storageTypeId:[],
      subItemGroup: [],
      updatedBy: [],
      updatedOn: [],
      warehouseId:  [, Validators.required],
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
      this.form.controls.createdBy.disable();
      this.form.controls.updatedOn.disable();
      this.form.controls.createdOn.disable();
      if(this.auth.userTypeId != 1){
        this.form.controls.companyId.patchValue(this.auth.companyId);

    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
       this.form.controls.plantId.disable();
       this.form.controls.warehouseId.disable(); 
        this.dropdownlist();
        
      }
      else{
        this.superadmindropdownlist();
      }
      if (this.js.pageflow != 'New') {
        if (this.js.pageflow == 'Display')
          this.form.disable();
         this.fill();
      }
    }
    sub = new Subscription();
    fill(){
      this.spin.show();
      this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.floorId,this.js.plantId,this.js.companyId,this.js.languageId).subscribe(res => {
        this.form.patchValue(res, { emitEvent: false });
           this.form.controls.createdOn.patchValue(this.cs.dateapiutc0(this.form.controls.createdOn.value));
          this.form.controls.updatedOn.patchValue(this.cs.dateapiutc0(this.form.controls.updatedOn.value));
      }))
    }
    companyList: any[] = [];
    plantList: any[] = [];
    warehouseidList: any[] = [];
    floorList: any[] = [];
    storageList: any[] = [];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.companyid.url,
         this.cas.dropdownlist.setup.plantid.url,
         this.cas.dropdownlist.setup.warehouseid.url,
         this.cas.dropdownlist.setup.floorid.url,
         this.cas.dropdownlist.setup.storagesectionid.url,
      ]).subscribe((results) => {
      this.companyList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.companyid.key);
       this.plantList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.plantid.key);
       this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
       this.floorList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.floorid.key);
       this.floorList = this.cs.removeDuplicatesFromArrayNewstatus(this.floorList);
       this.storageList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.storagesectionid.key);
       if(this.auth.userTypeId != 1){
        this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
        this.form.controls.languageId.patchValue(this.auth.languageId);
        this.form.controls.plantId.patchValue(this.auth.plantId);
        this.form.controls.companyId.patchValue(this.auth.companyId);
        this.form.controls.companyId.disable();
      }
      this.spin.hide();
      }, (err) => {
        this.toastr.error(err, "");
        this.spin.hide();
      });
    }
    superadmindropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.floorid.url,
        this.cas.dropdownlist.setup.storagesectionid.url,
      ]).subscribe((results) => {
      this.companyidDropdown = results[0];
      this.companyidDropdown.forEach(element => {
        this.companyList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description, languageId: element.languageId});
      });
      this.plantList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.plantid.key);
      this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);
      this.floorList = this.cas.foreachlist2(results[3], this.cas.dropdownlist.setup.floorid.key);
      this.storageList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.storagesectionid.key)
    
      this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
     }
    oncompanyChange(value){
      this.form.controls.languageId.patchValue(value.languageId);
      
      this.masterService.searchPlant({companyCodeId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res=>{
        this.plantList=[];
        res.forEach(element=>{
          this.plantList.push({value:element.plantId,label:element.plantId+'-'+element.description})
        })
      })
     this.masterService.searchWarehouse({companyCodeId:value.value,languageId:[this.form.controls.languageId.value],plantId:this.form.controls.plantId.value}).subscribe(res=>{
      this.warehouseidList=[];
      res.forEach(element=>{
        this.warehouseidList.push({value:element.warehouseId,label:element.warehouseId+'-'+element.warehouseDesc})
      })
     })
     this.masterService.searchFloor({companyCodeId: [value.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.floorList = [];
      res.forEach(element => {
        this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
      })
    })
    this.masterService.searchstoragesection({companyCodeId: [value.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
      this.storageList = [];
      res.forEach(element => {
        this.storageList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    
    }
    
    onplantchange(value){
      this.form.controls.languageId.patchValue(value.languageId);
      this.masterService.searchWarehouse({plantId:value.value  ,companyCodeId:this.form.controls.companyId.value,languageId:[this.form.controls.languageId.value]}).subscribe(res=>{
        this.warehouseidList=[];
        res.forEach(element=>{
          this.warehouseidList.push({value:element.warehouseId,label:element.warehouseId+'-'+element.warehouseDesc})
        })
      })
      this.masterService.searchFloor({companyCodeId: [this.form.controls.companyId.value], plantId: [value.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.floorList = [];
        res.forEach(element => {
          this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        })
      })
      this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyId.value], plantId: [value.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
        this.storageList = [];
        res.forEach(element => {
          this.storageList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
        });
      });
    }
    onWarehouseChange(value){
     //this.form.controls.languageId.patchValue(value.languageId);
      this.masterService.searchFloor({companyCodeId: [this.form.controls.companyId.value], plantId: [this.form.controls.plantId.value], warehouseId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.floorList = [];
        res.forEach(element => {
          this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
      });
      this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyId.value],  plantId: [this.form.controls.plantId.value], warehouseId: [value.value], languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
        this.storageList = [];
        res.forEach(element => {
          this.storageList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
        });
      });
  }
  onfloorChange(value){
    this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyId.value],  plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value],floorId:[value.value]}).subscribe(res => {
      this.storageList = [];
      res.forEach(element => {
        this.storageList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
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
      this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code, this.js.warehouseId,this.js.floorId,this.js.plantId,this.js.companyId,this.js.languageId).subscribe(res => {
        this.toastr.success(res.storageSectionId + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/organisationsetup/storage']);
  
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
        this.router.navigate(['/main/organisationsetup/storage']);
        this.spin.hide();
    
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
    
      }));
     }
    
    }
  
  }
  