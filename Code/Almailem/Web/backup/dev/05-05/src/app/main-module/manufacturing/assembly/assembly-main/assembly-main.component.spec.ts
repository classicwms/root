import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssemblyMainComponent } from './assembly-main.component';

describe('AssemblyMainComponent', () => {
  let component: AssemblyMainComponent;
  let fixture: ComponentFixture<AssemblyMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssemblyMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssemblyMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
