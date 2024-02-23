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
screenid=3030;
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
    RA: any = {};
    ngOnInit(): void {
      this.RA = this.auth.getRoleAccess(this.screenid);
      this.getAll();
    }

    warehouseId = this.auth.warehouseId;
    /** Whether the number of selected elements matches the total number of rows. */
   
    openDialog(data: any = 'New',type?:any): void {
      this.selectedstorage.push(type);
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
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedstorage[0].storageBin : null,warehouseId: data != 'New' ? this.selectedstorage[0].warehouseId : null,languageId: data != 'New' ? this.selectedstorage[0].languageId : null, plantId: data != 'New' ? this.selectedstorage[0].plantId : null,companyCodeId: data != 'New' ? this.selectedstorage[0].companyCodeId : null});
    this.router.navigate(['/main/mastersStorageNew/binLocationNew/' + paramdata]);
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
   this.storageselection = res;

 }
       this.spin.hide();
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
        "Language":x.languageId,
        "Company":x.companyCodeId,
         "Branch":x.plantId,
         "Warehouse":x.warehouseId,
        "Storage Bin":x.storageBin,
        "Floor":x.floorId,
        "Storage Section":x.storageSectionId,
        "Bin Class Id":x.binClassId,
        "Aisle":x.aisleNumber,
        "Status":x.statusId == 0?'Empty':'Occupied',
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Storage Bin");
  }
  onChange() {
    const choosen= this.selectedstorage[this.selectedstorage.length - 1];   
    this.selectedstorage.length = 0;
    this.selectedstorage.push(choosen);
  } 
  }
   
  
  
  




