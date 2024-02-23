import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MasterService } from 'src/app/shared/master.service';
import { WarehouseService } from '../../setup/warehouse/warehouse.service';
import { SetupStorageService } from '../setup-storage.service';

@Component({
  selector: 'app-storage-class',
  templateUrl: './storage-class.component.html',
  styleUrls: ['./storage-class.component.scss']
})
export class StorageClassComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  variantId: any;
  storageBinType: any;

  warehouseList: any[] = [];

  storageClassList: any[] = [];
  showingStorageClassList: any[] = [];
  storageClassDescription = '';

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

  storageClassId: any;
  constructor(private fb: FormBuilder, private setupStorageService: SetupStorageService,
    private masterService: MasterService, private warehouseService: WarehouseService,
    private route: ActivatedRoute, public toastr: ToastrService, private router: Router) {
    this.storageClassId = this.route.snapshot.params['storageClassId'];

    if (sessionStorage.getItem('storageBinType') != null && sessionStorage.getItem('storageBinType') != undefined) {
      this.storageBinType = JSON.parse(sessionStorage.getItem('storageBinType') as '{}');
      this.storageClassId = this.storageBinType.storageClassId;
      console.log(this.storageClassId);
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
        hazardMaterialClass: [,],
        languageId: ["en"],
        plantId: [],
        remarks: [],
        storageClassId: [, [Validators.required]],
        waterPollutionClass: [, ],
        warehouseId: [, [Validators.required]],
        description:[]

      });

      this.dropdownfill();

      this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
        this.showingStorageClassList = this.storageClassList.filter(element => {
          return element.warehouseId === this.form.controls['warehouseId'].value;
        });
      })

      this.form.controls["storageClassId"].valueChanges.subscribe(selectedValue => {
        let filteredStorageClassList = this.showingStorageClassList.filter(element => {
          return element.storageClassId === this.form.controls['storageClassId'].value;
        });

        this.form.patchValue({description: filteredStorageClassList[0].description});
      })


  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      storageClass: this.masterService.getStorageClassMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ warehouse, storageClass }) => {

        this.warehouseList = warehouse;
        this.storageClassList = storageClass;
        console.log(this.storageClassList);

        if (this.storageClassId !== undefined && this.storageClassId !== null) {
          this.getStorageClassDetails();
        }
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

  }

  getStorageClassDetails() {
    this.setupStorageService.getStorageSetupDetails('storageClass', this.storageClassId ? this.storageClassId : '').subscribe(
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

    // stop here if form is invalid
    if (this.form.invalid) {
      return;
    }

    // this.loading = true;
    if (!this.storageClassId) {
      this.createStorageClass(formDirective);
    } else {
      this.updateStorageClass(formDirective);
    }
  }

  private createStorageClass(formDirective: FormGroupDirective) {
    this.setupStorageService.saveStorageSetupDetails('storageClass', this.form.value)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Storage Class details Saved Successfully","Notification",{
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

  private updateStorageClass(formDirective: FormGroupDirective) {
    this.setupStorageService.updateStorageSetupDetails('storageClass', this.form.value)
      .subscribe(() => {
        this.toastr.success("Storage Class Details updated successfully","Notification",{
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
