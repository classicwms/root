import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BinlocationService } from './binlocation.service';

@Component({
  selector: 'app-binlocation',
  templateUrl: './binlocation.component.html',
  styleUrls: ['./binlocation.component.scss']
})
export class BinlocationComponent implements OnInit {

  advanceFilterShow: boolean;
  @ViewChild('Setupstorageselection') Setupstorageselection: Table | undefined;
  storageselection: any[] = [];
  selectedstorage : any[] = [];
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
    private service:  BinlocationService) { }
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
      this.Setupstorageselection!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
    
    ngOnInit(): void {
      this.getAll();
    }

    warehouseId = this.auth.warehouseId;
    /** Whether the number of selected elements matches the total number of rows. */
   
  openDialog(data: any = 'New'): void {
    if (data != 'New'){
      if (this.selectedstorage.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedstorage[0].itemCode : null,warehouseId: data != 'New' ? this.selectedstorage[0].warehouseId : null,languageId: data != 'New' ? this.selectedstorage[0].languageId : null, plantId: data != 'New' ? this.selectedstorage[0].plantId : null,companyCodeId: data != 'New' ? this.selectedstorage[0].companyCodeId : null});
    this.router.navigate(['/main/masternew/basicdataNew/' + paramdata]);
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
    // obj.companyCodeId = this.auth.companyId;
    // obj.plantId = this.auth.plantId;
    // obj.languageId = this.auth.languageId;
    obj.warehouseId = [this.auth.warehouseId];
    this.spin.show();
    this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
      console.log(res)
if(res){
   this.storageselection = res;

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
        this.storageselection=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedstorage.length === 0) {
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
      data: this.selectedstorage[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedstorage[0].storageBin,this.selectedstorage[0].warehouseId,this.selectedstorage[0].languageId,this.selectedstorage[0].plantId,this.selectedstorage[0].companyCodeId);
  
      }
    });
  }
  
  
  deleterecord(id: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,warehouseId,languageId,plantId,companyCodeId).subscribe((res) => {
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
    this.storageselection.forEach(x => {
      res.push({
      "Part No ":x.itemCode,
        "Item Group ":x.itemGroup,
        "Item Type":x.itemType,
        "Maximum Stock":x.maximumStock,
        "Minimum Stock":x.minimumStock,
        "Total Stock":x.totalStock,
        "Base UOM":x.uomId,
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Basic Data");
  }
  onChange() {
    const choosen= this.selectedstorage[this.selectedstorage.length - 1];   
    this.selectedstorage.length = 0;
    this.selectedstorage.push(choosen);
  } 
  }
   
  
  
  



