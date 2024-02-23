import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PrepetualCountService } from '../../prepetual-count.service';

export interface SelectItem {
  id: number;
  movementTypeId: number;
  name: string;
}

@Component({
  selector: 'app-create-popup',
  templateUrl: './create-popup.component.html',
  styleUrls: ['./create-popup.component.scss']
})
export class CreatePopupComponent implements OnInit {
  screenid: 1072 | undefined;
  form!: FormGroup;
  movementTypeList = [{ id: 1, name: 'Inbound' },
  // { id: 2, name: 'Make and Change' }, 
  { id: 3, name: 'Outbound' }];
  subMovementTypeList = [{ id: 2, movementTypeId: 1, name: 'Putaway' }, { id: 3, movementTypeId: 1, name: 'Reversal' },
  // { id: 3, movementTypeId: 2, name: 'Bin to Bin' }, 
  // { id: 1, movementTypeId: 3, name: 'Picking' }, 
  { id: 1, movementTypeId: 3, name: 'Picking' },
  { id: 5, movementTypeId: 3, name: 'Reversal' }];
  filterSubMovementTypeList: SelectItem[] = [];
  constructor(
    public cs: CommonService, private spin: NgxSpinnerService, private fb: FormBuilder, private auth: AuthService, private prepetualCountService: PrepetualCountService,
    private toastr: ToastrService, private router: Router) { }

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        dateFrom: [],
        dateTo: [],
        dateFromFE: [],
        dateToFE: [],
        movementTypeId: [this.fb.array([])],
        subMovementTypeId: [this.fb.array([])],
        warehouseId: [{ value: this.auth.warehouseId, disabled: true }]
      });

    this.form.controls['subMovementTypeId'].disable();

    this.form.controls["movementTypeId"].valueChanges.subscribe(item => {
      console.log(item);
      this.form.controls['subMovementTypeId'].enable();
      this.filterSubMovementTypeList = [];
      item.forEach(singleItem => {
        console.log(this.subMovementTypeList);
        let filter = this.subMovementTypeList.filter(x => x.movementTypeId == singleItem);
        filter.forEach(singleFilter => {
          this.filterSubMovementTypeList.push(singleFilter);
        });
        filter = [];
      });

      //console.log(filterSubMovementType);
      //this.itemTypeDescription = itemType[0].itemType;
      //this.form.patchValue({ description: itemType[0].itemType });
    })

    //routerLink="/main/cycle-count/Prepetual-confirm"
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }

    this.runPerpetual();
  }

  runPerpetual() {
    this.form.controls.dateFrom.patchValue(this.cs.day_callapi(this.form.controls.dateFromFE.value));
    this.form.controls.dateTo.patchValue(this.cs.day_callapi(this.form.controls.dateToFE.value));
    this.spin.show();
    this.prepetualCountService.runPrepetual(this.form.value)
      .subscribe(result => {
        console.log(result.length);

        if (result.length <= 0) {
          this.toastr.error(
            "No Skus available for the selection",
            "Notification",{
              timeOut: 2000,
              progressBar: false,
            }
          )
          this.spin.hide();
          return;
        }

        this.toastr.success("Prepetual details created successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        sessionStorage.setItem('RunPerpetualResponse', JSON.stringify(result));
        this.spin.hide();
        let paramdata = this.cs.encrypt({ code: this.form.value, pageflow: 'New' });
        this.router.navigate(['/main/cycle-count/Prepetual-confirm/' + paramdata]);

      },
        error => {
          console.log(error);
          this.toastr.error(error.error, "Error", {
            timeOut: 2000,
            progressBar: false,
          });
          this.spin.hide();
          if (error.status == 415) {
            this.runPerpetual();
          }
        });
  }
}
