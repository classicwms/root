import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { StockCountService } from '../stock-count.service';

let Periodic: any[] = [
  { "manufacturerName": "autolab", "itemDescription": "1234", "barcodeId": 2, "inventoryQuantity": 30, "adjustmentQty": 5, "packbarcode": '1' },
  { "manufacturerName": "autolab", "itemDescription": "1234", "barcodeId": 2, "inventoryQuantity": 100, "adjustmentQty": 5, "packbarcode": '2' },
  { "manufacturerName": "autolab", "itemDescription": "1234", "barcodeId": 2, "inventoryQuantity": 80, "adjustmentQty": 5, "packbarcode": '3' },
  { "manufacturerName": "autolab", "itemDescription": "12345", "barcodeId": 2, "inventoryQuantity": 50, "adjustmentQty": 5, "packbarcode": '4' },
  { "manufacturerName": "autolab", "itemDescription": "12345", "barcodeId": 1, "inventoryQuantity": 20, "adjustmentQty": 5, "packbarcode": '5' },
  { "manufacturerName": "autolab", "itemDescription": "6666", "barcodeId": 1, "inventoryQuantity": 70, "adjustmentQty": 5, "packbarcode": '6' },
]
const data: any[] = [
  { name: "A", category: "X", value1: 1, value2: 2 },
  { name: "B", category: "X", value1: 3, value2: 4 },
  { name: "C", category: "Y", value1: 5, value2: 6 },
  { name: "D", category: "Y", value1: 7, value2: 8 },
];


@Component({
  selector: 'app-stock-adjustment-edit',
  templateUrl: './stock-adjustment-edit.component.html',
  styleUrls: ['./stock-adjustment-edit.component.scss']
})
export class StockAdjustmentEditComponent implements OnInit {
  stockAdjustment: any[] = [];
  selectedstockAdjustment: any[] = [];
  @ViewChild('stockAdjustmentTag') stockAdjustmentTag: Table | any;

  screenid = 3207;
  constructor(public cs: CommonService, public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, private route: ActivatedRoute, private auth: AuthService,
    private spinner: NgxSpinnerService, private StockCountService: StockCountService,) { }

  pageflow: any;
  data: any;

  ngOnInit(): void {
    this.stockAdjustment = [];
    this.data = this.cs.decrypt(this.route.snapshot.params.data);
    this.getStockAdjustment();
    this.getGroupData();
    //this.group()
  }


  group() {

  }

  updateInventoryQty1(line) {
    let get = this.groupedData.get(line.storageBin);
    line.adjustmentQtyEdit1 = line.adjustmentQtyEdit;
    for (let i = 0; i < get.length; i++) {
      if ((get[i].inventoryQuantity + line.adjustmentQtyEdit1) > 0) {
        get[i].inventoryQuantity1 = get[i].inventoryQuantity + line.adjustmentQtyEdit1;
        break;
      } else {
        let variance = get[i].inventoryQuantity + line.adjustmentQtyEdit1;
        get[i].inventoryQuantity1 = 0;
        line.adjustmentQtyEdit1 = variance;
      }
    }
    console.log(get)
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

  updateInventoryQty(line) {
    line.inventoryQuantity1 = 0;
    line.inventoryQuantity1 = line.inventoryQuantity - line.adjustmentQtyEdit;
  }

  openDialog(data: any = 'New', linedata: any = null): void {
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
    let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedstockAdjustment[0] : linedata, pageflow: this.pageflow = data });
    this.router.navigate(['/main/cycle-count/varianceConfirm/' + paramdata]);
  }

  onChange() {
    const choosen = this.selectedstockAdjustment[this.selectedstockAdjustment.length - 1];
    this.selectedstockAdjustment.length = 0;
    this.selectedstockAdjustment.push(choosen);
  }

  groupedData: any;
  getStockAdjustment() {
    this.spin.show();
    let unGroupedData: any = [];

    this.StockCountService.findStockAdjustment({ stockAdjustmentKey: [this.data.code.stockAdjustmentKey] }).subscribe(res => {

      if (res) {
        const result = res.reduce((acc, cur) => {
          const index = acc.findIndex((d) => d.storageBin === cur.storageBin);

          if (index === -1) {
            acc.push(cur);
          } else {
            acc[index].inventoryQuantity += cur.inventoryQuantity;
          }
          return acc;
        }, []);

        result.forEach((x, i) => {
          if (i == 0) {
            x.adjustmentQtyEdit = x.adjustmentQty;
            x.inventoryQuantity1 = x.inventoryQuantity + x.adjustmentQty;
          } else {
            x.inventoryQuantity1 = x.inventoryQuantity;
          }
        })
        this.stockAdjustment = result;
      }
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    })
  }

  getGroupData() {
    this.StockCountService.findStockAdjustment({ stockAdjustmentKey: [this.data.code.stockAdjustmentKey] }).subscribe(res => {
      console.log(res)
      if (res) {
        this.groupedData = this.cs.groupByData(res.sort((a, b) => (a.inventoryQuantity > b.inventoryQuantity) ? -1 : 1), (data: any) => data.storageBin);
      }
      console.log(this.groupedData)
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    })
  }
back(){
  this.router.navigate(["main/cycle-count/stockAdjustment"]);
}
  submit() {

    let finalLines: any[] = [];
    let storageBin: any[] = [];
    this.stockAdjustment.forEach(data => {
      if (!storageBin.includes(data.storageBin)) {
        storageBin.push(data.storageBin);
      }
    })


    storageBin.forEach(x => {
      let seperatedLines = this.groupedData.get(x);
      seperatedLines.forEach(element => {
        finalLines.push(element)
      });
    })

    let total = 0;
    let negtiveinventory: any[] = [];
    this.stockAdjustment.forEach(x => {
      total = total + (x.adjustmentQtyEdit != null ? x.adjustmentQtyEdit : 0)

      if ((x.inventoryQuantity != null ? x.inventoryQuantity : 0) + (x.adjustmentQtyEdit != null ? x.adjustmentQtyEdit : 0) < 0) {
        negtiveinventory.push(x)
      }
    })
    if (negtiveinventory.length  > 0) {
      this.toastr.error("Inventory is Negative", "Notification", {
        timeOut: 2000,
        progressBar: false,
      })
      return;
    }
    if (this.stockAdjustment[0].adjustmentQty != total) {
      this.toastr.error("Adjusted Qty should be equal to " + this.stockAdjustment[0].adjustmentQty, "Notification", {
        timeOut: 2000,
        progressBar: false,
      })
      return;
    }
    this.spin.show();
    finalLines.forEach(x => {
      x.inventoryQuantity = x.inventoryQuantity1 ? x.inventoryQuantity1 : x.inventoryQuantity;
      x.statusId = 88;
    })
    this.StockCountService.updateStockAdjustment(this.data.code.stockAdjustmentKey, this.data.code.itemCode,
      this.data.code.manufacturerName,finalLines).subscribe(res => {
        if (res) {
          this.toastr.success("Stock Adjusted Successfully", "Notification", {
            timeOut: 2000,
            progressBar: false,
          })
        }
        this.router.navigate(['/main/cycle-count/stockAdjustment'])
        this.spin.hide();
      }, err => {
        this.spin.hide();
        this.cs.commonerrorNew(err);
      })
  }
}

