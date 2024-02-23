import {
  Component,
  OnInit,
  ViewChild
} from '@angular/core';
import {
  FormBuilder,
  Validators,
  FormControl,
  FormArray,
  FormGroup
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
  MasterService
} from 'src/app/shared/master.service';
import {
  StratergyService
} from '../stratergy.service';
import {
  Table
} from 'primeng/table';
import { AddLinesComponent } from './add-lines/add-lines.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-stratergy-new',
  templateUrl: './stratergy-new.component.html',
  styleUrls: ['./stratergy-new.component.scss']
})
export class StratergyNewComponent implements OnInit {
  storageMethodList: any[] = [];
  maintainanceList: any[] = [];
  warehouseidList: any[] = [];
  warehouseidDropdown: any;
  advanceFilterShow: boolean;
  @ViewChild('Setupbatch') Setupbatch: Table | undefined;
  batch: any;
  selectedbatch: any;
  isLinear = false;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    public dialog: MatDialog,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service: StratergyService,

    private masterService: MasterService) {

  }




  public stx: FormGroup;
  form = this.fb.group({
    companyId: [, ],
    companyIdAndDescription: [],
    deletionIndicator: [],
    description: [],
    languageId: [, ],
    levelId: [, ],
    levelIdAndDescription: [],
    maintenance: [, ],
    plantId: [, ],
    plantIdAndDescription: [],
    storageMethod: [, ],
    warehouseId: [, ],
    warehouseIdAndDescription: [],
    updatedBy: [],
    updatedOn: [],
    updatedOnFE: [],
    createdOn: [],
    createdOnFE: [],
    createdBy: [],
  });

  get formArr() {
    return this.stx.get('Rows') as FormArray;
  }

  initRows() {
    return this.fb.group({
      companyId: [this.auth.companyId,],
      companyIdAndDescription: [],
      createdBy: [],
      createdOn: [],
      deletionIndicator: [],
      description: [],
      languageId: [this.auth.languageId,],
      plantId: [this.auth.plantId,],
      plantIdAndDescription: [],
      priority1: [],
      priority10: [],
      priority2: [],
      priority3: [],
      priority4: [],
      priority5: [],
      priority6: [],
      priority7: [],
      priority8: [],
      priority9: [],
      sequenceIndicator: [],
      strategyNo: [],
      strategyNoText: [],
      strategyTypeId: [],
      updatedBy: [],
      updatedOn: [],
      warehouseId: [this.auth.warehouseId,],
      warehouseIdAndDescription: [],
    });
  }

  addNewRow() {
    this.formArr.push(this.initRows());
  }

  removerow(x: any) {
    this.formArr.removeAt(x);
  }
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

    this.stx = this.fb.group({
      Rows: this.fb.array([this.initRows()]),
    });

    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);

    this.form.controls.updatedBy.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.updatedOn.disable();
    this.form.controls.createdOn.disable();

    this.form.controls.companyId.patchValue(this.auth.companyId);
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.plantId.patchValue(this.auth.plantId);

    this.form.controls.warehouseId.disable();
    this.dropdownlist();




    if (this.js.pageflow != 'New') {

      // if (this.js.pageflow == 'Display')
      this.form.disable();
      this.fill();
    }
  }
  sub = new Subscription();
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.js.code.strategyTypeId, this.js.code).subscribe(res => {
      this.form.patchValue(res, {
        emitEvent: false
      });
      this.form.controls.warehouseId.patchValue(res[0].warehouseId)
   

      this.masterService.searchstrategy({
        companyCodeId: this.auth.companyId,
        warehouseId: this.auth.warehouseId,
        plantId: this.auth.plantId,
        languageId: [this.auth.languageId],
        strategyTypeId: [res[0].strategyTypeId]
      }).subscribe(res => {
        this.priority1List = [];
        res.forEach(element => {
          this.priority1List.push({
            value: element.strategyNo,
            label: element.strategyNo + '-' + element.strategyText
          });
        })
      })
      this.stx.get('Rows')?.patchValue(res);

      this.resultTable = res;
      this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
      this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
  }
  stratergyList: any[] = [];
  companyList: any[] = [];
  languageidList: any[] = [];
  plantList: any[] = [];
  levelList: any[] = [];
  stateList: any[] = [];
  cityList: any[] = [];
  countryList: any[] = [];
  dropdownlist() {
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.levelid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.strategyid.url,
    ]).subscribe((results) => {
      this.warehouseidList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
      this.levelList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.levelid.key);
      this.companyList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
      this.languageidList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.languageid.key);
      this.stratergyList = this.cas.forLanguageFilter(results[5], this.cas.dropdownlist.setup.strategyid.key);
      this.stratergyList = this.cs.removeDuplicatesFromArrayNewstatus(this.stratergyList)
      //this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
      this.form.controls.languageId.patchValue(this.auth.languageId);
      //this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyId.patchValue(this.auth.companyId);
      this.form.controls.companyId.disable();
      this.form.controls.languageId.disable();

      this.masterService.searchPlantenter({
        companyId: this.auth.companyId,
        languageId: this.auth.languageId
      }).subscribe(res => {
        this.plantList = [];
        res.forEach(element => {
          this.plantList.push({
            value: element.plantId,
            label: element.plantId + '-' + element.description
          });
        });
      });

      this.masterService.searchWarehouse({
        companyCodeId: this.auth.companyId,
        languageId: [this.auth.languageId],
        plantId: this.form.controls.plantId.value
      }).subscribe(res => {
        this.warehouseidList = [];
        res.forEach(element => {
          this.warehouseidList.push({
            value: element.warehouseId,
            label: element.warehouseId + '-' + element.warehouseDesc
          });
        });
      });

      this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  //  onplantchange(value){

  //   this.masterService.searchWarehouseenter({companyId: this.auth.companyId, languageId:this.auth.languageId,plantId:this.form.controls.plantId.value}).subscribe(res => {
  //    this.warehouseidList = [];
  //    res.forEach(element => {
  //      this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.description});
  //    });
  //  });
  // }
  onWarehouseChange(value) {
    this.masterService.searchstrategy({
      companyCodeId: this.auth.companyId,
      warehouseId: value.value,
      plantId: this.auth.plantId,
      languageId: [this.auth.languageId]
    }).subscribe(res => {
      this.stratergyList = [];
      res.forEach(element => {
        this.stratergyList.push({
          value: element.strategyTypeId,
          label: element.strategyTypeId + '-' + element.strategyTypeText
        });
      })
      this.stratergyList = this.cs.removeDuplicatesFromArrayNewstatus(this.stratergyList);
    });
  }

  priority1List: any[] = [];
  selectedStratergyType(e, row){
    this.masterService.searchstrategy({
      companyCodeId: this.auth.companyId,
      warehouseId: this.auth.warehouseId,
      plantId: this.auth.plantId,
      languageId: [this.auth.languageId],
      strategyTypeId: [e.value]
    }).subscribe(res => {
      this.priority1List = [];
      res.forEach(element => {
        this.priority1List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
      })
    })
      let noofStratergy: any[] = [];
    this.stx.getRawValue().Rows.forEach(x => {
      console.log(x)
      if(x.strategyTypeId == e.value){
        noofStratergy.push(x)
      }
    })
    console.log(row)
    row.controls.sequenceIndicator.patchValue(noofStratergy.length);
  }

  priority2List:any[] = [];
  priority3List:any[] = [];
  priority4List:any[] = [];
  priority5List:any[] = [];
  priority6List:any[] = [];
  priority7List:any[] = [];
  priority8List:any[] = [];
  priority9List:any[] = [];
  priority10List:any[] = [];

  priority1Selected(e) {
    const optionsCopy = [...this.priority1List];
    for (const selectedValue of e.value) {
      const index = optionsCopy.findIndex(option => option.value === selectedValue);
      if (index !== -1) {  optionsCopy.splice(index, 1);   }
    }
    this.priority2List = optionsCopy; 
  }
  priority2Selected(e) {
    const optionsCopy = [...this.priority2List];
    for (const selectedValue of e.value) {
      const index = optionsCopy.findIndex(option => option.value === selectedValue);
      if (index !== -1) {  optionsCopy.splice(index, 1);   }
    }
    this.priority3List = optionsCopy; 
  }

  submit() {
    let obj: any[] = [];
    obj = (this.stx.getRawValue().Rows);
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
    if (this.js.code) {
      this.sub.add(this.service.Update(this.js.code.strategyTypeId, this.js.code, this.resultTable).subscribe(res => {
        this.toastr.success(res.storageMethod + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/productstorage/stratergy']);

        this.spin.hide();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    } else {
      this.sub.add(this.service.Create(this.resultTable).subscribe(res => {
        this.toastr.success(res.storageMethod + " Saved Successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/productstorage/stratergy']);
        this.spin.hide();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }

  }
  onChange() {
    console.log(this.selectedbatch.length)
    const choosen = this.selectedbatch[this.selectedbatch.length - 1];
    this.selectedbatch.length = 0;
    this.selectedbatch.push(choosen);
  }


  resultTable: any[ ] = [];
  openDialog(data: any = 'New', element: any = null, index: any = null): void {
    const dialogRef = this.dialog.open( AddLinesComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? element : null, lines: this.resultTable }
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


  delete(i){ 
    this.resultTable.splice(i, 1);
  }
}
