import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PrepetualCountService } from '../prepetual-count.service';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-perpetual-variance',
  templateUrl: './perpetual-variance.component.html',
  styleUrls: ['./perpetual-variance.component.scss']
})
export class PerpetualVarianceComponent implements OnInit {


  perpetualvariance: any[] = [];
  selectedperpetualvariance : any[] = [];
  @ViewChild('perpetualvarianceTag') perpetualvarianceTag: Table | any;

  screenid: 1071 | undefined;
  constructor(public cs: CommonService, public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, private route: ActivatedRoute, private auth: AuthService,
    private spinner: NgxSpinnerService, private prepetualCountService: PrepetualCountService,) {

    // route.params.subscribe(val => {
    //   debugger
    //   this.ngOnInit();
    // });
  }

  pageflow: any;


  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.pageflow = 'Variance Analysis';
    /** spinner starts on init */
    this.spinner.show();
      this.getPerpetualCountList();

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

  downloadexcel() {
    var res: any = [];
    this.perpetualvariance.forEach(x => {
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


  
  openDialog(data: any = 'New', linedata: any = null): void {

    if (data == 'LineEdit') {
      this.selectedperpetualvariance = [];
      this.selectedperpetualvariance.push(linedata);
      data = 'Edit';
    }

    if (data != 'New' && linedata == null) {
      if (this.selectedperpetualvariance.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
      if (this.selectedperpetualvariance[0].referenceField1 === 'true' && data == 'Edit') {
        this.toastr.error("Stock pushed to AMS can't be edited", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
      let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedperpetualvariance[0] : linedata, pageflow: this.pageflow = data });
      this.router.navigate(['/main/cycle-count/varianceConfirm/' + paramdata]);
  }

  getPerpetualCountList() {
    this.spin.show();
    this.perpetualvariance = [];
    let data: any = { 
      warehouseId: [this.auth.warehouseId],
      companyCode: [this.auth.companyId, ],
      languageId: [this.auth.languageId, ],
      plantId: [this.auth.plantId, ], }

    if (this.pageflow == 'Variance Analysis') {
      data.headerStatusId = [73, 74, 78];
    }
    this.prepetualCountService.getPerpetualCountList1(data).subscribe(
      result => {
        this.spin.hide();
        this.perpetualvariance = result;
      },
      error => {
        this.spin.hide();
        if (error.status == 415) {
          this.getPerpetualCountList();
        }
      }
    );
  }

  onChange() {
    const choosen= this.selectedperpetualvariance[this.selectedperpetualvariance.length - 1];   
    this.selectedperpetualvariance.length = 0;
    this.selectedperpetualvariance.push(choosen);
  }
}
