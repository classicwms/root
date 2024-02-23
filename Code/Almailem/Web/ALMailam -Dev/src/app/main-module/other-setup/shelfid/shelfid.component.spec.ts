import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShelfidComponent } from './shelfid.component';

describe('ShelfidComponent', () => {
  let component: ShelfidComponent;
  let fixture: ComponentFixture<ShelfidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShelfidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShelfidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
