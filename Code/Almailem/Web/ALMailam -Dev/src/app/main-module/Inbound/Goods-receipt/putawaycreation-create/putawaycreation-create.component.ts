import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription, forkJoin, of } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { clientcategory } from "src/app/main-module/setup-storage/strategies/strategies.component";
import { PutawayService } from "../../putaway/putaway.service";
import { GoodsReceiptService } from "../goods-receipt.service";
import { PutawayDetailsComponent } from "./putaway-details/putaway-details.component";

import { Location } from "@angular/common";
import { ItemReceiptService } from "../../Item receipt/item-receipt.service";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Table } from "primeng/table";
import { ReportsService } from "src/app/main-module/reports/reports.service";
import { catchError } from "rxjs/operators";
import { PutawayAddLinesComponent } from "./putaway-add-lines/putaway-add-lines.component";

interface SelectItem {
  item_id: number;
  item_text: string;
}



@Component({
  selector: 'app-putawaycreation-create',
  templateUrl: './putawaycreation-create.component.html',
  styleUrls: ['./putawaycreation-create.component.scss']
})
export class PutawaycreationCreateComponent implements OnInit {
  screenid: 3052 ;
  putawayCreate: any[] = [];
  selectedputawayCreate : any[] = [];
  @ViewChild('putawayCreateTag') putawayCreateTag: Table | any;

  
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


  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  form = this.fb.group({
    putAwayQuantity: [],
    putAwayNumber: [],
    containerType: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    proposedStorageBin: [],
    languageId: [],
    proposedHandlingEquipment: [],
    plantId: [],
    assignedUserId:[],
    preInboundLine: [],
    preInboundNo: [],
    refDocDate: [],
    refDocNumber: [],
    referenceDocumentType: [],
    putawayCreateNumber: [],
    putawayCreateQuantity: [],
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
    statusId: [],
    statusDescription:[],
    updatedBy: [],
    updatedOn: [],
    warehouseId: [],
  });


  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private pservice: ItemReceiptService,
    private service: PutawayService, private location: Location,
    public toastr: ToastrService, private dialog: MatDialog,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService, private reportService: ReportsService) { }
  sub = new Subscription();
  subscreen(): void {

    const dialogRef = this.dialog.open(PutawayDetailsComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '90%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  isbtntext = true;
  code: any;
  ngOnInit(): void {

    this.form.disable();
    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {

      let js = this.cs.decrypt(code);
      this.fill(js);

      this.code = js.code;
    }

  }

  StorageBinList: any[] = [];
  btntext = "Save";
  pageflow = "New";
  elementdata: any;
  selectedItems: any[] = [];
  multiSelectbinList: any[] = [];
  multibinList: any[] = [];


  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };
    fill(data: any) {

    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;
      this.btntext = 'Update';
      this.form.controls.preInboundNo.disable();
      this.form.controls.warehouseId.disable();
      console.log(data.code)
      this.form.patchValue(data.code, { emitEvent: false });
      this.form.controls.statusId.patchValue(this.form.controls.statusDescription.value)
      this.spin.show();

      // this.sub.add(this.service.findStorageBin({warehouseId: [this.auth.warehouseId]}).subscribe(res => {
      //   res.forEach(x => this.multibinList.push({ value: x.storageBin, label: x.storageBin }))
      //   this.multiSelectbinList = this.multibinList;
      //   console.log(this.multiSelectbinList)
      // }, err => {

      //   this.spin.hide();
      // }));
      this.spin.show();
      if (data.pageflow == 'Display') {
        this.form.disable();
        this.isbtntext = false;

        this.sub.add(this.service.searchLine({ putAwayNumber: [data.code.putAwayNumber] }).subscribe(res => {
          this.elementdata = JSON.parse(JSON.stringify(res));
          this.spin.hide();
          this.putawayCreate = res;
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));

      }

      else {
        this.sub.add(this.pservice.searchlineSpark({ packBarcodes: [data.code.packBarcodes], refDocNumber: [data.code.refDocNumber], preInboundNo: [data.code.preInboundNo] }).subscribe(res => {

          res.forEach((x: any) => {
            x.orderQty = this.form.controls.putAwayQuantity.value
            // x.putAwayQuantity = data.code.putAwayQuantity;
            x.confirmedStorageBin = this.form.controls.proposedStorageBin.value;
            x.putAwayNumber = this.form.controls.putAwayNumber.value;
            x.proposedStorageBin = this.form.controls.proposedStorageBin.value;
          })

          this.elementdata = JSON.parse(JSON.stringify(res));
          this.spin.hide();
          this.putawayCreate = res;
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }));



      }
    }
  }



  

  submit() {
    
    let putawauyQtyNull = false;
    let confirmedBinNull = false;
    let totalPutawayQty=0;
   
    this.putawayCreate.forEach((x: any) => {
      totalPutawayQty=totalPutawayQty + x.putawayConfirmedQty;
      if (x.putawayConfirmedQty == null) putawauyQtyNull = true;
      if (x.confirmedStorageBin == null) confirmedBinNull = true;
    });

    if(totalPutawayQty > this.form.controls.putAwayQuantity.value){
      this.toastr.error(
         "Total Putaway qty should not be greater than TO Qty",
        "Notification", {
        timeOut: 2000,
         progressBar: false,
       
       }
      )
       //console.log(this.form.controls.putawayConfirmedQty);
       console.log(this.form.controls.putAwayQuantity);
      console.log(totalPutawayQty)
     return;
    }
    if (putawauyQtyNull) {
      this.toastr.error(
        "Please fill putaway qty to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      )
      return;
    }

    if (confirmedBinNull) {
      this.toastr.error(
        "Please fill storage bin to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      )
      return;
    }

    this.spin.show();
    this.sub.add(this.service.CreateLine(this.putawayCreate).subscribe(res => {
      this.toastr.success(this.code.palletCode + " created successfully!", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.location.back();

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));

    
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

  add() {
    let totalPutawayQty1=0;
    this.putawayCreate.forEach((x: any) => {
      totalPutawayQty1 = totalPutawayQty1 + x.putawayConfirmedQty;
      console.log(x.putawayConfirmedQty)
    });

    if(totalPutawayQty1 > this.form.controls.putAwayQuantity.value){
      this.toastr.error(
        "Total Putaway qty should not be greater than TO Qty",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
        
      }
      
      )
      
      return;
    }

    const dialogRef = this.dialog.open(PutawayAddLinesComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',  
     data:this.elementdata[0],
    }
    );
    
  
    dialogRef.afterClosed().subscribe(result => {
      if(result){
      if(this.pageflow=="New")
      this.form.patchValue(result);
      this.putawayCreate = [...this.putawayCreate, result]
    }});

  //  this.dataSource._updateChangeSubscription();
  }

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
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


  onDeleteRow(rowIndex:any): void {
    this.putawayCreate.splice(rowIndex,1);
    this.putawayCreate=[...this.putawayCreate];
   
  }
  showDropdown1: true;
  showDropdown(){
    this.showDropdown1 = true;
  }


  openDialog(data: any,rowIndex): void {
    const dialogRef = this.dialog.open(PutawayAddLinesComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      data: this.putawayCreate[rowIndex],
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if(result){
      if(this.pageflow == 'New'){
        this.putawayCreate.push(result);
      }
    if(result!=null){
      if(this.pageflow == 'Edit'){
        if(result.length ==1){
        this.putawayCreate.splice(rowIndex,0);
        this.putawayCreate.splice(0, 1, result);
      this.form.patchValue(result);
      this.putawayCreate = [...this.putawayCreate]
        }
        if(result.length != 1 ){
          this.putawayCreate.splice(rowIndex,0);
          this.putawayCreate.splice(rowIndex, 1, result);
          console.log(result);
        this.form.patchValue(result);
        this.putawayCreate = [...this.putawayCreate]
        }
      }
      this.form.patchValue(this.putawayCreate);
    }}});
  }

}

