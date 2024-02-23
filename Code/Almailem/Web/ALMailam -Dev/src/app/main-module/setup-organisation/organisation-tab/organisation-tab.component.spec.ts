import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganisationTabComponent } from './organisation-tab.component';

describe('OrganisationTabComponent', () => {
  let component: OrganisationTabComponent;
  let fixture: ComponentFixture<OrganisationTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrganisationTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganisationTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
