import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Warehouse } from 'src/app/models/warehouse';
import { MasterService } from 'src/app/shared/master.service';
import { WarehouseService } from './warehouse.service';

interface SelectItem {
  id: string;
  itemName: string;
}


export class WarehouseCls implements Warehouse {
  constructor(public address1?: string, public address2?: string, public city?: string, public companyId?: string,
    public contactName?: string, public country?: string, public createdBy?: string, public createdOn?: Date,
    public inboundQACheck?: boolean, public deletionIndicator?: number, public designation?: string,
    public email?: string, public languageId?: string, public latitude?: number, public length?: number,
    public longitude?: number, public modeOfImplementation?: string, public noAisles?: number,
    public outboundQACheck?: boolean, public phoneNumber?: string, public plantId?: string,
    public state?: string, public storageMethod?: string, public totalArea?: number,
    public uom?: string, public updatedBy?: string, public updatedOn?: Date, public warehouseId?: string,
    public warehouseTypeId?: number, public width?: number, public zipCode?: number, public zone?: string) {
  }
}


@Component({
  selector: 'app-warehouse',
  templateUrl: './warehouse.component.html',
  styleUrls: ['./warehouse.component.scss']
})
export class WarehouseComponent implements OnInit {
  warehouse = new WarehouseCls();
  warehouseName = '';
  companyList: any[] = [];
  warehouseIdForEdit?: any;
  storageDetails?: any;

  cityList: any[] = [];
  currencyList: any[] = [];
  stateList: any[] = [];
  countryList: any[] = [];
  plantList: any[] = [];
  warehouseList: any[] = [];
  vertical: any[] = [];

  implementationList: any[] = [{ implementation: 'ENTERPRISE' }];
  warehouseFunctionList: any[] = [{ warehouseTypeId: 1, warehouseFunction: 'INTEGRATED' }];
  constructor(private warehouseService: WarehouseService,
    private masterService: MasterService, private route: ActivatedRoute,
    private spin: NgxSpinnerService, public toastr: ToastrService, private router: Router
  ) {
    this.warehouse.companyId = this.route.snapshot.params['companyId'];
    this.warehouse.plantId = this.route.snapshot.params['plantId'];
    // this.warehouse.warehouseId = this.route.snapshot.params['warehouseId'];
    // this.warehouseIdForEdit = this.warehouse.warehouseId;

    if (sessionStorage.getItem('storageSection') != null && sessionStorage.getItem('storageSection') != undefined) {
      this.storageDetails = JSON.parse(sessionStorage.getItem('storageSection') as '{}');
      this.warehouse.warehouseId = this.storageDetails.warehouseId;
      this.warehouse.companyId = this.storageDetails.companyId;
      this.warehouse.plantId = this.storageDetails.plantId;
      this.warehouseIdForEdit = this.warehouse.warehouseId;
    }
  }

  ngOnInit(): void {
    this.dropdownfill();
  }
  title1 = "Organisation Setup";
  title2 = "Warehouse";


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
  selectedItems2: SelectItem[] = [];
  selectedItems3: SelectItem[] = [];
  selectedItems4: SelectItem[] = [];
  selectedItems6: SelectItem[] = [];
  multiselectcompanyList: SelectItem[] = [];
  multiselectwarehouseList: SelectItem[] = [];
  multiselectcountryList: SelectItem[] = [];
  multiselectcityList: SelectItem[] = [];
  multicompanyList: SelectItem[] = [];
  multiwarehouseList: SelectItem[] = [];
  multiselectstateList: SelectItem[] = [];
  multistateList: SelectItem[] = [];
  multicountryList: SelectItem[] = [];
  multicityList: SelectItem[] = [];

  dropdownSettings = {
    singleSelection: false,
    text: "Select",
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

  dropdownfill() {
    this.spin.show();
    forkJoin({
      // company: this.masterService.getCompanyMasterDetails().pipe(catchError(err => of(err))),
      company: this.masterService.getCompanyEnterpriseDetails().pipe(catchError(err => of(err))),
      city: this.masterService.getCityMasterDetails().pipe(catchError(err => of(err))),
      //currency: this.masterService.getCurrencyMasterDetails().pipe(catchError(err => of(err))),
      state: this.masterService.getStateMasterDetails().pipe(catchError(err => of(err))),
      country: this.masterService.getCountryMasterDetails().pipe(catchError(err => of(err))),
      //   plant: this.masterService.getPlantMasterDetails().pipe(catchError(err => of(err))),
      plant: this.masterService.getPlantEnterpriseDetails().pipe(catchError(err => of(err))),
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err)))
      //vertical: this.masterService.getVerticalMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ company, city, state, country, plant, warehouse }) => {

        this.companyList = company;

        this.cityList = city;
        this.cityList.forEach(x => this.multicityList.push({ id: x.cityName, itemName: x.cityName }))
        this.multiselectcityList = this.multicityList;
        //this.currencyList = currency;
        //this.currencyList = [{currencyDescription: 'INR'}, {currencyDescription: 'USD'}];

        this.stateList = state;
        this.stateList.forEach(x => this.multistateList.push({ id: x.stateName, itemName: x.stateName }))
        this.multiselectstateList = this.multistateList;

        this.countryList = country;
        this.countryList.forEach(x => this.multicountryList.push({ id: x.countryId, itemName: x.countryName }))
        this.multiselectcountryList = this.multicountryList;

        this.plantList = plant;
        console.log(this.plantList);

        this.warehouseList = warehouse;
        this.warehouseList.forEach(x => this.multiwarehouseList.push({ id: x.warehouseId, itemName: x.warehouseId }))
        this.multiselectwarehouseList = this.multiwarehouseList;

        this.changeWarehouseName();

        if (this.warehouse.warehouseId !== undefined && this.warehouse.warehouseId !== null) {
          this.getWarehouseDetails();
        }
        //this.vertical = vertical;
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  getWarehouseDetails() {
    this.warehouseService.getWarehouseDetails(this.warehouse.warehouseId ? this.warehouse.warehouseId : '', 1, 'ENTERPRISE').subscribe(
      result => {
        console.log(result);
        this.warehouse = result;

        if (result.warehouseId != null) {
          let filteredWarehouse = this.multiselectwarehouseList.filter(x => x.id == result.warehouseId);
          let selectItem: SelectItem[] = [];
          if (filteredWarehouse != null && filteredWarehouse != undefined && filteredWarehouse.length > 0) {
            selectItem.push({ id: filteredWarehouse[0].id, itemName: filteredWarehouse[0].itemName });
            this.selectedItems6 = selectItem;
          }
        }

        if (result.city != null) {
          let filteredCity = this.multiselectcityList.filter(x => x.id == result.city);
          let selectItem: SelectItem[] = [];
          if (filteredCity != null && filteredCity != undefined && filteredCity.length > 0) {
            selectItem.push({ id: filteredCity[0].id, itemName: filteredCity[0].itemName });
            this.selectedItems4 = selectItem;
          }
        }

        if (result.state != null) {
          let filteredState = this.multiselectstateList.filter(x => x.id == result.state);
          let selectItem: SelectItem[] = [];
          if (filteredState != null && filteredState != undefined && filteredState.length > 0) {
            selectItem.push({ id: filteredState[0].id, itemName: filteredState[0].itemName });
            this.selectedItems2 = selectItem;
          }
        }

        if (result.country != null) {
          let filteredCountry = this.multiselectcountryList.filter(x => x.id == result.country);
          let selectItem: SelectItem[] = [];
          if (filteredCountry != null && filteredCountry != undefined && filteredCountry.length > 0) {
            selectItem.push({ id: filteredCountry[0].id, itemName: filteredCountry[0].itemName });
            this.selectedItems3 = selectItem;
          }
        }
      },
      error => {
        console.log(error);
      }
    );
  }

  changeWarehouseName() {
    let singleWarehouse = this.warehouseList.filter(x => x.warehouseId == this.warehouse.warehouseId)
    console.log(singleWarehouse);
    if (singleWarehouse != null && singleWarehouse != undefined && singleWarehouse.length > 0) {
      this.warehouseName = singleWarehouse[0].description;
    }
    console.log(this.warehouseName);
  }


  onSubmit() {
    // // let element: HTMLElement = document.getElementById('btnsave') as HTMLElement;
    // element.click();

    //this.isLoadingResults = true;
    //this.userDetails.instance_id = this.instance.id;
    if ((this.warehouse.companyId != null && this.warehouse.companyId != undefined) &&
      (this.warehouse.plantId != null && this.warehouse.plantId != undefined) &&
      (this.selectedItems6 != null || this.selectedItems6 != undefined) &&
      (this.warehouse.warehouseTypeId != null || this.warehouse.warehouseTypeId != undefined) &&
      (this.warehouse.modeOfImplementation != null || this.warehouse.modeOfImplementation != undefined)) {
      if (!this.warehouseIdForEdit) {
        this.saveWarehouseDetails();
      }
      else {
        this.updateWarehouseDetails();
      }
    }
    else {
      this.toastr.error("Please fill the required fields", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
  }

  saveWarehouseDetails() {
    if (this.selectedItems6 != null && this.selectedItems6 != undefined && this.selectedItems6.length > 0) {
      this.warehouse.warehouseId = this.selectedItems6[0].id;
    }

    if (this.selectedItems4 != null && this.selectedItems4 != undefined && this.selectedItems4.length > 0) {
      this.warehouse.city = this.selectedItems4[0].itemName;
    }

    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
      this.warehouse.state = this.selectedItems2[0].itemName;
    }

    if (this.selectedItems3 != null && this.selectedItems3 != undefined && this.selectedItems3.length > 0) {
      this.warehouse.country = this.selectedItems3[0].id;
    }
    this.warehouse.zipCode = Number(this.warehouse.zipCode);
    this.warehouse.languageId = 'en';
    this.warehouseService.saveWarehouseDetails(this.warehouse).subscribe(
      result => {
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);
        this.toastr.success("Warehouse details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/floor', this.warehouse.companyId, this.warehouse.plantId, this.warehouse.warehouseId]);
      },
      error => {
        this.toastr.error(error.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
        //this.isLoadingResults = false;
      }
    );
  }

  updateWarehouseDetails() {
    console.log(this.warehouse);

    if (this.selectedItems6 != null && this.selectedItems6 != undefined && this.selectedItems6.length > 0) {
      this.warehouse.warehouseId = this.selectedItems6[0].id;
    }

    if (this.selectedItems4 != null && this.selectedItems4 != undefined && this.selectedItems4.length > 0) {
      this.warehouse.city = this.selectedItems4[0].itemName;
    }

    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
      this.warehouse.state = this.selectedItems2[0].itemName;
    }

    if (this.selectedItems3 != null && this.selectedItems3 != undefined && this.selectedItems3.length > 0) {
      this.warehouse.country = this.selectedItems3[0].id;
    }
    this.warehouse.zipCode = Number(this.warehouse.zipCode);

    this.warehouseService.updateWarehouseDetails(this.warehouse).subscribe(
      result => {
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);
        this.toastr.success("Warehouse details updated successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/floor', this.warehouse.companyId, this.warehouse.plantId, this.warehouse.warehouseId]);
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
    //this.warehouse.warehouseId = this.selectedItems6[0].id;
    console.log(item);

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
