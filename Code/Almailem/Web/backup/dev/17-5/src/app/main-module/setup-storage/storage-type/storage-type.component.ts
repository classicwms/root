import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { MasterService } from 'src/app/shared/master.service';
import { WarehouseService } from '../../setup/warehouse/warehouse.service';
import { SetupStorageService } from '../setup-storage.service';

@Component({
  selector: 'app-storage-type',
  templateUrl: './storage-type.component.html',
  styleUrls: ['./storage-type.component.scss']
})
export class StorageTypeComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  variantId: any;
  warehouseList: any[] = [];
  storageBinType: any;

  storageClassList: any[] = [];
  showingStorageClassList: any[] = [];
  storageTypeList: any[] = [];
  storageTypeList1: any[] = [];

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

  storageTypeId: any;
  constructor(private fb: FormBuilder, private setupStorageService: SetupStorageService,  private cs: CommonService,
    private route: ActivatedRoute, private masterService: MasterService, private warehouseService: WarehouseService,
    public toastr: ToastrService, private router: Router) {
    if (sessionStorage.getItem('storageBinType') != null && sessionStorage.getItem('storageBinType') != undefined) {
      this.storageBinType = JSON.parse(sessionStorage.getItem('storageBinType') as '{}');
      console.log(this.storageBinType);
      this.storageTypeId = this.storageBinType.storageBinTypeId;
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
        languageId: ['en'],
        plantId: ['1001'],
        storageClassId: [, [Validators.required]],
        warehouseId: [, [Validators.required]],
        addToExistingStock: [],
        allowNegativeStock: [],
        capacityCheck: [],
        mixToStock: ['0', [Validators.required]],
        returnToSameStorageType: [],
        storageTemperatureFrom: [, ],
        storageTemperatureTo: [, ],
        storageTypeId: [, [Validators.required]],
        storageUom: [],
        description: []

      });



    this.dropdownfill();

    this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
      this.showingStorageClassList = this.storageClassList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
    })

    this.form.controls["storageTypeId"].valueChanges.subscribe(selectedValue => {
      let filteredStorageTypeList = this.storageTypeList.filter(element => {
        return element.storageTypeId === this.form.controls['storageTypeId'].value;
      });

      this.form.patchValue({ description: filteredStorageTypeList[0].description });
    })
  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      storageClass: this.masterService.getStorageClassMasterDetails().pipe(catchError(err => of(err))),
      // storageClass: this.setupStorageService.getStorageSetupList('storageClass').pipe(catchError(err => of(err))),
      storageType: this.masterService.getStorageTypeMasterDetails().pipe(catchError(err => of(err))),
    })
      .subscribe(({ warehouse, storageClass, storageType }) => {

        this.warehouseList = warehouse;
        this.storageClassList = storageClass;
        console.log(this.storageClassList);
        this.storageTypeList1 = storageType;
        this.storageTypeList1.forEach((x: { storageTypeId: string; description: string; }) => this.storageTypeList.push({key: x.storageTypeId, value:  x.storageTypeId + '-' + x.description}))
        this.storageTypeList = this.cs.removeDuplicatesFromArraydropdown(this.storageTypeList);

        console.log(this.storageTypeList);

        if (this.storageTypeId !== undefined && this.storageTypeId !== null) {
          this.getStorageTypeDetails();
        }
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

  }

  getStorageTypeDetails() {
    this.setupStorageService.getStorageSetupDetails('storageType', this.storageTypeId ? this.storageTypeId : '').subscribe(
      result => {
        console.log(result);
        this.form.patchValue(result);
        this.form.patchValue({ mixToStock: String(result.mixToStock) });
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

    // stop here if form is invalid
    console.log(this.form.value);
    if (this.form.invalid) {
      return;
    }

    // this.loading = true;
    if (!this.storageTypeId) {
      this.createStorageType(formDirective);
    } else {
      this.updateStorageType(formDirective);
    }
  }

  private createStorageType(formDirective: FormGroupDirective) {
    this.form.patchValue({ returnToSameStorageType: 1 });
    this.setupStorageService.saveStorageSetupDetails('storageType', this.form.value)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Storage Type Details Saved Successfully", "", {
          timeOut: 2000,
          progressBar: false,
        });
        formDirective.resetForm();
        this.form.reset();
        // this.alertService.success('User added', { keepAfterRouteChange: true });
        // this.router.navigate(['../'], { relativeTo: this.route });
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

  private updateStorageType(formDirective: FormGroupDirective) {
    this.setupStorageService.updateStorageSetupDetails('storageType', this.form.value)
      .subscribe(() => {
        this.toastr.success("Storage Type Details Updated Successfully", "", {
          timeOut: 2000,
          progressBar: false,
        });
        formDirective.resetForm();
        this.form.reset();
        // this.alertService.success('User updated', { keepAfterRouteChange: true });
        // this.router.navigate(['../../'], { relativeTo: this.route });
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

}
