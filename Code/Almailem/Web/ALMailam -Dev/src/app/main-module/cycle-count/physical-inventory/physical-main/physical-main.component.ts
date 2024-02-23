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
import { AnnualCreateComponent } from "./annual-create/annual-create.component";
import { Table } from "primeng/table";
@Component({
  selector: 'app-physical-main',
  templateUrl: './physical-main.component.html',
  styleUrls: ['./physical-main.component.scss']
})
export class PhysicalMainComponent implements OnInit {

  periodic: any[] = [];
  selectedperiodic: any[] = [];
  @ViewChild('periodicTag') periodicTag: Table | any;

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
    this.periodic = [];
    let obj: any = {};
    obj.warehouseId = [this.auth.warehouseId];
    obj.companyCode = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.headerStatusId = [];
    this.spin.show();
    this.periodicService.findPeriodicDataListNew1(obj).subscribe( //20-06-23 streaming
      result => {
        this.spin.hide();
        this.periodic = result;
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
    });
  }


  goTo(type: any, element: any = null) {
    if (type == 'Assign' || type == 'count') {
      this.selectedperiodic = [];
      this.selectedperiodic.push(element);
    }
    if (type == 'LineEdit') {
      this.selectedperiodic = [];
      this.selectedperiodic.push(element);
      type = 'Display'
    }
    if (this.selectedperiodic[0].statusId === 78 && type != "Display") {
      this.toastr.error("Stock count confirmed can't be edited", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    if (type == "count") {
      let obj: any = {};
      obj.companyCode = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
      obj.warehouseId = [this.auth.warehouseId];
      obj.cycleCountNo = [element.cycleCountNo];
      obj.lineStatusId = [72, 75] 
      this.periodicService.findPeriodicDLineSprk(obj).subscribe(res => {

        if (this.selectedperiodic.length == 0) {
          this.toastr.error(
            "Please choose line to edit",
            "Notification"
          );
          return;
        }
        else if (res.length == 0) {
          this.toastr.error(
            "Please assign counter first",
            "Notification"
          );
          return;

        }
        else {
          let obj: any = {};
          obj.periodicHeaderData = this.selectedperiodic[0];
          obj.periodicLine = res;
          obj.type = type;
          let data = this.cs.encrypt(obj);
          this.router.navigate(['/main/cycle-count/periodic-count-confirm', data])
        }
      },err => {
        this.spin.hide();
        this.cs.commonerrorNew(err);
      })
    }
    if (type == "Assign") {
      let obj: any = {};
      obj.companyCode = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
      obj.warehouseId = [this.auth.warehouseId];
      obj.cycleCountNo = [element.cycleCountNo];
      obj.lineStatusId = [70, 72, 73],

      this.periodicService.findPeriodicDLineSprk(obj).subscribe(res => {

        if (this.selectedperiodic.length == 0) {
          this.toastr.error(
            "Please choose line to edit",
            "Notification"
          );
          return;
        }
        else if (res.length == 0) {
          this.toastr.error(
            "No lines found",
            "Notification"
          );
          return;

        }
        else {
          let obj: any = {};
          obj.periodicHeaderData = this.selectedperiodic[0];
          obj.type = type;
          obj.periodicLine = res;
          let data = this.cs.encrypt(obj);
          this.router.navigate(['/main/cycle-count/periodic-count-confirm', data])
        }
      },err => {
        this.spin.hide();
        this.cs.commonerrorNew(err);
      })
    }
    if (type == "Display") {
      let obj: any = {};
      obj.companyCode = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
      obj.plantId = [this.auth.plantId];
      obj.warehouseId = [this.auth.warehouseId];
      obj.cycleCountNo = [this.selectedperiodic[0].cycleCountNo];

      this.periodicService.findPeriodicDLineSprk(obj).subscribe(res => {
        if (this.selectedperiodic.length == 0) {
          this.toastr.error(
            "Please choose line to edit",
            "Notification"
          );
          return;
        }
        else {
          let obj: any = {};
          obj.periodicHeaderData = this.selectedperiodic[0];
          obj.type = type;
          obj.periodicLine = res;
          let data = this.cs.encrypt(obj);
          this.router.navigate(['/main/cycle-count/periodic-count-confirm', data])
        }
      },err => {
        this.spin.hide();
        this.cs.commonerrorNew(err);
      });
    }
  }
  downloadexcel() {
    var res: any = [];
    this.periodic.forEach(x => {
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
    this.cs.exportAsExcel(res, "Assign & Count");
  }
  onChange() {
    const choosen = this.selectedperiodic[this.selectedperiodic.length - 1];
    this.selectedperiodic.length = 0;
    this.selectedperiodic.push(choosen);
  }
}
