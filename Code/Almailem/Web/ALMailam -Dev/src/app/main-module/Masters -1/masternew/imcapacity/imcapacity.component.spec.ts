import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImcapacityComponent } from './imcapacity.component';

describe('ImcapacityComponent', () => {
  let component: ImcapacityComponent;
  let fixture: ComponentFixture<ImcapacityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImcapacityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImcapacityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
