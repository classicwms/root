import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundComponent } from './preinbound.component';

describe('PreinboundComponent', () => {
  let component: PreinboundComponent;
  let fixture: ComponentFixture<PreinboundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
