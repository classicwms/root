import {
  SelectionModel
} from "@angular/cdk/collections";
import {
  Component,
  OnInit,
  ViewChild
} from "@angular/core";
import {
  FormBuilder
} from "@angular/forms";
import {
  MatDialog
} from "@angular/material/dialog";
import {
  MatPaginator,
  PageEvent
} from "@angular/material/paginator";
import {
  MatSort
} from "@angular/material/sort";
import {
  MatTableDataSource
} from "@angular/material/table";
import {
  Router
} from "@angular/router";
import {
  NgxSpinnerService
} from "ngx-spinner";
import {
  ToastrService
} from "ngx-toastr";
import {
  Subscription
} from "rxjs";
import {
  CommonService,
  dropdownelement1
} from "src/app/common-service/common-service.service";
import {
  MenuService
} from "src/app/common-service/menu.service";
import {
  AuthService
} from "src/app/core/core";
import {
  SelectionElement,
  MasterproductselectionService
} from "./masterproductselection.service";
import {
  SelectionPopupComponent
} from "./selection-popup/selection-popup.component";

// export interface selection {
//   no: string;
//   lineno: string;
//   partner: string;
//   product: string;
//   description: string;
//   refdocno: string;
//   variant: string;
//   type: string;
//   by: string;
//   }

//   const ELEMENT_DATA: selection[] = [
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },
//   { no: "1", lineno: 'readonly', partner: 'readonly', product: 'readonly',description: 'readonly',refdocno: 'readonly',variant: 'readonly',by: 'readonly',type: 'readonly', },

//   ];
@Component({
  selector: 'app-selection',
  templateUrl: './selection.component.html',
  styleUrls: ['./selection.component.scss']
})
export class SelectionComponent implements OnInit {

  displayedColumns: string[] = ['select', 'warehouseId', 'itemCode', 'description','manufacturerPartNo','uomId', 'itemType', 'itemGroup', 'createdBy', 'createdOn', ];
  sub = new Subscription();
  ELEMENT_DATA: SelectionElement[] = [];
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  @ViewChild(MatSort, {
    static: true
  })
  sort!: MatSort;
  @ViewChild(MatPaginator, {
    static: true
  })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId;
  searhform = this.fb.group({
    createdBy: [],
    description: [],
    endCreatedOn: [],
    endUpdatedOn: [],
    itemCode: [],
    itemGroup: [],
    itemType: [],
    startCreatedOn: [],
    startUpdatedOn: [],
    subItemGroup: [],
    updatedBy: [],
    warehouseId: [
      [this.auth.warehouseId]
    ],

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

  itemGroupListselected: any[] = [];
  itemGroupList: any[] = [];

  itemTypeListselected: any[] = [];
  itemTypeList: any[] = [];

  // preOutboundNoselected: any[] = [];
  // preOutboundNoList: any[] = [];


  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  pageNumber = 0;
  pageSize = 500;
  totalRecords = 0;

  constructor(public dialog: MatDialog,
    private service: MasterproductselectionService,
    // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private cs: CommonService,
    private ms: MenuService,
    // private excel: ExcelService,
    private router: Router,
    private fb: FormBuilder,
    private auth: AuthService) {}

  showFiller = false;
  animal: string | undefined;

  ngOnInit(): void {
    // this.auth.isuserdata();

    this.getAll();
  }

  getAll() {
    this.spin.show();
    this.sub.add(this.service.getAllByPagination(this.pageNumber, this.pageSize, this.searhform.getRawValue()).subscribe((res) => {
      let result = res.content.filter((x: any) => x.warehouseId == this.warehouseId);
      this.ELEMENT_DATA = result;
      this.dataSource = new MatTableDataSource < any > (result);
      this.selection = new SelectionModel < SelectionElement > (true, []);
      this.dataSource.sort = this.sort;
      this.totalRecords = res.totalElements;
      console.log(this.totalRecords)
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  dataSource = new MatTableDataSource < SelectionElement > (this.ELEMENT_DATA);
  selection = new SelectionModel < SelectionElement > (true, []);


  downloadexcel() {
    
    // if (excel)
    var res: any = [];
    this.spin.show()
    this.sub.add(this.service.Getall().subscribe((data: any) => {
      let result = data.filter((x: any) => x.warehouseId == this.warehouseId);
      console.log(result)
      console.log(res)
      result.forEach(x => {
      res.push({

        "Warehouse No ": x.warehouseId,
        'Product Code': x.itemCode,
        'Description': x.description,
        "Mfr Part No": x.manufacturerPartNo,
        "UOM": x.uomId,
        "Item Type  ": x.itemType,
        "Item Group": x.itemGroup,
        "Created By": x.createdBy,
        "Created On": this.cs.dateExcel(x.createdOn),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Product Master");
this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
  }

  selectionpara(): void {
    sessionStorage.removeItem('masterProduct');
    const dialogRef = this.dialog.open(SelectionPopupComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: {
        top: '9%',
      },
      data: {
        type: 'save'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  search(ispageload = false) {
    if (!ispageload) {
      //dateconvertion
      // this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));
      // this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
      //  patching
      if (this.itemCodeListselected.length > 0) {
        const itemCode = [...new Set(this.itemCodeListselected.map(item => item.id))].filter(x => x != null);
        this.searhform.controls.itemCode.patchValue(itemCode);
      }else{
        this.searhform.controls.itemCode.patchValue([ this.searhform.controls.itemCode.value]);
      }



      // const itemGroup = [...new Set(this.itemGroupListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.itemGroup.patchValue(itemGroup);

      // const itemType = [...new Set(this.itemTypeListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.itemType.patchValue(itemType);

      // const preOutboundNo = [...new Set(this.preOutboundNoselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.preOutboundNo.patchValue(preOutboundNo);



      // const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.statusId.patchValue(statusId);
    }
    this.service.search(this.searhform.value).subscribe(res => {
      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let tempitemCodeList: any[] = []
        const itemCode = [...new Set(res.map(item => item.itemCode))].filter(x => x != null)
        itemCode.forEach(x => tempitemCodeList.push({
          value: x,
          label: x
        }));
        this.itemCodeList = tempitemCodeList;

        let tempitemGroupList: any[] = []
        const itemGroup = [...new Set(res.map(item => item.itemGroup))].filter(x => x != null)
        itemGroup.forEach(x => tempitemGroupList.push({
          value: x,
          label: x
        }));
        this.itemGroupList = tempitemGroupList;

        let tempitemTypeList: any[] = []
        const itemType = [...new Set(res.map(item => item.itemType))].filter(x => x != null)
        itemType.forEach(x => tempitemTypeList.push({
          value: x,
          label: x
        }));
        this.itemTypeList = tempitemTypeList;

        //  let temppreOutboundNoList: any[] = []
        //  const preOutboundNo = [...new Set(res.map(item => item.preOutboundNo))].filter(x => x != null)
        //  preOutboundNo.forEach(x => temppreOutboundNoList.push({ id: x, itemName: x }));
        //  this.preOutboundNoList = temppreOutboundNoList;


        //  let tempstatusIdList: any[] = []
        //  const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        //  statusId.forEach(x => tempstatusIdList.push({ id: x, itemName: x }));
        //  this.statusIdList = tempstatusIdList;
      }
      this.dataSource = new MatTableDataSource < any > (res);
      this.selection = new SelectionModel < SelectionElement > (true, []);
      this.dataSource.sort = this.sort;
      this.totalRecords = res.totalElements;
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    });
  }

  pageHandler($event: PageEvent) {
    this.pageNumber = $event.pageIndex;
    this.pageSize = $event.pageSize;
    this.getAll();
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
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

  reload() {
    this.searhform.reset();
    this.getAll();
  }
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
  checkboxLabel(row ? : SelectionElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.itemCode + 1}`;
  }


  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
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

  onEditClick() {
    sessionStorage.setItem('masterProduct', JSON.stringify(this.selection.selected[0]));
    const dialogRef = this.dialog.open(SelectionPopupComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: {
        top: '9%',
      },
      data: {
        type: 'edit'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    console.log(url);
    if (!url) {
      let menu =
        JSON.parse("[" + sessionStorage.getItem('menu') + "]");
      url = this.ms.getMeuList('save').filter(x => x.id == Number(id))[0].children ?.filter(x => menu.includes(x.id))[0].url;
    }
    this.router.navigate([url]);
  }

  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }
  }

}
