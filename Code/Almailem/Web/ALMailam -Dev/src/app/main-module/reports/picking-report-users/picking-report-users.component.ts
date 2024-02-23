import { SelectionModel } from '@angular/cdk/collections';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PickingService } from '../../Outbound/picking/picking.service';
import { ReportsService } from '../reports.service';
import { StocksampleService } from '../stocksamplereport/stocksample.service';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-picking-report-users',
  templateUrl: './picking-report-users.component.html',
  styleUrls: ['./picking-report-users.component.scss']
})
export class PickingReportUsersComponent implements OnInit {
  screenid=3167;
  inbound: any[] = [];
  selectedinbound : any[] = [];
  @ViewChild('inboundTag') inboundTag: Table | any;
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;

  searhform = this.fb.group({
    companyCodeId:[[this.auth.companyId],],
    plantId:[[this.auth.plantId],],
    languageId:[[this.auth.languageId],],
    warehouseId:[[this.auth.warehouseId],],
    itemCode: [],
    itemCodeFE:[],
    refDocNumber: [],
    pickedStorageBin:[],
    storageSectionId:[],
    toPickConfirmedOn:[],
    fromPickConfirmedOn:[],
    statusId:[],
  });

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
    public auth: AuthService) { 

      this.StatusList = [
      //  {value: 48, label: '48 - Picking'},
        {value: 50, label: '50 - Picked'},
        {value: 51, label: '51 -Picker Denial'},
       // {value: 59, label: '59 - Delivered'},
    ];

    }
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
  multiSelectorderList:any[] = []
  ngOnInit(): void {
    // this.spin.show();
    // this.pickingService.search({statusId : [50, 59]}).subscribe(res => {
    //   res.forEach(x => this.multiSelectorderList.push({ value: x.refDocNumber, label: x.refDocNumber}));
    //   this.spin.hide();
    // }, err => {
    //   this.cs.commonerrorNew(err);
    //   this.spin.hide();
    // })
    this.getDropdown();
    this.selectedCompany=this.auth.companyIdAndDescription;
    this.selectedplant=this.auth.plantIdAndDescription;
    this.selectedwarehouse=this.auth.warehouseIdAndDescription;
  }
 
  // ngAfterViewInit() {
  //   this.dataSource.paginator = this.paginator;
  // }
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
            this.multiselectStorageList = [];
            this.storageBinList1 = storageList;
            this.storageBinList1.forEach(x => this.multiselectStorageList.push({ value: x.storageBin, label: x.storageBin}))
          }
        });
    }
  }
  multiOrderNo: any[] = [];
  getDropdown(){
    this.sub.add(this.reportService.pickupSpark({warehouseId : [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId]}).subscribe(res => {
      res.forEach((x: any) => this.multiOrderNo.push({ value: x.refDocNumber, label: x.refDocNumber }));
      this.multiOrderNo=this.cs.removeDuplicatesFromArrayNewstatus(this.multiOrderNo);
    }))
  }
  refDocNumber :'';
  pickedPackCode = '';
  itemCode = '';
  pickedStorageBin: any;
  toPickConfirmedOn = '';
  fromPickConfirmedOn = '';
  statusId: any[] = [50, 59]; 

  reset(){
    this.searhform.reset();
    this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
    this.searhform.controls.companyCodeId.patchValue([this.auth.companyId]);
    this.searhform.controls.plantId.patchValue([this.auth.plantId]);
    this.searhform.controls.languageId.patchValue([this.auth.languageId]);
    this.searhform.controls.statusId.patchValue([50, 59]);
  }
  
  filtersearch(){
    if(this.searhform.controls.itemCodeFE.value != null){
      this.searhform.controls.itemCode.patchValue([this.searhform.controls.itemCodeFE.value]);
    }
    if(this.searhform.controls.itemCodeFE.value == null){
      this.searhform.controls.itemCode.patchValue(this.searhform.controls.itemCodeFE.value);
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

    obj.refDocNumber = this.refDocNumber;

    //obj.toPickConfirmedOn = [];
    // if (this.toPickConfirmedOn != null) {
    //   obj.toPickConfirmedOn.push(this.cs.day_callapi(this.toPickConfirmedOn))
    // }
    this.searhform.controls.fromPickConfirmedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.fromPickConfirmedOn.value));
        this.searhform.controls.toPickConfirmedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.toPickConfirmedOn.value));
    obj.warehouseId = [this.auth.warehouseId]
    obj.companyCode = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
    obj.statusId = this.statusId

    this.sub.add(this.pickingService.pickuplineSpark(this.searhform.value).subscribe(res => {
     
 //     let result = res.filter((x: any) => x.statusId == 50);
      this.inbound =res;
   
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
      this.spin.hide();
      this.totalRecords = this.inbound.length
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
    this.inbound.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Status  ": (x.statusDescription),
        "Order No  ": x.refDocNumber,
        'Pickup No': x.pickupNumber,
        'Line No': x.lineNumber,
        "Mfr Name":x.manufacturerName,
        'Part No': x.itemCode,
        'Description': x.description,
        'Barcode Id': x.partnerItemBarcode,
        'Bin Location': x.pickedStorageBin,
        'Picker': x.assignedPickerId,
        "Store": x.partnerCode,
        "Order Qty":x.allocatedQty,
        "Pick TO Qty": x.pickConfirmQty,
        "Variance":x.varianceQuantity,
        "Assigned On": this.cs.dateapiwithTime(x.pickupCreatedOn),
        "Picked On": this.cs.dateapiwithTime(x.pickupUpdatedOn),

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    res.push({

      "Status  ": '',
      "Order No  ": '',
      'Pickup No': '',
      'Line No': '',
      'Part No': '',
      'Mfr Name': '',
      'Description':'',
      'Barcode Id': '',
      'Bin Location': '',
      'Picker': '',
      "Store": '',
      "Order Qty": this.getOrderQty(),
      "Pick TO Qty": this.getPickingQty(),
      "Variance":'',
      "Assigned On": '',
      "Picked On": '',

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
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseId + 1}`;
  // }



  // clearselection(row: any) {

  //   this.selection.clear();
  //   this.selection.toggle(row);
  // }
  // getBillableAmount() {
  //   let total = 0;
  //   this.dataSource.data.forEach(element => {
  //     total = total + (element.s != null ? element.s : 0);
  //   })
  //   return (Math.round(total * 100) / 100);
  // }

  getOrderQty(){
    let total = 0;
    this.inbound.forEach(x =>{
      total = total + (x.allocatedQty != null ? x.allocatedQty : 0)
    })
    return Math.round(total *100 / 100)
  }
  getPickingQty(){
    let total = 0;
    this.inbound.forEach(x =>{
      total = total + (x.pickConfirmQty != null ? x.pickConfirmQty : 0)
    })
    return Math.round(total *100 / 100)
  }
}

