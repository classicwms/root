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
import { PreinboundPopupeditComponent } from './preinbound-popupedit/preinbound-popupedit.component';
import { CancelComponent } from '../cancel/cancel.component';
import { CustomdialogComponent } from 'src/app/common-field/customdialog/customdialog.component';
import { RequestpopupComponent } from 'src/app/common-field/requestpopup/requestpopup.component';
@Component({
  selector: 'app-preinbound-main',
  templateUrl: './preinbound-main.component.html',
  styleUrls: ['./preinbound-main.component.scss']
})
export class PreinboundMainComponent implements OnInit {
screenid=3044;
  Update(arg0: any, code: any) {
    throw new Error('Method not implemented.');
  }
  Create(arg0: any) {
    throw new Error('Method not implemented.');
  }
  displayedColumns: string[] = ['select', 'statusId', 'referenceDocumentType', 'refDocNumber', 'containerNo', 'preInboundNo', 'createdBy', 'refDocDate',];
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
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.search();
    //this.searchfilter(true);
  }
 

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
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Status  ": x.statusDescription,
        "Order Type ": x.referenceDocumentType,
        "Order No ": x.refDocNumber,
        "Container No": x.containerNo,
        'Preinbound No': x.preInboundNo,
        'Created By': x.createdBy,
        'Order Date':this.cs.dateapiwithTime(x.refDocDate),
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
  openDialogHeader(data: any = 'New'): void {
    console.log(this.selectedPreinbound)
      if (data != 'New')
      if (this.selectedPreinbound.length === 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
      const dialogRef = this.dialog.open(PreinboundPopupeditComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        data: { pageflow: data, code: data != 'New' ? this.selectedPreinbound[0].preInboundNo : null,warehouseId: data != 'New' ? this.selectedPreinbound[0].warehouseId : null,companyCode: data != 'New' ? this.selectedPreinbound[0].companyCode : null,plantId: data != 'New' ? this.selectedPreinbound[0].plantId : null,languageId: data != 'New' ? this.selectedPreinbound[0].languageId : null}
      });
    
      dialogRef.afterClosed().subscribe(result => {
        this.search();
      });
   }
  openDialog(data: any = 'new',type ?: any): void {
    if(type && type != undefined){
      this.selectedPreinbound = [];
      this.selectedPreinbound.push(type);
    }
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

  openDialogView(data) {
    let obj: any = {};
    obj.languageId=[this.auth.languageId];
    obj.companyCodeId=[this.auth.companyId];
    obj.plantId=[this.auth.plantId];
    obj.warehouseId=[this.auth.warehouseId];
    obj.refDocumentNo=[data.refDocNumber];
    this.spin.show();
    this.sub.add(this.service.searchInboundHeader(obj).subscribe((res: any[]) => {
      const lineReferences = res[0]?.line?.map(line => line.lineReference) || []; 
      const maxLineRef = lineReferences.length; 
      console.log(maxLineRef);
    const dialogRef2 = this.dialog.open(RequestpopupComponent, {
      width: '50%',
      maxWidth: '50%',
      position: {
        top: '1%',
      },
      data: { title: "Order Details", body: "Order Details", value: res,lines:maxLineRef,},
    });
    
    dialogRef2.afterClosed().subscribe(result => {
      // Handle the result if needed
    });
  }))
   this.spin.hide();
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
  list:any[]=[];
  search() {
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    this.spin.show();
    this.sub.add(this.service.searchSpark(obj).subscribe(res => {
      let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.preInbound = result;
      console.log(res);
      res.forEach(element => {
        if(element.inboundOrderTypeId == 5){
          this.list.push(element)
        }
      });
      console.log(this.list);
      this.spin.hide();
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
       // const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        res.forEach(x => tempstatusIdList.push({ item_id: x.statusId, item_text:x.statusDescription }));
        this.statusIdList = tempstatusIdList;
        this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
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
  //   const dialogRef = this.dialog.open(PreinboundUploadComponent, {
  //     disableClose: true,
  //     width: '80%',
  //     maxWidth: '80%',
  //     position: { top: '9%', },
  //   });
    
  //   dialogRef.afterClosed().subscribe(result => {
  // })
  this.router.navigate(['/main/inbound/preInboundCreate'])
  
  }


  onChange() {
    const choosen= this.selectedPreinbound[this.selectedPreinbound.length - 1];   
    this.selectedPreinbound.length = 0;
    this.selectedPreinbound.push(choosen);
  }


  cancel(data: any): void {
    const dialogRef = this.dialog.open(CustomdialogComponent, {
      // width: '60%', height: '70%',
      width: '50%',
      maxWidth: '80%',
      position: {
        top: '6.7%',
      },

      data: { title: "Confirm", body: "Do you want to replace the Order No?" },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result == 'Yes') {
      const dialogRef = this.dialog.open(CancelComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        data: { code: data,}
      });
    }
    else{
      dialogRef.afterClosed().subscribe(result => {
        this.search();
      });
    }
    }
    )
  }
}
