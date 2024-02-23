import { SelectionModel } from "@angular/cdk/collections";
import { HttpClient } from "@angular/common/http";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription, forkJoin, of } from "rxjs";
import { catchError } from "rxjs/operators";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { MasterService } from "src/app/shared/master.service";
import { ContainerReceiptService } from "../../Inbound/Container-receipt/container-receipt.service";
import { PickingService } from "../../Outbound/picking/picking.service";
import { ReportsService } from "../reports.service";
import { StocksampleService } from "../stocksamplereport/stocksample.service";
import { PreoutboundService } from "../../Outbound/preoutbound/preoutbound.service";
import { Table } from "primeng/table";


@Component({
  selector: 'app-preoutbound',
  templateUrl: './preoutbound.component.html',
  styleUrls: ['./preoutbound.component.scss']
})
export class PreoutboundComponent implements OnInit {
  screenid=3186;
  inbound: any[] = [];
  selectedinbound : any[] = [];
  @ViewChild('inboundTag') inboundTag: Table | any;
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;



  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  showFiller = false;

 // displayedColumns: string[] = [ 'statusId','itemCode','proposedStorageBin', 'confirmedStorageBin', 'packBarcodes', 'putAwayQuantity', 'putawayConfirmedQty', 'confirmedBy','confirmedOn','createdOn', 'leadTime'];
      //wms demo
      displayedColumns: string[] = ['select', 'referenceField10', 'partnerCode', 'outboundOrderTypeId', 'referenceField1', 'preOutboundNo', 'refDocNumber', 'refDocDate', 'requiredDeliveryDate',];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];

  soTypeList1: any[];
  constructor(public dialog: MatDialog,
    private http: HttpClient,
    // private cas: CommonApiService,
    private fb: FormBuilder,
    public toastr: ToastrService,
    private router: Router,
    private spin: NgxSpinnerService,
    private reportService: ReportsService,
    public cs: CommonService, 
    private service: PreoutboundService,
    public auth: AuthService,
    private masterService: MasterService,) { 
      this.soTypeList1 = [
        {value: 'N', label: 'N'},
        {value: 'S', label: 'S'},
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

  form = this.fb.group({
    createdBy: [],
    endCreatedOn:[],
    endOrderDate:[],
    endRequiredDeliveryDate:[],
    outboundOrderTypeId: [],
    partnerCode: [],
    preOutboundNo: [],
    refDocNumber:[],
    soNumber: [],
    soType: [,],
    startCreatedOn:[],
    itemCode:[],
    itemCodeFE:[],
    startOrderDate:[],
    startRequiredDeliveryDate:[],
    statusId: [],
    companyCodeId: [[this.auth.companyId]],
    languageId: [[this.auth.languageId] ],
    plantId: [[this.auth.plantId] ],
    warehouseId: [[this.auth.warehouseId] ],
    
 
  });

  ngOnInit(): void {
    this.getDropdown();
    this.form.controls.companyCodeId.patchValue(this.auth.companyId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.plantId.disable();
    this.form.controls.companyCodeId.disable();
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
    this.form.controls.warehouseId.disable();
  }

 

  // ngAfterViewInit() {
  //   this.dataSource.paginator = this.paginator;
  // }





 
  itemCode: any;
  confirmedStorageBin: any;
  refDocNumber = '';
  warehouseId = '';

  filtersearch(){
this.spin.show();
this.form.controls.endRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.form.controls.endRequiredDeliveryDate.value));
this.form.controls.startRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.form.controls.startRequiredDeliveryDate.value));
this.form.controls.endOrderDate.patchValue(this.cs.day_callapiSearch(this.form.controls.endOrderDate.value));
this.form.controls.startOrderDate.patchValue(this.cs.day_callapiSearch(this.form.controls.startOrderDate.value));
let obj: any = {};
obj.companyCodeId = [this.auth.companyId];
obj.languageId = [this.auth.languageId];
obj.plantId = [this.auth.plantId];
obj.warehouseId = [this.auth.warehouseId];
obj.preOutboundNo=this.form.controls.preOutboundNo.value;
obj.startRequiredDeliveryDate=this.form.controls.startRequiredDeliveryDate.value;
obj.endRequiredDeliveryDate=this.form.controls.endRequiredDeliveryDate.value;
obj.endOrderDate=this.form.controls.endOrderDate.value;
obj.startOrderDate=this.form.controls.startOrderDate.value;
obj.soType=this.form.controls.soType.value;
obj.refDocNumber=this.form.controls.refDocNumber.value;

    this.sub.add(this.service.searchSpark(obj).subscribe(res => {
      console.log(res)
      this.inbound = res;
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

  multiOrderNo: any[] = [];
  getDropdown(){
    this.spin.show();
    let obj: any = {};
    obj.companyCode = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    this.sub.add(this.service.searchSpark(obj).subscribe(res => {
      res.forEach((x: any) => this.multiOrderNo.push({ value: x.refDocNumber, label: x.refDocNumber }));
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);

    }))
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
        "Status  ": x.referenceField10,
        'Target Branch': x.partnerCode,
        'Order Type': x.referenceDocumentType,
        "Order Category": x.referenceField1,
        "Preoutbound No": x.preOutboundNo,
        "Order No": x.refDocNumber,
        "Ordered Date":this.cs.dateapiwithTime(x.refDocDate),
        "Delivery Date":this.cs.dateapiwithTime(x.requiredDeliveryDate),

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Binning");
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
  

   reset(){
    this.form.reset();
    this.form.controls.companyCodeId.patchValue([this.auth.companyId])
    this.form.controls.languageId.patchValue([this.auth.languageId])
    this.form.controls.plantId.patchValue([this.auth.plantId])
    this.form.controls.warehouseId.patchValue([this.auth.warehouseId])
   }
}
