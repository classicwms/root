import {
  Component,
  OnInit
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  Validators
} from '@angular/forms';
import {
  ActivatedRoute,
  Router
} from '@angular/router';
import {
  NgxSpinnerService
} from 'ngx-spinner';
import {
  ToastrService
} from 'ngx-toastr';
import {
  Subscription
} from 'rxjs';
import {
  CommonApiService
} from 'src/app/common-service/common-api.service';
import {
  CommonService
} from 'src/app/common-service/common-service.service';
import {
  AuthService
} from 'src/app/core/core';
import {
  BasicdataService
} from '../basicdata.service';
import {
  MasterService
} from 'src/app/shared/master.service';
import {
  ReportsService
} from 'src/app/main-module/reports/reports.service';

@Component({
  selector: 'app-basicdata-new',
  templateUrl: './basicdata-new.component.html',
  styleUrls: ['./basicdata-new.component.scss']
})

export class BasicdataNewComponent implements OnInit {
  warehouseidDropdown: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    public auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service: BasicdataService,
    private reportservice: ReportsService,
    private masterService: MasterService
  ) {}



  form = this.fb.group({
    capacityCheck: [],
    companyCodeId: [this.auth.companyId, Validators.required],
    companyIdAndDescription: [],
    createdBy: [],
    createdOn: [],
    createdOnFE: [],
    deletionIndicator: [],
    weight:[],
    quantity:[],
    description: [],
    capacityUnit:["Volume",],
    shelfLifeIndicator: [],
    eanUpcNo: [],
    capacityUom:["CBM",],
    hsnCode: [],
    itemCode: [, Validators.required],
    itemGroup: [],
    itemType: [],
    languageId: [this.auth.languageId, Validators.required],
    manufacturerPartNo: [, Validators.required],
    maximumStock: [],
    minimumStock: [],
    model: [],
    plantId: [this.auth.plantId, Validators.required],
    plantIdAndDescription: [],
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
    reorderLevel: [],
    replenishmentQty: [],
    safetyStock: [],
    specifications1: [],
    specifications2: [],
    statusId: [],
    storageSectionId: [],
    subItemGroup: [],
    totalStock: [],
    uomId: [, Validators.required],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE: [],
    warehouseId: [, Validators.required],
    warehouseIdAndDescription: [],
    height: [],
    length: [],
    width: [],

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
    this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdOnFE.disable();

    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.companyCodeId.patchValue(this.auth.companyId);
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.warehouseId.disable();
    
    this.dropdownlist();

    if (this.js.pageflow == 'basicDataNew') {
      console.log(this.js.pageflow);
    }
  if(this.js.pageflow == 'New'){
   this.form.controls.totalStock.patchValue(0);
   this.form.controls.totalStock.disable();
   console.log(this.form.controls.capacityCheck.value);
   if(this.form.controls.capacityCheck.value == true){
   this.form.controls.capacityUnit.patchValue("Volume");
   this.form.controls.capacityUom.patchValue("CBM");
   }
  }
  
    if (this.js.pageflow != 'New') {
      this.form.controls.itemCode.disable();
      this.form.controls.uomId.disable();
      this.form.controls.manufacturerPartNo.disable();
   
      if (this.js.pageflow != 'New') {
        let obj: any = {};
        let total1=0;
        obj.itemCode = [this.js.itemCode];
        obj.warehouseId = [this.js.warehouseId];
        this.sub.add(this.reportservice.findInventory2(obj).subscribe(res => {
          let total = 0;
        res.forEach(x =>{
          total = total + (x.inventoryQuantity != null ? x.inventoryQuantity : 0)
        })
        console.log(total);
        this.form.controls.totalStock.patchValue(total);
        }))
      }
     // this.form.disable();
      this.fill();
    }
  }
  sub = new Subscription();
  skipitem: any;
  fill() {
    this.spin.show();
    if (this.js.pageflow == 'basicDataEdit' || this.js.pageflow == 'basicDataEdit2' || this.js.pageflow == 'basicDataEdit3') {
      console.log(this.js);
      let obj: any = {};
      obj.companyCodeId = [this.js.basicdataresult.companyCodeId];
      obj.plantId = [this.js.basicdataresult.plantId];
      obj.languageId = [this.js.basicdataresult.languageId];
      obj.warehouseId = [this.js.basicdataresult.warehouseId];
      obj.itemCode = [this.js.basicdataresult.itemCode];
      this.sub.add(this.service.search(obj).subscribe(res => {
        this.form.patchValue(res[0], {
          emitEvent: false
        });
        this.skipitem = res[0];
        this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
        this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));

        this.spin.hide();

      }))
      if (this.js.pageflow != 'New') {
        let obj: any = {};
        obj.itemCode = [this.js.itemCode];
        obj.warehouseId = [this.js.warehouseId];
        this.sub.add(this.reportservice.findInventory2(obj).subscribe(res => {
          let total = 0;
        res.forEach(x =>{
          total = total + (x.inventoryQuantity != null ? x.inventoryQuantity : 0)
        })
        console.log(total);
        this.form.controls.totalStock.patchValue(total);
        }))
      }

    } else {
      this.sub.add(this.service.Get(this.js.code, this.js.warehouseId, this.js.languageId, this.js.plantId, this.js.companyCodeId, this.js.uomId, this.js.itemCode, this.js.manufacturerPartNo).subscribe(res => {
        this.form.patchValue(res, {
          emitEvent: false
        });
        this.skipitem = res[0];
        this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
        this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));

        this.spin.hide();
        if (this.js.pageflow != 'New') {
          let obj: any = {};
          let total1=0;
          obj.itemCode = [this.js.itemCode];
          obj.warehouseId = [this.js.warehouseId];
          this.sub.add(this.reportservice.findInventory2(obj).subscribe(res => {
            let total = 0;
          res.forEach(x =>{
            total = total + (x.inventoryQuantity != null ? x.inventoryQuantity : 0)
          })
          console.log(total);
          this.form.controls.totalStock.patchValue(total);
          }))
        }
      }))
    }
    this.dropdownlist();
    this.spin.hide();
  }
  itemtypeList: any[] = [];
  storagesectionList: any[] = [];
  itemgroupList: any[] = [];
  warehouseIdList: any[] = [];
  partnercodeList: any[] = [];
  companyList: any[] = [];
  plantList: any[] = [];
  languageList: any[] = [];
  uomList:any[]=[];
  dropdownlist() {
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
     // this.cas.dropdownlist.setup.itemgroupid.url,
      //this.cas.dropdownlist.setup.itemtypeid.url,
      //this.cas.dropdownlist.setup.storagesectionid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.uomid.url,
    ]).subscribe((results) => {
      this.warehouseIdList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
      this.masterService.searchitemtype({
        companyCodeId: this.auth.companyId,
        plantId: this.auth.plantId,
        warehouseId: this.auth.warehouseId,
        languageId: [this.auth.languageId],
      }).subscribe(res => {
        this.itemtypeList = [];
        res.forEach(element => {
          this.itemtypeList.push({
            value: element.itemTypeId,
            label: element.itemTypeId + '-' + element.itemType
          });
        })
      });
      this.masterService.searchitemgroup({
        companyCodeId: this.auth.companyId,
        plantId: this.auth.plantId,
        warehouseId: this.auth.warehouseId,
        languageId: [this.auth.languageId],
       // itemTypeId: [this.form.controls.itemTypeId]
      }).subscribe(res => {
        this.itemgroupList = [];
        res.forEach(element => {
          this.itemgroupList.push({
            value: element.itemGroupId,
            label: element.itemGroupId + '-' + element.itemGroup
          });
        })
      })
   
      this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId:[this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
        this.storagesectionList = [];
        res.forEach(element => {
          this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
       })
        });
      this.companyList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.plantid.key);
      this.languageList = this.cas.foreachlist2(results[3], this.cas.dropdownlist.setup.languageid.key);
     // this.uomList=this.cas.forLanguageFilter(results[],this.cas.dropdownlist.setup.uomid.key);
     this.masterService.searchuom({companyCodeId:this.auth.companyId ,plantId: this.auth.plantId,warehouseId:this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.uomList = [];
      res.forEach(element => {
        this.uomList.push({value: element.uomId, label: element.uomId + '-' + element.description});
      })
    });
    this.itemgroupList=this.cas.removeDuplicatesFromArrayNew(this.itemgroupList);
      this.uomList=this.cas.removeDuplicatesFromArrayNew(this.uomList);
      // this.masterService.searchitemtype({
      //   companyCodeId: this.auth.companyId,
      //   languageId: [this.auth.languageId],
      //   plantId: this.auth.plantId,
      //   warehouseId: this.auth.warehouseId
      // }).subscribe(res => {
      //   this.itemtypeList = [];
      //   res.forEach(element => {
      //     this.itemtypeList.push({
      //       value: element.itemTypeId,
      //       label: element.itemTypeId + '-' + element.itemType
      //     });
      //   });
      // })
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


  onWarehouseChange(value) {
    console.log(value);
    this.form.controls.companyCodeId.patchValue(value.companyCodeId);
    this.form.controls.languageId.patchValue(value.languageId);
    this.form.controls.plantId.patchValue(value.plantId);

    this.masterService.searchitemtype({
      companyCodeId: this.form.controls.companyCodeId.value,
      warehouseId: value.value,
      plantId: this.form.controls.plantId.value,
      languageId: [this.form.controls.languageId.value]
    }).subscribe(res => {
      this.itemtypeList = [];
      res.forEach(element => {
        this.itemtypeList.push({
          value: element.itemTypeId,
          label: element.itemTypeId + '-' + element.itemType
        });
      })
    });
    this.masterService.searchitemgroup({
      companyCodeId: this.form.controls.companyCodeId.value,
      plantId: this.form.controls.plantId.value,
      warehouseId: value.value,
      languageId: [this.form.controls.languageId.value],
      itemTypeId: [this.form.controls.itemTypeId.value]
    }).subscribe(res => {
      this.itemgroupList = [];
      res.forEach(element => {
        this.itemgroupList.push({
          value: element.itemGroupId,
          label: element.itemGroupId + '-' + element.itemGroup
        });
      })
    });
    this.masterService.searchstoragesection({
      companyCodeId: [this.form.controls.companyCodeId.value],
      plantId: [this.form.controls.plantId.value],
      warehouseId: [value.value],
      languageId: [this.form.controls.languageId.value]
    }).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({
          value: element.storageSectionId,
          label: element.storageSectionId + '-' + element.description
        });
      })
    });

  }
  onItemtypeChange(value) {
    this.masterService.searchitemgroup({
      companyCodeId: this.form.controls.companyCodeId.value,
      warehouseId: this.form.controls.warehouseId.value,
      plantId: this.form.controls.plantId.value,
      languageId: [this.form.controls.languageId.value],
      itemTypeId: [value.value]
    }).subscribe(res => {
      this.itemgroupList = [];
      res.forEach(element => {
        this.itemgroupList.push({
          value: element.itemGroupId,
          label: element.itemGroupId + '-' + element.itemGroup
        });
      })
    });
  }
  Skip() {

    let paramdata = "";
    paramdata = this.cs.encrypt({
      pageflow: 'basicDataEdit',
      basicdataresult: this.skipitem
    });
    this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);

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

    this.cs.notifyOther(false);
    this.spin.show();
    this.form.controls.createdOn.patchValue(this.cs.day_callapiSearch(this.form.controls.createdOn.value));
    this.form.controls.updatedOn.patchValue(this.cs.day_callapiSearch(this.form.controls.updatedOn.value));
    if (this.js.pageflow != 'New' || this.js.pageflow != 'basicDataNew') {
      if (this.js.pageflow == 'basicDataEdit' || this.js.pageflow == 'basicDataEdit3' || this.js.pageflow == 'Edit') {
        if (this.js.code) {
          this.sub.add(this.service.Update(this.form.getRawValue(), this.js.code, this.js.warehouseId, this.js.languageId, this.js.plantId, this.js.companyCodeId, this.js.uomId, this.js.itemCode, this.js.manufacturerPartNo).subscribe(res => {
            this.toastr.success(res.itemCode + " updated successfully!", "Notification", {
              timeOut: 2000,
              progressBar: false,
            });


            if (this.js.pageflow != 'New' && this.js.pageflow != 'basicDataNew') {
              let paramdata = "";
              paramdata = this.cs.encrypt({
                pageflow: 'basicDataEdit',
                basicdataresult: res
              });
              this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
              this.spin.hide();
            }
            if (this.js.pageflow == 'basicDataEdit3') {
              let paramdata = "";
              paramdata = this.cs.encrypt({
                pageflow: 'basicDataEdit3',
                basicdataresult: res
              });
              this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
              this.spin.hide();
            }

            this.spin.hide();

          }, err => {

            this.cs.commonerrorNew(err);
            this.spin.hide();

          }));
        }
      }
    }
    if (this.js.pageflow == "basicDataEdit2" || this.js.pageflow == 'basicDataEdit3') {
      this.sub.add(this.service.Update(this.form.getRawValue(), this.form.controls.itemCode.value, this.js.basicdataresult.warehouseId, this.js.basicdataresult.languageId, this.js.basicdataresult.plantId, this.js.basicdataresult.companyCodeId, this.form.controls.uomId.value, this.js.basicdataresult.itemCode, this.form.controls.manufacturerPartNo.value).subscribe(res => {
        this.toastr.success(res.itemCode + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });


        if (this.js.pageflow == "basicDataEdit2") {
          let paramdata = "";
          paramdata = this.cs.encrypt({
            pageflow: 'basicDataEdit2',
            basicdataresult: res
          });
          this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
          this.spin.hide();
        }
        if (this.js.pageflow == 'basicDataEdit3') {
          let paramdata = "";
          paramdata = this.cs.encrypt({
            pageflow: 'basicDataEdit3',
            basicdataresult: res
          });
          this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
          this.spin.hide();
        }


        this.spin.hide();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    if (this.js.pageflow == 'New') {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.itemCode + " Saved Successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,

        });
        let paramdata = "";
        paramdata = this.cs.encrypt({
          pageflow: 'basicDataNew',
          basicdataresult: res
        });
        this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
        this.spin.hide();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }

  }

}
