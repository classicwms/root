import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { InhouseTransferService } from '../../make&change/inhouse-transfer/inhouse-transfer.service';
import { PreoutboundService } from '../../Outbound/preoutbound/preoutbound.service';
import { UpdateInventoryComponent } from '../update-inventory/update-inventory.component';

@Component({
  selector: 'app-update-outbound-line',
  templateUrl: './update-outbound-line.component.html',
  styleUrls: ['./update-outbound-line.component.scss']
})
export class UpdateOutboundLineComponent implements OnInit {
  itemCode: any;
  preOutboundNo: any;
  refDocNumber: any;
  deliveryQty: any;
  statusId: any;
  outboundOrderTypeId: any;
  allocatedQuantity: number;
  inventoryQuantity: number;
  lineNumber: any;
  partnerCode: any;
  showHiddenFields: boolean;



  constructor(private outbound: PreoutboundService, private spin: NgxSpinnerService, public cs: CommonService,   public auth: AuthService,
    public toastr: ToastrService,
    public dialogRef: MatDialogRef<UpdateInventoryComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    ) { }
  
  sub = new Subscription();

  ngOnInit(): void {
    this.showHiddenFields = false;
  }
  multipreOutboundNo: any[] = [];
  multirefDocNumber: any[] = [];
  multideliveryQty: any[] = [];
  multistatusId: any[] = [];



  submit() {

      if(!this.itemCode){
    this.toastr.error(
      "Please enter item code to continue",
      "Notification", {
      timeOut: 2000,
      progressBar: false,
    }
    )
    return;
  }

    let obj: any = {};

    obj.deliveryQty = Number;
    obj.statusId = Number;
    obj.outboundOrderTypeId = Number;

    if(this.statusId != null) {
      obj.deliveryQty = Number(this.deliveryQty);
    }
    if (this.statusId != null) {
      obj.statusId = Number(this.statusId);
    }
    if (this.outboundOrderTypeId != null) {
      obj.outboundOrderTypeId = Number(this.outboundOrderTypeId);
    }

    this.sub.add(this.outbound.updateOutBoundLine(this.lineNumber, this.itemCode, this.partnerCode, this.preOutboundNo,this.refDocNumber, this.auth.warehouseId, obj).subscribe(res => {
      this.toastr.success("Inventory updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });

      this.itemCode = null;
      this.lineNumber = null;
      this.partnerCode = null;
      this.preOutboundNo = null;
      this.refDocNumber = null;
      this.deliveryQty  = 0;
      this.statusId  = 0;
     // this.dialogRef.close();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }

}
