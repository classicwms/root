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
import { CompanyNewComponent } from './company-new/company-new.component';
import { CompanyService } from './company.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.scss']
})
export class CompanyComponent implements OnInit {
  screenid=3082;
  advanceFilterShow: boolean;
  @ViewChild('Setupcompany') Setupcompany: Table | undefined;
  companys: any;
  selectedcompany : any;
  displayedColumns: string[] = ['select','companyCodeId', 'description','createdBy', 'createdOn',];
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
    public auth: AuthService,
    private service: CompanyService, ) { }
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
    this.Setupcompany!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    console.log(this.RA)
    this.getAll();
    this.getallfilter();
  }
  multilanguageList: any[] = [];
  multiyseridList: any[] = [];
  multicountryList:any[]=[];
      multiselectyseridList: any[] = [];
      searhform = this.fb.group({
  companyCodeId:[],
languageId: [[this.auth.languageId]],
startCreatedOn:[],
    endCreatedOn:[],
      });
      search() {
        this.spin.show();
        this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
          console.log(res);
          this.companys = res;
         
          
          this.spin.hide();
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
      }
    

  getallfilter() {
    this.multilanguageList = [];
    this.multicountryList = [];
    let obj: any = {};
    obj.languageId=[this.auth.languageId];
    obj.companyCodeId=[this.auth.companyId];
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
        res.forEach((x: { companyCodeId: string; description: string, }) => {
          this.multicountryList.push({
            value: x.companyCodeId,
            label: x.companyCodeId + '-' + x.description,
          });
      });
        this.multiselectyseridList = this.multiyseridList;
        this.multiselectyseridList = this.cas.removeDuplicatesFromArrayNew(this.multiyseridList);
        this.multicountryList = this.cas.removeDuplicatesFromArrayNew(this.multicountryList);
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

  if (data != 'New')
  if (this.selectedcompany.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(CompanyNewComponent, {
    disableClose: true,
    width: '45%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selectedcompany[0].companyCodeId : null,languageId: data != 'New' ? this.selectedcompany[0].languageId : null}
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
  obj.companyCodeId = [this.auth.companyId];

  obj.languageId = [this.auth.languageId];
  
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    if(res){
      this.companys=res;
     } this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
superAdmin(){
  this.spin.show();
  this.sub.add(this.service.Getall().subscribe((res: any[]) => {
    if(res){
      this.companys=res;
     } this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
deleteDialog() {
  if (this.selectedcompany.length === 0) {
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
    data: this.selectedcompany[0].code,
  });

  dialogRef.afterClosed().subscribe(result => {

    if (result) {
      this.deleterecord(this.selectedcompany[0].companyCodeId,this.selectedcompany[0].languageId);

    }
  });
}


deleterecord(id: any,languageId:any) {
  this.spin.show();
  this.sub.add(this.service.Delete(id,languageId).subscribe((res) => {
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
  this.companys.forEach(x => {
    res.push({
      "Language Id":x.languageId,
      " Company ID ": x.companyCodeId,
      "Description":x.description,
      "Created By": x.createdBy,
      "Created On" :this.cs.dateapi(x.createdOn),
      
    
    });

  })
  this.cs.exportAsExcel(res, "Company");
}
onChange() {
  console.log(this.selectedcompany.length)
  const choosen= this.selectedcompany[this.selectedcompany.length - 1];   
  this.selectedcompany.length = 0;
  this.selectedcompany.push(choosen);
} 
}