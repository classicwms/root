

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
  import { ReportsService } from "../reports.service";
import  { stockElement, StocksampleService }  from "../stocksamplereport/stocksample.service";
import { Table } from "primeng/table";
import { PathLocationStrategy } from "@angular/common";

  @Component({
    selector: 'app-container-report',
    templateUrl: './container-report.component.html',
    styleUrls: ['./container-report.component.scss']
  })
  export class ContainerReportComponent implements OnInit {
    container: any[] = [];
  selectedcontainer: any[] = [];
  @ViewChild('containerreportTag') containerreportTag: Table | any;
  isShowDiv = false;
  table = false;
  fullscreen = false;
  search = true;
  back = false;



  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  showFiller = false;

  displayedColumns: string[] = ['select', 'warehouseId', 'statusId', 'containerNo', 'containerReceiptNo', 'refDocNumber', 'containerType', 'origin', 'partnerCode', 'createdBy', 'referenceField2', 'referenceField5'];
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
    public cs: CommonService,
    private containerservice: ContainerReceiptService,
    private masterService: MasterService,
    public auth: AuthService) { }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }
  animal: string | undefined;

  ngOnInit(): void {
    // this.auth.isuserdata();
    //   this.callstatus();
    this.containerget();
    this.dropdownfill();
    this.searhform.controls.companyCodeId.patchValue(this.auth.companyId);
    this.searhform.controls.companyCodeId.disable();
    this.searhform.controls.plantId.patchValue(this.auth.plantId);
    this.searhform.controls.plantId.disable();
  }
  warehouseList: any[] = [];
  selectedWarehouseList: any[] = [];
  selectedItems: any[] = [];
  multiselectWarehouseList: any[] = [];
  multiWarehouseList: any[] = [];

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
        this.multiselectWarehouseList = this.cs.removeDuplicatesFromArrayNewstatus(this.multiselectWarehouseList)
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }


  containerNoListselected: any[] = [];
  containerNoList: any[] = [];

  containerReceiptNoListselected: any[] = [];
  containerReceiptNoList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  callstatus() {
    this.http.get<any>('/wms-idmaster-service/statusid').subscribe((res) => {
      res.forEach(element => {
        if (element.statusId == 10 || element.statusId == 24 || element.statusId == 11) {
          this.statusIdList.push({ value: element.statusId, label: this.cs.getstatus_text(element.statusId) })
        }
      });
    })
    console.log(this.statusIdList)
    this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
    console.log(this.statusIdList)
  }
  containerget() {
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    this.sub.add(this.containerservice.search(obj).subscribe(res => {


      res.forEach((x: { containerNo: string; }) => {
        // Check if 'containerNo' is not null before pushing to 'containerNoList'.
        if (x.containerNo !== null) {
          this.containerNoList.push({ value: x.containerNo, label: x.containerNo });
        }
      });
      res.forEach((x: { containerReceiptNo: string; }) => this.containerReceiptNoList.push({ value: x.containerReceiptNo, label: x.containerReceiptNo }))

      res.forEach(x => {
        if (x.referenceField5 != null) {
          x.statusId = 24;
        }
        if (x.refDocNumber && x.referenceField5 == null) {
          x.statusId = 11;
        }
      })

      console.log(res)
      // res.forEach((x: { partnerCode: string;}) => this.partnerCodeList.push({value: x.partnerCode, label:  x.partnerCode}));
      res.forEach((x: { statusId: string; }) => this.statusIdList.push({ value: x.statusId, label: this.cs.getstatus_text(x.statusId) }))
      console.log(this.statusIdList)
      this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
      console.log(this.statusIdList)

      res.forEach(x => {
        if (x.partnerCode != null) {
          this.partnerCodeList.push({ value: x.partnerCode, label: x.partnerCode });
        }
      });
      console.log(this.partnerCodeList)
      this.partnerCodeList = this.cs.removeDuplicatesFromArrayNew(this.partnerCodeList);
      console.log(this.partnerCodeList)
      this.spin.hide();
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
  }


  div1Function() {
    this.table = !this.table;
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




  searhform = this.fb.group({

    containerNo: [],
    containerReceiptNo: [],
    endContainerReceivedDate: [],

    partnerCode: [],
    referenceField1: [],
    startContainerReceivedDate: [],
    statusId: [],
    warehouseId: [],
    companyCodeId: [[this.auth.companyId]],
    languageId: [[this.auth.languageId]],
    plantId: [[this.auth.plantId]],

  });

  totalRecords = 0;

  reset() {
    this.searhform.reset();
    this.searhform.controls.companyCodeId.patchValue([this.auth.companyId])
    this.searhform.controls.languageId.patchValue([this.auth.languageId])
    this.searhform.controls.plantId.patchValue([this.auth.plantId])
    this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId])
  }
  filtersearch(excel = false) {

    this.searhform.controls.startContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startContainerReceivedDate.value));
    this.searhform.controls.endContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endContainerReceivedDate.value));
    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.containerNo=this.searhform.controls.containerNo.value;
    obj.startContainerReceivedDate=this.searhform.controls.startContainerReceivedDate.value;
    obj.endRequiredDeliveryDate=this.searhform.controls.endContainerReceivedDate.value;
    this.containerservice.searchSpark(obj).subscribe(res => {
      this.spin.hide();


      res.forEach(x => {
        if (x.referenceField5 != null) {
          x.statusId = 24;
        }
        if (x.refDocNumber && x.referenceField5 == null) {
          x.statusId = 11;
        }
      })
      this.container = res;

      console.log(this.container.length)

      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    });
  }
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.container.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Plant":x.plantDescription,
        'Warehouse': x.warehouseDescription,
        "Status": this.cs.getstatus_text(x.statusId),
        'Container No': x.containerNo,
        "Receipt No ": x.containerReceiptNo,
        "Size ": x.containerType,
        "Origin ": x.origin,
        'Supplier': x.partnerCode,
        "Created By  ": x.createdBy,
        " Container Unloaded On":this.cs.dateapiwithTime(x.referenceField2),
        'Actual Receipt Date':this.cs.dateapiwithTime(x.referenceField5),
        // 'Created By': x.createdBy,
        // 'Created On': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Container Receipt");
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
