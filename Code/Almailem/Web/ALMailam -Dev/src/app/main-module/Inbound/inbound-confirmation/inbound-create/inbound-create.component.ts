import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { InboundConfirmationService } from "../inbound-confirmation.service";
import { PalletdetailsComponent, variant } from "./palletdetails/palletdetails.component";
import { PutawayDetailsComponent } from "./putaway-details/putaway-details.component";

import { Location } from "@angular/common";
import { GoodsReceiptService } from "../../Goods-receipt/goods-receipt.service";
import { PutawayService } from "../../putaway/putaway.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Table } from "primeng/table";
import { ConfirmEditComponent } from "./confirm-edit/confirm-edit.component";
import { Customdialog2Component } from "src/app/common-field/customdialog2/customdialog2.component";
import { CustomdialogComponent } from "src/app/common-field/customdialog/customdialog.component";
@Component({
  selector: 'app-inbound-create',
  templateUrl: './inbound-create.component.html',
  styleUrls: ['./inbound-create.component.scss']
})

export class InboundCreateComponent implements OnInit {
  screenid = 3054;
  inbound: any[] = [];
  selectedinbound: any[] = [];
  @ViewChild('putawayTag') putawayTag: Table | any;

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: InboundConfirmationService,
    private putservice: PutawayService,
    private caseservice: GoodsReceiptService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService, private location: Location,
    private route: ActivatedRoute, private router: Router,
    public cs: CommonService, public dialog: MatDialog,) { }
  sub = new Subscription();
  code: any;
  pageflow1: any;
  ngOnInit(): void {

    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);

      this.fill(js);
   this.pageflow1 = js.pageflow1;
      this.code = js.code;
    }

  }
  pageflow = 'New';
  isbtntext: boolean = true;

remarks(data){
  const dialogRef = this.dialog.open(Customdialog2Component, {
    // width: '60%', height: '70%',
    width: '25%',
    maxWidth: '30%',
    position: {
      top: '6.7%',
    },

    data: { title: "Remarks", body: "Remarks",value: data.referenceField1},
  });
  dialogRef.afterClosed().subscribe(result => {
    console.log(result);
    if (result != null) {
      data.referenceField1 = result.remarks;
    }
  });
}

  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;
      if (data.pageflow == 'Display') {

        this.isbtntext = false;
      }


      this.spin.show();
      this.sub.add(this.service.Get(data.code.warehouseId, data.code.preInboundNo, data.code.refDocNumber, this.auth.companyId, data.code.languageId, data.code.plantId).subscribe(res => {
        this.code = res;

        res.inboundLine.forEach((x: any) => {
          if (!x.damageQty)
            x.damageQty = 0;
          if (!x.acceptedQty)
            x.acceptedQty = 0;
          x.varianceQty = x.orderQty as number - (x.acceptedQty as number
            + x.damageQty as number);

            x.qtyreceived = ((x.acceptedQty + x.damageQty) / x.orderQty) * 100
        });
        this.inbound = res.inboundLine;

        //this.form.patchValue(res, { emitEvent: false });
        // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        // this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
        // this.form.controls.referenceField2.patchValue(this.cs.dateapi(this.form.controls.referenceField2.value));
        this.spin.hide();
        // this.getclient_class(this.form.controls.classId.value);
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
  }
  
  cancel(){
    console.log(this.pageflow1);
    if(this.pageflow1 == "Inbound Summary"){
      this.router.navigate(["/main/inbound/inbound-confirm"]);
    }
    else{
      this.router.navigate(["/main/inbound/readytoConfirm"]);
    }
  }
  confirmAnyway = false;
  confirmPopup(dataErrorArray) {
    const dialogRef = this.dialog.open(CustomdialogComponent, {
      // width: '60%', height: '70%',
      width: '50%',
      maxWidth: '80%',
      position: {
        top: '6.7%',
      },

      data: { title: "GR Confirm", body: "Putaway not confirmed in line no: " + dataErrorArray + "\n, Do you still want to confirm?" },
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result == 'Yes') {
        this.confirmAnyway = true;
        this.confirm();
      }
    });
  }

  confirm() {


    let dataErrorArray: any = [];
    this.inbound.forEach(data => {
      if (data.statusId != 20) {
        dataErrorArray.push(data.lineNo);
      }
    });
    if (dataErrorArray.length > 0 && this.confirmAnyway == false) {
      // this.toastr.error(
      //   "Putaway not confirmed in line no : " + dataErrorArray,
      //   "Notification", {
      //   timeOut: 2000,
      //   progressBar: false,
      // }
      // );
      this.confirmPopup(dataErrorArray);
      this.cs.notifyOther(true);
      return;
    }

    //changes confirm to confirm v2 on 18th Sep 2023 
    this.spin.show();
    this.sub.add(this.service.confirmV2(this.code.warehouseId, this.code.preInboundNo, this.code.refDocNumber, this.code.plantId, this.auth.companyId, this.code.languageId).subscribe(res => {
      this.location.back();
      this.spin.hide();
      this.toastr.success(this.code.refDocNumber + " Confirm Successfully.", "", {
        timeOut: 2000,
        progressBar: false,
      })
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      if (err.error.error) {
        this.toastr.error(err.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      } else {
        this.cs.commonerrorNew(err);
      }
      this.spin.hide();
    }));

  }
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
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
  openDialog(data: any = 'new', type?: any): void {
    // console.log(this.selectedPreinbound)
    if (data != 'New')
      if (this.selectedinbound.length === 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    const dialogRef = this.dialog.open(ConfirmEditComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: this.inbound[0] }
    });

    dialogRef.afterClosed().subscribe(result => {




    });
  }
  putaway(data): void {


    this.sub.add(this.putservice.searchLinev2({ lineNo: [data.lineNo], preInboundNo: [data.preInboundNo], refDocNumber: [data.refDocNumber],itemCode:[data.itemCode] }).subscribe(res => {
      this.spin.hide();
      let putAwayLineArray: any[] = [];
      let result = res.filter((x: any) => x.deletionIndicator == 0);
      if (result) {
        result.forEach(element => {
          if (element.putawayConfirmedQty != null && element.putawayConfirmedQty > 0) {
            putAwayLineArray.push(element);
          }
        });
      }
      const dialogRef = this.dialog.open(PutawayDetailsComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: putAwayLineArray
      });

      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });

      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }
  pallet(data: any): void {

    this.sub.add(this.caseservice.searchLine({ lineNo: [data.lineNo], preInboundNo: [data.preInboundNo], refDocNumber: [data.refDocNumber] }).subscribe(res => {

      const dialogRef = this.dialog.open(PalletdetailsComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: res
      });

      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
      });
      this.spin.hide();
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }



  onChange() {
    const choosen = this.selectedinbound[this.selectedinbound.length - 1];
    this.selectedinbound.length = 0;
    this.selectedinbound.push(choosen);
  }







  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.inbound.forEach(x => {
      res.push({

        "Status ": x.statusDescription,
        'Line No': x.lineNo,
        'Invoice No': x.refDocNumber,
        "Part No": x.itemCode,
        "Description": x.description,
        " Mfr Part Noe": x.manufacturerPartNo,
        "Order Qty": x.orderQty,
        "Accepted Qty": x.acceptedQty,
        "Damaged Qty": x.damageQty,
        "Variance": x.varianceQty,
        "% of Quantity Received": x.qtyreceived,

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Inbound Confirmation");
  }

  getCaulated() {

  }

}
