import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModuleidNewComponent } from './moduleid-new.component';

describe('ModuleidNewComponent', () => {
  let component: ModuleidNewComponent;
  let fixture: ComponentFixture<ModuleidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModuleidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModuleidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
