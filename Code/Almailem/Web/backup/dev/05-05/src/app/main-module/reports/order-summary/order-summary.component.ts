




  
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
import { DeliveryService } from "../../Outbound/delivery/delivery.service";
import { PickingService } from "../../Outbound/picking/picking.service";
  import { ReportsService } from "../reports.service";
import  { stockElement, StocksampleService }  from "../stocksamplereport/stocksample.service";

@Component({
  selector: 'app-order-summary',
  templateUrl: './order-summary.component.html',
  styleUrls: ['./order-summary.component.scss']
})
export class OrderSummaryComponent implements OnInit {
  
    isShowDiv = false;
    table = true;
    fullscreen = false;
    search = true;
    back = false;
  
  
  
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    showFiller = false;

    displayedColumns: string[] = [ 'warehouseId','actions', 'refDocNumber', 'referenceField1', 'outboundOrderTypeId', 'partnerCode', 'referenceField10', 'referenceField9', 'referenceField8', 'deliveryQty', 'requiredDeliveryDate', 'refDocDate', 'deliveryConfirmedOn',];
    sub = new Subscription();
    ELEMENT_DATA: any[] = [];

    movementList: any[];
    submovementList: any[] = [];
    NofilterSubmovementList: any[];

    outboundOrderTypeIdList: any[] = [];
    soTypeList: any[] = [];

    constructor(public dialog: MatDialog,
      private service: StocksampleService,
      private http: HttpClient,
      // private cas: CommonApiService,
      private fb: FormBuilder,
      public toastr: ToastrService,
      private router: Router,
      private spin: NgxSpinnerService,
      public cs: CommonService,
      private DeliveryService: DeliveryService,
      private ReportsService: ReportsService,
      private masterService: MasterService,
      private auth: AuthService) {

        this.soTypeList = [
          {value: 'S', label: 'S'},
          {value: 'N', label: 'N'}
        ];

        this.outboundOrderTypeIdList = [
          { value: 0, label: 'SO' },
          { value: 1, label: 'WH2WH' },
          { value: 2, label: 'Return PO' },
          { value: 3, label: 'TE Order' },
        ];

       }
    routeto(url: any, id: any) {
      sessionStorage.setItem('crrentmenu', id);
      this.router.navigate([url]);
    }

    // this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
    //   console.log(selectedValue);
    //   this.showingStorageClassList = this.storageClassList.filter(element => {
    //     return element.warehouseId === this.form.controls['warehouseId'].value;
    //   });
    // })

 


    animal: string | undefined;
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
  
      this.dataSource.filter = filterValue.trim().toLowerCase();
  
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }
    ngOnInit(): void {
      this.dropdownfill()
    }

    warehouseList: any[] = [];
    multiselectWarehouseList: any[] = [];
    multiWarehouseList: any[] = [];
  
    selectedItems: any[] = [];
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
        }, (err) => {
          this.toastr.error(
            err,
            ""
          );
        });
      this.spin.hide();
  
    }

    ngAfterViewInit() {
      this.dataSource.paginator = this.paginator;
    }

    // movementChange(){
    //   this.submovementList = []
    //     this.NofilterSubmovementList.forEach(x=>{
    //     if(x.movemenTypeID === this.movementType[0]){
    //       this.submovementList.push(x)
    //     }
    //     })
    //   }


      refDocNumber = '';
    outboundOrderTypeId = '';
    soType = [];
    statusId = '';
    endDeliveryConfirmedOn = '';
    startDeliveryConfirmedOn = '';
    startOrderDate = '';
    endOrderDate = '';
    startRequiredDeliveryDate = '';
    endRequiredDeliveryDate = '';

    filtersearch(){
      // if(this.startDeliveryConfirmedOn == null || this.startDeliveryConfirmedOn == undefined || !this.startDeliveryConfirmedOn){
      //   this.toastr.error("Please fill the date to continue", "Notification", {
      //     timeOut: 2000,
      //     progressBar: false,
      //   });
      //   this.cs.notifyOther(true);
      //   return
      // }
this.spin.show();
      let obj: any = {};

     // obj.soType = [];
      if (this.soType != null) {
        obj.soType = this.soType
      }

      
      obj.statusId = [];
      if (this.statusId != null && this.statusId.trim() != "") {
        obj.statusId.push(this.statusId)
      }

      obj.refDocNumber = [];
    
      if (this.refDocNumber != null && this.refDocNumber) {
        obj.refDocNumber = this.refDocNumber
      }
     // obj.outboundOrderTypeId = [];
      if (this.outboundOrderTypeId != null && this.outboundOrderTypeId) {
        obj.outboundOrderTypeId = this.outboundOrderTypeId
      }
     obj.endDeliveryConfirmedOn = this.cs.dateNewFormat(this.endDeliveryConfirmedOn);
     obj.startDeliveryConfirmedOn = this.cs.dateNewFormat(this.startDeliveryConfirmedOn);

     obj.endOrderDate = this.cs.dateNewFormat(this.endOrderDate);
     obj.startOrderDate = this.cs.dateNewFormat(this.startOrderDate);
     
     obj.endRequiredDeliveryDate = this.cs.dateNewFormat(this.endRequiredDeliveryDate);
     obj.startRequiredDeliveryDate = this.cs.dateNewFormat(this.startRequiredDeliveryDate);

      obj.warehouseId = this.selectedItems;

      obj.statusId = []

      this.sub.add(this.DeliveryService.search(obj).subscribe(res => {
        console.log(res)
       // let result = res.filter((x: any) => x.statusId == 50);

       res.forEach(element => {
        if(element.statusId != 59){
          element.referenceField8 = 0;
          element.referenceField7 = 0;
        }
       });

        this.dataSource = new MatTableDataSource<any>(res);
        console.log(this.dataSource)
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        console.log(this.paginator)
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
  
          "Status": x.statusId == 59 ? 'Delivered' : x.statusId == 39 ? 'Received' : 'Inprogress',
          "Order No ": x.refDocNumber,
          "Order Category ": x.referenceField1,
          'Order Type': this.cs.getoutboundOrderType_text(x.outboundOrderTypeId),
          "Store ": x.partnerCode,
          " Order Lines ": x.referenceField10,
          "Total Quantity  ": x.referenceField9,
          " Delivered Lines ": x.referenceField8,
          " Delivered Qty ": x.referenceField7,
          "Req Date  ": this.cs.dateExcel(x.requiredDeliveryDate),
          "Order Date": this.cs.dateTimeApi(x.refDocDate),
          "Delivery Date ": this.cs.dateTimeApi(x.deliveryConfirmedOn),
        });
  
      })
      this.cs.exportAsExcel(res, "Order Summary");
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
  }

