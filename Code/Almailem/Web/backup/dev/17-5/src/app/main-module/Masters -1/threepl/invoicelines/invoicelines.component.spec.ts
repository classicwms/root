import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoicelinesComponent } from './invoicelines.component';

describe('InvoicelinesComponent', () => {
  let component: InvoicelinesComponent;
  let fixture: ComponentFixture<InvoicelinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InvoicelinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvoicelinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
