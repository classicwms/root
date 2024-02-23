import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransfertypeidComponent } from './transfertypeid.component';

describe('TransfertypeidComponent', () => {
  let component: TransfertypeidComponent;
  let fixture: ComponentFixture<TransfertypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransfertypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransfertypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
