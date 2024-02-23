import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { ItemReceiptService } from '../../item-receipt.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { AuthService } from 'src/app/core/Auth/auth.service';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-assign-he',
  templateUrl: './assign-he.component.html',
  styleUrls: ['./assign-he.component.scss']
})
export class AssignHEComponent implements OnInit {
  screenid: 1048 | undefined;
  constructor(public dialogRef: MatDialogRef<AssignHEComponent>, private service: ItemReceiptService, private auth: AuthService,private masterService: MasterService,
    public toastr: ToastrService,
     private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { }
  heno: any;
  ngOnInit(): void {
    this.dropdownlist();
  }
  sub = new Subscription();
  submit() {


    this.spin.show();
    this.sub.add(this.service.getheVaidate(this.heno,this.auth.companyId,this.auth.plantId,this.auth.languageId).subscribe(res => {
      this.dialogRef.close(this.heno);
      this.spin.hide();
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));




  }
  handlingList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
    
      this.cas.dropdownlist.master.handlingequipment.url,

   
   //  this.cas.dropdownlist.setup.uomid.url,
    ]).subscribe((results) => {

 this.masterService.searchhandlingequipment({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
  this.handlingList = [];
  res.forEach(element => {
    this.handlingList.push({value: element.handlingEquipmentId, label: element.handlingEquipmentId});
  });
});


 

    });
  
    this.spin.hide();
  }


}
