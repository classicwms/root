import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { CommonService, dropdownelement} from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BasicdataService } from './basicdata.service';

@Component({
  selector: 'app-basicdata',
  templateUrl: './basicdata.component.html',
  styleUrls: ['./basicdata.component.scss']
})
export class BasicdataComponent implements OnInit {

  screenid=3020;
  advanceFilterShow: boolean;
  @ViewChild('setupitemtype') setupitemtype: Table | undefined;
  itemtype: any[] = [];
  selecteditemtype : any[] = [];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  ELEMENT_DATA: any[] = [];
  
  constructor(public dialog: MatDialog,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private router: Router,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,
    private service: BasicdataService ) { }
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
   
    applyFilterGlobal($event: any, stringVal: any) {
      this.setupitemtype!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
    RA: any = {};
    ngOnInit(): void {
      this.RA = this.auth.getRoleAccess(this.screenid);
      this.getAll();
    }

    warehouseId = this.auth.warehouseId;
    /** Whether the number of selected elements matches the total number of rows. */
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
  openDialog(data: any = 'New',type?:any): void {
    this.selecteditemtype.push(type);
    if (data != 'New'){
      if (this.selecteditemtype.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selecteditemtype[0].itemCode : null,warehouseId: data != 'New' ? this.selecteditemtype[0].warehouseId : null,languageId: data != 'New' ? this.selecteditemtype[0].languageId : null, plantId: data != 'New' ? this.selecteditemtype[0].plantId : null,companyCodeId: data != 'New' ? this.selecteditemtype[0].companyCodeId : null,uomId: data != 'New' ? this.selecteditemtype[0].uomId : null,itemCode: data != 'New' ? this.selecteditemtype[0].itemCode : null,manufacturerPartNo: data != 'New' ? this.selecteditemtype[0].manufacturerPartNo : null});
    this.router.navigate(['/main/masternew/basicdataNew/' + paramdata]);
  }

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
  
  itemtypeListselected: any[] = [];
  itemtypeList: dropdownelement[] = [];
  
  itemgroupListselected: any[] = [];
  itemgroupList: dropdownelement[] = [];
  
  descriptionselected: any[] = [];
  descriptionList: dropdownelement[] = [];
  
  
  itemcodeselected: any[] = [];
  itemocodeList: dropdownelement[] = [];
  
  search(ispageload = false) {
    if (!ispageload) {
  
      //dateconvertion
      this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
      this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
    
    
      //patching
      // const itemCode = [...new Set(this.itemCodeListselected.map(item => item.item_id))].filter(x => x != null);
      // this.searhform.controls.itemCode.patchValue(itemCode);
    
      const itemType = [...new Set(this.itemtypeListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.itemType.patchValue(itemType);
    
      const itemGroup = [...new Set(this.itemgroupList.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.itemGroup.patchValue(itemGroup);
    
      const description = [...new Set(this.descriptionselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.description.patchValue(description);
    
    
    
      const itemCode = [...new Set(this.itemcodeselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.itemCode.patchValue(itemCode);
    }
   this.service.search(this.searhform.value).subscribe(res => {
     // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
     this.spin.hide();
     if (ispageload) {
      //  let tempitemCodeList: dropdownelement[] = []
      //  const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
      //  itemCode.forEach(x => tempitemCodeList.push({ item_id: x, item_text: x }));
      //  this.itemCodeList = tempitemCodeList;
  
       let tempitemtypeList: dropdownelement[] = []
       const handlingEquipmentId = [...new Set(res.map(item => item.itemType))].filter(x => x != null)
       handlingEquipmentId.forEach(x => tempitemtypeList.push({ item_id: x, item_text: x }));
       this.itemtypeList = tempitemtypeList;
  
       let tempitemgroupList: dropdownelement[] = []
       const handlingUnit = [...new Set(res.map(item => item.itemGroup))].filter(x => x != null)
       handlingUnit.forEach(x => tempitemgroupList.push({ item_id: x, item_text: x }));
       this.itemgroupList = tempitemgroupList;
  
       let tempdescriptionList: dropdownelement[] = []
       const category = [...new Set(res.map(item => item.category))].filter(x => x != null)
       category.forEach(x => tempdescriptionList.push({ item_id: x, item_text: x }));
       this.descriptionList = tempdescriptionList;
  
  
       let tempitemcodeList: dropdownelement[] = []
       const statusId = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
       statusId.forEach(x => tempitemcodeList.push({ item_id: x, item_text: x }));
       this.itemocodeList = tempitemcodeList;
     }
     this.itemtype = res;
    
   }, err => {
  
     this.cs.commonerrorNew(err);
     this.spin.hide();
  
   });   

  
  }
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
    this.sub.add(this.service.searchSpark(obj).subscribe((res: any[]) => {
      console.log(res)
if(res){
   this.itemtype = res;

}
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  deleteDialog() {
    if (this.selecteditemtype.length === 0) {
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
      data: this.selecteditemtype[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selecteditemtype[0].warehouseId,this.selecteditemtype[0].languageId,this.selecteditemtype[0].plantId,this.selecteditemtype[0].uomId,this.selecteditemtype[0].companyCodeId,this.selecteditemtype[0].itemCode,this.selecteditemtype[0].manufacturerPartNo);
  
      }
    });
  }
  
  
  deleterecord(warehouseId:any,languageId:any,plantId:any,uomId:any,companyCodeId:any,itemCode:any,manufacturerPartNo:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(warehouseId,languageId,plantId,uomId,companyCodeId,itemCode,manufacturerPartNo).subscribe((res) => {
      this.toastr.success(itemCode+ " Deleted successfully.","Notification",{
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
    this.itemtype.forEach(x => {
      res.push({
        "Company ":x.companyDescription,
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Mfr Name":x.manufacturerPartNo,
      "Part No ":x.itemCode,
      "Description":x.description,
        "Base UOM":x.uomId,
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Basic Data 1");
  }
  onChange() {
    const choosen= this.selecteditemtype[this.selecteditemtype.length - 1];   
    this.selecteditemtype.length = 0;
    this.selecteditemtype.push(choosen);
  } 
  }
   
  
  
  



