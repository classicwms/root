import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PrepetualCountService } from "../prepetual-count.service";
import { CreatePopupComponent } from "./create-popup/create-popup.component";
import { Table } from "primeng/table";

// export interface  variant {


//   no: string;
//   actions:  string;
//   status:  string;
//   warehouseno:  string;
//   preinboundno:  string;
//   refdocno:  string;
//   type:  string;
// }
// const ELEMENT_DATA:  variant[] = [
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },
//   { no: "readonly",warehouseno:  'readonly',type:  'readonly',refdocno:  'readonly',preinboundno:  'readonly',  status: 'readonly' ,actions: 's' },

// ];


// export interface PerpetualCount {
//   cycleCountNo?: string;
//   movementTypeId?: number;
//   subMovementTypeId?: number;
//   statusId?: string;
//   warehouseId?: string;
//   createdBy?: string;
//   createdOn?: Date;
// }

@Component({
  selector: 'app-prepetual-main',
  templateUrl: './prepetual-main.component.html',
  styleUrls: ['./prepetual-main.component.scss']
})
export class PrepetualMainComponent implements OnInit {
  screenid: 1071 | undefined;
  constructor(public cs: CommonService, public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router, private route: ActivatedRoute, private auth: AuthService,
    private spinner: NgxSpinnerService, private prepetualCountService: PrepetualCountService,) {

    // route.params.subscribe(val => {
    //   debugger
    //   this.ngOnInit();
    // });
  }

  perpetual: any[] = [];
  selectedperpetual : any[] = [];
  @ViewChild('perpetualTag') perpetualTag: Table | any;

  create(): void {

    const dialogRef = this.dialog.open(CreatePopupComponent, {
      disableClose: true,
      width: '80%',
      maxWidth: '50%',
      position: { top: '9%', },

    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  pageflow: any;


  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.pageflow = code == 'count' ? 'Perpetual Creation' : 'Variance Analysis';
    /** spinner starts on init */
    this.spinner.show();
      this.getPerpetualCountList();

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 500);
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


  downloadexcel() {
    var res: any = [];
    this.perpetual.forEach(x => {
      res.push({
         "Branch":x.plantDescription,
         "Warehouse":x.warehouseDescription,
        "Status":x.statusDescription,
        "AMS Cycle Count":x.referenceCycleCountNo,
        "Cycle Count No":x.cycleCountNo,
        "Created On":this.cs.dateapi(x.createdOn),
        "Created By":x.createdBy,
       
      });
  
    })
    this.cs.exportAsExcel(res, "Perpetual Create");
  }


  openDialog(data: any = 'New', linedata: any = null): void {
    if (data != 'New' && data != 'Edit' && linedata == null) {
       if (this.selectedperpetual.length === 0) {
          this.toastr.error("Kindly select any row", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          return;
        }

      }
      if (data == 'Assign User' || data == 'Count') {
        this.selectedperpetual = [];
        this.selectedperpetual.push(linedata);
      }
      if (data == 'Edit') {
         if (this.selectedperpetual.length === 0) {
            this.toastr.error("Kindly select any row", "Notification", {
              timeOut: 2000,
              progressBar: false,
            });
            return;
         }
         console.log(2)
        }


        if (data == 'LineEdit') {
          this.selectedperpetual = [];
          this.selectedperpetual.push(linedata);
          data = 'Edit'
        }
    if (this.selectedperpetual[0].statusId == 78 && data != 'Edit') {
      this.toastr.error("Stock count confirmed can't be edited", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }

    let paramdata = "";

    if (data != 'New') {
      if(data == 'Count'){
        let obj: any = {};
        obj.warehouseId = [this.auth.warehouseId];
        obj.companyCodeId = [this.auth.companyId];
        obj.languageId = [this.auth.languageId];
        obj.plantId = [this.auth.plantId];
        obj.cycleCountNo = [linedata.cycleCountNo];
        obj.lineStatusId = [72, 75];
        
        this.prepetualCountService.findPerpetualSpark(obj).subscribe(res => {
        
          if(res.length == 0){
              this.toastr.error(
                "Please assign counter first",
                "Notification"
              );
              return;
          } 
          else{
            let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedperpetual[0] : linedata, pageflow: this.pageflow = data });
            this.router.navigate(['/main/cycle-count/Prepetual-confirm/' + paramdata]);
          }
        })
      }
      else{
        let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedperpetual[0] : linedata, pageflow: this.pageflow = data });
        this.router.navigate(['/main/cycle-count/Prepetual-confirm/' + paramdata]);
      }
    }
    else {
      let paramdata = this.cs.encrypt({ code: linedata == null ? this.selectedperpetual[0] : linedata, pageflow: this.pageflow == 'Variance Analysis' ? 'Variance Analysis' : data });
      this.router.navigate(['/main/cycle-count/Prepetual-confirm/' + paramdata]);
    }
  }

  getPerpetualCountList() {
    this.spin.show();
    this.perpetual = [];
    let data: any = { 
      warehouseId: [this.auth.warehouseId],
      companyCodeId: [this.auth.companyId],
      languageId: [this.auth.languageId],
      plantId: [this.auth.plantId], }
    if (this.pageflow == 'Variance Analysis') {
      data.headerStatusId = [73, 74, 78];
    }
    this.prepetualCountService.getPerpetualCountList1(data).subscribe(
      result => {
        this.spin.hide();
        this.perpetual = result;
      },
      error => {
        this.spin.hide();
        if (error.status == 415) {
          this.getPerpetualCountList();
        }
      }
    );
  }

  onChange() {
    const choosen= this.selectedperpetual[this.selectedperpetual.length - 1];   
    this.selectedperpetual.length = 0;
    this.selectedperpetual.push(choosen);
  }
}
