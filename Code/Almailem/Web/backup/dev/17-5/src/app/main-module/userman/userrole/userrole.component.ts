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
  advanceFilterShow: boolean;
  @ViewChild('SetupuserRole') SetupuserRole: Table | undefined;
  userRole : any[] = [];
  selecteduserRole : any[] = [];
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  constructor(private service:UserroleService ,
    private spin: NgxSpinnerService, public cs : CommonService,   public toastr: ToastrService, private router: Router) { }

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
  ngOnInit(): void {
    this.getAll();
  }
  ELEMENT_DATA: any[] = [];
  getAll() {
    this.spin.show();
    this.sub.add(this.service.Getall().subscribe(res => {
      this.ELEMENT_DATA = [];
      // res = [...new Set(res.map(item => item.userRoleId, item.userRoleName, item.roleStatus))];
      res.forEach((x) => {
        x.statusId = x.statusId == true ? 'Active' : 'InActive';
        let filter = this.cs.filterArray(this.ELEMENT_DATA, { roleId: x.roleId })
        if (filter.length == 0)
          this.ELEMENT_DATA.push(x);
      })
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
}


