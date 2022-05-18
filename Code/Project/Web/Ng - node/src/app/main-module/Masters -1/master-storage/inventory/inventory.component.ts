import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
  title1 = "Masters-Storage";
  title2 = "Inventory";
}
