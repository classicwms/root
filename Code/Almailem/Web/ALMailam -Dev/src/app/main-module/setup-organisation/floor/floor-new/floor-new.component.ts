



  import { Component, OnInit } from '@angular/core';
  import { FormBuilder, Validators, FormControl } from '@angular/forms';
  import { ActivatedRoute, Router } from '@angular/router';
  import { NgxSpinnerService } from 'ngx-spinner';
  import { ToastrService } from 'ngx-toastr';
  import { Subscription } from 'rxjs';
  import { CommonApiService } from 'src/app/common-service/common-api.service';
  import { CommonService } from 'src/app/common-service/common-service.service';
  import { AuthService } from 'src/app/core/core';
import { FloorService } from '../floor.service';
import { MasterService } from 'src/app/shared/master.service';
  
@Component({
  selector: 'app-floor-new',
  templateUrl: './floor-new.component.html',
  styleUrls: ['./floor-new.component.scss']
})
export class FloorNewComponent implements OnInit {
  companyidDropdown: any;
    constructor(private fb: FormBuilder,
      private auth: AuthService,
      public toastr: ToastrService,
      // private cas: CommonApiService,
      private spin: NgxSpinnerService,
      private route: ActivatedRoute, private router: Router,
      private cs: CommonService,
      private cas: CommonApiService,
      private service : FloorService,
      private masterService: MasterService  ) { }
  
  
  
    form = this.fb.group({
 companyId:  [, Validators.required],
  createdBy: [],
  createdOn: [],
  createdOnFE:[],
  deletionIndicator: [],
  floorId:  [, Validators.required],
  languageId: [this.auth.languageId, Validators.required],
  plantId:  [, Validators.required],
  updatedBy: [],
  updatedOn: [],
  companyIdAndDescription:[],
  plantIdAndDescription:[],
  warehouseIdAndDescription:[],
  updatedOnFE:[],
  warehouseId:  [, Validators.required],
    });
  
    isLinear = false;
  
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
     
     
        this.dropdownlist();  
    
     this.form.controls.warehouseId.disable();
     this.form.controls.plantId.disable();
      if (this.js.pageflow != 'New') {
        this.form.controls.plantId.disable();
        this.form.controls.warehouseId.disable();
        this.form.controls.floorId.disable();
       if (this.js.pageflow == 'Display')
    
          this.form.disable();
         this.fill();
      }
    }
    sub = new Subscription();
    fill(){
      this.spin.show();
      this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.companyId,this.js.plantId,this.js.languageId).subscribe(res => {
        this.form.patchValue(res, { emitEvent: false });
          this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
          this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
      }))
    }
    companyList: any[] = [];
    plantList: any[] = [];
    languageidList: any[] = [];
    warehouseidList: any[] = [];
    floorList: any[] = [];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.companyid.url,
         this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.floorid.url,
        this.cas.dropdownlist.setup.languageid.url,
      ]).subscribe((results) => {
      this.companyList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.companyid.key);
       this.plantList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.plantid.key);
      this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
      this.floorList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.floorid.key);
      this.languageidList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.languageid.key);
        this.form.controls.companyId.patchValue(this.auth.companyId);
        this.form.controls.languageId.patchValue(this.auth.languageId);
        this.form.controls.languageId.disable();
        this.form.controls.companyId.disable();
        this.masterService.searchPlantenter({companyId: this.auth.companyId, languageId: this.auth.languageId}).subscribe(res => {
          this.plantList = [];
          res.forEach(element => {
            this.plantList.push({value: element.plantId, label: element.plantId + '-' + element.description});
            if(this.plantList.length == 1){
              this.form.controls.plantId.patchValue(this.plantList[0].value)
            }
          
          });
          
        });
        this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.form.controls.plantId.value}).subscribe(res => {
          this.warehouseidList = [];
          res.forEach(element => {
            this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
            if(this.warehouseidList.length != 0){
              this.form.controls.warehouseId.patchValue(this.warehouseidList[0].value)
            }
          });
        });
        this.masterService.searchFloor({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
          this.floorList = [];
           res.forEach(element => {
             this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
           });
         });
       
    
         this.spin.hide();
      }, (err) => {
        this.toastr.error(err, "");
        this.spin.hide();
      });
    }
   
  //   oncompanyChange(value){
  //     this.form.controls.languageId.patchValue(value.languageId);
  //     this.masterService.searchPlant({companyCodeId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res=>{
  //       this.plantList=[];
  //       res.forEach(element=>{
  //         this.plantList.push({value:element.plantId,label:element.plantId+'-'+element.description})
  //       })
  //     })
  //    this.masterService.searchWarehouse({companyCodeId:value.value,languageId:[this.form.controls.languageId.value],plantId:this.form.controls.plantId.value}).subscribe(res=>{
  //     this.warehouseidList=[];
  //     res.forEach(element=>{
  //       this.warehouseidList.push({value:element.warehouseId,label:element.warehouseId+'-'+element.warehouseDesc})
  //     })
  //    })
  //    this.masterService.searchFloor({companyCodeId: [value.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  //     this.floorList = [];
  //     res.forEach(element => {
  //       this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
  //     });
  //   });
      
    
  //   }
    
     onplantchange(value){
      this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.form.controls.plantId.value}).subscribe(res => {
        this.warehouseidList = [];
        res.forEach(element => {
          this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
          if(this.warehouseidList.length != 0){
            this.form.controls.warehouseId.patchValue(this.warehouseidList[0].value)
          }
        });
      });
      this.masterService.searchFloor({companyId: [this.form.controls.companyId.value], plantId: [value.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.auth.languageId]}).subscribe(res => {
        this.floorList = [];
         res.forEach(element => {
           this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
         });
       });
    }
    onWarehouseChange(value){
      this.masterService.searchFloor({companyId: [this.auth.companyId], plantId: [this.form.controls.plantId.value], warehouseId: [value.value], languageId: [this.auth.languageId]}).subscribe(res => {
       this.floorList = [];
        res.forEach(element => {
          this.floorList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
      });
   }
  onItemgroupChange(value){
    this.masterService.searchWarehouse({companyCodeId: [this.form.controls.companyId.value], warehouseId:[this.form.controls.warehouseId.value], plantId:[this.form.controls.plantId.value], 
     languageId: [this.form.controls.languageId.value],floorId:[value.value] }).subscribe(res => {
       this.form.controls.description.patchValue(res[0].description);
     
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
      this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code, this.js.warehouseId,this.js.companyId,this.js.plantId,this.js.languageId).subscribe(res => {
        this.toastr.success(res.floorId + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/organisationsetup/floor']);
  
        this.spin.hide();
    
      }, err => {
    
        this.cs.commonerrorNew(err);
        this.spin.hide();
    
      }));
    }else{
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.floorId + " Saved Successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/organisationsetup/floor']);
        this.spin.hide();
    
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
    
      }));
     }
    
    }
  
  }
  
