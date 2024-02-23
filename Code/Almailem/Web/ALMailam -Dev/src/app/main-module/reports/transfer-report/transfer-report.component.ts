



  
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
  import { Table } from "primeng/table";
import { InhouseTransferService } from "../../make&change/inhouse-transfer/inhouse-transfer.service";
  
  
  @Component({
    selector: 'app-transfer-report',
    templateUrl: './transfer-report.component.html',
    styleUrls: ['./transfer-report.component.scss']
  })
  export class TransferReportComponent implements OnInit {
    screenid=3169;
    transfer: any[] = [];
    selectedtransfer : any[] = [];
    @ViewChild('transferTag') transferTag: Table | any;
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
        private reportService: ReportsService,
        public cs: CommonService,
        private ReportsService: ReportsService,
        public auth: AuthService, private transferService: InhouseTransferService) { 
          this.multiOrderNo1 = [{
            label: 'On Hand',
            value: 1
          },
          {
            label: 'Hold',
            value: 7
          },
          {
            label: 'WIP',
            value: 10
          },
        
        ];
        this.muultiOrderNo2 = [{
          label: 'On Hand',
          value: 1
        },
        {
          label: 'Hold',
          value: 7
        },
        {
          label: 'WIP',
          value: 10
        },
      ];
        }
      routeto(url: any, id: any) {
        sessionStorage.setItem('crrentmenu', id);
        this.router.navigate([url]);
      }
      animal: string | undefined;
     selectedCompany:any[]=[];
     selectedplant:any[]=[];
     selectedwarehouse:any[]=[];
      ngOnInit(): void {
        this.selectedCompany=this.auth.companyIdAndDescription;
        this.selectedplant=this.auth.plantIdAndDescription;
        this.selectedwarehouse=this.auth.warehouseIdAndDescription;
       // this.getDropdown();
      }
   
  
  
      multiselectItemCodeList: any[] = [];
      multiselectItemCodeList1: any[] = [];
      itemCodeList: any[] = [];
      itemCodeList1: any[] = [];
      onItemType(searchKey) {
        let searchVal = searchKey?.filter;
        if (searchVal !== '' && searchVal !== null) {
          forkJoin({
            itemList: this.ReportsService.getItemCodeDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
          })
            .subscribe(({ itemList }) => {
              if (itemList != null && itemList.length > 0) {
                this.multiselectItemCodeList = [];
                this.itemCodeList = itemList;
                this.itemCodeList.forEach(x => this.multiselectItemCodeList.push({value: x.itemCode, label: x.itemCode + ' - ' + x.manufacturerName + ' - ' + x.description }))
              }
            });
        }
      }
  
      onItemType1(value) {
        let searchTarget = value?.filter;
        if (searchTarget !== '' && searchTarget !== null) {
          forkJoin({
            itemList1: this.ReportsService.getItemCodeDropDown2(searchTarget.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
          })
            .subscribe(({ itemList1 }) => {
              if (itemList1 != null && itemList1.length > 0) {
                this.multiselectItemCodeList1 = [];
                this.itemCodeList1 = itemList1;
                this.itemCodeList1.forEach(x => this.multiselectItemCodeList1.push({value: x.itemCode, label: x.itemCode + ' - ' + x.manufacturerName + ' - ' + x.description}))
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
            storageList: this.ReportsService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
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
            storageList: this.ReportsService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
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
  
      
      targetStockTypeId:any;
      sourceItemCode: any;
      sourceStockTypeId :any;
      sourceStorageBin: any;
      targetItemCode: any;
      targetStorageBin: any;
      stockTypeId = '';
      startConfirmedOn = '';
      endConfirmedOn = '';
  
  
      reset(){
        this.searhform.reset();
        this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
        this.searhform.controls.companyCodeId.patchValue([this.auth.companyId]);
        this.searhform.controls.plantId.patchValue([this.auth.plantId]);
        this.searhform.controls.languageId.patchValue([this.auth.languageId]);
      
      }
      searhform = this.fb.group({
        companyCodeId:[[this.auth.companyId],],
        plantId:[[this.auth.plantId],],
        languageId:[[this.auth.languageId],],
        warehouseId:[[this.auth.warehouseId],],
        sourceItemCode: [],
        sourceItemCodeFE: [],
        sourceStockTypeId:[],
        targetStockTypeId:[],
        sourceStorageBin:[],
        sourceStorageBinFE:[],
        targetStorageBin:[],
        targetStorageBinFE:[],
        endConfirmedOn:[],
        startConfirmedOn:[],
        refDocNumber: [],
        targetItemCode:[],
        targetItemCodeFE:[],
        statusId:[],
      }); 
      filtersearch(){
       
  
  this.spin.show();
        let obj: any = {};
  
        obj.sourceItemCode = [];
        if (this.sourceItemCode != null) {
          obj.sourceItemCode.push(this.sourceItemCode)
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
  
  this.searhform.controls.sourceItemCode.patchValue([this.searhform.controls.sourceItemCodeFE.value]);
  this.searhform.controls.targetItemCode.patchValue([this.searhform.controls.targetItemCodeFE.value]);
  this.searhform.controls.sourceStorageBin.patchValue([this.searhform.controls.sourceStorageBinFE.value]);
  this.searhform.controls.targetStorageBin.patchValue([this.searhform.controls.targetStorageBinFE.value]);
  if(this.searhform.controls.targetStorageBinFE.value == null){
    this.searhform.controls.targetStorageBin.patchValue(this.searhform.controls.targetStorageBinFE.value);
  }
  if(this.searhform.controls.sourceStorageBinFE.value == null){
    this.searhform.controls.sourceStorageBin.patchValue(this.searhform.controls.sourceStorageBinFE.value);
  }
  if(this.searhform.controls.sourceItemCodeFE.value == null){
    this.searhform.controls.sourceItemCode.patchValue(this.searhform.controls.sourceStorageBinFE.value);
  }
  if(this.searhform.controls.targetItemCodeFE.value == null){
    this.searhform.controls.targetItemCode.patchValue(this.searhform.controls.targetItemCodeFE.value);
  }
      this.searhform.controls.startConfirmedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startConfirmedOn.value));
      this.searhform.controls.endConfirmedOn.patchValue(this.cs.day_callapiSearch( this.searhform.controls.endConfirmedOn.value));
   
      obj.companyCodeId = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
      obj.warehouseId = [this.auth.warehouseId];
  obj.sourceStockTypeId=this.sourceStockTypeId;
  obj.stockTypeId=this.targetStockTypeId;
        // obj.statusId = []
  
        this.sub.add(this.transferService.searchTransferLineSpark(this.searhform.getRawValue()).subscribe(res => {
          let result = res.filter((x: any) => x.statusId == 50);
          this.transfer = res;
       
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
        this.transfer.forEach(x => {
          res.push({
            "Company":x.companyDescription,
            "Branch":x.plantDescription,
            "Warehouse":x.warehouseDescription,
            "Mfr Name":x.manufacturerName,
            "Source Part No": x.sourceItemCode,
            "Source Storage Bin": x.sourceStorageBin,
            "Target Part No": x.targetItemCode,
            "Target Storage Bin":  x.targetStorageBin,
            "Transfer Confirmed Qty":  x.transferConfirmedQty,
            "Barcode": x.barcodeId,
            "Confirmed By": x.confirmedBy,
            "Confirmed On": this.cs.dateapi(x.confirmedOn),
    
            // 'Created By': x.createdBy,
            // 'Date': this.cs.dateapi(x.createdOn),
          });
    
        })
        res.push({
          "Company":'',
          "Branch":'',
          "Warehouse":'',
          "Source Part No": '',
          "Mfr Name":'',
          "Source Storage Bin": '',
          "Target Part No": '',
          "Target Storage Bin": '',
          "Transfer Confirmed Qty":  this.getTransferQty(),
          "Barcodes": '',
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
    
     
      multiOrderNo1:any[]=[];
      muultiOrderNo2:any[]=[];
      // getDropdown(){
      //   this.sub.add(this.reportService.getTransferReport({warehouseId : [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId]}).subscribe(res => {
      //     res.forEach((x: any) => 
      //     this.multiOrderNo1.push({ value: x.sourceStockTypeId, label: x.sourceStockTypeId }));
      //     this.multiOrderNo1=this.cs.removeDuplicatesFromArrayNewstatus(this.multiOrderNo1);
      //   }))
       

      //   this.sub.add(this.reportService.getTransferReport({warehouseId : [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId]}).subscribe(res => {
      //     res.forEach((x: any) => this.muultiOrderNo2.push({ value: x.targetStockTypeId, label: x.targetStockTypeId }));
      //     this.muultiOrderNo2=this.cs.removeDuplicatesFromArrayNewstatus(this.muultiOrderNo2);
      //   }))
       
      
      // }
    
     
    
    
      // getBillableAmount() {
      //   let total = 0;
      //   this.dataSource.data.forEach(element => {
      //     total = total + (element.s != null ? element.s : 0);
      //   })
      //   return (Math.round(total * 100) / 100);
      // }
  
      getTransferQty(){
        let total = 0;
        this.transfer.forEach(x =>{
          total = total + (x.transferConfirmedQty != null ? x.transferConfirmedQty : 0)
        })
        return Math.round(total *100 / 100)
      }
    }
  
  
  