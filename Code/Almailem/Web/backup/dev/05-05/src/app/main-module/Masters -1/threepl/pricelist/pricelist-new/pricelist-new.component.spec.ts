import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PricelistNewComponent } from './pricelist-new.component';

describe('PricelistNewComponent', () => {
  let component: PricelistNewComponent;
  let fixture: ComponentFixture<PricelistNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PricelistNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PricelistNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
