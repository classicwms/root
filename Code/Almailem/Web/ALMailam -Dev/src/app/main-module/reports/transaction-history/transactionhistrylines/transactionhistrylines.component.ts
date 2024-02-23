import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ReportsService } from '../../reports.service';

@Component({
  selector: 'app-transactionhistrylines',
  templateUrl: './transactionhistrylines.component.html',
  styleUrls: ['./transactionhistrylines.component.scss']
})
export class TransactionhistrylinesComponent implements OnInit {
  inventory:any[]=[];
 // reportService: any;
  searhform: any;
  transfer: any;
  constructor(  public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
   // private service: InboundConfirmationService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private reportService: ReportsService,
    private fb: FormBuilder,
    public cs: CommonService,) { }

  ngOnInit(): void {
    console.log(this.data);
    this.getStockMovementData();
  }
  getStockMovementData() {
    // if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0)
    //   this.selectedWarehouse = this.selectedItems;

   
    const today = new Date();

//this.searhform.controls.itemCode.patchValue([this.searhform.controls.itemCodeFE.value]);

//controls.toDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.toCreatedOn.value));
    let obj1: any = {};
    obj1.warehouseId = [this.auth.warehouseId];
    //this.searhform.controls.toCreatedOn.patchValue(today);
    obj1.companyCodeId = [this.auth.companyId];
    obj1.languageId = [this.auth.languageId];
    obj1.plantId = [this.auth.plantId];
    obj1.fromDeliveryDate = this.cs.day_callapiSearch(this.data.fromCreatedOn);
    obj1.toDeliveryDate = this.cs.day_callapiSearch(this.data.toCreatedOn);
    obj1.fromCreatedOn=this.cs.day_callapiSearch(this.data.fromCreatedOn);
    obj1.toCreatedOn=this.cs.day_callapiSearch(this.data.toCreatedOn);
    obj1.itemCode = [this.data.code];
  //  / obj.warehouseId = [this.selectedWarehouse];

    this.reportService.getStockMovementDetailsNewV2(obj1).subscribe(
      result => {
        this.spin.hide();
        let obj: any = {};
        obj.itemCode = [this.data.code];
        obj.warehouseId = [this.auth.warehouseId];
        obj.binClassId = [1];
        obj.companyCodeId = [this.auth.companyId];
        obj.languageId = [this.auth.languageId];
        obj.plantId = [this.auth.plantId];

        let inventoryQuantity = 0;
        this.reportService.findInventory2(obj).subscribe(
          inventoryResult => {
            inventoryResult.forEach(element => {
              inventoryQuantity = inventoryQuantity + (element.inventoryQuantity != null ? element.inventoryQuantity : 0) + (element.allocatedQuantity != null ? element.allocatedQuantity : 0);
            });
            if (result != null && result.length > 0) {
              //last row data bind 
              let i = result.length;
              result.forEach(element => {
                if (i >= 0) {
                  if (i == result.length) {
                    result[i - 1]['balanceOHQty'] = inventoryQuantity;
                  } else {
                    result[i - 1]['balanceOHQty'] = result[i]['openingStock'];
                  }
                  if (result[i - 1]['documentType'] == 'OutBound') {
                    result[i - 1]['openingStock'] = result[i - 1]['balanceOHQty'] + result[i - 1]['movementQty'];
                  }
                  else {
                    result[i - 1]['openingStock'] = result[i - 1]['balanceOHQty'] - result[i - 1]['movementQty'];
                  }
                  i--;
                
                }
              });
            } else {
              if (inventoryResult != null && inventoryResult.length > 0) {
                let obj: any = {};
                obj.warehouseId = inventoryResult[0].warehouseId;
                obj.manufacturerSKU = inventoryResult[0].referenceField9;
                obj.itemCode = inventoryResult[0].itemCode;
                obj.itemText = inventoryResult[0].description;
                obj.documentType = null;
                obj.documentNumber = null;
                obj.openingStock = inventoryQuantity;
                obj.newmMovementQty = 0;
                obj.balanceOHQty = inventoryQuantity;
                obj.customerCode = null;
                obj.confirmedOn = null;
                obj.companyDescription=inventoryResult[0].companyDescription,
                obj.plantDescription=inventoryResult[0].plantDescription,
                obj.warehouseDescription=inventoryResult[0].warehouseDescription,
                obj.manufacturerName=inventoryResult[0].manufacturerName,
                
                result.push(obj);
              }
            }
            this.transfer = result;

            this.transfer.forEach((x) => {
              if (x.documentType == "OutBound") {
                x['newmMovementQty'] = '-' + (x.movementQty)
              }
              if (x.documentType == "InBound") {
                x['newmMovementQty'] = '+' + (x.movementQty)
              }
            })
           
          });
      },
      error => {
        console.log(error);
        this.spin.hide();
      }
    );
  }
}
