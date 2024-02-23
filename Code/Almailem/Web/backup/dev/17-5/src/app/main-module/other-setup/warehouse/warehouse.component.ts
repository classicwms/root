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
import { VerticalNewComponent } from '../vertical/vertical-new/vertical-new.component';
import { WarehouseNewComponent } from './warehouse-new/warehouse-new.component';
import { WarehouseService } from './warehouse.service';

@Component({
  selector: 'app-warehouse',
  templateUrl: './warehouse.component.html',
  styleUrls: ['./warehouse.component.scss']
})
export class WarehouseComponent implements OnInit {
screenid=3083;
  advanceFilterShow: boolean;
  @ViewChild('Setupwarehouse') Setupwarehouse: Table | undefined;
  warehousess: any;
  selectedwarehouse : any;

  displayedColumns: string[] = ['select','companyCode', 'warehouseDesc','plantId','warehouseId','createdBy', 'createdOn',];
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
    private service: WarehouseService) { }
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
    this.Setupwarehouse!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  ngOnInit(): void {
    this.getAll();
  }
  ELEMENT_DATA: any[] = [];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);




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
console.log(this.selectedwarehouse)
  if (data != 'New')
  if (this.selectedwarehouse.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(WarehouseNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selectedwarehouse[0].warehouseId : null,plantId: data != 'New' ? this.selectedwarehouse[0].plantId : null,companyCodeId: data != 'New' ? this.selectedwarehouse[0].companyCodeId : null,languageId: data != 'New' ? this.selectedwarehouse[0].languageId : null}
  });

  dialogRef.afterClosed().subscribe(result => {
    this.getAll();
  });
}

getAll() {
  if(this.auth.userTypeId == 1){
    this.superAdmin()
  }else{
    this.adminUser()
  }
}

adminUser(){
  let obj: any = {};
  obj.languageId = [this.auth.languageId];
  obj.companyCodeId=this.auth.companyId;
  obj.plantId=this.auth.plantId;
this.spin.show();
this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
  console.log(res)
  if(res){
    this.warehousess=res;
  }this.spin.hide();
}, err => {
  this.cs.commonerrorNew(err);
  this.spin.hide();
}));
}
superAdmin(){
  this.spin.show();
  this.sub.add(this.service.Getall().subscribe((res: any[]) => {
    if(res){
      this.warehousess=res;
     } this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}

deleteDialog() {
  if (this.selectedwarehouse.length === 0) {
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
    data: this.selectedwarehouse[0].code,
  });

  dialogRef.afterClosed().subscribe(result => {

    if (result) {
      this.deleterecord(this.selectedwarehouse[0].warehouseId,this.selectedwarehouse[0].plantId,this.selectedwarehouse[0].companyCodeId,this.selectedwarehouse[0].languageId);

    }
  });
}


deleterecord(id: any,plantId:any,companyCodeId:any,languageId:any) {
  this.spin.show();
  this.sub.add(this.service.Delete(id,plantId,companyCodeId,languageId).subscribe((res) => {
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
downloadexcel() {
  var res: any = [];
  this.warehousess.forEach(x => {
    res.push({
      "Language Id":x.languageId,
      "Company Details":x.companyIdAndDescription,
      "Plant Details":x.plantIdAndDescription,
      "Warehouse Id	": x.warehouseId,
      ' Descripton	': x.warehouseDesc,
      "Created By": x.createdBy,
      "Created On": this.cs.dateapiutc0(x.createdOn),
    });

  })
  this.cs.exportAsExcel(res, "Warehouse");
}
onChange() {
  console.log(this.selectedwarehouse.length)
  const choosen= this.selectedwarehouse[this.selectedwarehouse.length - 1];   
  this.selectedwarehouse.length = 0;
  this.selectedwarehouse.push(choosen);
} 
}
 
