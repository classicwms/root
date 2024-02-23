import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { WarehouseService } from 'src/app/main-module/setup/warehouse/warehouse.service';
import { MasterService } from 'src/app/shared/master.service';
import { SetupStorageService } from '../../setup-storage.service';

@Component({
  selector: 'app-strategy-popup',
  templateUrl: './strategy-popup.component.html',
  styleUrls: ['./strategy-popup.component.scss']
})
export class StrategyPopupComponent implements OnInit {
  strategyTypeList: any[] = [];
  warehouseList: any[] = [];
  strategyId: any;
  form!: FormGroup;
  constructor(private masterService: MasterService, private setupStorageService: SetupStorageService,
    private route: ActivatedRoute, private fb: FormBuilder, public toastr: ToastrService, private warehouseService: WarehouseService,
    @Inject(MAT_DIALOG_DATA) public data: any, private dialogRef: MatDialogRef<StrategyPopupComponent>
  ) { 
    console.log(data);
    this.strategyId = data;

    if (this.strategyId !== undefined && this.strategyId !== null) {
      this.getStrategyDetails();
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
        deletionIndicator: [],
        languageId: ['en'],
        plantId: [],
        priority: [, Validators.required],
        sequenceIndicator: [, Validators.required],
        strategyNo: [, Validators.required],
        strategyTypeId: [, Validators.required],     
        warehouseId: [, Validators.required]
      });
      
    //this.getStrategyTypeMasterDetails();
    this.getWarehouseList();
  }

  getStrategyTypeMasterDetails(){
    this.masterService.getStrategyTypeMasterDetails().subscribe(
      result => {
        console.log(result);
        this.strategyTypeList = result;
      },
      error => {
        console.log(error);
      }
    );
  }

  getWarehouseList()
  {
    this.warehouseService.getWarehouseList().subscribe(
      result => {
        console.log(result);
        this.warehouseList = result;
      },
      error => {
        console.log(error);
      }
    );
  }

  getStrategyDetails() {
    this.setupStorageService.getStorageSetupDetails('strategy', this.strategyId ? this.strategyId : '').subscribe(
      result => {
        console.log(result);
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
    if (!this.strategyId) {
      this.createStrategy(formDirective);
    } else {
      this.updateStrategy(formDirective);
    }
  }

  private createStrategy(formDirective: FormGroupDirective) {
    console.log(this.form.value);
    this.setupStorageService.saveStorageSetupDetails('strategy', this.form.value)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Strategy Details saved successfully","",{
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
          //this.isLoadingResults = false;
        });
    // .add(() => this.loading = false);
  }

  private updateStrategy(formDirective: FormGroupDirective) {
    this.setupStorageService.updateStorageSetupDetails('strategy', this.form.value)
      .subscribe(() => {
        this.toastr.success("Strategy Details updated successfully","",{
        timeOut: 2000,
        progressBar: false,
      });
        this.dialogRef.close();
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
