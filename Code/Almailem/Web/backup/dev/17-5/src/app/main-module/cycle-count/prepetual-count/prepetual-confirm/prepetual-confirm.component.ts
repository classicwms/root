import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AssignuserPopupComponent } from "./assignuser-popup/assignuser-popup.component";

import { Location } from "@angular/common";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { PrepetualCountService } from "../prepetual-count.service";
import { AssignPickerComponent } from "src/app/main-module/Outbound/assignment/assignment-main/assign-picker/assign-picker.component";
import { AuthService } from "src/app/core/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
// export interface clientcategory {
//   no: string;
//   sku: string;
//   section: string;
//   product: string;
//   description: string;
//   bin: string;
//   variant: string;
//   batch: string;
//   counted: string;
//   pallet: string;
//   stock: string;
//   spl: string;
//   inventory: string;
//   pack: string;
//   variance: string;
//   by: string;
//   date: string;
//   status: string;
//   remarks: string;
// }

// export interface PerpetualCount {
//   itemCode?: string;
//   itemDesc?: string;
//   manufacturerPartNo?: string;
//   storageSectionId?: string;
//   storageBin?: string;
//   approverCode?: string;
//   stockTypeId?: string;
//   specialStockIndicator?: number;
//   inventoryQuantity?: number;
// }

// const ELEMENT_DATA: clientcategory[] = [
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },
//   { no: "1", sku: 'readonly', section: 'readonly', product: 'readonly', description: 'readonly', bin: 'readonly', variant: 'readonly', batch: 'readonly', counted: 'dropdowm', pallet: 'readonly', stock: 'readonly', spl: 'readonly', inventory: 'readonly', by: 'readonly', date: 'readonly', pack: 'readonly', status: 'readonly', variance: 'readonly', remarks: 'readonly', },

// ];
@Component({
  selector: 'app-prepetual-confirm',
  templateUrl: './prepetual-confirm.component.html',
  styleUrls: ['./prepetual-confirm.component.scss']
})
export class PrepetualConfirmComponent implements OnInit {
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
  confirm(pageflow) {
    console.log(pageflow)
    let obj: any = {};
    obj = this.code;
    obj.updatePerpetualLine = this.code.perpetualLine;
    obj.perpetualLine = [];
    this.spin.show();
    if(pageflow == "Variance Analysis"){
      this.prepetualCountService.varienAnalysisConfirm(this.code.cycleCountNo, this.dataSource.data)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Prepetual Counted successfully", "Notification", {
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
    }else{
         this.prepetualCountService.varienAnalysisConfirm(this.code.cycleCountNo, this.selection.selected)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("Prepetual Counted successfully", "Notification", {
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
    // this.prepetualCountService.varienAnalysisConfirm(this.code.cycleCountNo, this.selection.selected)
    //   .subscribe(result => {
    //     console.log(result);
    //     this.toastr.success("Prepetual Counted successfully", "Notification", {
    //       timeOut: 2000,
    //       progressBar: false,
    //     });
    //     this.spin.hide();
    //     this.location.back();
    //   },
    //     error => {
    //       console.log(error);
    //       this.toastr.error(error.error, "Error", {
    //         timeOut: 2000,
    //         progressBar: false,
    //       });
    //       this.spin.hide();
    //     });
  }

  confirmNew(){
    let code = this.route.snapshot.params.code;
    let js = this.cs.decrypt(code);
    console.log(js)
    // this.prepetualCountService.updatePerpetualHeader(js.code.cycleCountNo, js.code.cycleCountTypeId, this.auth.warehouseId, js.code.movementTypeId,js.code.subMovementTypeId, {
    //   plantId: this.auth.plantId, languageId: this.auth.languageId,
    //   warehouseId: this.auth.warehouseId, cycleCountTypeId: js.code.cycleCountTypeId, companyCodeId: this.auth.companyId, cycleCountNo: js.code.cycleCountNo,  movementTypeId: this.code.movementTypeId,
    //   subMovementTypeId: this.code.subMovementTypeId, updatePerpetualLine: this.selection.selected
    // })
    if(js.pageflow == "Variance Analysis"){
     this.prepetualCountService.varienAnalysisConfirm(this.code.cycleCountNo, this.selection.selected)
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
      }else{
      this.prepetualCountService.updatePerpetualHeader(js.code.cycleCountNo, js.code.cycleCountTypeId, this.auth.warehouseId, js.code.movementTypeId,js.code.subMovementTypeId, {
      plantId: this.auth.plantId, languageId: this.auth.languageId,
      warehouseId: this.auth.warehouseId, cycleCountTypeId: js.code.cycleCountTypeId, companyCodeId: this.auth.companyId, cycleCountNo: js.code.cycleCountNo,  movementTypeId: this.code.movementTypeId,
      subMovementTypeId: this.code.subMovementTypeId, updatePerpetualLine: this.selection.selected
    })
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
  }
  save() {
    if (this.pageflow == 'Edit') {
      this.AssignHHt();
    }
    else {
      this.spin.show();
      this.prepetualCountService. Save({
        plantId: this.auth.plantId, languageId: this.auth.languageId,
        warehouseId: this.auth.warehouseId, cycleCountTypeId: 1, companyCodeId: this.auth.companyId, movementTypeId: this.code.movementTypeId[0],
        subMovementTypeId: this.code.subMovementTypeId[0], addPerpetualLine: this.dataSource.data
      })
        .subscribe(result => {
          console.log(result);
          this.toastr.success("Prepetual details created successfully", "Notification", {
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
  }
  AssignHHt() {
    this.spin.show();
    //this.prepetualCountService.SaveHHT(this.dataSource.data)
    this.prepetualCountService.SaveHHT(this.selection.selected)
      .subscribe(result => {
        console.log(result);
        this.toastr.success("User Assign successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });

        this.spin.hide();
        //this.location.back();
        this.router.navigate(['/main/cycle-count/Prepetual-main/count']);
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
  assign(): void {
    if (this.selection.selected.length === 0) {
      this.toastr.error("Kindly select one Row", "", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(AssignPickerComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
   if(result){
    this.selection.selected.forEach((x: any) => { x.cycleCounterId = result; });
    this.toastr.success("User Assign successfully", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });
    this.AssignHHt()
   }
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


    this.code = js.code;
    this.pageflow = js.pageflow;
    console.log(js)
    if (js.pageflow == 'New') {
      this.dataSource = new MatTableDataSource(JSON.parse(sessionStorage.getItem('RunPerpetualResponse') as '[]'));
      this.displayedColumns = ['no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stockTypeId', 'inventoryQuantity'];
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }
    else {
      this.filteredVariance = []
      this.spin.show();
      this.prepetualCountService.get(this.code).subscribe(result=>{
        console.log(result)
        this.code = result;
        console.log(result)

        this.code.perpetualLine.forEach(element => {
          if (element.inventoryQuantity == null) {
            element.inventoryQuantity = 0
          }
          if (element.statusId == 72) {
            element.countedQty = element.inventoryQuantity;
          }
        
        });
        if(js.pageflow == "Variance Analysis"){
          console.log("Variance Analysis")
          console.log(this.code.perpetualLine)
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
        }
        if(js.pageflow == "Count"){
          console.log("Count")
          this.code.perpetualLine.forEach(element => {
            if(element.cycleCounterId && element.statusId == 72){
              this.filterCountLines.push(element)
            }
          });
        this.dataSource = new MatTableDataSource(this.filterCountLines);
        this.displayedColumns = ['select', 'no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stockTypeId', 'inventoryQuantity', 'countedQty', 'variance','cycleCounterId', 'statusId'];
        }
       if(js.pageflow == "Edit"){
        console.log("Edit")
        this.dataSource = new MatTableDataSource(this.code.perpetualLine);
        this.displayedColumns = ['no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stockTypeId', 'inventoryQuantity', 'cycleCounterId','countedQty', 'statusId'];
       }
       if(js.pageflow == "Assign User"){
        this.code.perpetualLine.forEach(element => {
          if( element.statusId == 70 || element.statusId == 72 || element.statusId == 73){
            console.log(element)
            this.filteredAssignUser.push(element)
          }
        });
        this.dataSource = new MatTableDataSource(this.filteredAssignUser);
        this.pageTitle = this.pageflow;
        this.displayedColumns = ['select', 'no', 'itemCode', 'itemDesc', 'manufacturerPartNo', 'storageSectionId', 'storageBin', 'packBarcodes', 'stockTypeId', 'inventoryQuantity', 'cycleCounterId','statusId'];
      }
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
      this.spin.hide();

    }
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

