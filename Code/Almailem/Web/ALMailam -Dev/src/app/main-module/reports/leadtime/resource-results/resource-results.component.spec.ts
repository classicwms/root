import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceResultsComponent } from './resource-results.component';

describe('ResourceResultsComponent', () => {
  let component: ResourceResultsComponent;
  let fixture: ComponentFixture<ResourceResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResourceResultsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResourceResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
