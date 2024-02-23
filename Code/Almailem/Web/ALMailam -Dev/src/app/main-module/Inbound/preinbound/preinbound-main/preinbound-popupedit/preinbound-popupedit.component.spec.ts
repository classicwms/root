import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundPopupeditComponent } from './preinbound-popupedit.component';

describe('PreinboundPopupeditComponent', () => {
  let component: PreinboundPopupeditComponent;
  let fixture: ComponentFixture<PreinboundPopupeditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundPopupeditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundPopupeditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
