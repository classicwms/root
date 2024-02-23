import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PreoutboundService } from "../preoutbound.service";
import { InventoryQtyComponent } from "./inventory-qty/inventory-qty.component";
import { Table } from "primeng/table";

export interface clientcategory {


  no: string;
  lineno: string;
  partner: string;
  product: string;
  description: string;
  mfrno: string;
  variant: string;
  order: string;
  uom: string;
  req: string;
  stock: string;
  spl: string;
  sname: string;
}

const ELEMENT_DATA: clientcategory[] = [
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },
  { no: "1", lineno: 'readonly', partner: 'dropdown', product: 'Enter', description: 'readonly', mfrno: 'readonly', variant: 'readonly', order: 'enter', uom: 'dropdowm', req: 'enter', stock: 'enter', spl: 'enter', sname: 'enter', },

];
@Component({
  selector: 'app-preoutbound-create',
  templateUrl: './preoutbound-create.component.html',
  styleUrls: ['./preoutbound-create.component.scss']
})
export class PreoutboundCreateComponent implements OnInit {

  isShowDiv = false; 
  outbounddis: any[] = [];
  selectedOutbounddis : any[] = [];
  @ViewChild('preoutboundTag') preoutboundTag: Table | any;
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

  
  animal: string | undefined;
  name: string | undefined;
  stockdetails(): void {

    const dialogRef = this.dialog.open(InventoryQtyComponent, {
      disableClose: true,
      width: '45%',
      maxWidth: '83%',
      position: {},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  constructor(private service: PreoutboundService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService,
    private fb: FormBuilder, private route: ActivatedRoute,
    public cs: CommonService,) { }
  sub = new Subscription();
  data: any;
  

  ngOnInit(): void {

    let code = this.route.snapshot.params.code;
    if (code != 'new') {
      let js = this.cs.decrypt(code);
      this.data = js.code;
      
      console.log(this.data);
      this.data.refDocDate = this.cs.day_callapi11(this.data.refDocDate);
      this.data.requiredDeliveryDate = this.cs.day_callapi11(this.data.requiredDeliveryDate);
      this.data.createdOn = this.cs.day_callapi11(this.data.createdOn);

      this.spin.show();
      this.sub.add(this.service.searchLineSpark({ refDocNumber: [this.data.refDocNumber], preOutboundNo: [this.data.preOutboundNo], warehouseId: [this.auth.warehouseId] ,companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],languageId:[this.auth.languageId]}).subscribe(res => {
        this.spin.hide();

        this.outbounddis = res;
       ;
      
        this.data.outboundOrderTypeId=this.cs.getoutboundOrderType_text( this.data.outboundOrderTypeId)
      }, err => {

        this.cs.commonerrorNew(err);;
        this.spin.hide();

      }));

    }

  }



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

  onChange() {
    const choosen= this.selectedOutbounddis[this.selectedOutbounddis.length - 1];   
    this.selectedOutbounddis.length = 0;
    this.selectedOutbounddis.push(choosen);
  }
}

