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
import { StorageclassService } from './storageclass.service';

@Component({
  selector: 'app-storageclass',
  templateUrl: './storageclass.component.html',
  styleUrls: ['./storageclass.component.scss']
})
export class StorageclassComponent implements OnInit {
screenid=3014;
  advanceFilterShow: boolean;
  @ViewChild('setupstorageclass') setupstorageclass: Table | undefined;
  storageclass: any[] = [];
  selectedstorageclass : any[] = [];
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
    private service:  StorageclassService) { }
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
      this.setupstorageclass!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
    RA: any = {};
    ngOnInit(): void {
      this.RA = this.auth.getRoleAccess(this.screenid);
      this.getAll();
    }

    warehouseId = this.auth.warehouseId;
    /** Whether the number of selected elements matches the total number of rows. */
   
  openDialog(data: any = 'New'): void {
    if (data != 'New'){
      if (this.selectedstorageclass.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedstorageclass[0].storageClassId : null,warehouseId: data != 'New' ? this.selectedstorageclass[0].warehouseId : null,plantId: data != 'New' ? this.selectedstorageclass[0].plantId : null,companyId: data != 'New' ? this.selectedstorageclass[0].companyId : null ,languageId: data != 'New' ? this.selectedstorageclass[0].languageId : null});
    this.router.navigate(['/main/productstorage/storageclassNew/' + paramdata]);
  }
  

  getAll() {
   
      this.adminUser()
    
  }
  
  adminUser(){
    let obj: any = {};
    obj.companyCodeId = this.auth.companyId;
    obj.plantId = this.auth.plantId;
    obj.languageId = this.auth.languageId;
    obj.warehouseId = this.auth.warehouseId;
  
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    console.log(res)
    if(res){
      this.storageclass=res;
  
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
        this.storageclass=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  
  deleteDialog() {
    if (this.selectedstorageclass.length === 0) {
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
      data: this.selectedstorageclass[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedstorageclass[0].storageClassId,this.selectedstorageclass[0].warehouseId,this.selectedstorageclass[0].plantId,this.selectedstorageclass[0].companyId,this.selectedstorageclass[0].languageId);
  
      }
    });
  }
  
  
  deleterecord(id: any,warehouseId:any,plantId:any,companyId:any,languageId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,warehouseId,plantId,companyId,languageId).subscribe((res) => {
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
    this.storageclass.forEach(x => {
      res.push({
      "Language ":x.languageId,
      "Company ":x.companyIdAndDescription,
      "Branch":x.plantIdAndDescription,
      "Warehouse":x.warehouseIdAndDescription,
        "Storage Class Id":x.storageClassId,
        "Description":x.description,
        "Company ID":x.companyId,
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Storage Class");
  }
  onChange() {
    const choosen= this.selectedstorageclass[this.selectedstorageclass.length - 1];   
    this.selectedstorageclass.length = 0;
    this.selectedstorageclass.push(choosen);
  } 
  }
   
  
  
  



