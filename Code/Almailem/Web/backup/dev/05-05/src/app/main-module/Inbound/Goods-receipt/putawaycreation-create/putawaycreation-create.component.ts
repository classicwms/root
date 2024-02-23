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
import { clientcategory } from "src/app/main-module/setup-storage/strategies/strategies.component";
import { PutawayService } from "../../putaway/putaway.service";
import { GoodsReceiptService } from "../goods-receipt.service";
import { PutawayDetailsComponent } from "./putaway-details/putaway-details.component";

import { Location } from "@angular/common";
import { ItemReceiptService } from "../../Item receipt/item-receipt.service";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";

interface SelectItem {
  item_id: number;
  item_text: string;
}



@Component({
  selector: 'app-putawaycreation-create',
  templateUrl: './putawaycreation-create.component.html',
  styleUrls: ['./putawaycreation-create.component.scss']
})
export class PutawaycreationCreateComponent implements OnInit {
  screenid: 1052 | undefined;
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


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({
    putAwayQuantity: [],
    putAwayNumber: [],
    containerType: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    proposedStorageBin: [],
    languageId: [],
    proposedHandlingEquipment: [],
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


  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private pservice: ItemReceiptService,
    private service: PutawayService, private location: Location,
    public toastr: ToastrService, private dialog: MatDialog,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,) { }
  sub = new Subscription();
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);
  subscreen(): void {

    const dialogRef = this.dialog.open(PutawayDetailsComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '90%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  isbtntext = true;
  code: any;
  ngOnInit(): void {
    this.form.disable();
    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {

      let js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
    }

  }

  StorageBinList: any[] = [];
  btntext = "Save";
  pageflow = "New";
  elementdata: any;
  selectedItems: any[] = [];
  multiSelectbinList: any[] = [];
  multibinList: any[] = [];


  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };
  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;
      this.btntext = 'Update';
      this.form.controls.preInboundNo.disable();
      this.form.controls.warehouseId.disable();
      console.log(data.code)
      this.form.patchValue(data.code, { emitEvent: false });
      this.form.controls.statusId.patchValue(this.cs.getstatus_text(this.form.controls.statusId.value))
      this.spin.show();

      this.sub.add(this.service.findStorageBin({warehouseId: [this.auth.warehouseId]}).subscribe(res => {
// console.log(res)
//         this.StorageBinList.push(res);  
//         console.log(this.StorageBinList)
        res.forEach(x => this.multibinList.push({ value: x.storageBin, label: x.storageBin }))
        this.multiSelectbinList = this.multibinList;
        console.log(this.multiSelectbinList)
      }, err => {

        this.spin.hide();
      }));
      this.spin.show();
      if (data.pageflow == 'Display') {
        this.form.disable();
        this.isbtntext = false;

        this.sub.add(this.service.searchLine({ putAwayNumber: [data.code.putAwayNumber] }).subscribe(res => {

          // res.forEach((x: any) => {
          //   // x.putAwayQuantity = data.code.putAwayQuantity;
          //   x.confirmedStorageBin = this.form.controls.proposedStorageBin.value;
          //   x.putAwayNumber = this.form.controls.putAwayNumber.value;
          //   x.proposedStorageBin = this.form.controls.proposedStorageBin.value;
          // })

          this.elementdata = JSON.parse(JSON.stringify(res));
          this.spin.hide();
          this.dataSource = new MatTableDataSource<any>(res);
          this.selection = new SelectionModel<any>(true, []);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));

      }

      else {
        this.sub.add(this.pservice.searchline({ packBarcodes: [data.code.packBarcodes], refDocNumber: [data.code.refDocNumber], preInboundNo: [data.code.preInboundNo] }).subscribe(res => {

          res.forEach((x: any) => {
            x.orderQty = this.form.controls.putAwayQuantity.value
            // x.putAwayQuantity = data.code.putAwayQuantity;
            x.confirmedStorageBin = this.form.controls.proposedStorageBin.value;
            x.putAwayNumber = this.form.controls.putAwayNumber.value;
            x.proposedStorageBin = this.form.controls.proposedStorageBin.value;
          })

          this.elementdata = JSON.parse(JSON.stringify(res));
          this.spin.hide();
          this.dataSource = new MatTableDataSource<any>(res);
          this.selection = new SelectionModel<any>(true, []);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));



      }
    }
  }



  
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination

  submit() {

 

    let isnotinvoice = false;
    let totalPutawayQty=0;

    this.dataSource.data.forEach((x: any) => {
      totalPutawayQty=totalPutawayQty + x.putawayConfirmedQty;

      if (!x.confirmedStorageBin) isnotinvoice = true;
    });
    if(totalPutawayQty > this.form.controls.putAwayQuantity.value){
      this.toastr.error(
        "Total Putaway qty should not be greater than TO Qty",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      )
      return;
    }
    if (isnotinvoice) {
      this.toastr.error(
        "Please fill confirmedStorageBin to continue",
        "", {
        timeOut: 2000,
        progressBar: false,
      }
      )
      return;
    }


    this.spin.show();
    this.sub.add(this.service.CreateLine(this.dataSource.data).subscribe(res => {
      this.toastr.success(this.code.palletCode + " created successfully!", "", {
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.location.back();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));

    
  }
  displayedColumns: string[] = ['lineNo', 'itemCode', 'manufacturerPartNo','itemDescription', 'storageBin', 'orderQty', 'lineno','delete'];


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
  checkboxLabel(row?: clientcategory): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }

  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
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

  add() {

    this.dataSource.data.push(this.elementdata[0]);

    // this.elementdata.push(this.elementdata[0]);

    this.dataSource._updateChangeSubscription();
  }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

}

