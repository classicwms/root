import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PickingService } from 'src/app/main-module/Outbound/picking/picking.service';

@Component({
  selector: 'app-supplierinvoice-lines',
  templateUrl: './supplierinvoice-lines.component.html',
  styleUrls: ['./supplierinvoice-lines.component.scss']
})
export class SupplierinvoiceLinesComponent implements OnInit {

  screenid=3063;
  picking: any[] = [];
  selectedpicker : any[] = [];
  @ViewChild('pickingTag') pickingTag: Table | any;
 data:any;
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  storecodeList: any;
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

  

  title1 = "Outbound";
  title2 = "Order Management";
  constructor(private service: PickingService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,private route: ActivatedRoute,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,) { }
  sub = new Subscription();
  code:any;
  RA: any = {};
  ngOnInit(): void {
console.log(this.code);
    let code = this.route.snapshot.params.code;
    let js = this.cs.decrypt(code);
    this.code = js.code;
    console.log(js);
   this.fill(js);
  }

  warehouseId = this.auth.warehouseId
 
fill(data:any){
  
this.picking=data.code.supplierInvoiceLines;
}

form = this.fb.group({
  assignedPickerId: [,],
});

downloadexcel() {
  var res: any = [];
  this.picking.forEach((x, index) => {
    res.push({
      'S No': index + 1, 
      'Supplier Code': x.supplierCode,
      'Mfr Name': x.manufacturerShortName,
      'Part No': x.itemCode,
      "Description": x.itemDescription,
      "Supplier Invoice No": x.supplierInvoiceNo,
      "Supplier Name": x.supplierName,
      "UOM": x.unitOfMeasure,
      "Purchase Order No": x.purchaseOrderNo,
      "Invoice Qty": x.invoiceQty,
      "Received Date": this.cs.dateapiwithTime(x.receivedDate),
      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.createdOn),
    });
  });
  const returnOrderNo = this.code.supplierInvoiceNo; 
const fileName = `Supplier Invoice Lines-_${returnOrderNo}-`;
this.cs.exportAsExcel(res, fileName);
}





}


