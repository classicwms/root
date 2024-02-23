import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PalletdetailsComponent } from './palletdetails.component';

describe('PalletdetailsComponent', () => {
  let component: PalletdetailsComponent;
  let fixture: ComponentFixture<PalletdetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PalletdetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PalletdetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
