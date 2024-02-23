import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PlantService } from '../../setup-organisation/plant/plant.service';
import { InvoiceNewComponent } from '../../three-pl/invoice/invoice-new/invoice-new.component';
import { ConsignmentsEditComponent } from './consignments-edit/consignments-edit.component';
import { ConsignmentsOpenComponent } from './consignments-open/consignments-open.component';
import { DeliveryService } from '../delivery.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-consignments',
  templateUrl: './consignments.component.html',
  styleUrls: ['./consignments.component.scss']
})
export class ConsignmentsComponent implements OnInit {
screenid=3209;
  
advanceFilterShow: boolean;
@ViewChild('Setupinvoice') Setupinvoice: Table | undefined;
invoices: any;
selectedinvoice : any;
displayedColumns: string[] = ['select','companyCodeId', 'description', 'plantId','createdBy', 'invoiceDate', ];
sub = new Subscription();
isShowDiv = false;
showFloatingButtons: any;
toggle = true;
public icon = 'expand_more';
constructor(public dialog: MatDialog,
 // private cas: CommonApiService,
  public toastr: ToastrService,
  private spin: NgxSpinnerService,
  public cs: CommonService,
 // private excel: ExcelService,
  private fb: FormBuilder,
  private auth: AuthService,
  private router: Router,
  private service: DeliveryService, ) { }
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
animal: string | undefined;
applyFilter(event: Event) {
  const filterValue = (event.target as HTMLInputElement).value;

  this.dataSource.filter = filterValue.trim().toLowerCase();

  if (this.dataSource.paginator) {
    this.dataSource.paginator.firstPage();
  }
}
applyFilterGlobal($event: any, stringVal: any) {
  this.Setupinvoice!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
}
ngOnInit(): void {
  this.getAll();
}
ELEMENT_DATA: any[] = [];
dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
selection = new SelectionModel<any>(true, []);

downloadexcel() {
  var res: any = [];
  this.invoices.forEach(x => {
    res.push({
      "Status":x.statusDescription,
      "Company":x.companyDescription,
      "Branch":x.plantDescription,
      "Warehouse ": x.warehouseDescription,
      'Order No': x.refDocNumber,
      "Invoice No":x.salesInvoiceNumber,
      'Order Type': x.referenceDocumentType,
      "Customer Code": x.customerId,
      "Address": x.address,
      "Target Branch":x.targetBranchCode,
      "Contact Number":x.phoneNumber,
      "RFD Date": this.cs.dateExcel(x.requiredDeliveryDate),
      "Count Of Items":x.referenceField8,
      "Invoice Qty":x.referenceField2,
     "Created On":this.cs.dateExcel(x.invoiceDate),

      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.invoiceDate),
    });
  
  })
  this.cs.exportAsExcel(res, "Consignments");
}


@ViewChild(MatSort, { static: true })
sort!: MatSort;
@ViewChild(MatPaginator, { static: true })
paginator!: MatPaginator; // Pagination
// Pagination
warehouseId = this.auth.warehouseId;






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
  return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.handlingUnit + 1}`;
}






clearselection(row: any) {

  this.selection.clear();
  this.selection.toggle(row);
}
openDialog(data: any = 'New'): void {
  if (data != 'New')
  if (this.selectedinvoice.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(ConsignmentsOpenComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selectedinvoice[0].plantId : null}
    
  });
console.log(data);
  dialogRef.afterClosed().subscribe(result => {
    this.getAll();
  });
}
getAll() {
this.spin.show();
let obj: any = {};
//obj.companyCodeId = [this.auth.companyId];
obj.languageId = [this.auth.languageId];
//obj.plantId = [this.auth.plantId];
obj.warehouseId = [this.auth.warehouseId];
obj.statusId=[59];
this.sub.add(this.service.searchV2(obj).subscribe((res: any[]) => {
  console.log(res)
  if(res){
    this.invoices = res.filter(item => item.salesInvoiceNumber != null);
//   this.dataSource = new MatTableDataSource<any>(res);
//   this.selection = new SelectionModel<any>(true, []);
//   this.dataSource.sort = this.sort;
//  this.dataSource.paginator = this.paginator;
  }this.spin.hide();
}, err => {
  this.cs.commonerrorNew(err);
  this.spin.hide();
}));
}

deleteDialog() {
if (this.selectedinvoice.length === 0) {
  this.toastr.error("Kindly select any row", "Notification",{
    timeOut: 2000,
    progressBar: false,
  });
  return;
}
const dialogRef = this.dialog.open(DeleteComponent, {
  disableClose: true,
  width: '40%',
  maxWidth: '80%',
  position: { top: '9%', },
  data: this.selectedinvoice[0].plantId,
});

dialogRef.afterClosed().subscribe(result => {

  if (result) {
    this.deleterecord(this.selectedinvoice[0]);

  }
});
}


deleterecord(id: any) {
this.spin.show();
this.sub.add(this.service.Delete(id).subscribe((res) => {
  this.toastr.success(id + " Deleted successfully.","Notification",{
    timeOut: 2000,
    progressBar: false,
  });
  this.spin.hide();
  this.getAll();
}, err => {
  this.cs.commonerrorNew(err);
  this.spin.hide();
}));
}
// downloadexcel() {
// var res: any = [];
// this.invoices.forEach(x => {
//   res.push({
//     "Bill No":x.billno,
//     "Proforma No": x.proformano,
//     " Customer ID ": x.customerid,
//     "Bill Date ":this.cs.dateapiutc0(x.billdate),
//     "	Bill Period ": x.billperiod,
//     "Bill Amount":x.billamount,
//     "Created By":x.createdby,
//     "Status":x.status,
//   });

// })
// this.cs.exportAsExcel(res, "Invoice");
// }
selectedRefDocNumberList: any[] = [];
createManifest(data: any = 'New', linedata: any = null): void {  
  this.selectedRefDocNumberList = [];
  this.selectedinvoice.filter(x => this.selectedRefDocNumberList.push(x.refDocNumber))
  let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedinvoice : linedata, selectedRefDocNumber: this.selectedRefDocNumberList, pageflow:  data });
  this.router.navigate(['/main/delivery/consignmentEdit/' + paramdata]);
  console.log(linedata);
}

}




