import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterStoragetabComponent } from './master-storagetab.component';

describe('MasterStoragetabComponent', () => {
  let component: MasterStoragetabComponent;
  let fixture: ComponentFixture<MasterStoragetabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MasterStoragetabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MasterStoragetabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
