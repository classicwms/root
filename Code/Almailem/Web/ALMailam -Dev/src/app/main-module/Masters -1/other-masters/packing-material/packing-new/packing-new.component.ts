import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PackingmaterialElement, PackingMaterialService } from '../packing-material.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { UomService } from 'src/app/main-module/other-setup/uom/uom.service';
import { MasterService } from 'src/app/shared/master.service';



interface SelectItem {
  id: number;
  itemName: string;
}
interface SelectUom {
  value: string;
  label: string;
}

@Component({
  selector: 'app-packing-new',
  templateUrl: './packing-new.component.html',
  styleUrls: ['./packing-new.component.scss']
})
export class PackingNewComponent implements OnInit {
  screenid: 1041 | undefined;

  sub = new Subscription();

  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  //creation of Form
  form = this.fb.group({
    businessPartnerCode: [],
    createdby: [],
    createdon: [],
   // deletionIndicator: [],
    description: [],
    dimensionUom: ['m',],
    height: [],
    length: [],
    packingMaterialNo: [,Validators.required],
    referenceField1: [], //passed for weight
    // referenceField10: [],
    // referenceField2: [],
    // referenceField3: [],
    // referenceField4: [],
    // referenceField5: [],
    // referenceField6: [],
    // referenceField7: [],
    // referenceField8: [],
    // referenceField9: [],
    statusId: ["1"],
    totalStock: [],
    updatedby: [],
    updatedon: [],
    volume: [],
    volumeUom: ['m3',],
    width: [],
    createdOn: [],
    languageId: [,],
    warehouseId: [,Validators.required],
    plantId: [this.auth.plantId,],
    companyCodeId: [this.auth.companyId,],
    createdBy: [this.auth.username],
    updatedBy: [this.auth.username],
    updatedOn: [],
  });
  submitted = false;
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
  formgr: PackingmaterialElement | undefined;

  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: PackingMaterialService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    private cs: CommonService,
    private cas: CommonApiService,
    private uomservice:UomService,
    private masterService: MasterService,
  ) { }
  ngOnInit(): void {
    this.form.controls.createdBy.disable();
    this.form.controls.createdOn.disable();
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOn.disable();
   
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
    this.form.controls.languageId.patchValue(this.auth.languageId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.companyCodeId.patchValue(this.auth.companyId);
    this.form.controls.warehouseId.disable();
    
      this.dropdownlist();      
    
      
  
    if (this.data.pageflow != 'New') {
      this.form.controls.packingMaterialNo.disable();
      if (this.data.pageflow == 'Display')
     
        this.form.disable();
      this.fill();
    }
  }



  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId).subscribe(res => {
      console.log(res)
      this.form.patchValue(res, { emitEvent: false });
      //this.form.controls.businessPartnerCode.patchValue([{id: res.partnerCode,itemName: res.partnerCode}]);
      //this.form.controls.businessPartnerCode.patchValue([{id: res.businessPartnerCode,itemName: res.businessPartnerCode}]);
      this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
      this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
      this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }

  itemcodeList: any[] = [];
  filtertemgroupList: any[] = [];
  selectedItems1: any[] = [];
  multiiitemcodelist :  SelectItem[] = [];
  multiSelectitemcodeList: SelectItem[] = [];
  uomList: any[]=[];
  multiuomlist: SelectUom[]=[];
  filteruomList:any[]=[];
  multiSelectuomList:any[]=[];
  businessList:any[]=[];

  dropdownSettings = {
    singleSelection: true,
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
  warehouseIdList: any[] = [];
  companyList: any[] = [];
  plantList: any[]=[];
  languageList: any[]=[];
  warehouseidDropdown:any;
  dropdownlist() {
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.uomid.url,
      this.cas.dropdownlist.master.businesspartner.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
      this.warehouseIdList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.warehouseid.key);
      this.uomList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.uomid.key);
      this.businessList=this.cas.forLanguageFilter(results[2],this.cas.dropdownlist.master.businesspartner.key);
      this.companyList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.plantid.key);
      this.languageList=this.cas.foreachlist2(results[5],this.cas.dropdownlist.setup.languageid.key);
     
        this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
        this.form.controls.languageId.patchValue(this.auth.languageId);
        this.form.controls.plantId.patchValue(this.auth.plantId);
        this.form.controls.companyCodeId.patchValue(this.auth.companyId);
       // this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
       
       this.form.controls.warehouseId.disable();
       this.form.controls.companyCodeId.disable();
       this.form.controls.plantId.disable();
       this.form.controls.languageId.disable();
       this.masterService.searchbuisnesspartner({languageId: [this.auth.languageId],companyCodeId:[this.auth.companyId],warehouseId:[this.auth.warehouseId],plantId:[this.auth.plantId]}).subscribe(res => {
        this.businessList = [];
        res.forEach(element => {
          this.businessList.push({value: element.partnerCode, label: element.partnerCode + '-' + element.partnerName});
        });
      });
  
  
    });
    this.sub.add(this.service.Getpartnercode().subscribe(res => {
      this.itemcodeList = res;
      this.filtertemgroupList = this.itemcodeList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({id: x.partnerCode, itemName: x.partnerCode}))
           // this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({id: x.itemCode, itemName: x.itemCode + (x.description == null ? '' : '- ' + x.description)}))
      this.multiSelectitemcodeList = this.multiiitemcodelist;

      this.spin.hide();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  //   this.sub.add(this.uomservice.Getall().subscribe(res => {
  //     this.uomList = res;
  //     this.filteruomList = this.uomList.filter(element => {
  //       return element.warehouseId === this.form.controls['warehouseId'].value;
  //     });
  //     this.filteruomList.forEach(x => this.multiuomlist.push({value: x.uomId, label: x.description}))
  //          // this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({id: x.itemCode, itemName: x.itemCode + (x.description == null ? '' : '- ' + x.description)}))
  //     this.multiSelectuomList = this.multiuomlist;

  //     this.spin.hide();

  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }));
  // }
  onWarehouseChange(value){
    this.form.controls.companyCodeId.patchValue(value.companyCodeId);
    this.form.controls.languageId.patchValue(value.languageId);
    this.form.controls.plantId.patchValue(value.plantId);
  
   
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
  submit() {
    this.submitted = true;
    this.form.patchValue(this.form.controls.businessPartnerCode.value);
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
    this.form.removeControl('updatedOn');
    this.form.removeControl('createdOn');
    this.form.patchValue({ updatedby: this.auth.username });
    if (this.data.code) {
      this.sub.add(this.service.Update(this.form.getRawValue(),this.data.code,this.data.companyCodeId,this.data.languageId,this.data.plantId,this.data.warehouseId).subscribe(res => {
        this.toastr.success(this.data.code + " updated successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.dialogRef.close();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.packingMaterialNo + " Saved Successfully!","Notification",{
          timeOut: 2000,
          progressBar: false,
        });

        this.spin.hide();
        this.dialogRef.close();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  };

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
  onItemSelect(item: any) {
    console.log(item);
  }

OnItemDeSelect(item:any){
    console.log(item);
    console.log(this.selectedItems1);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}
}




