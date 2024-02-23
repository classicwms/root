import { Component, OnInit, ViewChild } from '@angular/core';
import { Location } from "@angular/common";
import { SelectionModel } from '@angular/cdk/collections';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PrepetualCountService } from '../prepetual-count.service';
import { Table } from 'primeng/table';
import { AssignPickerComponent } from 'src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component';
import { BinlocationService } from 'src/app/main-module/Masters -1/masternew/binlocation/binlocation.service';
import { forkJoin, of } from 'rxjs';
import { ReportsService } from 'src/app/main-module/reports/reports.service';
import { catchError } from 'rxjs/operators';
@Component({
  selector: 'app-perpetual-variance-confirm',
  templateUrl: './perpetual-variance-confirm.component.html',
  styleUrls: ['./perpetual-variance-confirm.component.scss']
})
export class PerpetualVarianceConfirmComponent implements OnInit {



  periodicvarianceConfirm: any[] = [];
  periodicvarianceassigned: any[] = [];
  perpetualNoStock: any[] = [];
  @ViewChild('periodicvarianceConfirmTag') periodicvarianceConfirmTag: Table | any;

  @ViewChild('perpetualNoStockTag') perpetualNoStockTag: Table | any;

  screenid: 1075 | undefined;
  title1 = "Cycle count";
  title2 = "Prepetual Confirm";
  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;
  constructor(private auth: AuthService, public dialog: MatDialog, private prepetualCountService: PrepetualCountService, public toastr: ToastrService, private spin: NgxSpinnerService, private location: Location,
    private route: ActivatedRoute, private router: Router, private storageService: BinlocationService, private reportService: ReportsService,
    public cs: CommonService,) { }


  showAMS = false;

  checkAssignUser() {
    let asshignUser: any[] = []
    this.periodicvarianceConfirm.forEach(x => {
      if (x.cycleCountAction == 'CONFIRM') {
        x.statusId = 78;
      }
      if (x.cycleCountAction == 'RECOUNT' && x.firstCountedQty == null) {
        this.AssignUser(x);
        asshignUser.push(x)
      }
    })
    if (asshignUser.length == 0) {
      this.periodicvarianceConfirm.filter(x => x.firstCountedQty != null ? x.secondCountedQty = x.countedQty : x.secondCountedQty = null);
      this.confirmNew();
    }
  }

  AssignUser(line) {
    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.periodicvarianceConfirm.forEach(element => {
          element.firstCountedQty = element.countedQty;
          if (element.cycleCountAction == 'RECOUNT') {
            element.cycleCounterId = result;
            element.statusId = 75;
          }
        });
        this.confirmNew();
      }
    });
  }

  confirmNew() {
    let code = this.route.snapshot.params.code;
    let js = this.cs.decrypt(code);

    this.prepetualCountService.varienAnalysisConfirm(this.code.cycleCountNo, this.periodicvarianceConfirm)
      .subscribe(result => {
        this.toastr.success("Prepetual details updated successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/cycle-count/varianceConfirm']);
      },
        error => {
          console.log(error);
          this.toastr.error(error.error, "Error", {
            timeOut: 2000,
            progressBar: false,
          });
          this.spin.hide();

        });
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
  code: any;
  pageflow: any;
  pageTitle: any = 'Perpetual Count';

  filterAssignedUSER: any[] = [];
  filterNoStock: any[] = [];
  filteredVariance: any[] = []
  filteredAssignUser: any[] = []
  filterCountLines: any[] = []
  ngOnInit(): void {

    let code = this.route.snapshot.params.code;

    let js = this.cs.decrypt(code);
    this.code = js.code;

    this.pageflow = js.pageflow;
    this.filteredVariance = [];
    this.periodicvarianceConfirm = [];
    this.periodicvarianceassigned = [];
    this.perpetualNoStock = [];
    this.spin.show();
    this.prepetualCountService.get(this.code).subscribe(result => {
      this.code = result;
      this.code.perpetualLine.forEach(element => {
        if (element.inventoryQuantity == null) {
          element.inventoryQuantity = 0
        }
        if (element.statusId == 72) {
          element.countedQty = element.inventoryQuantity;
        }
        if (element.statusId == 72 || element.statusId == 70 || element.statusId == 75) {
          this.filterAssignedUSER.push(element);
        }
        if (element.statusId == 47) {
          element.countedQty = 0;
          this.filterNoStock.push(element);
        }
        if (element.firstCountedQty != null) {
          element.cycleCountAction = 'CONFIRM';
        }
      });

      this.code.perpetualLine.forEach(element => {
        // if ((element.countedQty - element.inventoryQuantity) != 0 && element.varianceQty != null) {
        if (element.statusId == 74 || element.statusId == 75 || element.statusId == 78) {
          if (element.cycleCountAction == null) {
            element.cycleCountAction = "RECOUNT";
          }
          if ((element.countedQty - element.inventoryQuantity) != 0 && element.varianceQty != null && element.statusId != 78) {
            element.cycleCountAction = "RECOUNT";
          } else {
            element.cycleCountAction = "CONFIRM"
          }
          if (element.firstCountedQty != null) {
            element.cycleCountAction = "CONFIRM"
          }
          this.filteredVariance.push(element)
        }

        //}
      });
      if (this.filteredVariance.length == 0) {
        this.toastr.error("No Lines found", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/cycle-count/varianceConfirm']);
      }
      this.periodicvarianceConfirm = this.filteredVariance;
      this.pageTitle = this.pageflow;
      this.periodicvarianceassigned = this.filterAssignedUSER;
      this.perpetualNoStock = this.filterNoStock;
    });
    this.spin.hide();
  };

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
  back() {
    this.location.back();
  }




  pushToAms() {

    let dataErrorArray: any = [];
    this.code.perpetualLine.forEach(data => {
      //referenceField9 -> picked qty
      //referenceField10 -> qa qty
      if (data.statusId != 78 || data.statusId != 47) {
        dataErrorArray.push(data);
      }
    });
    if (dataErrorArray.length > 0) {
      this.toastr.error(
        "Order not completely confirmed",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.spin.show();
    this.code.perpetualLine.filter(x => x.statusId == 47 ? x.countedQty = 0 : '');
    this.prepetualCountService.pushToAMS(this.code.cycleCountNo, this.code.perpetualLine).subscribe( //this.periodicvarianceConfirm
      result => {
       if(result.statusCode == "1400"){
        this.toastr.error("Counted Qty Update Failed", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide(); 
        return;
       }
        this.spin.hide();
        this.toastr.success(
          "Stock count sent to AMS Successfully",
          "Notification"
        );

        // if (result) {
        //   this.prepetualCountService.updatePerpetualHeader(this.code.cycleCountNo, this.code.cycleCountTypeId, this.auth.warehouseId, this.code.movementTypeId, this.code.subMovementTypeId, {
        //     plantId: this.auth.plantId, languageId: this.auth.languageId,
        //     warehouseId: this.auth.warehouseId, cycleCountTypeId: this.code.cycleCountTypeId, companyCodeId: this.auth.companyId, cycleCountNo: this.code.cycleCountNo, movementTypeId: this.code.movementTypeId,
        //     subMovementTypeId: this.code.subMovementTypeId, referenceField1: true, perpetualLine: this.code.perpetualLine
        //   }).subscribe(res => {

        //     this.router.navigate(['/main/cycle-count/varianceConfirm']);

        //   });
        // }
        this.router.navigate(['/main/cycle-count/varianceConfirm']);
      },
      error => {
        this.spin.hide();
        this.cs.commonerrorNew(error);
      }
    );
  }

  downloadexcel() {
    var res: any = [];
    this.periodicvarianceConfirm.forEach(x => {
      res.push({
        "Actions ": x.cycleCountAction,
        "Product code ": x.itemCode,
        'Description': x.itemDesc,
        'Mfr Sku': x.manufacturerPartNo,
        "Section": x.storageSectionId,
        "Bin Location": x.storageBin,
        "Barcode Id": x.barcodeId,
        "Stock Type": x.stockTypeId,
        "Spl Stock Type": x.specialStockIndicator,
        "Inventory Qty": x.inventoryQuantity,
        "Counted Qty": x.countedQty,
        "Variance": x.countedQty - x.inventoryQuantity,
        "User ID  ": x.cycleCounterId,
        "Status Id": x.statusDescription

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Perpetual  Stock Analysis");
  }


  multiselectStorageList: any[] = [];
  storageBinList1: any[] = [];

  onStorageType(searchKey) {
    let searchVal = searchKey?.filter;
    if (searchVal !== '' && searchVal !== null) {
      forkJoin({
        storageList: this.reportService.getStorageDropDown2(searchVal.trim(), this.auth.companyId, this.auth.plantId, this.auth.warehouseId, this.auth.languageId).pipe(catchError(err => of(err))),
      })
        .subscribe(({ storageList }) => {
          if (storageList != null && storageList.length > 0) {
            this.multiselectStorageList = [];
            this.storageBinList1 = storageList;
            this.storageBinList1.forEach(x => this.multiselectStorageList.push({ value: x.storageBin, label: x.storageBin }))
          }
        });
    }
  }
  create(element) {
    if (!element.storageBin) {
      this.toastr.error("Kindly select the storage bin", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    this.spin.show();
    this.storageService.Get(element.storageBin, this.auth.warehouseId, this.auth.languageId, this.auth.plantId, this.auth.companyId).subscribe(res => {
      element.storageSectionId = res.storageSectionId;
      element.levelId = res.floorId;
      element.referenceField5 = res.binClassId;
      element.referenceField6 = res.aisleNumber;
      element.referenceField7 = res.shelfId;
      element.referenceField8 = res.rowId;

      this.prepetualCountService.noStockPerpetual([element]).subscribe(res => {
        this.toastr.success(
          "Stock count sent to AMS Successfully",
          "Notification"
        );
        this.spin.hide();
      }, err => {
        this.spin.hide();
      })
    }, err => {
      this.spin.hide();
    })
  }
}

