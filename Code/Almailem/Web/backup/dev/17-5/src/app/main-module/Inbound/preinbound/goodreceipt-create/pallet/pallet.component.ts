import { SelectionModel } from "@angular/cdk/collections";
import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AssignPickerComponent } from "src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component";
import { GoodsReceiptService } from "../../../Goods-receipt/goods-receipt.service";
import { Table } from "primeng/table";

export interface variant {


  no: string;
  dimensions: string;
  width: string;
  uom: string;
}
const ELEMENT_DATA: variant[] = [
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },
  { no: "1", dimensions: 'Value', width: 'Enter', uom: 'dropdown', },



];
@Component({
  selector: 'app-pallet',
  templateUrl: './pallet.component.html',
  styleUrls: ['./pallet.component.scss']
})
export class PalletComponent implements OnInit {

  assignedCase: any[] = [];
  selectedAssignedCase : any[] = [];
  @ViewChild('assignedCaseTag') assignedCaseTag: Table | any;
  
  constructor(public dialogRef: MatDialogRef<PalletComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: GoodsReceiptService, private dialog: MatDialog,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    public cs: CommonService,) { }
  barcodeList: any[] = [];

  statusId = 13;
  ngOnInit(): void {
    if (this.data.statusId == 13) {
      this.spin.show();
      this.sub.add(this.service.searchLine({ preInboundNo: [this.data.preInboundNo], }).subscribe(res => {
        let result = res.filter((x: any) => x.statusId == this.statusId);
        this.rowcount = res.length;
        this.assignedCase = result;
        this.spin.hide();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
    else {
      this.data.caseCode.forEach((y: any) => { this.barcodeList.push({ lineNo: this.data.lineNo, invoiceNo: this.data.invoiceNo, refDocNumber: this.data.refDocNumber, containerNo: this.data.containerNo, itemCode: this.data.itemCode, caseCode: y, nooflable: 1, orderUom: this.data.orderUom, assignedUserId: this.data.assignedUserId }) });
      this.rowcount = this.barcodeList.length;
      this.assignedCase = this.barcodeList;
    }

  }

  
  rowcount = 0;
  sub = new Subscription();
  Delete() {
    if (this.selectedAssignedCase.length === 0) {
      this.toastr.error("Kindly select one Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selectedAssignedCase.length > 1) {
      this.toastr.error("Kindly select one Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    if (this.selectedAssignedCase[0].statusId == 14) {
      this.toastr.error("Order can't be Deleted ", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.spin.show();
    this.sub.add(this.service.deleteLine(this.data.lineNo, this.selectedAssignedCase[0].caseCode, this.data.itemCode, this.data.preInboundNo, this.selectedAssignedCase[0].stagingNo).subscribe(res => {


      this.toastr.success(this.selectedAssignedCase[0].caseCode + " deleted successfully!", "", {
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.dialogRef.close();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  Confirm() {

    if (this.selectedAssignedCase.length === 0) {
      this.toastr.error("Kindly select one Row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }


    let dataaa: any[] = this.selectedAssignedCase.filter(x => x.status != 14);
    this.spin.show();

    this.sub.add(this.service.caseConfirmation(dataaa, dataaa[0].caseCode).subscribe(res => {
      this.toastr.success(dataaa[0].caseCode + " Confirm successfully!", "", {
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.dialogRef.close();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }


  assignhht(): void {
    if (this.selectedAssignedCase.length === 0) {
      this.toastr.error("Kindly select one Row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    // if (list.length > 0) {
    //   this.toastr.error("Order can't be Assign hht ", "");
    //   return;
    // }
    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
     if(result){
      let list = this.selectedAssignedCase.filter((row: any) => row.statusId != 14)

      list.forEach((x: any) => { x.assignedUserId = result; });
      this.spin.show();
      this.sub.add(this.service.assignHHTUser(list, result).subscribe(res => {
        this.toastr.success(result + " Saved Successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();


      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
     }
    });
  }
}
