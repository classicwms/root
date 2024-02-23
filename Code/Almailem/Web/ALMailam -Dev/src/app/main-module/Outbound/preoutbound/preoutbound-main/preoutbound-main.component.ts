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
  MatPaginator
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
  IDropdownSettings
} from "ng-multiselect-dropdown";
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
  AuthService
} from "src/app/core/core";
import {
  PreoutboundCreateComponent
} from "../preoutbound-create/preoutbound-create.component";
import {
  PreoutboundNewComponent
} from "../preoutbound-new/preoutbound-new.component";
import {
  PreoutboundService
} from "../preoutbound.service";
import {
  Table
} from "primeng/table";
import { RequestpopupComponent } from "src/app/common-field/requestpopup/requestpopup.component";

export interface preoutbound {


  no: string;
  actions: string;
  status: string;
  order: string;
  orderedlines: string;
  date: string;
  outboundno: string;
  refno: string;
  required: string;
  scode: string;
  sname: string;
}
const ELEMENT_DATA: preoutbound[] = [{
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },
  {
    no: "Value",
    order: 'Value',
    refno: 'Value',
    outboundno: 'Value',
    orderedlines: 'Value',
    scode: 'Value',
    sname: 'Value',
    date: 'date',
    required: 'date',
    status: 'date',
    actions: 's'
  },

];

interface soTypeList {
  value: string,
    label: string
}

@Component({
  selector: 'app-preoutbound-main',
  templateUrl: './preoutbound-main.component.html',
  styleUrls: ['./preoutbound-main.component.scss']
})
export class PreoutboundMainComponent implements OnInit {
  screenid = 3059;
  preoutboundMain: any[] = [];
  selectedpreoutboundManin: any[] = [];
  @ViewChild('preoutboundMainTag') preoutboundMainTag: Table | any;

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


  soTypeList1: soTypeList[];

  constructor(private service: PreoutboundService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService, ) {
    this.soTypeList1 = [{
        value: 'N',
        label: 'N'
      },
      {
        value: 'S',
        label: 'S'
      },
    ];
  }
  sub = new Subscription();
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.getstorename()
    this.search(true);

  }


  warehouseId = this.auth.warehouseId

  searhform = this.fb.group({
    createdBy: [],
    endCreatedOn: [],
    endOrderDate: [],
    endRequiredDeliveryDate: [],
    outboundOrderTypeId: [],
    partnerCode: [],
    preOutboundNo: [],
    soNumber: [],
    soType: [, ],
    companyCodeId: [
      [this.auth.companyId]
    ],
    languageId: [
      [this.auth.languageId]
    ],
    plantId: [
      [this.auth.plantId]
    ],
    startCreatedOn: [],
    startOrderDate: [],
    startRequiredDeliveryDate: [],
    statusId: [
      [39, 47]
    ],
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

  outboundOrderTypeIdListselected: any[] = [];
  outboundOrderTypeIdList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];

  preOutboundNoselected: any[] = [];
  preOutboundNoList: any[] = [];

  refDocNumberListselected: any[] = [];
  refDocNumberList: any[] = [];


  statusIdListselected: any[] = [];
  statusIdList: any[] = [];

  getstorename() {
    this.sub.add(this.service.GetStoreCode().subscribe(res => {
        this.storecodeList = res;

      },
      err => {
        this.cs.commonerrorNew(err);;
      }));
  }


  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endRequiredDeliveryDate.value));
      this.searhform.controls.startRequiredDeliveryDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startRequiredDeliveryDate.value));
      this.searhform.controls.endOrderDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endOrderDate.value));
      this.searhform.controls.startOrderDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startOrderDate.value));



    }
    this.spin.show();
    this.service.searchSpark(this.searhform.value).subscribe(res => {
      //  let result = res.filter((x: any) => x.statusId == 39);
      let result = res



      if (ispageload) {

        let tempoutboundOrderTypeIdList: any[] = []
       // const outboundOrderTypeId = [...new Set(result.map(item => item.outboundOrderTypeId))].filter(x => x != null)
        res.forEach(x => tempoutboundOrderTypeIdList.push({
          value: x.outboundOrderTypeId,
          label:x.referenceDocumentType,
        }));
        this.outboundOrderTypeIdList = tempoutboundOrderTypeIdList;
        this.outboundOrderTypeIdList=this.cs.removeDuplicatesFromArrayNew(this.outboundOrderTypeIdList);
        let temppartnerCodeList: any[] = []
        const partnerCode = [...new Set(result.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temppartnerCodeList.push({
          value: x,
          label: x
        }));
        this.partnerCodeList = temppartnerCodeList;

        let temppreOutboundNoList: any[] = []
        const preOutboundNo = [...new Set(result.map(item => item.preOutboundNo))].filter(x => x != null)
        preOutboundNo.forEach(x => temppreOutboundNoList.push({
          value: x,
          label: x
        }));
        this.preOutboundNoList = temppreOutboundNoList;

        //  let temprefDocNumberList: any[] = []
        //  const refDocNumber = [...new Set(result.map(item => item.refDocNumber))].filter(x => x != null)
        //  refDocNumber.forEach(x => temprefDocNumberList.push({ id: x, itemName: x }));
        //  this.refDocNumberList = temprefDocNumberList;


        let tempstatusIdList: any[] = []
        // const statusId = [...new Set(result.map(item => item.statusId))].filter(x => x != null)
        res.forEach(x => tempstatusIdList.push({
          value: x.statusId,
          label: x.statusDescription
        }));
        this.statusIdList = tempstatusIdList;
        this.statusIdList = this.cs.removeDuplicatesFromArrayNewstatus(this.statusIdList);


      }
      this.preoutboundMain = result;

      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);;
      this.spin.hide();

    });


  }
  reload() {
    this.searhform.reset();
    this.searhform.controls.companyCodeId.patchValue([this.auth.companyId]);
    this.searhform.controls.plantId.patchValue([this.auth.plantId]);
    this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId]);
    this.searhform.controls.languageId.patchValue([this.auth.languageId]);
    this.searhform.controls.statusId.patchValue([39,47])

  }
  openDialogView(data) {
    let obj: any = {};
    obj.languageId=[this.auth.languageId];
    obj.companyCodeId=[this.auth.companyId];
    obj.plantId=[this.auth.plantId];
    obj.warehouseId=[this.auth.warehouseId];
    obj.refDocumentNo=[data.refDocNumber];
    this.spin.show();
    this.sub.add(this.service.searchOutboundHeader(obj).subscribe((res: any[]) => {
      const lineReferences = res[0]?.line?.map(line => line.lineReference) || []; 
      const maxLineRef = lineReferences.length; 
      console.log(maxLineRef);
    const dialogRef2 = this.dialog.open(RequestpopupComponent, {
      width: '45%',
      maxWidth: '45%',
      position: {
        top: '1%',
      },
      data: { title: "Order Details", body: "Order Details", value: res,lines:maxLineRef,type:'Outbound'},
    });
    
    dialogRef2.afterClosed().subscribe(result => {
      // Handle the result if needed
    });
  }))
   this.spin.hide();
  }

  openDialog(data: any = 'new', type ? : any): void {
    if (type && type != undefined) {
      this.selectedpreoutboundManin = [];
      this.selectedpreoutboundManin.push(type);
    }
    if (data != 'New')
      if (this.selectedpreoutboundManin.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";
    if (this.selectedpreoutboundManin.length > 0) {
      paramdata = this.cs.encrypt({
        code: this.selectedpreoutboundManin[0],
        pageflow: data
      });
      this.router.navigate(['/main/outbound/preoutbound-create/' + paramdata]);
    } else {
      paramdata = this.cs.encrypt({
        pageflow: data
      });
      this.router.navigate(['/main/outbound/preoutbound-create/' + paramdata]);
    }
  }

  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.preoutboundMain.forEach(x => {
      res.push({
        "Branch": x.plantDescription,
        "Warehouse": x.warehouseDescription,
        "Target Branch":x.targetBranchCode,
        "Token Number  ": x.tokenNumber,
        "Sales Order No": x.salesOrderNumber,
        "Status  ": x.statusDescription,
        "Order No": x.refDocNumber,
        'Order Type': x.referenceDocumentType,
        "Preoutbound No": x.preOutboundNo,
        "Order Category": x.referenceField1,
        "Ordered Date": this.cs.dateapiwithTime(x.refDocDate),
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Preoutbound");
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

  preOutboundUpload() {
    //   const dialogRef = this.dialog.open(PreoutboundNewComponent, {
    //     disableClose: true,
    //     width: '80%',
    //     maxWidth: '80%',
    //     position: { top: '9%', },
    //   });

    //   dialogRef.afterClosed().subscribe(result => {
    // })
    this.router.navigate(['/main/outbound/preboutboundUpload'])

  }
  onChange() {
    const choosen = this.selectedpreoutboundManin[this.selectedpreoutboundManin.length - 1];
    this.selectedpreoutboundManin.length = 0;
    this.selectedpreoutboundManin.push(choosen);
  }
}
