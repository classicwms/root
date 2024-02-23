
import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
import { ContainerReceiptService } from '../../Inbound/Container-receipt/container-receipt.service';
import { variant } from '../../Inbound/preinbound/goodreceipt-create/variant/variant.component';
import { ReportsService } from '../reports.service';
import { stockElement, StocksampleService } from '../stocksamplereport/stocksample.service';
import { InboundLinesComponent } from './inbound-lines/inbound-lines.component';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-inbound-status',
  templateUrl: './inbound-status.component.html',
  styleUrls: ['./inbound-status.component.scss']
})
export class InboundStatusComponent implements OnInit {
screenid=3172;
inbound: any[] = [];
selectedinbound : any[] = [];
@ViewChild('inboundTag') inboundTag: Table | any;
form = this.fb.group({
  containerNo: [],
  endConfirmedOn: [],
  endCreatedOn: [],
  inboundOrderTypeId: [],
  refDocNumber: [],
  startConfirmedOn: [],
  startCreatedOn: [],
  statusId: [],
  warehouseId: [[this.auth.warehouseId]],
  companyCode: [[this.auth.companyId]],
  languageId: [[this.auth.languageId]],
  plantId: [[this.auth.plantId]],
});

isShowDiv = false;
table = true;
fullscreen = false;
search = true;
back = false;
div1Function() {
  this.table = !this.table;
}
showFloatingButtons: any;
toggle = true;
public icon = 'expand_more';
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
showFiller = false;

displayedColumns: string[] = ['statusId','docLines','refDocNumber','inboundOrderTypeId', 'containerNo','createdOn', 'confirmedOn', 'refdocno', 'confirmedBy'];
sub = new Subscription();
ELEMENT_DATA: variant[] = [];


statusIdList1: any[] = [];

constructor(
  private router: Router,
  private spin: NgxSpinnerService,
  public toastr: ToastrService,
  private fb: FormBuilder,
  public cs: CommonService,
  public dialog: MatDialog, 
  private service: ReportsService,
  private containerservice: ContainerReceiptService,
  private reportService: ReportsService,
  private masterService: MasterService,
    public auth: AuthService ,
  ) { 
    this.statusIdList1 = [{
      label: 'Receipt Confirmed',
      value: 24
    },
    {
      label: 'Receipt In Progress',
      value: 5
    },

  ];
  }
animal: string | undefined;

ngOnInit(): void {
  this.dropdownfill();
  this.containerget();
  this.getDropdown();
  this.form.controls.companyCode.patchValue(this.auth.companyId);
  this.form.controls.plantId.patchValue(this.auth.plantId);
  this.form.controls.plantId.disable();
  this.form.controls.companyCode.disable();
 }
 warehouseList: any[] = [];
 selectedWarehouseList: any[] = [];
 selectedItems: any[] = [];
 multiselectWarehouseList: any[] = [];
 multiWarehouseList: any[] = [];
 CustomeridList : any[]=[];

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
      this.multiselectWarehouseList = this.cs.removeDuplicatesFromArrayNewstatus(this.multiselectWarehouseList)
    }, (err) => {
      this.toastr.error(
        err,
        ""
      );
    });
  this.spin.hide();

}

multiOrderNo: any[] = [];
  getDropdown(){
    this.sub.add(this.reportService.getInboundStatusReportv2({warehouseId : [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId]}).subscribe(res => {
      res.forEach((x: any) => this.multiOrderNo.push({ value: x.refDocNumber, label: x.refDocNumber }));
      res.forEach((x: { containerNo: string; }) => {
        // Check if 'containerNo' is not null before pushing to 'containerNoList'.
          this.containerNoList.push({ value: x.containerNo, label: x.containerNo });
          this.containerNoList=this.cs.removeDuplicatesFromArrayNewstatus(this.containerNoList);
          console.log(this.containerNoList);
        
     
      });
     
    }))
  
  }










// filtersearch() {
//   //  this.spin.show();
//     this.table = true;
//     this.search = true;
//     this.fullscreen = true;


// }
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











/** The label for the checkbox on the passed row */
// checkboxLabel(row?: variant): string {
//   if (!row) {
//     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
//   }
//   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.warehouseno + 1}`;
// }
containerNoListselected: any[] = [];
containerNoList: any[] = [];

containerReceiptNoListselected: any[] = [];
containerReceiptNoList: any[] = [];

partnerCodeListselected: any[] = [];
partnerCodeList: any[] = [];

statusIdListselected: any[] = [];
statusIdList: any[] = [];
containerget(){
  this.sub.add(this.containerservice.Getall().subscribe(res => {

    
    res.forEach((x: { containerNo: string; }) => {
      // Check if 'containerNo' is not null before pushing to 'containerNoList'.
      if (x.containerNo !== null) {
        this.containerNoList.push({ value: x.containerNo, label: x.containerNo });
      }
    });
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


onItemSelect(item: any) {
  console.log(item);
}

onSelectAll(items: any) {
  console.log(items);
}
submitted = false;
filtersearch() {
 
  this.submitted = true;
  if (this.form.invalid) {
    this.toastr.error(
      "Please fill the required fields to continue",
      "Notification", {
      timeOut: 2000,
      progressBar: false,
    }
    );

    this.cs.notifyOther(true);
    return;
  }
 
  this.form.controls.endCreatedOn.patchValue(this.cs.day_callapi(this.form.controls.endCreatedOn.value));
  this.form.controls.startCreatedOn.patchValue(this.cs.day_callapi(this.form.controls.startCreatedOn.value));
  this.spin.show();
  let obj: any = {};
  obj.companyCodeId = [this.auth.companyId];
  obj.languageId = [this.auth.languageId];
  obj.plantId = [this.auth.plantId];
  obj.warehouseId = [this.auth.warehouseId];
  obj.statusId = this.form.controls.statusId.value;
  obj.containerNo = this.form.controls.containerNo.value;
  obj.refDocNumber=this.form.controls.refDocNumber.value;
  obj.startCreatedOn=this.form.controls.startCreatedOn.value;
  obj.endCreatedOn=this.form.controls.endCreatedOn.value;
  this.sub.add(this.service.getInboundStatusReportv2(obj).subscribe(res => {
    this.inbound = res;
    this.spin.hide()
   

    this.spin.hide();
    this.table = true;
    this.search = false;
    this.fullscreen = false;
    this.back = true;
  },
    err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
}


reset(){
  this.form.reset();
  this.form.controls.companyCode.patchValue([this.auth.companyId])
  this.form.controls.languageId.patchValue([this.auth.languageId])
  this.form.controls.plantId.patchValue([this.auth.plantId])
  this.form.controls.warehouseId.patchValue([this.auth.warehouseId])
this.form.controls.startCreatedOn.patchValue("");
this.form.controls.endCreatedOn.patchValue("");
}
downloadexcel() {
  var res: any = [];
  this.inbound.forEach(x => {
    res.push({
      "Company":x.companyDescription,
      "Plant":x.plantDescription,
      "Warehouse":x.warehouseDescription,
      "Status": x.statusDescription,
      "Order Lines":x.countOfOrderLines,
      "Order No ": x.refDocNumber,
      "Order Type": x.referenceDocumentType,
      "Received Lines":x.receivedLines,
      'Order Date':this.cs.dateapi(x.createdOn) ,
      'Receipt Confirmation Date':this.cs.dateapi(x.confirmedOn),
      "Lead Time":x.confirmedOn != null ? this.cs.getDataDiff(x.createdOn,x.confirmedOn) : '',
      "Confirmed By": x.confirmedBy,
    });

  })
  this.cs.exportAsExcel(res,"Inbound Order Summary Report");
}

lines(res){
  console.log(res)
  const dialogRef = this.dialog.open(InboundLinesComponent, {
    //width: '55%',
    maxWidth: '90%',
    position: { top: '8.5%' },
    data: res 
  });

  dialogRef.afterClosed().subscribe(result => {
    console.log('The dialog was closed');
    // this.animal = result;
  });
}

}

