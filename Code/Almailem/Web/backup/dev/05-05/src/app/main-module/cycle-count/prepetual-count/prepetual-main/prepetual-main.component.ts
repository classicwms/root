import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PrepetualCountService } from "../prepetual-count.service";
import { CreatePopupComponent } from "./create-popup/create-popup.component";

// export interface  variant {


//   no: string;
//   actions:  string;
//   status:  string;
//   warehouseno:  string;
//   preinboundno:  string;
//   refdocno:  string;
//   type:  string;
// }
// const ELEMENT_DATA:  variant[] = [
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },

// ];


// export interface PerpetualCount {
//   cycleCountNo?: string;
//   movementTypeId?: number;
//   subMovementTypeId?: number;
//   statusId?: string;
//   warehouseId?: string;
//   createdBy?: string;
//   createdOn?: Date;
// }

@Component({
  selector: 'app-prepetual-main',
  templateUrl: './prepetual-main.component.html',
  styleUrls: ['./prepetual-main.component.scss']
})
export class PrepetualMainComponent implements OnInit {
  screenid: 1071 | undefined;
  constructor(public cs: CommonService, public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, private route: ActivatedRoute, private auth: AuthService,
    private spinner: NgxSpinnerService, private prepetualCountService: PrepetualCountService,) {

    // route.params.subscribe(val => {
    //   debugger
    //   this.ngOnInit();
    // });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  create(): void {

    const dialogRef = this.dialog.open(CreatePopupComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },

    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  pageflow: any;


  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.pageflow = code == 'count' ? 'Perpetual Creation' : 'Variance Analysis';
    /** spinner starts on init */
    this.spinner.show();
    if (this.pageflow == 'Variance Analysis') {
      this.displayedColumns = ['select', 'cycleCountNo', 'type', 'refdocno', 'createdOn', 'no', 'status',];
      this.getPerpetualCountList();
    }
    else {
      this.displayedColumns = ['select', 'count', 'cycleCountNo', 'type', 'refdocno', 'createdOn', 'no', 'status',];
      this.getPerpetualCountList();
    }

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 500);
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

  displayedColumns: string[] = ['select', 'count', 'warehouseno', 'type', 'refdocno', 'preinboundno', 'no', 'status',];
  dataSource = new MatTableDataSource<any>();
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
  // checkboxLabel(row?:  variant): string {
  //   if (!row) {
  //     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
  //   }
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  // }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }
  openDialog(data: any = 'New', linedata: any = null): void {
    if (data != 'New' && linedata == null) {
      if (this.selection.selected.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }


    // if (this.selection.selected[0].statusId === 24) {
    //   this.toastr.error("Order can't be proccessed as it's already confirmed.", "", {
    //     timeOut: 2000,
    //     progressBar: false,
    //   });
    //   return;
    // }
    let paramdata = "";

    if (data != 'New') {
      let paramdata = this.cs.encrypt({ code: linedata == null ? this.selection.selected[0] : linedata, pageflow: this.pageflow = data });
      this.router.navigate(['/main/cycle-count/Prepetual-confirm/' + paramdata]);
    }
    else {
      let paramdata = this.cs.encrypt({ code: linedata == null ? this.selection.selected[0] : linedata, pageflow: this.pageflow == 'Variance Analysis' ? 'Variance Analysis' : data });
      this.router.navigate(['/main/cycle-count/Prepetual-confirm/' + paramdata]);
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

  getPerpetualCountList() {
    this.spin.show();
    this.dataSource.data = [];
    let data: any = { warehouseId: [this.auth.warehouseId] }
    if (this.pageflow == 'Variance Analysis') {
      data.headerStatusId = [73, 74, 78];
    }
    this.prepetualCountService.getPerpetualCountList(data).subscribe(
      result => {
        this.spin.hide();
        this.dataSource = new MatTableDataSource(result);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error => {
        this.spin.hide();
        if (error.status == 415) {
          this.getPerpetualCountList();
        }
      }
    );
  }
  navegaeter(paramdata: any) {
    this.router.navigate(['/main/cycle-count/Prepetual-main/' + paramdata]);
    this.tabChange(paramdata);
  }

  tabChange(paramdata){
    let code = paramdata
    console.log(code)
    this.pageflow = code == 'count' ? 'Perpetual Creation' : 'Variance Analysis';
    /** spinner starts on init */
    this.spinner.show();
    if (this.pageflow == 'Variance Analysis') {
      this.displayedColumns = ['select', 'cycleCountNo', 'type', 'refdocno', 'createdOn', 'no', 'status',];
      this.getPerpetualCountList();
    }
    else {
      this.displayedColumns = ['select', 'count', 'cycleCountNo', 'type', 'refdocno', 'createdOn', 'no', 'status',];
      this.getPerpetualCountList();
    }
  }
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
}
