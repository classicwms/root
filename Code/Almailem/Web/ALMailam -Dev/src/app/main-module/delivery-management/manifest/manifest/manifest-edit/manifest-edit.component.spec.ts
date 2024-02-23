import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManifestEditComponent } from './manifest-edit.component';

describe('ManifestEditComponent', () => {
  let component: ManifestEditComponent;
  let fixture: ComponentFixture<ManifestEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManifestEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManifestEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
