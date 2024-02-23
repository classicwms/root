import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MasterService } from 'src/app/shared/master.service';
import { ReportsService } from '../reports.service';
import { StockListComponent } from './stock-list/stock-list.component';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
export interface variant {


  no: string;
  actions: string;
  status: string;
  warehouseno: string;
  preinboundno: string;
  countno: string;
  by: string;
  damage: string;
  available: string;
  hold: string;
}

interface SelectItem {
  item_id: string;
  item_text: string;
}

const ELEMENT_DATA: variant[] = [
  { no: "1", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "2", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "3", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "4", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "5", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "6", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "7", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "8", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "9", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "10", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "11", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },

];
@Component({
  selector: 'app-stock-report',
  templateUrl: './stock-report.component.html',
  styleUrls: ['./stock-report.component.scss']
})
export class StockReportComponent implements OnInit {


  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;
  itemText = '';
  itemCode = '';
  stockTypeText = 'All';
  warehouseList: any[] = [];
  isValid = true;

  selectedItems: any[] = [];

  multiselectWarehouseList: any[] = [];

  multiWarehouseList: any[] = [];

  dropdownSettings: IDropdownSettings = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  dataSource = new MatTableDataSource<any>();
  selection = new SelectionModel<any>(true, []);

  pageNumber = 0;
  pageSize = 100;
  totalRecords = 0;

  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  showFiller = false;
  // displayedColumns: string[] = ['warehouseno', 'countno', 'by', 'preinboundno', 'status', 'damage', 'hold', 'available'];

  displayedColumns: string[] = ['warehouseId', 'itemCode', 'manufacturerSKU', 'itemText', 'onHandQty', 'damageQty', 'holdQty', 'availableQty'];

  sub = new Subscription();
  ELEMENT_DATA: variant[] = [];

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination

  constructor(
    private router: Router,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private masterService: MasterService,
    public cs: CommonService,
    private reportService: ReportsService,
    public auth: AuthService
  ) { }

  ngOnInit(): void {
    // this.auth.isuserdata();
    this.dropdownfill();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
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

  
  reset() {
    this.itemCode = '';
    this.itemText = '';
    this.stockTypeText = '';
  }

  filtersearch() {
    if ((this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) &&
      (this.stockTypeText != null && this.stockTypeText != undefined && this.stockTypeText != '')) {
      this.spin.show();
      this.getStockReportData();
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
      this.isValid = true;
    }
    else {
      this.toastr.error("Please fill the required fields. Required fields are Warehouse No, Stock Type", "", {
        timeOut: 2000,
        progressBar: false,
      });

      this.isValid = false;
    }
  }

  filterResult: any[] = [];


  getStockReportData() {
    this.filterResult = []
    let selectedWarehouseList: any[] = [];
    // this.selectedItems.forEach(element => {
    //   selectedWarehouseList.push(element.item_id);
    // });
    this.reportService.GetallDataStockReport([this.itemCode], this.itemText, this.stockTypeText, this.selectedItems).subscribe(
      result => {
        result.forEach(element => {
          if(element.onHandQty < 0){
            element.onHandQty = 0;
          }
        });
        result.forEach(element => {
          if(element.availableQty < 0){
            element.availableQty = 0;
          }
        });

        result.forEach(element => {
          if(element.availableQty != 0){
           this.filterResult.push(element)
          }
        });
        console.log(this.filterResult)
        this.filterResult = this.cs.removeDuplicatesFromArrayList(this.filterResult, 'itemCode')
        console.log(this.filterResult)
        this.spin.hide();
        this.dataSource = new MatTableDataSource(this.filterResult);
        this.totalRecords = result.totalElements;
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }

  pageHandler($event: PageEvent) {
    this.pageNumber = $event.pageIndex;
    this.pageSize = $event.pageSize;
    this.filtersearch();
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
  checkboxLabel(row?: variant): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseno + 1}`;
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


  downloadexcel() {
    // if (excel)
    var res: any = [];
    console.log(this.dataSource.data)
    this.dataSource.data.forEach(x => {
      res.push({
        "Warehouse No" : x.warehouseId,
        "Product Code  ": x.itemCode,
        'Manufacturer SKU': x.manufacturerSKU,
        'Item Text': x.itemText,
        "On Hand Qty": x.onHandQty,
        "Damage Qty": x.damageQty,
        "Hold Qty": x.holdQty,
        "Available Qty": x.availableQty,
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    res.push({
      "Warehouse No" : '',
      "Product Code  ": '',
      'Manufacturer SKU': '',
      'Item Text': '',
      "On Hand Qty": this.getOnhandQty(),
      "Damage Qty": this.getDamageQty(),
      "Hold Qty": this.getHoldQty(),
      "Available Qty": this.getavailableQty(),
      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.createdOn),
    });

    this.cs.exportAsExcel(res, "Stock Report By Item");
  }

  clearselection(row: any) {

    this.selection.clear();
    this.selection.toggle(row);
  }

  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
    })
      .subscribe(({ warehouse }) => {
        if(this.auth.userTypeId != 3){
          this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        }else{
          this.warehouseList = warehouse
        }
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
        console.log(this.multiselectWarehouseList)
        this.multiselectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.value == this.auth.warehouseId)
            this.selectedItems = [warehouse.value];
        })
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }

  getOnhandQty(){
    let total = 0;
    this.dataSource.data.forEach(element => {
      total = total + (element.onHandQty != null ? element.onHandQty : 0);
    })
    return (Math.round(total * 100) / 100);
  }
  getDamageQty(){
    let total = 0;
    this.dataSource.data.forEach(element => {
      total = total + (element.damageQty != null ? element.damageQty : 0);
    })
    return (Math.round(total * 100) / 100);
  }

  getHoldQty(){
    let total = 0;
    this.dataSource.data.forEach(element => {
      total = total + (element.holdQty != null ? element.holdQty : 0);
    })
    return (Math.round(total * 100) / 100);
  }
  getavailableQty(){
    let total = 0;
    this.dataSource.data.forEach(element => {
      total = total + (element.availableQty != null ? element.availableQty : 0);
    })
    return (Math.round(total * 100) / 100);
  }
}