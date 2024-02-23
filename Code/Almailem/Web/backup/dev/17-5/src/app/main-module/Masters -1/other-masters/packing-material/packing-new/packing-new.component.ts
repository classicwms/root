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



interface SelectItem {
  id: number;
  itemName: string;
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
    description: [,Validators.required],
    dimensionUom: [],
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
    volumeUom: [],
    width: [],
    createdOn: [],
    languageId: ['EN'],
    warehouseId: [this.auth.warehouseId],
    plantId: ['1001'],
    companyCodeId: ['1000'],
    createdBy: ['wms'],
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
  ) { }
  ngOnInit(): void {
    this.form.controls.createdBy.disable();
    this.form.controls.createdOn.disable();
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOn.disable();
    this.dropdownlist();  
    if (this.data.pageflow != 'New') {
      if (this.data.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }
  }



  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code).subscribe(res => {
      console.log(res)
      this.form.patchValue(res, { emitEvent: false });
      //this.form.controls.businessPartnerCode.patchValue([{id: res.partnerCode,itemName: res.partnerCode}]);
      this.form.controls.businessPartnerCode.patchValue([{id: res.businessPartnerCode,itemName: res.businessPartnerCode}]);
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
  selectedItems1: SelectItem[] = [];
  multiiitemcodelist :  SelectItem[] = [];
  multiSelectitemcodeList: SelectItem[] = [];

  dropdownSettings = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
  dropdownlist() {
    this.spin.show();
    this.sub.add(this.service.Getpartnercode().subscribe(res => {
      this.itemcodeList = res;
      this.filtertemgroupList = this.itemcodeList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({id: x.partnerCode, itemName: x.partnerCode}))
           // this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({id: x.itemCode, itemName: x.itemCode + (x.description == null ? '' : '- ' + x.description)}))
      this.multiSelectitemcodeList = this.multiiitemcodelist;
      
      this.spin.hide();
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }


  submit() {
    this.submitted = true;
    this.form.patchValue({businessPartnerCode: this.selectedItems1[0].id});
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
      this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code).subscribe(res => {
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




