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
import { SpanidNewComponent } from './spanid-new/spanid-new.component';
import { SpanidService } from './spanid.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-spanid',
  templateUrl: './spanid.component.html',
  styleUrls: ['./spanid.component.scss']
})
export class SpanidComponent implements OnInit {
screenid=3089;
  advanceFilterShow: boolean;
  @ViewChild('Setupspan') Setupspan: Table | undefined;
  spans: any;
  selectedspans : any;

  displayedColumns: string[] = ['select','doorId','doorDescription', 'createdBy','createdOn'];
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
    private service: SpanidService) { }
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
      this.Setupspan!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
    RA: any = {};
    ngOnInit(): void {
      this.RA = this.auth.getRoleAccess(this.screenid);
      this.getAll(

      );
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
          rowId:[],
          floorId:[],
          spanId:[],
          aisleId:[],
          startCreatedOn:[],
          endCreatedOn:[],
          languageId: [[this.auth.languageId]],
           createdBy:[],
        });
        search() {
          this.spin.show();
          this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
            console.log(res);
            this.spans= res;
           
            
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
        multifloorList:any[]=[];
        multispanList:any[]=[];
        multiaisleList:any[]=[];
        getallfilter() {
          this.multilanguageList = [];
          this.multiplantList = [];
          this.multicompanyList=[];
          this.multiwarehouseList=[];
          this.multidateformatList=[];
          this.multifloorList=[];
          this.multispanList=[];
          this.multiaisleList=[];
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
      res.forEach((x: { floorId: string; floorIdAndDescription: string, }) => {
        this.multifloorList.push({
          value: x.floorId,
          label: x.floorId + '-' + x.floorIdAndDescription,
        });
    });
    res.forEach((x: { aisleId: string; aisleIdAndDescription: string, }) => {
      this.multiaisleList.push({
        value: x.aisleId,
        label: x.aisleId + '-' + x.aisleIdAndDescription,
      });
  });
      
      res.forEach((x: { rowId: string; rowIdAndDescription: string, }) => {
        this.multidateformatList.push({
          value: x.rowId,
          label: x.rowId + '-' + x.rowIdAndDescription,
        });
    });
    res.forEach((x: { spanId: string; spanDescription: string, }) => {
      this.multispanList.push({
        value: x.spanId,
        label: x.spanId + '-' + x.spanDescription,
      });
  });
              this.multiselectyseridList = this.multiyseridList;
              this.multiselectyseridList = this.cas.removeDuplicatesFromArrayNew(this.multiyseridList);
              this.multiplantList = this.cas.removeDuplicatesFromArrayNew(this.multiplantList);
              this.multicompanyList = this.cas.removeDuplicatesFromArrayNew(this.multicompanyList);
              this.multiwarehouseList = this.cas.removeDuplicatesFromArrayNew(this.multiwarehouseList);
              this.multidateformatList = this.cas.removeDuplicatesFromArrayNew(this.multidateformatList);
             this.spans=res;
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
  
    if (data != 'New')
    if (this.selectedspans.length === 0) {
      this.toastr.warning("Kindly select any Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(SpanidNewComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? this.selectedspans[0].spanId : null,warehouseId: data != 'New' ? this.selectedspans[0].warehouseId : null,languageId: data != 'New' ? this.selectedspans[0].languageId : null,companyCodeId: data != 'New' ? this.selectedspans[0].companyCodeId : null,plantId: data != 'New' ? this.selectedspans[0].plantId : null,aisleId: data != 'New' ? this.selectedspans[0].aisleId : null,rowId: data != 'New' ? this.selectedspans[0].rowId : null,storageSectionId: data != 'New' ? this.selectedspans[0].storageSectionId : null,floorId: data != 'New' ? this.selectedspans[0].floorId : null}
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
   this.spans = res;
  
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
        this.spans=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedspans.length === 0) {
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
      data: this.selectedspans[0].spanId,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedspans[0].spanId,this.selectedspans[0].warehouseId,this.selectedspans[0].languageId,this.selectedspans[0].companyCodeId,this.selectedspans[0].plantId,this.selectedspans[0].aisleId,this.selectedspans[0].rowId,this.selectedspans[0].storageSectionId,this.selectedspans[0].floorId);
  
      }
    });
  }
  
  
  deleterecord(id: any,warehouseId:any,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,floorId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,warehouseId,languageId,companyCodeId,plantId,aisleId,rowId,storageSectionId,floorId).subscribe((res) => {
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
    this.spans.forEach(x => {
      res.push({
        "Langauge Id":x.languageId,
      "Company":x.companyIdAndDescription,
      "Plant ":x.plantIdAndDescription,
      "Warehouse ":x.warehouseIdAndDescription,
      "Floor":x.floorIdAndDescription,
      "Storage Section":x.storageSectionIdDescription,
        "Span Id":x.spanId,
       "Span Description":x.spanDescription,
       "Storage Section ID":x.storageSectionId,
       "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      
      });
  
    })
    this.cs.exportAsExcel(res, "Span");
  }
  onChange() {
    console.log(this.selectedspans.length)
    const choosen= this.selectedspans[this.selectedspans.length - 1];   
    this.selectedspans.length = 0;
    this.selectedspans.push(choosen);
  } 
  }
   
  
  
  





