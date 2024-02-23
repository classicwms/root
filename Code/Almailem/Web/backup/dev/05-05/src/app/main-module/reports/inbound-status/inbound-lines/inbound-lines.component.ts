import { SelectionModel } from '@angular/cdk/collections';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { InboundConfirmationService } from 'src/app/main-module/Inbound/inbound-confirmation/inbound-confirmation.service';
import { HandlingEquipmentService } from 'src/app/main-module/Masters -1/other-masters/handling-equipment/handling-equipment.service';

@Component({
  selector: 'app-inbound-lines',
  templateUrl: './inbound-lines.component.html',
  styleUrls: ['./inbound-lines.component.scss']
})
export class InboundLinesComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private service: InboundConfirmationService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
  ) { }
  sub = new Subscription();
  ngOnInit(): void {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.warehouseId, this.data.preInboundNo, this.data.refDocNumber).subscribe(res => {

      res.inboundLine.forEach((x: any) => {
        if (!x.damageQty)
          x.damageQty = 0;
        if (!x.acceptedQty)
          x.acceptedQty = 0;
        x.varianceQty = x.orderQty as number - (x.acceptedQty as number
          + x.damageQty as number)

         // x.qtyreceived =     element.approvedAmount = Math.round(((element.approvedHours / element.timeTicketHours) * element.timeTicketAmount) * 100) / 100;
      });
      this.spin.hide();
      this.dataSource = new MatTableDataSource<any>(res.inboundLine);

      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }))
  }


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  
  ELEMENT_DATA: any[] = [];
  displayedColumns: string[] = [  'lineNo', 'itemCode', 'description', 'manufacturerPartNo',
  'orderQty', 'acceptedQty', 'damageQty', 'varianceQty'];
  dataSource = new MatTableDataSource < any > (this.ELEMENT_DATA);
  selection = new SelectionModel < any > (true, []);


  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row ? : any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.invoiceNumber + 1}`;
  }

  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  submit(){
    if (this.selection.selected.length === 0) {
      this.toastr.warning("Kindly select any Row", "Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.dialogRef.close(this.selection.selected[0].customerCode);
  }
  
}

