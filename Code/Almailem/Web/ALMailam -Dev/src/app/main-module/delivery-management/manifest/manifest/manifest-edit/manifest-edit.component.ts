import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { DeliveryService } from '../../../delivery.service';
import { DeliverypopComponent } from '../../../pickup/deliverypop/deliverypop.component';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';

@Component({
  selector: 'app-manifest-edit',
  templateUrl: './manifest-edit.component.html',
  styleUrls: ['./manifest-edit.component.scss']
})
export class ManifestEditComponent implements OnInit {



  advanceFilterShow: boolean;
  @ViewChild('manifest') manifest: Table | undefined;
  manifesttable: any;
  selectedmanifest: any;
  displayedColumns: string[] = ['select', 'companyCodeId', 'description', 'plantId', 'createdBy', 'createdOn',];
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
    private router: Router,
    // private excel: ExcelService,
    private fb: FormBuilder,
    public auth: AuthService,
    private route: ActivatedRoute,
    private deliverService: DeliveryService,
  ) { }
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

  applyFilterGlobal($event: any, stringVal: any) {
    this.manifest!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  data: any;
  ngOnInit(): void {
    this.data = this.cs.decrypt(this.route.snapshot.params.code);
    this.getOutboundLines();
  }
  ELEMENT_DATA: any[] = [];


  @ViewChild(MatSort, { static: true })
  sort!: MatSort;
  @ViewChild(MatPaginator, { static: true })
  paginator!: MatPaginator; // Pagination
  // Pagination
  warehouseId = this.auth.warehouseId;

  getOutboundLines() {
    this.manifesttable = [];
    this.spin.show();
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.plantId = [this.auth.plantId];
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.deliveryNo = [this.data.code.deliveryNo];
    obj.statusId = [89, 93, 94];
    this.deliverService.searchDeliveryLine(obj).subscribe(res => {
      this.manifesttable = res;
      this.selectedmanifest = res;
      this.spin.hide();
    }, err => {
      this.spin.hide();
      this.cs.commonerrorNew(err);
    })
  }
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

  driverId: any;
  driverName: any;
  routeId: any;
  vehicleNumber: any;
  table = true;
  fullscreen = false;
  search = true;
  step = 0;
  back = false;
  updatePicker() {

    const dialogRef = this.dialog.open(DeliverypopComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '55%',
    });
    dialogRef.afterClosed().subscribe(result => {

      if (result) {
        this.data.code.driverId = result.driverId
        this.data.code.driverName = result.driverName
        this.data.code.routeId = result.routeId
        this.data.code.vehicleNo = result.vehicleNumber
        this.data.code.statusId = 90;

        this.selectedmanifest.forEach(element => {
          element.driverId = result.driverId
          element.driverName = result.driverName
          element.routeId = result.routeId
          element.vehicleNo = result.vehicleNumber
          element.statusId = 90;

        });

        this.sub.add(this.deliverService.updatedelivery(this.data.code, this.data.code.deliveryNo, this.auth.companyId, this.auth.plantId, this.auth.warehouseId, this.auth.languageId).subscribe(res => {
          this.spin.hide();
          // this.toastr.success(res.deliveryNo + " updated successfully.", "Notification", {
          //   timeOut: 2000,
          //   progressBar: false,
          // });
          this.sub.add(this.deliverService.updatedeliveryLine(this.selectedmanifest).subscribe(res => {
            this.spin.hide();
            this.toastr.success(" Driver assigned successfully.", "Notification", {
              timeOut: 2000,
              progressBar: false,
            })
            this.router.navigate(['/main/delivery/manifest']);
          }))
        })) 
      }

    });
  }


  deleteDialog() {
    if (this.selectedmanifest.length === 0) {
      this.toastr.error("Kindly select any row", "Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(DeleteComponent, {
      disableClose: true,
      width: '40%',
      maxWidth: '80%',
      position: { top: '9%', },
      data: this.selectedmanifest[0].plantId,
    });
    
    dialogRef.afterClosed().subscribe(result => {
    
      if (result) {
        this.deleterecord(this.selectedmanifest[0]);
      }
    });
    }
    
    
    deleterecord(id: any) {
    this.spin.show();
    this.sub.add(this.deliverService.Delete(id).subscribe((res) => {
      this.toastr.success(id + " Deleted successfully.","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.getOutboundLines();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
    }
}





