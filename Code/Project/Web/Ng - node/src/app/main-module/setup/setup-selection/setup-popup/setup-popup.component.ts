import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';

@Component({
  selector: 'app-setup-popup',
  templateUrl: './setup-popup.component.html',
  styleUrls: ['./setup-popup.component.scss']
})
export class SetupPopupComponent implements OnInit {
  screenid: 1020 | undefined;
  

  // routeto() {
  //   localStorage.setItem('crrentmenu', '1002');
  //   this.router.navigate(["/main/masters/basic-data1"]);
  // }
  routeto(url: any, id: any) {
    localStorage.setItem('crrentmenu', id);
    this.router.navigate([url]);
  }

  disabled = false;
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

  panelOpenState = false;
  constructor(private router: Router, 
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }
  ngOnInit(): void {}
  
}




