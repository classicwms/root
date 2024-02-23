import { SelectionModel } from '@angular/cdk/collections';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
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
import { MasterService } from 'src/app/shared/master.service';
import { ReportsService } from '../reports.service';
import { TransactionhistrylinesComponent } from './transactionhistrylines/transactionhistrylines.component';

@Component({
  selector: 'app-transaction-history',
  templateUrl: './transaction-history.component.html',
  styleUrls: ['./transaction-history.component.scss']
})
export class TransactionHistoryComponent implements OnInit {
  screenid=3208;
  transfer: any[] = [];
  selectedtransfer : any[] = [];
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;



  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  showFiller = false;

  displayedColumns: string[] = ['warehouseId', 'itemCode','itemDescription',  'openingStock', 'inboundQty', 'outboundQty','stockAdjustmentQty','closingStock'];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];


  constructor(public dialog: MatDialog,
    private http: HttpClient,
    // private cas: CommonApiService,
    private fb: FormBuilder,
    public toastr: ToastrService,
    private router: Router,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    private service: ReportsService,
  private masterService: MasterService,
    public auth: AuthService) { }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;
 
  ngOnInit(): void {
    this.dropdownfill();
  }

  multiselectWarehouseList: any[] = [];
  multiselectItemCodeList: any[] = [];
  itemCodeList: any[] = [];
  
  onItemType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        itemList: this.service.getItemCodeDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
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

  warehouseList: any[] = [];
  selectedWarehouseList: any[] = [];
  selectedItems: any[] = [];
  multiWarehouseList: any[] = [];
  lines(res){
    console.log(res)
    const dialogRef = this.dialog.open(TransactionhistrylinesComponent, {
      //width: '55%',
      maxWidth: '90%',
      position: { top: '8.5%' },
      data: { code: res.itemCode , languageId:this.auth.languageId, companyCodeId:this.auth.companyId,warehouseId: this.auth.warehouseId,plantId: this.auth.plantId,fromCreatedOn:this.searhform.controls.fromCreatedOn.value,toCreatedOn:this.searhform.controls.toCreatedOn.value}
    });
  
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      // this.animal = result;
    });
  }
  
  
  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),

    })
    .subscribe(({ warehouse }) => {
      if(this.auth.userTypeId != 3){
        this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
      }else{
        this.warehouseList = warehouse
      }
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiselectWarehouseList = this.multiWarehouseList;
        this.multiselectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.value == this.auth.warehouseId)
            this.searhform.controls.warehouseId.patchValue(warehouse.value);
        })
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

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


  searhform = this.fb.group({
    companyCodeId:[this.auth.companyId],
    plantId:[this.auth.plantId],
    languageId:[this.auth.languageId],
    warehouseId: [this.auth.warehouseId],
    itemCode: [],
    itemCodeFE: [],
    fromCreatedOn: [,Validators.required],
    toCreatedOn: [,Validators.required],
  });

  totalRecords = 0;

  reset(){
    this.searhform.reset();
    this.searhform.controls.companyCodeId.patchValue(this.auth.companyId)
    this.searhform.controls.languageId.patchValue(this.auth.languageId)
    this.searhform.controls.plantId.patchValue(this.auth.plantId)
    this.searhform.controls.warehouseId.patchValue(this.auth.warehouseId)
  }
  filtersearch(excel = false) {
this.spin.show();

    if (this.searhform.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        ""
      );

      this.cs.notifyOther(true);
      this.spin.hide();
      return;
    }

      if(this.searhform.controls.itemCodeFE.value){
        this.searhform.controls.itemCode.patchValue([this.searhform.controls.itemCodeFE.value])
      }
    this.searhform.controls.fromCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.fromCreatedOn.value));
    this.searhform.controls.toCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.toCreatedOn.value));

    this.service.getTransactionHistoryReport(this.searhform.value).subscribe(res => {
      this.transfer = res;
      

   
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
      this.spin.hide();
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    });
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.transfer.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        'Mfr Name ': x.manufacturerName,
        "Part No": x.itemCode,
        "Description":x.itemDescription,
        'Opening Stock': x.openingStock,
        "Inbound Qty": x.inboundQty,
        "Outbound Qty": x.outboundQty,
        "Stock Adjustment Qty ": x.stockAdjustmentQty,
        "Closing Qty ": x.closingStock,
        "System Stock":x.systemInventory,
        "Variance":x.variance,
      });

    })
    this.cs.exportAsExcel(res, "Transaction History");
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




 


 
}


