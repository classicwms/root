import {
  Component,
  OnInit,
  ViewChild
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
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
  Table
} from 'primeng/table';
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
  BatchserialService
} from '../batchserial.service';
import {
  MasterService
} from 'src/app/shared/master.service';

@Component({
  selector: 'app-batchserial-new',
  templateUrl: './batchserial-new.component.html',
  styleUrls: ['./batchserial-new.component.scss']
})
export class BatchserialNewComponent implements OnInit {
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


  storageMethodList: any[] = [];
  maintainanceList: any[] = [];

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService, private masterService: MasterService,
    private service: BatchserialService) {
    this.storageMethodList = [{
      'value': 'Batch',
      'label': 'Batch'
    }, {
      'value': 'Serial',
      'label': 'Serial'
    }, {
      'value': 'Not Applicable',
      'label': 'Not Applicable'
    }];
    this.maintainanceList = [{
      'value': 'Internal',
      'label': 'Internal'
    }, {
      'value': 'External',
      'label': 'External'
    }]
  }


  form: FormGroup;

  public levelReferences: FormGroup;
  private createTableRow(): FormGroup {
    return this.fb.group({
      companyId: new FormControl(this.companyId),
      companyIdAndDescription: new FormControl(),
      deletionIndicator: new FormControl(),
      description: new FormControl(),
      languageId: new FormControl(this.languageId, ),
      levelId: new FormControl(),
      levelIdAndDescription: new FormControl(),
      levelReferences: new FormControl(),
      maintenance: new FormControl(),
      id: new FormControl(),
      plantId: new FormControl(this.plantId, ),
      plantIdAndDescription: new FormControl(),
      storageMethod: new FormControl(),
      warehouseIdAndDescription: new FormControl(),
      warehouseId: new FormControl(this.warehouseId, )
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
    }


    this.createForm();

    if (this.js.pageflow != 'New' && this.js.pageflow != 'basicDataNew') {
      if (this.js.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }
  }


  createdBy: any;
  createdOn: any;
  updatedOn: any;
  updatedBy: any;

  sub = new Subscription();
  sampleArray: any[] = [];
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code,this.js.languageId, this.js.plantId, this.js.warehouseId,this.js.companyId,this.js.levelId, this.js.maintenance).subscribe(res => {
      
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
      this.storageMethod1 = res[0].storageMethod;
      this.maintenance1 = res[0].maintenance;
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
       console.log(this.form)
       console.log(this.form.controls.tableRowArray)
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
  levelId: any;


  referencelist: any[] = [];
  addreference(levelId) {
    if (levelId.value == 1) {
      this.masterService.searchWarehouse({
        companyCodeId: this.auth.companyId,
        plantId: this.auth.plantId,
        languageId: [this.auth.languageId]
      }).subscribe(res => {
        this.referencelist = [];
        res.forEach(element => {
          this.referencelist.push({
            value: element.warehouseId,
            label: element.warehouseId + '-' + element.warehouseDesc
          });
        })
      });
    }
    if (levelId.value == 2) {
      this.masterService.searchstoragesection({
        companyCodeId: [this.auth.companyId],
        plantId: [this.auth.plantId],
        languageId: [this.auth.languageId]
      }).subscribe(res => {
        this.referencelist = [];
        res.forEach(element => {
          this.referencelist.push({
            value: element.storageSectionId,
            label: element.storageSectionId + '-' + element.storageSection
          });
        })
      });
    }
  }
  levelList: any[] = [];
  languageidList: any[] = [];

  dropdownlist() {
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.levelid.url,
    ]).subscribe((results) => {
      this.warehouseIdList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
      this.companyList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.plantid.key);
      this.languageidList = this.cas.foreachlist2(results[3], this.cas.dropdownlist.setup.languageid.key);
      this.levelList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.levelid.key);

      this.warehouseId = (this.auth.warehouseId);
      this.languageId = (this.auth.languageId);
      this.plantId = (this.auth.plantId);
      this.companyId = (this.auth.companyId);


      // this.masterService.searchPlantenter({
      //   companyId: this.auth.companyId,
      //   languageId: this.auth.languageId
      // }).subscribe(res => {
      //   this.plantList = [];
      //   res.forEach(element => {
      //     this.plantList.push({
      //       value: element.plantId,
      //       label: element.plantId + '-' + element.description
      //     });
      //     if (this.plantList.length == 1) {
      //       this.plantId = this.plantList[0].value;
      //     }

      //   });

      // });
      // this.masterService.searchWarehouseenter({
      //   companyId: this.auth.companyId,
      //   languageId: this.auth.languageId,
      //   plantId: this.auth.plantId
      // }).subscribe(res => {
      //   this.warehouseidList = [];
      //   res.forEach(element => {
      //     this.warehouseidList.push({
      //       value: element.warehouseId,
      //       label: element.warehouseId + '-' + element.description
      //     });
      //     if (this.warehouseidList.length != 0) {
      //       this.warehouseId = (this.warehouseidList[0].value)
      //     }
      //   });

      // });
      this.masterService.searchlevel({
        companyCodeId: this.auth.companyId,
        warehouseId: this.auth.warehouseId,
        plantId: this.auth.plantId,
        languageId: [this.auth.languageId]
      }).subscribe(res => {
        this.levelList = [];
        res.forEach(element => {
          this.levelList.push({
            value: element.levelId,
            label: element.levelId + '-' + element.level
          });
        })
      });

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
  onWarehouseChange(value) {
    this.companyId = (value.companyCodeId);
    this.languageId = (value.languageId);
    this.plantId = (value.plantId);
    this.warehouseId = (value.warehouseId);
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
      element.maintenance = this.maintenance1;
      element.storageMethod = this.storageMethod1;
      element.id = element.id ? element.id : null;
    })


    this.cs.notifyOther(false);
    this.spin.show();

    if (this.js.code) {
      this.sub.add(this.service.Update(this.form.controls.tableRowArray.value, this.js.code, this.js.warehouseId, this.js.languageId, this.js.plantId, this.js.companyId, this.js.levelId, this.js.maintenance).subscribe(res => {
        this.toastr.success(res.alternateUom + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/productsetup/batch']);

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
        this.router.navigate(['/main/productsetup/batch']);
        //  console.log(paramdata)
        this.spin.hide();


      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }

  }
}
