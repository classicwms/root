import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundMainComponent } from './preinbound-main.component';

describe('PreinboundMainComponent', () => {
  let component: PreinboundMainComponent;
  let fixture: ComponentFixture<PreinboundMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
