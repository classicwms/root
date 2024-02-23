import { Component, OnInit } from '@angular/core';


export interface PeriodicElement {
  name: string;
  position: string;
  weight: string;
  symbol: string;
  Strategytype: string;
  SequenceNo: string;
  Symbol1: string;
  Symbol2: string;
  Symbol3: string;
  Symbol4: string;
  Symbol5: string;
  Symbol6: string;
  Symbol7: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  { position: '1', Strategytype: 'Putaway', SequenceNo: '1',  name: 'Fixed Bin', weight: 'Next Avl Bin', symbol: 'Interim', Symbol1:'', Symbol2: '',
  Symbol3: '', Symbol4:'', Symbol5:'', Symbol6:'', Symbol7:''},

  { position: '2', Strategytype: 'Putaway', SequenceNo: '2',  name: 'Capacity', weight: 'Next Avl Bin', symbol: 'Open', Symbol1:'Interim', Symbol2: '',
  Symbol3: '', Symbol4:'', Symbol5:'', Symbol6:'', Symbol7:''},
  
  { position: '3', Strategytype: 'Picking', SequenceNo: '1',  name: 'FIFO', weight: 'Wave', symbol: 'Manual', Symbol1:'', Symbol2: '',
  Symbol3: '', Symbol4:'', Symbol5:'', Symbol6:'', Symbol7:''},
  
  { position: '4', Strategytype: 'Picking', SequenceNo: '2',  name: 'Wave', weight: 'Zone', symbol: 'Manual', Symbol1:'', Symbol2: '',
  Symbol3: '', Symbol4:'', Symbol5:'', Symbol6:'', Symbol7:''},
];

@Component({
  selector: 'app-strategies-new',
  templateUrl: './strategies-new.component.html',
  styleUrls: ['./strategies-new.component.scss']
})
export class StrategiesNewComponent implements OnInit {

  constructor() { }

  displayedColumns: string[] = ['position', 'Strategytype','SequenceNo','name', 'weight', 'symbol','Symbol1', 'Symbol2', 'Symbol3', 'Symbol4', 'Symbol5', 'Symbol6',
'Symbol7'];
  dataSource = ELEMENT_DATA;

  ngOnInit(): void {
  }

}
