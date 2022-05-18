import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { BatchPopupComponent } from './batch-popup/batch-popup.component';

@Component({
  selector: 'app-batch-serial',
  templateUrl: './batch-serial.component.html',
  styleUrls: ['./batch-serial.component.scss']
})
export class BatchSerialComponent implements OnInit {

  constructor(public dialog: MatDialog) { }
  Bintype(): void {

    const dialogRef = this.dialog.open(BatchPopupComponent, {
      disableClose: true,
      width: '77.5%',
      maxWidth: '80%',
      position: { top: '26.5%',right: '1.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  ngOnInit(): void {
  }
  title1 = "Masters";
  title2 = "Product";
}
