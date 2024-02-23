

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

  @Component({
    selector: 'app-container-report',
    templateUrl: './container-report.component.html',
    styleUrls: ['./container-report.component.scss']
  })
  export class ContainerReportComponent implements OnInit {
  
    isShowDiv = false;
    table = false;
    fullscreen = false;
    search = true;
    back = false;
  
  
  
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    showFiller = false;

    displayedColumns: string[] = ['select', 'warehouseId', 'statusId',  'containerNo', 'containerReceiptNo', 'refDocNumber','containerType','origin','partnerCode', 'createdBy', 'referenceField2','referenceField5'];
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
      private auth: AuthService) { }
    routeto(url: any, id: any) {
      sessionStorage.setItem('crrentmenu', id);
      this.router.navigate([url]);
    }
    animal: string | undefined;
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
  
      this.dataSource.filter = filterValue.trim().toLowerCase();
  
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }
    ngOnInit(): void {
      // this.auth.isuserdata();
   //   this.callstatus();
      this.containerget();
      this.dropdownfill();
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
        if(this.auth.userTypeId != 3){
          this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        }else{
          this.warehouseList = warehouse
        }
          this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
          this.multiselectWarehouseList = this.multiWarehouseList;
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
    if(element.statusId == 10 ||element.statusId == 24 ||element.statusId == 11){
      this.statusIdList.push({value: element.statusId, label:  this.cs.getstatus_text(element.statusId)})
    }
   });
      })
      console.log(this.statusIdList)
      this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
      console.log(this.statusIdList)
    }
    containerget(){
      this.sub.add(this.containerservice.Getall().subscribe(res => {
   
        
        res.forEach((x: { containerNo: string;}) => this.containerNoList.push({value: x.containerNo, label:  x.containerNo}))
        res.forEach((x: { containerReceiptNo: string;}) => this.containerReceiptNoList.push({value: x.containerReceiptNo, label:  x.containerReceiptNo}))

        res.forEach(x => {
          if(x.referenceField5 != null){
            x.statusId = 24;
          }
          if(x.refDocNumber && x.referenceField5 == null){
            x.statusId = 11;
          }
        })

        console.log(res)
       // res.forEach((x: { partnerCode: string;}) => this.partnerCodeList.push({value: x.partnerCode, label:  x.partnerCode}));
       res.forEach((x: { statusId: string;}) => this.statusIdList.push({value: x.statusId, label:  this.cs.getstatus_text(x.statusId)}))
       console.log(this.statusIdList)
       this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
       console.log(this.statusIdList)

      res.forEach(x => {
        if(x.partnerCode != null){
          this.partnerCodeList.push({value: x.partnerCode, label:  x.partnerCode});
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

    dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
    selection = new SelectionModel<any>(true, []);
  
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
  
    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator: MatPaginator; // Pagination
    // Pagination
  

    searhform = this.fb.group({

      containerNo: [],
      containerReceiptNo: [],
      endContainerReceivedDate: [],
  
      partnerCode: [],
      referenceField1: [],
      startContainerReceivedDate: [],
      statusId: [],
      warehouseId: [],
  
    });

    totalRecords = 0;

    reset(){
      this.searhform.reset();
    }
    filtersearch(excel = false) {

      this.searhform.controls.startContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startContainerReceivedDate.value));
      this.searhform.controls.endContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endContainerReceivedDate.value));

      this.containerservice.search(this.searhform.value).subscribe(res => {
        console.log(res)
        this.spin.hide();


        res.forEach(x => {
          if(x.referenceField5 != null){
            x.statusId = 24;
          }
          if(x.refDocNumber && x.referenceField5 == null){
            x.statusId = 11;
          }
        })
        this.dataSource = new MatTableDataSource<any>(res);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;

        console.log( this.dataSource.data.length)
        this.dataSource.paginator = this.paginator;
        this.totalRecords =   this.dataSource.data.length;
        this.dataSource.sort = this.sort;
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
      this.dataSource.data.forEach(x => {
        res.push({
          'Warehouse Id': x.warehouseId,
          "Status": this.cs.getstatus_text(x.statusId),
          'Container No': x.containerNo,
          "Receipt No ": x.containerReceiptNo,
          "Order No ": x.refDocNumber,
          "Size ": x.containerType,
          "Origin ": x.origin,
          'Supplier': x.partnerCode,
          "Created By  ": x.createdBy,
          " Container Unloaded On" : this.cs.dateExcel(x.referenceField2),
          'Actual Receipt Date': this.cs.dateExcel(x.referenceField5),
          // 'Created By': x.createdBy,
          // 'Created On': this.cs.dateapi(x.createdOn),
        });
  
      })
      this.cs.exportAsExcel(res, "Conatiner Receipt");
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
