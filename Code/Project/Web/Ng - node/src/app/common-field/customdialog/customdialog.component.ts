import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-customdialog',
  templateUrl: './customdialog.component.html',
  styleUrls: ['./customdialog.component.scss']
})
export class CustomdialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<CustomdialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }
  onNoClick() { this.dialogRef.close(); }
}
