import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-business-new',
  templateUrl: './business-new.component.html',
  styleUrls: ['./business-new.component.scss']
})
export class BusinessNewComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
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

}
