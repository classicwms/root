import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { UpdatePickerComponent } from '../picking-main/update-picker/update-picker.component';
import { PickingService } from '../picking.service';

@Component({
  selector: 'app-cancellation',
  templateUrl: './cancellation.component.html',
  styleUrls: ['./cancellation.component.scss']
})
export class CancellationComponent implements OnInit {
  screenid=3222;
  RA: any = {};
  picking: any[] = [];
  selectedpicker : any[] = [];
  @ViewChild('pickingTag') pickingTag: Table | any;
 
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  storecodeList: any;
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

  

  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: PickingService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    console.log(this.RA);
    this.search(true);
    this.getDropdown();

  }

  warehouseId = this.auth.warehouseId
 
  searhform = this.fb.group({
    companyCodeId: [[this.auth.companyId]],
    endDeliveryConfirmedOn: [],
    endOrderDate: [],
    endRequiredDeliveryDate: [],
    languageId: [[this.auth.languageId]],
    newInvoiceNumber: [],
    newPickListNumber: [],
    newPreOutboundNo: [],
    newRefDocNumber: [],
    newSalesInvoiceNumber: [],
    newSalesOrderNumber: [],
    newSupplierInvoiceNo: [],
    newTokenNumber: [],
    oldInvoiceNumber: [],
    oldPickListNumber: [],
    oldPreOutboundNo: [],
    oldRefDocNumber: [],
    oldSalesInvoiceNumber: [],
    oldSalesOrderNumber: [],
    oldSupplierInvoiceNo: [],
    oldTokenNumber: [],
    outboundOrderTypeId: [],
    partnerCode: [],
    plantId: [[this.auth.plantId]],
    soType: [],
    startDeliveryConfirmedOn: [],
    startOrderDate: [],
    startRequiredDeliveryDate: [],
    statusId: [],
    warehouseId: [[this.auth.warehouseId]],
  });





  dropdownSettings = {
    singleSelection: false, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
 oldpicklistno:any[]=[];
 newpicklistno:any[]=[];
 confirmedby:any[]=[];
  getDropdown() {
    this.sub.add(this.service.searchcancellation({ warehouseId: [this.auth.warehouseId],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId] }).subscribe(res => {
      res.forEach((x: any) => this.oldpicklistno.push({ value: x.oldPickListNumber, label: x.oldPickListNumber }));
      res.forEach((x: any) => this.newpicklistno.push({ value: x.newPickListNumber, label: x.newPickListNumber }));
      res.forEach((x: any) => this.confirmedby.push({ value: x.deliveryConfirmedBy, label: x.deliveryConfirmedBy }));
    }))
  }
 

  search(ispageload = false) {
    if (!ispageload) {
    }
    this.searhform.controls.startDeliveryConfirmedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.startDeliveryConfirmedOn.value));
    this.searhform.controls.endDeliveryConfirmedOn.patchValue(this.cs.dateNewFormat1(this.searhform.controls.endDeliveryConfirmedOn.value));
    //this.searhform.controls.statusId.value.push(48);
    this.service.searchcancellation(this.searhform.value).subscribe(res => {
      this.picking = res;
      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);;
      this.spin.hide();

    });


  }
  onChange() {
    const choosen= this.selectedpicker[this.selectedpicker.length - 1];   
    this.selectedpicker.length = 0;
    this.selectedpicker.push(choosen);
  }
 
  
  reload() {
    window.location.reload();
  }
 

  openConfirm(data: any) {
    console.log(data);
    console.log(data.line);
    if(data.line.length == 0){
      this.toastr.error("No Lines Present", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
   
    let paramdata = this.cs.encrypt({ code: data, pageflow: 'Edit' });
    this.router.navigate(['/main/outbound/cancellation-lines/' + paramdata]);

  }


 

  
 


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.picking.forEach(x => {
      res.push({
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Target Branch":x.targetBranchCode,
        "Old PickList No":x.oldPickListNumber,
        "New PickList No":x.newPickListNumber,
        "No of Old Picked Lines":x.oldCountOfPickedLine,
        "No of New Picked Lines":x.newCountOfPickedLine,
        "Confimed By":x.deliveryConfirmedBy,
        "Confirmed On":this.cs.dateapiwithTime(x.deliveryConfirmedOn),
        
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Picklist Cancellation");
  }
  onItemSelect(item: any) {
    console.log(item);
  }
  
  OnItemDeSelect(item:any){
    console.log(item);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}

form = this.fb.group({
  assignedPickerId: [,],
});





}

