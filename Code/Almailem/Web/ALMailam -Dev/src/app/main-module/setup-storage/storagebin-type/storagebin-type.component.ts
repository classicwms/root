import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of } from "rxjs";
import { catchError, filter } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { MasterService } from "src/app/shared/master.service";
import { WarehouseService } from "../../setup/warehouse/warehouse.service";
import { SetupStorageService } from "../setup-storage.service";
import { StoragebinTableComponent } from "./storagebin-table/storagebin-table.component";


@Component({
  selector: 'app-storagebin-type',
  templateUrl: './storagebin-type.component.html',
  styleUrls: ['./storagebin-type.component.scss']
})
export class StoragebinTypeComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  variantId: any;
  warehouseList: any[] = [];
  storageBinType: any;
  storageBinTypeId: any;

  storageClassList: any[] = [];
  showingStorageClassList: any[] = [];
  storageTypeList: any[] = [];
  storageTypeList1: any[] = [];
  storageBinTypeList: any[] = [];
  storageBinTypeList1: any[] = [];

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

  animal: string | undefined;
  name: string | undefined;
  form!: FormGroup;
  constructor(public dialog: MatDialog, private fb: FormBuilder, private setupStorageService: SetupStorageService, private cs: CommonService,
    private route: ActivatedRoute, private masterService: MasterService, private warehouseService: WarehouseService,
    public toastr: ToastrService) {
    this.storageBinTypeId = this.route.snapshot.params['storageBinTypeId'];

    if (sessionStorage.getItem('storageBinType') != null && sessionStorage.getItem('storageBinType') != undefined) {
      this.storageBinType = JSON.parse(sessionStorage.getItem('storageBinType') as '{}');
      console.log(this.storageBinType);
      this.storageBinTypeId = this.storageBinType.storageBinTypeId;
    }
  }
  Bintype(): void {

    const dialogRef = this.dialog.open(StoragebinTableComponent, {
      disableClose: true,
      width: '77.5%',
      maxWidth: '80%',
      position: { top: '12.5%', right: '1.5%' },
      data: this.form.value
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      this.form.patchValue({ height: result.height, width: result.width, length: result.length, dimentionUom: result.dimentionUom });
    });
  }

  ngOnInit(): void {
    this.dropdownfill();

    this.form = this.fb.group(
      {
        companyId: [],
        createdBy: [],
        createdOn: [],
        updatedBy: [],
        updatedOn: [],
        deletionIndicator: [0],
        dimentionUom: [],
        height: [],
        languageId: ['en'],
        length: [],
        plantId: ['1001'],
        storageBinTypeBlock: ['0'],
        storageBinTypeId: [, Validators.required],
        storageTypeId: [, [Validators.required]],
        totalVolume: [],
        warehouseId: [, [Validators.required]],
        volumeUom: [],
        width: [],
        description: [],
        storageClassId: []

      });

    this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
      console.log(selectedValue);
      this.showingStorageClassList = this.storageClassList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
    })

    this.form.controls["storageBinTypeId"].valueChanges.subscribe(selectedValue => {
      console.log(selectedValue);
      let filteredStorageBinTypeList = this.storageBinTypeList.filter(element => {
        return element.storageTypeId === this.form.controls['storageBinTypeId'].value;
      });

      if (filteredStorageBinTypeList.length > 0) {
        this.form.patchValue({ description: filteredStorageBinTypeList[0].description });
      }
    })
  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      storageClass: this.masterService.getStorageClassMasterDetails().pipe(catchError(err => of(err))),
      // storageClass: this.setupStorageService.getStorageSetupList('storageClass').pipe(catchError(err => of(err))),
      storageType: this.masterService.getStorageTypeMasterDetails().pipe(catchError(err => of(err))),
      // storageClass: this.setupStorageService.getStorageSetupList('storageType').pipe(catchError(err => of(err))),
      storageBinType: this.masterService.getStorageBinTypeMasterDetails().pipe(catchError(err => of(err)))
    })
      .subscribe(({ warehouse, storageClass, storageType, storageBinType }) => {

        this.warehouseList = warehouse;
        this.storageClassList = storageClass;
        console.log(this.storageClassList);
        this.storageTypeList1 = storageType;
        this.storageTypeList1.forEach((x: { storageTypeId: string; description: string; }) => this.storageTypeList.push({key: x.storageTypeId, value:  x.storageTypeId + '-' + x.description}))
        this.storageTypeList = this.cs.removeDuplicatesFromArraydropdown(this.storageTypeList);
        console.log(this.storageTypeList);
        this.storageBinTypeList1 = storageBinType;
        this.storageBinTypeList1.forEach((x: { storageBinTypeId: string; description: string; }) => this.storageBinTypeList.push({key: x.storageBinTypeId, value:  x.storageBinTypeId + '-' + x.description}))
        this.storageBinTypeList = this.cs.removeDuplicatesFromArraydropdown(this.storageBinTypeList);
        console.log(this.storageBinTypeList);

        if (this.storageBinTypeId !== undefined && this.storageBinTypeId !== null) {
          this.getStorageBinTypeDetails();
        }

      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

  }

  getStorageBinTypeDetails() {
    this.setupStorageService.getStorageSetupDetails('storageBinType', this.storageBinTypeId ? this.storageBinTypeId : '').subscribe(
      result => {
        console.log(result);
        this.form.patchValue(result);
        this.form.patchValue({ storageBinTypeBlock: String(result.storageBinTypeBlock) });
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
    if (!this.storageBinTypeId) {
      this.createStorageBinType(formDirective);
    } else {
      this.updateStorageBinType(formDirective);
    }
  }

  private createStorageBinType(formDirective: FormGroupDirective) {
    console.log(this.form.value);
    this.setupStorageService.saveStorageSetupDetails('storageBinType', this.form.value)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Storage Bin Type Details Saved Successfully", "", {
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

  private updateStorageBinType(formDirective: FormGroupDirective) {
    this.setupStorageService.updateStorageSetupDetails('storageBinType', this.form.value)
      .subscribe(() => {
        this.toastr.success("Storage Bin Type Details Updated Successfully", "", {
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
