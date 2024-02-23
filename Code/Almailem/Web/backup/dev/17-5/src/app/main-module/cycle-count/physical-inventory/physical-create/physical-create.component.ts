import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PhysicalInventoryService } from "../physical-inventory.service";
import { AssignPhysicalComponent } from "./assign-physical/assign-physical.component";
import { InhouseTransferService } from "src/app/main-module/make&change/inhouse-transfer/inhouse-transfer.service";
@Component({
  selector: 'app-physical-create',
  templateUrl: './physical-create.component.html',
  styleUrls: ['./physical-create.component.scss']
})
export class PhysicalCreateComponent implements OnInit {
  screenid: 1080 | undefined;
  isShown: boolean = false; // hidden by default
  animal: string | undefined;
  name: string | undefined;
  data: any = {};
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;

  displayedColumns: string[] = ['select', 'no', 'product', 'referenceField8', 'referenceField9', 'referenceField10', 'bin', 'pallet', 'stock', 'inventory', 'uom', 'user',];
  dataSource = new MatTableDataSource<any>();
  selection = new SelectionModel<any>(true, []);

  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination

  pageNumber = 0;
  pageSize = 100;
  totalRecords = 0;

  constructor(
    public dialog: MatDialog,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    public auth: AuthService,
    public router: Router,
    private route: ActivatedRoute,
    private inventoryService: InhouseTransferService,
    public periodicService: PhysicalInventoryService) { }

  create(): void {
    const dialogRef = this.dialog.open(AssignPhysicalComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  ngOnInit(): void {
    this.data = this.cs.decrypt(this.route.snapshot.params.data);
    console.log(this.data)
    
    if(this.data.pageflow == 'New'){
      console.log(2)
      this.displayedColumns = ['no', 'product', 'referenceField8', 'referenceField9', 'referenceField10', 'bin', 'pallet', 'stock', 'inventory', 'uom', 'user',];
    }
    this.runPeriodicHeader();
    
  }

  pageHandler($event: PageEvent) {
    this.pageNumber = $event.pageIndex;
    this.pageSize = $event.pageSize;
    this.runPeriodicHeader();
  }

  runPeriodicHeader() {
    let storageSectionIds: any[] = [];
    // this.data.selectedStorageSection.forEach(element => {
    //   storageSectionIds.push(element);
    // });
    this.spin.show();
  //  this.periodicService.runPeriodicHeaderNew(this.data.selectedWareHouse, 'itemCode', this.pageNumber, this.pageSize).subscribe(
    //this.periodicService.runPeriodicHeader(this.data.selectedWareHouse, storageSectionIds).subscribe(
      this.inventoryService.GetInventory(this.data.obj).subscribe(
      result => {

        if (result.length <= 0) {
          this.toastr.error(
            "No Skus available for the selection",
            "Notification",{
              timeOut: 2000,
              progressBar: false,
            }
          )
          this.spin.hide();
          this.router.navigate(['/main/cycle-count/physical-main'])
          return;
        }

        this.spin.hide();
        result.forEach(element => {
          element.warehouseId = this.data.obj.warehouseId[0];
        });
        console.log(result);
        this.dataSource = new MatTableDataSource(result);
        this.dataSource.paginator = this.paginator;
        this.totalRecords = result.totalElements;
      },
      error => {
        this.spin.hide();
        this.toastr.error(
          "Error",
          "Notification"
        );
      }
    );
  }

  createPeriodicHeader() {
    console.log(this.dataSource.data[0].warehouseId)
    let createObject: any = {};
    createObject = this.dataSource.data[0];
    createObject.periodicLine = [];
    createObject.warehouseId = this.dataSource.data[0].warehouseId;
    this.dataSource.data.forEach(data => {
      let lineObj: any = {};
      lineObj.companyCode = data.companyCodeId;
      lineObj.inventoryQuantity = data.inventoryQuantity;
      lineObj.inventoryUom = data.inventoryUom;
      lineObj.itemCode = data.itemCode;
      lineObj.packBarcodes = data.packBarcodes;
      lineObj.plantId = data.plantId;
      lineObj.specialStockIndicator = data.specialStockIndicator;
      lineObj.stockTypeId = data.stockTypeId;
      lineObj.storageBin = data.storageBin;
      lineObj.varianceQty = data.varianceQty;
      lineObj.warehouseId = data.warehouseId;
      createObject.periodicLine.push(lineObj);
    })
    this.spin.show();
    this.periodicService.createPeriodicHeader(createObject).subscribe(
      result => {
        this.spin.hide();
        this.toastr.success(
          result.cycleCountNo + "Created Successfully",
          "Notification"
        );
        this.router.navigate(['/main/cycle-count/physical-main'])
      },
      error => {
        this.spin.hide();
        this.toastr.error(
          "Error",
          "Notification"
        );
      }
    );
  }

  toggleShow() { this.isShown = !this.isShown; }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
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
  checkboxLabel(row?: any): string {
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

