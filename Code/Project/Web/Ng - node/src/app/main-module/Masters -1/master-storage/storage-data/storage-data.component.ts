import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-storage-data',
  templateUrl: './storage-data.component.html',
  styleUrls: ['./storage-data.component.scss']
})
export class StorageDataComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
  title1 = "Masters-Storage";
  title2 = "Storage Data";
}