

// export interface  handlingequipment {

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
  import { EquipmentNewComponent } from "./equipment-new/equipment-new.component";
  import { HandlingquipmentElement, HandlingEquipmentService } from "./handling-equipment.service";
import { Table } from "primeng/table";
  
  
  //   no: string;
  //   status:  string;
  //   warehouseno:  string;
  //   preinboundno:  string;
  //   date:  string;
  //   refdocno:  string;
  //   type:  string;
  // }
  // const ELEMENT_DATA:  handlingequipment[] = [
  //   { no: "1", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "2", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "3", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "4", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "5", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "6", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "7", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "8", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "9", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "10", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  //   { no: "11", warehouseno:  'Value',type:  'Value',refdocno:  'Value',preinboundno:  'Value', date: 'date', status: 'date' , },
  
  
  // ];
  @Component({
    selector: 'app-handling-equipment',
    templateUrl: './handling-equipment.component.html',
    styleUrls: ['./handling-equipment.component.scss']
  })
  export class HandlingEquipmentComponent implements OnInit {
    screenid = 3034;

    displayedColumns: string[] = ['select', 'handlingEquipmentId','category','handlingUnit', 'statusId', 'createdby', 'createdon', ];
    sub = new Subscription();
    advanceFilterShow: boolean;
  @ViewChild('Setupcurrency') Setupcurrency: Table | undefined;
  currrencys: any;
  selectedcurrency : any;
    ELEMENT_DATA: HandlingquipmentElement[] = [];
    isShowDiv = false;
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    constructor(public dialog: MatDialog,
      private service: HandlingEquipmentService,
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
  
    dataSource = new MatTableDataSource<HandlingquipmentElement>(this.ELEMENT_DATA);
    selection = new SelectionModel<HandlingquipmentElement>(true, []);
  
  
  
  
    deleteDialog() {
      
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
        data: this.selectedcurrency[0].handlingEquipmentId,
      });
  
      dialogRef.afterClosed().subscribe(result => {
  
        if (result) {
          this.deleterecord(this.selectedcurrency[0].handlingEquipmentId,this.selectedcurrency[0].companyCodeId,this.selectedcurrency[0].languageId,this.selectedcurrency[0].plantId,this.selectedcurrency[0].warehouseId);
  
        }
      });
    }
    deleterecord(id: any,companyCodeId:any,languageId:any,plantId:any,warehouseId) {
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
      const dialogRef = this.dialog.open(EquipmentNewComponent, {
        disableClose: true,
        width: '50%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: { pageflow: data, code: data != 'New' ? this.selectedcurrency[0].handlingEquipmentId : null,companyCodeId: data != 'New' ? this.selectedcurrency[0].companyCodeId : null,languageId: data != 'New' ? this.selectedcurrency[0].languageId : null,plantId: data != 'New' ? this.selectedcurrency[0].plantId : null,warehouseId: data != 'New' ? this.selectedcurrency[0].warehouseId : null, }
      });
  
      dialogRef.afterClosed().subscribe(result => {
  
       this.getAll();
       window.location.reload();
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

  // getAll() {
  //   this.spin.show();
  //   this.sub.add(this.service.Getall().subscribe((res: HandlingquipmentElement[]) => {
  //     let result = res.filter((x: any) =>   x.warehouseId == this.warehouseId);
  //     this.ELEMENT_DATA = result;
  //     console.log(this.ELEMENT_DATA);
  //     this.currrencys = result;
     
  //     this.spin.hide();
  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }));
  // }
  
  searhform = this.fb.group({
    category: [],
    createdBy: [],
    endCreatedOn: [],
    endUpdatedOn: [],
    handlingEquipmentId: [],
    handlingUnit: [],
    startCreatedOn: [],
    startUpdatedOn: [],
    statusId: [],
    updatedBy: [],
    warehouseId: [[this.auth.warehouseId]],
  
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
  
  handlingEquipmentIdListselected: any[] = [];
  handlingEquipmentIdList: dropdownelement[] = [];
  
  handlingUnitListselected: any[] = [];
  handlingUnitList: dropdownelement[] = [];
  
  categoryselected: any[] = [];
  categoryList: dropdownelement[] = [];
  
  
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
    
      const handlingEquipmentId = [...new Set(this.handlingEquipmentIdListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.handlingEquipmentId.patchValue(handlingEquipmentId);
    
      const handlingUnit = [...new Set(this.handlingUnitListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.handlingUnit.patchValue(handlingUnit);
    
      const category = [...new Set(this.categoryselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.category.patchValue(category);
    
    
    
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
  
       let temphandlingEquipmentIdList: dropdownelement[] = []
       const handlingEquipmentId = [...new Set(res.map(item => item.handlingEquipmentId))].filter(x => x != null)
       handlingEquipmentId.forEach(x => temphandlingEquipmentIdList.push({ item_id: x, item_text: x }));
       this.handlingEquipmentIdList = temphandlingEquipmentIdList;
  
       let temphandlingUnitList: dropdownelement[] = []
       const handlingUnit = [...new Set(res.map(item => item.handlingUnit))].filter(x => x != null)
       handlingUnit.forEach(x => temphandlingUnitList.push({ item_id: x, item_text: x }));
       this.handlingUnitList = temphandlingUnitList;
  
       let tempcategoryList: dropdownelement[] = []
       const category = [...new Set(res.map(item => item.category))].filter(x => x != null)
       category.forEach(x => tempcategoryList.push({ item_id: x, item_text: x }));
       this.categoryList = tempcategoryList;
  
  
       let tempstatusIdList: dropdownelement[] = []
       const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
       statusId.forEach(x => tempstatusIdList.push({ item_id: x, item_text: x }));
       this.statusIdList = tempstatusIdList;
     }
     this.currrencys = res;
    
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
    checkboxLabel(row?: HandlingquipmentElement): string {
      if (!row) {
        return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
      }
      return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.handlingEquipmentId + 1}`;
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
          "Handling Equipment":x.handlingEquipmentId,
          "Category":x.category,
          "Handling Unit":x.handlingUnit,
          "Status":x.statusId ? 'Active':'Inactive',
        
          "Created By":x.createdBy,
          "Created On":this.cs.dateapi(x.createdOn),
        
        
        });
    
      })
      this.cs.exportAsExcel(res, "Handling Equiment");
    }
  }