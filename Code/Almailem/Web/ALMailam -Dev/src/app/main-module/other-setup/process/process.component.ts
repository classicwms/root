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
import { ProcessNewComponent } from './process-new/process-new.component';
import { ProcessService } from './process.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-process',
  templateUrl: './process.component.html',
  styleUrls: ['./process.component.scss']
})
export class ProcessComponent implements OnInit {
  screenid=3144;
  advanceFilterShow: boolean;
  @ViewChild('Setupprocess') Setupprocess: Table | undefined;
  processseq: any;
  selectedprocess : any[];

  displayedColumns: string[] = ['select', 'companyCodeId','plantId','processDescription','processId','subLevelId','subProcessDescription','warehouseId', 'createdBy', 'createdOn'];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
   private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,
    private service: ProcessService ) { }
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
    this.Setupprocess!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.getAll();
    this.getallfilter();
  }
  multilanguageList: any[] = [];
  multiyseridList: any[] = [];
  multicountryList:any[]=[];
      multiselectyseridList: any[] = [];
      searhform = this.fb.group({
        companyCodeId: [this.auth.companyId],
        plantId: [this.auth.plantId],
        warehouseId:[this.auth.warehouseId],
        processId:[],
        processSequenceId:[],
        startCreatedOn:[],
        endCreatedOn:[],
        languageId: [[this.auth.languageId]],
         createdBy:[],
      });
      search() {
        this.spin.show();
        this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
          console.log(res);
          this.processseq= res;
         
          
          this.spin.hide();
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
      }
      multiplantList:any[]=[];
      multicompanyList:any[]=[];
      multiwarehouseList:any[]=[];
      multidockList:any[]=[];
      multiprocessList:any[]=[];
      getallfilter() {
        this.multilanguageList = [];
        this.multiplantList = [];
        this.multicompanyList=[];
        this.multiwarehouseList=[];
        this.multidockList=[];
        this.multiprocessList=[];
        let obj: any = {};
        obj.languageId=[this.auth.languageId];
        obj.companyCodeId=this.auth.companyId;
        obj.plantId=this.auth.plantId;
        obj.warehouseId=this.auth.warehouseId;
        this.spin.show();
        this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
             this.dataSource = new MatTableDataSource < any >(res);
            this.spin.hide();
            res.forEach((x: { languageId: string }) => {
                this.multilanguageList.push({
                  value: x.languageId,
                  label: x.languageId,
                });
            });
            this.multilanguageList = this.cas.removeDuplicatesFromArrayNew(this.multilanguageList);
            res.forEach((x: {
              createdBy: string;
            }) => this.multiyseridList.push({
              value: x.createdBy,
              label: x.createdBy
            }))
         
          res.forEach((x: { companyCodeId: string; companyIdAndDescription: string, }) => {
            this.multicompanyList.push({
              value: x.companyCodeId,
              label: x.companyCodeId + '-' + x.companyIdAndDescription,
            });
        });
        res.forEach((x: { plantId: string; plantIdAndDescription: string, }) => {
          this.multiplantList.push({
            value: x.plantId,
            label: x.plantId + '-' + x.plantIdAndDescription,
          });
      });
      res.forEach((x: { warehouseId: string; warehouseIdAndDescription: string, }) => {
        this.multiwarehouseList.push({
          value: x.warehouseId,
          label: x.warehouseId + '-' + x.warehouseIdAndDescription,
        });
    });
    res.forEach((x: { processId: string; processIdAndDescription: string, }) => {
      this.multidockList.push({
        value: x.processId,
        label: x.processId + '-' + x.processIdAndDescription,
      });
  });
  res.forEach((x: { processSequenceId: string; processDescription: string, }) => {
    this.multiprocessList.push({
      value: x.processSequenceId,
      label: x.processSequenceId + '-' + x.processDescription,
    });
});
            this.multiselectyseridList = this.multiyseridList;
            this.multiselectyseridList = this.cas.removeDuplicatesFromArrayNew(this.multiyseridList);
            this.multiplantList = this.cas.removeDuplicatesFromArrayNew(this.multiplantList);
            this.multicompanyList = this.cas.removeDuplicatesFromArrayNew(this.multicompanyList);
            this.multiwarehouseList = this.cas.removeDuplicatesFromArrayNew(this.multiwarehouseList);
            this.multidockList = this.cas.removeDuplicatesFromArrayNew(this.multidockList);
            this.dataSource.sort = this.sort;
            this.dataSource.paginator = this.paginator;
          }
          , err => {
            this.cs.commonerrorNew(err);
            this.spin.hide();
          }));
      } 
      reload() {
        window.location.reload();
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
console.log(this.selectedprocess)
  if (data != 'New')
  if (this.selectedprocess.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(ProcessNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selectedprocess[0].processSequenceId : null,processId: data != 'New' ? this.selectedprocess[0].processId : null,warehouseId: data != 'New' ? this.selectedprocess[0].warehouseId : null,languageId: data != 'New' ? this.selectedprocess[0].languageId : null,companyCodeId: data != 'New' ? this.selectedprocess[0].companyCodeId : null,plantId: data != 'New' ? this.selectedprocess[0].plantId : null,}
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
  obj.companyCodeId = this.auth.companyId;
  obj.plantId = this.auth.plantId;
  obj.languageId = [this.auth.languageId];
  obj.warehouseId = this.auth.warehouseId;
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    console.log(res)
if(res){
 this.processseq = res;

}
    this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
superAdmin(){
  this.spin.show();
  this.sub.add(this.service.Getall().subscribe((res: any[]) => {
    if(res){
      this.processseq=res;
     } this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
deleteDialog() {
  if (this.selectedprocess.length === 0) {
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
    data: this.selectedprocess[0].code,
  });

  dialogRef.afterClosed().subscribe(result => {

    if (result) {
      this.deleterecord(this.selectedprocess[0].processSequenceId,this.selectedprocess[0].warehouseId,this.selectedprocess[0].languageId,this.selectedprocess[0].companyCodeId,this.selectedprocess[0].plantId,this.selectedprocess[0].processId);

    }
  });
}


deleterecord(id: any,warehouseId:any,languageId:any,companyCodeId:any,plantId:any,processId:any) {
  this.spin.show();
  this.sub.add(this.service.Delete(id,warehouseId,languageId,companyCodeId,plantId,processId).subscribe((res) => {
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
  this.processseq.forEach(x => {
    res.push({
      "Language Id":x.languageId,
      "Company ":x.companyIdAndDescription,
      "Plant ":x.plantIdAndDescription,
      "Warehouse ":x.warehouseIdAndDescription,
      "Process Sequence": x.processSequenceId,
      " Description ": x.processDescription,
      "Created By": x.createdBy,
      "Created On": this.cs.dateapi(x.createdOn),
    });

  })
  this.cs.exportAsExcel(res, "Process Sequence");
}
onChange() {
  console.log(this.selectedprocess.length)
  const choosen= this.selectedprocess[this.selectedprocess.length - 1];   
  this.selectedprocess.length = 0;
  this.selectedprocess.push(choosen);
} 
}
 