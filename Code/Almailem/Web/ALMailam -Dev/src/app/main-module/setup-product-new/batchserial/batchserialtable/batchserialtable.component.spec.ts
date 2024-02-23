import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchserialtableComponent } from './batchserialtable.component';

describe('BatchserialtableComponent', () => {
  let component: BatchserialtableComponent;
  let fixture: ComponentFixture<BatchserialtableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchserialtableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchserialtableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
