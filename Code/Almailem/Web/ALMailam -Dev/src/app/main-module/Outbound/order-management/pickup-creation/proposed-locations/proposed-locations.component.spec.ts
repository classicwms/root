import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProposedLocationsComponent } from './proposed-locations.component';

describe('ProposedLocationsComponent', () => {
  let component: ProposedLocationsComponent;
  let fixture: ComponentFixture<ProposedLocationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProposedLocationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProposedLocationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
