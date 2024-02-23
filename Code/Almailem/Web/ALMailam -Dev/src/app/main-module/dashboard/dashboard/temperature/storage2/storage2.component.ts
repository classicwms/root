import { Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Zone1Component } from '../zone1/zone1.component';
import { Zone2Component } from '../zone2/zone2.component';

@Component({
  selector: 'app-storage2',
  templateUrl: './storage2.component.html',
  styleUrls: ['./storage2.component.scss']
})
export class Storage2Component implements OnInit {

  
  constructor(public dialog: MatDialog,) { }

  ngOnInit(): void {
  }
  temperature = false;

  triggerBlink(){
    this.temperature = !this.temperature
  }

  itemslist: any[]= []

  @ViewChild('scrollframe', { static: false }) scrollFrame: ElementRef;
  @ViewChildren('item') itemElements: QueryList<any>;

  private itemContainer: any;
  private scrollContainer: any;
  public items: any[] = [];
  private isNearBottom = true;



  ngAfterViewInit() {
    this.scrollContainer = this.scrollFrame.nativeElement;
    this.itemElements.changes.subscribe((_) => this.onItemElementsChanged());

    // Add a new item every 2 seconds for demo purposes
    setInterval(() => {
      this.items.push(this.itemslist);
    }, 2000);
  
  }

  
  private onItemElementsChanged(): void {
    if (this.isNearBottom) {
      this.scrollToBottom();
    }
  }

  private scrollToBottom(): void {
    this.scrollContainer.scroll({
      top: this.scrollContainer.scrollHeight,
      left: 0,
      behavior: 'smooth',
    });
  }

  private isUserNearBottom(): boolean {
    const threshold = 150;
    const position =
      this.scrollContainer.scrollTop + this.scrollContainer.offsetHeight;
    const height = this.scrollContainer.scrollHeight;
    return position > height - threshold;
  }

  scrolled(event: any): void {
    this.isNearBottom = this.isUserNearBottom();
  }

  zone1 = false;


showZone1(){
  const dialogRef = this.dialog.open(Zone1Component, {
    disableClose: true,
    width: '40%',
    position: { right: '1%', top: '8%'},
    maxWidth: '23%',
   // data: { No: binNo,}
  });

  dialogRef.afterClosed().subscribe(result => {
  });
}

showZone2(){
  const dialogRef = this.dialog.open(Zone2Component, {
    disableClose: true,
    width: '40%',
    position: { right: '1%', top: '8%'},
    maxWidth: '23%',
   // data: { No: binNo,}
  });

  dialogRef.afterClosed().subscribe(result => {
  });
}

}
