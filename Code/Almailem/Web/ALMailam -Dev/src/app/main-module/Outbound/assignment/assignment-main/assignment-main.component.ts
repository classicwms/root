import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService, dropdownelement1 } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { OrdermanagementService } from "../../order-management/ordermanagement-main/ordermanagement.service";
import { AssignPickerComponent } from "./assign-picker/assign-picker.component";
import { Table } from "primeng/table";

export interface ordermanagement {
  no: string;
  lineno: string;
  partner: string;
  product: string;
  description: string;
  refdocno: string;
  variant: string;
  order: string;
  type: string;
  preoutboundno: string;
  uom: string;
  req: string;
  allocated: string;
  status: string;
  reallocate: string;

}

const ELEMENT_DATA: ordermanagement[] = [
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },
  { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', reallocate: 'value', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', },

];
@Component({
  selector: 'app-assignment-main',
  templateUrl: './assignment-main.component.html',
  styleUrls: ['./assignment-main.component.scss']
})
export class AssignmentMainComponent implements OnInit {
  screenid = 3061;
  assignment: any[] = [];
  selectedassignment: any[] = [];
  @ViewChild('assignmentTag') assignmentTag: Table | any;

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  storecodeList: any;
  currentDate = new Date();
  fifteenDaysDate = new Date();
  ondeDaysBeforeDate = new Date();

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



  assign(type?: any): void {
    if (type && type != undefined) {
      this.selectedassignment = [];
      this.selectedassignment.push(type);
    }
    if (this.selectedassignment.length === 0) {
      this.toastr.error("Kindly select any row", "Norification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
      if (this.auth.warehouseId != 100) {
        let dataErrorArray: any = [];
        this.selectedassignment.forEach(data => {
          if (data.levelId != this.selectedassignment[0].levelId) {
            dataErrorArray.push(data.lineNumber);
          }
        });

        if (dataErrorArray.length > 0) {
          this.toastr.error(
            "The selected records have different level Id",
            "Notification", {
            timeOut: 2000,
            progressBar: false,
          }
          );
          this.cs.notifyOther(true);
          return;
        }
      }


    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '55%',
      data: { levelID: this.selectedassignment[0].levelId, }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        let data: any[] = [];
        this.selectedassignment.forEach((x: any) => {
          data.push({
            companyCodeId: x.companyCodeId,
            plantId: x.plantId,
            languageId: x.languageId,
            itemCode: x.itemCode,
            lineNumber: x.lineNumber,
            partnerCode: x.partnerCode,
            preOutboundNo: x.preOutboundNo,
            proposedPackCode: x.proposedPackBarCode,
            proposedStorageBin: x.proposedStorageBin,
            refDocNumber: x.refDocNumber,
            warehouseId: x.warehouseId,
          })
        });
        this.spin.show();
        this.sub.add(this.service.AssignPickerv2(data, result).subscribe(res => {
          this.spin.hide();
          this.toastr.success(result + " assign successfully.", "Notification", {
            timeOut: 2000,
            progressBar: false,
          })
          this.search();

        }, err => {

          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));
      }
    });
  }


  assignmentList: any;
  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: OrdermanagementService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.fifteenDaysDate.setDate(this.currentDate.getDate() + 7);
    this.ondeDaysBeforeDate.setDate(this.currentDate.getDate() - 1);

    // this.searhform.controls.endRequiredDeliveryDate.patchValue(this.fifteenDaysDate);
    // this.searhform.controls.startRequiredDeliveryDate.patchValue(this.ondeDaysBeforeDate);

    this.getstorename();
    this.search(true);

  }


  warehouseId = this.auth.warehouseId


  searhform = this.fb.group({
    endRequiredDeliveryDate: [],
    endoOrderDate: [],
    itemCode: [],
    outboundOrderTypeId: [],
    partnerCode: [],
    preOutboundNo: [],
    refDocNumber: [],
    soType: [],
    companyCodeId: [[this.auth.companyId]],
    plantId: [[this.auth.plantId]],
    languageId: [[this.auth.languageId]],
    startOrderDate: [],
    startRequiredDeliveryDate: [],
    statusId: [[42, 43]], //[42, 43]
    warehouseId: [[this.auth.warehouseId]],

  });




  dropdownSettings = {
    singleSelection: false,
    text: "Select",
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };

  itemCodeListselected: any[] = [];
  itemCodeList: any[] = [];

  outboundOrderTypeIdListselected: any[] = [];
  outboundOrderTypeIdList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];

  preOutboundNoselected: any[] = [];
  preOutboundNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];

  soTypeListselected: any[] = [];
  soTypeList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  getstorename() {
    //this.spin.show();
    this.sub.add(this.service.GetStoreCode().subscribe(res => {
      this.storecodeList = res;
      console.log(this.storecodeList)
      //this.spin.hide();
    },
      err => {
        this.cs.commonerrorNew(err);;
        //this.spin.hide();
      }));
  }

  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endRequiredDeliveryDate.value));
      this.searhform.controls.startRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startRequiredDeliveryDate.value));

    }
    this.spin.show();
    this.service.searchSpark(this.searhform.value).subscribe(res => {
      //let result = res.filter((x: any) => x.statusId == this.statusId);
      //  let result = res.filter((x: any) => x.statusId == 42 || 43);
      let result = res;  //.filter((x: any) => x.statusId == 42 || x.statusId == 43)


      // result.forEach((x) => {
      //   // x.noteTypeId = this.noteTypeIdlist.find(y => y.key == x.noteTypeId)?.value;
      // x.partnerCode = this.storecodeList.find(y => y.partnerCode == x.partnerCode)?.partnerName;
      // })


      if (ispageload) {
        let tempitemCodeList: any[] = []
        const itemCode = [...new Set(result.map(item => item.itemCode))].filter(x => x != null)
        itemCode.forEach(x => tempitemCodeList.push({ value: x, label: x }));
        this.itemCodeList = tempitemCodeList;

        let tempoutboundOrderTypeIdList: any[] = []
        const outboundOrderTypeId = [...new Set(result.map(item => item.outboundOrderTypeId))].filter(x => x != null)
        outboundOrderTypeId.forEach(x => tempoutboundOrderTypeIdList.push({ value: x, label: this.cs.getoutboundOrderType_text(x) }));
        this.outboundOrderTypeIdList = tempoutboundOrderTypeIdList;

        let temppartnerCodeList: any[] = []
        const partnerCode = [...new Set(result.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
        this.partnerCodeList = temppartnerCodeList;

        let temppreOutboundNoList: any[] = []
        const preOutboundNo = [...new Set(result.map(item => item.preOutboundNo))].filter(x => x != null)
        preOutboundNo.forEach(x => temppreOutboundNoList.push({ value: x, label: x }));
        this.preOutboundNoList = temppreOutboundNoList;


        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(result.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;

        let tempsoTypeList: any[] = []
        const soType = [...new Set(result.map(item => item.referenceField1))].filter(x => x != null)
        soType.forEach(x => tempsoTypeList.push({ value: x, label: x }));
        this.soTypeList = tempsoTypeList;

        let tempstatusIdList: any[] = []
        // const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
        res.forEach(x => tempstatusIdList.push({ value: x.statusId, label: x.statusDescription }));
        this.statusIdList = tempstatusIdList;
        this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);
      }
      this.assignmentList = result;
      this.assignment = result;

      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);;
      this.spin.hide();

    });


  }
  reload() {
    this.searhform.reset();
  }


  displayedColumns: string[] = ['select', 'proposedStorageBin', 'refDocNumber', 'referenceField1', 'outboundOrderTypeId', 'partnerCode', 'lineNumber', 'itemCode', 'referenceField9', 'referenceField10', 'orderQty', 'allocatedQty', 'requiredDeliveryDate', 'description', 'statusId',];
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }





  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.assignment.forEach(x => {
      res.push({
        "Branch": x.plantDescription,
        "Warehouse": x.warehouseDescription,
        "Target Branch": x.targetBranchCode,
        "Token Number": x.tokenNumber,
        "Sales Order No": x.salesOrderNumber,
        "Status  ": x.statusDescription,
        "Order No  ": x.refDocNumber,
        'Order Type': x.referenceDocumentType,
        "Mfr Name": x.manufacturerName,
        "Part No": x.itemCode,
        "Line No": x.lineNumber,
        "Order Qty": x.orderQty,
        "Allocated Qty": x.allocatedQty,
        "Bin Location": x.proposedStorageBin,
        "Description": x.description,
        "Barcode Id": x.partnerItemBarcode,
        "Order Category": x.referenceField1,
        "Storage Section": x.referenceField9,
        "Level": x.levelId,





        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Assignment");
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

  searchrefDocNumber: any;
  searchitemCode: any;
  searchpartnerCode: any;
  referenceField9: any;
  referenceField10: any;
  outboundOrderTypeId1: any;
  orderCategory: any;
  binLocation: any;

  filterValues = {
    refDocNumber: '',
    itemCode: '',
    partnerCode: '',
    referenceField9: '',
    referenceField10: '',
    outboundOrderTypeId: '',
    referenceField1: '',
    proposedStorageBin: '',
  };

  searchrefDocNumberMethod(event) {
    this.filterValues.refDocNumber = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  searchitemCodeMethod(event) {
    this.filterValues.itemCode = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  searchpartnerCodeMethod(event) {
    this.filterValues.partnerCode = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  searchStorageMethod(event) {
    this.filterValues.referenceField9 = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  searchLevelMethod(event) {
    this.filterValues.referenceField10 = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
    console.log(this.filterValues)
    console.log(this.dataSource.filter)
  }
  searchoutboundOrderTypeIdMethod(event) {
    this.filterValues.outboundOrderTypeId = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  searchorderCatMethod(event) {
    this.filterValues.referenceField1 = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  searchbinLocation(event) {
    this.filterValues.proposedStorageBin = event;
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }

  customFilterPredicate() {
    const myFilterPredicate = function (data: any, filter: string): boolean {
      let searchString = JSON.parse(filter);
      return data.proposedStorageBin.toString().trim().toLowerCase().indexOf(searchString.proposedStorageBin.toLowerCase()) !== -1
        && data.refDocNumber.toString().trim().toLowerCase().indexOf(searchString.refDocNumber.toLowerCase()) !== -1
        && data.outboundOrderTypeId.toString().trim().toLowerCase().indexOf(searchString.outboundOrderTypeId.toLowerCase()) !== -1
        && data.itemCode.toString().trim().toLowerCase().indexOf(searchString.itemCode.toLowerCase()) !== -1
        && data.partnerCode.toString().trim().toLowerCase().indexOf(searchString.partnerCode.toLowerCase()) !== -1
        && data.referenceField1.toString().trim().toLowerCase().indexOf(searchString.referenceField1.toLowerCase()) !== -1;

    }
    return myFilterPredicate;
  }
  onChange() {
    const choosen = this.selectedassignment[this.selectedassignment.length - 1];
    this.selectedassignment.length = 0;
    this.selectedassignment.push(choosen);
  }
}
