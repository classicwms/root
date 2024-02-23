import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasternewComponent } from './masternew.component';

describe('MasternewComponent', () => {
  let component: MasternewComponent;
  let fixture: ComponentFixture<MasternewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MasternewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MasternewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
