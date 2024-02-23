import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MasterService } from 'src/app/shared/master.service';
import { WarehouseService } from '../../setup/warehouse/warehouse.service';
import { SetupProductService } from '../setup-product.service';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { SelectEventObject } from 'highcharts';
import { CommonService } from 'src/app/common-service/common-service.service';

interface SelectItem {
  item_id: number;
  item_text: string;
}

@Component({
  selector: 'app-item-group',
  templateUrl: './item-group.component.html',
  styleUrls: ['./item-group.component.scss']
})
export class ItemGroupComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  itemGroupId: any;
  warehouseList: any[] = [];

  itemTypeList: any[] = [];
  itemTypeList1: any[] = [];
  itemGroupList: any[] = [];
  storageSectionList: any[] = [];
  storageSectionList1: any[] = [];
  storageClassList: any[] = [];
  storageClassList1: any[] = [];
  subItemGroupList: any[] = [];
  subItemGroupList1: any[] = [];
  itemGroupDescription = '';
  selectedItems: SelectItem[] = [];

  multiList: SelectItem[] = [];
  multiSelectItemGroupList: SelectItem[] = [];

  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

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


  constructor(private fb: FormBuilder, private setupProductService: SetupProductService, private cs: CommonService,
    private route: ActivatedRoute, private warehouseService: WarehouseService,
    private masterService: MasterService, public toastr: ToastrService, private router: Router) {
    this.itemGroupId = this.route.snapshot.params['itemGroupId'];

    if (this.itemGroupId !== undefined && this.itemGroupId !== null) {
      this.getItemGroupDetails();
    }
  }


  ngOnInit(): void {
    this.form = this.fb.group(
      {
        companyId: [],
        createdBy: [],
        createdOn: [],
        updatedBy: [],
        updatedOn: [],
        deletionIndicator: [0],
        itemGroupId: [, [Validators.required]],
        itemTypeId: [, [Validators.required]],
        languageId: ['en'],
        plantId: [],
        storageClassId: [, [Validators.required]],
        storageSectionId: [, [Validators.required]],
        subItemGroupId: [, [Validators.required]],
        warehouseId: [, [Validators.required]]

      });
    console.log(this.route.snapshot.params['itemTypeId']);
    this.form.patchValue({ warehouseId: this.route.snapshot.params['warehouseId'], itemTypeId: Number(this.route.snapshot.params['itemTypeId']) });

    this.dropdownfill();
  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      itemtype: this.masterService.getItemTypeMasterDetails().pipe(catchError(err => of(err))),
      itemgroup: this.masterService.getItemGroupMasterDetails().pipe(catchError(err => of(err))),
      storageSection: this.masterService.getStorageSectionMasterDetails().pipe(catchError(err => of(err))),
      storageClass: this.masterService.getStorageClassMasterDetails().pipe(catchError(err => of(err))),
      subItemGroup: this.masterService.getSubItemGroupMasterDetails().pipe(catchError(err => of(err)))
    })
      .subscribe(({ warehouse, itemtype, itemgroup, storageSection, storageClass, subItemGroup }) => {

        this.warehouseList = warehouse;
        this.itemTypeList1 = itemtype;
        this.itemTypeList1.forEach((x: { itemTypeId: string; itemType: string; }) => this.itemTypeList.push({key: x.itemTypeId, value:  x.itemTypeId + '-' + x.itemType}))
        this.itemTypeList = this.cs.removeDuplicatesFromArraydropdown(this.itemTypeList);

        this.itemGroupList = itemgroup;
        this.storageSectionList1 = storageSection;
        this.storageSectionList1.forEach((x: { storageSectionId: string; description: string; }) => this.storageSectionList.push({key: x.storageSectionId, value:  x.storageSectionId + '-' + x.description}))
        console.log(this.storageSectionList)
        this.storageSectionList = this.cs.removeDuplicatesFromArraydropdown(this.storageSectionList);
        console.log(this.storageSectionList)

        this.storageClassList1 = storageClass;
        this.storageClassList1.forEach((x: { storageClassId: string; description: string; }) => this.storageClassList.push({key: x.storageClassId, value:  x.storageClassId + '-' + x.description}))
        this.storageClassList = this.cs.removeDuplicatesFromArraydropdown(this.storageClassList);

        this.subItemGroupList1 = subItemGroup;
        this.subItemGroupList1.forEach((x: { subItemGroupId: string; description: string; }) => this.subItemGroupList.push({key: x.subItemGroupId, value:  x.subItemGroupId + '-' + x.description}))
        this.subItemGroupList = this.cs.removeDuplicatesFromArraydropdown(this.subItemGroupList);

        this.itemGroupList.forEach(x => this.multiList.push({ item_id: x.itemGroupId, item_text: x.itemGroup }))
        this.multiSelectItemGroupList = this.multiList;

        console.log(this.itemGroupList);

        if (sessionStorage.getItem('itemGroup') != null && sessionStorage.getItem('itemGroup') != undefined) {
          // this.itemGroup = JSON.parse(sessionStorage.getItem('itemGroup') as '{}');
          // this.itemTypeId = this.itemGroup.itemTypeId;
          console.log(sessionStorage.getItem('itemGroup') as '{}');
          this.form.patchValue(JSON.parse(sessionStorage.getItem('itemGroup') as '{}'));
          this.itemGroupId = this.form.controls['itemGroupId'].value;
          let tmp: SelectItem[] = [];
          tmp.push({ item_id: this.form.controls['itemGroupId'].value, item_text: this.itemGroupList.filter(x => x.itemGroupId == this.form.controls['itemGroupId'].value)[0].itemGroup });
          this.selectedItems = tmp;
        }
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

  }

  getItemGroupDetails() {
    this.setupProductService.getProductSetupDetails('itemGroup', this.itemGroupId ? this.itemGroupId : '').subscribe(
      result => {
        this.form.patchValue(result);
      },
      error => {
        console.log(error);
      }
    );
  }

  onSubmit(formDirective: FormGroupDirective) {

    // this.submitted = true;

    // // reset alerts on submit
    // this.alertService.clear();
    console.log(this.selectedItems);
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length != 0) {
      this.form.patchValue({ itemGroupId: this.selectedItems[0].item_id });
    }

    // stop here if form is invalid
    if (this.form.invalid) {
      console.log("invalid");
      return;
    }

    // this.loading = true;
    if (!this.itemGroupId) {
      this.createItemGroup(formDirective);
    } else {
      this.updateItemGroup(formDirective);
    }
  }

  private createItemGroup(formDirective: FormGroupDirective) {

    this.setupProductService.saveProductSetupDetails('itemGroup', this.form.value)
      .subscribe(result => {
        console.log(result);
        // this.alertService.success('User added', { keepAfterRouteChange: true });
        // this.router.navigate(['../'], { relativeTo: this.route });
        this.toastr.success("Item Group details Saved Successfully", "", {
          timeOut: 2000,
          progressBar: false,
        });
        formDirective.resetForm();
        this.form.reset();
        this.selectedItems = [];
      },
        error => {
          console.log(error);
          this.toastr.error(error.error.error, "Error", {
            timeOut: 2000,
            progressBar: false,
          });
          //this.isLoadingResults = false;
        });
    // .add(() => this.loading = false);
  }

  private updateItemGroup(formDirective: FormGroupDirective) {
    //this.form.patchValue({itemGroupId: this.selectedItems[0].item_id});
    this.setupProductService.updateProductSetupDetails('itemGroup', this.form.value)
      .subscribe(() => {
        // this.alertService.success('User updated', { keepAfterRouteChange: true });
        // this.router.navigate(['../../'], { relativeTo: this.route });
        this.toastr.success("Item Group details Updated Successfully", "", {
          timeOut: 2000,
          progressBar: false,
        });
        formDirective.resetForm();
        this.form.reset();
        this.selectedItems = [];
      },
        error => {
          console.log(error);
          this.toastr.error(error.error.error, "Error", {
            timeOut: 2000,
            progressBar: false,
          });
          //this.isLoadingResults = false;
        });
    // .add(() => this.loading = false);
  }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }
}
