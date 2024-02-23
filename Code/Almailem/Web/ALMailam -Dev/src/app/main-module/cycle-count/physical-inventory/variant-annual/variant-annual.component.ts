import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PhysicalInventoryService } from "../physical-inventory.service";
import { AnnualCreateComponent } from "../physical-main/annual-create/annual-create.component";
import { Table } from "primeng/table";
@Component({
  selector: 'app-variant-annual',
  templateUrl: './variant-annual.component.html',
  styleUrls: ['./variant-annual.component.scss']
})
export class VariantAnnualComponent implements OnInit {

  periodicvariance: any[] = [];
  selectedperiodicvariance: any[] = [];
  @ViewChild('periodicvarianceTag') periodicvarianceTag: Table | any;

  screenid: 1076 | undefined;

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  constructor(
    public dialog: MatDialog,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    public auth: AuthService,
    public periodicService: PhysicalInventoryService,
    public toastr: ToastrService,
    public router: Router,
  ) { }

  ngOnInit(): void {
    this.getAllPeriodicCountList();
  }

  getAllPeriodicCountList() {
    let obj: any = {};
    obj.warehouseId = [this.auth.warehouseId];
    obj.headerStatusId = [73, 74, 78];
    obj.companyCode = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    this.spin.show();
    this.periodicService.findPeriodicDataListNew1(obj).subscribe( //20-06-23 streaming
      result => {
        this.spin.hide();
        this.periodicvariance = result;
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


  create(): void {
    const dialogRef = this.dialog.open(AnnualCreateComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  downloadexcel() {
    var res: any = [];
    this.periodicvariance.forEach(x => {
      res.push({
         "Branch":x.plantDescription,
         "Warehouse":x.warehouseDescription,
        "Status":x.statusDescription,
        "AMS Cycle Count":x.referenceCycleCountNo,
        "Cycle Count No":x.cycleCountNo,
        "Created On":this.cs.dateapi(x.createdOn),
        "Created By":x.createdBy,
       
      });
  
    })
    this.cs.exportAsExcel(res, "Stock Analysis");
  }

  goTo(pageFlow: any = 'Edit', element: any = null) {
    if (pageFlow == 'LineEdit') {
      this.selectedperiodicvariance = [];
      this.selectedperiodicvariance.push(element);
      pageFlow = 'Edit';
    }
    if (this.selectedperiodicvariance.length == 0) {
      this.toastr.error(
        "Please choose line to edit",
        "Notification"
      );
      return;
    }
    if (this.selectedperiodicvariance[0].referenceField1 === 'true' && pageFlow == 'Edit') {
      this.toastr.error("Stock pushed to AMS can't be edited", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    else {
      let obj: any = {};
      obj.periodicHeaderData = this.selectedperiodicvariance[0];
      obj.type = pageFlow;
      let data = this.cs.encrypt(obj);
      this.router.navigate(['/main/cycle-count/variant-annual-edit', data])
    }
  }
  onChange() {
    const choosen = this.selectedperiodicvariance[this.selectedperiodicvariance.length - 1];
    this.selectedperiodicvariance.length = 0;
    this.selectedperiodicvariance.push(choosen);
  }
}
