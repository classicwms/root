import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContainerreceiptCreateComponent } from './containerreceipt-create.component';

describe('ContainerreceiptCreateComponent', () => {
  let component: ContainerreceiptCreateComponent;
  let fixture: ComponentFixture<ContainerreceiptCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContainerreceiptCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContainerreceiptCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
