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
import { StratergyService } from './stratergy.service';

@Component({
  selector: 'app-stratergy',
  templateUrl: './stratergy.component.html',
  styleUrls: ['./stratergy.component.scss']
})
export class StratergyComponent implements OnInit {

  screenid=3017;
  advanceFilterShow: boolean;
  @ViewChild('setupstorageclass') setupstorageclass: Table | undefined;
  storageclass: any[] = [];
  selectedstrategy : any[] = [];
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
    private service:  StratergyService) { }
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
      if (this.selectedstrategy.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedstrategy[0] : null });
    this.router.navigate(['/main/productstorage/stratergyNew/' + paramdata]);
  }
  
  getAll() {
    this.spin.show();
    let obj: any = {};
    obj.companyId = this.auth.companyId;
    obj.plantId = this.auth.plantId;
    obj.warehouseId = this.auth.warehouseId;
    obj.languageId = this.auth.languageId;
    this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
      if(res){
          this.storageclass = res;
      }
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  deleteDialog() {
    if (this.selectedstrategy.length === 0) {
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
      data: this.selectedstrategy[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedstrategy[0]);
  
      }
    });
  }
  
  
  deleterecord(id: any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id, this.auth.warehouseId).subscribe((res) => {
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
      
        "Stratergy Type Id":x.strategyTypeId,
        "Description":x.description,
        "Sequence No":x.sequenceIndicator,
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Strategy");
  }
  onChange() {
    const choosen= this.selectedstrategy[this.selectedstrategy.length - 1];   
    this.selectedstrategy.length = 0;
    this.selectedstrategy.push(choosen);
  } 
  }
   
  
  
  




