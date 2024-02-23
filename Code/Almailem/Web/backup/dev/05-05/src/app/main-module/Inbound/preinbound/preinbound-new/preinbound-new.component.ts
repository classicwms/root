import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AssignInvoiceComponent } from "./assign-invoice/assign-invoice.component";


import { Location } from "@angular/common";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PreinboundService } from "../preinbound.service";
import { ContainerReceiptService } from "../../Container-receipt/container-receipt.service";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";

interface SelectItem {
  id: number;
  itemName: string;
}



@Component({
  selector: 'app-preinbound-new',
  templateUrl: './preinbound-new.component.html',
  styleUrls: ['./preinbound-new.component.scss']
})
export class PreinboundNewComponent implements OnInit {
  screenid: 1045 | undefined;

  displayedColumns: string[] = ['select', 'statusId', 'invoiceNo', 'lineno', 'itemCode', 'itemDescription', 'manufacturerPartNo', 'businessPartnerCode', 'orderQty', 'orderUom', 'stockTypeId', 'expectedArrivalDate',];
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
    private service: PreinboundService, private location: Location,
    public toastr: ToastrService, private dialog: MatDialog,
    private sservice: ContainerReceiptService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,) { }
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

  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  containerList: any[] = [];
  filtercontainerList: any[] =[];
  selectedItems1: SelectItem[] = [];
  multiitemlistList: SelectItem[] = [];
  multiSelectcontainerList: SelectItem[] = [];

  dropdownSettings = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

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
    companyCode: [],
    containerNo: [, [Validators.required]],
    containerType: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    inboundOrderTypeId: [],
    languageId: [],
    noOfContainers: [],
    plantId: [],
    preInboundLine: [],
    preInboundNo: [],
    refDocDate: [],
    refDocNumber: [],
    referenceDocumentType: [],
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
    statusId: [],
    updatedBy: [],
    updatedOn: [],
    warehouseId: [],
  });

  submitted = false;
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    // if (this.email.hasError('required')) {
    //   return ' Field should not be blank';
    // }
    return this.email.hasError('required') ? ' Field should not be blank' : '';
      
  }

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  isbtntext = true;

  code: any;
  ngOnInit(): void {
    this.form.disable();
    this.form.controls.containerNo.enable();
    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
    }

  }

  isProcess = true;
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
      this.sub.add(this.service.Get(data.code.preInboundNo, data.code.warehouseId).subscribe(res => {
        this.form.patchValue(res, { emitEvent: false });
        this.form.controls.containerNo.patchValue([{id: res.containerNo,itemName: res.containerNo}]);
       // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        //this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
       // this.form.controls.refDocDate.patchValue(this.cs.dateapi(this.form.controls.refDocDate.value));

        if (res.statusId == 5)
          this.isProcess = false;
        this.spin.hide();
        this.dataSource = new MatTableDataSource<any>(res.preInboundLine);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));

      this.spin.show();
      this.sub.add(this.sservice.Getall().subscribe(res => {
        this.packBarcodesList1 = res.filter((x: any) => !x.refDocNumber);
        this.packBarcodesList = this.packBarcodesList1.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        console.log(this.packBarcodesList)
        this.packBarcodesList.forEach(x => this.multiitemlistList.push({id: x.containerNo, itemName: x.containerNo}))
        this.multiSelectcontainerList = this.multiitemlistList;
        this.spin.hide();
        // this.sub.add(this.sservice.Getall().subscribe(res => {
        //   this.packBarcodesList = res.filter((x: any) => !x.refDocNumber);
        //   this.packBarcodesList = this.packBarcodesList.filter(element => {
        //     return element.warehouseId === this.form.controls['warehouseId'].value;
        //   });
        //   this.packBarcodesList.forEach(x => this.multiitemlistList.push({id: x.containerNo, itemName: x.containerNo}))
        //   this.multiSelectcontainerList = this.multiitemlistList;
        //   this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
  }
  packBarcodesList: any[] = [];  
  packBarcodesList1: any[] = [];
  submit() {

    this.form.patchValue({containerNo: this.selectedItems1[0].id});
    let isnotinvoice = false;

    this.dataSource.data.forEach((x: any) => {
      x.containerNo = this.form.controls.containerNo.value;
      x.inboundOrderTypeId = this.form.controls.inboundOrderTypeId.value;

      if (!x.invoiceNo) isnotinvoice = true;
    });
    if (isnotinvoice) {
      this.toastr.error(
        "Please fill invoiceNo to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      )
      return;
    }
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.spin.show();
    this.sub.add(this.service.processASN(this.dataSource.data).subscribe(res => {
      this.toastr.success(this.form.controls.preInboundNo.value + " Processed successfully!","",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.spin.show();
      this.sub.add(this.sservice.search({ containerNo: [this.form.controls.containerNo.value] }).subscribe(res => {
        //   this.toastr.success(this.form.controls.preInboundNo.value + " Processed successfully!");
        this.spin.hide();
        this.spin.show();
        this.sub.add(this.sservice.Update({ refDocNumber: this.form.controls.refDocNumber.value }, res[0].containerReceiptNo,).subscribe(res => {
          //   this.toastr.success(this.form.controls.preInboundNo.value + " Processed successfully!");
          this.spin.hide();
          this.location.back();

        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));


      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));


    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));







    return;

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
    this.form.controls.preInboundLine.patchValue(this.dataSource.data);


    this.form.patchValue({ updatedby: this.auth.userID });
    if (this.code) {
      this.sub.add(this.service.Update(this.form.getRawValue(), this.code.preInboundNo, this.code.warehouseId).subscribe(res => {
        this.toastr.success(res.preInboundNo + " updated successfully!");
        this.spin.hide();
        this.location.back();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.preInboundNo + " Saved Successfully!");
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
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }

  assign() {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      return;
    }
    const dialogRef = this.dialog.open(AssignInvoiceComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.selection.selected.forEach((x: any) => x.invoiceNo = result);
    });

  }
  bom() {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any one Row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      return;
    }
    if (this.selection.selected.length > 1) {
      this.toastr.error("Kindly select any one Row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      return;
    }
    let data = this.selection.selected[0];
    this.spin.show();
    this.sub.add(this.service.createbom(data.itemCode, data.lineNo, data.preInboundNo, data.refDocNumber, data.warehouseId).subscribe(res => {
      this.toastr.success(res.itemCode + " BOM created successfully!");
      this.spin.hide();
      this.location.back();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));

  }

  onItemSelect(item: any) {
    console.log(item);
  }

OnItemDeSelect(item:any){
    console.log(item);
    console.log(this.selectedItems1);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}


downloadexcel() {
  // if (excel)
  var res: any = [];
  this.dataSource.data.forEach(x => {
    res.push({
      "Invoice No": x.invoiceNo,
      'Line No': x.lineno,
      "Product Code ": x.itemCode,
      "Description ": x.itemDescription,
      'Mfr Part No': x.manufacturerPartNo,
      "Supplier Code ": x.businessPartnerCode,
      "Expected Qty ": x.orderQty,
      "UOM ": x.orderUom,
      "Stock Type ": x.stockTypeId,
      "Status  ": x.statusId,
      'Exp Arrival Date': this.cs.dateapi(x.expectedArrivalDate),
      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.createdOn),
    });

  })
  this.cs.exportAsExcel(res, "Preinbound");
}


}

