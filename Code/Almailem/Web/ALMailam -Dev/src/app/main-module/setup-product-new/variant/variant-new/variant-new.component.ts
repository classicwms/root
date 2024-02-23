import { Component, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl,FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { VariantService } from '../variant.service';
import { MasterService } from 'src/app/shared/master.service';
import { BatchserialService } from '../../batchserial/batchserial.service';

@Component({
  selector: 'app-variant-new',
  templateUrl: './variant-new.component.html',
  styleUrls: ['./variant-new.component.scss']
})
export class VariantNewComponent implements OnInit {
  warehouseidDropdown: any;
  warehouseidList: any[] = [];
  isLinear = false;
  batch: any;
  selectedbatch: any;
  warehouseId: any;
  languageId: any;
  plantId: any;
  companyId: any;
  itemCode: any;
  levelId: any;

  storageMethodList: any[] = [];
  maintainanceList: any[] = [];
  variantTypeList: any[] = [];

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService, private masterService: MasterService,
    private service: VariantService) {
      this.storageMethodList = [{'storageMethodId': 'Batch', 'description': 'Batch'},{'storageMethodId': 'Serial', 'description': 'Serial'},{'storageMethodId': 'Not Applicable', 'description': 'Not Applicable'}];
      this.maintainanceList = [{'maintainanceId': 'Internal', 'description': 'Internal'},{'maintainanceId': 'External', 'description': 'External'}]
  }


  form: FormGroup;

  public levelReferences: FormGroup;
  private createTableRow(): FormGroup {
    return this.fb.group({
      companyId: new FormControl(this.companyId),
      deletionIndicator: new FormControl(),
      description: new FormControl(),
      languageId: new FormControl(this.languageId, ),
      levelId: new FormControl(),
      levelReference: new FormControl(),
      id: new FormControl(),
      plantId: new FormControl(this.plantId, ),
      variantId: new FormControl(),
      variantSubId: new FormControl(),
      warehouseId:new FormControl(this.warehouseId, )
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


  js: any = {}

  createForm(): void {
    this.form = this.fb.group({
      tableRowArray: this.fb.array([
        this.createTableRow()
      ])
    })
  }

  createdBy: any;
  createdOn: any;
  updatedOn: any;
  updatedBy: any;

  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);

    this.warehouseId = (this.auth.warehouseId);
      this.languageId = (this.auth.languageId);
      this.plantId = (this.auth.plantId);
      this.companyId = (this.auth.companyId);


    this.dropdownlist();
    if (this.js.pageflow == 'basicDataNew') {
      this.itemCode = (this.js.result.itemCode);
      //  this.itemCode.disable();
    }
    this.dropdownlist();


    this.createForm();

    if (this.js.pageflow != 'New' && this.js.pageflow != 'basicDataNew') {
      if (this.js.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }
  }




  sub = new Subscription();
  sampleArray: any[] = [];
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.languageId, this.js.plantId, this.js.warehouseId,this.js.companyId,this.js.levelId, this.js.variantSubId).subscribe(res => {
      
    // let obj: any = {};
    // obj.companyId = [this.js.companyId];
    // obj.languageId = [this.js.languageId];
    // obj.storageMethod = [this.js.code];
    // obj.levelId = [this.js.levelId];
    // obj.warehouseId = [this.js.warehouseId];
    // obj.maintenance = [this.js.maintenance];

   // this.sub.add(this.service.search(obj).subscribe(res => {

      this.languageId = res[0].languageId;
      this.warehouseId = res[0].warehouseId;
      this.companyId = res[0].companyId;
      this.plantId = res[0].plantId;
      this.variantId = res[0].variantId;
      this.variantSubId = res[0].variantSubId;
      this.levelId = res[0].levelId;


      this.createdOn = this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOn = this.cs.dateTimeApi(res[0].updatedOn);
      this.createdBy = res[0].createdBy;
      this.updatedBy = res[0].updatedBy;

      //  this.form.get('tableRowArray')?.patchValue(res.levelReferences);
      if (res[0].levelId == 1) {
        this.masterService.searchWarehouse({
          companyCodeId: this.auth.companyId,
          plantId: this.auth.plantId,
          languageId: [this.auth.languageId]
        }).subscribe(searchres => {
          this.referencelist = [];
          searchres.forEach(element => {
            this.referencelist.push({
              value: element.warehouseId,
              label: element.warehouseId + '-' + element.warehouseDesc
            });
          })
          res.forEach((element, index) => {
            if(index != res.length -1){
              this.addNewRow()
            }
            });
    
       this.form.get('tableRowArray')?.patchValue(res);
       console.log(this.form.controls.tableRowArray)
       console.log(this.form.controls.tableRowArray)
        });
      }
      if (res[0].levelId == 2) {
        this.masterService.searchstoragesection({
          companyCodeId: [this.auth.companyId],
          plantId: [this.auth.plantId],
          languageId: [this.auth.languageId]
        }).subscribe(searchres => {
          this.referencelist = [];
          searchres.forEach(element => {
            this.referencelist.push({
              value: element.storageSectionId,
              label: element.storageSectionId + '-' + element.storageSection
            });
          })
          res.forEach((element, index) => {
            if(index != res.length -1){
              this.addNewRow()
            }
            });
    
       this.form.get('tableRowArray')?.patchValue(res);
        });
      }
      

    }))
    this.spin.hide();
  }
  companyList: any[] = [];
  plantList: any[] = [];
  warehouseIdList: any[] = [];
  partnercodeList: any[] = [];
  storageMethod1: any;
  maintenance1: any;


  referencelist: any[] = [];
  addreference(levelId){
    if(levelId.value==1){
      this.masterService.searchWarehouse({companyCodeId: this.auth.companyId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
        this.referencelist = [];
        res.forEach(element => {
          this.referencelist.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
        })
      });
    }
  if(levelId.value==2){
      this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId], plantId:[this.auth.plantId], languageId: [this.auth.languageId]}).subscribe(res => {
        this.referencelist = [];
        res.forEach(element => {
          this.referencelist.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
        })
      });
    }
  }
  levelList: any[] = [];
  languageidList: any[] = [];

  variantList: any[] = [];
  variantId: any;
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
        this.cas.dropdownlist.setup.variantid.url,
        this.cas.dropdownlist.setup.levelid.url,
        this.cas.dropdownlist.setup.companyid.url,
        this.cas.dropdownlist.setup.plantid.url,
        this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
    this.warehouseIdList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
   this.variantList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.variantid.key);
   this.levelList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.levelid.key);
   this.companyList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.companyid.key);
   this.plantList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.plantid.key);
   this.languageidList = this.cas.foreachlist2(results[5], this.cas.dropdownlist.setup.languageid.key);
   
   this.warehouseId = (this.auth.warehouseId);
   this.languageId = (this.auth.languageId);
   this.plantId = (this.auth.plantId);
   this.companyId = (this.auth.companyId);
    // this.masterService.searchPlantenter({companyId: this.auth.companyId, languageId: this.auth.languageId}).subscribe(res => {
    //   this.plantList = [];
    //   res.forEach(element => {
    //     this.plantList.push({value: element.plantId, label: element.plantId + '-' + element.description});
    //     if(this.plantList.length == 1){
    //       this.plantId = (this.plantList[0].value)
    //     }
      
    //   });
      
    // });
    // this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.plantId.value}).subscribe(res => {
    //   this.warehouseidList = [];
    //   res.forEach(element => {
    //     this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
    //     if(this.warehouseidList.length != 0){
    //       this.warehouseId = (this.warehouseidList[0].value)
    //     }
    //   });
      
    // });
    this.masterService.searchvariant({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res=>{
      this.variantList=[];
      res.forEach(element=>{
        this.variantList.push({value:element.variantCode,label:element.variantCode+'-'+element.variantText})
      })
    })
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
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

  

  variantSubId: any[] = [];
  onVariantChange(value){
    this.masterService.searchvariant({companyCodeId: this.companyId, warehouseId: this.warehouseId, plantId: this.plantId, languageId: [this.languageId], variantCode: [value.value]}).subscribe(res=>{
      this.variantSubId=[];
      res.forEach(element=>{
        this.variantSubId.push({value:element.variantSubCode, label:element.variantSubCode})
      })
    })
  }
  submit() {
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }

    // let obj: any = {};
    // obj.companyId = this.companySelection;
    // obj.languageId = this.languageSelection;
    // obj.plantId = this.plantSelection;
    // obj.storageMethod = this.storageMethod1;
    // obj.warehouseId = this.warehouseSelection;
    // obj.maintenance = this.maintenance1;
    // obj.levelId = this.levelId;
    // obj.levelReferences = this.form.controls.tableRowArray.value;

    this.form.controls.tableRowArray.value.forEach(element => {
      element.warehouseId = this.warehouseId;
      element.languageId = this.languageId;
      element.companyId = this.companyId;
      element.plantId = this.plantId;
      element.levelId = this.levelId;
      element.variantSubId = this.variantSubId;
      element.variantId = this.variantId;
      element.id = element.id ? element.id : null;
    })

    this.cs.notifyOther(false);
    this.spin.show();

    if (this.js.code) {
      this.sub.add(this.service.Update(this.form.controls.tableRowArray.value, this.js.code, this.js.warehouseId, this.js.languageId, this.js.plantId, this.js.companyId, this.js.levelId, this.js.variantSubId).subscribe(res => {
        this.toastr.success(res.alternateUom + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/productsetup/variant']);

        this.spin.hide();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    } else {
      this.sub.add(this.service.Create(this.form.controls.tableRowArray.value).subscribe(res => {
        this.toastr.success(res.alternateUom + " Saved Successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/productsetup/variant']);
        //  console.log(paramdata)
        this.spin.hide();


      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }

  }
}
