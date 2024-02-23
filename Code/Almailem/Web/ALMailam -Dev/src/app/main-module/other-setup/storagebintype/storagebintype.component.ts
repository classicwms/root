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
import { StoragebintypeNewComponent } from './storagebintype-new/storagebintype-new.component';
import { StoragebintypeService } from './storagebintype.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-storagebintype',
  templateUrl: './storagebintype.component.html',
  styleUrls: ['./storagebintype.component.scss']
})
export class StoragebintypeComponent implements OnInit {
  screenid=3093;
  advanceFilterShow: boolean;
  @ViewChild('Setupstoragebin') Setupstoragebin: Table | undefined;
  storagebin: any;
  selectedstoragebin : any;
  displayedColumns: string[] = ['select', 'companyCodeId','description','plantId','storageBinTypeId','storageClassId','storageTypeId','warehouseId', 'createdBy', 'createdOn'];
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
    private service: StoragebintypeService) { }
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
    this.Setupstoragebin!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
        storageClassId:[],
        storageTypeId:[],
        startCreatedOn:[],
        endCreatedOn:[],
        languageId: [[this.auth.languageId]],
         createdBy:[],
      });
      search() {
        this.spin.show();
        this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
          console.log(res);
          this.storagebin= res;
         
          
          this.spin.hide();
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));
      }
      multiplantList:any[]=[];
      multicompanyList:any[]=[];
      multiwarehouseList:any[]=[];
      multidateformatList:any[]=[];
      multistorageTypeList:any[]=[];
      multistorageBinTypeList:any[]=[];
      getallfilter() {
        this.multilanguageList = [];
        this.multiplantList = [];
        this.multicompanyList=[];
        this.multiwarehouseList=[];
        this.multidateformatList=[];
        this.multistorageTypeList=[];
        this.multistorageBinTypeList=[];
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
            res.forEach((x: { countryId: string; countryIdAndDescription: string, }) => {
              this.multicountryList.push({
                value: x.countryId,
                label: x.countryId + '-' + x.countryIdAndDescription,
              });
          });
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
    res.forEach((x: { storageClassId: string; storageClassIdAndDescription: string, }) => {
      this.multidateformatList.push({
        value: x.storageClassId,
        label: x.storageClassId + '-' + x.storageClassIdAndDescription,
      });
  });
  res.forEach((x: { storageTypeId: string; storageTypeIdAndDescription: string, }) => {
    this.multistorageTypeList.push({
      value: x.storageTypeId,
      label: x.storageTypeId + '-' + x.storageTypeIdAndDescription,
    });
});
res.forEach((x: { storageBinTypeId: string; description: string, }) => {
  this.multistorageBinTypeList.push({
    value: x.storageBinTypeId,
    label: x.storageBinTypeId + '-' + x.description,
  });
});
            this.multiselectyseridList = this.multiyseridList;
            this.multiselectyseridList = this.cas.removeDuplicatesFromArrayNew(this.multiyseridList);
            this.multiplantList = this.cas.removeDuplicatesFromArrayNew(this.multiplantList);
            this.multicompanyList = this.cas.removeDuplicatesFromArrayNew(this.multicompanyList);
            this.multiwarehouseList = this.cas.removeDuplicatesFromArrayNew(this.multiwarehouseList);
            this.multidateformatList = this.cas.removeDuplicatesFromArrayNew(this.multidateformatList);
            this.multistorageTypeList = this.cas.removeDuplicatesFromArrayNew(this.multistorageTypeList);
            this.multistorageBinTypeList = this.cas.removeDuplicatesFromArrayNew(this.multistorageBinTypeList);
          this.storagebin=res;
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
console.log(this.selectedstoragebin)
  if (data != 'New')
  if (this.selectedstoragebin.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(StoragebintypeNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selectedstoragebin[0].storageBinTypeId : null,storageTypeId: data != 'New' ? this.selectedstoragebin[0].storageTypeId : null,storageClassId: data != 'New' ? this.selectedstoragebin[0].storageClassId : null,warehouseId: data != 'New' ? this.selectedstoragebin[0].warehouseId : null,companyCodeId: data != 'New' ? this.selectedstoragebin[0].companyCodeId : null,plantId: data != 'New' ? this.selectedstoragebin[0].plantId : null,languageId: data != 'New' ? this.selectedstoragebin[0].languageId : null}
  });

  dialogRef.afterClosed().subscribe(result => {
    this.getAll();
  });
}
// getAll() {
//   this.spin.show();
//   this.sub.add(this.service.search({}).subscribe((res: any[]) => {
//     console.log(res)
//     if(res){
//       this.storagebin=res;
//   //   this.dataSource = new MatTableDataSource<any>(res);
//   //   this.selection = new SelectionModel<any>(true, []);
//   //   this.dataSource.sort = this.sort;
//   //  this.dataSource.paginator = this.paginator;
//     }this.spin.hide();
//   }, err => {
//     this.cs.commonerrorNew(err);
//     this.spin.hide();
//   }));
// }
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
    this.storagebin=res;

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
      this.storagebin=res;
     } this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
deleteDialog() {
  if (this.selectedstoragebin.length === 0) {
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
    data: this.selectedstoragebin[0].code,
  });

  dialogRef.afterClosed().subscribe(result => {

    if (result) {
      this.deleterecord(this.selectedstoragebin[0].storageBinTypeId,this.selectedstoragebin[0].storageTypeId,this.selectedstoragebin[0].storageClassId,this.selectedstoragebin[0].warehouseId,this.selectedstoragebin[0].companyCodeId,this.selectedstoragebin[0].plantId,this.selectedstoragebin[0].languageId);

    }
  });
}


deleterecord(id: any,storageTypeId:any,storageClassId:any,warehouseId:any,companyCodeId:any,plantId:any,languageId:any) {
  this.spin.show();
  this.sub.add(this.service.Delete(id,storageTypeId,storageClassId,warehouseId,companyCodeId,plantId,languageId).subscribe((res) => {
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
  this.storagebin.forEach(x => {
    res.push({
      "Langauge Id":x.languageId,
      "Company Details":x.companyIdAndDescription,
      "Plant Details":x.plantIdAndDescription,
      "Warehouse Details":x.warehouseIdAndDescription,
      "Storage Class":x.storageClassIdAndDescription,
      "Storage Type":x.storageTypeIdAndDescription,
      "StorageBinType ID": x.storageBinTypeId,
      'Descripton	': x.description,
      "Created By": x.createdBy,
      "Created On": x.createdOn,
    });

  })
  this.cs.exportAsExcel(res, "StorageBinType");
}
onChange() {
  console.log(this.selectedstoragebin.length)
  const choosen= this.selectedstoragebin[this.selectedstoragebin.length - 1];   
  this.selectedstoragebin.length = 0;
  this.selectedstoragebin.push(choosen);
} 
}
 
