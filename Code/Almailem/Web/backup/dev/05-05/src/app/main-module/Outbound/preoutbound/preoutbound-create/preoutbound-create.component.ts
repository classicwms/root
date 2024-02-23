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

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
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
  
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
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
      this.sub.add(this.service.searchLine({ refDocNumber: [this.data.refDocNumber], preOutboundNo: [this.data.preOutboundNo], warehouseId: [this.data.warehouseId] }).subscribe(res => {
        this.spin.hide();

        this.dataSource = new MatTableDataSource<any>(res);
        this.selection = new SelectionModel<any>(true, []);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.data.outboundOrderTypeId=this.cs.getoutboundOrderType_text( this.data.outboundOrderTypeId)
      }, err => {

        this.cs.commonerror(err);
        this.spin.hide();

      }));

    }

  }
  displayedColumns: string[] = [ 'status','lineNumber','ordercategory','storecode', 'product', 'description',  'order', 'uom', 'req',];
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel<any>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: clientcategory): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
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


}

