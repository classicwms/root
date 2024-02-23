import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { Table } from 'primeng/table';
import { ItemReceiptService } from '../../item-receipt.service';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { ToastrService } from 'ngx-toastr';
import { CreateBarcodeComponent } from '../create-barcode/create-barcode.component';

@Component({
  selector: 'app-inventory-details',
  templateUrl: './inventory-details.component.html',
  styleUrls: ['./inventory-details.component.scss']
})
export class InventoryDetailsComponent implements OnInit {

  inventory: any[] = [];
  @ViewChild('inventoryTag') inventoryTag: Table | any;

  constructor(public dialog: MatDialog, public dialogRef: MatDialogRef<InventoryDetailsComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
  private spin: NgxSpinnerService, private service: ItemReceiptService, private cs: CommonService, public toastr: ToastrService,) { }
  sub = new Subscription();
  ngOnInit(): void {
    console.log(this.data)
    this.inventory = this.data.lines;
  }



  proceedBarcode(element: any){
    if(!element.barCodeId){
      element.packBarcode = []
     }
     if(element.acceptedQty == null || element.acceptedQty == ''){
       element.acceptedQty = 0;
     }
     let data: any[] = [];
     if (element.packBarcodes.length == 0) {
       this.spin.show();
       this.sub.add(this.service.packBarcode(element.acceptedQty, element.damageQty, element.warehouseId).subscribe(res => {
         element.packBarcodes = res;
         res.forEach((x: any) => {
           data.push({
             packBarcodes: x.barcode, Qty: x.quantityType == 'A' ? element.acceptedQty : element.damageQty, Type: x.quantityType == 'A' ? '' : 'D', warehouseId: element.warehouseId,
             nooflable: 1, quantityType: x.quantityType, itemCode: element.itemCode, invoiceNo: element.invoiceNo, itemDescription: element.itemDescription,
             manufacturerPartNo: element.manufacturerPartNo
           });
         });

         sessionStorage.setItem(
          'barcode',
          JSON.stringify({ BCfor: "barcode", list: data })
        );
        window.open('#/barcodeNew', '_blank');
    
        this.dialogRef.close(data);
        
         this.spin.hide();
       }, err => {
         this.cs.commonerrorNew(err);
         this.spin.hide();
       }));
 
     }
  }

  


  createBarocde(element): void {
    if(element.acceptedQty == null || element.acceptedQty == ''){
      element.acceptedQty = 0;
    }
    const dialogRef = this.dialog.open(CreateBarcodeComponent, {
      disableClose: true,
      width: '85%',
      maxWidth: '65%',
      data: { lines: element}
    });
    dialogRef.afterClosed().subscribe(result => {
    });
}
  
}
