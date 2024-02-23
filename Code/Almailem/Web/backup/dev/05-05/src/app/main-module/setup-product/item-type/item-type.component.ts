import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { MasterService } from 'src/app/shared/master.service';
import { WarehouseService } from '../../setup/warehouse/warehouse.service';
import { SetupProductService } from '../setup-product.service';

@Component({
  selector: 'app-item-type',
  templateUrl: './item-type.component.html',
  styleUrls: ['./item-type.component.scss']
})
export class ItemTypeComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  itemTypeId: any;
  itemGroup: any;

  warehouseList: any[] = [];

  itemTypeList: any[] = [];
  itemTypeList1: any[] = [];
  //itemTypeDescription = '';

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


  constructor(private fb: FormBuilder, private setupProductService: SetupProductService,  private cs: CommonService,
    private route: ActivatedRoute, private masterService: MasterService, private warehouseService: WarehouseService,
    public toastr: ToastrService, private router: Router) {
    this.itemTypeId = this.route.snapshot.params['itemTypeId'];

    if (sessionStorage.getItem('itemGroup') != null && sessionStorage.getItem('itemGroup') != undefined) {
      this.itemGroup = JSON.parse(sessionStorage.getItem('itemGroup') as '{}');
      this.itemTypeId = this.itemGroup.itemTypeId;
    }
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
        deletionIndicator: [],
        description: [],
        itemTypeId: [, [Validators.required]],
        languageId: [],
        plantId: [],
        storageMethod: [],
        variantManagementIndicator: [],
        warehouseId: [, [Validators.required]]

      });

    this.form.controls["itemTypeId"].valueChanges.subscribe(item => {
      console.log(item);
      let itemType = this.itemTypeList.filter(x => { return x.itemTypeId == item });
      console.log(itemType);
      //this.itemTypeDescription = itemType[0].itemType;
      this.form.patchValue({ description: itemType[0].itemType });
    })
  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      itemtype: this.masterService.getItemTypeMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ warehouse, itemtype }) => {

        this.warehouseList = warehouse;
        this.itemTypeList1 = itemtype;
        this.itemTypeList1.forEach((x: { itemTypeId: string; itemType: string; }) => this.itemTypeList.push({key: x.itemTypeId, value:  x.itemTypeId + '-' + x.itemType}))
        this.itemTypeList = this.cs.removeDuplicatesFromArraydropdown(this.itemTypeList);
        console.log(this.itemTypeList);

        if (this.itemTypeId !== undefined && this.itemTypeId !== null) {
          this.getItemTypeDetails();
        }
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

  }

  getItemTypeDetails() {
    this.setupProductService.getProductSetupDetails('itemType', this.itemTypeId ? this.itemTypeId : '').subscribe(
      result => {
        console.log(result);
        this.form.patchValue(result);
        this.form.patchValue({ variantManagementIndicator: result.variantManagementIndicator == 0 ? false : true });

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

    // this.createItemType();
    // this.loading = true;
    if (!this.itemTypeId) {
      this.createItemType(formDirective);
    } else {
      this.updateItemType(formDirective);
    }
  }

  private createItemType(formDirective: FormGroupDirective) {
    // variantManagementIndicator: this.form.controls['variantManagementIndicator'].value  ? 1 : 0,
    this.form.patchValue({ variantManagementIndicator: this.form.controls['variantManagementIndicator'].value ? 1 : 0, languageId: "en" });
    this.setupProductService.saveProductSetupDetails('itemType', this.form.value)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Item Type details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        //this.router.navigate(['/main/product/itemgroup', this.form.controls['warehouseId'].value, this.form.controls['itemTypeId'].value]);

        formDirective.resetForm();
        this.form.reset();
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

  private updateItemType(formDirective: FormGroupDirective) {
    console.log(this.form.value);
    this.form.patchValue({ variantManagementIndicator: this.form.controls['variantManagementIndicator'].value ? 1 : 0, languageId: "en" });
    this.setupProductService.updateProductSetupDetails('itemType', this.form.value)
      .subscribe(() => {
        // this.alertService.success('User updated', { keepAfterRouteChange: true });
        // this.router.navigate(['../../'], { relativeTo: this.route });

        formDirective.resetForm();
        this.form.reset();
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
