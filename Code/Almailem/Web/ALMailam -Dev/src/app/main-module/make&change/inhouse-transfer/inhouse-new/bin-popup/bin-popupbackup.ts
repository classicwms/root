import { SelectionModel } from "@angular/cdk/collections";
import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of, Subscription } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { ReportsService } from "src/app/main-module/reports/reports.service";
import { InhouseTransferService } from "../../inhouse-transfer.service";
import { sku } from "../inhouse-new.component";

interface SelectItem {
  id: number;
  itemName: string;
}


export interface bin {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  receipt: string;
}
const ELEMENT_DATA3: bin[] = [
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
];
@Component({
  selector: 'app-bin-popup',
  templateUrl: './bin-popup.component.html',
  styleUrls: ['./bin-popup.component.scss']
})
export class BinPopupComponent implements OnInit {


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

  form = this.fb.group({
    targetStock: [],
  });

  applySourceFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  applyDestinationFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.destinationDataSource.filter = filterValue.trim().toLowerCase();
  }
  sourceStorageBin: any;
  targetStorageBin: any;
  sourceItemCode: any;
  binlist: any[] = [];
  filterItemSelectList: any[] = [];
  load(sourceStorageBin: any) {
    this.spin.show();

    this.sub.add(this.service.GetInventoryv2({ warehouseId: [this.auth.warehouseId], storageBin: [sourceStorageBin], itemCode: this.sourceItemCode != null ? [this.sourceItemCode] : [] }).subscribe(res => {
      this.spin.hide();
      res.forEach((y: any) => {console.log(2); y.stockTypeIddes = y.stockTypeId == 1 ? ' On Hand' : 'Hold' });
      res.forEach((y: any) => {y.targetStock = y.stockTypeId == '1' ? '1' : '7'; console.log(y.targetStock) });

      this.dataSource = new MatTableDataSource<any>(res);
      this.destinationDataSource = new MatTableDataSource<any>(res);
      this.selection = new SelectionModel<any>(true, []);
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));
  }

  multiselectItemCodeList: any[] = [];
  itemCodeList: any[] = [];
  multiselectItemCodeList1: any[] = [];
itemCodeList1: any[] = [];
  onItemType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        itemList: this.ReportsService.getItemCodeDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ itemList }) => {
          if (itemList != null && itemList.length > 0) {
            this.multiselectItemCodeList = [];
            this.itemCodeList = itemList;
            this.itemCodeList.forEach(x => this.multiselectItemCodeList.push({value: x.itemCode, label: x.itemCode + ' - ' + x.manufacturerName + ' - ' + x.description }))
          }
        });
    }
  }

  multiselectStorageList: any[] = [];
  storageBinList1: any[] = [];
  selectedStorageBin: any[] = [];
  onStorageType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        storageList: this.ReportsService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ storageList }) => {
          if (storageList != null && storageList.length > 0) {
            console.log(3)
            this.multiselectStorageList = [];
            this.storageBinList1 = storageList;
            this.storageBinList1.forEach(x => this.multiselectStorageList.push({ value: x.storageBin, label: x.storageBin}))
          }
        });
    }
  }

  multiselectStorageList1: any[] = [];
  storageBinList11: any[] = [];
  selectedStorageBin1: any[] = [];
  onStorageType1(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        storageList: this.ReportsService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ storageList }) => {
          if (storageList != null && storageList.length > 0) {
            this.multiselectStorageList1 = [];
            this.storageBinList11 = storageList;
            this.storageBinList11.forEach(x => this.multiselectStorageList1.push({ value: x.storageBin, label: x.storageBin}))
          }
        });
    }
  }

  save() {
    console.log(this.form)

    if(!this.sourceStorageBin || this.sourceStorageBin == null || this.sourceStorageBin == undefined){
      this.toastr.error("Please select source Part No to continue", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      this.cs.notifyOther(true);
      return;
    }
    this.targetStorageBin = this.targetStorageBin;

    if (!this.targetStorageBin || this.targetStorageBin == null || this.targetStorageBin == undefined){
      this.toastr.error(
        "Please enter target bin to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );
      this.cs.notifyOther(true);
      return;
    }
    if (this.sourceStorageBin == this.targetStorageBin){
      this.toastr.error(
        "Source and target bin cannot be the same",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );
      this.cs.notifyOther(true);
      return;
    }
    if (this.selection.selected.length == 0){
      this.toastr.error(
        "Please select any row to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );
      return;
    }

 

  

    this.sub.add(this.service.GetAllBin({ warehouseId: [this.auth.warehouseId], storageBin: [this.targetStorageBin] }).subscribe(res => {
      if(res[0]){
       this.spin.show();
        this.selection.selected.forEach((x: any) => {
          this.data.inhouseTransferLine.push({
            caseCode: x.caseCode,
            packBarcodes: x.packBarcodes,
            palletCode: x.palletCode,
            plantId: x.plantId,
            sourceItemCode: x.itemCode,
            sourceStockTypeId: x.stockTypeId,
            sourceStorageBin: x.storageBin,
            specialStockIndicatorId: x.specialStockIndicatorId,
            targetItemCode: x.itemCode,
            targetStockTypeId: x.targetStock,
            targetStorageBin: this.targetStorageBin,
            transferConfirmedQty: x.inventoryQuantity1,
            transferOrderQty: x.inventoryQuantity1,
            transferUom: x.inventoryUom,
    
          });
        });
    
        this.sub.add(this.service.Create(this.data).subscribe(res => {
          this.spin.hide();
          this.toastr.success(res.transferNumber + ' Created successfully.', "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          this.destinationDataSource.data = [];
          this.targetStorageBin = null;
          this.sourceItemCode = null;
          this.sourceStorageBin = null;
          this.dataSource.data = [];
          this.data.inhouseTransferLine = [];
          //this.dialogRef.close(res);
    
        }, err => {
    
          this.cs.commonerrorNew(err);
          this.spin.hide();
    
        }));
      }else{
        this.toastr.error(
          "Target bins not found",
          "Notification"
        );
        this.cs.notifyOther(true);
        return;
      }
    } ));
    

  }
  selectedItems1: SelectItem[] = [];
  selectedItems: SelectItem[] = [];
  multibinlist: SelectItem[] = [];
  multiselectbinList: SelectItem[] = [];


  dropdownSettings = {
    singleSelection: true,
    text: "Select",
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };


  ngOnInit(): void {
  //  this.spin.show();

    // this.sub.add(this.service.GetAllBin().subscribe(res => {
    //   this.spin.hide();

    //   this.binlist = res;
    //   this.binlist.forEach(x => this.multibinlist.push({ id: x.storageBin, itemName: x.storageBin + (x.description == null ? '' : '- ' + x.description) }))
    //   this.multiselectbinList = this.multibinlist;
    // }, err => {

    //   this.cs.commonerrorNew(err);
    //   this.spin.hide();

    // }));
  }
  constructor(public dialogRef: MatDialogRef<BinPopupComponent>, private service: InhouseTransferService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, @Inject(MAT_DIALOG_DATA) public data: any,
    private auth: AuthService,
    private fb: FormBuilder,
    private cs: CommonService,private ReportsService: ReportsService) { }
  sub = new Subscription();



  displayedColumns: string[] = ['1no', '1no2', '1lineno', '1supcode',];
  displayedColumns2: string[] = ['select', 'no', 'no2', 'lineno', 'supcode',];
  dataSource = new MatTableDataSource<any>([]);
  destinationDataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.destinationDataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.destinationDataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: sku): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  onItemSelect(item: any) {
    this.sourceStorageBin = this.selectedItems[0].id;
    this.load(this.sourceStorageBin)
    console.log(item);

  }
  OnItemDeSelect(item: any) {
    console.log(item);
    console.log(this.selectedItems);
  }
  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any) {
    console.log(items);
  }

  binSelected(e){
    this.filterItemSelectList = [];
    this.sub.add(this.service.GetInventoryv2({storageBin: [e.value]}).subscribe(res => {
      res.forEach(x => this.filterItemSelectList.push({ value: x.itemCode, label: x.itemCode + ' - ' + x.referenceField8}))
    }))
  }

  
}