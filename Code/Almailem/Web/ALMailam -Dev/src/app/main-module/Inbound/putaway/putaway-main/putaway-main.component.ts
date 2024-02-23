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
import {
  CommonService,
  dropdownelement1,
} from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PutawayService } from '../putaway.service';
import { Table } from 'primeng/table';
import { stat } from 'fs';
import { PutawayheaderComponent } from '../putawayheader/putawayheader.component';
import { AssignPickerComponent } from 'src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component';

@Component({
  selector: 'app-putaway-main',
  templateUrl: './putaway-main.component.html',
  styleUrls: ['./putaway-main.component.scss'],
})
export class PutawayMainComponent implements OnInit {
  screenid = 3051;
  putaway: any[] = [];
  selectedputaway: any[] = [];
  @ViewChild('putawayTag') putawayTag: Table | any;

  selectedStatusIdList: any[] = [];
  constructor(
    private service: PutawayService,
    public toastr: ToastrService,
    public dialog: MatDialog,
    private spin: NgxSpinnerService,
    private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService
  ) {
    this.selectedStatusIdList = [
      { value: 19, label: 'Putaway Created' },
      { value: 20, label: 'Putaway Confirmed' },
    ];
  }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.search(true);
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
      this.icon = 'expand_more';
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.putaway.forEach((x) => {
      res.push({
        'Branch': x.plantDescription,
        'Warehouse': x.warehouseDescription,
        'Status ': x.statusDescription,
        'Order Type ': x.referenceDocumentType,
        'Part No':x.referenceField5,
        'Order No': x.refDocNumber,
        'Putaway No': x.putAwayNumber,
        'Barcode': x.barcodeId,
        'Storage Bin': x.proposedStorageBin,
        'Assign User': x.assignedUserId,
        "Level ":x.levelId,
        'To Qty': x.putAwayQuantity,
        'Created By': x.createdBy,
        'Created On':this.cs.dateapiwithTime(x.createdOn),
         "Remark":x.remark,
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
    });
    this.cs.exportAsExcel(res, 'Putaway');
  }

  warehouseId = this.auth.warehouseId;

  searhform = this.fb.group({
    createdBy: [],
    endCreatedOn: [],
    packBarcodes: [],
    barcodeId: [],
    proposedHandlingEquipment: [],
    proposedStorageBin: [],
    putAwayNumber: [],
    refDocNumber: [],
    itemCode:[],
    startCreatedOn: [],
    statusId: [[19]], //19
    warehouseId: [[this.auth.warehouseId]],
    companyCodeId: [[this.auth.companyId]],
    languageId: [[this.auth.languageId]],
    plantId: [[this.auth.plantId]],
  });

  dropdownSettings = {
    singleSelection: false,
    text: 'Select',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2,
  };

  packBarcodesListselected: any[] = [];
  packBarcodesList: any[] = [];

  proposedHandlingEquipmentListselected: any[] = [];
  proposedHandlingEquipmentList: any[] = [];

  proposedStorageBinListselected: any[] = [];
  proposedStorageBinList: any[] = [];

  putAwayNumberListselected: any[] = [];
  putAwayNumberList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  search(ispageload = false) {
    if (!ispageload) {
    }
    this.spin.show();
    this.service.searchSpark(this.searhform.value).subscribe(
      (res) => {
        let result = res.filter((x: any) => x.statusId != 22);
        // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);

        if (ispageload) {
          let temppackBarcodesList: any[] = [];
          const packBarcodes = [
            ...new Set(result.map((item) => item.barcodeId)),
          ].filter((x) => x != null);
          packBarcodes.forEach((x) =>
            temppackBarcodesList.push({ value: x, label: x })
          );
          this.packBarcodesList = temppackBarcodesList;

          let tempproposedHandlingEquipmentList: any[] = [];
          const proposedHandlingEquipment = [
            ...new Set(result.map((item) => item.referenceField5)),
          ].filter((x) => x != null);
          proposedHandlingEquipment.forEach((x) =>
            tempproposedHandlingEquipmentList.push({ value: x, label: x })
          );
          this.proposedHandlingEquipmentList =
            tempproposedHandlingEquipmentList;

          let tempproposedStorageBinList: any[] = [];
          const proposedStorageBin = [
            ...new Set(result.map((item) => item.proposedStorageBin)),
          ].filter((x) => x != null);
          proposedStorageBin.forEach((x) =>
            tempproposedStorageBinList.push({ value: x, label: x })
          );
          this.proposedStorageBinList = tempproposedStorageBinList;

          let tempputAwayNumberList: any[] = [];
          const putAwayNumber = [
            ...new Set(result.map((item) => item.putAwayNumber)),
          ].filter((x) => x != null);
          putAwayNumber.forEach((x) =>
            tempputAwayNumberList.push({ value: x, label: x })
          );
          this.putAwayNumberList = tempputAwayNumberList;

          let temprefDocNumberList: any[] = [];
          const refDocNumber = [
            ...new Set(result.map((item) => item.refDocNumber)),
          ].filter((x) => x != null);
          refDocNumber.forEach((x) =>
            temprefDocNumberList.push({ value: x, label: x })
          );
          this.refDocNumberList = temprefDocNumberList;

          let tempstatusIdList: any[] = [];
          // const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
          res.forEach((x) =>
            tempstatusIdList.push({
              value: x.statusId,
              label: x.statusDescription,
            })
          );
          this.statusIdList = tempstatusIdList;
          this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(
            this.statusIdList
          );
        }
        this.spin.hide();
        this.putaway = res;
      },
      (err) => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }
    );
  }

  deleteDialog() {
    if (this.selectedputaway.length === 0) {
      this.toastr.error('Kindly select any row', 'Norification');
      return;
    }
    if (this.selectedputaway[0].statusId != 19) {
      this.toastr.error("Confirmed items can't be deleted", 'Norification');
      return;
    }
    const dialogRef = this.dialog.open(DeleteComponent, {
      disableClose: true,
      width: '40%',
      maxWidth: '80%',
      position: { top: '9%' },
      // data: this.selection.selected[0],
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleterecord(this.selectedputaway[0]);
      }
    });
  }
  deleterecord(obj: any) {
    this.spin.show();
    this.sub.add(
      this.service.deletePutawayHeader(obj).subscribe(
        (ress) => {
          this.toastr.success(
            obj.putAwayNumber + ' Deleted successfully.',
            'Notification',
            {
              timeOut: 2000,
              progressBar: false,
            }
          );
          this.search(true);
          this.spin.hide();
        },
        (err) => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
        }
      )
    );
  }

  reload() {
    this.searhform.reset();
  }
  openDialog(data: any = 'new', type?: any): void {
    if (type && type != undefined) {
      this.selectedputaway = [];
      this.selectedputaway.push(type);
    }

    if (data != 'New')
      if (this.selectedputaway.length === 0) {
        this.toastr.error('Kindly select any row', 'Notification', {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }

    if (data != 'Display')
      if (![19, 21].includes(this.selectedputaway[0].statusId)) {
        this.toastr.error(
          "Order can't be edited as it's already proccessed.",
          'Notification',
          {
            timeOut: 2000,
            progressBar: false,
          }
        );
        return;
      }
    let paramdata = '';

    if (this.selectedputaway.length > 0) {
      paramdata = this.cs.encrypt({
        code: this.selectedputaway[0],
        pageflow: data,
      });
      this.router.navigate(['/main/inbound/putaway-create/' + paramdata]);
    } else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/putaway-create/' + paramdata]);
    }
  }
  openDialogHeader(data: any = 'New'): void {
    console.log(this.selectedputaway);
    if (data != 'New')
      if (this.selectedputaway.length === 0) {
        this.toastr.warning('Kindly select any Row', 'Notification', {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    const dialogRef = this.dialog.open(PutawayheaderComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: this.selectedputaway[0] },
    });

    dialogRef.afterClosed().subscribe((result) => {
      this.search(true);
    });
  }

  onItemSelect(item: any) {
    console.log(item);
  }

  OnItemDeSelect(item: any) {
    console.log(item);
  }
  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any) {
    console.log(items);
  }

  onChange() {
    const choosen = this.selectedputaway[this.selectedputaway.length - 1];
    this.selectedputaway.length = 0;
    this.selectedputaway.push(choosen);
  }

  assignUserID(): void {
    if (this.selectedputaway.length === 0) {
      this.toastr.error('Kindly select one row', '', {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '9%' },
    });

    dialogRef.afterClosed().subscribe((result2) => {
      if (result2) {
        this.selectedputaway.forEach((x: any) => {
          x.assignedUserId = result2;
        });
        this.updateHHtUser();
      }
    });
  }

  updateHHtUser() {
    this.spin.show();
    this.service.UpdateBulk(this.selectedputaway).subscribe((res) => {
      this.toastr.success('HHT Assigned successfully', 'Notification', {
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    });
  }
}
