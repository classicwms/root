import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemtypeNewComponent } from './itemtype-new.component';

describe('ItemtypeNewComponent', () => {
  let component: ItemtypeNewComponent;
  let fixture: ComponentFixture<ItemtypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItemtypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemtypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
