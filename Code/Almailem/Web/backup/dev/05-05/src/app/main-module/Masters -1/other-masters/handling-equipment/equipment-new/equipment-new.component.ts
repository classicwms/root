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
    handlingEquipmentId: [,Validators.required],
    handlingUnit: [],
    category: [],
    acquistionDate: [],
    acquistionValue: [],
    currencyId: [],
    modelNo: [],
    languageId: ['EN'],
    warehouseId: [this.auth.warehouseId],
    plantId: ['1001'],
    companyCodeId: ['1000'],
    manufacturerPartNo: [],
    countryOfOrigin: [],
    heBarcode: [],
    createdOn: [],
    statusId: ["1"],
    createdBy: [this.auth.username],
    updatedBy: [this.auth.username],
    updatedOn: [],
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
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
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

    this.filteredOptions = this.form.controls.handlingUnit.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value)),
    );
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.handlingunitList.filter(option => option.toLowerCase().includes(filterValue));
  }


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

  handlingunitList: any = [];
  dropdownlist() {
    this.spin.show();
    this.sub.add(this.service.Gethandlingunit().subscribe(res => {
      this.handlingunitList = res;
      this.filterheItems = this.handlingunitList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;

      }); 
      this.filterheItems.forEach(x => this.multiiitemcodelist.push({id: x.handlingUnit, itemName: x.handlingUnit}))
      this.multiSelectheList = this.multiiitemcodelist;
    //  if (this.data.pageflow != 'New') {
    //   //   let tmp:SelectItem[] = [];
    //   //       tmp.push({id: this.form.controls['handlingUnit'].value, itemName: this.multiSelectheList.filter(x => x.id == this.form.controls['handlingUnit'].value)[0].itemName});
    //   //       console.log(tmp);
    //   //       this.selectedItems = tmp;
    //   let temphandlingUnitList: dropdownelement[] = []
    //  const handlingUnit = [...new Set(res.map(item => item.handlingUnit))].filter(x => x != null)
    //  handlingUnit.forEach(x => temphandlingUnitList.push({ id: x, itemName: x }));
    //  this.multiSelectheList = temphandlingUnitList;
    //    }

      this.spin.hide();
      
    }, 
    err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code).subscribe(res => {
      console.log(res)
      this.form.patchValue(res, { emitEvent: false });
     // this.selectedItems.push({id: res.handlingUnit,itemName: res.handlingUnit,})
      this.form.controls.handlingUnit.patchValue([{id: res.handlingUnit,itemName: res.handlingUnit}]);
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

  submit() {
    this.submitted = true;
    this.form.patchValue({handlingUnit: this.selectedItems[0].id});
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
    this.form.patchValue({floorId: this.selectedItems[0].id});
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
        this.toastr.success(res.handlingEquipmentId + " Saved Successfully!","Notification",{
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




