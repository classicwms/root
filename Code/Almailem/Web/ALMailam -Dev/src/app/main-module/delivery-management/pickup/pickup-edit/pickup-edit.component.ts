import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { DeliveryService } from '../../delivery.service';
import { DeleteComponent } from 'src/app/common-field/delete/delete.component';

@Component({
  selector: 'app-pickup-edit',
  templateUrl: './pickup-edit.component.html',
  styleUrls: ['./pickup-edit.component.scss']
})
export class PickupEditComponent implements OnInit {

  advanceFilterShow: boolean;
  @ViewChild('pickup') pickup: Table | undefined;
  pickuptable: any;
  selectedpickup: any;
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
    this.pickup!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
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
    this.pickuptable = [];
    this.spin.show();
    let obj: any = {};
   // obj.companyCodeId = [this.auth.companyId];
    //obj.plantId = [this.auth.plantId];
    obj.languageId = [this.auth.languageId];
    obj.warehouseId = [this.auth.warehouseId];
    obj.deliveryNo = [this.data.code.deliveryNo];
    obj.refDocNumber = [this.data.code.refDocNumber];
    obj.statusId = [90];

    this.deliverService.searchDeliveryLine(obj).subscribe(res => {
      this.pickuptable = res;
      this.selectedpickup = res;
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

  confirm() {
    this.data.code.statusId = 91;

    this.selectedpickup.forEach(element => {
      element.statusId = 91;     
      element.picked = true;   
    });

      this.sub.add(this.deliverService.updatedelivery(this.data.code, this.data.code.deliveryNo, this.auth.companyId, this.auth.plantId, this.auth.warehouseId, this.auth.languageId).subscribe(res => {
        this.spin.hide();
        this.sub.add(this.deliverService.updatedeliveryLine(this.selectedpickup).subscribe(res => {
          this.spin.hide();
          this.toastr.success(" Driver assigned successfully.", "Notification", {
            timeOut: 2000,
            progressBar: false,
          })
          this.router.navigate(['/main/delivery/pickup']);
        }))
      }));
  }



    
  deleteDialog() {
    if (this.selectedpickup.length === 0) {
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
      data: this.selectedpickup[0].plantId,
    });
    
    dialogRef.afterClosed().subscribe(result => {
    
      if (result) {
        this.deleterecord(this.selectedpickup[0]);
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

  onChange(line, e){
 //   line.picked = true;
  }
}





