



  import { Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';

  @Component({
    selector: 'app-zone1',
    templateUrl: './zone1.component.html',
    styleUrls: ['./zone1.component.scss']
  })
  export class Zone1Component implements OnInit {
  

  constructor() {
    this.itemslist = [{
      label: 'Consumables'
    },
    {
      label: 'Consumables'
    },
    {
      label: 'Consumables'
    },
    {
      label: 'Consumables'
    },
    {
      label: 'Consumables'
    },
    {
      label: 'Consumables'
    },
  ];
   }

  ngOnInit(): void {
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
    }, 1000);
    // this.itemslist.forEach((x: { label: string;}) => {
    //   for(let i=0; i<this.itemslist.length; i++){
    // setInterval(() => {
    //     this.items.push({value: x.label}); 
    // }, 200);
    //   }
    // })
  
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
}

