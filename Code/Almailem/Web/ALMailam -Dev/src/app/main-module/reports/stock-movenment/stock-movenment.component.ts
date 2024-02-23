import { SelectionModel } from "@angular/cdk/collections";
import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
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
import { CommonApiService } from "src/app/common-service/common-api.service";

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
  stype: string;
  balance: string;
  opening: string;
  time: string;
}

interface SelectItem {
  item_id: string;
  item_text: string;
}

const ELEMENT_DATA: variant[] = [
  { no: "1", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "2", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "3", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "4", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "5", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "6", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "7", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "8", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "9", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "10", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "11", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
  { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },

];
@Component({
  selector: 'app-stock-movenment',
  templateUrl: './stock-movenment.component.html',
  styleUrls: ['./stock-movenment.component.scss']
})
export class StockMovenmentComponent implements OnInit {
screenid=3170;
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;

  warehouseList: any[] = [];
  multiselectWarehouseList: SelectItem[] = [];
  multiWarehouseList: SelectItem[] = [];

  selectedItems: SelectItem[] = [];

  itemCode = '';
  selectedWarehouse = '';
  fromCreatedOn = '';
  toCreatedOn = '';
  sendingFromCreatedOn = '';
  sendingToCreatedOn = '';

  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  div1Function() {
    this.table = !this.table;
  }
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
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
  showFiller = false;
  displayedColumns: string[] = ['warehouseno', 'countno', 'by', 'preinboundno', 'damage', 'hold', 'opening', 'moveQty', 'balance', 'available', 'stype', 'time'];
  sub = new Subscription();
  ELEMENT_DATA: variant[] = [];
  constructor(
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService, private masterService: MasterService,
    private reportService: ReportsService, public datepipe: DatePipe, private auth: AuthService, public cs: CommonService,  private cas: CommonApiService,) { }
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

  dataSource = new MatTableDataSource<any>();
  selection = new SelectionModel<any>(true, []);


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





  filtersearch() {
    if ((this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) &&
      (this.itemCode != null && this.itemCode != undefined && this.itemCode.trim() != '')) {
      this.spin.show();
      this.getStockMovementData();
      this.table = true;
      this.search = true;
      this.fullscreen = true;
      this.back = false;
    }
    else {
      this.toastr.error("Please fill the required fields. Required fields are Warehouse No, Part No", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
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



  clearselection(row: any) {

    this.selection.clear();
    this.selection.toggle(row);
  }

  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err)))

    })
      .subscribe(({ warehouse }) => {
        this.warehouseList = warehouse;
        console.log(this.warehouseList);
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ item_id: x.warehouseId, item_text: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
        this.multiselectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.item_id == this.auth.warehouseId)
            this.selectedItems = [warehouse];
        })
       // this.multiWarehouseList=this.cas.removeDuplicatesFromArray(this.multiWarehouseList);
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  getStockMovementData() {
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0)
      this.selectedWarehouse = this.selectedItems[0].item_id;

    if (this.fromCreatedOn != '') {
      let from = new Date(this.fromCreatedOn);
      let transformedFrom = this.datepipe.transform(from, 'MM-dd-yyyy');
      this.sendingFromCreatedOn = (transformedFrom == undefined ? '' : transformedFrom.toString());
    }

    if (this.toCreatedOn != '') {
      let to = new Date(this.toCreatedOn);
      let transformedTo = this.datepipe.transform(to, 'MM-dd-yyyy');
      this.sendingToCreatedOn = (transformedTo == undefined ? '' : transformedTo.toString());
    }

    this.reportService.getStockMovementDetails(this.sendingFromCreatedOn, this.itemCode, this.sendingToCreatedOn, this.selectedWarehouse).subscribe(
      result => {
        this.spin.hide();
        this.dataSource = new MatTableDataSource(result);
        this.dataSource.paginator = this.paginator;

        // let obj: any = {};
        // obj.itemCode = [this.itemCode];
        // obj.warehouseId = [this.selectedWarehouse];
        // let inventoryQuantity = 0;
        // this.reportService.findInventory(obj).subscribe(
        //   inventoryResult => {
        //     inventoryResult.forEach(element => {
        //       inventoryQuantity = inventoryQuantity + (element.inventoryQuantity != null ? element.inventoryQuantity : 0)
        //     });
        //     if (result != null && result.length > 0) {
        //       //last row data bind 
        //       let i = result.length;
        //       result.forEach(element => {
        //         if (i >= 0) {
        //           if (i == result.length) {
        //             result[i - 1]['balanceOHQty'] = inventoryQuantity;
        //           } else {
        //             result[i - 1]['balanceOHQty'] = result[i]['openingStock'];
        //           }
        //           if (result[i - 1]['documentType'] == 'Outbound') {
        //             result[i - 1]['openingStock'] = result[i - 1]['balanceOHQty'] + result[i - 1]['movementQty'];
        //           } else {
        //             result[i - 1]['openingStock'] = result[i - 1]['balanceOHQty'] - result[i - 1]['movementQty'];
        //           }
        //           i--;
        //         }
        //       });
        //     }
        // this.dataSource = new MatTableDataSource(result);
        // this.dataSource.paginator = this.paginator;
        //     });
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Warehouse": x.warehouseId,
        'MFR Part No': x.manufacturerSKU,
        'Part No': x.itemCode,
        "Description": x.itemText,
        "Document Type": x.documentType,
        "Document Number": x.documentNumber,
        "Opening Qty": x.openingStock,
        'Movement Quantity': x.movementQty,
        'Balance OH Qty': x.balanceOHQty,
        'Customer Code': x.customerCode,
        'Date': x.createdOn,
        'Time': x.createdTime,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Stock Movement Report");
  }

}