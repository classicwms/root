import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute } from "@angular/router";
import { SetupProductService } from "../setup-product.service";

export interface variant {


  no: string;
  parameter: string;
  from: string;
  to: string;
}
const ELEMENT_DATA: variant[] = [
  { no: "1", parameter: 'Value', from: 'date', to: 'date' },
  { no: "2", parameter: 'Value', from: 'date', to: 'date' },
  { no: "3", parameter: 'Value', from: 'date', to: 'date' },
  { no: "4", parameter: 'Value', from: 'date', to: 'date' },
  { no: "5", parameter: 'Value', from: 'date', to: 'date' },
  { no: "6", parameter: 'Value', from: 'date', to: 'date' },
  { no: "7", parameter: 'Value', from: 'date', to: 'date' },
  { no: "8", parameter: 'Value', from: 'date', to: 'date' },

];


@Component({
  selector: 'app-barcode',
  templateUrl: './barcode.component.html',
  styleUrls: ['./barcode.component.scss']
})
export class BarcodeComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  form!: FormGroup;
  barCodeId: any;
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
  constructor(private fb: FormBuilder, private setupProductService: SetupProductService,
    private route: ActivatedRoute) {
    this.barCodeId = this.route.snapshot.params['barCodeId'];

    if (this.barCodeId !== undefined && this.barCodeId !== null) {
      this.getBarCodeDetails();
    }
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
        barcodeLength: [0, [Validators.required]],
        barcodeTypeId: [, [Validators.required]],
        barcodeSubTypeId: [, [Validators.required]],
        languageId: [2, [Validators.required]],
        plantId: [200, [Validators.required]],
        barcodeWidth: [, [Validators.required]],
        labelInformation: [, [Validators.required]],
        levelId: [, [Validators.required]],
        levelReference: [, [Validators.required]],
        method: [, [Validators.required]],
        numberRangeFrom: [, [Validators.required]],
        numberRangeTo: [, [Validators.required]],
        processId: [, [Validators.required]],
        warehouseId: [, [Validators.required]]

      });
  }

  displayedColumns: string[] = ['select', 'parameter', 'from', 'to'];
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

  getBarCodeDetails() {
    this.setupProductService.getProductSetupDetails('barCode', this.barCodeId ? this.barCodeId : '').subscribe(
      result => {
        this.form.patchValue(result);
      },
      error => {
        console.log(error);
      }
    );
  }

  onSubmit() {

    // this.submitted = true;

    // // reset alerts on submit
    // this.alertService.clear();

    // stop here if form is invalid
    if (this.form.invalid) {
      return;
    }

    this.createBarCode();
    // this.loading = true;
    if (!this.barCodeId) {
      this.createBarCode();
    } else {
      this.updateBarCode();
    }
  }

  private createBarCode() {
    this.setupProductService.saveProductSetupDetails('barCode', this.form.value)
      .subscribe(result => {
        console.log(result);
        // this.alertService.success('User added', { keepAfterRouteChange: true });
        // this.router.navigate(['../'], { relativeTo: this.route });
      },
        error => {
          console.log(error);
          //this.isLoadingResults = false;
        });
    // .add(() => this.loading = false);
  }

  private updateBarCode() {
    this.setupProductService.updateProductSetupDetails('barCode', this.form.value)
      .subscribe(() => {
        // this.alertService.success('User updated', { keepAfterRouteChange: true });
        // this.router.navigate(['../../'], { relativeTo: this.route });
      },
        error => {
          console.log(error);
          //this.isLoadingResults = false;
        });
    // .add(() => this.loading = false);
  }
}
