import { SelectionModel } from '@angular/cdk/collections';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { GoodsReceiptService } from '../../../Goods-receipt/goods-receipt.service';

@Component({
  selector: 'app-pallet1',
  templateUrl: './pallet1.component.html',
  styleUrls: ['./pallet1.component.scss']
})
export class Pallet1Component implements OnInit {
  screenid: 1048 | undefined;
  sub = new Subscription();
  noOfCase: number = 1;
  barcodeList: any[] = [];
  noOfCaseCode: any[] = [];
  constructor(public dialogRef: MatDialogRef<Pallet1Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: GoodsReceiptService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private cs: CommonService,) { debugger; }

  ngOnInit(): void {
    debugger;
  }
  print() {
    let list: any[] = [];
    this.dataSource.data.forEach((x: any) => {

        list.push({ asn: x.refDocNumber, inv: x.invoiceNo, container_no: x.containerNo, case_no: x.caseCode, warehouseId: this.data[0].warehouseId });
   
    });
    this.noOfCaseCode = this.cs.removeDuplicatesFromArrayNew(list);
    sessionStorage.setItem(
      'barcode',
      JSON.stringify({ BCfor: "case", list: this.noOfCaseCode })
    );
    window.open('#/barcode', '_blank');

    this.dialogRef.close(this.data);

  }

  show = false;
  toggleFloat() {
    this.show = !this.show;
  }

  displayedColumns: string[] = ['select', 'no', 'dimensions','caseCode'];
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
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }
  isgenerate: any = false;
  generate() {
    this.isgenerate = true;
    this.spin.show();
    this.sub.add(this.service.GetBarcode(this.noOfCase, this.data[0].warehouseId).subscribe(res => {
      this.spin.hide();
      res.forEach((y: any) => {
        this.data.forEach((x: any) => {

          x?.caseCode.push(y)
        });
      });
      res.forEach((y: any) => {
        this.data.forEach((x: any) => {
          this.barcodeList.push({ lineNo: x.lineNo, invoiceNo: x.invoiceNo, refDocNumber: x.refDocNumber, containerNo: x.containerNo, itemCode: x.itemCode, caseCode: y, nooflable: 1, orderUom: x.orderUom, assignedUserId: x.assignedUserId }
          );
        });
      });

      this.dataSource = new MatTableDataSource<any>(this.barcodeList);
      this.selection = new SelectionModel<any>(true, []);
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }
}
