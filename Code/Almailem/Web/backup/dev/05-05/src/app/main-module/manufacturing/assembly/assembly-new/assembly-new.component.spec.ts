import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssemblyNewComponent } from './assembly-new.component';

describe('AssemblyNewComponent', () => {
  let component: AssemblyNewComponent;
  let fixture: ComponentFixture<AssemblyNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssemblyNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssemblyNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
