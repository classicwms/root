import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { VerticalNewComponent } from "../../other-setup/vertical/vertical-new/vertical-new.component";
import { UserprofileNewComponent } from "./userprofile-new/userprofile-new.component";
import { UserprofileService } from "./userprofile.service";


export interface  userprofile {


  no: string;
  actions:  string;
  status:  string;
   order:  string;
  orderedlines:  string;
  date:  string;
  outboundno:  string;
   refno:  string;
   required:  string;
   scode:  string;
   sname:  string;
}
const ELEMENT_DATA:  userprofile[] = [
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },
  { no: "Value",  order:  'Value', refno:  'Value',outboundno:  'Value',orderedlines:  'Value',scode:  'Value',sname:  'Value', date: 'date',required: 'date', status: 'date' ,actions: 's' },

];
@Component({
  selector: 'app-userprofile',
  templateUrl: './userprofile.component.html',
  styleUrls: ['./userprofile.component.scss']
})
export class UserprofileComponent implements OnInit {

  displayedColumns: string[] = ['select', 'companyCode','createdBy','createdOn','currencyDecimal','dateFormatId', 'emailId','firstName', 'languageId', 'lastName','plantId','statusId','timeZone','userId','userName','userRoleId','userTypeId','warehouseId','password'];
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
    private auth: AuthService,
    private service: UserprofileService,) { }
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
  if (this.selection.selected.length === 0) {
    this.toastr.warning("Kindly select any Row", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(UserprofileNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
    data: { pageflow: data, code: data != 'New' ? this.selection.selected[0].userId : null}
  });

  dialogRef.afterClosed().subscribe(result => {
    this.getAll();
  });
}

getAll() {
  this.spin.show();
  this.sub.add(this.service.Getall().subscribe((res: any[]) => {
    console.log(res)
    this.dataSource = new MatTableDataSource<any>(res);
    this.selection = new SelectionModel<any>(true, []);
    this.dataSource.sort = this.sort;
   this.dataSource.paginator = this.paginator;
    this.spin.hide();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
deleteDialog() {
  if (this.selection.selected.length === 0) {
    this.toastr.error("Kindly select any row", "Notification",{
      timeOut: 2000,
      progressBar: false,
    });
    return;
  }
  const dialogRef = this.dialog.open(DeleteComponent, {
    disableClose: true,
    width: '40%',
    maxWidth: '80%',
    position: { top: '9%', },
    data: this.selection.selected[0].userId,
  });

  dialogRef.afterClosed().subscribe(result => {

    if (result) {
      this.deleterecord(this.selection.selected[0].userId);

    }
  });
}


deleterecord(id: any) {
  this.spin.show();
  this.sub.add(this.service.Delete(id, this.auth.warehouseId).subscribe((res) => {
    this.toastr.success(id + " Deleted successfully.","Notification",{
      timeOut: 2000,
      progressBar: false,
    });
    this.spin.hide();
    this.getAll();
  }, err => {
    this.cs.commonerrorNew(err);
    this.spin.hide();
  }));
}
downloadexcel() {
  var res: any = [];
  this.dataSource.data.forEach(x => {
    res.push({
      "Company Code": x.companyCode,
      "Created By ": x.createdBy,
      "Created On	": x.createdOn,
      'Currency': x.currencyDecimal,
      'DateFormat ID': x.dateFormatId,
      'Email': x.emailId,
      "FirstName": x.firstName,
      "LanguageID": x.languageId,
      "LastName	": x.lastName,
      "PlantID": x.plantId,
      "StatusID": x.statusId,
      "TimeZone": x.timeZone,
      "UserID": x.userId,
      "UserName": x.userName,
      "UserRoleID": x.userRoleId,
      "UserTypeID": x.userTypeId,
      "WareHouseID": x.warehouseId,
    });

  })
  this.cs.exportAsExcel(res, "Userprofile");
}
}
