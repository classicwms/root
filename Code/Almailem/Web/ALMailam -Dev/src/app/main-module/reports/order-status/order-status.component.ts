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
import { Table } from 'primeng/table';
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
screenid=3168;
inbound: any[] = [];
selectedinbound : any[] = [];
@ViewChild('inboundTag') inboundTag: Table | any;
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
  // applyFilter(event: Event) {
  //   const filterValue = (event.target as HTMLInputElement).value;
  //   this.dataSource.filter = filterValue.trim().toLowerCase();

  //   if (this.dataSource.paginator) {
  //     this.dataSource.paginator.firstPage();
  //   }
  // }
  selectedCompany:any[]=[];
  selectedplant:any[]=[];
  selectedwarehouse:any[]=[];
  ngOnInit(): void {
    // this.auth.isuserdata();
    this.getDropdown();
    this.selectedCompany=this.auth.companyIdAndDescription;
    this.selectedplant=this.auth.plantIdAndDescription;
    this.selectedwarehouse=this.auth.warehouseIdAndDescription;
  
  }

 


  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
 





  filtersearch() {
  
   
      this.spin.show();
      this.getOrderStatusData();
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
    
  

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
    this.itemCode='';
  }
  itemCode:any;
  customerCode: '';
  orderNumber: '';
  statusId:any;
  getOrderStatusData() {
  


    let obj: any = {};

    


    obj.warehouseId   = [this.auth.warehouseId];
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.statusId=this.statusId;
    if(this.itemCode == null){
      obj.itemCode=this.itemCode
    }
    else{
    obj.itemCode=[this.itemCode];
    }
    obj.fromDeliveryDate  = this.cs.dateNewFormat1(this.fromCreatedOn);
    obj.toDeliveryDate   = this.cs.dateNewFormat1(this.toCreatedOn);

    console.log(obj);
    this.reportService.getStockMovementDetailsNew2(obj).subscribe(
      result => {
        console.log(result);

        result.forEach(element => {
          if(element.statusId == "IN PROGRESS" || element.statusId == "NOT FULFILLED"){
            element.deliveredQty = 0;
          }
          

        });
        this.inbound = result;
       // this.dataSource.filterPredicate = this.createFilter();
       
        this.totalRecords = result.length;
        this.spin.hide();
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }

  getDropdown() {
    this.sub.add(this.reportService.getStockMovementDetailsNew2({ warehouseId: [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId] }).subscribe(res => {
      res.forEach((x: any) => this.multiOrderTypeList.push({ value: x.referenceDocumentType, label: x.referenceDocumentType }));
    
      res.forEach((x: any) => this.multiStatusList.push({ value: x.statusId, label: x.statusDescription }));
      this.multiOrderTypeList=this.cs.removeDuplicatesFromArrayNewstatus(this.multiOrderTypeList);
      this.multiStatusList=this.cs.removeDuplicatesFromArrayNewstatus(this.multiStatusList);
     
    }))
   
  }




  // /** Whether the number of selected elements matches the total number of rows. */
  // isAllSelected() {
  //   const numSelected = this.selection.selected.length;
  //   const numRows = this.dataSource.data.length;
  //   return numSelected === numRows;
  // }

  // /** Selects all rows if they are not all selected; otherwise clear selection. */
  // masterToggle() {
  //   if (this.isAllSelected()) {
  //     this.selection.clear();
  //     return;
  //   }

  //   this.selection.select(...this.dataSource.data);
  // }

  // /** The label for the checkbox on the passed row */
  // checkboxLabel(row?: any): string {
  //   if (!row) {
  //     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
  //   }
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseno + 1}`;
  // }



  // clearselection(row: any) {

  //   this.selection.clear();
  //   this.selection.toggle(row);
  // }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }
  multiselectItemCodeList: any[] = [];
    itemCodeList: any[] = [];
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
  onWarehouseSelect(items: any) {
    if (items != null && items != undefined && items.length > 0)
      this.outboundParam.warehouseId = items[0].item_id;

    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
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
    this.inbound.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Plant":x.plantDescription,
        'Warehouse': x.warehouseDescription,
        'Order No': x.refDocNumber,
        "Part No": x.sku,
        "Description": x.skuDescription,
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

  // filterSearch(event){
  //   this.filterValues.soNumber = event;
  //   this.dataSource.filter = JSON.stringify(this.filterValues);
  // }

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
    this.inbound.forEach(x =>{
      total = total + (x.orderQty != null ? x.orderQty : 0)
    })
    return Math.round(total *100 / 100)
  }
  getDeliveryQty(){
    let total = 0;
    this.inbound.forEach(x =>{
      total = total + (x.deliveryQty != null ? x.deliveryQty : 0)
    })
    return Math.round(total *100 / 100)
  }
}