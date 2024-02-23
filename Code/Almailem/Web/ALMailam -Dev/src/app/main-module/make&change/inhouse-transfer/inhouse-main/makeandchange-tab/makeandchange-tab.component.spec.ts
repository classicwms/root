import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeandchangeTabComponent } from './makeandchange-tab.component';

describe('MakeandchangeTabComponent', () => {
  let component: MakeandchangeTabComponent;
  let fixture: ComponentFixture<MakeandchangeTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MakeandchangeTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MakeandchangeTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
