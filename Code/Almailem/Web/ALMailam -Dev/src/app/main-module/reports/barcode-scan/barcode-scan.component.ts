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
import { Location } from "@angular/common";
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ContainerReceiptService } from '../../Inbound/Container-receipt/container-receipt.service';
import { PickingService } from '../../Outbound/picking/picking.service';
import { ReportsService } from '../reports.service';
import { StocksampleService } from '../stocksamplereport/stocksample.service';
import { threadId } from 'worker_threads';

@Component({
  selector: 'app-barcode-scan',
  templateUrl: './barcode-scan.component.html',
  styleUrls: ['./barcode-scan.component.scss']
})
export class BarcodeScanComponent implements OnInit {
  isShowDiv = false;
  table = true;
  fullscreen = true;
  search = false;
  back = false;
  container: any[] = [];
  selectedcontainer: any[] = [];


  showFloatingButtons: any;
  toggle = true;
  public icon = 'exand_more';
  showFiller = false;
          displayedColumns: string[] =  ['Branch','barcode','itemCode', 'storageBin', 'referenceField1','createdBy', 'createdOn', ];
  sub = new Subscription();
  ELEMENT_DATA: any[] = [];


  constructor(public dialog: MatDialog,
    private service: StocksampleService,
    private http: HttpClient,
    // private cas: CommonApiService,
    private fb: FormBuilder,
    public toastr: ToastrService,
    private router: Router,
    private spin: NgxSpinnerService,
    public cs: CommonService, private reportService: ReportsService,
    private location: Location,
    private auth: AuthService,) { }

  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;
 
  ngOnInit(): void {
    this.searchBarcodeScan();
  }
  


  searchBarcodeScan(){
    this.spin.show();
      this.reportService.barCodeScan().subscribe(res => {
        console.log(res)
        this.container = res;
      
        this.table = true;
        this.search = false;
        this.fullscreen = false;
        this.back = false;
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      });
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
    this.container.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Plant":x.plantDescription,
        'Warehouse': x.warehouseDescription,
        "Barcode ": x.barcode,
        "Part No":  x.itemCode,
        "Bin Location":  x.storageBin,
        'Mfr Code': x.referenceField1,
        'Created By': x.createdBy,
        "Created On":this.cs.dateapiwithTime(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Barcode Scan");
  }


  togglesearch() {
    this.search = false;
    this.table = true;
    this.fullscreen = false;
    this.back = true;
  }
  backsearch() {
   this.location.back();
  }


  onItemSelect(item: any) {
  }

  onSelectAll(items: any) {
  }

 






}
