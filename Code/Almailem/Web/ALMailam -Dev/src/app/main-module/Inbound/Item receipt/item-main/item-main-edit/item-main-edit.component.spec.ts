import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemMainEditComponent } from './item-main-edit.component';

describe('ItemMainEditComponent', () => {
  let component: ItemMainEditComponent;
  let fixture: ComponentFixture<ItemMainEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItemMainEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemMainEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
