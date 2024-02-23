import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShelfidNewComponent } from './shelfid-new.component';

describe('ShelfidNewComponent', () => {
  let component: ShelfidNewComponent;
  let fixture: ComponentFixture<ShelfidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShelfidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShelfidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
