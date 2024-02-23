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
import { VariantService } from './variant.service';
import { VarinatableComponent } from './varinatable/varinatable.component';

@Component({
  selector: 'app-variant',
  templateUrl: './variant.component.html',
  styleUrls: ['./variant.component.scss']
})
export class VariantComponent implements OnInit {
screenid=3011;
  advanceFilterShow: boolean;
  @ViewChild('setupvariant') setupvariant: Table | undefined;
  variant: any[] = [];
  selectedvariant : any[] = [];
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
    private service:  VariantService) { }
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
      this.setupvariant!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
      if (this.selectedvariant.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedvariant[0].variantId : null,companyId: data != 'New' ? this.selectedvariant[0].companyId : null,languageId: data != 'New' ? this.selectedvariant[0].languageId : null,plantId: data != 'New' ? this.selectedvariant[0].plantId : null,warehouseId: data != 'New' ? this.selectedvariant[0].warehouseId : null,levelId: data != 'New' ? this.selectedvariant[0].levelId : null,variantSubId: data != 'New' ? this.selectedvariant[0].variantSubId : null });
    this.router.navigate(['/main/productsetup/variantNew/' + paramdata]);
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
      this.variant=res;
  
    }this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
  }


  openDialog2(element){
    const dialogRef = this.dialog.open(VarinatableComponent, {
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
  deleteDialog() {
    if (this.selectedvariant.length === 0) {
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
      data: this.selectedvariant[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selectedvariant[0].variantId,this.selectedvariant[0].companyId,this.selectedvariant[0].languageId,this.selectedvariant[0].plantId,this.selectedvariant[0].warehouseId,this.selectedvariant[0].levelId, this.selectedvariant[0].variantSubId);
  
      }
    });
  }
  
  
  deleterecord(id: any,companyId:any,languageId:any,plantId:any,warehouseId:any,levelId:any, subId: any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,companyId,languageId,plantId,warehouseId,levelId, subId).subscribe((res) => {
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
    this.variant.forEach(x => {
      res.push({
        "Language ":x.languageId,
        "Company ":x.companyIdAndDescription,
        "Branch":x.plantIdAndDescription,
        "Warehouse":x.warehouseIdAndDescription,
        "Level Id":x.levelId,
        "Description":x.description,
        "Created By":x.createdBy,
       "Created On":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Variant");
  }
  onChange() {
    const choosen= this.selectedvariant[this.selectedvariant.length - 1];   
    this.selectedvariant.length = 0;
    this.selectedvariant.push(choosen);
  } 
  }
   
  
  
  



