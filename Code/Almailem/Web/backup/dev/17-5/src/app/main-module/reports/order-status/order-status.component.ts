import { SelectionModel } from '@angular/cdk/collections';
import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { DeliveryService } from '../../Outbound/delivery/delivery.service';
import { ReportsService } from '../reports.service';
// export interface any {


//   no: string;
//   actions: string;
//   status: string;
//   warehouseno: string;
//   preinboundno: string;
//   countno: string;
//   by: string;
//   damage: string;
//   available: string;
//   hold: string;
//   stype: string;
//   balance: string;
//   opening: string;
//   time: string;
//   perct: string;
//   status1: string;
// }

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

// const ELEMENT_DATA: any[] = [
//   { no: "1", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "2", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "3", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "4", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "5", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "6", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "7", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "8", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "9", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "10", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "11", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },
//   { no: "12", warehouseno: 'readonly', by: 'readonly', countno: 'readonly', perct: 'readonly', status1: 'readonly', stype: 'readonly', opening: 'readonly', balance: 'readonly', time: 'readonly', damage: 'readonly', hold: 'readonly', available: 'readonly', preinboundno: 'readonly', status: 'readonly', actions: 's' },

// ];
@Component({
  selector: 'app-order-status',
  templateUrl: './order-status.component.html',
  styleUrls: ['./order-status.component.scss']
})
export class OrderStatusComponent implements OnInit {


  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;
  selectedItems: any[] = [];
  selectedItems2: SelectItem[] = [];
  selectedItems3: SelectItem[] = [];
  selectedItems4: SelectItem[] = [];
  selectedWarehouse = '';

  selectedOrderTypeList: any[] = [];
  selectedStatusIdList: any[] = [];
  selectedCustomerCodeList: any[] = [];

  sendingOrderTypeList: any[] = [];
  sendingStatusIdList: any[] = [];
  sendingCustomerCodeList: any[] = [];

  warehouseList: any[] = [];
  multiWarehouseList: any[] = [];
  multiOrderTypeList: any[] = [];
  multiStatusList: any[] = [];
  multiCustomerCodeList: SelectItem[] = [];

  outboundParam = new OutboundCls();


  fromCreatedOn = '';
  toCreatedOn = '';
  sendingFromCreatedOn = '';
  sendingToCreatedOn = '';

  multiselectWarehouseList: SelectItem[] = [];
  multiselectOrderTypeList: SelectItem[] = [];
  multiselectStatusList: SelectItem[] = [];
  multiselectCustomerCodeList: SelectItem[] = [];

  dropdownSettings: IDropdownSettings = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  dropdownSettings2: IDropdownSettings = {
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
  displayedColumns: string[] = ['warehouseno', 'by', 'status', 'damage', 'hold', 'available', 'orderType','orderedQty', 'deliveredQty', 'orderReceivedDate', 'deliveryConfirmedOn', 'expectedDeliveryDate', 'percentageOfDelivered', 'statusId',];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];
  constructor(
    private router: Router, public toastr: ToastrService, private spin: NgxSpinnerService, private reportService: ReportsService,
    public datepipe: DatePipe, private masterService: MasterService, private deliveryService: DeliveryService, private auth: AuthService, public cs: CommonService,) { }
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

  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
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
      (this.fromCreatedOn != null && this.fromCreatedOn != undefined && this.fromCreatedOn != '') &&
      (this.toCreatedOn != null && this.toCreatedOn != undefined && this.toCreatedOn != '')) {
      this.spin.show();
      this.getOrderStatusData();
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
    }
    else {
      this.toastr.error("Please fill the required fields. Required fields are Warehouse No, From and To Date", "", {
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

  totalRecords = 0;

  reset(){
    this.selectedItems4=[];
    this.selectedItems2=[];
    this.selectedItems3=[];
    this.fromCreatedOn='';
    this.toCreatedOn='';
  }

  customerCode: '';
  orderNumber: '';
  getOrderStatusData() {
    if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0)
      this.selectedWarehouse = this.selectedItems[0];

    // let from = new Date(this.fromCreatedOn);
    // let transformedFrom = this.datepipe.transform(from, 'MM-dd-yyyy');
    // this.sendingFromCreatedOn = (transformedFrom == undefined ? '' : transformedFrom.toString());
  
    // let to = new Date(this.toCreatedOn);
    // let transformedTo = this.datepipe.transform(to, 'MM-dd-yyyy');
    // this.sendingToCreatedOn = (transformedTo == undefined ? '' : transformedTo.toString());

    // // this.selectedOrderTypeList = [];
    // // this.selectedItems2.forEach(element => {
    // //   this.selectedOrderTypeList.push(element.item_id);
    // // });
    // // this.sendingOrderTypeList = this.selectedOrderTypeList;

    // this.selectedStatusIdList = [];
    // this.selectedItems3.forEach(element => {
    //   if (element.item_id == 'Delivered') {
    //     this.selectedStatusIdList.push(59);
    //   }
    //   else if (element.item_id == 'Partial') {
    //     this.selectedStatusIdList.push(42);
    //     this.selectedStatusIdList.push(43);
    //     this.selectedStatusIdList.push(48);
    //     this.selectedStatusIdList.push(50);
    //     this.selectedStatusIdList.push(55);
    //   }
    //   else if (element.item_id == 'Unfulfilled') {
    //     this.selectedStatusIdList.push(51);
    //     this.selectedStatusIdList.push(47);
    //   }

    // });

    // this.sendingStatusIdList = this.selectedStatusIdList;

    // this.selectedCustomerCodeList = [];
    // this.selectedItems4.forEach(element => {
    //   this.selectedCustomerCodeList.push(element.item_id);
    // });

    // this.sendingCustomerCodeList = this.selectedCustomerCodeList;

    let obj: any = {};

      obj.customerCode = [];
      this.selectedItems4.forEach(element => {
        obj.customerCode.push(element.item_id);
      });
     
      obj.orderType = [];
      this.selectedItems2.forEach(element => {
        obj.orderType.push(element.item_id);
      });
      

        obj.statusId = [];

        
    this.selectedItems3.forEach(element => {
      if (element.item_id == 'Delivered') {
        obj.statusId.push(59);
      }
      else if (element.item_id == 'Partial') {
        obj.statusId.push(42);
        obj.statusId.push(43);
        obj.statusId.push(48);
        obj.statusId.push(50);
        obj.statusId.push(55);
      }
      else if (element.item_id == 'Unfulfilled') {
        obj.statusId.push(51);
        obj.statusId.push(47);
      }

    });


    obj.warehouseId   = this.selectedWarehouse;
    obj.fromDeliveryDate  = this.cs.dateNewFormat1(this.fromCreatedOn);
    obj.toDeliveryDate   = this.cs.dateNewFormat1(this.toCreatedOn);

    console.log(obj);
    this.reportService.getOrderStatusDetailsNew(obj).subscribe(
      result => {
        console.log(result);

        result.forEach(element => {
          if(element.statusId == "IN PROGRESS" || element.statusId == "NOT FULFILLED"){
            element.deliveredQty = 0;
          }
          element.percentageOfDelivered = (element.deliveredQty / element.orderedQty)*100;

        });
        this.dataSource = new MatTableDataSource(result);
       // this.dataSource.filterPredicate = this.createFilter();
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.totalRecords = result.length;
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
        this.onWarehouseSelect(this.selectedItems);

        this.multiOrderTypeList.push({ item_id: 'N', item_text: 'Normal' }, { item_id: 'S', item_text: 'Special' });
        this.multiselectOrderTypeList = this.multiOrderTypeList;

        this.multiStatusList.push({ item_id: 'Delivered', item_text: 'Delivered' }, { item_id: 'Partial', item_text: 'Partial Deliveries' }, { item_id: 'Unfulfilled', item_text: 'Not fulfilled' });
        this.multiselectStatusList = this.multiStatusList;

      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

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

  onWarehouseSelect(items: any) {
    if (items != null && items != undefined && items.length > 0)
      this.outboundParam.warehouseId = items[0].item_id;

    let obj: any = {};
    obj.warehouseId = [items[0].item_id];
    obj.businessPartnerType = [2]

    this.deliveryService.searchBusinessPartner(obj).subscribe(
      result => {
        this.multiCustomerCodeList = [];
        result.forEach(element => {
          if (this.multiCustomerCodeList.filter(x => x.item_id == element.partnerCode).length == 0)
            this.multiCustomerCodeList.push({ item_id: element.partnerCode, item_text: element.partnerCode })
        });
        this.multiselectCustomerCodeList = this.multiCustomerCodeList;
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Warehouse No": x.warehouseId,
        'SO Number': x.soNumber,
        'Customer Code': x.customerCode,
        "Customer Name": x.customerName,
        "SKU": x.sku,
        "SKU Description": x.skuDescription,
        "Order Type" : x.orderType,
        " Ordered Qty": x.orderedQty,
        'Delivered Qty': x.deliveredQty,
        'Order Rec Date': this.cs.dateapi(x.orderReceivedDate),
        'Delivered Date': this.cs.dateapi(x.deliveryConfirmedOn),
        'Exp Delivery Date': this.cs.dateapi(x.expectedDeliveryDate),
        '% of Delivered': x.percentageOfDelivered,
        'Status': x.statusId,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Order Status Report");
  }


  searchedWarehouse: any;

  filterValues = {
    soNumber: ''
  };

  filterSearch(event){
    this.filterValues.soNumber = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }

  createFilter(): (data: any, filter: string) => boolean {
    let filterFunction = function (data, filter): boolean {
      let searchTerms = JSON.parse(filter);
      return (
        data.soNumber.toLowerCase().indexOf(searchTerms.soNumber) !== -1
      );
    };
    return filterFunction;
  }

  getOrderQty(){
    let total = 0;
    this.dataSource.data.forEach(x =>{
      total = total + (x.orderedQty != null ? x.orderedQty : 0)
    })
    return Math.round(total *100 / 100)
  }
  getDeliveryQty(){
    let total = 0;
    this.dataSource.data.forEach(x =>{
      total = total + (x.deliveredQty != null ? x.deliveredQty : 0)
    })
    return Math.round(total *100 / 100)
  }
}