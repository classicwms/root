import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { AssignPickerComponent } from "src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component";
import { GoodsReceiptService } from "../../Goods-receipt/goods-receipt.service";
import { AssignInvoiceComponent } from "../preinbound-new/assign-invoice/assign-invoice.component";
import { BatchComponent } from "./batch/batch.component";
import { PalletComponent } from "./pallet/pallet.component";
import { Pallet1Component } from "./pallet1/pallet1.component";
import { ReceiptUomComponent } from "./receipt-uom/receipt-uom.component";
import { VariantComponent } from "./variant/variant.component";

import { Location } from "@angular/common";
import { PreinboundService } from "../preinbound.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
@Component({
  selector: 'app-goodreceipt-create',
  templateUrl: './goodreceipt-create.component.html',
  styleUrls: ['./goodreceipt-create.component.scss']
})
export class GoodreceiptCreateComponent implements OnInit {
  isShowDiv = false;
  screenid: 1047 | undefined;

  displayedColumns: string[] = ['select', 'cases', 'lineNo', 'containerNo', 'invoiceNo', 'itemCode', 'itemDescription', 'manufacturerPartNo', 'businessPartnerCode', 'expectedArrivalDate', 'orderQty', 'referenceField5', 'variance', 'orderUom', 'stockTypeId', 'specialStockIndicatorId', 'remarks',];

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
    private pservice: PreinboundService,
    private service: GoodsReceiptService, private location: Location,
    public toastr: ToastrService, private dialog: MatDialog,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    public cs: CommonService,) { }
  sub = new Subscription();
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


  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }



  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({
    companyCodeId: [],
    confirmedBy: [],
    confirmedOn: [],
    containerNo: [],
    containerReceiptNo: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    dockAllocationNo: [],
    inboundOrderTypeId: [],
    languageId: [],
    plantId: [],
    preInboundNo: [],
    refDocNumber: [],
    referenceField1: [],
    referenceField10: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    stagingNo: [],
    statusId: [],
    updatedBy: [],
    updatedOn: [],
    vechicleNo: [],
    variance: [],
    warehouseId: [],
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

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  isbtntext = true;

  code: any;
  ngOnInit(): void {
    this.form.disable();
    // this.auth.isuserdata();
    this.dropdownlist();
    let code = this.route.snapshot.params.code;
    if (code != 'new') {

      let js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
    }
  }



  clientIdList: any[] = [];
  caseSubCategoryIdList: any[] = [];
  caseCategoryIdList: any[] = [];
  classIdList: any[] = [];
  dropdownlist() {
    // this.spin.show();
    // this.cas.getalldropdownlist([
    //   this.cas.dropdownlist.client.clientId.url,
    //   this.cas.dropdownlist.setup.caseCategoryId.url,
    //   this.cas.dropdownlist.setup.classId.url,
    // ]).subscribe((results) => {
    //   this.clientIdList = this.cas.foreachlist(results[0], this.cas.dropdownlist.client.clientId.key, { classId: 1 });
    //   this.caseCategoryIdList = this.cas.foreachlist(results[1], this.cas.dropdownlist.setup.caseCategoryId.key);
    //   this.classIdList = this.cas.foreachlist(results[2], this.cas.dropdownlist.setup.classId.key, { classId: [1, 2] });
    // }, (err) => {
    //   this.toastr.error(err, "");
    // });
    // this.spin.hide();
  }
  btntext = "Save";
  pageflow = "New";
  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = "Edit";
      this.btntext = 'Update';
      this.form.controls.preInboundNo.disable();
      this.form.controls.warehouseId.disable();
      if (data.pageflow == 'Display') {
        this.form.disable();
        this.isbtntext = false;
      }


      this.spin.show();
      this.sub.add(this.pservice.Getline(data.code.preInboundNo).subscribe(res => {

        res.forEach((x: any) => {
          x.caseCode = [];
          x.assignedUserId = "";
          x.palletCode = 'abc';
          x.stagingNo = this.form.controls.stagingNo.value;
          x.variance = x.orderQty - x.referenceField5;
        });
        this.spin.hide();
        this.dataSource = new MatTableDataSource<any>(res);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
      this.form.patchValue(data.code, { emitEvent: false });
this.form.controls.statusId.patchValue(this.cs.getstatus_text(this.form.controls.statusId.value))
    }
  }

  submit() {


    let data = this.selection.selected;


    this.sub.add(this.service.CreateLine(data.filter((x: any) => x.statusId == 5)).subscribe(res => {
      this.toastr.success(this.code.stagingNo + " Saved Successfully!");
      this.spin.hide();
      this.location.back();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));
    // this.submitted = true;
    // if (this.form.invalid) {
    //   this.toastr.error(
    //     "Please fill required fields to continue",
    //     ""
    //   );

    //   this.cs.notifyOther(true);
    //   return;
    // }

    // this.cs.notifyOther(false);
    // this.spin.show();
    // this.form.removeControl('updatedOn');
    // this.form.removeControl('createdOn');
    // this.form.controls.preInboundLine.patchValue(this.dataSource.data);


    // this.form.patchValue({ updatedby: this.auth.userID });
    // if (this.code) {
    //   this.sub.add(this.service.Update(this.form.getRawValue(), this.code.preInboundNo, this.code.warehouseId).subscribe(res => {
    //     this.toastr.success(res.preInboundNo + " updated successfully!");
    //     this.spin.hide();
    //     this.location.back();

    //   }, err => {

    //     this.cs.commonerrorNew(err);
    //     this.spin.hide();

    //   }));
    // }
    // else {
    //   this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
    //     this.toastr.success(res.preInboundNo + " Saved Successfully!");
    //     this.spin.hide();
    //     this.location.back();

    //   }, err => {
    //     this.cs.commonerrorNew(err);
    //     this.spin.hide();

    //   }));
    // }
  };
  back() {
    this.location.back();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }





  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;
  pallet(data: any): void {

    const dialogRef = this.dialog.open(PalletComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '9%', }, data: data
    });

    dialogRef.afterClosed().subscribe(result => {

      this.spin.show();
      this.sub.add(this.pservice.Getline(data.preInboundNo).subscribe(res => {

        res.forEach((x: any) => {
          x.caseCode = [];
          x.assignedUserId = "";
          x.palletCode = 'abc';
          x.stagingNo = this.form.controls.stagingNo.value;
          x.preInboundNo = data.preInboundNo
        });
        this.spin.hide();
        this.dataSource = new MatTableDataSource<any>(res);
        this.selection = new SelectionModel<any>(true, []);
        
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    });


  }
  pallet1(): void {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "Norification",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }



    const dialogRef = this.dialog.open(Pallet1Component, {
      disableClose: true,
      width: '55%',
      maxWidth: '50%',
      position: { top: '9%', },
      data: this.selection.selected
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        result.forEach((re: any) => {
          let index: number = this.dataSource.data.findIndex((d: any) => d === re);

          this.dataSource.data[index] = result;
        })


        let data = this.selection.selected;
        this.spin.show();

        this.sub.add(this.service.CreateLine(data).subscribe(res => {
          this.toastr.success(this.code.stagingNo + " Saved Successfully!","Notification",{
            timeOut: 2000,
            progressBar: false,
          });
          this.spin.hide();
          this.spin.show();
          this.sub.add(this.pservice.Getline(this.code.preInboundNo).subscribe(res => {
            res.forEach((x: any) => {
              x.caseCode = [];
              x.assignedUserId = "";
              x.palletCode = 'abc';
              x.stagingNo = this.form.controls.stagingNo.value;

            });
            this.spin.hide();
            this.dataSource = new MatTableDataSource<any>(res);
            this.selection = new SelectionModel<any>(true, []);
          }, err => {
            this.cs.commonerrorNew(err);
            this.spin.hide();
          }));

        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));

      }

    });
  }
  receiptuom(): void {

    const dialogRef = this.dialog.open(ReceiptUomComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', right: '0.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }
  variant(): void {

    const dialogRef = this.dialog.open(VariantComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', right: '0.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  batch(): void {

    const dialogRef = this.dialog.open(BatchComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', right: '0.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }




}

