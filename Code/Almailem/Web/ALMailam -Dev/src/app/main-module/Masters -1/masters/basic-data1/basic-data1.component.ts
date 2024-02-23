import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterproductService } from '../masterproduct.service';
import { Basicdata1Service, basicdataelement } from './basicdata1.service';

interface SelectItem {
  id: number;
  itemName: string;
}



@Component({
  selector: 'app-basic-data1',
  templateUrl: './basic-data1.component.html',
  styleUrls: ['./basic-data1.component.scss']
})
export class BasicData1Component implements OnInit {


  filteritemtypeList: any[] = [];
  filteritemgroupList: any[] = [];
  filterstoragesectionList: any[] = [];
  sub = new Subscription();
  masterProduct: any;
  itemCode: any;
  warehouse: any;
  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({

    companyCodeId: ['1000'],
    createdby: [this.auth.userID,],
    createdon: [],
    deletionIndicator: [],
    description: [, Validators.required],
    eanUpcNo: [],
    hsnCode: [],
    itemCode: [, Validators.required],
    itemGroup: [],
    itemType: [],
    languageId: ['EN'],
    manufacturerPartNo: [],
    maximumStock: [],
    minimumStock: [],
    model: [],
    plantId: ['1001'],
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
    updatedby: [],
    updatedon: [],
    warehouseId: [this.auth.warehouseId],

  });


  submitted = false;
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }

  // getErrorMessage() {
  //   if (this.email.hasError('required')) {
  //     return ' Field should not be blank';
  //   }
  //   return this.email.hasError('email') ? 'Not a valid email' : '';
  // }
  formgr: basicdataelement | undefined;
  constructor(
    private router: Router,
    private service: Basicdata1Service,
    public toastr: ToastrService,
    private cas: Basicdata1Service,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    private cs: CommonService,
    private route: ActivatedRoute
    //  private route: ActivatedRoute
  ) {
    //this.form.controls.itemType.patchValue (this.route.snapshot.params['itemType']);
    if (sessionStorage.getItem('masterProduct') != null && sessionStorage.getItem('masterProduct') != undefined) {
      this.masterProduct = JSON.parse(sessionStorage.getItem('masterProduct') as '{}');
      this.itemCode = this.masterProduct.itemCode;
      this.warehouse = this.masterProduct.warehouseId;
      console.log(this.masterProduct);

      // this.company.companyId = this.storageDetails.companyId;
      // this.companyIdForEdit = this.company.companyId;
    }
  }


  ngOnInit(): void {
    this.dropdownlist();
    this.form.controls.warehouseId.disable();
  }


  warehouseIdList: any[] = [];
  itemgroupList: any[] = [];
  itemTypeIdList: any = [];
  storagesectionList: any = [];
  selectedItems: any[] = [];
  selectedItems1: any[] = [];
  selectedItems2: any[] = [];
  multiitemlistList: any[] = [];
  multiSelectItemGroupList: any[] = [];
  multiitemtypelist: any[] = [];
  multiSelectItemtypeList: any[] = [];
  multistoragelist: any[] = [];
  multiSelectstorageList: any[] = [];

  dropdownSettings = {
    singleSelection: true,
    text: "Select",
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

  getItemDetails() {
    this.sub.add(this.service.Get(this.itemCode, this.warehouse).subscribe(
      result => {
        console.log(result);
        this.form.patchValue(result, { emitEvent: false });
        if (result.itemType != null) {
          let filteredItemType = this.multiSelectItemtypeList.filter(x => x.value = result.itemType);
          let selectItem: SelectItem[] = [];
          selectItem.push({ id: filteredItemType[0].id, itemName: filteredItemType[0].itemName });
          this.selectedItems = selectItem;
        }



        if (result.itemGroup != null) {
          let filteredItemGroup = this.multiSelectItemGroupList.filter(x => x.value = result.itemGroup);
          let selectItem2: SelectItem[] = [];
          selectItem2.push({ id: filteredItemGroup[0].id, itemName: filteredItemGroup[0].itemName });
          this.selectedItems2 = selectItem2;
          console.log(this.selectedItems2);
        }

        if (result.storageSectionId != null) {
          let filteredStorageSection = this.multiSelectstorageList.filter(x => x.value = result.storageSectionId);
          let selectItem1: SelectItem[] = [];
          selectItem1.push({ id: filteredStorageSection[0].id, itemName: filteredStorageSection[0].itemName });
          this.selectedItems1 = selectItem1;
        }
        // result.itemType = null;
        // result.itemGroup = null;
        // result.storageSectionId = null;
        // this.form.patchValue(result);
      },
      error => {
        console.log(error);
      }
    ));
  }


  dropdownlist() {
    this.spin.show();
    this.sub.add(this.service.GetWh().subscribe(res => {
      this.warehouseIdList = res;
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
    this.sub.add(this.service.Getitemgroup().subscribe(res => {
      this.itemgroupList = res;
      this.filteritemgroupList = this.itemgroupList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filteritemgroupList.forEach(x => this.multiitemlistList.push({ value: x.itemGroupId, label: x.itemGroup }))
      this.multiSelectItemGroupList = this.multiitemlistList;
      this.spin.hide();

    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    this.sub.add(this.service.Getitemtype().subscribe(res => {
      this.itemTypeIdList = res;
      this.filteritemtypeList = this.itemTypeIdList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filteritemtypeList.forEach(x => this.multiitemtypelist.push({ value: x.itemTypeId, label: x.itemType }))
      this.multiSelectItemtypeList = this.multiitemtypelist;
      this.spin.hide();

    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    this.sub.add(this.service.Getstorage().subscribe(res => {
      this.storagesectionList = res;
      this.filterstoragesectionList = this.storagesectionList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;

      });
      this.filterstoragesectionList.forEach(x => this.multistoragelist.push({ value: x.storageSectionId, label: x.storageSectionId + '-' + x.storageSection }))
      this.multiSelectstorageList = this.multistoragelist;
      console.log(this.multiSelectstorageList)
      this.multiSelectstorageList = this.cs.removeDuplicatesFromArrayNewstatus(this.multiSelectstorageList)
      console.log(this.multiSelectstorageList)
      this.spin.hide();

    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));

    if (this.itemCode !== undefined && this.itemCode !== null) {
      this.getItemDetails();
    }
  }
  save() {
    if (this.itemCode != null && this.itemCode != undefined && this.itemCode != '') {
      this.updateCompanyDetails();
    }
    else {
      this.saveCompanyDetails();
    }
  }

  saveCompanyDetails() {
    // if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
    //   this.form.patchValue({ itemType: this.selectedItems[0].id });
    // }

    // if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
    //   this.form.patchValue({ storageSectionId: this.selectedItems1[0].id });
    // }

    // if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
    //   this.form.patchValue({ itemGroup: this.selectedItems2[0].id });
    // }
    this.form.patchValue({ updatedby: this.auth.username });
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(
      result => {
        console.log(result);
        // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        this.toastr.success("Basic Data details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/masters/Partner', this.form.controls.itemCode.value]);
      }, (err: any) => {
        this.cs.commonerrorNew(err);
        //this.isLoadingResults = false;
      }
    ));
  }

  updateCompanyDetails() {
    // if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
    //   this.form.patchValue({ itemType: this.selectedItems[0].id });
    // }

    // if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
    //   this.form.patchValue({ storageSectionId: this.selectedItems1[0].id });
    // }

    // if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
    //   this.form.patchValue({ itemGroup: this.selectedItems2[0].id });
    // }
    this.form.patchValue({ updatedby: this.auth.username });
    this.sub.add(this.service.Update(this.form.getRawValue(), this.itemCode).subscribe(
      result => {
        console.log(result);
        // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        this.toastr.success("Basic Data details Updated Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/masters/selection']);
      }, (err: any) => {
        this.cs.commonerrorNew(err);
        //this.isLoadingResults = false;
      }
    ));
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

    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.companyCodeId + " Saved Successfully!", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();


    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));
  };

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }


  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }
  onItemSelect(item: any) {
    console.log(item);
    console.log(this.selectedItems)
  }

  OnItemDeSelect(item: any) {
    console.log(item);
    console.log(this.selectedItems);
  }
  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any) {
    console.log(items);
  }
}