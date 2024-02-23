import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AssignInvoiceComponent } from "./assign-invoice/assign-invoice.component";


import { Location } from "@angular/common";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PreinboundService } from "../preinbound.service";
import { ContainerReceiptService } from "../../Container-receipt/container-receipt.service";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Table } from "primeng/table";
import { PreinboundeditpopupComponent } from "./preinboundeditpopup/preinboundeditpopup.component";
import { log } from "console";
import { OverlayPanel } from "primeng/overlaypanel";

interface SelectItem {
  id: number;
  itemName: string;
}



@Component({
  selector: 'app-preinbound-new',
  templateUrl: './preinbound-new.component.html',
  styleUrls: ['./preinbound-new.component.scss']
})
export class PreinboundNewComponent implements OnInit {
  screenid= 3045 ;
  toggle = true;
  //overlayPanel= false;
  preInboundNew: any;
  selectedPreInboundNew : any[] = [];
  @ViewChild('preInboundNewTag') preInboundNewTag: Table | any;
  @ViewChild('buttonElement') buttonElement: any; 
  @ViewChild('op1') overlayPanel: OverlayPanel;
  displayedColumns: string[] = ['select', 'statusId', 'invoiceNo', 'lineno', 'itemCode', 'itemDescription', 'manufacturerCode','manufacturerName', 'businessPartnerCode', 'orderQty', 'orderUom', 'stockTypeId', 'expectedArrivalDate',];
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;

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
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: PreinboundService, private location: Location,
    public toastr: ToastrService, private dialog: MatDialog,
    private sservice: ContainerReceiptService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    public cs: CommonService,) { }
  sub = new Subscription();



  containerList: any[] = [];
  filtercontainerList: any[] =[];
  selectedItems1: SelectItem[] = [];
  multiitemlistList: SelectItem[] = [];
  multiSelectcontainerList: SelectItem[] = [];

  dropdownSettings = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

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
  showStorageDetails: any;
  openOverlayPanel(data) {
    this.showStorageDetails = data.inventoryDetail
  }

  onSort(event: any): void {
    // Update the data source based on the sorting event
    // Sort the data by lineNo in ascending order
    this.preInboundNew.sort((a, b) => a.lineNo.localeCompare(b.lineNo));
  
    // If descending order is needed, you can reverse the array
    if (event.order === -1) {
      this.preInboundNew.reverse();
    }
  }


  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({
    companyCode: [],
    containerNo: [, [Validators.required]],
    containerType: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    inboundOrderTypeId: [],
    languageId: [],
    noOfContainers: [],
    plantId: [],
    preInboundLineV2: [],
    preInboundNo: [],
    refDocDate: [],
    refDocNumber: [],
    referenceDocumentType: [],
    referenceField1: [],
    referenceField10: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    referenceField7: [],
    referenceField8: [],
    referenceField9: [],
    statusDescription:[],
    statusId: [],
    updatedBy: [],
    updatedOn: [],
    warehouseId: [],
  });

  submitted = false;
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    // if (this.email.hasError('required')) {
    //   return ' Field should not be blank';
    // }
    return this.email.hasError('required') ? ' Field should not be blank' : '';
      
  }

  isbtntext = true;
js: any;
  code: any;
  ngOnInit(): void {
    this.form.disable();
    this.form.controls.containerNo.enable();
    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
      this.js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
    }


  }

  isProcess = true;
  btntext = "Save";
  pageflow = "New";
  fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = "Edit";
      this.btntext = 'Update';
      this.form.controls.preInboundNo.disable();
      this.form.controls.warehouseId.disable();
      if (data.pageflow == 'Display') {
        this.form.disable();
        this.isbtntext = false;
      }

      let obj: any = {};
      obj.companyCodeId = [this.auth.companyId];
      obj.warehouseId=[this.auth.warehouseId];
      obj.plantId=[this.auth.plantId];
      obj.languageId = [this.auth.languageId];
      obj.preInboundNo=[data.code.preInboundNo];
      this.spin.show();
      this.sub.add(this.service.searchLine(obj).subscribe(res => {
        this.form.patchValue(res, { emitEvent: false });
       // this.form.controls.containerNo.patchValue([{id: res.containerNo,itemName: res.containerNo}]);
      
         this.form.controls
         this.form.controls.containerNo.patchValue(data.code.containerNo);
         this.form.controls.refDocNumber.patchValue(data.code.refDocNumber);
         this.form.controls.referenceDocumentType.patchValue(data.code.referenceDocumentType);
         this.form.controls.refDocDate.patchValue(data.code.refDocDate);
         this.form.controls.statusDescription.patchValue(data.code.statusDescription);
         this.form.controls.createdBy.patchValue(data.code.createdBy);
         this.form.controls.containerNo.disable();
        if (data.code.statusId == 5)
          this.isProcess = false;
        this.spin.hide();
        this.preInboundNew = res;
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));

      this.spin.show();
      this.sub.add(this.sservice.Getall().subscribe(res => {
        this.packBarcodesList1 = res.filter((x: any) => !x.refDocNumber);
        this.packBarcodesList = this.packBarcodesList1.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        console.log(this.packBarcodesList)
        this.packBarcodesList.forEach(x => this.multiitemlistList.push({id: x.containerNo, itemName: x.containerNo}))
        this.multiSelectcontainerList = this.multiitemlistList;
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
  }
  packBarcodesList: any[] = [];  
  packBarcodesList1: any[] = [];
  submit() {

   this.form.patchValue({containerNo: this.selectedItems1[0].id});
    let isnotinvoice = false;

    this.preInboundNew.forEach((x: any) => {
      x.containerNo = this.form.controls.containerNo.value;
      x.inboundOrderTypeId = this.form.controls.inboundOrderTypeId.value;

      if (!x.invoiceNo) isnotinvoice = true;
    });
    if (isnotinvoice) {
      this.toastr.error(
        "Please fill invoiceNo to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      )
      return;
    }
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.spin.show();
    this.sub.add(this.service.processASN(this.preInboundNew).subscribe(res => {
      this.toastr.success(this.form.controls.preInboundNo.value + " Processed successfully!","",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.spin.show();
      this.sub.add(this.sservice.search({ containerNo: [this.form.controls.containerNo.value] }).subscribe(res => {
        //   this.toastr.success(this.form.controls.preInboundNo.value + " Processed successfully!");
        this.spin.hide();
        this.spin.show();
        this.sub.add(this.sservice.Update({ refDocNumber: this.form.controls.refDocNumber.value }, res[0].containerReceiptNo,  res[0].companyCode, res[0].plantId, res[0].languageId, res[0].warehoueId,).subscribe(res => {
          //   this.toastr.success(this.form.controls.preInboundNo.value + " Processed successfully!");
          this.spin.hide();
          this.location.back();

        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));


      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));


    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));







    return;

    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        ""
      );

      this.cs.notifyOther(true);
      return;
    }

    this.cs.notifyOther(false);
    this.spin.show();
    this.form.removeControl('updatedOn');
    this.form.removeControl('createdOn');
    this.form.controls.preInboundLineV2.patchValue(this.preInboundNew);


    this.form.patchValue({ updatedby: this.auth.userID });
    if (this.code) {
      this.sub.add(this.service.Update(this.form.getRawValue(), this.code.preInboundNo, this.code.warehouseId).subscribe(res => {
        this.toastr.success(res.preInboundNo + " updated successfully!");
        this.spin.hide();
        this.location.back();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.preInboundNo + " Saved Successfully!");
        this.spin.hide();
        this.location.back();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  };
  back() {
    this.location.back();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
  
  openDialogHeader(data: any = 'New' ): void {
   // console.log(this.selectedPreinbound)
      if (data != 'New')
      if (this.selectedPreInboundNew.length === 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
      const dialogRef = this.dialog.open(PreinboundeditpopupComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        data: { pageflow: data, code: this.preInboundNew}
      });
    
      dialogRef.afterClosed().subscribe(result => {
        console.log(result)
        if(result)
      
           this.preInboundNew=[];
           this.fill(this.js);
    
            
          
      });
   }
  assign() {
    if (this.selectedPreInboundNew.length === 0) {
      this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      return;
    }
    const dialogRef = this.dialog.open(AssignInvoiceComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.selectedPreInboundNew.forEach((x: any) => x.invoiceNo = result);
    });

  }
  bom() {
    if (this.selectedPreInboundNew.length === 0) {
      this.toastr.error("Kindly select any one Row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      return;
    }
    if (this.selectedPreInboundNew.length > 1) {
      this.toastr.error("Kindly select any one Row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
      return;
    }
    let data = this.selectedPreInboundNew[0];
    this.spin.show();
    this.sub.add(this.service.createbom(data.itemCode, data.lineNo, data.preInboundNo, data.refDocNumber, data.warehouseId).subscribe(res => {
      this.toastr.success(res.itemCode + " BOM created successfully!");
      this.spin.hide();
      this.location.back();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));

  }

  onItemSelect(item: any) {
    console.log(item);
  }

OnItemDeSelect(item:any){
    console.log(item);
    console.log(this.selectedItems1);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}


downloadexcel() {
  // if (excel)
  var res: any = [];
  this.preInboundNew.forEach(x => {
    res.push({
      "Order No":x.refDocNumber,
      "Line No":x.lineNo,
      'Mfr Name': x.manufacturerName,
      "Part No ": x.itemCode,
      "Description ": x.itemDescription,
      "Supplier Name ": x.supplierName,
      "Order Qty": x.orderQty,
      "UOM ": x.orderUom,
      "Stock Type ": x.stockTypeId,
      'Exp Arrival Date': this.cs.dateapi(x.expectedArrivalDate),
      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.createdOn),
    });
    x.inventoryDetail.forEach(xx => {
      res.push({
        "Inv Qty ": xx.inventoryQty,
        "Storage Bin ": xx.storageBin,
      })
    })
  })
  this.cs.exportAsExcel(res, "Preinbound");
}


}

