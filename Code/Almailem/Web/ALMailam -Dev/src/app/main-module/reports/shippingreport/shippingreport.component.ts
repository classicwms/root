import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { PickingService } from '../../Outbound/picking/picking.service';
import { StocksampleService } from '../stocksamplereport/stocksample.service';
import { ReportsService } from '../reports.service';
import { InboundConfirmationService } from '../../Inbound/inbound-confirmation/inbound-confirmation.service';
import { PreinboundService } from '../../Inbound/preinbound/preinbound.service';

@Component({
  selector: 'app-shippingreport',
  templateUrl: './shippingreport.component.html',
  styleUrls: ['./shippingreport.component.scss']
})
export class ShippingreportComponent implements OnInit {
  screenid=3219;
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
      displayedColumns: string[] = ['refDocNumber', 'lineNo', 'itemCode', 'description', 'orderQty', 'acceptedQty', 'damageQty', 'varianceQty', 'confirmedOn', 'createdOn', 'referenceField10'];
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
    public cs: CommonService, 
    private service: PreinboundService,
    private reportService: ReportsService,
    private serviceLine: InboundConfirmationService,
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
 

  form = this.fb.group({
   warehouseId: [[this.auth.warehouseId]],
   companyCode: [[this.auth.companyId]],
   languageId: [[this.auth.languageId]],
   plantId: [[this.auth.plantId]],
   endConfirmedOn: [],
   itemCode: [],
   itemCodeFE: [],
   manufacturerName: [],
   salesOrderNumber:[],
   targetBranchCode:[],
   refDocNumber: [],
   statusId: [],
   startConfirmedOn: [],
  });

  ngOnInit(): void {
    this.getDropdown();
   
    this.form.controls.companyCode.patchValue(this.auth.companyId);
    this.form.controls.plantId.patchValue(this.auth.plantId);
    this.form.controls.plantId.disable();
    this.form.controls.companyCode.disable();
    this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
    this.form.controls.warehouseId.disable();
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

  multisalesOrderNo: any[]=[];
  multiOrderNo: any[] = [];
  multimanufacturerName:any[]=[];
  multitarget:any[]=[];
  multistatus:any[]=[];
    getDropdown(){
      this.spin.show();
      let obj: any = {};
      obj.companyCode = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
      obj.warehouseId = [this.auth.warehouseId];
      this.sub.add(this.reportService.getoutboundline(obj).subscribe(res => {
        res.forEach((x: any) =>
         this.multiOrderNo.push({ value: x.refDocNumber, label: x.refDocNumber })
         );
         res.forEach((x: any) =>
         this.multisalesOrderNo.push({ value: x.salesOrderNumber, label: x.salesOrderNumber })
         );
         res.forEach((x: any) =>
         this.multimanufacturerName.push({ value: x.manufacturerName, label: x.manufacturerName })
         );
         res.forEach((x: any) =>
         this.multistatus.push({ value: x.statusId, label: x.statusDescription })
         );
         res.forEach((x: any) =>
         this.multitarget = res
         .filter((x: any) => x.targetBranchCode !== null)
         .map((x: any) => ({ value: x.targetBranchCode, label: x.targetBranchCode })));
       
        this.multiOrderNo=this.cs.removeDuplicateInArray(this.multiOrderNo);
         this.multimanufacturerName=this.cs.removeDuplicateInArray(this.multimanufacturerName);
         this.multistatus=this.cs.removeDuplicateInArray(this.multistatus);
        this.spin.hide();
      }, err => {
        this.spin.hide();
        this.cs.commonerrorNew(err);

      }))
    }

  





 
  itemCode: any;
  confirmedStorageBin: any;
  refDocNumber = '';
  warehouseId = '';

  filtersearch(){
    if(this.form.controls.itemCodeFE.value != null){
      this.form.controls.itemCode.patchValue([this.form.controls.itemCodeFE.value]);
    }
    if(this.form.controls.itemCodeFE.value == null){
      this.form.controls.itemCode.patchValue(this.form.controls.itemCodeFE.value);
    }
    this.form.controls.warehouseId.patchValue([this.auth.warehouseId]);
    this.form.controls.companyCode.patchValue([this.auth.companyId]);
    this.form.controls.languageId.patchValue([this.auth.languageId]);
    this.form.controls.plantId.patchValue([this.auth.plantId]);
this.spin.show();
this.form.controls.endConfirmedOn.patchValue(this.cs.day_callapiSearch(this.form.controls.endConfirmedOn.value));
this.form.controls.startConfirmedOn.patchValue(this.cs.day_callapiSearch(this.form.controls.startConfirmedOn.value));


let obj: any = {};
obj.companyCodeId = [this.auth.companyId];
obj.languageId = [this.auth.languageId];
obj.plantId = [this.auth.plantId];
obj.warehouseId = [this.auth.warehouseId];
obj.itemCode = [this.form.controls.itemCodeFE.value];
obj.refDocNumber = this.form.controls.refDocNumber.value;
    this.sub.add(this.reportService.getoutboundline(this.form.getRawValue()).subscribe(res => {
      console.log(res)
      this.inbound = res;
   
     
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
      this.spin.hide();
     
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
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Order No ": x.refDocNumber,
      "Order Type":x.referenceDocumentType,
      "Sales Invoice Number":x.salesOrderNumber,
      "Sales Order Number":x.salesOrderNumber,
      "Target Branch":x.targetBranchCode,

        "Mfr Name":x.manufacturerName,
        'Part No': x.itemCode,
        'Description': x.description,
        "Order Qty":x.orderQty,
        'Picker Qty': x.referenceField9,
        'QA Qty': x.referenceField10,
        'Delivered Qty': x.deliveryQty,
        'Created Date':this.cs.dateapiwithTime(x.createdOn),
        "Status ": x.statusDescription,
      });

    })
    this.cs.exportAsExcel(res, "Shipping Report");
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

 




  
  

  reset(){
   this.form.reset();
   this.form.controls.companyCode.patchValue([this.auth.companyId])
   this.form.controls.languageId.patchValue([this.auth.languageId])
   this.form.controls.plantId.patchValue([this.auth.plantId])
   this.form.controls.warehouseId.patchValue([this.auth.warehouseId])
  }
}
