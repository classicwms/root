import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTable } from '@angular/material/table';
export interface PeriodicElement {

  position: number;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1,},
  {position: 2},
  {position: 3},
  {position: 4},
  {position: 5},
];
@Component({
  selector: 'app-bom-new',
  templateUrl: './bom-new.component.html',
  styleUrls: ['./bom-new.component.scss']
})
export class BomNewComponent implements OnInit {
  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];
  dataSource = [...ELEMENT_DATA];

  @ViewChild(MatTable) table!: MatTable<PeriodicElement>;

  addData() {
    const randomElementIndex = Math.floor(Math.random() * ELEMENT_DATA.length);
    this.dataSource.push(ELEMENT_DATA[randomElementIndex]);
    this.table.renderRows();
  }

  
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
