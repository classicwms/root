
import { SelectionModel } from "@angular/cdk/collections";
import { HttpClient } from "@angular/common/http";
  import { Component, OnInit, ViewChild } from "@angular/core";
  import { FormBuilder } from "@angular/forms";
  import { MatDialog } from "@angular/material/dialog";
  import { MatPaginator, PageEvent } from "@angular/material/paginator";
  import { MatSort } from "@angular/material/sort";
  import { MatTableDataSource } from "@angular/material/table";
  import { Router } from "@angular/router";
  import { IDropdownSettings } from "ng-multiselect-dropdown";
  import { NgxSpinnerService } from "ngx-spinner";
  import { ToastrService } from "ngx-toastr";
  import { forkJoin, of, Subscription } from "rxjs";
  import { catchError } from "rxjs/operators";
  import { CommonService } from "src/app/common-service/common-service.service";
  import { AuthService } from "src/app/core/core";
  import { MasterService } from "src/app/shared/master.service";
import { ContainerReceiptService } from "../../Inbound/Container-receipt/container-receipt.service";
import { PickingService } from "../../Outbound/picking/picking.service";
  import { ReportsService } from "../reports.service";
import  { stockElement, StocksampleService }  from "../stocksamplereport/stocksample.service";
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
import { Table } from "primeng/table";
import { DatePipe } from "@angular/common";
import { CommonApiService } from "src/app/common-service/common-api.service";


@Component({
  selector: 'app-inventorymovement',
  templateUrl: './inventorymovement.component.html',
  styleUrls: ['./inventorymovement.component.scss']
})
export class InventorymovementComponent implements OnInit {
  screenid=3173;
  binner: any[] = [];
  selectedbinner : any[] = [];
  @ViewChild('inventorymovTag') inventorymovTag: Table | any;
    isShowDiv = false;
    table = true;
    fullscreen = false;
    search = true;
    back = false;
  
  
  
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    showFiller = false;

    displayedColumns: string[] = ['select','warehouseId', 'itemCode', 'description', 'packBarcodes', 'storageBin', 'movementType', 'submovementType', 'refDocNumber', 'movementQty', 'inventoryUom','createdOn'];
    sub = new Subscription();
    ELEMENT_DATA: any[] = [];

    movementList: any[];
    submovementList: any[] = [];
    NofilterSubmovementList: any[];

    constructor(public dialog: MatDialog,
      private cas: CommonApiService,
      private service: StocksampleService,
      private http: HttpClient,
      // private cas: CommonApiService,
      private fb: FormBuilder,
      public toastr: ToastrService,
      private router: Router,
      public datepipe: DatePipe,
      private spin: NgxSpinnerService,
      public cs: CommonService,
      private pickingService: PickingService,
      private ReportsService: ReportsService,
      public auth: AuthService,
      private masterService: MasterService,) {
        this.movementList = [
          {value: 1, label: 'Inbound'},
          {value: 2, label: 'Transfer'},
          {value: 3, label: 'Outbound'},
          {value: 4, label: 'Inventory'},
      ];

      this.NofilterSubmovementList = [
        {value: 1, label: 'Staging', movemenTypeID: 1},
        {value: 2, label: 'Putaway', movemenTypeID: 1},
        {value: 3, label: 'Reversal', movemenTypeID: 1},
        {value: 3, label: 'Bin to Bin', movemenTypeID: 2},
        {value: 1, label: 'Picking', movemenTypeID: 3},
        {value: 2, label: 'Quality', movemenTypeID: 3},
        {value: 5, label: 'Delivery', movemenTypeID: 3},
        {value: 1, label: 'Adjustment', movemenTypeID: 4},
    ];

       }
       form = this.fb.group({
        batchSerialNumber: [],
       caseCode: [],
        fromCreatedOn: [],
        fromCreatedOnFE: [new Date("01/01/00 00:00:00")],
        itemCode: [,],
        itemCodeFE:[],
        movementDocumentNo: [],
        movementType: [],
        packBarcodes: [],
        palletCode: [],
        submovementType: [],
        toCreatedOn: [],
        toCreatedOnFE: [this.cs.todayapi()],
        variantCode: [],
        variantSubCode: [],
        movementTypeFE:[],
        plantId:[[this.auth.plantId]],
        languageId:[[this.auth.languageId]],
        companyCodeId:[[this.auth.companyId]],
        warehouseId: [[this.auth.warehouseId]],
        
      });
    routeto(url: any, id: any) {
      sessionStorage.setItem('crrentmenu', id);
      this.router.navigate([url]);
    }

    // this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
    //   console.log(selectedValue);
    //   this.showingStorageClassList = this.storageClassList.filter(element => {
    //     return element.warehouseId === this.form.controls['warehouseId'].value;
    //   });
    // })

 
    startDate: any;
    currentDate: Date;
    animal: string | undefined;
    selectedCompany:any[]=[];
    selectedplant:any[]=[]; 
    ngOnInit(): void {
     this.dropdownfill();
     this.currentDate = new Date();
     let yesterdayDate = new Date();
     let currentMonthStartDate = new Date();
     yesterdayDate.setDate(this.currentDate.getDate() - 1);
     this.startDate = this.datepipe.transform(yesterdayDate, 'dd');
    currentMonthStartDate.setDate(this.currentDate.getDate() - this.startDate);
    this.form.controls.fromCreatedOn.patchValue(currentMonthStartDate);
     // this.initialSearch();
     this.selectedCompany=this.auth.companyId;
      this.selectedplant=this.auth.plantId;
   
    }
    
    initialSearch(){
      this.spin.show();
   
      if(this.form.controls.movementType.value != null){
      this.form.controls.movementType.patchValue([this.form.controls.movementType.value])
      }
      else{
        this.form.controls.movementType.patchValue(this.form.controls.movementType.value)
      }
      this.form.controls.fromCreatedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.fromCreatedOnFE.value));
      this.form.controls.toCreatedOn.patchValue(this.cs.dateNewFormat1(this.form.controls.toCreatedOnFE.value));
     if(this.form.controls.itemCodeFE.value != null){
      this.form.controls.itemCode.patchValue([this.form.controls.itemCodeFE.value]);
     }
if(this.form.controls.itemCodeFE.value == null){
  this.form.controls.itemCode.patchValue(this.form.controls.itemCodeFE.value);
}
      this.sub.add(this.ReportsService.getInventoryMovementV2(this.form.getRawValue()).subscribe(res => {
        this.ELEMENT_DATA = res;
        console.log(this.ELEMENT_DATA);
        res.forEach(element => {
          if(element.movementType == 1 && element.submovementType == 1){
            element['subMovementText'] = 'Staging'
          }
          if(element.movementType == 1 && element.submovementType == 2){
            element['subMovementText'] = 'Putaway'
          }
          if(element.movementType == 1 && element.submovementType == 3){
            element['subMovementText'] = 'Reversal'
          }
          if(element.movementType == 2 && element.submovementType == 3){
            element['subMovementText'] = 'Bin to Bin'
          }
  
          
          if(element.movementType == 3 && element.submovementType == 1){
            element['subMovementText'] = 'Picking'
          }
          if(element.movementType == 3 && element.submovementType == 2){
            element['subMovementText'] = 'Quality'
          }
          if(element.movementType == 3 && element.submovementType == 5){
            element['subMovementText'] = 'Delivery'
          }
  
          if(element.movementType == 4 && element.submovementType == 1){
            element['subMovementText'] = 'Adjustment'
          }
  
         });
        this.binner = res;

       
        this.table = true;
        this.search = false;
        this.fullscreen = false;
        this.back = true;
      this.spin.hide();
      }))
    }

    multiselectItemCodeList: any[] = [];
    itemCodeList: any[] = [];
    onItemType(searchKey) {
      let searchVal = searchKey?.filter;
      if (searchVal !== '' && searchVal !== null) {
        forkJoin({
          itemList: this.ReportsService.getItemCodeDropDown2(searchVal.trim(),this.auth.companyId,this.auth.plantId,this.auth.warehouseId,this.auth.languageId).pipe(catchError(err => of(err))),
        })
          .subscribe(({ itemList }) => {
            if (itemList != null && itemList.length > 0) {
              this.multiselectItemCodeList = [];
              this.itemCodeList = itemList;
              this.itemCodeList.forEach(x => this.multiselectItemCodeList.push({value: x.itemCode, label: x.itemCode + ' - ' + x.manufacturerName + ' - ' + x.description}))
            }
          });
      }
    }
    
    warehouseList: any[] = [];
    selectedWarehouseList: any[] = [];
    selectedItems: any[] = [];
    multiselectWarehouseList: any[] = [];
    multiWarehouseList: any[] = [];
    dropdownfill() {
     // this.spin.show();
      forkJoin({
        warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
  
      })
      .subscribe(({ warehouse }) => {
        if(this.auth.userTypeId != 3){
          this.warehouseList = warehouse.filter((x: any) => x.warehouseId == this.auth.warehouseId);
        }else{
          this.warehouseList = warehouse
        }
          this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
          this.multiselectWarehouseList = this.multiWarehouseList;
          console.log(this.multiselectWarehouseList)
          this.multiselectWarehouseList.forEach((warehouse: any) => {
            if (warehouse.value == this.auth.warehouseId)
              this.selectedItems = [warehouse.value];
          })
          this.multiselectWarehouseList=this.cas.removeDuplicatesFromArray(this.multiselectWarehouseList);
           // this.spin.hide();
        }, (err) => {
          this.toastr.error(
            err,
            ""
          );
        });

  
    }
 
                  
    movementChange(){
      this.submovementList = []
        this.NofilterSubmovementList.forEach(x=>{
        if(x.movemenTypeID === this.form.controls.movementType.value){
          this.submovementList.push(x)
        }
        })
      }


    movementType = '';
    submovementType = '';
    itemCode = '';
    packBarcodes = '';
    toCreatedOn = '';
    fromCreatedOn = '';

    reset(){
      this.form.reset();
    this.form.controls.warehouseId.patchValue([this.auth.warehouseId]);
    this.form.controls.companyCodeId.patchValue([this.auth.companyId]);
    this.form.controls.plantId.patchValue([this.auth.plantId]);
    this.form.controls.languageId.patchValue([this.auth.languageId]);
  }
    filtersearch(){
      // if(this.fromCreatedOn == null || this.fromCreatedOn == undefined || !this.fromCreatedOn){
      //   this.toastr.error("Please fill the date to continue", "Notification", {
      //     timeOut: 2000,
      //     progressBar: false,
      //   });
      //   this.cs.notifyOther(true);
      //   return
      // }
      this.spin.show();
      let obj: any = {};

      obj.itemCode = [];
      if (this.itemCode != null && this.itemCode.trim() != "") {
        obj.itemCode.push(this.itemCode)
      }

      
      obj.packBarcodes = [];
      if (this.packBarcodes != null && this.packBarcodes.trim() != "") {
        obj.packBarcodes.push(this.packBarcodes)
      }

      obj.movementType = [];
    
      if (this.movementType != null && this.movementType) {
        obj.movementType.push(this.movementType); 
      }
      obj.submovementType = [];
      if (this.submovementType != null && this.submovementType) {
        obj.submovementType = this.submovementType
      }
     obj.toCreatedOn = this.cs.day_callapiSearch(this.toCreatedOn);
     obj.fromCreatedOn = this.cs.day_callapiSearch(this.fromCreatedOn);
      obj.warehouseId = this.selectedItems;

      obj.statusId = []


      console.log(obj)
    let data = this.cs.filterArray(this.ELEMENT_DATA, obj)
console.log(data)
   data.forEach(element => {
        if(element.movementType == 1 && element.submovementType == 1){
          element['subMovementText'] = 'Staging'
        }
        if(element.movementType == 1 && element.submovementType == 2){
          element['subMovementText'] = 'Putaway'
        }
        if(element.movementType == 1 && element.submovementType == 3){
          element['subMovementText'] = 'Reversal'
        }
        if(element.movementType == 2 && element.submovementType == 3){
          element['subMovementText'] = 'Bin to Bin'
        }

        
        if(element.movementType == 3 && element.submovementType == 1){
          element['subMovementText'] = 'Picking'
        }
        if(element.movementType == 3 && element.submovementType == 2){
          element['subMovementText'] = 'Quality'
        }
        if(element.movementType == 3 && element.submovementType == 5){
          element['subMovementText'] = 'Delivery'
        }

        if(element.movementType == 4 && element.submovementType == 1){
          element['subMovementText'] = 'Adjustment'
        }

       });
    this.binner = data;

 
            this.table = true;
        this.search = false;
        this.fullscreen = false;
        this.back = true;
this.spin.hide();
      // this.sub.add(this.ReportsService.getNewInventoryMovement1().subscribe(res => {
      //  res.forEach(element => {
      //   if(element.movementType == 1 && element.submovementType == 1){
      //     element['subMovementText'] = 'Staging'
      //   }
      //   if(element.movementType == 1 && element.submovementType == 2){
      //     element['subMovementText'] = 'Putaway'
      //   }
      //   if(element.movementType == 1 && element.submovementType == 3){
      //     element['subMovementText'] = 'Reversal'
      //   }
      //   if(element.movementType == 2 && element.submovementType == 3){
      //     element['subMovementText'] = 'Bin to Bin'
      //   }

        
      //   if(element.movementType == 3 && element.submovementType == 1){
      //     element['subMovementText'] = 'Picking'
      //   }
      //   if(element.movementType == 3 && element.submovementType == 2){
      //     element['subMovementText'] = 'Quality'
      //   }
      //   if(element.movementType == 3 && element.submovementType == 5){
      //     element['subMovementText'] = 'Delivery'
      //   }

      //   if(element.movementType == 4 && element.submovementType == 1){
      //     element['subMovementText'] = 'Adjustment'
      //   }

      //  });
      //   this.dataSource = new MatTableDataSource<any>(res);
      //   this.selection = new SelectionModel<any>(true, []);
      //   this.dataSource.sort = this.sort;
      //   this.dataSource.paginator = this.paginator;
      //   this.table = true;
      //   this.search = false;
      //   this.fullscreen = false;
      //   this.back = true;
      //  this.spin.hide();
      // this.totalRecords = this.dataSource.data.length
      // },
      //   err => {
      //     this.cs.commonerrorNew(err);
      //     this.spin.hide();
      //   }));
    }

  
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
  
    ngOnDestroy() {
      if (this.sub != null) {
        this.sub.unsubscribe();
      }
    }
  
    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator: MatPaginator; // Pagination
    // Pagination
  

  

    totalRecords = 0;
    downloadexcel() {
      // if (excel)
      var res: any = [];
      this.binner.forEach(x => {
        res.push({
          "Company":x.companyDescription,
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Mfr Name": x.manufacturerName,
          'Part No': x.itemCode,
          "Description": x.description,
          "Barcode Id": x.barcodeId,
          "Storage Bin": x.storageBin,
          "Movement": (x.movementType == 1 ? "Inbound" : x.movementType == 3 ? "Outbound" : x.movementType == 2 ? "Transfer" : x.movementType == 4 ? "Inventory" : ''),
          "Sub Movement":   x.subMovementText,
          "Order No": x.refDocNumber,
          "Opening Qty":x.referenceField2,
          "Movement Qty": x.movementQty,
          "Closing Qty":x.balanceOHQty,
          "UOM": x.inventoryUom,
          'Confirmed On':this.cs.dateapiwithTime(x.createdOn),
  
          // 'Created By': x.createdBy,
          // 'Date': this.cs.dateapi(x.createdOn),
        });
  
      })
      res.push({
        "Company":"",
        "Branch":"",
        "Warehouse":"",
        'Part No': '',
        "Description":'',
        "Mfr Name":'',
        "Barcode Id": '',
        "Storage Bin":'',
        "Movement": '',
        "Sub Movement":   '',
        "Order No": '',
        "Opening Qty":this.getopenQty(),
        "Movement Qty": this.getMovementQty(),
        "Closing Qty":this.getclosedQty(),
        "UOM": '',
        'Confirmed On': '',

        
      });

      this.cs.exportAsExcel(res, "Total Stock Movement");
    }

  
    togglesearch() {
      this.search = false;
      this.table = true;
      this.fullscreen = false;
      this.back = true;
    }
    backsearch() {
      this.table = true;
      this.search = true;
      this.fullscreen = true;
      this.back = false;
    }

  
    onItemSelect(item: any) {
    }
  
    onSelectAll(items: any) {
    }
  
  
  
   
  
    /** The label for the checkbox on the passed row */
    
  
  
  
    
    // getBillableAmount() {
    //   let total = 0;
    //   this.dataSource.data.forEach(element => {
    //     total = total + (element.s != null ? element.s : 0);
    //   })
    //   return (Math.round(total * 100) / 100);
    // }
    getopenQty(){
      let total = 0;
      this.binner.forEach(x =>{
        total = total + (x.referenceField2 != null ? x.referenceField2 : 0)
      })
      return Math.round(total *100 / 100)
    }
    getclosedQty(){
      let total = 0;
      this.binner.forEach(x =>{
        total = total + (x.balanceOHQty != null ? x.balanceOHQty : 0)
      })
      return Math.round(total *100 / 100)
    }
    getMovementQty(){
      let total = 0;
      this.binner.forEach(x =>{
        total = total + (x.movementQty != null ? x.movementQty : 0)
      })
      return Math.round(total *100 / 100)
    }

    deleteDialog() {
      if (this.binner.length === 0) {
        this.toastr.error("Kindly select any row", "Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
      const dialogRef = this.dialog.open(DeleteComponent, {
        disableClose: true,
        width: '40%',
        maxWidth: '80%',
        position: { top: '9%', },
        data: this.selectedItems[0].code,
      });
    
      dialogRef.afterClosed().subscribe(result => {
    
        if (result) {
          this.deleterecord(this.selectedItems[0]);
    
        }
      });
    }
    
    
    deleterecord(id: any) {
      this.spin.show();
      this.sub.add(this.ReportsService.deleteInventoryMovement(id).subscribe((res) => {
        this.toastr.success("Inventory movement record deleted successfully.","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.filtersearch();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
    fromdate(fromValue){
      this.form.controls.fromCreatedOnFE.patchValue(fromValue.value);
         }
         todate(toValue){
          this.form.controls.toCreatedOnFE.patchValue(toValue.value);
             }
  }

