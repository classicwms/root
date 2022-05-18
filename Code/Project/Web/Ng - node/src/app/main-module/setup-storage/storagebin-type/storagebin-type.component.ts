import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { StoragebinTableComponent } from "./storagebin-table/storagebin-table.component";


@Component({
  selector: 'app-storagebin-type',
  templateUrl: './storagebin-type.component.html',
  styleUrls: ['./storagebin-type.component.scss']
})
export class StoragebinTypeComponent implements OnInit {
  title1="Storage Setup";
  title2="Storag Bin Type";
  animal: string | undefined;
  name: string | undefined;
  constructor(public dialog: MatDialog) { }
  Bintype(): void {

    const dialogRef = this.dialog.open(StoragebinTableComponent, {
      disableClose: true,
      width: '77.5%',
      maxWidth: '80%',
      position: { top: '12.5%',right: '1.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

  ngOnInit(): void {
  }

}
