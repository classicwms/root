import {
  SelectionModel
} from "@angular/cdk/collections";
import {
  Component,
  OnInit,
  ViewChild
} from "@angular/core";
import {
  FormBuilder,
  FormControl,
  Validators
} from "@angular/forms";
import {
  MatDialog
} from "@angular/material/dialog";
import {
  MatTableDataSource
} from "@angular/material/table";
import {
  ActivatedRoute,
  Router
} from "@angular/router";
import {
  NgxSpinnerService
} from "ngx-spinner";
import {
  ToastrService
} from "ngx-toastr";
import {
  Subscription
} from "rxjs";
import {
  CommonService
} from "src/app/common-service/common-service.service";
import {
  AuthService
} from "src/app/core/core";
import {
  CrossDockComponent
} from "../../preinbound/goodreceipt-create/cross-dock/cross-dock.component";
import {
  ItemReceiptService
} from "../item-receipt.service";
import {
  AssignHEComponent
} from "./assign-he/assign-he.component";
import {
  Packdetails1Component
} from "./packdetails1/packdetails1.component";

import {
  Location
} from "@angular/common";
import {
  GoodsReceiptService
} from "../../Goods-receipt/goods-receipt.service";
import {
  AssignPickerComponent
} from "src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component";
import {
  DeleteComponent
} from "src/app/common-field/delete/delete.component";
import {
  MatPaginator
} from "@angular/material/paginator";
import {
  MatSort
} from "@angular/material/sort";
import {
  Table
} from "primeng/table";
import {
  PartnerService
} from "src/app/main-module/Masters -1/masternew/partner/partner.service";
import {
  ReportsService
} from "src/app/main-module/reports/reports.service";
import {
  InventoryDetailsComponent
} from "./inventory-details/inventory-details.component";
import {
  ReleasedQtyComponent
} from "./released-qty/released-qty.component";
import { LabelGenerateComponent } from "./label-generate/label-generate.component";
import { EditpopupComponent } from "./editpopup/editpopup.component";
@Component({
  selector: 'app-item-create',
  templateUrl: './item-create.component.html',
  styleUrls: ['./item-create.component.scss'],
  styles: [`
  :host ::ng-deep .row-confirmed {
      color: #007bff !important;
      font-weight: bold !important;
  }
  :host ::ng-deep .row-confirmed1 {
    color: #495057 !important;
}
`
]
})
export class ItemCreateComponent implements OnInit {

  showRelease = false;
  grNew: any[] = [];
  grBackup: any[] = [];
  selectedgrNew: any[] = [];
  @ViewChild('grNewTag') grNewTag: Table | any;

  screenid: 1050 | undefined;
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
  }


  isShown: boolean = false; // hidden by default
  toggleShow() {
    this.isShown = !this.isShown;
  }
  animal: string | undefined;
  name: string | undefined;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: ItemReceiptService, private location: Location,
    public toastr: ToastrService, private dialog: MatDialog, private grservice: GoodsReceiptService, private partnerService: PartnerService,
    private inventoryService: ReportsService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    public cs: CommonService, ) {}
  sub = new Subscription();

 


  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({
    barcodeId: [],
    companyCodeId: [],
    confirmedBy: [],
    confirmedOn: [],
    containerNo: [],
    containerReceiptNo: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    dockAllocationNo: [],
    partner_item_barcode:[],
    inboundOrderTypeId: [],
    languageId: [],
    plantId: [],
    preInboundNo: [],
    refDocNumber: [],
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
    stagingNo: [],
    statusId: [],
    updatedBy: [],
    updatedOn: [],
    vechicleNo: [],
    warehouseId: [],
    goodsReceiptNo: [],
  });

  submitted = false;
  public errorHandling = (control: string, error: string = "required") => {
    return this.form.controls[control].hasError(error) && this.submitted;
  }
  getErrorMessage() {
    if (this.email.hasError('required')) {
      return ' Field should not be blank';
    }
    return this.email.hasError('email') ? 'Not a valid email' : '';
  }



  isbtntext = true;

  code: any;
  js: any;

  ngOnInit(): void {
    sessionStorage.removeItem('barcode');
    this.form.disable();

    this.form.controls.containerNo.enable();
    let code = this.route.snapshot.params.code;
    if (code != 'new') {

      let js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
      this.js = js;
      console.log(js.refDocNumber);
    }
  }



  btntext = "Save";
  pageflow = "New";

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort, {
    static: true
  })
  sort: MatSort;
  itemReceipt:any[]=[];
  fill(data: any) {

    if (data.pageflow != 'New') {
      console.log(data);
      this.pageflow = "Edit";
      this.btntext = 'Update';
      this.form.controls.preInboundNo.disable();
      this.form.controls.warehouseId.disable();
      if (data.pageflow == 'Display') {

        this.isbtntext = false;
      }
      this.form.patchValue(data.code);
      this.form.disable();
      this.spin.show();
      this.sub.add(this.grservice.searchGrheader({
       companyCodeId:[this.auth.companyId],
       plantId:[this.auth.plantId],
       warehouseId:[this.auth.warehouseId],
       languageId:[this.auth.languageId],
       refDocNumber:[data.code.refDocNumber],
       statusId:[17],
      }).subscribe(res => {
       
        this.spin.hide();
        this.itemReceipt = res;
     

        
        // if(data.pageflow == 'Edit'){
        //   console.log('done')
        //   this.transactionHistory(data);  
        // }
        
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
      this.sub.add(this.grservice.searchLineSpark({
        stagingNo: [data.code.stagingNo],
        caseCode: [data.code.caseCode],
        companyCodeId: [this.auth.companyId],
        plantId: [this.auth.plantId],
        warehouseId: [this.auth.warehouseId],
        languageId: [this.auth.languageId],
      }).subscribe(res => {
        const filteredRes = res.filter((x: any) => x.statusId != 17);
       console.log(filteredRes);
        filteredRes.forEach((x: any)=>{
          x.varianceQty = Number(x.orderQty) - (Number(x.rec_accept_qty) + Number(x.rec_damage_qty));
          x.putAwayHandlingEquipment = "";
          x.packBarcodes = [];
          x.stagingNo = this.form.controls.stagingNo.value;
          x.goodsReceiptNo = this.form.controls.goodsReceiptNo.value;
          x.damageQty = 0;
        //  x.acceptedQty = Number(x.orderQty);
          x.acceptedQty = null;
        //  x['interimStorageBin'] = 'A1-AA-01-10A';
        });
        this.spin.hide();
        this.grNew = filteredRes;
        this.grBackup = filteredRes;

        if(filteredRes.length == 0){
          this.toastr.error("No Lines Found", "", {
            timeOut: 2000,
            progressBar: false,
          });
        }
    
        // if(data.pageflow == 'Edit'){
        //   console.log('done')
        //   this.transactionHistory(data);  
        // }
        
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
      this.form.patchValue(data.code, {
        emitEvent: false
      });

    }
  }

  
  back() {
    this.location.back();
  }
  ngOnDestroy() {
    // if(this.js.pageflow == 'Edit' && this.activeUser == true){
    //   localStorage.removeItem('transactionHistory');
    // }
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
    sessionStorage.removeItem('barcode');
  }

  getVariance(element: any) {

    let num: number = (Number(element.damageQty) + Number(element.acceptedQty) + Number(element.rec_accept_qty) + Number(element.rec_damage_qty));
    element.varianceQty = Number(element.orderQty) as number - num;
  }

  delete() {
    if (this.selectedgrNew.length === 0) {
      this.toastr.error("Kindly select one row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    if (this.selectedgrNew.length > 1) {
      this.toastr.error("Kindly select one row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(DeleteComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: {
        top: '6.7%',
      },
      // data: this.selection.selected[0],
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.spin.show();
        debugger
        this.sub.add(this.service.DeleteLine(this.selectedgrNew[0]).subscribe(res => {
          this.spin.hide();
          this.ngOnInit();
        }, err => {

          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));
      }
    });

  }
  impartnerLinesFound: any[] = [];
  selectedgrNew1: any[] = [];



  filterValue: any;

  lablelGenerate(element, pageflow) {
    if (!element) {  //this.selectedgrNew.length === 0
      this.toastr.error("Kindly select one row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    
    const dialogRef = this.dialog.open(LabelGenerateComponent, {
      disableClose: true,
      width: '85%',
      maxWidth: '60%',
      position: { top: '9%', },
      data: {line: element, pageflow: pageflow},
    });

    dialogRef.afterClosed().subscribe(result => {
      // this.getAll();
      // this.grNewTag.filters.partner_item_barcode.value = "";
      // this.filterValue = "";
      this.selectedgrNew = [];
      console.log(result);
      if(result.line.partner_item_barcode == null){   // && (result.line.partner_item_barcode1 ==null)
        this.form.controls.partner_item_barcode.patchValue(result.line.itemCode);
      }
      // if((result.line.partner_item_barcode1 != null) && (result.line.partner_item_barcode == null)){
      //   this.form.controls.partner_item_barcode.patchValue(result.line.partner_item_barcode1);
      // }
      if(result){
      this.showRelease = true;
      console.log(result.length)
      if(result){
        console.log(this.selectedgrNew1);
      this.selectedgrNew1.push(result)
      }
      }
    });
  }

  finalValue: any[] = [];


  

assignUserID(): void {
console.log(this.selectedgrNew1)
  if (this.selectedgrNew1.length === 0) {
    this.toastr.error("Kindly select one row", "", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }

  const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

  dialogRef.afterClosed().subscribe(result2 => {
   if (result2) {
        // this.selection.selected.forEach((x: any) => { x.assignedUserId = result; });
        this.grNew.forEach((x: any) => {
         if(x.packBarcodesState == true){
          x.assignedUserId = result2;
         }
        });
       // this.assignhe();
       this.submitInterim();
      }
  });
}

  submitInterim(){
    this.finalValue=[];
    this.spin.show();
    this.grNew.forEach(x => {
      if(x.packBarcodesState == true){
        this.finalValue.push(x);
      }
    })
    this.service.CreateLine(this.finalValue).subscribe(res => {
      this.toastr.success("GR created successfully", "Notification",
       {
        timeOut: 2000,
        progressBar: false,
      });
      this.router.navigate(["/main/inbound/item-main"]);
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    })
  }

  onChange() {
    const choosen= this.selectedgrNew[this.selectedgrNew.length - 1];   
    this.selectedgrNew.length = 0;
    this.selectedgrNew.push(choosen);
    this.lablelGenerate(this.selectedgrNew[0], 'New');
  }


  onDeleteRow(element){
    element.acceptedQty = null;
    element.damageQty = 0;
    element.statusId = 14;
    element.packBarcodes = [];
    element.packBarcodesState = false;
    element.partner_item_barcode = null;
    element.varianceQty = 0;
    this.grNew.forEach((x: any) => {
      let count =0;
      if(x.acceptedQty != null){
        count++;
        console.log(count);
        if(count ==1){
       this.showRelease=true;
        }
      else{
        this.showRelease=false;
      }
      }
      else{
        console.log(count);
        this.showRelease=false;
      }
     });
  }
  Edit(element){
    if(this.js.pageflow == "Edit"){
    const dialogRef = this.dialog.open(EditpopupComponent, {
      disableClose: true,
      width: '85%',
      maxWidth: '60%',
      position: { top: '9%', },
      data: element,
    
    });

    dialogRef.afterClosed().subscribe(result => {
      // this.getAll();
      // this.grNewTag.filters.partner_item_barcode.value = "";
      // this.filterValue = "";
      this.selectedgrNew = [];
      console.log(result);
      if(result){
      this.showRelease = true;
      console.log(result.length)
      if(result){
      this.selectedgrNew1.push(result)
      }
      }
    });
  }
  }



  printLabel(element){
    if(element.acceptedQty == null || element.acceptedQty == '' || element.acceptedQty == 0){
      this.toastr.error(
        "Lines not confirmed",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );
  
      this.cs.notifyOther(true);
      return;
    }

    let obj: any = {};
    let dataa: any[] = [];
    for(let j=0; j < element.noOfAcceptedLabel; j++){
      obj.barcodeId = element.partner_item_barcode;
      obj.partner_item_barcode = element.partner_item_barcode;
      obj.goodReceiptQty = 1;
      obj.noOfDamageLabel = element.noOfDamageLabel;
      obj.noOfAcceptedLabel = element.noOfAcceptedLabel;
      obj.totalLabel = element.noOfDamageLabel + element.noOfAcceptedLabel;
      obj.barCodeType = element.barCodeType;
      obj.manufacturerName =  element.manufacturerName;
      obj.origin =  element.origin;
      obj.itemCode =  element.itemCode;
      obj.itemDescription =  element.itemDescription;
      obj.partner_item_barcode =  element.partner_item_barcode;
      obj.qty =  element.noOfAcceptedLabel;
      dataa.push(obj);
     } 
     let obj2: any = {};
    for(let i=0; i< element.noOfDamageLabel; i++){
      obj2.barcodeId = element.partner_item_barcode;
      obj2.partner_item_barcode = element.partner_item_barcode;
      obj2.goodReceiptQty = 1;
      obj2.noOfDamageLabel = element.noOfDamageLabel;
      obj2.noOfAcceptedLabel = element.noOfAcceptedLabel;
      obj2.totalLabel = element.noOfDamageLabel + element.noOfAcceptedLabel;
      obj2.barCodeType = element.barCodeType;
      obj2.manufacturerName =  element.manufacturerName;
      obj2.origin =  element.origin;
      obj2.itemCode =  element.itemCode;
      obj2.itemDescription =  element.itemDescription;
      obj2.partner_item_barcode =  element.partner_item_barcode;
      
      obj2.qty =  element.noOfDamageLabel;
      dataa.push(obj2);
     } 
    sessionStorage.setItem(
      'barcode',
      JSON.stringify({ BCfor: "barcode", list: dataa })
    );
    window.open('#/barcodeNew', '_blank');
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.grNew.forEach((x, i) => {
      res.push({ 
        "S No": i + 1,
        "Mfr Name":x.manufacturerName,
        "Part No ": x.itemCode,
        "Description": x.itemDescription,
        "Order Qty":x.orderQty,
        "Released Qty ": x.acceptedQty,
        'Rejected Qty': x.damageQty,
        "Variance": x.varianceQty,
        'Barcode': x.partner_item_barcode,
        "Total Release Qty":x.rec_accept_qty,
        "Total Rejected Qty":x.rec_damage_qty,
        "UOM":x.orderUom,
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "GR Release");
  }

  // activeUser = false;
  // transactionHistory(data){
  //     if(localStorage.getItem('transactionHistory') != null && localStorage.getItem('transactionHistory') != undefined){
  //       let item =      JSON.parse("[" + localStorage.getItem('transactionHistory') + "]");
  //       if(item[0].statusId == 1 && item[0].transactionid == data.code.goodsReceiptNo){
  //         this.form.disable();
  //         this.isbtntext = false;
  //       }
  //     }else{
  //       let obj: any = {};
  //       obj.languageId = data.code.languageId;
  //       obj.companyId = data.code.companyId;
  //       obj.plantId = data.code.plantId;
  //       obj.warehouseId = data.code.warehouseId;
  //       obj.module = 'Inbound';
  //       obj.menu = 'Goods Receipt';
  //       obj.subMenu = 'Goods Receipt';
  //       obj.transactionid = data.code.goodsReceiptNo;
  //       obj.statusId = 1;
  //       this.activeUser = true;
  //       localStorage.setItem('transactionHistory', JSON.stringify(obj));
  //     }
  // }
}

