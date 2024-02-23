import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
import { CommonService, dropdownelement } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PackingmaterialElement } from "../packing-material/packing-material.service";
import { HandlingUnitElement, HandlingUnitService } from "./handling-unit.service";
import { Table } from 'primeng/table';
import { UnitNewComponent } from "./unit-new/unit-new.component";

// Z
@Component({
  selector: 'app-handling-unit',
  templateUrl: './handling-unit.component.html',
  styleUrls: ['./handling-unit.component.scss']
})
export class HandlingUnitComponent implements OnInit {
  screenid = 3032;
  advanceFilterShow: boolean;
  @ViewChild('Setupcurrency') Setupcurrency: Table | undefined;
  currrencys: any;
  selectedcurrency : any;
  displayedColumns: string[] = ['select', 'handlingUnit', 'statusId', 'createdby', 'createdon', ];
    sub = new Subscription();
    ELEMENT_DATA: HandlingUnitElement[] = [];
    isShowDiv = false;
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    constructor(public dialog: MatDialog,
      private service: HandlingUnitService,
     // private cas: CommonApiService,
      public toastr: ToastrService,
      private spin: NgxSpinnerService,
      public cs: CommonService,
     // private excel: ExcelService,
      private fb: FormBuilder,
      private auth: AuthService) { }
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

    RA: any = {};
    ngOnInit(): void {
     // this.auth.isuserdata();
     this.RA = this.auth.getRoleAccess(this.screenid);
    
      //this.search(true);
      this.getAll();
    }
  
    dataSource = new MatTableDataSource<HandlingUnitElement>(this.ELEMENT_DATA);
    selection = new SelectionModel<HandlingUnitElement>(true, []);
  
  
  
  
    deleteDialog() {
      debugger
      if (this.selectedcurrency.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
      const dialogRef = this.dialog.open(DeleteComponent, {
        
        disableClose: true,
        width: '50%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: this.selectedcurrency[0].handlingUnit,
      });
  
      dialogRef.afterClosed().subscribe(result => {
  
        if (result) {
          this.deleterecord(this.selectedcurrency[0].handlingUnit,this.selectedcurrency[0].companyCodeId,this.selectedcurrency[0].languageId,this.selectedcurrency[0].plantId,this.selectedcurrency[0].warehouseId);
  
        }
      });
    }
    deleterecord(id: any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
      this.spin.show();
      this.sub.add(this.service.Delete(id,companyCodeId,languageId,plantId,warehouseId).subscribe((res) => {
        // console.log(id);
        // console.log(id.handlingEquipmentId);
        // console.log(res);
        this.toastr.success(id + " Deleted successfully.","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide(); //this.getAll();
        window.location.reload();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
    openDialog(data: any = 'New'): void {
      if (data != 'New')
        if (this.selectedcurrency.length === 0) {
          this.toastr.error("Kindly select any row", "Notification",{
            timeOut: 2000,
            progressBar: false,
          });
          return;
        }
      const dialogRef = this.dialog.open(UnitNewComponent, {
        disableClose: true,
        width: '50%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: { pageflow: data, code: data != 'New' ? this.selectedcurrency[0].handlingUnit : null,companyCodeId: data != 'New' ? this.selectedcurrency[0].companyCodeId : null,languageId: data != 'New' ? this.selectedcurrency[0].languageId : null,plantId: data != 'New' ? this.selectedcurrency[0].plantId : null,warehouseId: data != 'New' ? this.selectedcurrency[0].warehouseId : null }
      });
  
      dialogRef.afterClosed().subscribe(result => {
  
       this.getAll();
      // window.location.reload();
      });
    }
    ngOnDestroy() {
      if (this.sub != null) {
        this.sub.unsubscribe();
      }
  
    }
    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator!: MatPaginator; // Pagination
   // Pagination
  
  //  getAll() {
  //   this.spin.show();
  //   this.sub.add(this.service.Getall().subscribe((res: HandlingUnitElement[]) => {
  //     let result = res.filter((x: any) =>   x.warehouseId == this.warehouseId);
  //     this.ELEMENT_DATA = result;
  //     console.log(this.ELEMENT_DATA);
   
  //     this.spin.hide();
  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }));
  // }
  
  getAll() {
  
      this.adminUser()
    
  }
 
  adminUser(){
  let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
     obj.plantId = [this.auth.plantId];
     obj.languageId = [this.auth.languageId];
    obj.warehouseId = [this.auth.warehouseId];
    this.spin.show();
    this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
      console.log(res)
if(res){
   this.currrencys = res;

}
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
     this.spin.hide();
    }));
  }

  searhform = this.fb.group({
    createdBy: [],
    endCreatedOn: [],
    endUpdatedOn: [],
    handlingUnit: [],
    referenceField1: [],
    startCreatedOn: [],
    startUpdatedOn: [],
    statusId: [],
    updatedBy: [],
    warehouseId: [],
 
  });
 
 
 
 
  dropdownSettings: IDropdownSettings = {
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };
 
  // itemCodeListselected: any[] = [];
  // itemCodeList: dropdownelement[] = [];
 
  handlingUnitListselected: any[] = [];
  handlingUnitList: dropdownelement[] = [];
 
  // partnerCodeListselected: any[] = [];
  // partnerCodeList: dropdownelement[] = [];
 
  // preOutboundNoselected: any[] = [];
  // preOutboundNoList: dropdownelement[] = [];
 

  statusIdListselected: any[] = [];
  statusIdList: dropdownelement[] = [];
 
  search(ispageload = false) {
    if (!ispageload) {
 
      //dateconvertion
      this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
      this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
  
  
      //patching
      // const itemCode = [...new Set(this.itemCodeListselected.map(item => item.item_id))].filter(x => x != null);
      // this.searhform.controls.itemCode.patchValue(itemCode);
  
      const handlingUnit = [...new Set(this.handlingUnitListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.handlingUnit.patchValue(handlingUnit);
  
      // const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.item_id))].filter(x => x != null);
      // this.searhform.controls.partnerCode.patchValue(partnerCode);
  
      // const preOutboundNo = [...new Set(this.preOutboundNoselected.map(item => item.item_id))].filter(x => x != null);
      // this.searhform.controls.preOutboundNo.patchValue(preOutboundNo);
   
  
  
      const statusId = [...new Set(this.statusIdListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.statusId.patchValue(statusId);
  }
   this.service.search({}).subscribe(res => {
     // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
     this.spin.hide();
     if (ispageload) {
      //  let tempitemCodeList: dropdownelement[] = []
      //  const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
      //  itemCode.forEach(x => tempitemCodeList.push({ item_id: x, item_text: x }));
      //  this.itemCodeList = tempitemCodeList;
 
       let temphandlingUnitList: dropdownelement[] = []
       const handlingUnit = [...new Set(res.map(item => item.handlingUnit))].filter(x => x != null)
       handlingUnit.forEach(x => temphandlingUnitList.push({ item_id: x, item_text: x }));
       this.handlingUnitList = temphandlingUnitList;
 
      //  let temppartnerCodeList: dropdownelement[] = []
      //  const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
      //  partnerCode.forEach(x => temppartnerCodeList.push({ item_id: x, item_text: x }));
      //  this.partnerCodeList = temppartnerCodeList;
 
      //  let temppreOutboundNoList: dropdownelement[] = []
      //  const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
      //  preOutboundNo.forEach(x => temppreOutboundNoList.push({ item_id: x, item_text: x }));
      //  this.preOutboundNoList = temppreOutboundNoList;


       let tempstatusIdList: dropdownelement[] = []
       const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
       statusId.forEach(x => tempstatusIdList.push({ item_id: x, item_text: x }));
       this.statusIdList = tempstatusIdList;
     }
     this.currrencys =res;
    
   }, err => {
 
     this.cs.commonerrorNew(err);
     this.spin.hide();
 
   });   

 
 }
 reload(){
   window.location.reload();
 }

  
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
    checkboxLabel(row?: HandlingUnitElement): string {
      if (!row) {
        return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
      }
      return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.handlingUnit + 1}`;
    }
  
  
  
  
  
  
    clearselection(row: any) {
  
      this.selection.clear();
      this.selection.toggle(row);
    }
    onChange() {
      console.log(this.selectedcurrency.length)
      const choosen= this.selectedcurrency[this.selectedcurrency.length - 1];   
      this.selectedcurrency.length = 0;
      this.selectedcurrency.push(choosen);
    } 
    downloadexcel() {
      var res: any = [];
      this.currrencys.forEach(x => {
        res.push({
          "LanguageId":x.languageId,
          "Company":x.companyCode,
          "Branch":x.plantId,
          "Warehouse":x.warehouseId,
          "Handling Id":x.handlingUnit,
          "Status":x.statusId ? 'Active':'Inactive',
        
          "Created By":x.createdBy,
          "Created On":this.cs.dateapi(x.createdOn),
        
        
        });
    
      })
      this.cs.exportAsExcel(res, "Handling Unit");
    }
  }