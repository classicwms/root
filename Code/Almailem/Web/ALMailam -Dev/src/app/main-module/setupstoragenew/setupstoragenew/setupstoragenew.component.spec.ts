import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupstoragenewComponent } from './setupstoragenew.component';

describe('SetupstoragenewComponent', () => {
  let component: SetupstoragenewComponent;
  let fixture: ComponentFixture<SetupstoragenewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupstoragenewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupstoragenewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
