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
  selector: 'app-salesreturnheader-lines',
  templateUrl: './salesreturnheader-lines.component.html',
  styleUrls: ['./salesreturnheader-lines.component.scss']
})
export class SalesreturnheaderLinesComponent implements OnInit {

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
  
this.picking=data.code.salesReturnLines;
}

form = this.fb.group({
  assignedPickerId: [,],
});

downloadexcel() {
  var res: any = [];
  this.picking.forEach((x, index) => {
    res.push({
      'S No': index + 1, 
      'Source Branch': x.sourceBranchCode,
      'Mfr Name': x.manufacturerCode,
      'Part No': x.itemCode,
      "Description": x.itemDescription,
      "Ref Invoice No": x.referenceInvoiceNo,
      "Origin": x.countryOfOrigin,
      "UOM": x.unitOfMeasure,
      "No of Packs": x.noOfPacks,
      "Return Qty": x.returnQty,
      "Return Order Date": this.cs.dateapiwithTime(x.returnOrderDate),
      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.createdOn),
    });
  });
  const returnOrderNo = this.code.returnOrderNo; 
const fileName = `Sales Return Lines-_${returnOrderNo}-`;
this.cs.exportAsExcel(res, fileName);
}





}


