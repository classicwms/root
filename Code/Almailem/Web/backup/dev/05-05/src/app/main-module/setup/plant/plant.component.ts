import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, Validators, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { Plant } from 'src/app/models/plant';
import { MasterService } from 'src/app/shared/master.service';
import { WmsIdmasterService } from '../../wms-idmaster-service.service';
import { SetupService } from '../setup.service';
import { PlantService } from './plant.service';

export class PlantCls implements Plant {
  constructor(public address1?: string, public address2?: string, public city?: string, public companyId?: string,
    public contactName?: string, public country?: string, public createdBy?: string, public createdOn?: Date,
    public deletionIndicator?: number, public designation?: string, public email?: string,
    public languageId?: string, public plantId?: string, public phoneNumber?: string,
    public state?: string, public updatedBy?: string, public updatedOn?: Date,
    public zipCode?: number) {
  }
}

interface SelectItem {
  id: string;
  itemName: string;
}



@Component({
  selector: 'app-plant',
  templateUrl: './plant.component.html',
  styleUrls: ['./plant.component.scss']
})
export class PlantComponent implements OnInit, OnDestroy {
  checked = false;
  sub = new Subscription();
  companyList: any[] = [];
  plant = new PlantCls();
  plantIdForEdit?: any;
  plantName = '';
  commuicationwith = 'Address';
  storageDetails: any;
  // sform = this.fb.group(
  //   {
  //     companyCode: [, [Validators.required]],
  //     plant: [, [Validators.required]],
  //     plantName: [, [Validators.required]],
  //     commuicationwith: ['Address'],


  //     address1: [, [Validators.required]],
  //     address2: [],
  //     city: [, [Validators.required]],
  //     state: [, [Validators.required]],
  //     country: [, [Validators.required]],
  //     zipCode: [, [Validators.required]],

  //     contranctName: [, [Validators.required]],
  //     designation: [],
  //     phoneNumber: [, [Validators.required, Validators.minLength(12),]],
  //     emailId: [, [Validators.required, Validators.email]],


  //     userid: [0]

  //   });



  cityList: any[] = [];
  //currencyList: any[] = [];
  stateList: any[] = [];
  countryList: any[] = [];
  plantList: any[] = [];
  //vertical: any[] = [];

  public errorHandling = (control: string, error: string = "required") => {
    //   return this.sform.controls[control].hasError(error) && this.submitted;
  }
  // getErrorMessage() {
  //   // if (this.email.hasError('required')) {
  //   //   return ' Field should not be blank';
  //   // }
  //   return this.sform.hasError('required') ? ' Field should not be blank' : '';

  // }

  constructor(private router: Router,
    private fb: FormBuilder,
    public toastr: ToastrService,
    public service: WmsIdmasterService,
    private cs: CommonService,
    private spin: NgxSpinnerService,
    private setupService: SetupService,
    private auth: AuthService,
    private route: ActivatedRoute,
    private plantService: PlantService,
    private masterService: MasterService
  ) {
    this.plant.companyId = this.route.snapshot.params['companyId'];
    //this.plant.plantId = this.route.snapshot.params['plantId'];

    if (sessionStorage.getItem('storageSection') != null && sessionStorage.getItem('storageSection') != undefined) {
      this.storageDetails = JSON.parse(sessionStorage.getItem('storageSection') as '{}');
      this.plant.plantId = this.storageDetails.plantId;
      this.plant.companyId = this.storageDetails.companyId;
      this.plantIdForEdit = this.plant.plantId;
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
  section: any = [1];
  currentItemsToShow = [];
  tabcheck(list: any, id: any) {
    return this.section.includes(id) && list == id;
  }
  updatecurrentItemsToShow($event: any) {
    this.currentItemsToShow = $event;
  }

  //validation
  submitted = false;
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
  selectedItems4: SelectItem[] = [];
  multiselectcompanyList: SelectItem[] = [];
  multiselectcountryList: SelectItem[] = [];
  multiselectcityList: SelectItem[] = [];
  multiselectplantList: SelectItem[] = [];
  multicompanyList: SelectItem[] = [];
  multiselectstateList: SelectItem[] = [];
  multistateList: SelectItem[] = [];
  multicountryList: SelectItem[] = [];
  multicityList: SelectItem[] = [];
  multiplantList: SelectItem[] = [];

  dropdownSettings = {
    singleSelection: true,
    idField: 'id',
    textField: 'itemName',
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
      //currency: this.service.getallcurrency().pipe(catchError(err => of(err))),
      state: this.masterService.getStateMasterDetails().pipe(catchError(err => of(err))),
      country: this.masterService.getCountryMasterDetails().pipe(catchError(err => of(err))),
      plant: this.masterService.getPlantMasterDetails().pipe(catchError(err => of(err)))
      //vertical: this.service.getallvetical().pipe(catchError(err => of(err))),

    })
      .subscribe(({ company, city, state, country, plant }) => { //currency
        
        this.companyList = company;
        console.log(this.companyList);
        this.companyList.forEach(x => this.multicompanyList.push({ id: x.companyCodeId, itemName: x.description }))
        this.multiselectcompanyList = this.multicompanyList;

        this.cityList = city;
        this.cityList.forEach(x => this.multicityList.push({ id: x.cityName, itemName: x.cityName }))
        this.multiselectcityList = this.multicityList;
        //  this.currencyList = currency;

        this.stateList = state;
        this.stateList.forEach(x => this.multistateList.push({ id: x.stateName, itemName: x.stateName }))
        this.multiselectstateList = this.multistateList;

        this.countryList = country;
        this.countryList.forEach(x => this.multicountryList.push({ id: x.countryName, itemName: x.countryName }))
        this.multiselectcountryList = this.multicountryList;


        this.plantList = plant;
        this.plantList.forEach(x => this.multiplantList.push({ id: x.plantId, itemName: x.plantId + "-" + x.description }))
        this.multiselectplantList = this.multiplantList;

        //this.vertical = vertical;
        this.changePlantName();

        if (this.plant.plantId !== undefined && this.plant.plantId !== null) {
          this.getPlantDetails();
        }
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

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

  changePlantName() {
    let singlePlant = this.plantList.filter(x => x.plantId == this.plant.plantId)

    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.plantName = this.selectedItems1[0].itemName;
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

    // this.sform.patchValue({ createdBy: this.auth.userID });
    // this.sub.add(this.setupService.patchcompany(this.sform.controls.companyId.value, this.sform.getRawValue()).subscribe(res => {
    //   this.toastr.success("saved");
    //   this.spin.hide();

    // }, err => {
    //   this.toastr.error(err, "");
    //   this.spin.hide();

    // }));
  };

  getPlantDetails() {
    console.log("Hit Plant");
    this.plantService.getPlantDetails(this.plant.plantId ? this.plant.plantId : '').subscribe(
      result => {
        this.plant = result;

        if (result.plantId != null) {
          let filteredPlant = this.multiselectplantList.filter(x => x.id == result.plantId);
          let selectItem: SelectItem[] = [];
          if (filteredPlant != null && filteredPlant != undefined && filteredPlant.length > 0) {
            selectItem.push({ id: filteredPlant[0].id, itemName: filteredPlant[0].itemName });
            this.selectedItems1 = selectItem;
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

  //save 
  onSubmit() {
    // let element: HTMLElement = document.getElementById('btnsave') as HTMLElement;
    // element.click();

    if ((this.plant.companyId != null && this.plant.companyId != undefined) &&
      (this.selectedItems1 != null && this.selectedItems1 != undefined)) {
      if (!this.plantIdForEdit) {
        this.savePlantDetails();
      }
      else {
        this.updatePlantDetails();
      }
    }
    else {
      this.toastr.error("Please fill the required fields", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
  }

  savePlantDetails() {
    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.plant.plantId = this.selectedItems1[0].id;
    }
    
    if (this.selectedItems4 != null && this.selectedItems4 != undefined && this.selectedItems4.length > 0) {
      this.plant.city = this.selectedItems4[0].itemName;
    }

    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
      this.plant.state = this.selectedItems2[0].itemName;
    }

    if (this.selectedItems3 != null && this.selectedItems3 != undefined && this.selectedItems3.length > 0) {
      this.plant.country = this.selectedItems3[0].itemName;
    }
    this.plant.languageId = 'en';
    this.plant.zipCode = Number(this.plant.zipCode);
    this.plantService.savePlantDetails(this.plant).subscribe(
      result => {
        console.log(result);
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);
        this.toastr.success("Plant details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/warehouse', this.plant.companyId, this.plant.plantId]);
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

  updatePlantDetails() {
    if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
      this.plant.plantId = this.selectedItems1[0].id;
    }

    if (this.selectedItems4 != null && this.selectedItems4 != undefined && this.selectedItems4.length > 0) {
      this.plant.city = this.selectedItems4[0].itemName;
    }

    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0) {
      this.plant.state = this.selectedItems2[0].itemName;
    }

    if (this.selectedItems3 != null && this.selectedItems3 != undefined && this.selectedItems3.length > 0) {
      this.plant.country = this.selectedItems3[0].itemName;
    }
    this.plant.languageId = 'en';
    this.plant.zipCode = Number(this.plant.zipCode);
    this.plantService.updatePlantDetails(this.plant).subscribe(
      result => {
        console.log(result);
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);

        this.toastr.success("Plant details updated successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/warehouse', this.plant.companyId, this.plant.plantId]);
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
    console.log(item);
    //this.plant.plantId = this.selectedItems1[0].id;
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
