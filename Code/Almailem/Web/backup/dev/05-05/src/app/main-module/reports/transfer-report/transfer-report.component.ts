



  
  import { SelectionModel } from "@angular/cdk/collections";
import { HttpClient } from "@angular/common/http";
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
import { ContainerReceiptService } from "../../Inbound/Container-receipt/container-receipt.service";
import { PickingService } from "../../Outbound/picking/picking.service";
  import { ReportsService } from "../reports.service";
import  { stockElement, StocksampleService }  from "../stocksamplereport/stocksample.service";


@Component({
  selector: 'app-transfer-report',
  templateUrl: './transfer-report.component.html',
  styleUrls: ['./transfer-report.component.scss']
})
export class TransferReportComponent implements OnInit {
  
    isShowDiv = false;
    table = false;
    fullscreen = false;
    search = true;
    back = false;
  
  
  
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    showFiller = false;

    displayedColumns: string[] = ['sourceItemCode', 'sourceStockTypeId', 'sourceStorageBin', 'targetItemCode', 'targetStockTypeId', 'targetStorageBin', 'transferConfirmedQty', 'packBarcodes', 'confirmedBy', 'confirmedOn'];
    sub = new Subscription();
    ELEMENT_DATA: any[] = [];

    movementList: any[];
    submovementList: any[];

    constructor(public dialog: MatDialog,
      private service: StocksampleService,
      private http: HttpClient,
      // private cas: CommonApiService,
      private fb: FormBuilder,
      public toastr: ToastrService,
      private router: Router,
      private spin: NgxSpinnerService,
      public cs: CommonService,
      private ReportsService: ReportsService,
      private auth: AuthService) {}
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
    }
    ngAfterViewInit() {
      this.dataSource.paginator = this.paginator;
    }


    multiselectItemCodeList: any[] = [];
    multiselectItemCodeList1: any[] = [];
    itemCodeList: any[] = [];
    itemCodeList1: any[] = [];
    onItemType(searchKey) {
      let searchVal = searchKey?.filter;
      if (searchVal !== '' && searchVal !== null) {
        forkJoin({
          itemList: this.ReportsService.getItemCodeDropDown(searchVal.trim()).pipe(catchError(err => of(err))),
        })
          .subscribe(({ itemList }) => {
            if (itemList != null && itemList.length > 0) {
              this.multiselectItemCodeList = [];
              this.itemCodeList = itemList;
              this.itemCodeList.forEach(x => this.multiselectItemCodeList.push({ value: x.itemCode, label: x.itemCode + ' - ' + x.description }))
            }
          });
      }
    }

    onItemType1(value) {
      let searchTarget = value?.filter;
      if (searchTarget !== '' && searchTarget !== null) {
        forkJoin({
          itemList1: this.ReportsService.getItemCodeDropDown(searchTarget.trim()).pipe(catchError(err => of(err))),
        })
          .subscribe(({ itemList1 }) => {
            if (itemList1 != null && itemList1.length > 0) {
              this.multiselectItemCodeList1 = [];
              this.itemCodeList1 = itemList1;
              this.itemCodeList1.forEach(x => this.multiselectItemCodeList1.push({ value: x.itemCode, label: x.itemCode + ' - ' + x.description }))
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
          storageList: this.ReportsService.getStorageDropDown(searchVal.trim()).pipe(catchError(err => of(err))),
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
          storageList: this.ReportsService.getStorageDropDown(searchVal.trim()).pipe(catchError(err => of(err))),
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

    

    sourceItemCode: any;
    sourceStockTypeId = '';
    sourceStorageBin: any;
    targetItemCode: any;
    targetStorageBin: any;
    stockTypeId = '';
    startConfirmedOn = '';
    endConfirmedOn = '';


    reset(){
      this.sourceItemCode = '';
      this.sourceStockTypeId = '';
      this.sourceStorageBin = '';
      this.targetItemCode = '';
      this.targetStorageBin = '';
      this.stockTypeId = '';
      this.startConfirmedOn = '';
      this.endConfirmedOn = '';
    }
    
    filtersearch(){
      if(this.endConfirmedOn == null || this.endConfirmedOn == undefined || !this.endConfirmedOn){
        this.toastr.error("Please fill the date to continue", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.cs.notifyOther(true);
        return
      }

this.spin.show();
      let obj: any = {};

      obj.sourceItemCode = [];
      if (this.sourceItemCode != null) {
        obj.sourceItemCode.push(this.sourceItemCode)
      }
      obj.sourceStockTypeId = [];
      if (this.sourceStockTypeId != null && this.sourceStockTypeId.trim() != "") {
        obj.sourceStockTypeId.push(this.sourceStockTypeId)
      }
      obj.sourceStorageBin = [];
      if (this.sourceStorageBin != null) {
        obj.sourceStorageBin.push(this.sourceStorageBin)
      }

      obj.targetItemCode = [];
      if (this.targetItemCode != null) {
        obj.targetItemCode.push(this.targetItemCode)
      }

      obj.targetItemCode = [];
      if (this.targetItemCode != null) {
        obj.targetItemCode.push(this.targetItemCode)
      }   
      
      obj.targetStorageBin = [];
      if (this.targetStorageBin != null) {
        obj.targetStorageBin.push(this.targetStorageBin)
      }

      obj.stockTypeId = [];
      if (this.stockTypeId != null && this.stockTypeId.trim() != "") {
        obj.stockTypeId.push(this.stockTypeId)
      }

     obj.startConfirmedOn = this.cs.day_callapiSearch(this.startConfirmedOn);
     obj.endConfirmedOn = this.cs.day_callapiSearch(this.endConfirmedOn);
      obj.warehouseId = [this.auth.warehouseId]

      // obj.statusId = []

      this.sub.add(this.ReportsService.getTransferReport(obj).subscribe(res => {
        let result = res.filter((x: any) => x.statusId == 50);
        this.dataSource = new MatTableDataSource<any>(res);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table = true;
        this.search = false;
        this.fullscreen = false;
        this.back = true;
        this.spin.hide();
        this.totalRecords = this.dataSource.data.length
      },
        err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
    }

    dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
    selection = new SelectionModel<any>(true, []);
  
  
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
  

  

    totalRecords = 0;
    downloadexcel() {
      // if (excel)
      var res: any = [];
      this.dataSource.data.forEach(x => {
        res.push({

          "Source Item Code": x.sourceItemCode,
          'Source StockType ID': x.sourceStockTypeId,
          "Source Storage Bin": x.sourceStorageBin,
          "Target Item Code": x.targetItemCode,
          "Target StockType ID": (x.targetStockTypeId == 1 ? 'On Hand' : x.targetStockTypeId == 7 ? 'Hold' : '') ,
          "Target Storage Bin":  x.targetStorageBin,
          "Transfer Confirmed Qty":  x.transferConfirmedQty,
          "Pack Barcodes": x.packBarcodes,
          "Confirmed By": x.confirmedBy,
          "Confirmed On": this.cs.dateapi(x.confirmedOn),
  
          // 'Created By': x.createdBy,
          // 'Date': this.cs.dateapi(x.createdOn),
        });
  
      })
      res.push({

        "Source Item Code": '',
        'Source StockType ID': '',
        "Source Storage Bin": '',
        "Target Item Code": '',
        "Target StockType ID": '' ,
        "Target Storage Bin": '',
        "Transfer Confirmed Qty":  this.getTransferQty(),
        "Pack Barcodes": '',
        "Confirmed By": '',
        "Confirmed On": '',

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
      this.cs.exportAsExcel(res, "Transfer");
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
    checkboxLabel(row?: any): string {
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

    getTransferQty(){
      let total = 0;
      this.dataSource.data.forEach(x =>{
        total = total + (x.transferConfirmedQty != null ? x.transferConfirmedQty : 0)
      })
      return Math.round(total *100 / 100)
    }
  }


