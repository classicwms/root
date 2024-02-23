

  import { SelectionModel } from "@angular/cdk/collections";
  import { Component, Inject, OnInit, ViewChild } from "@angular/core";
  import { FormBuilder } from "@angular/forms";
  import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
  import { MatPaginator } from "@angular/material/paginator";
  import { MatSort } from "@angular/material/sort";
  import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
  import { NgxSpinnerService } from "ngx-spinner";
  import { ToastrService } from "ngx-toastr";
  import { Subscription } from "rxjs";
  import { DeleteComponent } from "src/app/common-field/delete/delete.component";
  import { CommonService, dropdownelement } from "src/app/common-service/common-service.service";
  import { AuthService } from "src/app/core/core";
import { DashboardService } from "../../dashboard.service";
import { Location } from "@angular/common";

  @Component({
    selector: 'app-fast-slow-list',
    templateUrl: './fast-slow-list.component.html',
    styleUrls: ['./fast-slow-list.component.scss']
  })
  export class FastSlowListComponent implements OnInit {
    displayedColumns: string[] = ['type', 'itemCode','itemText', 'deliveryQuantity', ];
    sub = new Subscription();
    ELEMENT_DATA: any[] = [];
    isShowDiv = false;
    showFloatingButtons: any;
    toggle = true;
    public icon = 'expand_more';
    constructor(public dialog: MatDialog,
      private service: DashboardService,
     // private cas: CommonApiService,
      public toastr: ToastrService,
      private spin: NgxSpinnerService,
      public dialogRef: MatDialogRef<any>,
      @Inject(MAT_DIALOG_DATA) public data: any,
      private route: ActivatedRoute,
      public cs: CommonService,
      private location: Location,
     // private excel: ExcelService,
      private fb: FormBuilder,
      private auth: AuthService) { }
    showFiller = false;
    animal: string | undefined;
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
  
      this.dataSource.filter = filterValue.trim().toLowerCase();
  
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }

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
    


  storecodeList: any;
  currentDate = new Date();
  fifteenDaysDate = new Date();
  ondeDaysBeforeDate = new Date();
  
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
  
    ngOnInit(): void {
   this.form.controls.fromDate.patchValue(this.data.fromDate)
   this.form.controls.toDate.patchValue(this.data.toDate)
     this.getAll();
    }
  
    dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
    selection = new SelectionModel<any>(true, []);
  

    form = this.fb.group({
      fromDate: [, ],
      toDate: [, ],
      warehouseId: ["110", ],
    });

    @ViewChild(MatSort, { static: true })
    sort!: MatSort;
    @ViewChild(MatPaginator, { static: true })
    paginator!: MatPaginator; // Pagination
   // Pagination
  warehouseId = this.auth.warehouseId
  Result: any[] = [];
   getAll() {
    this.spin.show();
    
    this.sub.add(this.service.fastSlow(this.form.getRawValue()).subscribe(res => {
      res.forEach(element => {
                    if( element.type == "FAST"){
                      element.type = '1 - FAST'
                    }
                    if( element.type == "AVERAGE"){
                      element.type = '2 - AVERAGE'
                    }
                    if( element.type == "SLOW"){
                      element.type = '3 - SLOW'
                    }
        this.Result.push(element)
      });
      this.dataSource = new MatTableDataSource<any>(res);
      this.selection = new SelectionModel<any>(true, []);
      this.dataSource.sort = this.sort;
     this.dataSource.paginator = this.paginator;
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }

  
  
  

  


  


    back() {
      this.location.back();
    }

    
  downloadexcel() {
    // if (excel)
    var res: any = [];
    this.dataSource.data.forEach(x => {
      res.push({

        "Type": x.type,
        "Part No": x.itemCode,
        "Description": x.itemText,
        "Qty": x.deliveryQuantity,
      });

    })
    this.cs.exportAsExcel(res, "Turn Over Rates");
  }
  }