import { SelectionModel } from "@angular/cdk/collections";
import { DatePipe } from '@angular/common';
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
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { MasterService } from "src/app/shared/master.service";
import { PutawayService } from "../../Inbound/putaway/putaway.service";
import { PickingService } from "../../Outbound/picking/picking.service";
import { ReportsService } from "../reports.service";
import { PickingDetailsComponent } from "./picking-details/picking-details.component";
import { PutawayDeatilsComponent } from "./putaway-deatils/putaway-deatils.component";
import { Table } from "primeng/table";
import { CommonApiService } from "src/app/common-service/common-api.service";
import { FormBuilder } from "@angular/forms";

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
  selector: 'app-new-stock-movement',
  templateUrl: './new-stock-movement.component.html',
  styleUrls: ['./new-stock-movement.component.scss']
})
export class NewStockMovementComponent implements OnInit {



  screenid=3170;
  transfer: any[] = [];
  selectedtransfer : any[] = [];
  @ViewChild('transferTag') transferTag: Table | any;


  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;

  warehouseList: any[] = [];
  multiselectWarehouseList: any[] = [];
  multiWarehouseList: any[] = [];

  selectedItems: any[] = [];

  itemCode = '';
  selectedWarehouse = '';
  fromCreatedOn = '';
  toCreatedOn = new Date();
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
    private reportService: ReportsService, public datepipe: DatePipe, public auth: AuthService, public cs: CommonService, private putservice: PutawayService, public dialog: MatDialog,
    private pickingservice: PickingService,private cas: CommonApiService,  private fb: FormBuilder,) { }
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
  ngOnInit(): void {
    // this.auth.isuserdata();
    this.dropdownfill();
    this.selectedCompany=this.auth.companyId;
    this.selectedplant=this.auth.plantId;
    this.searhform.controls.toCreatedOn.disable();
    this.searhform.controls.itemCode.patchValue([this.searhform.controls.itemCodeFE.value]);

  }
  searhform = this.fb.group({
    companyCodeId:[[this.auth.companyId],],
    plantId:[[this.auth.plantId],],
    languageId:[[this.auth.languageId],],
    warehouseId:[[this.auth.warehouseId],],
    itemCode: [],
    itemCodeFE:[],
    fromCreatedOn:[],
    fromDeliveryDate:[],
    toDeliveryDate:[],
    toCreatedOn:[new Date()],
  });
  reset(){
    this.searhform.reset();
    this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
    this.searhform.controls.companyCodeId.patchValue([this.auth.companyId]);
    this.searhform.controls.plantId.patchValue([this.auth.plantId]);
    this.searhform.controls.languageId.patchValue([this.auth.languageId]);
    this.searhform.controls.toCreatedOn.patchValue(new Date())
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

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
  
 






  filtersearch() {
    if ((this.searhform.controls.itemCodeFE.value !== null && this.searhform.controls.fromCreatedOn.value !== null)&&(this.searhform.controls.itemCodeFE.value !== null || this.searhform.controls.fromCreatedOn.value !== null)) {
      this.spin.show();
      this.getStockMovementData();
      this.table = true;
      this.search = false;
      this.fullscreen = true;
      this.back = false;
    }
    if((this.searhform.controls.itemCodeFE.value == null )) {
      this.toastr.error("Please fill the required fields. Required fields are  Part No To continue", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
  
  if((this.searhform.controls.fromCreatedOn.value == null )) {
    this.toastr.error("Please fill the required fields. Required fields are  From Date To continue", "", {
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
  // checkboxLabel(row?: variant): string {
  //   if (!row) {
  //     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
  //   }
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseno + 1}`;
  // }



  // clearselection(row: any) {

  //   this.selection.clear();
  //   this.selection.toggle(row);
  // }

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
        this.multiselectWarehouseList=this.cas.removeDuplicatesFromArray(this.multiselectWarehouseList);
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }
 
  getStockMovementData() {
    // if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0)
    //   this.selectedWarehouse = this.selectedItems;

    if (this.fromCreatedOn != '') {
      let from = new Date(this.fromCreatedOn);
      let transformedFrom = this.datepipe.transform(from, 'yyyy-MM-dd');
      this.sendingFromCreatedOn = (transformedFrom == undefined ? '' : transformedFrom.toString());
    }
    const today = new Date();

this.searhform.controls.itemCode.patchValue([this.searhform.controls.itemCodeFE.value]);
this.searhform.controls.fromDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.fromCreatedOn.value));
this.searhform.controls.toDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.toCreatedOn.value));
    let obj: any = {};
    obj.warehouseId = this.selectedItems;
    this.searhform.controls.toCreatedOn.patchValue(today);
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];

    obj.itemCode = [this.itemCode];
  //  / obj.warehouseId = [this.selectedWarehouse];
    obj.fromDeliveryDate = this.cs.day_callapiSearch(this.sendingFromCreatedOn);
    obj.toDeliveryDate = this.cs.day_callapiSearch(this.sendingToCreatedOn);
    this.reportService.getStockMovementDetailsNewV2(this.searhform.getRawValue()).subscribe(
      result => {
        this.spin.hide();
        let obj: any = {};
        obj.itemCode = [this.searhform.controls.itemCodeFE.value];
        obj.warehouseId = this.selectedItems;
        if(result){
          obj.manufacturerName = [result[0].manufacturerSKU];
        }
        //obj.binClassId = [1];
        obj.companyCodeId = [this.auth.companyId];
        obj.languageId = [this.auth.languageId];
        obj.plantId = [this.auth.plantId];

        let inventoryQuantity = 0;
        this.reportService.findInventory2(obj).subscribe(
          inventoryResult => {
            inventoryResult.forEach(element => {
              inventoryQuantity = inventoryQuantity + (element.inventoryQuantity != null ? element.inventoryQuantity : 0) + (element.allocatedQuantity != null ? element.allocatedQuantity : 0);
            });
            if (result != null && result.length > 0) {
              //last row data bind 
              let i = result.length;
              result.forEach(element => {
                if (i >= 0) {
                  if (i == result.length) {
                    result[i - 1]['balanceOHQty'] = inventoryQuantity;
                  } else {
                    result[i - 1]['balanceOHQty'] = result[i]['openingStock'];
                  }
                  if (result[i - 1]['documentType'] == 'OutBound') {
                    result[i - 1]['openingStock'] = result[i - 1]['balanceOHQty'] + result[i - 1]['movementQty'];
                  }
                  else {
                    result[i - 1]['openingStock'] = result[i - 1]['balanceOHQty'] - result[i - 1]['movementQty'];
                  }
                  i--;
                
                }
              });
            } else {
              if (inventoryResult != null && inventoryResult.length > 0) {
                let obj: any = {};
                obj.warehouseId = inventoryResult[0].warehouseId;
                obj.manufacturerSKU = inventoryResult[0].referenceField9;
                obj.itemCode = inventoryResult[0].itemCode;
                obj.itemText = inventoryResult[0].description;
                obj.documentType = null;
                obj.documentNumber = null;
                obj.openingStock = inventoryQuantity;
                obj.newmMovementQty = 0;
                obj.balanceOHQty = inventoryQuantity;
                obj.customerCode = null;
                obj.confirmedOn = null;
                obj.companyDescription=inventoryResult[0].companyDescription,
                obj.plantDescription=inventoryResult[0].plantDescription,
                obj.warehouseDescription=inventoryResult[0].warehouseDescription,
                obj.manufacturerName=inventoryResult[0].manufacturerName,
                
                result.push(obj);
              }
            }
            this.transfer = result;

            this.transfer.forEach((x) => {
              if (x.documentType == "OutBound") {
                x['newmMovementQty'] = '-' + (x.movementQty)
              }
              if (x.documentType == "InBound") {
                x['newmMovementQty'] = '+' + (x.movementQty)
              }
            })
           
          });
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


  putaway(data): void {
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.itemCode = [data.itemCode];
    obj.refDocNumber = [data.documentNumber];

    this.sub.add(this.putservice.searchLinev2(obj).subscribe(res => {
      this.spin.hide();
      let putAwayLineArray: any[] = [];
      let result = res.filter((x: any) => x.deletionIndicator == 0);
      // if (result) {
      //   result.forEach(element => {
      //     if (element.putawayConfirmedQty != null && element.putawayConfirmedQty > 0) {
      //       putAwayLineArray.push(element);
      //     }
      //   });
      // }
      putAwayLineArray.push(result);
      const dialogRef = this.dialog.open(PutawayDeatilsComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: putAwayLineArray
      });

      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });

      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }
  picking(data): void {
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.itemCode = [data.itemCode];
    obj.refDocNumber = [data.documentNumber];

    this.sub.add(this.pickingservice.getpickinglinev2(obj).subscribe(res => {
      this.spin.hide();
      let pickingLineArray: any[] = [];
      let result = res.filter((x: any) => x.deletionIndicator == 0);
      // if (result) {
      //   result.forEach(element => {
      //     if (element.putawayConfirmedQty != null && element.putawayConfirmedQty > 0) {
      //       putAwayLineArray.push(element);
      //     }
      //   });
      // }
      pickingLineArray.push(result);
      const dialogRef = this.dialog.open(PickingDetailsComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: pickingLineArray
      });

      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });

      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.transfer.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        'MFR Name': x.manufacturerSKU,
        'Part No': x.itemCode,
        " Description": x.itemText,
        "Document Type": x.documentType,
        "Document Number": x.documentNumber,
        "Opening Qty": x.openingStock ,
        'Movement Quantity': x.newmMovementQty,
        'Balance OH Qty': x.balanceOHQty,
        'Date': this.cs.dateMMYY(x.confirmedOn),
        'Time': this.cs.dateHHSS(x.confirmedOn),
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Stock Movement Report");
  }


 
  
}