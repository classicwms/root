import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-replace-asn',
  templateUrl: './replace-asn.component.html',
  styleUrls: ['./replace-asn.component.scss']
})
export class ReplaceASNComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ReplaceASNComponent>,
  ) { }
  asnno: any;
  ngOnInit(): void {
  }
  submit() {

    this.dialogRef.close(this.asnno);




  }
}
