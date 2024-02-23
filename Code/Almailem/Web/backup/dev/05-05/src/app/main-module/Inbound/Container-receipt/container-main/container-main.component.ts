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
import { DeleteComponent } from "src/app/common-field/delete/delete.component";
import { CommonService, dropdownelement1 } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { ContainerReceiptService } from "../container-receipt.service";




@Component({
  selector: 'app-container-main',
  templateUrl: './container-main.component.html',
  styleUrls: ['./container-main.component.scss']
})
export class ContainerMainComponent implements OnInit {

  constructor(private service: ContainerReceiptService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();

  ngOnInit(): void {
    this.search(true);

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


  displayedColumns: string[] = ['select', 'statusId',  'containerNo', 'containerReceiptNo', 'refDocNumber', 'partnerCode', 'createdBy', 'referenceField2','referenceField5'];
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
        "Status": this.cs.getstatus_text(x.statusId),
        'Container No': x.containerNo,
        "Receipt No ": x.containerReceiptNo,
        "Order No ": x.refDocNumber,
        'Supplier Name': x.partnerCode,
        "Created By  ": x.createdBy,
        " Container Unloaded On" : this.cs.dateExcel(x.referenceField2),
        'Actual Receipt Date': this.cs.dateExcel(x.referenceField5),
        // 'Created By': x.createdBy,
        // 'Created On': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Conatiner Receipt");
  }

  deleteDialog() {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select any row", "Norification" ,{
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
    this.sub.add(this.service.Delete(id.containerReceiptNo).subscribe((res) => {
      this.toastr.success(id.containerReceiptNo + " Deleted successfully.");

      this.spin.hide(); //this.getAllListData();
      window.location.reload();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  openDialog(data: any = 'new'): void {
    if (data != 'New')
      if (this.selection.selected.length === 0) {
        this.toastr.error("Kindly select any row", "Notification" ,{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";

    if (this.selection.selected.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selection.selected[0], pageflow: data });
      this.router.navigate(['/main/inbound/container-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/container-create/' + paramdata]);
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
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId

  searhform = this.fb.group({

    containerNo: [],
    containerReceiptNo: [],
    endContainerReceivedDate: [],

    partnerCode: [],
    referenceField1: [],
    startContainerReceivedDate: [],
    statusId: [],
    warehouseId: [[this.auth.warehouseId]],

  });




  
  dropdownSettings = {
    singleSelection: false, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };
  containerNoListselected: any[] = [];
  containerNoList: any[] = [];

  containerReceiptNoListselected: any[] = [];
  containerReceiptNoList: any[] = [];

  partnerCodeListselected: any[] = [];
  partnerCodeList: any[] = [];

  statusIdListselected: any[] = [];
  statusIdList: any[] = [];
  // search(ispageload = false) {
  //   //dateconvertion
  //   this.searhform.controls.endContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endContainerReceivedDate.value));
  //   this.searhform.controls.startContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startContainerReceivedDate.value));




  //   //patching
  //   const containerNo = [...new Set(this.containerNoListselected.map(item => item.id))].filter(x => x != null);
  //   this.searhform.controls.containerNo.patchValue(containerNo);

  //   const containerReceiptNo = [...new Set(this.containerReceiptNoListselected.map(item => item.id))].filter(x => x != null);
  //   this.searhform.controls.containerReceiptNo.patchValue(containerReceiptNo);

  //   const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.id))].filter(x => x != null);

  //   this.searhform.controls.partnerCode.patchValue(partnerCode);




  //   const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
  //   this.searhform.controls.statusId.patchValue(statusId);


  //   this.service.search(this.searhform.value).subscribe(res => {
  //     // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
  //     this.spin.hide();
  //     if (ispageload) {
  //       let tempContainerList: dropdownelement1[] = []
  //       const containerNo = [...new Set(res.map(item => item.containerNo))].filter(x => x != null)
  //       containerNo.forEach(x => tempContainerList.push({ id: x, itemName: x }));
  //       this.containerNoList = tempContainerList;

  //       let tempcontainerReceiptNoList: dropdownelement1[] = []
  //       const containerReceiptNo = [...new Set(res.map(item => item.containerReceiptNo))].filter(x => x != null)
  //       containerReceiptNo.forEach(x => tempcontainerReceiptNoList.push({ id: x, itemName: x }));
  //       this.containerReceiptNoList = tempcontainerReceiptNoList;

  //       let temppartnerCodeList: dropdownelement1[] = []
  //       const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
  //       partnerCode.forEach(x => temppartnerCodeList.push({ id: x, itemName: x }));
  //       this.partnerCodeList = temppartnerCodeList;

  //       let tempstatusIdList: dropdownelement1[] = []
  //       const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
  //       statusId.forEach(x => tempstatusIdList.push({ id: x, itemName: this.cs.getstatus_text(x) }));
  //       this.statusIdList = tempstatusIdList;




  //     }
  //     this.dataSource = new MatTableDataSource<any>(res);
  //     this.selection = new SelectionModel<any>(true, []);
  //     this.dataSource.sort = this.sort;
  //     this.dataSource.paginator = this.paginator;


  //     if (ispageload) {
  //       let tempa: any[] = []
  //       tempa.push({ id: 10, itemName: this.cs.getstatus_text(10) });



  //       this.statusIdListselected = tempa;


  //       let tempa2: any[] = []
  //       tempa2.push({ id: 'CNT2000', itemName: 'CNT2000' });
  //       this.containerNoListselected = tempa2;

  //       this.searhform.controls.endContainerReceivedDate.patchValue(new Date());
  //       this.searhform.controls.startContainerReceivedDate.patchValue(new Date());

  //       this.searhform.controls.statusId.patchValue([10]);


  //       this.search();
  //     }
  //   }, err => {

  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();

  //   });

  //   // this.searhform.controls.startCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startCreatedOn.value));
  //   // this.searhform.controls.endCreatedOn.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endCreatedOn.value));

  //   // this.spin.show();
  //   // this.cas.getalldropdownlist([this.cas.dropdownlist.setup.classId.url,
  //   // this.cas.dropdownlist.setup.statusId.url,
  //   // this.cas.dropdownlist.client.clientId.url,

  //   // ]).subscribe((results) => {
  //   //   this.classIdList = this.cas.foreachlist_searchpage(results[0], this.cas.dropdownlist.setup.classId.key);
  //   //   this.statuslist = this.cas.foreachlist_searchpage(results[1], this.cas.dropdownlist.setup.statusId.key);
  //   //   this.clientlist = this.cas.foreachlist_searchpage(results[2], this.cas.dropdownlist.client.clientId.key);
  //   //   this.spin.hide();

  //   //   this.spin.show();
  //   //   this.sub.add(this.searhform.getRawValue().subscribe((res: any[]) => {
  //   //     if (this.auth.classId != '3')
  //   //       this.ELEMENT_DATA = res.filter(x => x.classId === Number(this.auth.classId));
  //   //     else
  //   //       this.ELEMENT_DATA = res;
  //   //     this.ELEMENT_DATA.forEach((x) => {
  //   //       x.classId = this.classIdList.find(y => y.key == x.classId)?.value;
  //   //       x.statusIddes = this.statuslist.find(y => y.key == x.statusId)?.value;
  //   //       x.referenceField1 = this.clientlist.find(y => y.key == x.clientId)?.value;
  //   //     })


  //   //     this.dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  //   //     this.selection = new SelectionModel<any>(true, []);
  //   //     this.dataSource.sort = this.sort;
  //   //     this.dataSource.paginator = this.paginator;
  //   //     this.spin.hide();
  //   //   }, (err: any) => {
  //   //     this.cs.commonerrorNew(err);
  //   //     this.spin.hide();
  //   //   }));
  //   // }, (err) => {
  //   //   this.toastr.error(err, "");
  //   // });
  //   // this.spin.hide();
  // }


  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endContainerReceivedDate.value));
      this.searhform.controls.startContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startContainerReceivedDate.value));


      //patching
    //  const containerNo = [...new Set(this.containerNoListselected.map(item => item.id))].filter(x => x != null);
     // this.searhform.controls.containerNo.patchValue(this.searhform.controls.containerNo.value);

      // const containerReceiptNo = [...new Set(this.containerReceiptNoListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.containerReceiptNo.patchValue(containerReceiptNo);

      // const partnerCode = [...new Set(this.partnerCodeListselected.map(item => item.id))].filter(x => x != null);

      // this.searhform.controls.partnerCode.patchValue(partnerCode);




      // const statusId = [...new Set(this.statusIdListselected.map(item => item.id))].filter(x => x != null);
      // this.searhform.controls.statusId.patchValue(statusId);


    }
    this.spin.show();
    this.service.search(this.searhform.value).subscribe(res => {
      // let result = res.filter((x: any) => x.warehouseId == this.warehouseId);
      this.spin.hide();
      if (ispageload) {
        let tempContainerList: any[] = []
        const containerNo = [...new Set(res.map(item => item.containerNo))].filter(x => x != null)
        containerNo.forEach(x => tempContainerList.push({ value: x, label: x }));
        this.containerNoList = tempContainerList;

        let tempcontainerReceiptNoList: any[] = []
        const containerReceiptNo = [...new Set(res.map(item => item.containerReceiptNo))].filter(x => x != null)
        containerReceiptNo.forEach(x => tempcontainerReceiptNoList.push({ value: x, label: x }));
        this.containerReceiptNoList = tempcontainerReceiptNoList;

        let temppartnerCodeList: any[] = []
        const partnerCode = [...new Set(res.map(item => item.partnerCode))].filter(x => x != null)
        partnerCode.forEach(x => temppartnerCodeList.push({ value: x, label: x }));
        this.partnerCodeList = temppartnerCodeList;

        let tempstatusIdList: any[] = []
        const statusId = [...new Set(res.map(item => item.statusId))].filter(x => x != null)
        statusId.forEach(x => tempstatusIdList.push({ value: x, label: this.cs.getstatus_text(x) }));
        this.statusIdList = tempstatusIdList;
      }
      res.forEach(x => {
        if(x.referenceField5 != null){
          x.statusId = 24;
        }
        if(x.refDocNumber && x.referenceField5 == null){
          x.statusId = 11;
        }
      })
      this.dataSource = new MatTableDataSource<any>(res);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    });}


    reload(){
      this.searhform.reset();
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
}
