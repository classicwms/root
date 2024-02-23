import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PutawayService } from '../../../putaway/putaway.service';
import { forkJoin,of } from 'rxjs';
import { ReportsService } from 'src/app/main-module/reports/reports.service';
import { catchError } from 'rxjs/operators';


@Component({
  selector: 'app-putaway-add-lines',
  templateUrl: './putaway-add-lines.component.html',
  styleUrls: ['./putaway-add-lines.component.scss']
})
export class PutawayAddLinesComponent implements OnInit {

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private reportService: ReportsService,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : PutawayService,
    ) { }


  form = this.fb.group({
    languageId: [],
    companyCode: [],
    plantId: [],
    warehouseId: [],
    preInboundNo: [],
    refDocNumber: [],
    goodsReceiptNo: [],
    palletCode: [],
    caseCode: [],
    packBarcodes: [],
    lineNo: [],
    itemCode: [],
    inboundOrderTypeId: [],
    variantCode: [],
    variantSubCode: [],
    batchSerialNumber: [],
    stockTypeId: [],
    specialStockIndicatorId: [],
    storageMethod: [],
    statusId: [],
    businessPartnerCode: [],
    containerNo: [],
    invoiceNo: [],
    itemDescription: [],
    manufacturerPartNo: [],
    hsnCode: [],
    variantType: [],
    specificationActual: [],
    itemBarcode: [],
    orderQty: [],
    putawayConfirmedQty:[],
    orderUom: [],
    receiptQty: [],
    grUom: [],
    acceptedQty: [],
    damageQty: [],
    quantityType: [],
    assignedUserId: [],
    putAwayHandlingEquipment: [],
    confirmedQty: [],
    remainingQty: [],
    referenceOrderNo: [],
    referenceOrderQty: [],
    crossDockAllocationQty: [],
    manufacturerDate: [],
    expiryDate: [],
    storageQty: [],
    remarks: [],
    cbmUnit: [],
    referenceField1: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    referenceField10: [],
    deletionIndicator: [],
    createdBy: [],
    createdOn: [],
    updatedBy: [],
    updatedOn: [],
    confirmedBy: [],
    confirmedOn: [],
    inventoryQuantity: [],
    barcodeId: [],
    cbm: [],
    manufacturerCode: [],
    manufacturerName: [],
    origin: [],
    brand: [],
    rejectType: [],
    rejectReason: [],
    cbmQuantity: [],
    companyDescription: [],
    plantDescription: [],
    warehouseDescription: [],
    statusDescription: [],
    interimStorageBin: [],
    confirmedStorageBin: [],
    putAwayNumber: [],
    proposedStorageBin: [],
  });


  ngOnInit(): void {
    this.form.controls.lineNo.disable();
    this.form.controls.itemCode.disable();
    this.form.controls.itemDescription.disable();
    this.form.controls.manufacturerName.disable();
    this.form.controls.orderQty.disable();
    //this.form.controls.confirmedStorageBin.patchValue(this.data.confirmedStorageBin.value);
    console.log(this.data);
    this.form.patchValue(this.data);
  }
  multiselectStorageList: any[] = [];
  storageBinList1: any[] = [];
  selectedStorageBin: any[] = [];
  onStorageType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        storageList: this.reportService.getStorageDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ storageList }) => {
          if (storageList != null && storageList.length > 0) {
            console.log(3)
            this.multiselectStorageList = [];
            this.storageBinList1 = storageList;
            this.storageBinList1.forEach(x => this.multiselectStorageList.push({ value: x.storageBin, label: x.storageBin}))
          }
        });
    }
  }
  showDropdown1: true;
  showDropdown(){
    this.showDropdown1 = true;
  }

back(){
  this.dialogRef.close();
}
submit(){
  
  let obj: any = {};
  console.log(this.form.value);
  console.log(this.data)
  obj.data = this.form.value;
  this.dialogRef.close(this.form.getRawValue());
  console.log(this.form.getRawValue());
}
}
