import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PickingService } from '../../Outbound/picking/picking.service';
import { ReportsService } from '../reports.service';
import { StocksampleService } from '../stocksamplereport/stocksample.service';
import { PickingproductivitylinesComponent } from './pickingproductivitylines/pickingproductivitylines.component';


@Component({
  selector: 'app-pickingproductivity',
  templateUrl: './pickingproductivity.component.html',
  styleUrls: ['./pickingproductivity.component.scss']
})
export class PickingproductivityComponent implements OnInit {
  screenid=3217;
  hhtPicker: any[] = [];
  selectedinbound : any[] = [];
  @ViewChild('hhtPickupTag') hhtPickupTag: Table | any;
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
    assignedPickerId:[],
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
    console.log('Data type:', typeof this.hhtPicker);
  console.log('Data:', this.hhtPicker);
    this.getDropdown();
    this.selectedCompany=this.auth.companyIdAndDescription;
    this.selectedplant=this.auth.plantIdAndDescription;
    this.selectedwarehouse=this.auth.warehouseIdAndDescription;
  }
 
  // ngAfterViewInit() {
  //   this.dataSource.paginator = this.paginator;
  // }
 
 
  
  multipickerId: any[] = [];
  getDropdown(){
    this.sub.add(this.reportService.pickupSpark({warehouseId : [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId]}).subscribe(res => {
      res.forEach((x: any) => this.multipickerId.push({ value: x.assignedPickerId, label: x.assignedPickerId }));
      this.multipickerId=this.cs.removeDuplicatesFromArrayNewstatus(this.multipickerId);
    }))
  }
 

 
  reset(){
    this.searhform.reset();
    this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
    this.searhform.controls.companyCodeId.patchValue([this.auth.companyId]);
    this.searhform.controls.plantId.patchValue([this.auth.plantId]);
    this.searhform.controls.languageId.patchValue([this.auth.languageId]);
   // this.searhform.controls.statusId.patchValue([50, 59]);
  }
  
  filtersearch(){
   
  

    this.spin.show();
    let obj: any = {};

   
    this.searhform.controls.fromPickConfirmedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.fromPickConfirmedOn.value));
        this.searhform.controls.toPickConfirmedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.toPickConfirmedOn.value));
    obj.warehouseId = [this.auth.warehouseId]
    obj.companyCode = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
   // obj.statusId = this.statusId

    this.sub.add(this.pickingService.pickuplineSpark(this.searhform.value).subscribe(res => {
     
 //     let result = res.filter((x: any) => x.statusId == 50);
      this.hhtPicker =res;
   
      this.table = true;
      this.search = false;
      this.fullscreen = false;
      this.back = true;
      this.spin.hide();
      this.totalRecords = this.hhtPicker.length
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
  }

  getCount(){
    this.totalRecords=this.hhtPicker.length;
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


  lines(res) {
    console.log(res);
  
    const dialogRef = this.dialog.open(PickingproductivitylinesComponent, {
      width: '37%',
      maxWidth: '90%',
      position: { top: '8.5%' },
      data: res, 
    });
  
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      // this.animal = result;
    });
  }
  getProductivity() {
    let total = 0;

    this.hhtPicker.forEach(element => {
        // Convert the string to a number using parseFloat or parseInt
        const fieldValue = parseFloat(element.referenceField1);

        // Check if fieldValue is a valid number
        if (!isNaN(fieldValue) && fieldValue > 0) {
            total += fieldValue;
        }
    });

    let percentage = (total > 0) ? (this.hhtPicker.length / total) : 0;
  
    return percentage;
}

getavgLeadTime() {
  let total = 0;

  this.hhtPicker.forEach(element => {
      const fieldValue = parseFloat(element.referenceField1);
      if (!isNaN(fieldValue) && fieldValue > 0) {
          total += fieldValue;
      }
  });

  let averageLeadTime = (this.hhtPicker.length > 0) ? total / this.hhtPicker.length : 0;

  averageLeadTime = parseFloat(averageLeadTime.toFixed(2));

  return averageLeadTime;
}

  totalRecords = 0;
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.hhtPicker.forEach(x => {
      res.push({
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Picker  ": (x.assignedPickerId),
        "Order No  ": x.refDocNumber,
        'Pickup Qty': x.pickConfirmQty,
        'Lead Time': x.referenceField1,
        "Assigned On": this.cs.dateapi(x.pickupCreatedOn),
        "Picked On": this.cs.dateapi(x.pickupConfirmedOn),

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
 

    this.cs.exportAsExcel(res, "Picking Productivity Report");
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


}

