import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemgroupNewComponent } from './itemgroup-new.component';

describe('ItemgroupNewComponent', () => {
  let component: ItemgroupNewComponent;
  let fixture: ComponentFixture<ItemgroupNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItemgroupNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemgroupNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
