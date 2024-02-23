import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ContainerReceiptService } from '../../Inbound/Container-receipt/container-receipt.service';
import { PickingService } from '../../Outbound/picking/picking.service';
import { ReportsService } from '../reports.service';
import { StocksampleService } from '../stocksamplereport/stocksample.service';

@Component({
  selector: 'app-grline',
  templateUrl: './grline.component.html',
  styleUrls: ['./grline.component.scss']
})
export class GrlineComponent implements OnInit {
  screenid=3221;
  binner: any[] = [];
  selectedbinner: any[] = [];
  @ViewChild('binnerTag') binnerTag: Table | any;
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;
  selectedCompany:any[]=[];


  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  showFiller = false;

  // displayedColumns: string[] = [ 'statusId','itemCode','proposedStorageBin', 'confirmedStorageBin', 'packBarcodes', 'putAwayQuantity', 'putawayConfirmedQty', 'confirmedBy','confirmedOn','createdOn', 'leadTime'];
  //wms demo
  displayedColumns: string[] = ['statusId', 'itemCode', 'proposedStorageBin', 'confirmedStorageBin', 'packBarcodes', 'putAwayQuantity', 'putawayConfirmedQty', 'confirmedBy', 'confirmedOn',];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];


  constructor(public dialog: MatDialog,
    private cas: CommonApiService,
    private service: StocksampleService,
    private http: HttpClient,
    // private cas: CommonApiService,
    private fb: FormBuilder,
    public toastr: ToastrService,
    private router: Router,
    private spin: NgxSpinnerService,
    public cs: CommonService, private reportService: ReportsService,
    private pickingService: PickingService,
    public auth: AuthService,
    private containerservice: ContainerReceiptService,
    private masterService: MasterService,) {
      this.multistatusList = [
        //  {value: 48, label: '48 - Picking'},
          {value: 70, label: 'Stock Count Creation'},
          {value: 72, label: 'Stock Count user assigned'},
          {value: 74, label: 'Counted'},
          {value:75,label:'Recounted'},
          {value:78,label:'Stock Count Confirmed'},
      ];
     }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;
  selectedplant:any[]=[];
  selectedwarehouse:any[]=[];
  ngOnInit(): void {
    this.getDropdown();
   // this.getDropdownLINE();
    this.dropdownfill();
    this.selectedCompany=this.auth.companyIdAndDescription;
    this.selectedplant=this.auth.plantIdAndDescription;
    this.selectedwarehouse=this.auth.warehouseIdAndDescription;
  
  }

  multimfrlist: any[] = [];
  multistatusList:any[]=[];
  multirefdocList:any[]=[];
  multiamscycleList:any[]=[];
  getDropdown() {
    this.sub.add(this.reportService.getgrlinev2({ warehouseId: [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId] }).subscribe(res => {
      res.forEach((x: any) => this.multimfrlist.push({ value: x.manufacturerName, label: x.manufacturerName }));
    
      res.forEach((x: any) => this.multistatusList.push({ value: x.statusId, label: x.statusDescription }));
      res.forEach((x: any) => this.multirefdocList.push({ value: x.refDocNumber, label: x.refDocNumber }));
      this.multimfrlist=this.cs.removeDuplicatesFromArrayNewstatus(this.multimfrlist);
      this.multistatusList=this.cs.removeDuplicatesFromArrayNewstatus(this.multistatusList);
      this.multirefdocList=this.cs.removeDuplicatesFromArrayNewstatus(this.multirefdocList);
    }))
   
  }
 

  warehouseList: any[] = [];
  selectedWarehouseList: any[] = [];
  selectedItems: any[] = [];
  multiselectWarehouseList: any[] = [];
  multiWarehouseList: any[] = [];
  CustomeridList: any[] = [];
  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ warehouse }) => {
        if (this.auth.userTypeId != 3) {
          this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        } else {
          this.warehouseList = warehouse
        }
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
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
            this.itemCodeList.forEach(x => this.multiselectItemCodeList.push({ value: x.itemCode, label: x.itemCode + ' - ' + x.manufacturerName + ' - ' + x.description}))
          }
        });
    }
  }

  multiselectStorageList: any[] = [];
  storageBinList1: any[] = [];
  selectedStorageBin: any[] = [];

  searhform = this.fb.group({
    barcodeId: [],
  brand: [],
  caseCode: [],
  companyCodeId: [[this.auth.companyId]],
  interimStorageBin: [],
  itemCode: [],
  itemCodeFE:[],
  languageId: [[this.auth.languageId]],
  lineNo: [],
  manufacturerCode: [],
  manufacturerName: [],
  origin: [],
  packBarcodes: [],
  plantId: [[this.auth.plantId]],
  preInboundNo: [],
  refDocNumber: [],
  rejectReason: [],
  rejectType: [],
  statusId: [],
  startCreatedOn:[],
  endCreatedOn:[],
  });
 
  onStorageType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        storageList: this.reportService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ storageList }) => {
          if (storageList != null && storageList.length > 0) {
            this.multiselectStorageList = [];
            this.storageBinList1 = storageList;
            this.storageBinList1.forEach(x => this.multiselectStorageList.push({ value: x.storageBin, label: x.storageBin }))
          }
        });
    }
  }
  filtersearch() {
    this.spin.show();
    let obj: any = {};
    this.searhform.controls.startCreatedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.startCreatedOn.value));
    this.searhform.controls.endCreatedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.endCreatedOn.value));
    //    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    // obj.toPickConfirmedOn = this.toPickConfirmedOn
    // obj.fromPickConfirmedOn = this.fromPickConfirmedOn
    obj.warehouseId = [this.auth.warehouseId];
    obj.companyCode = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
   ;
   
    this.sub.add(this.reportService.getgrlinev2(this.searhform.getRawValue()).subscribe(res => {
      this.binner = res;

      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
      this.spin.hide();
      //this.totalRecords = this.dataSource.data.length
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
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




  totalRecords = 0;
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.binner.forEach(x => {
      res.push({
  
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Status":x.statusDescription,
        "Mfr Name":x.manufacturerName,
        "Part No": x.itemCode,
        "Description":x.itemDescription,
        "Assigned User Id":x.assignedUserId,
        "Order No":x.refDocNumber,
        "Order Type":x.referenceDocumentType,
        "Barcode":x.barcodeId,
        "Inventory Qty":x.inventoryQuantity,
        "Released Qty":x.acceptedQty,
        "Damaged Qty":x.damageQty,
        "Remaining Qty":x.remainingQty,
        "Quantity Type":x.quantityType,
        "Created On":this.cs.dateExcel(x.createdOn),
        "Company":x.companyDescription,
        
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "GR Release Report");
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








  // getBillableAmount() {
  //   let total = 0;
  //   this.dataSource.data.forEach(element => {
  //     total = total + (element.s != null ? element.s : 0);
  //   })
  //   return (Math.round(total * 100) / 100);
  // }

  getToQtyQty() {
    let total = 0;
    this.binner.forEach(x => {
      total = total + (x.putAwayQuantity != null ? x.putAwayQuantity : 0)
    })
    return Math.round(total * 100 / 100)
  }
  getBinnerQty() {
    let total = 0;
    this.binner.forEach(x => {
      total = total + (x.putawayConfirmedQty != null ? x.putawayConfirmedQty : 0)
    })
    return Math.round(total * 100 / 100)
  }

  reset() {
  this.searhform.reset();
  this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
this.searhform.controls.companyCodeId.patchValue([this.auth.companyId]);
this.searhform.controls.plantId.patchValue([this.auth.plantId]);
this.searhform.controls.languageId.patchValue([this.auth.languageId]);
  }
  
}

