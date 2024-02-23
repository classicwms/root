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
import { BatchserialService } from './batchserial.service';
import { BatchserialtableComponent } from './batchserialtable/batchserialtable.component';

@Component({
  selector: 'app-batchserial',
  templateUrl: './batchserial.component.html',
  styleUrls: ['./batchserial.component.scss']
})
export class BatchserialComponent implements OnInit {
  screenid=3010;
  advanceFilterShow: boolean;
  @ViewChild('setupbatch') setupbatch: Table | undefined;
  batch: any[] = [];
  selectedbatch : any[] = [];
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
    private service: BatchserialService ) { }
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
      this.setupbatch!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
      if (this.selectedbatch.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedbatch[0].storageMethod : null,languageId: data != 'New' ? this.selectedbatch[0].languageId : null,plantId: data != 'New' ? this.selectedbatch[0].plantId : null,warehouseId: data != 'New' ? this.selectedbatch[0].warehouseId : null,companyId: data != 'New' ? this.selectedbatch[0].companyId : null ,levelId: data != 'New' ? this.selectedbatch[0].levelId : null, maintenance: data != 'New' ? this.selectedbatch[0].maintenance : null,});
    this.router.navigate(['/main/productsetup/batchNew/' + paramdata]);
  }
  openDialog2(element){
    const dialogRef = this.dialog.open(BatchserialtableComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
     // data: { pageflow: data, code: data != 'New' ? this.selectedaisle[0].aisleId : null, floorId: data != 'New' ? this.selectedaisle[0].floorId : null, storageSectionId: data != 'New' ? this.selectedaisle[0].storageSectionId : null,}
     data: element
    });
  
    dialogRef.afterClosed().subscribe(result => {
    //  this.getAll();
    });
  }
  getAll() {
   
      this.adminUser()
    
  }
  
  adminUser(){
    let obj: any = {};
    obj.companyId = [this.auth.companyId];
   obj.plantId = [this.auth.plantId];
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = [this.auth.warehouseId];
  
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    console.log(res)
    if(res){
      this.batch=res;
  
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
        this.batch=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedbatch.length === 0) {
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
      data: this.selectedbatch[0],
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedbatch[0]);
      }
    });
  }
  
  
  deleterecord(id: any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id).subscribe((res) => {
      this.toastr.success(id.s + " Deleted successfully.","Notification",{
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
    this.batch.forEach(x => {
      res.push({
        "Language ":x.languageId,
        "Company ":x.companyIdAndDescription,
        "Branch":x.plantIdAndDescription,
        "Warehouse":x.warehouseIdAndDescription,
        "Storage Method":x.storageMethod,
        "Level":x.levelId,
        "Level Refernce":x.levelReference,
        "Maintanence":x.maintenance,
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Batch/Serial");
  }
  onChange() {
    const choosen= this.selectedbatch[this.selectedbatch.length - 1];   
    this.selectedbatch.length = 0;
    this.selectedbatch.push(choosen);
  } 
  }
   
  
  
  

