import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpetualHeaderComponent } from './perpetual-header.component';

describe('PerpetualHeaderComponent', () => {
  let component: PerpetualHeaderComponent;
  let fixture: ComponentFixture<PerpetualHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpetualHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpetualHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
