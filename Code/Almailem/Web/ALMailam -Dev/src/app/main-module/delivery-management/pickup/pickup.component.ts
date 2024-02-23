import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PlantService } from '../../setup-organisation/plant/plant.service';
import { ConsignmentsOpenComponent } from '../consignments/consignments-open/consignments-open.component';
import { DeliveryService } from '../delivery.service';
import { Router } from '@angular/router';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
import { UpdateLineComponent } from '../update-line/update-line.component';

@Component({
  selector: 'app-pickup',
  templateUrl: './pickup.component.html',
  styleUrls: ['./pickup.component.scss']
})
export class PickupComponent implements OnInit {
screenid=3211;
  advanceFilterShow: boolean;
  @ViewChild('Setupinvoice') Setupinvoice: Table | undefined;
  invoices: any;
  selectedinvoice : any;
  displayedColumns: string[] = ['select','companyCodeId', 'description', 'plantId','createdBy', 'createdOn', ];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private service: DeliveryService, ) { }
  toggleFloat() {
  
    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;
  
    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
  
  }
  showFiller = false;
  animal: string | undefined;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
  
    this.dataSource.filter = filterValue.trim().toLowerCase();
  
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  applyFilterGlobal($event: any, stringVal: any) {
    this.Setupinvoice!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  ngOnInit(): void {
    this.getAll();
  }
  ELEMENT_DATA: any[] = [];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);
  
  
  
  
  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId;
  
  
  
  
  
  
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
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.handlingUnit + 1}`;
  }
  
  
  
  
  
  
  clearselection(row: any) {
  
    this.selection.clear();
    this.selection.toggle(row);
  }
  
  openDialog(data: any = 'New'): void {
    console.log(this.Setupinvoice)
      if (data != 'New')
      if (this.selectedinvoice.length === 0) {
        this.toastr.warning("Kindly select any Row", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        return;
      }
      const dialogRef = this.dialog.open(ConsignmentsOpenComponent, {
        disableClose: true,
        width: '55%',
        maxWidth: '80%',
        data: { pageflow: data, code: data != 'New' ? this.selectedinvoice[0].plantId : null}
      });
    
      dialogRef.afterClosed().subscribe(result => {
        this.getAll();
      });
    }
    getAll() {
      this.spin.show();
      let obj: any = {};
     // obj.companyCodeId = [this.auth.companyId];
      obj.languageId = [this.auth.languageId];
    //  obj.plantId = [this.auth.plantId];
      obj.warehouseId = [this.auth.warehouseId];
      obj.statusId = [90];
     
      this.sub.add(this.service.searchDeliveryLine(obj).subscribe((res: any[]) => {
        if(res){
          let result = res.filter(item => item.salesdeliveryNumber !== null);
          this.lineResult = res;
          this.invoices = this.cs.removeDuplicatesFromArrayList(result, 'refDocNumber');
      //   this.dataSource = new MatTableDataSource<any>(res);
      //   this.selection = new SelectionModel<any>(true, []);
      //   this.dataSource.sort = this.sort;
      //  this.dataSource.paginator = this.paginator;
        }this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
      }
      downloadexcel() {
        var res: any = [];
        this.invoices.forEach(x => {
          res.push({
            "Company":x.companyDescription,
            "Branch":x.plantDescription,
            "Warehouse ": x.warehouseDescription,
            'Delivery No': x.deliveryNo,
            'Route Id': x.routeId,
            "Driver": x.driverName,
            "Vechile": x.vechicleNo,
            "Status":x.statusDescription,
            "Created On":this.cs.dateExcel(x.createdOn),
      
            // 'Created By': x.createdBy,
            // 'Date': this.cs.dateapi(x.createdOn),
          });
        
        })
        this.cs.exportAsExcel(res, "Pickup");
      }

  confirm(linedata: any = null): void {  
    let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedinvoice[0] : linedata });
    this.router.navigate(['/main/delivery/pickupEdit/' + paramdata]);
    console.log(linedata);
  }
  
  lineResult: any[] = [];
  update(element): void {
    const dialogRef = this.dialog.open(UpdateLineComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
      position: { top: '6.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        result.forEach(x => this.lineResult.push(x));
        this.lineResult.filter(x => x.deliveryNo = this.lineResult[0].deliveryNo)
        this.sub.add(this.service.updatedeliveryLine(this.lineResult).subscribe(res => {
          this.spin.hide();
          this.toastr.success("Updated successfully.", "Notification", {
            timeOut: 2000,
            progressBar: false,
          })
          this.router.navigate(['/main/delivery/manifest']);
        }))
      }
    });
  }

  }
  
  
  
  
  
