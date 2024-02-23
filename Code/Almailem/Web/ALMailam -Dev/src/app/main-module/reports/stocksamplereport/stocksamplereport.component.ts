import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of, Subscription } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { MasterService } from "src/app/shared/master.service";
import { ReportsService } from "../reports.service";
import { stockElement, StocksampleService } from "./stocksample.service";

@Component({
  selector: 'app-stocksamplereport',
  templateUrl: './stocksamplereport.component.html',
  styleUrls: ['./stocksamplereport.component.scss']
})
export class StocksamplereportComponent implements OnInit {

  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;

  warehouseList: any[] = [];
  itemCodeList: any[] = [];

  selectedWarehouseList: any[] = [];
  selectedItemCodeList: any[] = [];

  sendingWarehouseList: any[] = [];
  sendingItemCodeList: any[] = [];

  selectedItems: any[] = [];
  selectedItems1: any[] = [];

  multiselectWarehouseList: any[] = [];
  multiselectItemCodeList: any[] = [];

  multiWarehouseList: any[] = [];
  multiItemCodeList: any[] = [];

  dropdownSettings: IDropdownSettings = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  showFiller = false;
  displayedColumns: string[] = ['itemCode','referenceField8','referenceField9', 'storageBin', 'referenceField10', 'barcodeId', 'inventoryQuantity', 'allocatedQuantity', 'total', 'stockTypeId',   ];
  sub = new Subscription();
  ELEMENT_DATA: stockElement[] = [];

  pageSize = 500;
  pageNumber = 0;
  totalRecords = 0;
  productCode: any = null;
  storageBinCode: any = null;
  packBarcodes: any = null;

  constructor(public dialog: MatDialog,
    private service: StocksampleService,
    // private cas: CommonApiService,
    public toastr: ToastrService,
    private router: Router,
    private spin: NgxSpinnerService,
    private cs: CommonService,
    private masterService: MasterService,
    private reportService: ReportsService,
    private auth: AuthService) { }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  ngOnInit(): void {
    // this.auth.isuserdata();
    this.dropdownfill();
  }
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }


  multiselectStorageList: any[] = [];
  storageBinList1: any[] = [];
  selectedStorageBin: any[] = [];
  onStorageType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        storageList: this.reportService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
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


  onItemType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        itemList: this.reportService.getItemCodeDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ itemList }) => {
          if (itemList != null && itemList.length > 0) {
            this.multiselectItemCodeList = [];
            this.itemCodeList = itemList;
            this.itemCodeList.forEach(x => this.multiselectItemCodeList.push({value: x.itemCode, label: x.itemCode + ' - ' + x.manufacturerName + ' - ' + x.description}))
          }
        });
    }
  }


  dataSource = new MatTableDataSource<stockElement>(this.ELEMENT_DATA);
  selection = new SelectionModel<stockElement>(true, []);

  div1Function() {
    this.table = !this.table;
  }
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;
    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
  }

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator: MatPaginator; // Pagination
  // Pagination

  totalQty = 0;

  nonZeroTotal: any[] = [];
  nonZeroExcel: any[] = [];
  
  storageSectionId: any [] = [];
  filtersearch(excel = false) {
    if ((this.selectedItems == null && this.selectedItems != undefined)) {
      this.spin.show();
      this.toastr.error("Please fill the required fields. Required fields are Warehouse No", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.selectedWarehouseList = [];
    this.selectedItemCodeList = [];
    this.selectedItems.forEach(element => {
      this.selectedWarehouseList.push(element.item_id);
    });
    this.sendingWarehouseList = this.selectedWarehouseList;

    this.selectedItems1.forEach(element => {
      this.selectedItemCodeList.push(element.item_id);
    });
    this.sendingItemCodeList = this.selectedItemCodeList;
    let obj: any = {};
    obj.itemCode = this.sendingItemCodeList;
    if (this.productCode != null && this.productCode.trim() != "") {
      obj.itemCode.push(this.productCode)
    }

    obj.packBarcodes=[];
    if (this.packBarcodes != null && this.packBarcodes.trim() != "") {
      obj.packBarcodes.push(this.packBarcodes)
    } 
    
    // obj.storageBin = [];
    // if (this.storageBinCode != null && this.storageBinCode.trim() != "") {
    //   obj.storageBin.push(this.storageBinCode)
    // }
    obj.storageBin = []
    if (this.storageBinCode != null)
    obj.storageBin.push(this.storageBinCode);


    obj.storageSectionId = [];
    if (this.storageSectionId != null) {
      obj.storageSectionId = (this.storageSectionId);
    }

    obj.warehouseId = this.sendingWarehouseList;

    this.spin.show();
    this.totalQty = 0;

    if (!excel) {
      this.nonZeroTotal = []
      this.sub.add(this.service.search(obj).subscribe((res: any) => {
        console.log(res)
        this.spin.hide();
        res.forEach(element => {
          if(element.inventoryQuantity < 0){
            element.inventoryQuantity = 0;
          }
          this.totalQty += element.inventoryQuantity;

          element.total  = element.inventoryQuantity + element.allocatedQuantity;
          if(element.stockTypeId == 1){
            element.stockTypeId1 = "On Hand"
          }
          if(element.stockTypeId == 7){
            element.stockTypeId1 = "Hold"
          }

        });

    res.forEach(element => {
      if(element.total > 0){
        this.nonZeroTotal.push(element)
       }
    });
        this.dataSource = new MatTableDataSource<stockElement>(this.nonZeroTotal);
        this.selection = new SelectionModel<stockElement>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        // this.totalRecords = res.totalElements;
        this.table = true;
        this.search = false;
        this.fullscreen = false;
        this.back = true;
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    } else {
      this.downloadexcel(obj);
    }
  }


  reset(){
    this.storageSectionId = [];
    this.storageBinCode = '';
    this.packBarcodes = '';
    this.productCode = '';
  }

  downloadexcel(obj: any) {
    this.nonZeroExcel = [];
    this.spin.show();
    this.sub.add(this.service.search(obj).subscribe((data: any) => {


      data.forEach(element => {
        this.totalQty += element.inventoryQuantity;
      });
      data.forEach(element => {
        if(element.inventoryQuantity < 0){
          element.inventoryQuantity = 0;
        }
      });

//total
      let total = 0;
      data.forEach(element => {
        total = element.inventoryQuantity + element.allocatedQuantity;
        
        element.total = total

        if(element.stockTypeId == 1){
          element.stockTypeId1 = "On Hand"
        }
        if(element.stockTypeId == 7){
          element.stockTypeId1 = "Hold"
        }
      });
      data.forEach(element => {
        if(element.total > 0){
          this.nonZeroExcel.push(element)
         }
      });

      this.spin.hide();
      if (this.nonZeroExcel.length > 0) {
        this.nonZeroExcel.forEach(x => {
          res.push({
            'Part No': x.itemCode,
            "Description": x.referenceField8,
            'Mfr Part No': x.referenceField9,
            "Storage Bin": x.storageBin,
            'Storage Section': x.referenceField10,
            'Barcode Id': x.barcodeId,
            'Inv Qty': x.inventoryQuantity,
            'Alloc Qty': x.allocatedQuantity,
            'Total': x.total,
            "Stock Type": x.stockTypeId1,
            // 'Status': x.statusId,
            // 'Date': this.cs.dateapi(x.createdOn),
          });
        })
        this.cs.exportAsExcel(res, "Stock List Report By Bin Location");
      } else {
        this.toastr.error(
          "No data present",
          "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
      }

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
    var res: any = [];

  }

  pageHandler($event: PageEvent) {
    this.pageNumber = $event.pageIndex;
    this.pageSize = $event.pageSize;
    this.filtersearch(false);
  }

  togglesearch() {
    this.search = false;
    this.table = true;
    this.fullscreen = false;
    this.back = true;
  }
  backsearch() {
    this.table = true;
    this.search = true;
    this.fullscreen = true;
    this.back = false;
  }
  multiSelectStorageSectionID1: any[] = [];
  multiSelectStorageSectionID: any[] = [];
  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
      //  itemCode: this.reportService.getItemCodeDetails().pipe(catchError(err => of(err))),
      storageSection: this.masterService.getStorageSectionMasterDetails().pipe(catchError(err => of(err)))

    })
      .subscribe(({ warehouse, storageSection }) => {
        //   .subscribe(({ warehouse, itemCode, storageSection }) => {
          if(this.auth.userTypeId != 3){
            this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
          }else{
            this.warehouseList = warehouse
          }
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ item_id: x.warehouseId, item_text: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
        this.multiselectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.item_id == this.auth.warehouseId)
            this.selectedItems = [warehouse];
        })
        //   this.itemCodeList = itemCode;
        //   this.itemCodeList.forEach(x => this.multiItemCodeList.push({ item_id: x.itemCode, item_text: x.itemCode + ' - ' + x.description }))
        //      this.multiselectItemCodeList = this.multiItemCodeList;

        this.multiSelectStorageSectionID1 = storageSection.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        this.multiSelectStorageSectionID1.forEach(x => this.multiSelectStorageSectionID.push({ value: x.storageSectionId, label: x.storageSectionId}));
        this.multiSelectStorageSectionID = this.cs.removeDuplicatesFromArrayNewstatus(this.multiSelectStorageSectionID)

      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  onItemSelect(item: any) {
  }

  onSelectAll(items: any) {
  }

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
  checkboxLabel(row?: stockElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseId + 1}`;
  }



  clearselection(row: any) {

    this.selection.clear();
    this.selection.toggle(row);
  }
  // getBillableAmount() {
  //   let total = 0;
  //   this.dataSource.data.forEach(element => {
  //     total = total + (element.s != null ? element.s : 0);
  //   })
  //   return (Math.round(total * 100) / 100);
  // }

  getTotal(){
    let total = 0;
    this.dataSource.data.forEach(x =>{
      total = total + (x.total != null ? x.total : 0)
    })
    return Math.round(total *100 / 100)
  }
  getAllocQty(){
    let total = 0;
    this.dataSource.data.forEach(x =>{
      total = total + (x.allocatedQuantity != null ? x.allocatedQuantity : 0)
    })
    return Math.round(total *100 / 100)
  }
  getInvQty(){
    let total = 0;
    this.dataSource.data.forEach(x =>{
      total = total + (x.inventoryQuantity != null ? x.inventoryQuantity : 0)
    })
    return Math.round(total *100 / 100)
  }
}