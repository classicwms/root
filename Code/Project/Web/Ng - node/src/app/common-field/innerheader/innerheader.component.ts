import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA, MatDialogConfig } from '@angular/material/dialog';
import { ActivationEnd, Router } from '@angular/router';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';

@Component({
  selector: 'app-innerheader',
  templateUrl: './innerheader.component.html',
  styleUrls: ['./innerheader.component.scss']
})
export class InnerheaderComponent implements OnInit {
  selected = "one";
  @Input() title1 = "";
  @Input() title2 = "";
  currenturl = this.router.url;
  constructor(public dialog: MatDialog, public router: Router) {

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
  openDialog(): void {
    const dialogRef = this.dialog.open(DialogExampleComponent, {
      width: '18rem',
      position: { right: '0%', top: '5%' },
      data: { name: "ipsum", animal: "lorem" }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      // this.animal = result;
    });
  }



  ngOnInit(): void {

  }
}

