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
import { CommonService, dropdownelement1,} from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { AssignPickerComponent } from "../../assignment/assignment-main/assign-picker/assign-picker.component";
import { ReversalOutboundPopupComponent } from "../reversal-outbound-popup/reversal-outbound-popup.component";
import { ReversalOutboundService } from "../reversal-outbound.service";

export interface reversal {
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
  actions: string;

}

const ELEMENT_DATA: reversal[] = [
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },
  { no: "Value", lineno: 'readonly', partner: 'readonly', product: 'readonly', description: 'readonly', refdocno: 'readonly', variant: 'readonly', order: 'readonly', uom: 'dropdowm', req: 'readonly', allocated: 'readonly', status: 'readonly', preoutboundno: 'readonly', type: 'readonly', actions: 'readonly', },

];
@Component({
  selector: 'app-reversaloutbound-main',
  templateUrl: './reversaloutbound-main.component.html',
  styleUrls: ['./reversaloutbound-main.component.scss']
})
export class ReversaloutboundMainComponent implements OnInit {
  screenid: 1069 | undefined;
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
  constructor(private service: ReversalOutboundService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.search(true);

  }
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  searhform = this.fb.group({
    endreversedOn: [],
    itemCode: [],
    outboundReversalNo: [],
    packBarcode: [],
    partnerCode: [],
    refDocNumber: [],
    reversalType: [],
    reversedBy: [],
    startreversedOn: [],
    statusId: [],
    warehouseId: [[this.auth.warehouseId]],
  });




  dropdownSettings: IDropdownSettings = {
    idField: 'id',
    textField: 'itemName',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  itemCodeListselected: any[] = [];
  itemCodeList: any[] = [];

  outboundReversalNoListselected: any[] = [];
  outboundReversalNoList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];

  refDocNumberselected: any[] = [];
  refDocNumberList: any[] = [];

  reversalTypeselected: any[] = [];
  reversalTypeList: any[] = [];

  packBarcodeListselected: any[] = [];
  packBarcodeList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];
  warehouseId = this.auth.warehouseId;
  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endreversedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endreversedOn.value));
      this.searhform.controls.startreversedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startreversedOn.value));


      // patching
      // const itemCode = [...new Set(this.itemCodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.itemCode.patchValue(itemCode);

      // const outboundReversalNo = [...new Set(this.outboundReversalNoListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.outboundReversalNo.patchValue(outboundReversalNo);

      // const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.partnerCode.patchValue(partnerCode);

      // const packBarcode = [...new Set(this.packBarcodeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.packBarcode.patchValue(packBarcode);

      // const refDocNumber = [...new Set(this.refDocNumberselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.refDocNumber.patchValue(refDocNumber);

      // const reversalType = [...new Set(this.reversalTypeselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.reversalType.patchValue(reversalType);

      // const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.statusId.patchValue(statusId);
    }
    this.service.search(this.searhform.value).subscribe(res => {
      let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.show();
      if (ispageload) {
        let tempitemCodeList: any[] = []
        const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
        itemCode.forEach(x => tempitemCodeList.push({ value: x, label: x }));
        this.itemCodeList = tempitemCodeList;

        let tempoutboundReversalNoList: any[] = []
        const outboundReversalNo = [...new Set(res.map(item => item.outboundOrderTypeId))].filter(x => x != null)
        outboundReversalNo.forEach(x => tempoutboundReversalNoList.push({ value: x, label: x }));
        this.outboundReversalNoList = tempoutboundReversalNoList;

        let temppartnerCodeList: any[] = []
        const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
        this.partnerCodeList = temppartnerCodeList;

        let temppackBarcodeList: any[] = []
        const packBarcode = [...new Set(res.map(item => item.packBarcode))].filter(x => x != null)
        packBarcode.forEach(x => temppackBarcodeList.push({ value: x, label: x }));
        this.packBarcodeList = temppackBarcodeList;

        let temprefDocNumberList: any[] = []
        const refDocNumber = [...new Set(res.map(item => item.refDocNumber))].filter(x => x != null)
        refDocNumber.forEach(x => temprefDocNumberList.push({ value: x, label: x }));
        this.refDocNumberList = temprefDocNumberList;

        let tempreversalTypeList: any[] = []
        const reversalType = [...new Set(res.map(item => item.reversalType))].filter(x => x != null)
        reversalType.forEach(x => tempreversalTypeList.push({ value: x, label: x }));
        this.reversalTypeList = tempreversalTypeList;

        let tempstatusIdList: any[] = []
        const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
        this.statusIdList = tempstatusIdList;
      }
      this.dataSource = new MatTableDataSource<any>(result);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      this.spin.hide();
    }, err => {

      this.cs.commonerror(err);
      this.spin.hide();

    });


  }
  reload() {
    this.searhform.reset();
  }
  assign(): void {

    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '55%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  title1 = "Outbound";
  title2 = "Order Management";


  displayedColumns: string[] = [ 'select', 'actions', 'outboundReversalNo', 'refDocNumber', 'itemCode', 'packBarcode', 'reversalType', 'reversedQty', 'reversedOn', 'reversedBy',];
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
  checkboxLabel(row?: reversal): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  new(): void {

    const dialogRef = this.dialog.open(ReversalOutboundPopupComponent, {
      disableClose: true,
      width: '45%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.search();
    });
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

downloadexcel() {
  var res: any = [];
  this.dataSource.data.forEach(x => {
    res.push({
      "Status ": this.cs.getstatus_text(x.statusId),
      'Reversal No': x.outboundReversalNo,
      'Order No': x.refDocNumber,
      "Product Code": x.itemCode,
      "Pallet ID": x.packBarcode,
      "Revesal Type": x.reversalType,
      "Reversed Qty": x.reversedQty,
      "Reversed On": this.cs.dateExcel(x.reversedOn),
      "Reversed By": x.reversedBy,
    });

  })
  this.cs.exportAsExcel(res, "Reversal");
}

}
