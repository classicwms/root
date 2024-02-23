import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-assign-invoice',
  templateUrl: './assign-invoice.component.html',
  styleUrls: ['./assign-invoice.component.scss']
})
export class AssignInvoiceComponent implements OnInit {
  invoiceno: any;
  constructor(public dialogRef: MatDialogRef<AssignInvoiceComponent>,) { }

  ngOnInit(): void {
  }
  close() {
    this.dialogRef.close(this.invoiceno);
  }
}
