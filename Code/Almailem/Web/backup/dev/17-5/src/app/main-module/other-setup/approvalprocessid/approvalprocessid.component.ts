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
import { ApprovalprocesidService } from './approvalprocesid.service';
import { ApprovalprocessidNewComponent } from './approvalprocessid-new/approvalprocessid-new.component';

@Component({
  selector: 'app-approvalprocessid',
  templateUrl: './approvalprocessid.component.html',
  styleUrls: ['./approvalprocessid.component.scss']
})
export class ApprovalprocessidComponent implements OnInit {
  screenid = 3103;
  advanceFilterShow: boolean;
  @ViewChild('Setupapprovalprocess') Setupapprovalprocess: Table | undefined;
  approvalsprocess: any;
  selectedapprovalsprocess : any;

  displayedColumns: string[] = ['select','movementTypeId','movementTypeText', 'createdBy','createdOn'];
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
    private service: ApprovalprocesidService ) { }
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
      this.selectedapprovalsprocess!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
      console.log(this.selectedapprovalsprocess)
        if (data != 'New')
        if (this.selectedapprovalsprocess.length === 0) {
          this.toastr.warning("Kindly select any Row", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          return;
        }
        const dialogRef = this.dialog.open(ApprovalprocessidNewComponent, {
          disableClose: true,
          width: '55%',
          maxWidth: '80%',
          data: { pageflow: data, code: data != 'New' ? this.selectedapprovalsprocess[0].approvalProcessId : null,warehouseId: data != 'New' ? this.selectedapprovalsprocess[0].warehouseId : null,languageId: data != 'New' ? this.selectedapprovalsprocess[0].languageId : null,plantId: data != 'New' ? this.selectedapprovalsprocess[0].plantId : null,companyCodeId: data != 'New' ? this.selectedapprovalsprocess[0].companyCodeId : null}
        });
      
        dialogRef.afterClosed().subscribe(result => {
          this.getAll();
        });
     }
 getAll() {
    this.spin.show();
    this.sub.add(this.service.search({}).subscribe((res: any[]) => {
      console.log(res)
if(res){
   this.approvalsprocess = res;
//   this.dataSource = new MatTableDataSource<any>(res);
//   this.selection = new SelectionModel<any>(true, []);
//   this.dataSource.sort = this.sort;
//  this.dataSource.paginator = this.paginator;
}
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedapprovalsprocess.length === 0) {
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
      data: this.selectedapprovalsprocess[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedapprovalsprocess[0].approvalProcessId,this.selectedapprovalsprocess[0].warehouseId,this.selectedapprovalsprocess[0].languageId,this.selectedapprovalsprocess[0].plantId,this.selectedapprovalsprocess[0].companyCodeId);
  
      }
    });
  }
  
  
  deleterecord(id: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,warehouseId,languageId,plantId,companyCodeId).subscribe((res) => {
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
    this.approvalsprocess.forEach(x => {
      res.push({
        "Language Id":x.languageId,
        "Company ":x.companyIdAndDescription,
        "Plant ":x.plantIdAndDescription,
        "Warehouse ":x.warehouseIdAndDescription,
        "Approval Process ID":x.approvalProcessId,
       "Approval Process ":x.approvalProcess,
       "Created By":x.createdBy,
       "Created One":this.cs.dateapi(x.createdOn),
      
      });
  
    })
    this.cs.exportAsExcel(res, "Approval Process Id");
  }
  onChange() {
    console.log(this.selectedapprovalsprocess.length)
    const choosen= this.selectedapprovalsprocess[this.selectedapprovalsprocess.length - 1];   
    this.selectedapprovalsprocess.length = 0;
    this.selectedapprovalsprocess.push(choosen);
  } 
  }
   
  
  
  





