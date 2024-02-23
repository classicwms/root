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
import { MiddlewareService } from '../../middleware.service';

@Component({
  selector: 'app-interwarehousein-lines',
  templateUrl: './interwarehousein-lines.component.html',
  styleUrls: ['./interwarehousein-lines.component.scss']
})
export class InterwarehouseinLinesComponent implements OnInit {

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
  constructor(private service: MiddlewareService,
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
  let obj: any = {};
    
     obj.transferOrderNo = [data.code.transferOrderNo];
   
  this.service.interwarehouseinlines(obj).subscribe(
    (res) => {
      this.spin.hide();
      this.picking = res;
    },
    (err) => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }
  );
//this.picking=data.code.transferInLines;
}

form = this.fb.group({
  assignedPickerId: [,],
});

downloadexcel() {
  var res: any = [];
  this.picking.forEach((x, index) => {
    res.push({
      'S No': index + 1, 
      'Mfr Name': x.manufacturerCode,
      "Part No":x.itemCode,
      "Description":x.itemDescription,
      "Transfer Order No":x.transferOrderNo,
      "UOM": x.unitOfMeasure,
      "Trasnfer Qty": x.transferQty,
      
      // 'Created By': x.createdBy,
      // 'Date': this.cs.dateapi(x.createdOn),
    });
  });
  const returnOrderNo = this.code.transferOrderNo; 
const fileName = `Interwarehouse Transfer Lines-_${returnOrderNo}-`;
this.cs.exportAsExcel(res, fileName);
}





}




