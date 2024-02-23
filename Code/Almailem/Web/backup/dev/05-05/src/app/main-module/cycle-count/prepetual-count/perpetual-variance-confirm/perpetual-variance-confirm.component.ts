import { Component, OnInit, ViewChild } from '@angular/core';
import { Location } from "@angular/common";
import { SelectionModel } from '@angular/cdk/collections';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PrepetualCountService } from '../prepetual-count.service';
@Component({
  selector: 'app-perpetual-variance-confirm',
  templateUrl: './perpetual-variance-confirm.component.html',
  styleUrls: ['./perpetual-variance-confirm.component.scss']
})
export class PerpetualVarianceConfirmComponent implements OnInit {

  screenid: 1075 | undefined;
  title1 = "Cycle count";
  title2 = "Prepetual Confirm";
  isShown: boolean = false; // hidden by default
  toggleShow() { this.isShown = !this.isShown; }
  animal: string | undefined;
  name: string | undefined;
  constructor(private auth: AuthService, public dialog: MatDialog, private prepetualCountService: PrepetualCountService, public toastr: ToastrService, private spin: NgxSpinnerService, private location: Location,
    private route: ActivatedRoute, private router: Router,
    public cs: CommonService,) { }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  confirmNew(){
    let code = this.route.snapshot.params.code;
    let js = this.cs.decrypt(code);
    
     this.prepetualCountService.varienAnalysisConfirm(this.code.cycleCountNo, this.dataSource.data)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Prepetual details updated successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();

        this.location.back();
      },
        error => {
          console.log(error);
          this.toastr.error(error.error, "Error", {
            timeOut: 2000,
            progressBar: false,
          });
          this.spin.hide();

        });
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
  code: any;
  pageflow: any;
  pageTitle: any = 'Perpetual Count';

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  filteredVariance: any[] = []
  filteredAssignUser: any[] = []
  filterCountLines: any[] = []
  ngOnInit(): void {

    let code = this.route.snapshot.params.code;

    let js = this.cs.decrypt(code);

console.log(js)
    this.code = js.code;
    this.pageflow = js.pageflow;
      this.filteredVariance = []
      this.spin.show();
      this.prepetualCountService.get(this.code).subscribe(result=>{
        this.code = result;
        this.code.perpetualLine.forEach(element => {
          if (element.inventoryQuantity == null) {
            element.inventoryQuantity = 0
          }
          if (element.statusId == 72) {
            element.countedQty = element.inventoryQuantity;
          }
        
        });
          this.code.perpetualLine.forEach(element => {
            if((element.countedQty - element.inventoryQuantity) != 0 || element.statusId == 74 || element.statusId == 75){
             if(element.cycleCountAction == null){
              element.cycleCountAction = "RECOUNT";
             }
              this.filteredVariance.push(element)
            }
          });
          this.dataSource = new MatTableDataSource(this.filteredVariance);
          this.pageTitle = this.pageflow;
          this.displayedColumns = ['action2s', 'no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stockTypeId', 'inventoryQuantity', 'countedQty', 'variance', 'cycleCounterId', 'statusId'];
 
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
      this.spin.hide();


    this.isAllSelected()
  };


  displayedColumns: string[] = ['select', 'no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stockTypeId', 'inventoryQuantity', 'cycleCounterId','countedQty', 'statusId'];

  //for confirm
  //displayedColumns: string[] = ['select', 'no', 'product', 'description', 'sku',  'section',  'bin','pallet', 'pack', 'stock', 'spl', 'inventory', 'counted', 'variance', 'by', 'date', 'status', 'remarks', 'actions'];
  dataSource = new MatTableDataSource<any>();
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
  // checkboxLabel(row?: clientcategory): string {
  //   if (!row) {
  //     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
  //   }
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  // }




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
  back() {
    this.location.back();
  }


  downloadexcel() {
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Product code ": x.itemCode,
        'Description': x.itemDesc,
        'Mfr Sku': x.manufacturerPartNo,
        "Section": x.storageSectionId,
        "Bin Location": x.storageBin,
        "Case Code": x.packBarcodes,
        "Pallet ID": x.packBarcodes,
        "Stock Type": x.stockTypeId,
        "Spl Stock Type": x.specialStockIndicator,
        "Inventory Qty": x.inventoryQuantity,
        "Counted Qty": x.countedQty,
        "Variance": x.variant,

        "User ID  ": x.cycleCounterId,

        // 'Created By': x.createdBy,
        // 'Date': this.cs.dateapi(x.createdOn),
      });

    })
    this.cs.exportAsExcel(res, "Perpetual Confirm");
  }


}

