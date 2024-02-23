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
import { Table } from "primeng/table";




@Component({
  selector: 'app-container-main',
  templateUrl: './container-main.component.html',
  styleUrls: ['./container-main.component.scss']
})
export class ContainerMainComponent implements OnInit {
screenid=3042;
  constructor(private service: ContainerReceiptService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();


  containerReceipt: any[] = [];
  selectedContainer : any[] = [];
  @ViewChild('containerReceiptTag') containerReceiptTag: Table | any;
  RA: any = {};
  ngOnInit(): void {
    this.RA = this.auth.getRoleAccess(this.screenid);
    this.search(true);

  }

  onChange() {
    const choosen= this.selectedContainer[this.selectedContainer.length - 1];   
    this.selectedContainer.length = 0;
    this.selectedContainer.push(choosen);
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


  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.containerReceipt.forEach(x => {
      res.push({
       
        "Branch":x.plantDescription,
        "Warehouse":x.warehouseDescription,
        "Status": x.statusDescription,
        'Container No': x.containerNo,
        "Receipt No ": x.containerReceiptNo,
        "Order No ": x.refDocNumber,
        'Supplier Name': x.partnerCode,
        "Created By  ": x.createdBy,
        " Container Unloaded On" :this.cs.dateapiwithTime(x.referenceField2),
        'Actual Receipt Date':this.cs.dateapiwithTime(x.referenceField5),
      });

    })
    this.cs.exportAsExcel(res, "Conatiner Receipt");
  }


  deleteDialog() {
    if (this.selectedContainer.length === 0) {
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
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.deleterecord(this.selectedContainer[0]);

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
      if (this.selectedContainer.length === 0) {
        this.toastr.error("Kindly select any row", "Notification" ,{
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    let paramdata = "";

    if (this.selectedContainer.length > 0) {
      paramdata = this.cs.encrypt({ code: this.selectedContainer[0], pageflow: data });
      this.router.navigate(['/main/inbound/container-create/' + paramdata]);
    }
    else {
      paramdata = this.cs.encrypt({ pageflow: data });
      this.router.navigate(['/main/inbound/container-create/' + paramdata]);
    }

   
  }
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId

  searhform = this.fb.group({
    companyCodeId: [[this.auth.companyId]],
    languageId: [[this.auth.languageId]],
    plantId: [[this.auth.plantId]],
    warehouseId: [[this.auth.warehouseId]],
    containerNo: [],
    containerReceiptNo: [],
    endContainerReceivedDate: [],

    partnerCode: [],
    referenceField1: [],
    startContainerReceivedDate: [],
    statusId: [],

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



  search(ispageload = false) {
    if (!ispageload) {

      //dateconvertion
      this.searhform.controls.endContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.endContainerReceivedDate.value));
      this.searhform.controls.startContainerReceivedDate.patchValue(this.cs.day_callapiSearch(this.searhform.controls.startContainerReceivedDate.value));

    }
    this.spin.show();
    this.service.searchSpark(this.searhform.value).subscribe(res => {
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

      }
      res.forEach(x => {
        if(x.referenceField5 != null){
          x.statusId = 24;
        }
        if(x.refDocNumber && x.referenceField5 == null){
          x.statusId = 11;
        }
      })

      this.containerReceipt = res;
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    });}


    reload(){
      this.searhform.reset();
      this.searhform.controls.companyCodeId.patchValue([this.auth.companyId])
      this.searhform.controls.languageId.patchValue([this.auth.languageId])
      this.searhform.controls.plantId.patchValue([this.auth.plantId])
      this.searhform.controls.warehouseId.patchValue([this.auth.warehouseId])
      
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
