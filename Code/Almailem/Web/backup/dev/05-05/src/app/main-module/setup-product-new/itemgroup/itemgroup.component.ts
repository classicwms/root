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
import { ItemgroupService } from './itemgroup.service';

@Component({
  selector: 'app-itemgroup',
  templateUrl: './itemgroup.component.html',
  styleUrls: ['./itemgroup.component.scss']
})
export class ItemgroupComponent implements OnInit {

  advanceFilterShow: boolean;
  @ViewChild('setupitemgroup') setupitemgroup: Table | undefined;
  itemgroup: any[] = [];
  selecteditemgroup : any[] = [];
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
    private service:  ItemgroupService) { }
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
      this.setupitemgroup!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
    
    ngOnInit(): void {
      this.getAll();
    }

    warehouseId = this.auth.warehouseId;
    /** Whether the number of selected elements matches the total number of rows. */
   
  openDialog(data: any = 'New'): void {
    if (data != 'New'){
      if (this.selecteditemgroup.length == 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = "";
    paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selecteditemgroup[0].itemGroupId : null ,warehouseId : data != 'New' ? this.selecteditemgroup[0].warehouseId : null,subItemGroupId : data != 'New' ? this.selecteditemgroup[0].subItemGroupId : null,itemTypeId : data != 'New' ? this.selecteditemgroup[0].itemTypeId : null,languageId : data != 'New' ? this.selecteditemgroup[0].languageId : null,plantId : data != 'New' ? this.selecteditemgroup[0].plantId : null,companyId : data != 'New' ? this.selecteditemgroup[0].companyId : null});
    this.router.navigate(['/main/productsetup/itemgroupNew/' + paramdata]);
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
    obj.companyCodeId = this.auth.companyId;
    obj.plantId = this.auth.plantId;
    obj.languageId = this.auth.languageId;
    obj.warehouseId = this.auth.warehouseId;
  
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe((res: any[]) => {
    console.log(res)
    if(res){
      this.itemgroup=res;
  
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
        this.itemgroup=res;
       } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  

  deleteDialog() {
    if (this.selecteditemgroup.length === 0) {
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
      data: this.selecteditemgroup[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
  
      if (result) {
        this.deleterecord(this.selecteditemgroup[0].itemGroupId,this.selecteditemgroup[0].subItemGroupId,this.selecteditemgroup[0].itemTypeId,this.selecteditemgroup[0].companyId,this.selecteditemgroup[0].languageId,this.selecteditemgroup[0].plantId);
  
      }
    });
  }
  
  
  deleterecord(id: any,subItemGroupId:any,itemTypeId:any,companyId:any,languageId:any,plantId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id,this.auth.warehouseId,subItemGroupId,itemTypeId,companyId,languageId,plantId).subscribe((res) => {
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
    this.itemgroup.forEach(x => {
      res.push({
      "Language Id":x.languageId,
        "Item Group Id":x.itemGroupId,
        "Description":x.description,
        "Created By":x.createdBy,
       "Created One":this.cs.dateapi(x.createdOn),
      });
  
    })
    this.cs.exportAsExcel(res, "Item Group");
  }
  onChange() {
    const choosen= this.selecteditemgroup[this.selecteditemgroup.length - 1];   
    this.selecteditemgroup.length = 0;
    this.selecteditemgroup.push(choosen);
  } 
  }
   
  
  
  

