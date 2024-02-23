import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemGroupAddLinesComponent } from './item-group-add-lines.component';

describe('ItemGroupAddLinesComponent', () => {
  let component: ItemGroupAddLinesComponent;
  let fixture: ComponentFixture<ItemGroupAddLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItemGroupAddLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemGroupAddLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
