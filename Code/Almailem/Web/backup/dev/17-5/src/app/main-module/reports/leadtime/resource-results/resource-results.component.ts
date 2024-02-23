
  import { SelectionModel } from '@angular/cdk/collections';
  import { Component, Inject, OnInit, ViewChild } from '@angular/core';
  import { FormBuilder } from '@angular/forms';
  import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
  import { MatPaginator } from '@angular/material/paginator';
  import { MatSort } from '@angular/material/sort';
  import { MatTableDataSource } from '@angular/material/table';
  import { Router } from '@angular/router';
  import { NgxSpinnerService } from 'ngx-spinner';
  import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
  import { AuthService } from 'src/app/core/core';

  export interface  variant {


    picker: string;
    picking:  string;
    availablity:  string;
    AvgPicking:  string;
  } 
  const ELEMENT_DATA:  variant[] = [
    { picker: "Ahmad",picking:  '0',availablity:  'Immediate',AvgPicking:  '11', },
    { picker: "Jaffer",picking:  '0',availablity:  'Immediate',AvgPicking:  '15', },
    { picker: "Sherrif",picking:  '2',availablity:  'After 6 minutes',AvgPicking:  '13', },
    { picker: "Ali",picking:  '2',availablity:  'After 8 minutes',AvgPicking:  '12', },
    { picker: "Mohammad",picking:  '4',availablity:  'After 10 minutes',AvgPicking:  '12', },
  
  ];

  
  @Component({
    selector: 'app-resource-results',
    templateUrl: './resource-results.component.html',
    styleUrls: ['./resource-results.component.scss']
  })
  export class ResourceResultsComponent implements OnInit {
  
    constructor(
      public dialogRef: MatDialogRef<any>,
      @Inject(MAT_DIALOG_DATA) public data: any,
      public toastr: ToastrService,
      private spin: NgxSpinnerService,
      private auth: AuthService,
      private fb: FormBuilder,
      private router: Router,
      public dialog: MatDialog,
      
    ) { }
    sub = new Subscription();
    ngOnInit(): void {

    }
  
    ELEMENT_DATA: any[] = [];
    displayedColumns: string[] = ['select', 'customerCode', 'customerName', 'mobileNumber', 'civilId',];
    dataSource = new MatTableDataSource < any > (ELEMENT_DATA);
    selection = new SelectionModel < any > (true, []);
  
  
    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
      const numSelected = this.selection.selected.length;
      const numRows = this.dataSource.data.length;
      return numSelected === numRows;
    }
  
    /** Selects all rows if they are not all selected; otherwise clear selection. */
    toggleAllRows() {
      if (this.isAllSelected()) {
        this.selection.clear();
        return;
      }
  
      this.selection.select(...this.dataSource.data);
    }
  
    /** The label for the checkbox on the passed row */
    checkboxLabel(row ? : any): string {
      if (!row) {
        return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
      }
      return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.invoiceNumber + 1}`;
    }
  
    clearselection(row: any) {
      this.selection.clear();
      this.selection.toggle(row);
    }
  
  
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
  
    submit(){
      this.toastr.success("User assigned successfully", "Notification", {
        timeOut: 2000,
        progressBar: false,
      });
  }
  
}
