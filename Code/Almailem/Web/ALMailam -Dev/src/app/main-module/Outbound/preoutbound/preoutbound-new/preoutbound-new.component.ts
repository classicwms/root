




import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTable, MatTableDataSource } from '@angular/material/table';
//import { table } from 'console';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PreinboundService } from 'src/app/main-module/Inbound/preinbound/preinbound.service';
import { BOMElement, BOMService } from 'src/app/main-module/Masters -1/other-masters/bom/bom.service';
import { BusinessPartnerService } from 'src/app/main-module/Masters -1/other-masters/business-partner/business-partner.service';
import { MasterService } from 'src/app/shared/master.service';
import { PreoutboundService } from '../preoutbound.service';
import { Router } from '@angular/router';
import { PopupComponent } from './popup/popup.component';

export interface PeriodicElement {

  position: number;
}


interface SelectItem {
  item_id: number;
  item_text: string;
}

// const ELEMENT_DATA: PeriodicElement[] = [
//   //{position: 1,},
// ];
@Component({
  selector: 'app-preoutbound-new',
  templateUrl: './preoutbound-new.component.html',
  styleUrls: ['./preoutbound-new.component.scss']
})
export class PreoutboundNewComponent implements OnInit {
  inventory: any[] = [];
  selectedbinner : any[] = [];
  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  displayedColumns: string[] = ['no','lineReference', 'orderType','orderedQty','sku','skuDescription','uom', 'delete'];
  dataSource = new MatTableDataSource<any>([]);
  screenid: 1037 | undefined;

  sub = new Subscription();


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

 

 soHeader= this.fb.group({
storeID: [],
storeName: [],
transferOrderNumber: [],
companyCode:[this.auth.companyId],
branchCode:[this.auth.plantId],
wareHouseId: [this.auth.warehouseId],
languageId:[this.auth.languageId],
requiredDeliveryDate : [],
});
  submitted = false;
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
  constructor(

    private service: PreoutboundService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private bonService: BusinessPartnerService,
    private masterService: MasterService,
    private auth: AuthService,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private router: Router,
    private cs: CommonService,
  ) { }

  ngOnInit(): void {
    this.dropdownfill();
    this.soHeader.get('wareHouseId')?.disable();
  this.soHeader.get('branchCode')?.disable();
  this.soHeader.get('companyCode')?.disable();
  this.soHeader.get('languageId')?.disable();
  }

 multiWarehouseList: any[] = [];
  multiPartnerList: any[] = [];
  dropdownfill() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
      partner: this.bonService.search({warehouseId: [this.auth.warehouseId], businessPartnerType: [2]}).pipe(catchError(err => of(err))),
    })
    .subscribe(({ warehouse, partner }) => {
      warehouse.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId }));
      partner.forEach(x => this.multiPartnerList.push({ value: x.partnerCode, label: x.partnerCode + ' - ' + x.partnerName }));
      this.soHeader.get('soHeader.wareHouseId')?.patchValue(this.auth.warehouseId)
    })
    this.spin.hide();
  }


  submit() {
    let obj: any = {};
    let soHeader: any = {};
   soHeader.storeID = this.soHeader.controls.storeID.value;
    soHeader.storeName = this.soHeader.controls.storeName.value;
 soHeader.transferOrderNumber = this.soHeader.controls.transferOrderNumber.value;
    soHeader.wareHouseId = this.soHeader.controls.wareHouseId.value;
    soHeader.branchCode=this.auth.plantId;
    soHeader.companyCode=this.auth.companyId;
    soHeader.languageId=this.auth.languageId;
    //soHeader.requiredDeliveryDate = this.soHeader.controls.requiredDeliveryDate.value;
    const originalDate = this.soHeader.controls.requiredDeliveryDate.value;
    soHeader.requiredDeliveryDate = this.cs.dateddMMYY(originalDate);
    obj.soHeader = soHeader;
   obj.soLine= this.inventory,
console.log(soHeader.requiredDeliveryDate);
this.soHeader.controls.requiredDeliveryDate.patchValue(soHeader.requiredDeliveryDate);
    this.sub.add(this.service.createShipmentOrder(obj).subscribe(res => {
  if(res){
    this.toastr.success("Order created successfully!",   "Notification");
    this.router.navigate(['/main/outbound/preoutbound']);
    this.spin.hide();
  }

    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  };

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }

  elementdata: any;
  @ViewChild(MatTable)
  table!: MatTable<any>;
  add() {
    const dialogRef = this.dialog.open(PopupComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
    });

    dialogRef.afterClosed().subscribe(result => {
     console.log(result.length);
     if(result){
      this.inventory
     
      if (result.length > 0) {
        this.inventory.push(result);
      }

      this.inventory.push(result);
    }
     this.inventory=[...this.inventory];
    });
  }
  
  removeRow(index: any) {
    const rowNo = this.dataSource.data.indexOf(index);
    if (rowNo == -1) {
      this.dataSource.data.splice(index, 1);
    }
    else {
      this.dataSource.data.splice(rowNo, 1);
    }
    this.dataSource._updateChangeSubscription();
  }  openDialog(data: any,rowIndex): void {
    const dialogRef = this.dialog.open(PopupComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      data: this.inventory[rowIndex],
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.inventory.splice(rowIndex,0);
        this.inventory.splice(rowIndex, 1, result);
        console.log(result);
      //this.form.patchValue(result);
      this.inventory = [...this.inventory]
  
  }});
  }

}





