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
import { MenuNewComponent } from './menu-new/menu-new.component';
import { MenuService } from './menu.service';
import { WarehouseService } from '../../setup-organisation/warehouse/warehouse.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  screenid = 3109;
  advanceFilterShow: boolean;
  @ViewChild('Setupmenu') Setupmenu: Table | undefined;
  menu: any;
  selectedmenu: any;


  displayedColumns: string[] = ['select', 'menuId', 'subMenuId', 'languageId', 'menuName', 'menuText', 'subMenuName', 'createdBy', 'createdOn',];
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
    private service: MenuService,
    private warehouse: WarehouseService) { }
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
    this.Setupmenu!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  RA: any = {};
  modeOfImplementation: any;
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
        menuId:[],
        startCreatedOn:[],
        endCreatedOn:[],
        languageId: [[this.auth.languageId]],
         createdBy:[],
      });
      search() {
        this.spin.show();
        this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
          console.log(res);
          this.menu= res;
         
          
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
    res.forEach((x: { menuId: string; menuName: string, }) => {
      this.multidateformatList.push({
        value: x.menuId,
        label: x.menuId + '-' + x.menuName,
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
    console.log(this.selectedmenu)
    if (data != 'New')
      if (this.selectedmenu.length === 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    const dialogRef = this.dialog.open(MenuNewComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? this.selectedmenu[0].menuId : null, plantId: data != 'New' ? this.selectedmenu[0].plantId : null, warehouseId: data != 'New' ? this.selectedmenu[0].warehouseId : null, languageId: data != 'New' ? this.selectedmenu[0].languageId : null, authorizationObjectId: data != 'New' ? this.selectedmenu[0].authorizationObjectId : null, authorizationObjectValue: data != 'New' ? this.selectedmenu[0].authorizationObjectValue : null, subMenuId: data != 'New' ? this.selectedmenu[0].subMenuId : null, menuName: data != 'New' ? this.selectedmenu[0].menuName : null, companyCodeId: data != 'New' ? this.selectedmenu[0].companyCodeId : null }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getAll();
    });
  }
  getAll() {
    if (this.auth.userTypeId == 1) {
      this.superAdmin()
    } else {
      this.adminUser()
    }
  }

  adminUser() {
    let obj: any = {};
    obj.companyCodeId = this.auth.companyId;
    obj.plantId = this.auth.plantId;
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = this.auth.warehouseId;
    this.spin.show();
    this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
      if (res) {
      //   if (this.modeOfImplementation == "LEAN") {
      //     let Result = res.filter(x => x.referenceField1 == "LEAN")
      //     this.menu = Result;
      //   }
      //   else {
      //     this.menu = res;
      //   }
      this.menu = res;
       }
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  superAdmin() {
    this.spin.show();
    this.sub.add(this.service.Getall().subscribe((res: any[]) => {
      if (res) {
        this.menu = res;
      } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  deleteDialog() {
    if (this.selectedmenu.length === 0) {
      this.toastr.error("Kindly select any row", "Notification", {
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
      data: this.selectedmenu[0].code,
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selectedmenu[0].menuId, this.selectedmenu[0].warehouseId, this.selectedmenu[0].authorizationObjectId, this.selectedmenu[0].companyCodeId, this.selectedmenu[0].languageId, this.selectedmenu[0].plantId, this.selectedmenu[0].subMenuId);

      }
    });
  }


  deleterecord(id: any, warehouseId: any, authorizationObjectId: any, companyCodeId: any, languageId: any, plantId: any, subMenuId: any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id, warehouseId, authorizationObjectId, companyCodeId, languageId, plantId, subMenuId).subscribe((res) => {
      this.toastr.success(id + " Deleted successfully.", "Notification", {
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
    this.menu.forEach(x => {
      res.push({
        "Language Id": x.languageId,
        "Company ": x.companyIdAndDescription,
        "Plant ": x.plantIdAndDescription,
        "Warehouse ": x.warehouseIdAndDescription,
        "Menu Id": x.menuId,
        "Description": x.menuName,
        "Sub Menu Id": x.subMenuId,
        "Sub Menu Description": x.subMenuName,
        "Created By": x.createdBy,
        "Created On": this.cs.dateapi(x.createdOn),

      });

    })
    this.cs.exportAsExcel(res, "Menu");
  }
  onChange() {
    console.log(this.selectedmenu.length)
    const choosen = this.selectedmenu[this.selectedmenu.length - 1];
    this.selectedmenu.length = 0;
    this.selectedmenu.push(choosen);
  }
}




