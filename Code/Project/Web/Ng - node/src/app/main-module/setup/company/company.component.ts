import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
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

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.scss'],
})
export class CompanyComponent implements OnInit, OnDestroy {
  sub = new Subscription();


  sform = this.fb.group(
    {
      companyId: [1, [Validators.required]],
      companyName: [, [Validators.required]],
      verticalId: [, [Validators.required]],
      currency: [, [Validators.required]],


      address1: [, [Validators.required]],
      address2: [],
      city: [, [Validators.required]],
      state: [, [Validators.required]],
      country: [, [Validators.required]],
      zipCode: [, [Validators.required]],

      contactName: [, [Validators.required]],
      designation: [],
      phoneNumber: [, [Validators.required]],
      email: [, [Validators.required, Validators.email]],


      noOfOutlets: [0],
      noOfPlants: [0],
      noOfWarehouses: [0],
      createdBy: []

    });
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
  ) { }

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
    return this.sform.controls[control].hasError(error) && this.submitted;
  }

  edit() {
    this.sform.enable();
    // this.sform.controls.companyid.disable();

  }

  initForm() {

    this.sform.disable();
    this.sub.add(this.sform.controls.companyId.valueChanges.subscribe(res => {
      var description: any = this.companyList.filter(x => x.companyId === res)[0];
      if (description)
        this.sform.patchValue({ companyName: description.companyDescription });
      this.sub.add(this.setupService.getcompany(res).subscribe(res => { console.log(res); }, err => { }));
    }));
    this.sub.add(this.sform.controls.city.valueChanges.subscribe(res => {
      var filter: any = this.cityList.filter(x => x.cityName === res)[0];
      if (filter)
        this.sform.patchValue({ zipCode: filter.zipCode });
    }));
  }

  dropdownfill() {
    this.spin.show();
    forkJoin({
      company: this.service.getallcompany().pipe(catchError(err => of(err))),
      city: this.service.getallcity().pipe(catchError(err => of(err))),
      //currency: this.service.getallcurrency().pipe(catchError(err => of(err))),
      state: this.service.getallstate().pipe(catchError(err => of(err))),
      country: this.service.getallcountry().pipe(catchError(err => of(err))),
      vertical: this.service.getallvetical().pipe(catchError(err => of(err))),

    })
      .subscribe(({ company, city, state, country, vertical }) => { //currency

        this.companyList = company;
        this.cityList = city;
        //  this.currencyList = currency;
        this.stateList = state;
        this.countryList = country;
        // this.vertical = vertical;
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


  submit() {
    this.submitted = true;
    if (this.sform.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        ""
      );

      this.cs.notifyOther(true);
      return;
    }

    this.cs.notifyOther(false);
    this.spin.show();

    this.sform.patchValue({ createdBy: this.auth.username });
    this.sub.add(this.setupService.patchcompany(this.sform.controls.companyId.value, this.sform.getRawValue()).subscribe(res => {
      this.toastr.success("saved");
      this.spin.hide();

    }, err => {
      this.toastr.error(err, "");
      this.spin.hide();

    }));
  };

  //save 
  save() {
    let element: HTMLElement = document.getElementById('btnsave') as HTMLElement;
    element.click();
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



}
