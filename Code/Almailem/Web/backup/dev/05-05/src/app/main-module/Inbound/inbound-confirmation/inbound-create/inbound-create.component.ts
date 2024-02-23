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
@Component({
  selector: 'app-inbound-create',
  templateUrl: './inbound-create.component.html',
  styleUrls: ['./inbound-create.component.scss']
})
export class InboundCreateComponent implements OnInit {
  screenid: 1054 | undefined;
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
  ngOnInit(): void {

    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
    }

  }
  pageflow = 'New';
  isbtntext: boolean = true;

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;
      if (data.pageflow == 'Display') {

        this.isbtntext = false;
      }


      this.spin.show();
      this.sub.add(this.service.Get(data.code.warehouseId, data.code.preInboundNo, data.code.refDocNumber).subscribe(res => {
        this.code = res;

        res.inboundLine.forEach((x: any) => {
          if (!x.damageQty)
            x.damageQty = 0;
          if (!x.acceptedQty)
            x.acceptedQty = 0;
          x.varianceQty = x.orderQty as number - (x.acceptedQty as number
            + x.damageQty as number)

           // x.qtyreceived =     element.approvedAmount = Math.round(((element.approvedHours / element.timeTicketHours) * element.timeTicketAmount) * 100) / 100;
        });
        this.dataSource = new MatTableDataSource<any>(res.inboundLine);
        this.dataSource.data.forEach(element => {
          element.qtyreceived = ((element.acceptedQty + element.damageQty) / element.orderQty) * 100
        })
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;

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

  confirm() {



    this.spin.show();
    this.sub.add(this.service.confirm(this.code.warehouseId, this.code.preInboundNo, this.code.refDocNumber).subscribe(res => {
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

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  putaway(data): void {


    this.sub.add(this.putservice.searchLine({ lineNo: [data.lineNo], preInboundNo: [data.preInboundNo], refDocNumber: [data.refDocNumber] }).subscribe(res => {
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

  displayedColumns: string[] = ['status', 'lineNo', 'refDocNumber', 'itemCode', 'description', 'manufacturerPartNo', 'orderQty', 'acceptedQty', 'damageQty', 'varianceQty', 'cases', 'putaway', 'qtyreceived',];
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);


  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: variant): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Status ": this.cs.getstatus_text(x.statusId),
        'Line No': x.lineNo,
        'Invoice No': x.refDocNumber,
        "Product Code": x.itemCode,
        "Description": x.description,
        " Mfr Part Noe": x.manufacturerPartNo,
        "Expected Qty": x.orderQty,
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

  getCaulated(){
    
  }

}
