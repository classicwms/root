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
import { InvoicelinesNewComponent } from './invoicelines-new/invoicelines-new.component';
import { InvoicelinesService } from './invoicelines.service';

@Component({
  selector: 'app-invoicelines',
  templateUrl: './invoicelines.component.html',
  styleUrls: ['./invoicelines.component.scss']
})
export class InvoicelinesComponent implements OnInit {

  advanceFilterShow: boolean;
  @ViewChild('Setupinvoice') Setupinvoice: Table | undefined;
  invoice: any;
  selectedinvoicel : any;
 
  displayedColumns: string[] = ['select','doorId','doorDescription', 'createdBy','createdOn'];
  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  ELEMENT_DATA: any[] = [];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  selection = new SelectionModel<any>(true, []);
  
  constructor(public dialog: MatDialog,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,
    private service: InvoicelinesService) { }
  ngOnInit(): void {
    throw new Error('Method not implemented.');
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
    // ngOnInit(): void {
    //   //this.getAll(

    //   );
    // }

  
  
  
  
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
  console.log(this.selectedinvoicel)
    if (data != 'New')
    if (this.selectedinvoicel.length === 0) {
      this.toastr.warning("Kindly select any Row", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
      return;
    }
    const dialogRef = this.dialog.open(InvoicelinesNewComponent, {
      disableClose: true,
      width: '85%',
      maxWidth: '65%',
      data: { pageflow: data, code: data != 'New' ? this.selectedinvoicel[0].code : null,}
    });
  
    dialogRef.afterClosed().subscribe(result => {
     // this.getAll();
    });
  }
//   getAll() {
//     this.spin.show();
//     this.sub.add(this.service.Getall().subscribe((res: any[]) => {
//       console.log(res)
// if(res){
//    this.pallets = res;
// //   this.dataSource = new MatTableDataSource<any>(res);
// //   this.selection = new SelectionModel<any>(true, []);
// //   this.dataSource.sort = this.sort;
// //  this.dataSource.paginator = this.paginator;
// }
//       this.spin.hide();
//     }, err => {
//       this.cs.commonerrorNew(err);
//       this.spin.hide();
//     }));
//   }
  // deleteDialog() {
  //   if (this.selectedpallets.length === 0) {
  //     this.toastr.error("Kindly select any row", "Notification",{
  //       timeOut: 2000,
  //       progressBar: false,
  //     });
  //     return;
  //   }
  //   const dialogRef = this.dialog.open(DeleteComponent, {
  //     disableClose: true,
  //     width: '40%',
  //     maxWidth: '80%',
  //     position: { top: '9%', },
  //     data: this.selectedpallets[0].code,
  //   });
  
  //   dialogRef.afterClosed().subscribe(result => {
  
  //     if (result) {
  //       this.deleterecord(this.selectedpallets.palletizationLevelId,this.selectedpallets.palletizationLevel);
  
  //     }
  //   });
  // }
  
  
  // deleterecord(id: any,palletizationLevel) {
  //   this.spin.show();
  //   this.sub.add(this.service.Delete(id,this.auth.warehouseId,palletizationLevel).subscribe((res) => {
  //     this.toastr.success(id + " Deleted successfully.","Notification",{
  //       timeOut: 2000,
  //       progressBar: false,
  //     });
  //     this.spin.hide();
  //     this.getAll();
  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();
  //   }));
  // }
  // downloadexcel() {
  //   var res: any = [];
  //   this.pallets.forEach(x => {
  //     res.push({
  //       "Palletization Level ID":x.palletizationLevelId,
  //      "Palletization Level":x.palletizationLevel,
  //      "Palletization Level Reference":x.palletizationLevelReference,
  //      "Created By":x.createdBy,
  //      "Created One":this.cs.dateapi(x.createdOn),
      
  //     });
  
  //   })
  //   this.cs.exportAsExcel(res, "Palletiztion");
  // }
  onChange() {
    console.log(this.selectedinvoicel.length)
    const choosen= this.selectedinvoicel[this.selectedinvoicel.length - 1];   
    this.selectedinvoicel.length = 0;
    this.selectedinvoicel.push(choosen);
  }  
  }
   
  
  
  






