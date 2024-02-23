import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessidComponent } from './processid.component';

describe('ProcessidComponent', () => {
  let component: ProcessidComponent;
  let fixture: ComponentFixture<ProcessidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
