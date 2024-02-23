import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PrepetualCountService } from '../../prepetual-count/prepetual-count.service';
import { StockCountService } from '../stock-count.service';
import { timeStamp } from 'console';

@Component({
  selector: 'app-stock-adjustment',
  templateUrl: './stock-adjustment.component.html',
  styleUrls: ['./stock-adjustment.component.scss']
})
export class StockAdjustmentComponent implements OnInit {

  stockAdjustment: any[] = [];
  selectedstockAdjustment: any[] = [];
  @ViewChild('stockAdjustmentTag') stockAdjustmentTag: Table | any;

  screenid = 3207;
  constructor(public cs: CommonService, public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, private route: ActivatedRoute, private auth: AuthService,
    private StockCountService: StockCountService,) {

    // route.params.subscribe(val => {
    //   debugger
    //   this.ngOnInit();
    // });
  }

  pageflow: any;


  ngOnInit(): void {
    this.getStockAdjustment();
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

  openDialog(data: any = 'New', linedata: any = null): void {

    if (linedata.statusId === 88) {
      this.toastr.error("Stock Adjustment Confirmed", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    if (data == 'LineEdit') {
      this.selectedstockAdjustment = [];
      this.selectedstockAdjustment.push(linedata);
      data = 'Edit';
    }

    if (data != 'New' && linedata == null) {
      if (this.selectedstockAdjustment.length === 0) {
        this.toastr.error("Kindly select any row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
    }
    let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedstockAdjustment[0] : linedata, pageflow: data });
    this.router.navigate(['/main/cycle-count/stockAdjustmentEdit/' + paramdata]);
  }

  onChange() {
    const choosen = this.selectedstockAdjustment[this.selectedstockAdjustment.length - 1];
    this.selectedstockAdjustment.length = 0;
    this.selectedstockAdjustment.push(choosen);
  }
  downloadexcel() {
    var res: any = [];
    this.stockAdjustment.forEach(x => {
      res.push({
         "Adjustment No":x.stockAdjustmentKey,
         "Adjustment Type":x.statusDescription,
         "Mfr Name":x.manufacturerName,
        "Part No":x.itemCode,
        "Description":x.itemDescription,
        "Barcode Id":x.barcodeId,
        "Adjusted Qty":x.adjustmentQty,
        "Before Adjustment":x.beforeAdjustment,
        "After Adjustment":x.afterAdjustment,
        "Adjusted On":x.createdOn,
        "Created By":x.createdBy,
       
      });
  
    })
    this.cs.exportAsExcel(res, "Stock Adjustment");
  }
  getStockAdjustment() {
    this.stockAdjustment = [];
    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.languageId = [this.auth.languageId];
    obj.plantId = [this.auth.plantId];
    obj.warehouseId = [this.auth.warehouseId];

    this.StockCountService.findStockAdjustment(obj).subscribe(res => {
      let uniqueQuotationNumberArray  = this.cs.removeDuplicatesFromArrayList(res, 'stockAdjustmentKey');
      this.stockAdjustment = uniqueQuotationNumberArray;
      this.spin.hide();
    },err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    })
  }
}

