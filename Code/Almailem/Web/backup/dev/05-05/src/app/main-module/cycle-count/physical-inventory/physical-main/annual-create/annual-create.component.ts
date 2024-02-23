import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-annual-create',
  templateUrl: './annual-create.component.html',
  styleUrls: ['./annual-create.component.scss']
})
export class AnnualCreateComponent implements OnInit {

  screenid: 1077 | undefined;
  warehouseList: any[] = [];
  multiSelectWarehouseList: any[] = [];
  multiWarehouseList: any[] = [];
  selectedWareHouse: any[] = [];

  storageSectionList: any[] = [];
  multiSelectStorageSectionList: any[] = [];
  multiStorageSectionList: any[] = [];
  selectedStorageSection: any[] = [];

  storageBinList: any[] = [];
  multiSelectStorageBinList: any[] = [];
  multiStorageBinList: any[] = [];
  selectedStorageBin: any[] = [];

  createdDate = new Date();
  cycledCount = 2;

  dropdownSettings: IDropdownSettings = {
    singleSelection: false,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };

  constructor(
    public dialog: MatDialog,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private masterService: MasterService,
    public cs: CommonService,
    public auth: AuthService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.getAllDropDown();
  }

  getAllDropDown() {
    this.spin.show();
    forkJoin({
      warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
      storageSection: this.masterService.getStorageSectionMasterDetails().pipe(catchError(err => of(err)))
    })
      .subscribe(({ warehouse, storageSection }) => {
        this.warehouseList = warehouse;
        this.warehouseList.forEach(x => this.multiWarehouseList.push({ value: x.warehouseId, label: x.warehouseId + (x.description == null ? '' : '- ' + x.description) }));
        this.multiSelectWarehouseList = this.multiWarehouseList;
        this.multiSelectWarehouseList.forEach((warehouse: any) => {
          if (warehouse.value == this.auth.warehouseId)
            this.selectedWareHouse = [warehouse.value];
        })
        this.storageSectionList = storageSection;
        this.storageSectionList.forEach(x => this.multiStorageSectionList.push({ value: x.storageSectionId, label: x.storageSectionId + (x.storageSection == null ? '' : '- ' + x.storageSection) }));
        this.multiSelectStorageSectionList = this.multiStorageSectionList;
        this.multiSelectStorageSectionList = this.cs.removeDuplicatesFromArrayNewstatus(this.multiSelectStorageSectionList);


        //for token error
        forkJoin({
          storageBin: this.masterService.getAllStorageBin({}).pipe(catchError(err => of(err)))
        })
          .subscribe(({ storageBin }) => { })

      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();
  }

  onItemSelect(item: any) {
    this.spin.show();
    forkJoin({
      storageBin: this.masterService.getAllStorageBin({storageSectionId: item.value, warehouseId: [this.auth.warehouseId]}).pipe(catchError(err => of(err)))
    })
      .subscribe(({ storageBin }) => {
        this.storageBinList = storageBin;
        this.storageBinList.forEach(x => this.multiStorageBinList.push({ value: x.storageBin, label: x.storageBin }));
        this.multiSelectStorageBinList = this.multiStorageBinList;
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();
  }

  goTo() {
    let obj: any = {};
    obj.cycledCount = this.cycledCount;
    obj.createdDate = this.createdDate;
    obj.storageBin = this.selectedStorageBin;
    obj.storageSectionId = this.selectedStorageSection;
    obj.warehouseId = this.selectedWareHouse;
    let data = this.cs.encrypt({obj,  pageflow: 'New'});
    this.router.navigate(['/main/cycle-count/physical-create', data])
  }

}
