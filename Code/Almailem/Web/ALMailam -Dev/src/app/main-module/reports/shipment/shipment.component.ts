import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { forkJoin, of, Subscription } from "rxjs";
import { catchError } from "rxjs/operators";
import { MasterService } from "src/app/shared/master.service";
import { DeliveryService } from "../../Outbound/delivery/delivery.service";
import { ReportsService } from "../reports.service";
import { StockListComponent } from "../stock-report/stock-list/stock-list.component";

interface SelectItem {
  item_id: string;
  item_text: string;
}

interface Outbound {
  warehouseId?: string[];
}

export class OutboundCls implements Outbound {
  constructor(public warehouseId?: string[]) {
  }
}


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
  selector: 'app-shipment',
  templateUrl: './shipment.component.html',
  styleUrls: ['./shipment.component.scss']
})
export class ShipmentComponent implements OnInit {

  selectedItems: SelectItem[] = [];
  selectedItems2: SelectItem[] = [];

  selectedWarehouse = '';
  selectedOrderNumber = '';

  warehouseList: any[] = [];
  multiWarehouseList: any[] = [];
  multiselectWarehouseList: any[] = [];

  multiOrderNumberList: any[] = [];
  multiselectOrderNumberList: any[] = [];

  outboundParam = new OutboundCls();

  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;
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
  displayedColumns: string[] = ['warehouseno', 'countno', 'by', 'preinboundno', 'status', 'damage', 'hold', 'available'];
  sub = new Subscription();
  ELEMENT_DATA: variant[] = [];
  constructor(
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService,
    private reportService: ReportsService, private masterService: MasterService, private deliveryService: DeliveryService) { }
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

  dataSource = new MatTableDataSource<variant>(this.ELEMENT_DATA);
  selection = new SelectionModel<variant>(true, []);


  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination





  filtersearch() {
    if ((this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) &&
      (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0)) {
      this.spin.show();
      this.getShipmentData();
      this.table = true;
      this.search = true;
      this.fullscreen = true;
      this.back = false;
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

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

  getShipmentData() {
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0)
      this.selectedWarehouse = this.selectedItems[0].item_id;


    if (this.selectedItems2 != null && this.selectedItems2 != undefined && this.selectedItems2.length > 0)
      this.selectedOrderNumber = this.selectedItems2[0].item_id;
    this.reportService.getShipmentDetails(this.selectedWarehouse, this.selectedOrderNumber).subscribe(
      result => {
        console.log(result);
    //     let blob = new Blob([result], { type: 'application/pdf' });
    //   let pdfUrl = window.URL.createObjectURL(blob);

    //   var PDF_link = document.createElement('a');
    //   PDF_link.href = pdfUrl;
    // //   TO OPEN PDF ON BROWSER IN NEW TAB
    //   window.open(pdfUrl, '_blank');
    //   TO DOWNLOAD PDF TO YOUR COMPUTER
      //PDF_link.download = "ShipmentDelivery.pdf";
      //PDF_link.click();
        // result.forEach(element => {
        //   this.onHandQty += element.onHandQty;
        //   this.damagedQty += element.damageQty;
        //   this.holdQty += element.holdQty;
        //   this.availableQty += element.availableQty;
        // });
        //this.dataSource = new MatTableDataSource(result);
        this.spin.hide();
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }

  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err)))

    })
      .subscribe(({ warehouse }) => {
        this.warehouseList = warehouse;
        console.log(this.warehouseList);
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;



      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  onWarehouseSelect(items: any) {
    console.log(items);
    if (items != null && items != undefined)
      this.outboundParam.warehouseId = [items.item_id];
console.log(this.outboundParam);
    this.deliveryService.search(this.outboundParam).subscribe(
      result => {
        console.log(result);
        this.multiOrderNumberList = [];
        result.forEach(element => {
          this.multiOrderNumberList.push({ item_id: element.refDocNumber, item_text: element.refDocNumber });
        });
        this.multiselectOrderNumberList = this.multiOrderNumberList;
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }
}