import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { ContainerReceiptService } from "../container-receipt.service";

import { Location } from "@angular/common";
@Component({
  selector: 'app-containerreceipt-create',
  templateUrl: './containerreceipt-create.component.html',
  styleUrls: ['./containerreceipt-create.component.scss']
})
export class ContainerreceiptCreateComponent implements OnInit {
  screenid = 3043;
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
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: ContainerReceiptService, private location: Location,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,) { }
  sub = new Subscription();
  displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine', 'ten', 'eleven', 'twelve'];
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);

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
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }




  disabled = false;
  step = 0;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  panelOpenState = false;





  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({

    companyCodeId: [this.auth.companyId],
    consignmentType: [],
    containerNo: [, Validators.required],
    containerReceiptNo: [],
    containerType: [],
    createdBy: [this.auth.userID],
    createdOn: [this.cs.todayapi()],
    deletionIndicator: [0],
    dockAllocationNo: [],
    invoiceNo: [],
    languageId: [this.auth.languageId],
    numberOfCases: [],
    numberOfPallets: [],
    origin: [],
    partnerCode: [],
    plantId: [this.auth.plantId,],
    preInboundNo: [],
    refDocNumber: [],
    referenceField1: [],
    referenceField10: [],
    referenceField2: [this.cs.todayapi()],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    remarks: [],
    statusId: [],
    updatedBy: [this.auth.userID],
    updatedOn: [],
    warehouseId: [this.auth.warehouseId],
  });

  submitted = false;
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }



  isbtntext = true;

  code: any;
  js: any;
  ngOnInit(): void {
    this.form.controls.warehouseId.disable();
    this.form.controls.refDocNumber.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOn.disable();
    this.form.controls.updatedOn.disable();
    this.form.controls.updatedBy.disable();
    // this.auth.isuserdata();
   console.log(this.pageflow);
    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
      this.fill(js);
      //this.form.controls.containerNo.disable();
      this.code = js.code;
      this.js = js;


    }
    this.dropdownlist();
  }


  warehouseIdList: any[] = [];
  caseSubCategoryIdList: any[] = [];
  caseCategoryIdList: any[] = [];
  classIdList: any[] = [];
  dropdownlist() {
    this.spin.show();
    this.sub.add(this.service.GetWh().subscribe(res => {
      this.warehouseIdList = res;
      // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
      // this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
      // this.form.controls.referenceField2.patchValue(this.cs.dateapi(this.form.controls.referenceField2.value));
      this.spin.hide();
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  btntext = "Save";
  pageflow = "New";
  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = "Edit";
      console.log(this.pageflow);
      this.btntext = 'Update';
      if(this.pageflow == "Edit"){
      this.form.controls.containerReceiptNo.disable();
      this.form.controls.containerNo.disable();
      }
      if (data.pageflow == 'Display') {
        console.log('Display')
        this.form.disable();
        this.isbtntext = false;
        this.form.controls.containerReceiptNo.disable();
      }


      this.spin.show();
      this.sub.add(this.service.Get(data.code.containerReceiptNo, data.code.companyCodeId, data.code.plantId, data.code.languageId, data.code.warehouseId,).subscribe(res => {
        this.form.patchValue(res, { emitEvent: false });
        // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        // this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
        // this.form.controls.referenceField2.patchValue(this.cs.dateapi(this.form.controls.referenceField2.value));
        this.spin.hide();

        if (data.pageflow == 'Edit') {
          this.form.controls.containerReceiptNo.disable();
         }
        // this.getclient_class(this.form.controls.classId.value);
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
  }


  // activeUser = false;
  // transactionHistory(data) {
  //   if (localStorage.getItem('transactionHistory') != null && localStorage.getItem('transactionHistory') != undefined) {
  //     let item = JSON.parse("[" + localStorage.getItem('transactionHistory') + "]");
  //     if (item[0].statusId == 1) {
  //       this.form.disable();
  //       this.isbtntext = false;
  //     }
  //   } else {
  //     let obj: any = {};
  //     obj.languageId = data.code.languageId;
  //     obj.companyId = data.code.companyId;
  //     obj.plantId = data.code.plantId;
  //     obj.warehouseId = data.code.warehouseId;
  //     obj.module = 'Inbound';
  //     obj.menu = 'Container Receipt';
  //     obj.subMenu = 'Container Receipt';
  //     obj.transactionid = data.code.containerReceiptNo;
  //     obj.statusId = 1;
  //     this.activeUser = true;
  //     localStorage.setItem('transactionHistory', JSON.stringify(obj));
  //   }
  // }

  ngOnDestroy() {
    // if (this.js.pageflow == 'Edit' && this.activeUser == true) {
    //   localStorage.removeItem('transactionHistory');
    // }
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }


  submit() {
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        ""
      );

      this.cs.notifyOther(true);
      return;
    }

    this.cs.notifyOther(false);
    this.spin.show();
    this.form.removeControl('updatedOn');
    this.form.removeControl('createdOn');

    this.form.controls.referenceField2.patchValue(this.cs.day_callapi
      (this.form.controls.referenceField2.value));

    this.form.patchValue({ updatedby: this.auth.userID });
    if (this.code) {
      this.sub.add(this.service.Update(this.form.getRawValue(), this.code.containerReceiptNo, this.code.companyCodeId, this.code.plantId, this.code.languageId, this.code.warehouseId).subscribe(res => {
        this.toastr.success(this.code.containerReceiptNo + " updated successfully!");
        this.spin.hide();
        this.location.back();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.containerReceiptNo + " Saved Successfully!");
        this.spin.hide();
        this.location.back();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  };
  back() {
    this.location.back();
  }

}

