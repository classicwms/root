import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundaddlinesComponent } from './preinboundaddlines.component';

describe('PreinboundaddlinesComponent', () => {
  let component: PreinboundaddlinesComponent;
  let fixture: ComponentFixture<PreinboundaddlinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundaddlinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundaddlinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
