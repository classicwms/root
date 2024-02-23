import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService, dropdownelement } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { HandlingEquipmentService, HandlingquipmentElement } from '../handling-equipment.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { MasterService } from 'src/app/shared/master.service';
import { ActivatedRoute, Router } from '@angular/router';


interface SelectItem {
  id: number;
  itemName: string;
}


@Component({
  selector: 'app-equipment-new',
  templateUrl: './equipment-new.component.html',
  styleUrls: ['./equipment-new.component.scss']
})
export class EquipmentNewComponent implements OnInit {
  screenid: 1035 | undefined;
  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
    sub = new Subscription();
  //creation of Form
  form = this.fb.group({
    acquistionDate: [],
    acquistionValue: [],
    category: [],
    companyCodeId: [],
    countryOfOrigin: [],
    createdBy: [this.auth.username,],
    createdOn: [],
    createdOnFE:[],
    updatedOnFE:[],
    currencyId: [],
    deletionIndicator: [],
    handlingEquipmentId: [,Validators.required],
    handlingUnit: [,Validators.required],
    heBarcode: [,Validators.required],
    languageId: [,Validators.required],
    manufacturerPartNo: [],
    modelNo: [],
    plantId: [],
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
    statusId: [],
    updatedBy: [this.auth.username,],
    updatedOn: [],
    acquistionDateFE:[],
    warehouseId: [,Validators.required],
  });
  submitted = false;
  disabled = false;
  step = 0;
  
  filteredOptions: Observable<string[]> | undefined;
  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }
  formgr: HandlingquipmentElement | undefined;

  panelOpenState = false;
  filterheItems: any[] = [];
  selectedItems: any[] = [];
  multiiitemcodelist :  SelectItem[] = [];
  multiSelectheList: SelectItem[] = [];
  constructor(
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: HandlingEquipmentService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    private cas: CommonApiService,
    public cs: CommonService,
    private masterService: MasterService,
  ) { }

  ngOnInit(): void {
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
  
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      
      this.form.controls.warehouseId.disable();
      this.dropdownlist();      
    
    
    
   
    if (this.data.pageflow != 'New') {
      this.form.controls.handlingEquipmentId.disable();
      this.form.controls.handlingUnit.disable();
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }

    this.filteredOptions = this.form.controls.handlingUnit.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value)),
    );
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.handlingunitList.filter(option => option.toLowerCase().includes(filterValue));
  }

  warehouseIdList: any[] = [];
  dropdownSettings = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

  displayFn(user: any): string {
    return user && user.handlingUnit ? user.handlingUnit : '';
  }
  currencyList: any= [];
  warehouseidDropdown: any;
  handlingunitList: any = [];
  companyList: any[] = [];
  plantList: any[]=[];
  languageList: any[]=[];
  dropdownlist() {
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.master.handlingunit.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.currency.url,
    ]).subscribe((results) => {
      this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
      this.handlingunitList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.master.handlingunit.key);
      this.companyList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
      this.languageList=this.cas.foreachlist2(results[4],this.cas.dropdownlist.setup.languageid.key);
      this.currencyList=this.cas.forLanguageFilter(results[5],this.cas.dropdownlist.setup.currency.key);
        this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
        this.form.controls.languageId.patchValue(this.auth.languageId);
        this.form.controls.plantId.patchValue(this.auth.plantId);
        this.form.controls.companyCodeId.patchValue(this.auth.companyId);
        this.masterService.searchhandlingunit({languageId: [this.auth.languageId],companyCodeId:[this.auth.companyId],warehouseId:[this.auth.warehouseId],plantId:[this.auth.plantId]}).subscribe(res => {
          this.handlingunitList = [];
          res.forEach(element => {
            this.handlingunitList.push({value: element.handlingUnit, label: element.handlingUnit });
          });
        });   this.masterService.searchcurrency({languageId: [this.auth.languageId],companyCodeId:[this.auth.companyId],warehouseId:[this.auth.warehouseId],plantId:[this.auth.plantId]}).subscribe(res => {
          this.currencyList = [];
          res.forEach(element => {
            this.currencyList.push({value: element.currencyId, label: element.currencyDescription });
          });
        });

        this.form.controls.warehouseId.disable();
        this.form.controls.companyCodeId.disable();
        this.form.controls.plantId.disable();
        this.form.controls.languageId.disable();
      
      });
      this.spin.hide();
    }
 
   
  onWarehouseChange(value){
    
    this.masterService.searchhandlingunit({languageId: [this.form.controls.languageId.value],companyCodeId:[this.form.controls.companyCodeId.value],warehouseId:[value.value],plantId:[this.form.controls.plantId.value]}).subscribe(res => {
      this.handlingunitList = [];
      res.forEach(element => {
        this.handlingunitList.push({value: element.handlingUnit, label: element.handlingUnit + '-' + element.handlingUnit});
      });
    });

   
}

  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId).subscribe(res => {
      console.log(res)
      this.form.patchValue(res, { emitEvent: false });
     // this.selectedItems.push({id: res.handlingUnit,itemName: res.handlingUnit,})
      //this.form.controls.handlingUnit.patchValue([{id: res.handlingUnit,itemName: res.handlingUnit}]);
      //this.form.controls.handlingUnit.patchValue(this.data.handlingUnitId);
      this.form.controls.createdOnFE.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
      this.form.controls.updatedOnFE.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
      this.form.controls.statusId.patchValue(res.statusId != null ? res.statusId.toString() : '');
     // this.form.controls.acquistionDate.patchValue(this.cs.dateapi(this.form.controls.acquistionDate.value))
      this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }
  js: any = {}
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

  if (this.data.pageflow!='New') {
    this.sub.add(this.service.Update(this.form.getRawValue(),this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId).subscribe(res => {
      this.toastr.success(res.handlingEquipmentId + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/other-masters/handling-equipment']);

      this.spin.hide();
      this.dialogRef.close();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.handlingEquipmentId + " Saved Successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/other-masters/handling-equipment']);
      this.spin.hide();
      this.dialogRef.close();
  
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
     
    }));
   }
  
  }
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }

  onItemSelect(item:any){
    console.log(item);
    console.log(this.selectedItems);
}
OnItemDeSelect(item:any){
    console.log(item);
    console.log(this.selectedItems);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}
}




