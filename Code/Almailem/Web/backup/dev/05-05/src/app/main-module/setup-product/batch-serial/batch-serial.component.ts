import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { MasterService } from "src/app/shared/master.service";
import { WarehouseService } from "../../setup/warehouse/warehouse.service";
import { SetupProductService } from "../setup-product.service";

export interface batch {


  no: string;
  values: string;
}
const ELEMENT_DATA: batch[] = [
  { no: "1", values: 'Value' }
  // { no: "2", values: 'Value' },
  // { no: "3", values: 'Value' },
  // { no: "4", values: 'Value' },
  // { no: "5", values: 'Value' },
  // { no: "6", values: 'Value' },
  // { no: "7", values: 'Value' },
  // { no: "8", values: 'Value' },



];

@Component({
  selector: 'app-batch-serial',
  templateUrl: './batch-serial.component.html',
  styleUrls: ['./batch-serial.component.scss']
})
export class BatchSerialComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  batchSerialId: any;
  warehouseList: any[] = [];

  levelList: any[] = [];
  levelList1: any[] = [];
  storageMethodList: any[] = [];
  maintainanceList: any[] = [];

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
    private route: ActivatedRoute, private warehouseService: WarehouseService, private masterService: MasterService, 
    public toastr: ToastrService, private router: Router) {
    this.batchSerialId = this.route.snapshot.params['batchSerialId'];

    if (this.batchSerialId !== undefined && this.batchSerialId !== null) {
      this.getBatchSerialDetails();
    }
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        companyId: [1, [Validators.required]],
        createdBy: [],
        createdOn: [],
        updatedBy: [],
        updatedOn: [],
        deletionIndicator: [0],
        maintenance: [, [Validators.required]],
        itemTypeId: [],
        languageId: ['en'],
        levelId: [, Validators.required],
        levelReference: [, Validators.required],
        plantId: [],
        storageMethod: [, [Validators.required]],
        warehouseId: [, [Validators.required]]

      });
      this.dropdownfill();
  }

  displayedColumns: string[] = ['select',  'values'];
  dataSource = new MatTableDataSource<batch>(ELEMENT_DATA);
  selection = new SelectionModel<batch>(true, []);

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
  checkboxLabel(row?: batch): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }

  getBatchSerialDetails() {
    this.setupProductService.getProductSetupDetails('batchSerial', this.batchSerialId ? this.batchSerialId : '').subscribe(
      result => {
        this.form.patchValue(result);
      },
      error => {
        console.log(error);
      }
    );
  }

  dropdownfill() {
    //this.spin.show();
    forkJoin({
      warehouse: this.warehouseService.getWarehouseList().pipe(catchError(err => of(err))),
      level: this.masterService.getLevelMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ warehouse, level }) => {

        this.warehouseList = warehouse;
        this.levelList1 = level;
        this.levelList1.forEach((x: { levelId: string; level: string; }) => this.levelList.push({key: x.levelId, value:  x.levelId + '-' + x.level}))
        this.levelList = this.cs.removeDuplicatesFromArraydropdown(this.levelList);
        
        console.log(this.levelList);
        this.storageMethodList = [{'storageMethodId': 'Batch', 'description': 'Batch'},{'storageMethodId': 'Serial', 'description': 'Serial'},{'storageMethodId': 'Not Applicable', 'description': 'Not Applicable'}];
        this.maintainanceList = [{'maintainanceId': 'Internal', 'description': 'Internal'},{'maintainanceId': 'External', 'description': 'External'}]
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    //this.spin.hide();

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

    //this.createBatchSerial();
    // this.loading = true;
    if (!this.batchSerialId) {
      this.createBatchSerial(formDirective);
    } else {
      this.updateBatchSerial(formDirective);
    }
  }

  private createBatchSerial(formDirective: FormGroupDirective) {
    console.log(this.form.value);
    this.setupProductService.saveProductSetupDetails('batchSerial', this.form.value)
      .subscribe(result => {
        this.toastr.success("Batch Serial details Saved Successfully","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        //this.router.navigate(['/main/product/variant', this.form.controls['warehouseId'].value]);
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

  private updateBatchSerial(formDirective: FormGroupDirective) {
    this.setupProductService.updateProductSetupDetails('batchSerial', this.form.value)
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
