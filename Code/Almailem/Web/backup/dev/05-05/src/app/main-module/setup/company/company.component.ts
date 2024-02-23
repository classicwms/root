import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of, Subscription } from 'rxjs';
import { PeriodicElement } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { SubsidenavComponent } from '../../../common-field/subsidenav/subsidenav.component';
import { ErrorStateMatcher } from '@angular/material/core';
import { ToastrService } from 'ngx-toastr';
import { SetupService } from '../setup.service';
import { ThisReceiver } from '@angular/compiler';
import { catchError } from 'rxjs/operators';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from 'src/app/common-service/common-service.service';
import { WmsIdmasterService } from '../../wms-idmaster-service.service';
import { AuthService } from 'src/app/core/core';
import { Company } from 'src/app/models/company';
import { CompanyService } from './company.service';
import { MasterService } from 'src/app/shared/master.service';
import { IDropdownSettings } from 'ng-multiselect-dropdown';

export class CompanyCls implements Company {
  constructor(public address1?: string, public address2?: string, public city?: any, public companyId?: string,
    public contactName?: string, public country?: string, public createdBy?: string, public createdOn?: Date,
    public currencyId?: number, public deletionIndicator?: number, public designation?: string,
    public email?: string, public languageId?: string, public noOfOutlets?: number, public noOfPlants?: number,
    public noOfWarehouses?: number, public phoneNumber?: string, public registrationNo?: string,
    public state?: string, public updatedBy?: string, public updatedOn?: Date, public verticalId?: string,
    public zipCode?: number) {

  }
}


interface SelectItem {
  id: string;
  itemName: string;
}

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.scss'],
})
export class CompanyComponent implements OnInit, OnDestroy {
  sub = new Subscription();
  company = new CompanyCls();
  companyIdForEdit?: any;
  companyName = '';
  storageDetails: any;

  // sform = this.fb.group(
  //   {
  //     companyId: [1, [Validators.required]],
  //     companyName: [, [Validators.required]],
  //     verticalId: [, [Validators.required]],
  //     currency: [, [Validators.required]],


  //     address1: [, [Validators.required]],
  //     address2: [],
  //     city: [, [Validators.required]],
  //     state: [, [Validators.required]],
  //     country: [, [Validators.required]],
  //     zipCode: [, [Validators.required]],

  //     contactName: [, [Validators.required]],
  //     designation: [],
  //     phoneNumber: [, [Validators.required]],
  //     email: [, [Validators.required, Validators.email]],


  //     noOfOutlets: [0],
  //     noOfPlants: [0],
  //     noOfWarehouses: [0],
  //     createdBy: []

  //   });
  companyList: any[] = [];

  cityList: any[] = [];
  currencyList: any[] = [];
  stateList: any[] = [];
  countryList: any[] = [];
  vertical: any[] = [];



  constructor(private router: Router,
    private fb: FormBuilder,
    public toastr: ToastrService,
    public service: WmsIdmasterService,
    private cs: CommonService,
    private spin: NgxSpinnerService,
    private setupService: SetupService,
    private auth: AuthService,
    private companyService: CompanyService,
    private masterService: MasterService,
    private route: ActivatedRoute
  ) {
    if (sessionStorage.getItem('storageSection') != null && sessionStorage.getItem('storageSection') != undefined) {
      this.storageDetails = JSON.parse(sessionStorage.getItem('storageSection') as '{}');
      this.company.companyId = this.storageDetails.companyId;
      this.companyIdForEdit = this.company.companyId;
    }


  }

  ngOnInit() {
    this.initForm();
    this.dropdownfill();
    this.fill();
  }



  //#region common
  // for header title
  title1 = "Organisation Setup";
  title2 = "Company";

  // for section control
  section: any = [1, 2, 3, 4];
  currentItemsToShow = [];
  tabcheck(list: any, id: any) {
    return this.section.includes(id) && list == id;
  }
  updatecurrentItemsToShow($event: any) {
    this.currentItemsToShow = $event;
  }

  //validation
  submitted = false;
  public errorHandling = (control: string, error: string) => {
    //return this.sform.controls[control].hasError(error) && this.submitted;
  }

  edit() {
    //this.sform.enable();
    // this.sform.controls.companyid.disable();

  }
  initForm() {

    //this.sform.disable();
    // this.sub.add(this.sform.controls.companyId.valueChanges.subscribe(res => {
    //   var description: any = this.companyList.filter(x => x.companyId === res)[0];
    //   if (description)
    //     this.sform.patchValue({ companyName: description.companyDescription });
    //   this.sub.add(this.setupService.getcompany(res).subscribe(res => { console.log(res); }, err => { }));
    // }));
    // this.sub.add(this.sform.controls.city.valueChanges.subscribe(res => {
    //   var filter: any = this.cityList.filter(x => x.cityName === res)[0];
    //   if (filter)
    //     this.sform.patchValue({ zipCode: filter.zipCode });
    // }));
  }

  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  selectedItems2: SelectItem[] = [];
  selectedItems3: SelectItem[] = [];
  multiselectcompanyList: SelectItem[] = [];
  multiselectcountryList: SelectItem[] = [];
  multiselectcityList: SelectItem[] = [];
  multiselectstateList: SelectItem[] = [];
  multicompanyList: SelectItem[] = [];
  multicountryList: SelectItem[] = [];
  multicityList: SelectItem[] = [];
  multistateList: SelectItem[] = [];

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
      company: this.masterService.getCompanyMasterDetails().pipe(catchError(err => of(err))),
      city: this.masterService.getCityMasterDetails().pipe(catchError(err => of(err))),
      //currency: this.masterService.getCurrencyMasterDetails().pipe(catchError(err => of(err))),
      state: this.masterService.getStateMasterDetails().pipe(catchError(err => of(err))),
      country: this.masterService.getCountryMasterDetails().pipe(catchError(err => of(err))),
      vertical: this.masterService.getVerticalMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ company, city, state, country, vertical }) => {
        this.companyList = company;
        this.companyList.forEach(x => this.multicompanyList.push({ id: x.companyCodeId, itemName: x.companyCodeId + '-' + x.description }))
        this.multiselectcompanyList = this.multicompanyList;


        this.cityList = city;
        console.log(this.cityList);
        this.cityList.forEach(x => this.multicityList.push({ id: x.cityId, itemName: x.cityName }))
        this.multiselectcityList = this.multicityList;

        console.log(this.multiselectcityList);
        console.log(this.multicityList);

        //this.currencyList = currency;
        this.currencyList = [{ currencyId: 1, currencyDescription: 'INR' }, { currencyId: 2, currencyDescription: 'USD' }];

        this.stateList = state;
        console.log(this.stateList);
        this.stateList.forEach(x => this.multistateList.push({ id: x.stateName, itemName: x.stateName }))
        this.multiselectstateList = this.multistateList;

        console.log(this.multiselectstateList);
        console.log(this.multistateList);

        this.countryList = country;
        this.countryList.forEach(x => this.multicountryList.push({ id: x.countryName, itemName: x.countryName }))
        this.multiselectcountryList = this.multicountryList;

        this.vertical = vertical;
        this.changeCompanyName();

        if (this.company.companyId !== undefined && this.company.companyId !== null) {
          this.getCompanyDetails();
        }
        // setTimeout(() =>{
        // if (this.company.companyId !== undefined && this.company.companyId !== null) {
        //       this.getCompanyDetails();
        // }
        // }, 5000);
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();
    // if (this.company.companyId !== undefined && this.company.companyId !== null) {
    //           this.getCompanyDetails();
    //         }
  }
  fill() {
    // this.spin.show();
    // this.sub.add(this.setupService.getcompany('1000').subscribe(res => {
    //   this.sform.patchValue(res);
    //   this.spin.hide();
    // }, err => {
    //   this.toastr.error(err, "");
    //   this.spin.hide();
    // }));
  }

  changeCompanyName() {
    let singleCompany = this.companyList.filter(x => x.companyCodeId == this.company.companyId)
console.log(this.selectedItems[0].itemName)

let array = this.selectedItems[0].itemName.split('-');
console.log(array[1])
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
      this.companyName = array[1];
    }
  }


  submit() {
    this.submitted = true;

    // if (this.sform.invalid) {
    //   this.toastr.error(
    //     "Please fill required fields to continue",
    //     ""
    //   );

    //   this.cs.notifyOther(true);
    //   return;
    // }

    this.cs.notifyOther(false);
    this.spin.show();

    //this.sform.patchValue({ createdBy: this.auth.username });
    // this.sub.add(this.setupService.patchcompany(this.sform.controls.companyId.value, this.sform.getRawValue()).subscribe(res => {
    //   this.toastr.success("saved");
    //   this.spin.hide();

    // }, err => {
    //   this.toastr.error(err, "");
    //   this.spin.hide();

    // }));
  };

  getCompanyDetails() {
    console.log("Hit Company");
    this.companyService.getCompanyDetails(this.company.companyId ? this.company.companyId : '').subscribe(
      result => {
        console.log(result);
        this.company = result;

        if (result.companyId != null) {
          let filteredCompany = this.multiselectcompanyList.filter(x => x.id == result.companyId);
          let selectItem: SelectItem[] = [];
          if (filteredCompany != null && filteredCompany != undefined && filteredCompany.length > 0) {
            selectItem.push({ id: filteredCompany[0].id, itemName: filteredCompany[0].itemName });
            this.selectedItems = selectItem;
          }
        }

        if (result.city != null) {
          let filteredCity = this.multiselectcityList.filter(x => x.id == result.city);
          let selectItem: SelectItem[] = [];
          if (filteredCity != null && filteredCity != undefined && filteredCity.length > 0) {
            selectItem.push({ id: filteredCity[0].id, itemName: filteredCity[0].itemName });
            this.selectedItems1 = selectItem;
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
        // this.company.city=[{id: result.cityId,itemName: result.cityName}];
        // this.selectedItems1.push({id: result.cityId,itemName: result.cityName})
        //     const cityname=([{id: result.city,itemName: result.city}]);
        //     const city1=cityname[0].itemName;
        //     console.log(cityname);
        //  this.company.city=city1
        //  console.log(this.company.city)
      },
      error => {
        console.log(error);
      }
    );
  }

  //save 
  onSubmit() {
    // // let element: HTMLElement = document.getElementById('btnsave') as HTMLElement;
    // element.click();

    //this.isLoadingResults = true;
    //this.userDetails.instance_id = this.instance.id;
    if ((this.selectedItems != null && this.selectedItems != undefined) &&
      (this.company.verticalId != null && this.company.verticalId != undefined) &&
      (this.company.currencyId != null || this.company.currencyId != undefined)) {
      if (!this.companyIdForEdit) {
        this.saveCompanyDetails();
      }
      else {
        this.updateCompanyDetails();
      }
    }
    else {
      this.toastr.error("Please fill the required fields", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
  }

  saveCompanyDetails() {
    if (this.selectedItems3 != null && this.selectedItems3 != undefined && this.selectedItems3.length > 0) {
      this.company.country = this.selectedItems3[0].id;
    }
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
      this.company.companyId = this.selectedItems[0].id;
    }
    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.company.city = this.selectedItems1[0].id;
    }

    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
      this.company.state = this.selectedItems2[0].id;
    }
    this.company.zipCode = Number(this.company.zipCode);
    this.company.languageId = 'en';
    this.companyService.saveCompanyDetails(this.company).subscribe(
      result => {
        console.log(result);
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        this.toastr.success("Company details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/plant', this.company.companyId]);
      },
      error => {
        console.log(error);
        this.toastr.error(error.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
        //this.isLoadingResults = false;
      }
    );
  }

  updateCompanyDetails() {
    if (this.selectedItems3 != null && this.selectedItems3 != undefined && this.selectedItems3.length > 0) {
      this.company.country = this.selectedItems3[0].id;
    }

    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
      this.company.companyId = this.selectedItems[0].id;
    }

    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.company.city = this.selectedItems1[0].id;
    }

    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
      this.company.state = this.selectedItems2[0].id;
    }

    this.company.zipCode = Number(this.company.zipCode);
    this.companyService.updateCompanyDetails(this.company).subscribe(
      result => {
        console.log(result);
        this.toastr.success("Company Details updated successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/plant', this.company.companyId]);
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);
      },
      error => {
        console.log(error);
        this.toastr.error(error.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
        //this.isLoadingResults = false;
      }
    );
  }

  //#endregion 

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
    // this.company.companyId = this.selectedItems[0].id;
   this.changeCompanyName();
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
