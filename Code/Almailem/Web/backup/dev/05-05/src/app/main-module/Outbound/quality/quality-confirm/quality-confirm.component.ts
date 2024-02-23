import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { Location } from "@angular/common";
import { AssignHEComponent } from "src/app/main-module/Inbound/Item receipt/item-create/assign-he/assign-he.component";
import { QualityService } from "../quality.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";

export interface clientcategory {

  lineno: string;
  supcode: string;
  one: string;
  two: string;
  three: string;
  accepted: string;

}
const ELEMENT_DATA: clientcategory[] = [
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },
  { lineno: 'dropdown', supcode: 'dropdown', one: 'AP-Noneditable', two: 'AP-Editable', accepted: 'AP-Noneditable', three: 'AP-Editable', },

];
@Component({
  selector: 'app-quality-confirm',
  templateUrl: './quality-confirm.component.html',
  styleUrls: ['./quality-confirm.component.scss']
})
export class QualityConfirmComponent implements OnInit {
  screenid: 1066 | undefined;
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

  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;
  assignhe(): void {

    // const dialogRef = this.dialog.open(AssignHEComponent, {
    //   disableClose: true,
    //   width: '100%',
    //   maxWidth: '40%',
    //   position: { top: '9%', },
    // });

    // dialogRef.afterClosed().subscribe(result => {
    // if (result) {
    let data: any[] = [];
  // start CWMS/IW/2022/018 mugilan 03-10-2022
    this.dataSource.data.forEach((x: any) => {
      data.push({
        "actualHeNo": x.actualHeNo,
        "companyCodeId": x.companyCodeId,
        "description": x.description,
        "itemCode": x.itemCode,
        "languageId": x.languageId,
        "lineNumber": x.lineNumber,
        "outboundOrderTypeId": this.code.outboundOrderTypeId,
        "partnerCode": x.partnerCode,
        "plantId": this.code.plantId,
        "pickConfirmQty": parseInt(x.qualityQty),
        "preOutboundNo": this.code.preOutboundNo,
        "qualityInspectionNo": this.code.qualityInspectionNo,
        "pickPackBarCode": x.referenceField2,
        "qualityQty": parseInt(x.qualityQty),
        "qualityConfirmUom": x.pickUom,
        "refDocNumber": this.code.refDocNumber,
        "stockTypeId": this.code.stockTypeId,
        "warehouseId": this.code.warehouseId,
      });
    });
      // end CWMS/IW/2022/018 mugilan 03-10-2022
    this.spin.show();
    this.sub.add(this.service.confirm(data).subscribe(res => {

      this.toastr.success(this.code.refDocNumber + ' confirmed Successfully', "", {
        timeOut: 2000,
        progressBar: false,
      })

      this.spin.hide();
      this.location.back()
      // this.getclient_class(this.form.controls.classId.value);
    }, err => {
      this.cs.commonerror(err);
      this.spin.hide();
    }));

    //   }
    // });
  }



  constructor(private fb: FormBuilder,
    private auth: AuthService,
    private service: QualityService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService, private location: Location,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService, public dialog: MatDialog,) { }
  sub = new Subscription();
  code: any;
  ngOnInit(): void {

    // this.auth.isuserdata();

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
console.log(js)

      this.code = js.code;
      this.fill(js);
    }

  }
  pageflow = 'New';
  isbtntext: boolean = true;
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  result1: any[] = [];
  fill(data: any) {
    if (data.pageflow != 'New') {
      this.pageflow = data.pageflow;

      if (data.pageflow == 'Display') {

        this.isbtntext = false;
        this.spin.show();
        this.sub.add(this.service.getqualityline({ qualityinspectionNo: [data.code.qualityInspectionNo] }).subscribe(res => {
console.log(res)
          let data: any[] = [];
          // res.forEach((x: any) => {
          //   if (x.pickConfirmQty != null && x.pickConfirmQty > 0) {
          //     data.push(x);
          //   }
          // });
          this.dataSource = new MatTableDataSource<any>(res);    
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;

          this.spin.hide();
          // this.getclient_class(this.form.controls.classId.value);
        }, err => {
          this.cs.commonerror(err);
          this.spin.hide();
        }));
      }
// start of  CWMS/IW/2022/018 mugilan 02-10-2022
      else {
        this.spin.show();
        this.sub.add(this.service.search({ qualityInspectionNo: [data.code.qualityInspectionNo] , statusId: [54]}).subscribe(res => {

          this.sub.add(this.service.getpickingline({ pickupNumber: [res[0].pickupNumber], warehouseId: [this.auth.warehouseId]}).subscribe(result => {
          console.log(res)
          console.log(result)
// res.forEach(element => {
//   if(element.pickConfirmQty > 0){
//     this.result1.push(element)
//   }
// });
         // let result = this.result1.filter((x: any) => x.statusId == 50);
          res.forEach((x: any) => {
            x.qualityQty = x.qcToQty;
            x.rejectQty = 0;
           x.itemCode = result[0].itemCode;
           x['itemDescription'] = result[0].description;
           x.lineNumber = result[0].lineNumber;

          });
          this.dataSource = new MatTableDataSource<any>(res);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
          this.spin.hide();
          // this.getclient_class(this.form.controls.classId.value);

        }, err => {
          this.cs.commonerror(err);
          this.spin.hide();
        }));

        }, err => {
          this.cs.commonerror(err);
          this.spin.hide();
        }));
      }

      // end of  CWMS/IW/2022/018 mugilan 02-10-2022
    }
  }


  displayedColumns: string[] = [ 'itemCode','pickedStorageBin','pickedPackCode','stockTypeId', 'qualityQty', 'rejectQty',];
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
  checkboxLabel(row?: clientcategory): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.lineno + 1}`;
  }

  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }


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

  onKey(event: any, element: any) { // without type info

    if (element.pickConfirmQty < element.qualityQty) {
      element.qualityQty = '';
      this.toastr.error("To Qty is greater than picking Qty.", "", {
        timeOut: 2000,
        progressBar: false,
      });
    }
    element.rejectQty = element.pickConfirmQty - element.qualityQty;
  }

}

