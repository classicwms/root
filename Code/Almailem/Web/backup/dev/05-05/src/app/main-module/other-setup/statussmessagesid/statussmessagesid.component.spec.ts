import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatussmessagesidComponent } from './statussmessagesid.component';

describe('StatussmessagesidComponent', () => {
  let component: StatussmessagesidComponent;
  let fixture: ComponentFixture<StatussmessagesidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatussmessagesidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatussmessagesidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
