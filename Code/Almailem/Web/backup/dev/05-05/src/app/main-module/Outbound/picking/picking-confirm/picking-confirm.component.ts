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
import { Location } from "@angular/common";
import { AssignHEComponent } from "src/app/main-module/Inbound/Item receipt/item-create/assign-he/assign-he.component";
import { PickingService } from "../picking.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";

export interface clientcategory {

  lineno: string;
  supcode: string;
  one: string;
  two: string;
  three: string;
  four: string;
  five: string;
  accepted: string;
  he: string;

  status: string;
  cases: string;
}
const ELEMENT_DATA: clientcategory[] = [
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },
  { lineno: 'readonly', supcode: 'readonly', one: 'Enter', two: 'readonly', accepted: 'readonly', three: 'readonly', four: 'readonly', five: 'readonly', status: 'readonly', cases: 'readonly', he: 'readonly', },

];
@Component({
  selector: 'app-picking-confirm',
  templateUrl: './picking-confirm.component.html',
  styleUrls: ['./picking-confirm.component.scss']
})
export class PickingConfirmComponent implements OnInit {
  screenid: 1064 | undefined;
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  pickConfirmQty: boolean;

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

  
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: PickingService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService, private location: Location,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService, public dialog: MatDialog,) { }
  sub = new Subscription();
  code: any;
  linedata: any;
  listdata: any[] = [];
  nonZeroInv: any[] = [];
  ngOnInit(): void {

    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
      this.code = js.code;
      this.code.remainingqty = this.code?.pickToQty;
      this.fill(js);
    }

  }
  isbtntext: boolean = true;
  pageflow = 'New';
  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;

      if (data.pageflow == 'Display') {

        this.isbtntext = false;


        this.spin.show();
        this.sub.add(this.service.getpickingline({
          pickupNumber: [this.code.pickupNumber]
        }).subscribe(res => {


          this.spin.hide();
          res.forEach(x => x.allocatedQty = this.code?.pickToQty);
          this.dataSource = new MatTableDataSource<any>(res);
          this.selection = new SelectionModel<any>(true, []);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
          // this.getclient_class(this.form.controls.classId.value);
        }, err => {
          this.cs.commonerror(err);
          this.spin.hide();
        }));
      }
      else {
        debugger
        this.linedata = JSON.stringify({
          pickupQuantity: 0,
          inventoryQuantity: this.code?.inventoryQuantity,
          allocatedQty: this.code?.pickToQty,
          "actualHeNo": null,
          "assignedPickerId": this.code?.assignedPickerId,
          "batchSerialNumber": this.code?.batchSerialNumber,
          "companyCodeId": this.code?.companyCodeId,
          "deletionIndicator": this.code?.deletionIndicator,
          "description": this.code?.description,
          "itemCode": this.code?.itemCode,
          "languageId": this.code?.languageId,
          "lineNumber": this.code?.lineNumber,
          "manufacturerPartNo": this.code?.manufacturerPartNo,
          "partnerCode": this.code?.partnerCode,
          "pickCaseCode": this.code?.proposedPackCode,  
          "pickConfirmQty": '',
          "pickPalletCode": this.code?.pickPalletCode,
          "pickQty": this.code?.pickToQty,
          "pickUom": this.code?.pickUom,
          "pickedPackCode": this.code?.proposedPackBarCode,
          "pickedStorageBin": this.code?.proposedStorageBin,
          "pickupConfirmedBy": this.auth.userID,
          // "pickupConfirmedOn": "2022-04-04T19:32:10.847Z",
          // "pickupCreatedBy": "string",
          // "pickupCreatedOn": "2022-04-04T19:32:10.847Z",
          "pickupNumber": this.code?.pickupNumber,
          // "pickupReversedBy": "string",
          // "pickupReversedOn": "2022-04-04T19:32:10.847Z",
          // "pickupUpdatedBy": "string",
          // "pickupupdatedOn": "2022-04-04T19:32:10.847Z",
          "plantId": this.code?.plantId,
          "preOutboundNo": this.code?.preOutboundNo,
          "refDocNumber": this.code?.refDocNumber,

          "specialStockIndicatorId": this.code?.specialStockIndicatorId,
          // "statusId": ,
          "stockTypeId": this.code?.stockTypeId,
          // "variantCode": 0,
          // "variantSubCode": "string",
          "warehouseId": this.code?.warehouseId,

       //wmsDemo
          //"manufacturerDate": this.code?.manufacturerDate,
//end
        });

        this.spin.show();
        this.sub.add(this.service.GetInventory({
          warehouseId: [this.code.warehouseId], itemCode: [this.code?.itemCode],
          stockTypeId: [this.code?.stockTypeId],
          // storageBin: [this.code?.proposedStorageBin], packBarcodes: [this.code?.packBarcodes]
          storageBin: [this.code?.proposedStorageBin], packBarcodes: [this.code?.proposedPackBarCode]
        }).subscribe(res => {


          this.spin.hide();
          let dataa = JSON.parse(this.linedata);
          dataa.inventoryQuantity = res[0].inventoryQuantity;
          //wmsDemo
          // dataa.batchSerialNumber = res[0].batchSerialNumber;
          // dataa.expiryDate = res[0].expiryDate;
          // dataa.manufacturerDate = res[0].manufacturerDate;
         //end
          this.listdata.push(dataa);

          this.dataSource = new MatTableDataSource<any>(this.listdata);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
          this.selection = new SelectionModel<any>(true, []);
          // this.getclient_class(this.form.controls.classId.value);
        }, err => {
          this.cs.commonerror(err);
          this.spin.hide();
        }));

      }
    }
  }


  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;
  isaddbin = true;

  submitted = false;

  assignhe(): void {
    // if (this.selection.selected.length == 0) {
    //   this.toastr.error(
    //     "Select any line to continue",
    //     "Notification", {
    //     timeOut: 2000,
    //     progressBar: false,
    //   }
    //   )
    //   return;
    // }

    // start CWMS/IW/2022/020 mugilan 02-10-2022
    const BreakError = {};
    try {
    this.dataSource.data.forEach((x: any) => {
  if(!x.pickConfirmQty && x.allocatedQty > 0){
    this.pickConfirmQty = true;
    throw BreakError;
}else{
  this.pickConfirmQty = false;
}

// if(x.pickConfirmQty){
//   x.pickConfirmQty = parseInt(x.pickConfirmQty);
// }
    });
  } catch (err) {
    if (err !== BreakError) throw err;
  }

    if(this.pickConfirmQty == true){
      this.toastr.error(
        "Please enter pickup qty to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
  )
return;
    }

    let totalPutawayQty = 0;
    let toqty: number = this.code?.pickToQty as number;
    console.log(this.selection.selected)

    this.dataSource.data.forEach((x: any) => {

      if(x.pickConfirmQty){
        x['pickConfirmQty1'] = parseInt(x.pickConfirmQty);
      }
      if(x.pickConfirmQty){
      totalPutawayQty = totalPutawayQty + x.pickConfirmQty1;
      }
    });
    if (totalPutawayQty > toqty) {
      this.toastr.error(
        "Total Pickup qty should not be greater than TO Qty",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      )
      return;
    }
    let data: any[] = [];

    console.log(this.dataSource.data)
    this.dataSource.data.forEach((x: any) => {
      if(x.pickConfirmQty || x.allocatedQty > 0){
        data.push(x)
      }
    })
    console.log(data)

     // end of  CWMS/IW/2022/020 mugilan 02-10-2022

     
    // let data = this.dataSource.data.filter(x => ((x.pickConfirmQty as number > 0) || x.pickedStorageBin != this.code?.proposedStorageBin));
   
    // if (data.length == 0) {
    //   this.toastr.error("Please fill the TO Qty","",{
    //     timeOut: 2000,
    //     progressBar: false,
    //   });
    //   return;
    // }


    const dialogRef = this.dialog.open(AssignHEComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '40%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {

        data.forEach((x: any) => {
          x.actualHeNo = result;
        });
        this.spin.show();
        this.sub.add(this.service.confirm(data).subscribe(res => {

          this.toastr.success(this.code?.refDocNumber + ' confirmed Successfully', "", {
            timeOut: 2000,
            progressBar: false,
          })

          this.spin.hide();
          this.location.back()
          // this.getclient_class(this.form.controls.classId.value);
        }, err => {
          this.cs.commonerror(err);
          this.spin.hide();
        }));

      }
    });
  }


  addbin() {
    this.spin.show();
    this.sub.add(this.service.getadditionalBins(this.code?.itemCode, this.code?.outboundOrderTypeId, this.code?.warehouseId, this.code?.proposedPackBarCode, this.code?.proposedStorageBin).subscribe(res => {
  // start CWMS/IW/2022/020 mugilan 03-10-2022
      if (!res || res.length <= 0) {
        this.toastr.error(
          "Stock not available in other bin locations.",
          "Notification", {
          timeOut: 2000,
          progressBar: false,
        }
        )
        this.spin.hide();
        return;
      }

  // end CWMS/IW/2022/020 mugilan 03-10-2022

      this.isaddbin = false;
      this.spin.hide();
      res.forEach((x: any) => {
        let con = JSON.parse(this.linedata);
       
if(x.inventoryQuantity > 0){                       // CWMS/IW/2022/020 mugilan 03-10-2022
  con.inventoryQuantity = x.inventoryQuantity;
  con.allocatedQty = 0;
  con.pickedPackCode = x.packBarcodes;
  con.pickedStorageBin = x.storageBin;
  con.batchSerialNumber = x.batchSerialNumber;

  this.nonZeroInv.push(con);
}
      });
  // start CWMS/IW/2022/020 mugilan 03-10-2022
      if(this.nonZeroInv.length <= 0){
        this.toastr.error(
          "Stock not available in other bin locations.",
          "Notification", {
          timeOut: 2000,
          progressBar: false,
        }
        )
        return;
      }
  // end CWMS/IW/2022/020 mugilan 03-10-2022
      this.nonZeroInv.forEach((element: any) => {

        if (element.inventoryQuantity > 0) {
          this.listdata.push(element);
        }
      })

 
      this.dataSource = new MatTableDataSource<any>(this.listdata);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;


      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerror(err);
      this.spin.hide();
    }));
  }
  displayedColumns: string[] = ['select', 'pickedStorageBin', 'pickedPackCode', 'inventoryQuantity', 'allocatedQty', 'pickConfirmQty', ];
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);

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
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.lineno + 1}`;
  }





  disabled = false;
  step = 0;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  panelOpenState = false;
  //  onKey(event: any, element: any) { // without type info
  //   debugger;
  // if ((element.pickConfirmQty as number) > (element.inventoryQuantity as number)) {
  //   element.pickConfirmQty = 0;
  //   this.toastr.error("To Qty is greater than Inventory Qty","",{
  //     timeOut: 2000,
  //     progressBar: false,
  //   });
  //   return;
  // }
  // let total: number = 0;
  //  let toqty: number = this.code?.pickToQty as number;

  //   this.dataSource.data.forEach((x: any) => { total = total as number + x.pickConfirmQty as number });
  // if (total > toqty) {
  //   element.pickConfirmQty = 0;
  //   this.toastr.error("To Qty is greater than picking Qty", "", {
  //     timeOut: 2000,
  //     progressBar: false,
  //   });
  // }

  // }

  // pickedConfirmed: boolean;

  // pickConfirmChaneged(event){
  //   if(event){
  //     this.pickedConfirmed = true
  //     console.log(this.pickedConfirmed)
  //   }else{
  //     this.pickedConfirmed = false
  //     console.log(this.pickedConfirmed)
  //   }
    
  // }


  clearselection(row: any) {
    if (!this.selection.isSelected(row)) {
      this.selection.clear();
    }
    this.selection.toggle(row);
  }


}

