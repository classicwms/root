import { Component, OnInit } from '@angular/core';
import { StorageService } from './storage.service';
import { Storage } from 'src/app/models/storage';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { MasterService } from 'src/app/shared/master.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';


interface SelectItem {
  id: string;
  itemName: string;
}

export class StorageCls implements Storage {
  constructor(public companyId?: string, public createdBy?: string, public createdOn?: Date, public deletionIndicator?: number,
    public floorId?: number, public itemGroup?: number, public itemType?: number, public languageId?: string,
    public noAisles?: number, public noRows?: number, public noShelves?: number, public noSpan?: number,
    public noStorageBins?: number, public plantId?: string, public storageSectionId?: string, public storageTypeNo?: string,
    public subItemGroup?: number, public updatedBy?: string, public updatedOn?: Date, public warehouseId?: string) {
  }
}


@Component({
  selector: 'app-storage',
  templateUrl: './storage.component.html',
  styleUrls: ['./storage.component.scss']
})
export class StorageComponent implements OnInit {
  storage = new StorageCls();
  storageSectionIdForEdit?: any;
  companyList: any[] = [];
  plantList: any[] = [];
  warehouseList: any[] = [];
  floorList: any[] = [];
  storageSectionList: any[] = [];
  storageTypeList: any[] = [];
  showingFloorList: any[] = [];
  storageSectionName = '';
  constructor(private storageService: StorageService,private router: Router,
    private route: ActivatedRoute, public toastr: ToastrService, private masterService: MasterService, private spin: NgxSpinnerService) {
    this.storage.companyId = this.route.snapshot.params['companyId'];
    this.storage.plantId = this.route.snapshot.params['plantId'];
    this.storage.warehouseId = this.route.snapshot.params['warehouseId'];
    this.storage.floorId = Number(this.route.snapshot.params['floorId']);
    // this.storage.storageSectionId = this.route.snapshot.params['storageSectionId'];
    // this.storageSectionIdForEdit = this.storage.storageSectionId;

    if (sessionStorage.getItem('storageSection') != null && sessionStorage.getItem('storageSection') != undefined) {
      // this.storage.storageSectionId = JSON.parse(sessionStorage.getItem('storageSection') as '{}'); 
      this.storage = JSON.parse(sessionStorage.getItem('storageSection') as '{}');
      this.storageSectionIdForEdit = this.storage.storageSectionId;
    }


    if (this.storage.storageSectionId !== undefined && this.storage.storageSectionId !== null) {
      this.getStorageDetails();
    }
  }

  ngOnInit(): void {
    this.dropdownfill();
  }
  title1 = "Organisation Setup";
  title2 = "Storage";





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

  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  multiselectstorageList: SelectItem[] = [];
  multistorageList: SelectItem[] = [];
  multiselectstoragetypeList: SelectItem[] = [];
  multistoragetypeList: SelectItem[] = [];

  dropdownSettings = {
    singleSelection: false, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
  dropdownSettings1 = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2,

  };

  dropdownfill() {
    this.spin.show();
    forkJoin({
      //company: this.masterService.getCompanyMasterDetails().pipe(catchError(err => of(err))),
     // plant: this.masterService.getPlantMasterDetails().pipe(catchError(err => of(err))),
     // warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
     // floor: this.masterService.getFloorMasterDetails().pipe(catchError(err => of(err))),
     company: this.masterService.getCompanyEnterpriseDetails().pipe(catchError(err => of(err))),
     plant: this.masterService.getPlantEnterpriseDetails().pipe(catchError(err => of(err))),
     warehouse: this.masterService.getWarehouseEnterpriseDetails().pipe(catchError(err => of(err))),
     floor: this.masterService.getFloorEnterpriseDetails().pipe(catchError(err => of(err))),
      storageSection: this.masterService.getStorageSectionMasterDetails().pipe(catchError(err => of(err))),
      storageType: this.masterService.getStorageTypeMasterDetails().pipe(catchError(err => of(err)))

      //vertical: this.masterService.getVerticalMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ company, plant, warehouse, floor, storageSection, storageType }) => {

        this.companyList = company;
        this.plantList = plant;
        this.warehouseList = warehouse;
        this.floorList = floor;
        this.filterFloor();

        this.storageSectionList = storageSection;
        this.storageSectionList.forEach(x => this.multistorageList.push({ id: x.storageSectionId, itemName: x.storageSectionId }))
        this.multiselectstorageList = this.multistorageList;

        //this.storageTypeList = [{ storageTypeId: 1, description: 'General' }, { storageTypeId: 'ZG', description: 'Storage Ground' }]
        console.log(this.storageSectionList);

        this.storageTypeList = storageType;
        this.storageTypeList.forEach(x => this.multistoragetypeList.push({ id: x.storageTypeId, itemName: x.storageTypeId + '-' + x.description }))
        this.multiselectstoragetypeList = this.multistoragetypeList;

        this.changeStorageSectionName();
        //this.floorList = [{floorId: 1, description: 'First floor'}, {floorId: 2, description: 'Second floor'}]
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  filterFloor() {
    this.showingFloorList = this.floorList.filter(element => {
      return element.companyId === this.storage.companyId && element.plantId === this.storage.plantId && element.warehouseId === this.storage.warehouseId;
    });
  }


  changeStorageSectionName() {
    let singleStorageSection = this.storageSectionList.filter(x => x.storageSectionId == this.storage.storageSectionId)
    this.storageSectionName = singleStorageSection[0].description;
    console.log(this.storageSectionName);
  }

  getStorageDetails() {
    this.storageService.getStorageDetails(this.storage.storageSectionId).subscribe(
      result => {
        console.log(result);
        this.storage = result.data;

        if (result.storageSectionId != null) {
          let filteredStorage = this.multiselectstorageList.filter(x => x.id == result.storageSectionId);
          let selectItem: SelectItem[] = [];
          if (filteredStorage != null && filteredStorage != undefined && filteredStorage.length > 0) {
            selectItem.push({ id: filteredStorage[0].id, itemName: filteredStorage[0].itemName });
            this.selectedItems = selectItem;
          }
        }

        if (result.city != null) {
          let filteredStorageType = this.multiselectstoragetypeList.filter(x => x.id == result.storageTypeId);
          let selectItem: SelectItem[] = [];
          if (filteredStorageType != null && filteredStorageType != undefined && filteredStorageType.length > 0) {
            selectItem.push({ id: filteredStorageType[0].id, itemName: filteredStorageType[0].itemName });
            this.selectedItems1 = selectItem;
          }
        }
      },
      error => {
        console.log(error);
      }
    );
  }

  onSubmit() {
    this.storage.storageSectionId = this.selectedItems[0].id;
    this.storage.storageTypeNo = this.selectedItems1[0].id;
    if ((this.storage.companyId != null && this.storage.companyId != undefined) &&
      (this.storage.plantId != null && this.storage.plantId != undefined) &&
      (this.storage.warehouseId != null || this.storage.warehouseId != undefined) &&
      (this.storage.floorId != null || this.storage.floorId != undefined) &&
      (this.storage.storageSectionId != null || this.storage.storageSectionId != undefined) &&
      (this.storage.storageTypeNo != null || this.storage.storageTypeNo != undefined)) {
      if (!this.storageSectionIdForEdit) {
        this.saveStorageDetails();
      }
      else {
        this.updateStorageDetails();
      }
    }
    else {
      this.toastr.error("Please fill the required fields","",{
        timeOut: 2000,
        progressBar: false,
      });
    }
  }

  saveStorageDetails() {
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
      this.storage.storageSectionId = this.selectedItems[0].id;
    }

    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.storage.storageTypeNo = this.selectedItems1[0].id;
    }

    this.storage.languageId = 'en';
    this.storageService.saveStorageDetails(this.storage).subscribe(
      result => {
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);
        this.toastr.success("Storage section details Saved Successfully","",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/setup/selection']);
      },
      error => {
        //this.isLoadingResults = false;
        this.toastr.error(error.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      }
    );
  }

  updateStorageDetails() {
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
      this.storage.storageSectionId = this.selectedItems[0].id;
    }

    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.storage.storageTypeNo = this.selectedItems1[0].id;
    }

    this.storageService.updateStorageDetails(this.storage).subscribe(
      result => {
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);
        this.toastr.success("Storage section details updated successfully","",{
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(['/main/setup/selection']);
      },
      error => {
        //this.isLoadingResults = false;
        this.toastr.error(error.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      }
    );
  }
  onItemSelect(item: any) {
    console.log(item);
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
