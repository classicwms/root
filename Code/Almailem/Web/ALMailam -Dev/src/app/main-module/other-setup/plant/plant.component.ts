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
import { PlantNewComponent } from './plant-new/plant-new.component';
import { PlantService } from './plant.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-plant',
  templateUrl: './plant.component.html',
  styleUrls: ['./plant.component.scss']
})
export class PlantComponent implements OnInit {
  screenid=3084;
  advanceFilterShow: boolean;
  @ViewChild('Setupplant') Setupplant: Table | undefined;
  plants: any;
  selectedplant : any;
  displayedColumns: string[] = ['select','companyCodeId', 'description', 'plantId','createdBy', 'createdOn', ];
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
    private service: PlantService, ) { }
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
    this.Setupplant!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
        companyCodeId: [[this.auth.companyId]],
        plantId: [[this.auth.plantId]],
        startCreatedOn:[],
        endCreatedOn:[],
        languageId: [[this.auth.languageId]],
         createdBy:[],
      });
      search() {
        this.spin.show();
        this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
          console.log(res);
          this.plants= res;
         
          
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
      getallfilter() {
        this.multilanguageList = [];
        this.multiplantList = [];
        this.multicompanyList=[];
        this.multiwarehouseList=[];
        this.multidateformatList=[];
        let obj: any = {};
        obj.languageId=[this.auth.languageId];
        obj.companyCodeId=[this.auth.companyId];
        obj.plantId=[this.auth.plantId];
       
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
        res.forEach((x: { plantId: string; description: string, }) => {
          this.multiplantList.push({
            value: x.plantId,
            label: x.plantId + '-' + x.description,
          });
      });
    
            this.multiselectyseridList = this.multiyseridList;
            this.multiselectyseridList = this.cas.removeDuplicatesFromArrayNew(this.multiyseridList);
            this.multiplantList = this.cas.removeDuplicatesFromArrayNew(this.multiplantList);
            this.multicompanyList = this.cas.removeDuplicatesFromArrayNew(this.multicompanyList);
            this.multiwarehouseList = this.cas.removeDuplicatesFromArrayNew(this.multiwarehouseList);
            this.multidateformatList = this.cas.removeDuplicatesFromArrayNew(this.multidateformatList);
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
console.log(this.selectedplant)
  if (data != 'New')
  if (this.selectedplant.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(PlantNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selectedplant[0].plantId : null, companyCodeId: data != 'New' ? this.selectedplant[0].companyCodeId : null,languageId: data != 'New' ? this.selectedplant[0].languageId : null}
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
  obj.plantId = [this.auth.plantId];
  obj.languageId = [this.auth.languageId];
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    if(res){
      this.plants=res;
  
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
      this.plants=res;
     } this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}


deleteDialog() {
  if (this.selectedplant.length === 0) {
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
    data: this.selectedplant[0].plantId,
  });

  dialogRef.afterClosed().subscribe(result => {

    if (result) {
      this.deleterecord(this.selectedplant[0].plantId,this.selectedplant[0].companyCodeId,this.selectedplant[0].languageId);

    }
  });
}


deleterecord(id: any,companyCodeId:any,languageId:any) {
  this.spin.show();
  this.sub.add(this.service.Delete(id,companyCodeId,languageId).subscribe((res) => {
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
  this.plants.forEach(x => {
    res.push({
      "Language Id":x.languageId,
      "Company Details":x.companyIdAndDescription,
      "PlantID": x.plantId,
      " Descripton ": x.description,
      "Created By ": x.createdBy,
      "	Created On ": x.createdOn,
      
    });

  })
  this.cs.exportAsExcel(res, "Branch");
}
onChange() {
  console.log(this.selectedplant.length)
  const choosen= this.selectedplant[this.selectedplant.length - 1];   
  this.selectedplant.length = 0;
  this.selectedplant.push(choosen);
} 
}


