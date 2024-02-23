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
import { ApprovalidService } from '../approvalid/approvalid.service';
import { AisleNewComponent } from './aisle-new/aisle-new.component';
import { AisleService } from './aisle.service';

@Component({
  selector: 'app-aisle',
  templateUrl: './aisle.component.html',
  styleUrls: ['./aisle.component.scss']
})
export class AisleComponent implements OnInit {
  screenid = 3088;
  advanceFilterShow: boolean;
  @ViewChild('Setupaisle') Setupaisle: Table | undefined;
  aisels: any;
  selectedaisle : any;

  displayedColumns: string[] = ['select', 'floorId', 'description','warehouseId', 'createdBy','createdOn', ];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  ELEMENT_DATA: any[] = [];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);
  
  constructor(public dialog: MatDialog,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,
    private service: AisleService) { }
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
      this.Setupaisle!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
    ngOnInit(): void {
      this.getAll(

      );
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
    console.log(this.selectedaisle)
    if (data != 'New')
    if (this.selectedaisle.length === 0) {
      this.toastr.warning("Kindly select any Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(AisleNewComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? this.selectedaisle[0].aisleId : null, floorId: data != 'New' ? this.selectedaisle[0].floorId : null,storageSectionId: data != 'New' ? this.selectedaisle[0].storageSectionId : null,warehouseId: data != 'New' ? this.selectedaisle[0].warehouseId : null,languageId: data != 'New' ? this.selectedaisle[0].languageId : null,plantId: data != 'New' ? this.selectedaisle[0].plantId : null,companyCodeId: data != 'New' ? this.selectedaisle[0].companyCodeId : null}
    });
  
    dialogRef.afterClosed().subscribe(result => {
      this.getAll();
    });
  }
//   getAll() {
//     this.spin.show();
//     this.sub.add(this.service.search({}).subscribe((res: any[]) => {
//       console.log(res)
// if(res){
//    this.aisels = res;
// //   this.dataSource = new MatTableDataSource<any>(res);
// //   this.selection = new SelectionModel<any>(true, []);
// //   this.dataSource.sort = this.sort;
// //  this.dataSource.paginator = this.paginator;
// }
//       this.spin.hide();
//     }, err => {
//       this.cs.commonerrorNew(err);
//       this.spin.hide();
//     }));
//   }
  getAll() {
    if(this.auth.userTypeId == 1){
      this.superAdmin()
    }else{
      this.adminUser()
    }
  }
  
  adminUser(){
    let obj: any = {};
    obj.companyCodeId = this.auth.companyId;
    obj.plantId = this.auth.plantId;
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = this.auth.warehouseId;
  
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    console.log(res)
    if(res){
      this.aisels=res;
  
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
        this.aisels=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedaisle.length === 0) {
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
      data: this.selectedaisle[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedaisle[0].aisleId,this.selectedaisle[0].warehouseId,this.selectedaisle[0].languageId,this.selectedaisle[0].companyCodeId,this.selectedaisle[0].plantId,this.selectedaisle[0].floorId,this.selectedaisle[0].storageSectionId);
  
      }
    });
  }
  
  
  deleterecord(id: any,warehouseId:any,companyCodeId:any,plantId:any,languageId:any,floorId:any,storageSectionId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,warehouseId,companyCodeId,plantId,languageId,floorId,storageSectionId).subscribe((res) => {
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
    this.aisels.forEach(x => {
      res.push({
        "Language Id":x.languageId,
        "Company ":x.companyIdAndDescription,
        "Plant":x.plantIdAndDescription,
        "Warehouse ":x.warehouseIdAndDescription,
        "Floor":x.floorIdAndDescription,
        "Storage Section":x.storageSectionIdDescription,
        "Aisle ID":x.aisleId,
       "Aisle Description":x.aisleDescription,
      
       "Created By":x.createdBy,
       "Created One":this.cs.dateapi(x.createdOn),
      
      });
  
    })
    this.cs.exportAsExcel(res, "Approval");
  }
  onChange() {
    console.log(this.selectedaisle.length)
    const choosen= this.selectedaisle[this.selectedaisle.length - 1];   
    this.selectedaisle.length = 0;
    this.selectedaisle.push(choosen);
  }
  }
   
  
  
  



