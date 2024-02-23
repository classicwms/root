

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
  selector: 'app-picking-report',
  templateUrl: './picking-report.component.html',
  styleUrls: ['./picking-report.component.scss']
})
export class PickingReportComponent implements OnInit {
  
    isShowDiv = false;
    table = false;
    fullscreen = false;
    search = true;
    back = false;
  
  
  
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    showFiller = false;

   // displayedColumns: string[] = [ 'statusId', 'refDocNumber','referenceField1', 'lineNumber', 'itemCode', 'description', 'pickedPackCode', 'proposedStorageBin','assignedPickerId','partnerCode', 'pickConfirmQty', 'pickupConfirmedOn', 'pickupCreatedOn', 'leadTime'];

    //wms demo
    displayedColumns: string[] = [ 'statusId', 'refDocNumber','referenceField1', 'lineNumber', 'itemCode', 'description', 'pickedPackCode', 'proposedStorageBin','assignedPickerId','partnerCode', 'pickConfirmQty', 'pickupConfirmedOn'];
    sub = new Subscription();
    ELEMENT_DATA: any[] = [];
  StatusList: any[] = [];

  
    constructor(public dialog: MatDialog,
      private service: StocksampleService,
      private http: HttpClient,
      // private cas: CommonApiService,
      private fb: FormBuilder,
      public toastr: ToastrService,
      private router: Router,
      private reportService: ReportsService,
      private spin: NgxSpinnerService,
      public cs: CommonService,
      private pickingService: PickingService,
      private auth: AuthService) { 

        this.StatusList = [
          {value: 48, label: '48 - Picking'},
          {value: 50, label: '50 - Picked'},
          {value: 51, label: '51 - Denial'},
          {value: 59, label: '59 - Delivered'},
      ];

      }
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
    itemCodeList: any[] = [];

    onItemType(searchKey) {
      let searchVal = searchKey?.filter;
      if (searchVal !== '' && searchVal !== null) {
        forkJoin({
          itemList: this.reportService.getItemCodeDropDown(searchVal.trim()).pipe(catchError(err => of(err))),
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

    multiselectStorageList: any[] = [];
    storageBinList1: any[] = [];
    selectedStorageBin: any[] = [];
    onStorageType(searchKey) {
      let searchVal = searchKey?.filter;
      if (searchVal !== '' && searchVal !== null) {
        forkJoin({
          storageList: this.reportService.getStorageDropDown(searchVal.trim()).pipe(catchError(err => of(err))),
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

    refDocNumber = '';
    pickedPackCode = '';
    itemCode = '';
    pickedStorageBin: any;
    toPickConfirmedOn = '';
    fromPickConfirmedOn = '';
    statusId= [];

    reset(){
      this.refDocNumber = '';
      this.pickedPackCode = '';
      this.itemCode = '';
      this.pickedStorageBin= '';
      this.toPickConfirmedOn = '';
      this.fromPickConfirmedOn = '';
      this.statusId= [];
    }
    
    filtersearch(){
      if(this.fromPickConfirmedOn == null || this.fromPickConfirmedOn == undefined || !this.fromPickConfirmedOn){
        this.toastr.error("Please fill the Pickup Date to continue", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.cs.notifyOther(true);
        return
      }

      this.spin.show();
      let obj: any = {};

      obj.itemCode = [];
      if (this.itemCode != null && this.itemCode.trim() != "") {
        obj.itemCode.push(this.itemCode)
      }

      obj.pickedPackCode = [];
      if (this.pickedPackCode != null && this.pickedPackCode.trim() != "") {
        obj.pickedPackCode.push(this.pickedPackCode)
      }

      obj.pickedStorageBin = [];
      if (this.pickedStorageBin != null) {
        obj.pickedStorageBin.push(this.pickedStorageBin);
      }

      obj.refDocNumber = [];
      if (this.refDocNumber != null && this.refDocNumber.trim() != "") {
        obj.refDocNumber.push(this.refDocNumber)
      }

      //obj.toPickConfirmedOn = [];
      console.log(this.toPickConfirmedOn)
      // if (this.toPickConfirmedOn != null) {
      //   obj.toPickConfirmedOn.push(this.cs.day_callapi(this.toPickConfirmedOn))
      // }
      obj.toPickConfirmedOn = this.cs.day_callapiSearch(this.toPickConfirmedOn);
      obj.fromPickConfirmedOn = this.cs.day_callapiSearch(this.fromPickConfirmedOn);
      obj.warehouseId = [this.auth.warehouseId]

      obj.statusId = this.statusId

      this.sub.add(this.pickingService.getpickingline(obj).subscribe(res => {
        console.log(res)
   //     let result = res.filter((x: any) => x.statusId == 50);
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

          "Status  ": (x.statusId == 50 ? 'Picked' : x.statusId == 51 ? 'Picker Denial' : x.statusId == 59 ? 'Delivered' : ''),
          "Order No  ": x.refDocNumber,
          'Pickup No': x.pickupNumber,
          'Line No': x.lineNumber,
          'Product Code': x.itemCode,
          'Product Description': x.description,
          'Pallet ID': x.pickedPackCode,
          'Bin Location': x.pickedStorageBin,
          'Picker': x.assignedPickerId,
          "Store": x.partnerCode,
          "Pick TO Qty": x.pickConfirmQty,
          "Pickup Date": this.cs.dateapi(x.pickupConfirmedOn),
  
          // 'Created By': x.createdBy,
          // 'Date': this.cs.dateapi(x.createdOn),
        });
  
      })
      res.push({

        "Status  ": '',
        "Order No  ": '',
        'Pickup No': '',
        'Line No': '',
        'Product Code': '',
        'Product Description':'',
        'Pallet ID': '',
        'Bin Location': '',
        'Picker': '',
        "Store": '',
        "Pick TO Qty": this.getPickingQty(),
        "Pickup Date": '',

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

      this.cs.exportAsExcel(res, "Picking");
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


    getPickingQty(){
      let total = 0;
      this.dataSource.data.forEach(x =>{
        total = total + (x.pickConfirmQty != null ? x.pickConfirmQty : 0)
      })
      return Math.round(total *100 / 100)
    }
  }
