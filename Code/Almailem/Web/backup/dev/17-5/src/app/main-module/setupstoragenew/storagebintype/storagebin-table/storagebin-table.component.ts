import { SelectionModel } from '@angular/cdk/collections';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
export interface storagebin {


  no: string;
  dimensions: string;
  length: string;
  width: string;
  height: string;
  uom: string;
}
const ELEMENT_DATA: storagebin[] = [
  { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },
  // { no: "1", dimensions: 'Value', length: 'Enter', width: 'Enter', height: 'Enter', uom: 'dropdown' },



];
@Component({
  selector: 'app-storagebin-table',
  templateUrl: './storagebin-table.component.html',
  styleUrls: ['./storagebin-table.component.scss']
})
export class StoragebinTableComponent implements OnInit {
  form!: FormGroup;
  storageData: any;
  constructor(private dialogRef: MatDialogRef<StoragebinTableComponent>, private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    this.storageData = data;
  }


  ngOnInit(): void {
    this.form = this.fb.group({
      dimentionUom: [],
      height: [],
      length: [],
      width: []
    });

    if (this.storageData != null && this.storageData != undefined) {
      this.form.patchValue({ height: this.storageData.height, width: this.storageData.width, length: this.storageData.length, dimentionUom: this.storageData.dimentionUom });
    }
  }

  displayedColumns: string[] = ['select', 'dimensions', 'length', 'width', 'height', 'uom'];
  dataSource = new MatTableDataSource<storagebin>(ELEMENT_DATA);
  selection = new SelectionModel<storagebin>(true, []);

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
  checkboxLabel(row?: storagebin): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  closeDialog() {
    this.dialogRef.close(this.form.value);
  }
}
