import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { DeliveryService } from '../../delivery-management/delivery.service';
import { ReportsService } from '../reports.service';
import { StocksampleService } from '../stocksamplereport/stocksample.service';

@Component({
  selector: 'app-detailspopup',
  templateUrl: './detailspopup.component.html',
  styleUrls: ['./detailspopup.component.scss'],
  styles: [`
  :host ::ng-deep .row-green {
      color: #3eaa00 !important;
      font-weight: bold !important;
  }
  :host ::ng-deep .row-red {
    color: #ff1e00 !important;
    font-weight: bold !important;
}
:host ::ng-deep .row-blue {
    color: #0000FF !important;
    font-weight: bold !important;
}
:host ::ng-deep .row-yellow {
    color: #FFFF00 !important;
    font-weight: bold !important;
}
:host ::ng-deep .row-violet {
    color: #2b0d7a !important;
    font-weight: bold !important;
}
:host ::ng-deep .row-serious {
    color: #56F000 !important;
    font-weight: bold !important;
}
:host ::ng-deep .row-lightgreen {
    color: #008a7d !important;
    font-weight: bold !important;
}
`
]
})
export class DetailspopupComponent implements OnInit {

  container: any[] = [];
  selectedcontainer: any[] = [];
  @ViewChild('containerreportTag') containerreportTag: Table | any;
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

  displayedColumns: string[] = ['select', 'warehouseId', 'statusId', 'containerNo', 'containerReceiptNo', 'refDocNumber', 'containerType', 'origin', 'partnerCode', 'createdBy', 'referenceField2', 'referenceField5'];
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
    private service: DeliveryService,
    private reportService: ReportsService,
    private serviceLine: DeliveryService,
    public auth: AuthService) { }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;
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

  
  ngOnInit(): void {
    // this.auth.isuserdata();
    //   this.callstatus();
    this.getDropdown();
   
  }
 
  multisalesOrderNo: any[] = [];
  multiOrderNo: any[] = [];

  getDropdown(){
    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.statusId =[48,50,57],
    this.sub.add(this.serviceLine.searchLinev2(obj).subscribe(res => {
      res.forEach((x: any) =>
      this.multiOrderNo = res
      .map((x: any) => ({ value: x.refDocNumber, label: x.refDocNumber })));
      res.forEach((x: any) =>
      this.multisalesOrderNo = res
      .filter((x: any) => x.salesOrderNumber !== null)
      .map((x: any) => ({ value: x.salesOrderNumber, label: x.salesOrderNumber })));
    
     
     
      this.multiOrderNo=this.cs.removeDuplicateInArray(this.multiOrderNo);
      this.multisalesOrderNo=this.cs.removeDuplicateInArray(this.multisalesOrderNo)
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);

    }))
  }


  
  



  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }




  searhform = this.fb.group({
    companyCodeId:[[this.auth.companyId]],
    plantId:[[this.auth.plantId]],
    warehouseId:[[this.auth.warehouseId]],
    languageId:[[this.auth.languageId]],
    refDocNumber:[],
    salesOrderNumber:[],

  });

  totalRecords = 0;

  reset() {
    this.searhform.reset();
  
  }
  submit(){
    let obj: any = {};
    obj.companyCode = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.statusId =[48,50,57],
    obj.salesOrderNumber=this.searhform.controls.salesOrderNumber.value;
    obj.refDocNumber=this.searhform.controls.refDocNumber.value
    this.sub.add(this.serviceLine.searchLinev2(obj).subscribe(res => {
      this.spin.hide();
     this.container = res.filter(item => item.statusId != 59);
     this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
    }, err => {
      this.spin.hide();
     
      this.cs.commonerrorNew(err);
      
      
    }))
  }
  downloadexcel() {
    try {
      const excelData = this.container.map((x, index) => ({
        'SNO': index + 1,
        'Status': x.statusDescription,
        'Order Type': x.referenceDocumentType,
        'Sales Order Number': x.salesOrderNumber,
        'Order No': x.refDocNumber,
        'Line No': x.lineNumber,
        'Mfr Name': x.manufacturerName,
        'Part No': x.itemCode,
        'Description': x.description,
        'HE No': x.handlingEquipment,
        'Order Qty': x.orderQty,
        'Picked Qty': x.referenceField9,
        'QA Qty': x.referenceField10,
      }));
  
      // Assuming this.cs.exportAsExcel is a service method for exporting to Excel
      this.cs.exportAsExcel(excelData, 'Order Tracking Report');
    } catch (error) {
      console.error('Error during Excel export:', error);
      // Handle the error as needed (e.g., show a user-friendly message)
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
}

