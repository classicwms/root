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


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination

  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
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
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (this.selection.selected[0].statusId != 6) {
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
      // data: this.selection.selected[0],
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selection.selected[0]);

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
      if (this.selection.selected.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }


    if (this.selection.selected[0].statusId === 24) {
      this.toastr.error("Order can't be proccessed as it's already confirmed.", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    let paramdata = "";

    if (this.selection.selected.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selection.selected[0], pageflow: data });
      this.router.navigate(['/main/inbound/preinbound-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/preinbound-create/' + paramdata]);
    }

    // const dialogRef = this.dialog.open(CasecategoryDisplayComponent, {
    //   disableClose: true,
    //   width: '50%',
    //   maxWidth: '80%',
    //   position: { top: '9%', },
    //   data: { pageflow: data, code: data != 'New' ? this.selection.selected[0].clientId : null }
    // });

    // dialogRef.afterClosed().subscribe(result => {

    //   this.getAllListData();
    // });
  }




  filtersearch() {
    this.spin.show();
    this.sub.add(this.service.search(this.searhform.getRawValue()).subscribe((res: any[]) => {
      this.spin.hide();
      this.dataSource = new MatTableDataSource<any>(res);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
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

      this.dataSource = new MatTableDataSource<any>(result);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));

    // this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
    // this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));

    // this.spin.show();
    // this.cas.getalldropdownlist([this.cas.dropdownlist.setup.classId.url,
    // this.cas.dropdownlist.setup.statusId.url,
    // this.cas.dropdownlist.client.clientId.url,

    // ]).subscribe((results) => {
    //   this.classIdList = this.cas.foreachlist_searchpage(results[0], this.cas.dropdownlist.setup.classId.key);
    //   this.statuslist = this.cas.foreachlist_searchpage(results[1], this.cas.dropdownlist.setup.statusId.key);
    //   this.clientlist = this.cas.foreachlist_searchpage(results[2], this.cas.dropdownlist.client.clientId.key);
    //   this.spin.hide();

    //   this.spin.show();
    //   this.sub.add(this.searhform.getRawValue().subscribe((res: any[]) => {
    //     if (this.auth.classId != '3')
    //       this.ELEMENT_DATA = res.filter(x => x.classId === Number(this.auth.classId));
    //     else
    //       this.ELEMENT_DATA = res;
    //     this.ELEMENT_DATA.forEach((x) => {
    //       x.classId = this.classIdList.find(y => y.key == x.classId)?.value;
    //       x.statusIddes = this.statuslist.find(y => y.key == x.statusId)?.value;
    //       x.referenceField1 = this.clientlist.find(y => y.key == x.clientId)?.value;
    //     })


    //     this.dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
    //     this.selection = new SelectionModel<any>(true, []);
    //     this.dataSource.sort = this.sort;
    //     this.dataSource.paginator = this.paginator;
    //     this.spin.hide();
    //   }, (err: any) => {
    //     this.cs.commonerrorNew(err);
    //     this.spin.hide();
    //   }));
    // }, (err) => {
    //   this.toastr.error(err, "");
    // });
    // this.spin.hide();
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
      this.dataSource = new MatTableDataSource<any>(res);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
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
}
