import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { ConsignmentsOpenComponent } from '../consignments/consignments-open/consignments-open.component';
import { DeliveryService } from '../delivery.service';
import { HttpErrorResponse } from "@angular/common/http";
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-delivered-completed',
  templateUrl: './delivered-completed.component.html',
  styleUrls: ['./delivered-completed.component.scss']
})
export class DeliveredCompletedComponent implements OnInit {

  screenid = 3216;
  advanceFilterShow: boolean;
  @ViewChild('Delivery') Delivery: Table | undefined;
  deliverys: any;
  selecteddelivery: any;
  displayedColumns: string[] = ['select', 'companyCodeId', 'description', 'plantId', 'createdBy', 'createdOn',];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
    // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    // private excel: ExcelService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private auth: AuthService,
    private router: Router,
    private service: DeliveryService,) { }
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
  showFiller = false;
  animal: string | undefined;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  applyFilterGlobal($event: any, stringVal: any) {
    this.Delivery!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  ngOnInit(): void {
    this.getAll();
  }
  ELEMENT_DATA: any[] = [];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);




  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId;






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
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.handlingUnit + 1}`;
  }






  clearselection(row: any) {

    this.selection.clear();
    this.selection.toggle(row);
  }

  openDialog(data: any = 'New'): void {
    if (data != 'New')
      if (this.selecteddelivery.length === 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    const dialogRef = this.dialog.open(ConsignmentsOpenComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      data: { pageflow: data, code: data != 'New' ? this.selecteddelivery[0].plantId : null }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getAll();
    });
  }
  getAll() {
    this.spin.show();
    let obj: any = {};
   // obj.companyCodeId = [this.auth.companyId];
     obj.languageId = [this.auth.languageId];
   // obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.statusId = [92];

    this.sub.add(this.service.searchDeliveryLine(obj).subscribe((res: any[]) => {
      if (res) {
        let result = res.filter(item => item.salesdeliveryNumber !== null);
        this.deliverys = this.cs.removeDuplicatesFromArrayList(result, 'refDocNumber');
      } this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }


  // deleteDialog() {
  // if (this.selecteddelivery.length === 0) {
  //   this.toastr.error("Kindly select any row", "Notification",{
  //     timeOut: 2000,
  //     progressBar: false,
  //   });
  //   return;
  // }
  // const dialogRef = this.dialog.open(DeleteComponent, {
  //   disableClose: true,
  //   width: '40%',
  //   maxWidth: '80%',
  //   position: { top: '9%', },
  //   data: this.selecteddelivery[0].plantId,
  // });

  // dialogRef.afterClosed().subscribe(result => {

  //   if (result) {
  //     this.deleterecord(this.selecteddelivery[0].plantId);

  //   }
  // });
  // }


  // deleterecord(id: any) {
  // this.spin.show();
  // this.sub.add(this.service.Delete(id,this.auth.company,this.auth.languageId).subscribe((res) => {
  //   this.toastr.success(id + " Deleted successfully.","Notification",{
  //     timeOut: 2000,
  //     progressBar: false,
  //   });
  //   this.spin.hide();
  //   this.getAll();
  // }, err => {
  //   this.cs.commonerrorNew(err);
  //   this.spin.hide();
  // }));
  // }


  downloadexcel() {
    var res: any = [];
    this.deliverys.forEach(x => {
      res.push({
        "Company":x.companyDescription,
        "Branch":x.plantDescription,
        "Warehouse ": x.warehouseDescription,
        'Delivery No': x.deliveryNo,
        'Route Id': x.routeId,
        "Driver": x.driverName,
        "Vechile": x.vechicleNo,
        "Status":x.statusDescription,
        "Created On":this.cs.dateExcel(x.createdOn),
  
        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });
    
    })
    this.cs.exportAsExcel(res, "Delivery");
  }
  
  confirm(linedata: any = null): void {
    let paramdata = this.cs.encrypt({ code: linedata == null ? this.selecteddelivery[0] : linedata });
    this.router.navigate(['/main/delivery/deliveryCompoleted/' + paramdata]);
  }


  fileUrldownload: any;
  docurl: any;
  async download(element) {
    this.spin.show()
    const blob = await this.service.download(element.referenceField1, 'document/'+ element.refDocNumber)
      .catch((err: HttpErrorResponse) => {
        this.cs.commonerror(err);
      });
    this.spin.hide();
    if (blob) {
      const blobOb = new Blob([blob], {
        type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      });
      this.fileUrldownload = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blobOb));
      // http://remote.url.tld/path/to/document.doc&embedded=true
      this.docurl = window.URL.createObjectURL(blob);
      const a = document.createElement('a')
      a.href = this.docurl
      a.download = element.referenceField1;
      a.click();
      URL.revokeObjectURL(this.docurl);

    }
    this.spin.hide();
  }

}





