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
import { AssignuserPopupComponent } from "../../../prepetual-count/prepetual-confirm/assignuser-popup/assignuser-popup.component";
import { PhysicalInventoryService } from "../../physical-inventory.service";
import { Table } from "primeng/table";
import { AssignPickerComponent } from "src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component";
@Component({
  selector: 'app-variantedit-annual',
  templateUrl: './variantedit-annual.component.html',
  styleUrls: ['./variantedit-annual.component.scss']
})
export class VarianteditAnnualComponent implements OnInit {

  perpetualvarianceConfirm: any[] = [];
  @ViewChild('perpetualvarianceConfirmTag') perpetualvarianceConfirmTag: Table | any;

  title1 = "Cycle count";
  title2 = "Prepetual Confirm";
  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;

  data: any = {};

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  disabled = false;
  step = 0;
  panelOpenState = false;


  cycleCountActions: any[] = [{ view: 'Recount', value: 'RECOUNT' }, { view: 'Skip', value: 'SKIP' }, { view: 'Write Off', value: 'WRITEOFF' }]

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    public dialog: MatDialog,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    public auth: AuthService,
    public router: Router,
    private route: ActivatedRoute,
    public periodicService: PhysicalInventoryService
  ) { }


  @ViewChild(MatSort) sort: MatSort;
  pageflow: any;
  filteredVariance: any[] = [];
  ngOnInit(): void {
    this.perpetualvarianceConfirm = [];
    this.filteredVariance = [];
    this.data = this.cs.decrypt(this.route.snapshot.params.data);

    console.log(this.data)

    this.pageflow = this.data.type;
    let obj: any = {};
    obj.companyCode = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.cycleCountNo = [this.data.periodicHeaderData.cycleCountNo];

    this.periodicService.findPeriodicDLineSprk(obj).subscribe(res => {
      res.forEach(element => {
        //    if((element.countedQty - element.inventoryQuantity) != 0  && element.varianceQty != null ){   
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
        //  }
      });
      this.perpetualvarianceConfirm = this.filteredVariance;
    })

  }


  showAMS = false;
  checkAssignUser() {
    let asshignUser: any[] = []
    this.perpetualvarianceConfirm.forEach(x => {
      if (x.cycleCountAction == 'CONFIRM') {
        x.statusId = 78;
      }
      if (x.cycleCountAction == 'RECOUNT' && x.firstCountedQty == null) {
        this.AssignUser(x);
        asshignUser.push(x)
      }
    })
    if (asshignUser.length == 0) {
      this.perpetualvarianceConfirm.filter(x => x.firstCountedQty != null ? x.secondCountedQty = x.countedQty : x.secondCountedQty = null)
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
        this.perpetualvarianceConfirm.forEach(element => {
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

  stayOnSamePage = false;

  confirmNew() {
    this.spin.show();

    this.periodicService.updatePeriodicLine(this.perpetualvarianceConfirm).subscribe(
      result => {
        this.spin.hide();
        this.toastr.success(
          "Stock count confirmed Successfully",
          "Notification"
        );
        this.router.navigate(['/main/cycle-count/variant-analysis-annual']);
      },
      error => {
        this.spin.hide();
        this.cs.commonerrorNew(error);
      }
    );
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
    console.log('show:' + this.showFloatingButtons);
  }



  setStep(index: number) {
    this.step = index;
  }
  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }



  calculateVariance(element) {
    element.varianceQty = element.countedQty - element.inventoryQuantity;
  }


  pushToAms() {
    this.spin.show();
    this.periodicService.pushToAMSPeriodic(this.perpetualvarianceConfirm[0].cycleCountNo, this.perpetualvarianceConfirm).subscribe(
      result => {
        this.spin.hide();
        this.toastr.success(
          "Stock count sent to AMS Successfully",
          "Notification"
        );

        // if (result) {
        //   this.data.periodicHeaderData.referenceField1 = "true";
        //   this.periodicService.updatePeriodicHeader(this.data.periodicHeaderData).subscribe(res => {
        //     this.router.navigate(['main/cycle-count/variant-analysis-annual']);
        //   });
        // }
        this.router.navigate(['main/cycle-count/variant-analysis-annual']);
  
      },
      error => {
        this.spin.hide();
        this.cs.commonerrorNew(error);
      }
    );
  }


  downloadexcel() {
    var res: any = [];
    this.perpetualvarianceConfirm.forEach(x => {
      res.push({
        "Actions ": x.cycleCountAction,
        "Product code ": x.itemCode,
        'Description': x.referenceField8,
        'Mfr Sku': x.referenceField9,
        "Section": x.referenceField10,
        "Bin Location": x.storageBin,
        "Barcode Id": x.barcodeId,
        "Stock Type": x.stockTypeId,
        "Inventory Qty": x.inventoryQuantity,
        "Counted Qty": x.countedQty,
        "Variance": x.countedQty - x.inventoryQuantity,
        "User ID  ": x.cycleCounterId,
        "Status Id": x.statusDescription

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Periodic Stock Analysis");
  }
}

