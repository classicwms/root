import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { Floor } from 'src/app/models/floor';
import { MasterService } from 'src/app/shared/master.service';
import { FloorService } from './floor.service';

export class FloorCls implements Floor {
  constructor(public companyId?: string, public createdBy?: string, public createdOn?: Date,
    public deletionIndicator?: number, public floorId?: number, public languageId?: string,
    public plantId?: string, public updatedBy?: string, public updatedOn?: Date, public warehouseId?: string) {
  }
}

@Component({
  selector: 'app-floor',
  templateUrl: './floor.component.html',
  styleUrls: ['./floor.component.scss']
})
export class FloorComponent implements OnInit {
  title1 = "Organisation Setup";
  title2 = "Floor";
  floorIdForEdit?: any;
  storageDetails?: any;
  floor = new FloorCls();
  floorName = '';
  companyList: any[] = [];
  plantList: any[] = [];
  warehouseList: any[] = [];
  warehouseList1: any[] = [];
  floorList: any[] = [];
  floorList1: any[] = [];
  showingFloorList: any[] = [];
  showingFloorList1: any[] = [];

  constructor(private floorService: FloorService, private route: ActivatedRoute,  private cs: CommonService,
    public toastr: ToastrService, private router: Router, private masterService: MasterService, private spin: NgxSpinnerService) {
    this.floor.companyId = this.route.snapshot.params['companyId'];
    this.floor.plantId = this.route.snapshot.params['plantId'];
    this.floor.warehouseId = this.route.snapshot.params['warehouseId'];
    // this.floor.floorId = this.route.snapshot.params['floorId'];
    // this.floorIdForEdit = this.floor.floorId;

    if (sessionStorage.getItem('storageSection') != null && sessionStorage.getItem('storageSection') != undefined) {
      this.storageDetails = JSON.parse(sessionStorage.getItem('storageSection') as '{}');
      console.log(this.storageDetails);
      this.floor.floorId = this.storageDetails.floorId;
      this.floor.companyId = this.storageDetails.companyId;
      this.floor.plantId = this.storageDetails.plantId;
      this.floor.warehouseId = this.storageDetails.warehouseId;
      this.floorIdForEdit = this.floor.floorId;
    }

    if (this.floor.floorId !== undefined && this.floor.floorId !== null) {
      this.getFloorDetails();
    }
  }

  ngOnInit(): void {
    this.dropdownfill();
  }

  dropdownfill() {
    this.spin.show();
    forkJoin({
      // company: this.masterService.getCompanyMasterDetails().pipe(catchError(err => of(err))),
      company: this.masterService.getCompanyEnterpriseDetails().pipe(catchError(err => of(err))),
      //city: this.masterService.getCityMasterDetails().pipe(catchError(err => of(err))),
      //currency: this.masterService.getCurrencyMasterDetails().pipe(catchError(err => of(err))),
      //state: this.masterService.getStateMasterDetails().pipe(catchError(err => of(err))),
      //country: this.masterService.getCountryMasterDetails().pipe(catchError(err => of(err))),
      // plant: this.masterService.getPlantMasterDetails().pipe(catchError(err => of(err))),
      plant: this.masterService.getPlantEnterpriseDetails().pipe(catchError(err => of(err))),
      // warehouse: this.masterService.getWarehouseMasterDetails().pipe(catchError(err => of(err))),
      warehouse: this.masterService.getWarehouseEnterpriseDetails().pipe(catchError(err => of(err))),
      floor: this.masterService.getFloorMasterDetails().pipe(catchError(err => of(err))),

      //vertical: this.masterService.getVerticalMasterDetails().pipe(catchError(err => of(err))),

    })
      .subscribe(({ company, plant, warehouse, floor }) => {

        this.companyList = company;
        this.plantList = plant;

        this.warehouseList1 = warehouse;
        this.warehouseList1.forEach((x: { warehouseId: string; description: string; }) => this.warehouseList.push({key: x.warehouseId, value:  x.warehouseId}))
        this.warehouseList = this.cs.removeDuplicatesFromArraydropdown(this.warehouseList);

        this.floorList1 = floor;
        this.floorList1.forEach((x: { floorId: string; description: string; }) => this.floorList.push({key: x.floorId, value:  x.floorId + '-' + x.description}))
        this.floorList = this.cs.removeDuplicatesFromArraydropdown(this.floorList);

        this.filterFloor();
        this.changeFloorName();
        //this.floorList = [{floorId: 1, description: 'First floor'}, {floorId: 2, description: 'Second floor'}]
      }, (err) => {
        this.toastr.error(
          err,
          ""
        );
      });
    this.spin.hide();

  }

  filterFloor() {
    this.showingFloorList1 = this.floorList1.filter(element => {
      return element.companyCodeId === this.floor.companyId && element.plantId === this.floor.plantId && element.warehouseId === this.floor.warehouseId;
    });
    
    this.showingFloorList1.forEach((x: { floorId: string; description: string; }) => this.showingFloorList.push({key: x.floorId, value:  x.floorId + '-' + x.description}))
    this.showingFloorList = this.cs.removeDuplicatesFromArraydropdown(this.showingFloorList);
  }

  getFloorDetails() {
    this.floorService.getFloorDetails(this.floor.floorId).subscribe(
      result => {
        this.floor = result;
      },
      error => {
        console.log(error);
      }
    );
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

  changeFloorName() {
    let singleWarehouse = this.floorList.filter(x => x.floorId == this.floor.floorId);
    console.log(singleWarehouse);
    this.floorName = singleWarehouse[0].description;
  }

  onSubmit() {
    // let element: HTMLElement = document.getElementById('btnsave') as HTMLElement;
    // element.click();

    //this.isLoadingResults = true;
    //this.userDetails.instance_id = this.instance.id;
    if ((this.floor.companyId != null && this.floor.companyId != undefined) &&
      (this.floor.plantId != null && this.floor.plantId != undefined) &&
      (this.floor.warehouseId != null || this.floor.warehouseId != undefined) &&
      (this.floor.floorId != null || this.floor.floorId != undefined)) {
      if (!this.floorIdForEdit) {
        this.saveFloorDetails();
      }
      else {
        this.updateFloorDetails();
      }
    }
    else {
      this.toastr.error("Please fill the required fields");
    }
  }

  saveFloorDetails() {
    console.log(this.floor);
    //this.warehouse.zipCode = Number(this.warehouse.zipCode);
    this.floor.languageId = 'en';
    this.floorService.saveFloorDetails(this.floor).subscribe(
      result => {
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);

        this.toastr.success("Floor details Saved Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/storage', this.floor.companyId, this.floor.plantId, this.floor.warehouseId, this.floor.floorId]);
      },
      error => {
        //this.isLoadingResults = false;
        this.toastr.error(error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      }
    );
  }

  updateFloorDetails() {
    console.log(this.floor);
    this.floor.languageId = 'en';
    this.floorService.updateFloorDetails(this.floor).subscribe(
      result => {
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        //this.router.navigate(['administration/user/list']);

        this.toastr.success("Floor details updated successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/setup/storage', this.floor.companyId, this.floor.plantId, this.floor.warehouseId, this.floor.floorId]);
      },
      error => {
        //this.isLoadingResults = false;
        this.toastr.error(error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      }
    );
  }
}
