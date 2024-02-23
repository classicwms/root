import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { CommonService, dropdownelement } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ContainerReceiptService } from '../../Container-receipt/container-receipt.service';
import { PreinboundNewComponent } from '../preinbound-new/preinbound-new.component';
import { PreinboundUploadComponent } from '../preinbound-upload/preinbound-upload.component';
import { PreinboundService } from '../preinbound.service';
import { Table } from 'primeng/table';
@Component({
  selector: 'app-preinbound-main',
  templateUrl: './preinbound-main.component.html',
  styleUrls: ['./preinbound-main.component.scss']
})
export class PreinboundMainComponent implements OnInit {
  Update(arg0: any, code: any) {
    throw new Error('Method not implemented.');
  }
  Create(arg0: any) {
    throw new Error('Method not implemented.');
  }
  displayedColumns: string[] = ['select', 'statusId', 'inboundOrderTypeId', 'refDocNumber', 'containerNo', 'preInboundNo', 'createdBy', 'refDocDate',];
  constructor(private service: PreinboundService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();


  preInbound: any[] = [];
  selectedPreinbound : any[] = [];
  @ViewChild('preInboundTag') preInboundTag: Table | any;

  ngOnInit(): void {
    this.search();
    //this.searchfilter(true);
  }
  screenid: 1042 | undefined;

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




  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.preInbound.forEach(x => {
      res.push({
        "Status  ": this.cs.getstatus_text(x.statusId),
        "Order Type ": this.cs.getinboundorderType_text(x.inboundOrderTypeId),
        "Order No ": x.refDocNumber,
        "Container No": x.containerNo,
        'Reference Document Type': x.referenceDocumentType,
        'Preinbound No': x.preInboundNo,
        'Created By': x.createdBy,
        'Order Date': this.cs.dateExcel(x.refDocDate),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Preinbound");
  }

  deleteDialog() {
    if (this.selectedPreinbound.length === 0) {
      this.toastr.error("Kindly select any row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selectedPreinbound[0].statusId != 6) {
      this.toastr.error("Order can't delete.", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(DeleteComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selectedPreinbound[0]);

      }
    });
  }
  deleterecord(id: any) {
    this.spin.show();
    this.sub.add(this.service.Delete(id.preInboundNo, id.warehouseId).subscribe((res) => {
      this.toastr.success(id.preInboundNo + " Deleted successfully.", "", {
        timeOut: 2000,
        progressBar: false,
      });

      this.spin.hide(); //this.getAllListData();
      this.search();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selectedPreinbound.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }


    if (this.selectedPreinbound[0].statusId === 24) {
      this.toastr.error("Order can't be proccessed as it's already confirmed.", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    let paramdata = "";

    if (this.selectedPreinbound.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedPreinbound[0], pageflow: data });
      this.router.navigate(['/main/inbound/preinbound-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/preinbound-create/' + paramdata]);
    }

  }




  filtersearch() {
    this.spin.show();
    this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
      this.spin.hide();
      this.preInbound = res;
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));

  }
  warehouseId = this.auth.warehouseId
  search() {
    let obj: any = {};
    obj.warehouseId = [this.auth.warehouseId];
    this.spin.show();
    this.sub.add(this.service.search(obj).subscribe(res => {
      let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();

      this.preInbound = result;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));
    
  }







  searhform = this.fb.group({
    containerNo: [],
    endConfirmedOn: [],
    endCreatedOn: [],
    inboundOrderTypeId: [],
    refDocNumber: [],
    startConfirmedOn: [],
    startCreatedOn: [],
    statusId: [],
    warehouseId: [[this.auth.warehouseId]],
  });

  dropdownSettings: IDropdownSettings = {
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };


  containerNoListselected: any[] = [];
  containerNoList: dropdownelement[] = [];

  inboundOrderTypeListselected: any[] = [];
  inboundOrderTypeList: dropdownelement[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: dropdownelement[] = [];

  statusIdListselected: any[] = [];
  statusIdList: dropdownelement[] = [];


  searchfilter(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
      this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
      this.searhform.controls.startConfirmedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startConfirmedOn.value));
      this.searhform.controls.endConfirmedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endConfirmedOn.value));

      //patching
      const containerNo = [...new Set(this.containerNoListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.containerNo.patchValue(containerNo);

      const inboundOrderType = [...new Set(this.inboundOrderTypeListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.inboundOrderType.patchValue(inboundOrderType);

      const refDocNumber = [...new Set(this.refDocNumberListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.refDocNumber.patchValue(refDocNumber);




      const statusId = [...new Set(this.statusIdListselected.map(item => item.item_id))].filter(x => x != null);
      this.searhform.controls.statusId.patchValue(statusId);


    }
    this.service.search(this.searhform.value).subscribe(res => {
      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let tempContainerList: dropdownelement[] = []
        const containerNo = [...new Set(res.map(item => item.containerNo))].filter(x => x != null)
        containerNo.forEach(x => tempContainerList.push({ item_id: x, item_text: x }));
        this.containerNoList = tempContainerList;

        let tempinboundOrderTypeList: dropdownelement[] = []
        const containerReceiptNo = [...new Set(res.map(item => item.containerReceiptNo))].filter(x => x != null)
        containerReceiptNo.forEach(x => tempinboundOrderTypeList.push({ item_id: x, item_text: x }));
        this.inboundOrderTypeList = tempinboundOrderTypeList;

        let temprefDocNumberList: dropdownelement[] = []
        const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temprefDocNumberList.push({ item_id: x, item_text: x }));
        this.refDocNumberList = temprefDocNumberList;

        let tempstatusIdList: dropdownelement[] = []
        const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        statusId.forEach(x => tempstatusIdList.push({ item_id: x, item_text: this.cs.getstatus_text(x) }));
        this.statusIdList = tempstatusIdList;
      }
      this.preInbound = res;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    });

  }
  reload() {
    window.location.reload();
  }
  showSuccess() {
    this.toastr.success('ASN Processed successfully',);
  }
  preInboundUpload(){
    const dialogRef = this.dialog.open(PreinboundUploadComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '80%',
      position: { top: '9%', },
    });
    
    dialogRef.afterClosed().subscribe(result => {
  })
  
  
  }


  onChange() {
    const choosen= this.selectedPreinbound[this.selectedPreinbound.length - 1];   
    this.selectedPreinbound.length = 0;
    this.selectedPreinbound.push(choosen);
  }
}
