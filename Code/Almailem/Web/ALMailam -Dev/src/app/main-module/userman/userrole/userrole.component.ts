import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { Table } from 'primeng/table';
import { CommonService } from 'src/app/common-service/common-service.service';
import { UserroleService } from './userrole.service';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { AuthService } from 'src/app/core/Auth/auth.service';
export interface  hhtuser {


  no: string;
  actions:  string;
  status:  string;
   order:  string;
  orderedlines:  string;
  date:  string;
  outboundno:  string;
   refno:  string;
   required:  string;
   scode:  string;
   sname:  string;
}
const ELEMENT_DATA:  hhtuser[] = [
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },

];
@Component({
  selector: 'app-userrole',
  templateUrl: './userrole.component.html',
  styleUrls: ['./userrole.component.scss']
})
export class UserroleComponent implements OnInit {
  screenid=3159;
  advanceFilterShow: boolean;
  @ViewChild('SetupuserRole') SetupuserRole: Table | undefined;
  userRole : any[] = [];
  selecteduserRole : any[] = [];
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  constructor(private service:UserroleService ,
    private spin: NgxSpinnerService, public cs : CommonService, public dialog: MatDialog,   public toastr: ToastrService,private auth: AuthService, private router: Router) { }

  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;  
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
     console.log('show:' + this.showFloatingButtons);
  }


  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.getAll();
  }
  ELEMENT_DATA: any[] = [];
  // getAll() {
  //   this.spin.show();
  //   this.sub.add(this.service.Getall().subscribe(res => {
  //     this.ELEMENT_DATA = [];
  //     // res = [...new Set(res.map(item => item.userRoleId, item.userRoleName, item.roleStatus))];
  //   //   res.forEach((x) => {
  //   //     x.statusId = x.statusId == true ? 'Active' : 'InActive';
  //   //  if(x.roleId != null){
  //   //   let filter = this.cs.filterArray(this.ELEMENT_DATA, {roleId: x.roleId });
  //   //   console.log(filter);
  //   //   if (filter.length == 0) {
  //   //     console.log(filter);
  //   //     console.log(x);
  //   //      this.ELEMENT_DATA.push(x);
  //   //     }
  //   //  }
  //   //   })

  //   this.ELEMENT_DATA = this.cs.removeDuplicatesFromArrayList(res, 'roleId' )
  //     this.userRole= this.ELEMENT_DATA;
  
  //     this.spin.hide();
  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }));
  // }
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
   obj.languageId = [this.auth.languageId];
   obj.warehouseId = this.auth.warehouseId;
 
  this.spin.show();
  this.sub.add(this.service.search(obj).subscribe(res => {
    this.ELEMENT_DATA = [];
    // res = [...new Set(res.map(item => item.userRoleId, item.userRoleName, item.roleStatus))];
  //   res.forEach((x) => {
  //     x.statusId = x.statusId == true ? 'Active' : 'InActive';
  //  if(x.roleId != null){
  //   let filter = this.cs.filterArray(this.ELEMENT_DATA, {roleId: x.roleId });
  //   console.log(filter);
  //   if (filter.length == 0) {
  //     console.log(filter);
  //     console.log(x);
  //      this.ELEMENT_DATA.push(x);
  //     }
  //  }
  //   })

  this.ELEMENT_DATA = this.cs.removeDuplicatesFromArrayList(res, 'roleId' )
    this.userRole= this.ELEMENT_DATA;

    this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
  superAdmin(){
   this.spin.show();
   this.sub.add(this.service.Getall().subscribe(res => {
    this.ELEMENT_DATA = [];
    // res = [...new Set(res.map(item => item.userRoleId, item.userRoleName, item.roleStatus))];
  //   res.forEach((x) => {
  //     x.statusId = x.statusId == true ? 'Active' : 'InActive';
  //  if(x.roleId != null){
  //   let filter = this.cs.filterArray(this.ELEMENT_DATA, {roleId: x.roleId });
  //   console.log(filter);
  //   if (filter.length == 0) {
  //     console.log(filter);
  //     console.log(x);
  //      this.ELEMENT_DATA.push(x);
  //     }
  //  }
  //   })

  this.ELEMENT_DATA = this.cs.removeDuplicatesFromArrayList(res, 'roleId' )
    this.userRole= this.ELEMENT_DATA;

    this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
  onChange() {
    const choosen= this.selecteduserRole[this.selecteduserRole.length - 1];   
    this.selecteduserRole.length = 0;
    this.selecteduserRole.push(choosen);
  }

  

  openDialog(data: any = 'New'){
    if (data != 'New')
    if (this.selecteduserRole.length === 0) {
      this.toastr.error("Kindly select any Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    let paramdata;
    if (this.selecteduserRole.length > 0) {

      paramdata = this.cs.encrypt({ code: this.selecteduserRole[0], pageflow: data });

      this.router.navigate(['/main/userman/userrole-new/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/userman/userrole-new/' + paramdata]);
    }

  }


  
  deleteDialog() {
    if (this.selecteduserRole.length === 0) {
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
      data: this.selecteduserRole[0].code,
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleterecord(this.selecteduserRole[0].roleId,this.selecteduserRole[0].companyCodeId,this.selecteduserRole[0].plantId,this.selecteduserRole[0].languageId,this.selecteduserRole[0].warehouseId);
  
      }
    });
  }
  
  
  deleterecord(roleId: any,companyCodeId:any,plantId:any,languageId:any,warehouseId:any) {
    this.spin.show();
    this.sub.add(this.service.Delete(roleId,warehouseId,companyCodeId,plantId,languageId).subscribe((res) => {
      this.toastr.success(roleId + " Deleted successfully.","Notification",{
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

}


