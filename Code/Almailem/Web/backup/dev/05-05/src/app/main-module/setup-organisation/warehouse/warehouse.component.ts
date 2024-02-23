
  import { Component, OnInit, ViewChild } from '@angular/core';
  import { FormBuilder } from '@angular/forms';
  import { MatDialog } from '@angular/material/dialog';
  import { Router } from '@angular/router';
  import { NgxSpinnerService } from 'ngx-spinner';
  import { ToastrService } from 'ngx-toastr';
  import { Table } from 'primeng/table';
  import { Subscription } from 'rxjs';
  import { DeleteComponent } from 'src/app/common-field/delete/delete.component';
  import { CommonService } from 'src/app/common-service/common-service.service';
  import { AuthService } from 'src/app/core/core';
import { WarehouseService } from './warehouse.service';
  
  @Component({
    selector: 'app-warehouse',
    templateUrl: './warehouse.component.html',
    styleUrls: ['./warehouse.component.scss']
  })
  export class WarehouseComponent implements OnInit {

    advanceFilterShow: boolean;
    @ViewChild('setupWarehouse') setupWarehouse: Table | undefined;
    company: any[] = [];
    selectedWarehouse : any[] = [];
    sub = new Subscription();
    isShowDiv = false;
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
  
    ELEMENT_DATA: any[] = [];
    
    constructor(public dialog: MatDialog,
      public toastr: ToastrService,
      private spin: NgxSpinnerService,
      private router: Router,
      public cs: CommonService,
     // private excel: ExcelService,
      private fb: FormBuilder,
      private auth: AuthService,
      private service: WarehouseService) { }
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
        this.setupWarehouse!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
      }
      
      ngOnInit(): void {
        this.getAll();
      }
  
      warehouseId = this.auth.warehouseId;
      /** Whether the number of selected elements matches the total number of rows. */
     
    openDialog(data: any = 'New'): void {
      if (data != 'New'){
        if (this.selectedWarehouse.length == 0) {
          this.toastr.warning("Kindly select any Row", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          return;
        }
      }
      let paramdata = "";
      paramdata = this.cs.encrypt({ pageflow: data, code: data != 'New' ? this.selectedWarehouse[0].warehouseId : null,modeOfImplementation: data != 'New' ? this.selectedWarehouse[0].modeOfImplementation : null,warehouseTypeId: data != 'New' ? this.selectedWarehouse[0].warehouseTypeId : null, companyId: data != 'New' ? this.selectedWarehouse[0].companyId : null,plantId: data != 'New' ? this.selectedWarehouse[0].plantId: null,languageId: data != 'New' ? this.selectedWarehouse[0].languageId: null});
      this.router.navigate(['/main/organisationsetup/warehouseNew/' + paramdata]);
    }
    
    getAll() {
      this.spin.show();
      this.sub.add(this.service.Getall().subscribe((res: any[]) => {
        console.log(res)
  if(res){
  
      this.company = res;
  }
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
    deleteDialog() {
      if (this.selectedWarehouse.length === 0) {
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
        data: this.selectedWarehouse[0].code,
      });
    
      dialogRef.afterClosed().subscribe(result => {
    
        if (result) {
          this.deleterecord(this.selectedWarehouse[0].warehouseId,this.selectedWarehouse[0].modeOfImplementation,this.selectedWarehouse[0].warehouseTypeId,this.selectedWarehouse[0].companyId,this.selectedWarehouse[0].plantId,this.selectedWarehouse[0].languageId);
    
        }
      });
    }
    
    
    deleterecord(id: any,modeOfImplementation:any,warehouseTypeId:any,companyId:any,plantId:any,languageId:any) {
      this.spin.show();
      this.sub.add(this.service.Delete(id,modeOfImplementation,warehouseTypeId,companyId,plantId,languageId).subscribe((res) => {
        this.toastr.success(id + " Deleted successfully.","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.getAll();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }
    downloadexcel() {
      var res: any = [];
      this.company.forEach(x => {
        res.push({
        
          "Language Id":x.languageId,
          "Company":x.companyIdAndDescription,
          "Plant ":x.plantIdAndDescription,
          "Floor":x.floorIdAndDescription,
          "Warehouse Id":x.warehouseId,
          "Description":x.description,
          "Created By":x.createdBy,
         "Created One":this.cs.dateapi(x.createdOn),
        });
    
      })
      this.cs.exportAsExcel(res, "Warehouse");
    }
    onChange() {
      const choosen= this.selectedWarehouse[this.selectedWarehouse.length - 1];   
      this.selectedWarehouse.length = 0;
      this.selectedWarehouse.push(choosen);
    } 
    }
     
    
    
    
  