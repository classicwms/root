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
import { StateNewComponent } from './state-new/state-new.component';
import { StateService } from './state.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-state',
  templateUrl: './state.component.html',
  styleUrls: ['./state.component.scss']
})
export class StateComponent implements OnInit {
screenid=3134;
  advanceFilterShow: boolean;
  @ViewChild('Setupstate') Setupstate: Table | undefined;
  states: any;
  selectedstates : any;
  displayedColumns: string[] = ['select', 'countryId', 'stateId','stateName', 'languageId', ];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  ELEMENT_DATA: any[] = [];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);
  
  constructor(public dialog: MatDialog,
   private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,
    private service: StateService) { }
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
      this.Setupstate!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
          countryId: [],
  countryName: [],
  languageId: [[this.auth.languageId]],
  stateId:[],
  createdBy:[],
        });
        search() {
          this.spin.show();
          this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
            console.log(res);
            this.states = res;
           
            
            this.spin.hide();
          }, err => {
            this.cs.commonerrorNew(err);
            this.spin.hide();
          }));
        }
      
        multistateList:any[]=[];
    getallfilter() {
      this.multilanguageList = [];
      this.multicountryList = [];
      this.multistateList=[];
      let obj: any = {};
      obj.languageId=[this.auth.languageId];
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
        res.forEach((x: { stateId: string; stateName: string, }) => {
          this.multistateList.push({
            value: x.stateId,
            label: x.stateId + '-' + x.stateName,
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
  console.log(this.selectedstates)
    if (data != 'New')
    if (this.selectedstates.length === 0) {
      this.toastr.warning("Kindly select any Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(StateNewComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? this.selectedstates[0].stateId : null,countryId: data != 'New' ? this.selectedstates[0].countryId : null,languageId: data != 'New' ? this.selectedstates[0].languageId : null}
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
  this.states = res; 
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
        this.states=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedstates.length === 0) {
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
      data: this.selectedstates[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedstates[0].stateId,this.selectedstates[0].countryId,this.selectedstates[0].languageId);
  
      }
    });
  }
  
  
  deleterecord(id: any,countryId:any,languageId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,countryId,languageId).subscribe((res) => {
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
    this.states.forEach(x => {
      res.push({
        "Language Id":x.languageId,
        "Country":x.countryIdAndDescription,
       "State Id":x.stateId,
       "Description":x.stateName,
       "Created By":x.createdBy,
       "Created On":x.createdOn,
      });
  
    })
    this.cs.exportAsExcel(res, "State");
  }
  onChange() {
    console.log(this.selectedstates.length)
    const choosen= this.selectedstates[this.selectedstates.length - 1];   
    this.selectedstates.length = 0;
    this.selectedstates.push(choosen);
  } 
  }
   
  
  
  


