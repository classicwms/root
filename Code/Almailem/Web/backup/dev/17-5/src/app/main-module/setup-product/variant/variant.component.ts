import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { MasterService } from "src/app/shared/master.service";
import { WarehouseService } from "../../setup/warehouse/warehouse.service";
import { SetupProductService } from "../setup-product.service";

export interface variant {


  no: string;
  values: string;
}
const ELEMENT_DATA: variant[] = [
  { no: "1", values: 'Value' }



];
@Component({
  selector: 'app-variant',
  templateUrl: './variant.component.html',
  styleUrls: ['./variant.component.scss']
})
export class VariantComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  variantId: any;

  warehouseList: any[] = [];

  variantList: any[] = [];
  variantList1: any[] = [];
  levelList: any[] = [];
  levelList1: any[] = [];
  variantTypeList: any[] = [];

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
    private route: ActivatedRoute, public toastr: ToastrService, private warehouseService: WarehouseService, private masterService: MasterService) {
    this.variantId = this.route.snapshot.params['variantId'];

    if (this.variantId !== undefined && this.variantId !== null) {
      this.getVariantDetails();
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
        levelId: [, [Validators.required]],
        levelReferece: [, [Validators.required]],
        languageId: ['en'],
        plantId: [],
        variantId: [, [Validators.required]],
        variantSubId: [, [Validators.required]],
        warehouseID: [, [Validators.required]]

      });
    this.dropdownfill();
  }

  displayedColumns: string[] = ['select', 'values'];
  dataSource = new MatTableDataSource<variant>(ELEMENT_DATA);
  selection = new SelectionModel<variant>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: variant): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      variant: this.masterService.getVariantMasterDetails().pipe(catchError(err => of(err))),
      level: this.masterService.getLevelMasterDetails().pipe(catchError(err => of(err)))

    })
      .subscribe(({ warehouse, variant, level }) => {

        this.warehouseList = warehouse;
        this.levelList1 = level;
        this.levelList1.forEach((x: { levelId: string; level: string; }) => this.levelList.push({key: x.levelId, value:  x.levelId + '-' + x.level}))
        this.levelList = this.cs.removeDuplicatesFromArraydropdown(this.levelList);
        console.log(this.levelList);
        this.variantList1 = variant;
        this.variantList1.forEach((x: { variantCode: string; variantText: string; }) => this.variantList.push({key: x.variantCode, value:  x.variantCode + '-' + x.variantText}))
        this.variantList = this.cs.removeDuplicatesFromArraydropdown(this.variantList);

        console.log(this.variantList);
        this.variantTypeList = [{ 'variantTypeId': 'Attribute', 'description': 'Attribute' }];
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

  }

  getVariantDetails() {
    this.setupProductService.getProductSetupDetails('variant', this.variantId ? this.variantId : '').subscribe(
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
      this.toastr.error("Storage Level values are mandatory", "Error", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    // this.createVariant();
    // this.loading = true;
    if (!this.variantId) {
      this.createVariant(formDirective);
    } else {
      this.updateVariant(formDirective);
    }
  }

  private createVariant(formDirective: FormGroupDirective) {
    console.log(this.form.value);
    this.setupProductService.saveProductSetupDetails('variant', this.form.value)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Variant details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        // this.alertService.success('User added', { keepAfterRouteChange: true });
        // this.router.navigate(['../'], { relativeTo: this.route });
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

  private updateVariant(formDirective: FormGroupDirective) {
    this.setupProductService.updateProductSetupDetails('variant', this.form.value)
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
